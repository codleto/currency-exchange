package util;

import java.math.BigDecimal;

public class Validator {

    public static void checkCode(String code){
        int maxLength = 3;

        if(code == null || code.trim().isEmpty()){
            throw new RuntimeException("Код не должен быть пустым");
        }

        if(!code.matches("[A-Z]+")){
            throw new RuntimeException("Код должно содержать только заглавные буквы [A-Z]");
        }

        if(code.length() > maxLength){
            throw new RuntimeException("Код не должен быть длиннее чем" + maxLength + " символов");
        }
    }

    public static void checkName(String name){
        int maxLength = 50;

        if(name == null || name.trim().isEmpty()){
            throw new RuntimeException("Имя не должно быть пустым");
        }

        if(!name.matches("[a-zA-Zа-яА-Я]+")){
            throw new RuntimeException("Имя должно содержать только буквы");
        }

        if(name.length() > maxLength){
            throw new RuntimeException("Имя не должно быть длиннее чем" + maxLength + " символов");
        }
    }

    public static void checkSign(String sign){
        int maxLength = 5;

        if(sign == null || sign.trim().isEmpty()){
            throw new RuntimeException("Символ не должно быть пустым");
        }

        if(!sign.matches("[^a-zA-Z0-9]+")){
            throw new RuntimeException("Символ не должен содержать буквы и цифры");
        }

        if(sign.length() > maxLength){
            throw new RuntimeException("Символ не должно быть длиннее чем" + maxLength + " символов");
        }
    }

    public static void checkAmount(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Amount должен быть больше 0");
        }

        if (amount.scale() > 2) {
                throw new RuntimeException("Максимум 2 знака после запятой");
        }
    }

    public static void checkRate(BigDecimal rate){
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Rate должен быть больше 0");
        }

        if (rate.scale() > 2) {
            throw new RuntimeException("Максимум 2 знака после запятой");
        }
    }
}

