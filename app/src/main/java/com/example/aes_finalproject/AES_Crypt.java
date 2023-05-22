package com.example.aes_finalproject;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES_Crypt extends MainActivity{

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; //Algorithm used
    private static final byte[] KEY = "pugibaqu_incorp.".getBytes(); //your own secret key
    private static final byte[] IV = "1234567890123456".getBytes(); //your own initialization vector

    // encrypt file using algorithm
    static String encrypt(String text) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }
    // decrypt file using algorithm
    static String decrypt(String text) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decrypted = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
