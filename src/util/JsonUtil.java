package util;

import dto.CurrencyDto;
import dto.ExchangeRateDto;

import java.io.PrintWriter;

public class JsonUtil {

    public static void  writeExchangeRateResponse(PrintWriter writer, ExchangeRateDto dto) {
        writer.println("{");
        writer.println("\"base_currency\": {");
        writer.println("  \"code\": \"" + dto.getBaseCurrencyId().getCode() + "\",");
        writer.println("  \"name\": \"" + dto.getBaseCurrencyId().getName() + "\",");
        writer.println("  \"sign\": \"" + dto.getBaseCurrencyId().getSign() + "\"");
        writer.println("},");

        writer.println("\"target_currency\": {");
        writer.println("  \"code\": \"" + dto.getTargetCurrencyId().getCode() + "\",");
        writer.println("  \"name\": \"" + dto.getTargetCurrencyId().getName() + "\",");
        writer.println("  \"sign\": \"" + dto.getTargetCurrencyId().getSign() + "\"");
        writer.println("},");

        writer.println("  \"rate\": " + dto.getRate());

        if (dto.getAmount() != null) {
            writer.write(",\"amount\": " + dto.getAmount());
        }

        if (dto.getConvertedAmount() != null) {
            writer.write(",\"convertedAmount\": " + dto.getConvertedAmount());
        }

        writer.println("}");
    }

    public static void extracted(PrintWriter writer, CurrencyDto currencyDto) {
        writer.println("{");
        writer.println("\"code\": \"" + currencyDto.getCode() + "\",");
        writer.println("\"name\": \"" + currencyDto.getName() + "\",");
        writer.println("\"sign\": \"" + currencyDto.getSign() + "\"");
        writer.write("}");
    }
}
