package dto;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRateDto {

    private final CurrencyDto baseCurrencyId;
    private final CurrencyDto targetCurrencyId;
    private final BigDecimal rate;
    private BigDecimal amount ;
    private BigDecimal convertedAmount;

    public ExchangeRateDto(CurrencyDto baseCurrencyId, CurrencyDto targetCurrencyId, BigDecimal rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyDto getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public CurrencyDto getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateDto that = (ExchangeRateDto) o;
        return Objects.equals(baseCurrencyId, that.baseCurrencyId) && Objects.equals(targetCurrencyId, that.targetCurrencyId) && Objects.equals(rate, that.rate) && Objects.equals(amount, that.amount) && Objects.equals(convertedAmount, that.convertedAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrencyId, targetCurrencyId, rate, amount, convertedAmount);
    }

    @Override
    public String toString() {
        return "ExchangeRateDto{" +
                "baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", rate=" + rate +
                ", amount=" + amount +
                ", convertedAmount=" + convertedAmount +
                '}';
    }
}
