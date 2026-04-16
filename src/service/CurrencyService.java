package service;

import dao.CurrencyDao;
import dto.CurrencyDto;
import entity.Currency;
import util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService(){}

    public List<CurrencyDto> findAll(){
        List<Currency> currenciesDao = currencyDao.findAll();
        List<CurrencyDto> currenciesDto = new ArrayList<>();

        for (Currency currency : currenciesDao){
            CurrencyDto dto = new CurrencyDto(
                    currency.getCode(),
                    currency.getName(),
                    currency.getSign()
            );
            currenciesDto.add(dto);
        }
        return currenciesDto;
    }

    public Optional<CurrencyDto> findByCode(String code){

        Validator.checkCode(code);

        Optional<Currency> currencyDao = this.currencyDao.findByCode(code);

        if(currencyDao.isEmpty()){
            return Optional.empty();
        }

        Currency currency = currencyDao.get();

        CurrencyDto currencyDto = new CurrencyDto(
                currency.getCode(),
                currency.getName(),
                currency.getSign()
        );

        return Optional.of(currencyDto);
    }

    public CurrencyDto save(Currency currency){

        Validator.checkCode(currency.getCode());
        Validator.checkName(currency.getName());
        Validator.checkSign(currency.getSign());

        Currency newCurrency = currencyDao.save(currency);
        return new CurrencyDto(
                newCurrency.getCode(),
                newCurrency.getName(),
                newCurrency.getSign()
        );
    }

    public static CurrencyService getInstance(){
        return INSTANCE;
    }

}
