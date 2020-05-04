package com.example.simplechat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String pass = "randomwordhere"; //CHANGE PASSWORD HERE INSTEAD OF skillbox

    private static SecretKeySpec keySpec;

    static {
        MessageDigest shaDigest = null;
        try {
            shaDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (shaDigest != null) {
            byte[] bytes = pass.getBytes();
            shaDigest.update(bytes, 0, bytes.length);
            byte[] key = shaDigest.digest();
            keySpec = new SecretKeySpec(key, "AES");
        }
    }

    public static String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(text.getBytes());

        return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT);
    }

    public static String decrypt(String text)  throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        return new String(
                cipher.doFinal(android.util.Base64.decode(text, android.util.Base64.DEFAULT)),
                "UTF-8"
        );
    }
}
