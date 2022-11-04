package com.it.treasurebox.util.encryption;


import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA工具类<br>
 * 最佳实践：为了达到更好和性能，在应用启动时将字符格式的key通过 getPublicKeyFromX509 和 getPrivateKeyFromPKCS8 转成Key实例，并缓存起来。<br>
 *
 */
@Slf4j
public class RSAUtils {

    /**
     *
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    private static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;

    /**
     * 密钥长度
     */
    private static final int KEY_SIZE = 1024;

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * @return
     */
    public static KeyPair initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER_NAME);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    public static void printKeys() throws Exception {
        KeyPair keyPair = initKey();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 得到公钥字符串
        String publicKeyString = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.getEncoder().encode(privateKey.getEncoded()));

        log.info("public key:{}", publicKeyString);
        log.info("private key:{}", privateKeyString);
    }

    /**
     * 获取公钥
     *
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromX509(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, PROVIDER_NAME);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * 获取私钥
     *
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, PROVIDER_NAME);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
    }


    public static byte[] encrypt(Key key, String content) {
        if (null == content) {
            return null;
        }
        return encrypt(key, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 加密
     *
     * @param key
     * @param content
     * @return
     */
    public static byte[] encrypt(Key key, byte[] content) {
        if (null == key) {
            throw new IllegalArgumentException("key is null");
        }
        if (null == content || content.length == 0) {
            throw new IllegalArgumentException("content is empty");
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            RSAKey rsaKey = (RSAKey) key;
            // 为了解决"java.lang.RuntimeException: encrypt error:Data must not be longer than 117 bytes"错误，将数据进行分段加密
            // 密钥长度(bytes)
            int encryptedBytesChunkLength = rsaKey.getModulus().bitLength() / 8;
            // 明文长度(bytes) <= 密钥长度(bytes)-11
            int decryptedBytesChunkLength = encryptedBytesChunkLength - 11;

            // 片数=(明文长度(bytes)/(密钥长度(bytes)-11))的整数部分+1
            int numberenOfDecryptedChunks = (content.length - 1) / decryptedBytesChunkLength + 1;
            int encryptedBytesLength = numberenOfDecryptedChunks * encryptedBytesChunkLength;
            // Create the encoded byte array
            byte[] encryptedBytes = new byte[encryptedBytesLength];

            // Counters
            int decryptedByteIndex = 0;
            int encryptedByteIndex = 0;
            for (int i = 0; i < numberenOfDecryptedChunks; i++) {
                if (i < numberenOfDecryptedChunks - 1) {
                    int tmp = cipher.doFinal(content, decryptedByteIndex, decryptedBytesChunkLength, encryptedBytes, encryptedByteIndex);
                    encryptedByteIndex = encryptedByteIndex + tmp;
                    decryptedByteIndex = decryptedByteIndex + decryptedBytesChunkLength;
                } else {
                    cipher.doFinal(content, decryptedByteIndex, content.length - decryptedByteIndex, encryptedBytes, encryptedByteIndex);
                }
            }
            return encryptedBytes;
        } catch (Exception e) {
            throw new RuntimeException("encrypt error:" + e.getMessage(), e);
        }

    }

    public static RsaDecryptResult decrypt(Key key, String content) {
        if (null == content) {
            return null;
        }
        return decrypt(key, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解密
     *
     * @param key
     * @param content
     * @return
     */
    public static RsaDecryptResult decrypt(Key key, byte[] content) {
        if (null == key) {
            throw new IllegalArgumentException("key is null");
        }
        if (null == content || content.length == 0) {
            throw new IllegalArgumentException("content is empty");
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, key);
            RSAKey rsaKey = (RSAKey) key;
            // 密钥长度(bytes)
            int encryptedBytesChunkLength = rsaKey.getModulus().bitLength() / 8;
            int numberOfEncryptedChunks = content.length / encryptedBytesChunkLength;
            int decryptedBytesLength = numberOfEncryptedChunks * encryptedBytesChunkLength;

            // Create the decoded byte array
            byte[] decryptedBytes = new byte[decryptedBytesLength];

            // Counters
            int decryptedByteIndex = 0;
            int encryptedByteIndex = 0;
            for (int i = 0; i < numberOfEncryptedChunks; i++) {
                if (i < numberOfEncryptedChunks - 1) {
                    int tmp = cipher.doFinal(content, encryptedByteIndex, encryptedBytesChunkLength, decryptedBytes, decryptedByteIndex);
                    decryptedByteIndex = decryptedByteIndex + tmp;
                    encryptedByteIndex = encryptedByteIndex + encryptedBytesChunkLength;
                } else {
                    int tmp = cipher.doFinal(content, encryptedByteIndex, content.length - encryptedByteIndex, decryptedBytes, decryptedByteIndex);
                    decryptedByteIndex = decryptedByteIndex + tmp;
                }
            }
            return new RsaDecryptResult(decryptedBytes, decryptedByteIndex);
        } catch (Exception e) {
            throw new RuntimeException("decrypt error:" + e.getMessage(), e);
        }
    }
}
