package com.it.treasurebox.util.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * DES加密的，文件中共有两个方法,加密、解密
 *
 */
public class DES {

    /**
     * 已知密钥的情况下加密
     *
     * @param str 需要加密的明文
     * @param key 8个字符的密钥
     * @return 加密后内容
     * @throws Exception 异常
     */
    public static String encode(String str, String key) throws Exception {
        byte[] rawKey = key.getBytes(StandardCharsets.UTF_8);
        IvParameterSpec sr = new IvParameterSpec(rawKey);
        DESKeySpec dks = new DESKeySpec(rawKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedData = cipher.doFinal(data);
        return new String(Base64.getEncoder().encode(encryptedData), StandardCharsets.UTF_8);
    }

    /**
     * 已知密钥的情况下解密
     *
     * @param str 密文
     * @param key 8个字符的密钥
     * @return 解密后的明文
     * @throws Exception 异常
     */
    public static String decode(String str, String key) throws Exception {
        byte[] rawKey = key.getBytes(StandardCharsets.UTF_8);
        IvParameterSpec sr = new IvParameterSpec(rawKey);
        DESKeySpec dks = new DESKeySpec(rawKey);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        byte[] encryptedData = Base64.getDecoder().decode(str);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, StandardCharsets.UTF_8).trim();
    }
}
