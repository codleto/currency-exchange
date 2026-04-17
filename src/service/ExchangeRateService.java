package service;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import entity.Currency;
import entity.ExchangeRate;
import exception.BadRequestException;
import exception.NotFoundException;
import util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateService(){}

    public List<ExchangeRateDto> findAll(){
        List<ExchangeRate> exchangeRatesDao = exchangeRateDao.findAll();
        List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();

        for (ExchangeRate exchangeRate : exchangeRatesDao){
            Currency baseCurrency = exchangeRate.getBaseCurrencyId();
            Currency targetCurrency = exchangeRate.getTargetCurrencyId();

            ExchangeRateDto exchangeRateDto = buildExchangeRateDto(exchangeRate, baseCurrency, targetCurrency);
            exchangeRateDtos.add(exchangeRateDto);
        }
        return exchangeRateDtos;
    }

    public ExchangeRateDto findByCode(String baseCode, String targetCode){

        Validator.checkCode(baseCode);
        Validator.checkCode(targetCode);

        Optional<ExchangeRate> exchangeRateDao = this.exchangeRateDao.findByCode(baseCode, targetCode);

        if(exchangeRateDao.isEmpty()){
            throw new NotFoundException("Валютная пара не найдена");
        }

        ExchangeRate exchangeRate = exchangeRateDao.get();

        Currency baseCurrency = exchangeRate.getBaseCurrencyId();
        Currency targetCurrency = exchangeRate.getTargetCurrencyId();

        return buildExchangeRateDto(exchangeRate, baseCurrency, targetCurrency);
    }

    public void update(String baseCode, String targetCode, String newRate){

        BigDecimal rate;

        try {
            rate = new BigDecimal(newRate);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Некорректное значение rate");
        }

        Validator.checkCode(baseCode);
        Validator.checkCode(targetCode);
        Validator.checkRate(rate);

        Optional<ExchangeRate> exchangeRateDaoByCode = this.exchangeRateDao.findByCode(baseCode, targetCode);

        if(exchangeRateDaoByCode.isEmpty()){
            throw new NotFoundException("Валютная пара не найдена");
        }

        ExchangeRate exchangeRate = exchangeRateDaoByCode.get();
        exchangeRate.setRate(rate);

        this.exchangeRateDao.updateRate(exchangeRate);
    }

    public ExchangeRateDto save(String baseCode, String targetCode, String rateString){

        BigDecimal rate;

        try {
            rate = new BigDecimal(rateString);

        } catch (NumberFormatException e) {
            throw new BadRequestException("Некорректное значение rate");
        }

        Validator.checkCode(baseCode);
        Validator.checkCode(targetCode);
        Validator.checkRate(rate);

        Optional<Currency> baseCurrencyDao = currencyDao.findByCode(baseCode);
        Optional<Currency> targetCurrencyDao = currencyDao.findByCode(targetCode);

        if(baseCurrencyDao.isEmpty() || targetCurrencyDao.isEmpty()){
            throw new NotFoundException("Одна или обе валюты не найдены");
        }

        Currency baseCurrency = baseCurrencyDao.get();
        Currency targetCurrency = targetCurrencyDao.get();

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

        ExchangeRate newExchangeRate = exchangeRateDao.save(exchangeRate);

        Currency newBaseCurrency = newExchangeRate.getBaseCurrencyId();
        Currency newTargetCurrency = newExchangeRate.getTargetCurrencyId();

        return buildExchangeRateDto(newExchangeRate, newBaseCurrency, newTargetCurrency);

    }

    public ExchangeRateDto exchange(String baseCode, String targetCode, String amountString){

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountString);

        } catch (NumberFormatException e) {
            throw new BadRequestException("Некорректное значение rate");
        }

        Validator.checkCode(baseCode);
        Validator.checkCode(targetCode);
        Validator.checkAmount(amount);

        Currency baseCurrency = getCurrencyOrThrow(baseCode);
        Currency targetCurrency = getCurrencyOrThrow(targetCode);

        Optional<ExchangeRate> foundCurrencies = searchCurrency(baseCode, targetCode);
        if (foundCurrencies.isEmpty()){
            throw new NotFoundException("Обменный курс для пары" + baseCode + "/" + targetCode + " не найден");
        }

        ExchangeRate exchangeRate = foundCurrencies.get();

        ExchangeRateDto exchangeRateDto = buildExchangeRateDto(exchangeRate, baseCurrency, targetCurrency);
        exchangeRateDto.setAmount(amount);
        exchangeRateDto.setConvertedAmount(amount.multiply(exchangeRateDto.getRate()));

        return exchangeRateDto;
    }

    private Optional<ExchangeRate> searchCurrency(String base, String target){
        Optional<ExchangeRate> directRate = findAB(base, target);
        if(directRate.isPresent()){
            return directRate;
        }

        Optional<ExchangeRate> reverseRate = findReversRate(base, target);
        if(reverseRate.isPresent()){
            return reverseRate;
        }

        return findCrossRate(base, target);
    }

    private Optional<ExchangeRate> findAB(String base, String target){
        return exchangeRateDao.findByCode(base, target);
    }

    private Optional<ExchangeRate> findReversRate(String base, String target){
        Optional<ExchangeRate> reversRate = exchangeRateDao.findByCode(target, base);
        if (reversRate.isPresent()) {
            ExchangeRate rate = reversRate.get();
            BigDecimal reversedRate = BigDecimal.ONE.divide(rate.getRate(), 2, RoundingMode.HALF_UP);

            ExchangeRate reversExchangeRate = new ExchangeRate(
                    rate.getTargetCurrencyId(),
                    rate.getBaseCurrencyId(),
                    reversedRate
            );
            return Optional.of(reversExchangeRate);
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> findCrossRate(String base, String target) {

        List<ExchangeRate> allRates = exchangeRateDao.findAll();

        List<ExchangeRate> baseList = findRatesWithTarget(allRates, base);
        List<ExchangeRate> targetList = findRatesWithTarget(allRates, target);

        for (ExchangeRate baseRate : baseList) {
            for (ExchangeRate targetRate : targetList) {
                if (baseRate.getBaseCurrencyId().getCode().
                        equals(targetRate.getBaseCurrencyId().getCode())) {

                    BigDecimal reversedBaseRate = BigDecimal.ONE.divide(baseRate.getRate(), 2, RoundingMode.HALF_UP);
                    BigDecimal crossRate = reversedBaseRate.multiply(targetRate.getRate());

                    ExchangeRate result = new ExchangeRate(
                            baseRate.getTargetCurrencyId(),
                            targetRate.getBaseCurrencyId(),
                            crossRate
                    );
                    return Optional.of(result);
                }
            }
        }
        return Optional.empty();
    }

    private List<ExchangeRate> findRatesWithTarget(List<ExchangeRate> rates, String targetCode){
        List<ExchangeRate> result = new ArrayList<>();
        for (ExchangeRate rate : rates) {
            if (rate.getTargetCurrencyId().getCode().equals(targetCode)) {
                result.add(rate);
            }
        }
        return result;
    }

    private Currency getCurrencyOrThrow(String code) {
        Optional<Currency> currencyOptional = currencyDao.findByCode(code);

        if (currencyOptional.isPresent()) {
            return currencyOptional.get();
        } else {
            throw new NotFoundException("Валюта с кодом " + code + " не найдена");
        }
    }

    private static ExchangeRateDto buildExchangeRateDto(ExchangeRate exchangeRate, Currency baseCurrency,
                                                        Currency targetCurrency) {
        CurrencyDto baseCurrencyDto = new CurrencyDto(
                baseCurrency.getCode(),
                baseCurrency.getName(),
                baseCurrency.getSign()
        );

        CurrencyDto targetCurrencyDto = new CurrencyDto(
                targetCurrency.getCode(),
                targetCurrency.getName(),
                targetCurrency.getSign()
        );

        return new ExchangeRateDto(
                baseCurrencyDto,
                targetCurrencyDto,
                exchangeRate.getRate()
        );
    }
    public static ExchangeRateService getInstance(){
        return INSTANCE;
    }
}
