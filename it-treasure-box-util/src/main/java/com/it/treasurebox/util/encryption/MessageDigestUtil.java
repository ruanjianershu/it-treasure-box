package com.it.treasurebox.util.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtil {

    public static String getMessageDigest(byte[] buffer, String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance(key);
            digest.reset();
            digest.update(buffer);
            return bytes2Hex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = Integer.toHexString(bts[i] & 0xFF);
            if (tmp.length() == 1) {
                des = des + "0";
            }
            des = des + tmp;
        }
        return des;
    }

    public static String getMD5(byte[] buffer) {
        return getMessageDigest(buffer, "MD5");
    }

    public static String getMD5(String str) {
        return getMD5(str.getBytes(StandardCharsets.UTF_8));

    }

    public static String getSHA1(byte[] buffer) {
        return getMessageDigest(buffer, "SHA-1");
    }

    public static String getSHA1(String str) {
        return getSHA1(str.getBytes(StandardCharsets.UTF_8));

    }

    public static String getSHA256(byte[] buffer) {
        return getMessageDigest(buffer, "SHA-256");
    }

    public static String getSHA256(String str) {
        return getSHA256(str.getBytes(StandardCharsets.UTF_8));
    }
}