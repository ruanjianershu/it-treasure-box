package com.it.treasurebox.util.encryption;

import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * 用于封装RSA解密后的数据，其主要目的是为了减少byte数组的复制，提升系统性能
 *
 */
@Data
public class RsaDecryptResult {
    /**
     * 解密后数据
     */
    private final byte[] data;
    /**
     * 解密后数据的实际长度，注意：此值小于或等于data.length
     */
    private final int length;

    private String string;

    public RsaDecryptResult(byte[] data, int length) {
        this.data = data;
        this.length = length;
    }

    @Override
    public String toString() {
        if (null == string) {
            string = new String(data, 0, length, StandardCharsets.UTF_8);
        }
        return string;
    }

}
