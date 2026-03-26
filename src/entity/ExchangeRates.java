package entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRates {

    private int id;
    private Currencies baseCurrencyId;
    private Currencies targetCurrencyId;
    private BigDecimal rate;

    public ExchangeRates(int id, Currencies baseCurrencyId, Currencies targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public ExchangeRates() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currencies getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(Currencies baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public Currencies getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(Currencies targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRates that = (ExchangeRates) o;
        return id == that.id && Objects.equals(baseCurrencyId, that.baseCurrencyId) && Objects.equals(targetCurrencyId, that.targetCurrencyId) && Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrencyId, targetCurrencyId, rate);
    }

    @Override
    public String toString() {
        return "Exchange_rates{" +
                "id=" + id +
                ", baseCurrencyId=" + baseCurrencyId +
                ", targetCurrencyId=" + targetCurrencyId +
                ", rate=" + rate +
                '}';
    }
}
