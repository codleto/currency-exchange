package util;

import exception.BadRequestException;

import java.math.BigDecimal;

public class Validator {

    public static void checkCode(String code){
        int maxLength = 3;

        if(code == null || code.trim().isEmpty()){
            throw new BadRequestException("Код не должен быть пустым");
        }

        if(!code.matches("[A-Z]+")){
            throw new BadRequestException("Код должно содержать только заглавные буквы [A-Z]");
        }

        if(code.length() > maxLength){
            throw new BadRequestException("Код не должен быть длиннее чем" + maxLength + " символов");
        }
    }

    public static void checkName(String name){
        int maxLength = 50;

        if(name == null || name.trim().isEmpty()){
            throw new BadRequestException("Имя не должно быть пустым");
        }

        if(!name.matches("[a-zA-Zа-яА-Я]+")){
            throw new BadRequestException("Имя должно содержать только буквы");
        }

        if(name.length() > maxLength){
            throw new BadRequestException("Имя не должно быть длиннее чем" + maxLength + " символов");
        }
    }

    public static void checkSign(String sign){
        int maxLength = 5;

        if(sign == null || sign.trim().isEmpty()){
            throw new BadRequestException("Символ не должно быть пустым");
        }

        if(!sign.matches("[^a-zA-Z0-9]+")){
            throw new BadRequestException("Символ не должен содержать буквы и цифры");
        }

        if(sign.length() > maxLength){
            throw new BadRequestException("Символ не должно быть длиннее чем" + maxLength + " символов");
        }
    }

    public static void checkAmount(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount должен быть больше 0");
        }

        if (amount.scale() > 2) {
                throw new BadRequestException("Максимум 2 знака после запятой");
        }
    }

    public static void checkRate(BigDecimal rate){
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Rate должен быть больше 0");
        }

        if (rate.scale() > 2) {
            throw new BadRequestException("Максимум 2 знака после запятой");
        }
    }
}

