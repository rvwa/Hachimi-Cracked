package me.trdyun.patcher.utils;

import java.util.HashMap;
import java.util.Map;

public class CipherUtil {
    private static final Map<Character, Character> encryptMap = new HashMap<>();
    private static final Map<Character, Character> decryptMap = new HashMap<>();

    static {
        encryptMap.put('0' , '饿');
        encryptMap.put('1' , '酮');
        encryptMap.put('2' , '錒');
        encryptMap.put('3' , '爆');
        encryptMap.put('4' , '崩');
        encryptMap.put('5' , '蚌');
        encryptMap.put('6' , '乐');
        encryptMap.put('7' , '糙');
        encryptMap.put('8' , '瓦');
        encryptMap.put('9' , '弟');
        encryptMap.put('a' , '你');
        encryptMap.put('b' , '死');
        encryptMap.put('c' , '了');
        encryptMap.put('d' , '妈');
        encryptMap.put('e' , '操');
        encryptMap.put('f' , '草');
        encryptMap.put('-' , '拆');
        encryptMap.put(':' , '恶');

        for (Map.Entry<Character, Character> entry : encryptMap.entrySet()) {
            decryptMap.put(entry.getValue(), entry.getKey());
        }

    }

    public static String encrypt(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            Character encryptedChar = encryptMap.get(c);
            if (encryptedChar == null) {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
            result.append(encryptedChar);
        }
        result.append("恶屄运学行技瞎");  // postfix
        return result.toString();
    }

    public static String decrypt(String input) {
        if (input.endsWith("恶屄运学行技瞎")) {
            input = input.substring(0, input.length() - "恶屄运学行技瞎".length());
        } else {
            throw new IllegalArgumentException("Input does not have the correct suffix.");
        }

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            Character decryptedChar = decryptMap.get(c);
            if (decryptedChar == null) {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
            result.append(decryptedChar);
        }
        return result.toString();
    }
}
