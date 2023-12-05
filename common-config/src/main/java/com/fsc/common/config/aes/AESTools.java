package com.fsc.common.config.aes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESTools {
    private static final Logger logger = LoggerFactory.getLogger(AESTools.class);
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_LNE = 128;

    public AESTools() {
    }

    public static Key codeToKey(String key) throws Exception {
        byte[] keyBytes = Base64Tools.decodeAsByteArray(key);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        return secretKey;
    }

    public static String decrypt(byte[] data, byte[] key) throws Exception {
        Key k = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, k);
        return new String(cipher.doFinal(data), "UTF-8");
    }

    public static String decrypt(String data, String key) throws Exception {
        return decrypt(Base64Tools.decodeAsByteArray(data), Base64Tools.decodeAsByteArray(key));
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, k);
        return cipher.doFinal(data);
    }

    public static String encrypt(String data, String key) throws Exception {
        byte[] dataBytes = data.getBytes("UTF-8");
        byte[] keyBytes = Base64Tools.decodeAsByteArray(key);
        return Base64Tools.encodeURLSafe(encrypt(dataBytes, keyBytes));
    }
}
