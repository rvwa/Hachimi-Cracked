package me.trdyun.patcher.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class HachimiHelper {
    public static String clientDecrypt(String passText) throws GeneralSecurityException {
        byte[] key = Base64.getDecoder().decode("SBxPPKjQSmmAHQo7ffjIVA==");
        byte[] iv = Base64.getDecoder().decode("AAAAAAAAAAAAAAAAAAAAAA==");

        SecretKeySpec spec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, spec, ivSpec);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(passText));
        return  new String(bytes);
    }

    public static String responseGenerate(String request) {
        try {
            String dec = clientDecrypt(request);

            String responseBody = CipherUtil.encrypt(dec);

            byte[] clientKey = Base64.getDecoder().decode("SBxPPKjQSmmAHQo7ffjIVA==");
            byte[] aesKey = Base64.getDecoder().decode("/q4KByUBTlYmLR8EkcvmsZ/oONa6bBkF6QsianakcXo=");
            byte[] gcmKey = Base64.getDecoder().decode("RlVDSyBZT1UgRFlZ");
            byte[] ivKey = Base64.getDecoder().decode("AAAAAAAAAAAAAAAAAAAAAA==");

            SecretKeySpec clientSpec = new SecretKeySpec(clientKey, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, gcmKey);
            IvParameterSpec ivSpec = new IvParameterSpec(ivKey);
            SecretKeySpec aesSpec = new SecretKeySpec(aesKey, "AES");

            Cipher gcmCipher = Cipher.getInstance("AES/GCM/NoPadding");
            Cipher cbcCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            gcmCipher.init(Cipher.ENCRYPT_MODE, aesSpec, gcmSpec);
            cbcCipher.init(Cipher.ENCRYPT_MODE, clientSpec, ivSpec);

            byte[] firstEnc = cbcCipher.doFinal(responseBody.getBytes());
            byte[] secondEnc = gcmCipher.doFinal(Base64.getEncoder().encode(firstEnc));

            byte[] header = Base64.getDecoder().decode("Y3JhY2sgYnkgdHJkeXVufkZVQ0sgWU9VIERZWQ==");
            byte[] finalResponse = new byte[header.length + secondEnc.length];
            System.arraycopy(header, 0, finalResponse, 0, header.length);
            System.arraycopy(secondEnc, 0, finalResponse, header.length, secondEnc.length);
            return Base64.getEncoder().encodeToString(finalResponse);
        } catch (GeneralSecurityException | IllegalArgumentException gse) {
            return "FAILED";
        }
    }
}