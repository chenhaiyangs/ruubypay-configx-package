package com.ruubypay.framework.configx.encrypt.helper;

import com.ruubypay.framework.configx.encrypt.impl.EncryptByAes;
import com.ruubypay.framework.configx.encrypt.impl.EncryptByDes;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * 生成密钥工具类
 * @author chenhaiyang
 */
public class GenerateKeyUtil {

    /**
     * 生成DES加密算法可用的密钥
     * @return 返回结果
     * @throws NoSuchAlgorithmException 异常
     */
    public static String getStringSecturyKeyByDes() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        //指定keysize
        keyGenerator.init(56);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] bytesKey = secretKey.getEncoded();
        return Hex.bytesToHexString(bytesKey);
    }

    /**
     * 生成AES加密算法可用的密钥
     * @return 返回结果
     * @throws NoSuchAlgorithmException 异常
     */
    public static String getStringSecturyKeyByAes() throws NoSuchAlgorithmException {
        // 生成KEY
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] keyBytes = secretKey.getEncoded();
        return Hex.bytesToHexString(keyBytes);
    }

    /**
     * 根据密钥和原始报文使用DES算法加密一个信息，返回加密结果
     * @param src 原文
     * @param key key 一个工具类
     * @return 返回加密结果
     * @throws NoSuchAlgorithmException 异常
     */
    public static String getEncryptResultByDes(String src,String key) throws Exception {
        EncryptByDes encryptByDes = new EncryptByDes(key);
        return encryptByDes.encrypt(src);
    }

    /**
     * 根据密钥和原始报文使用AES算法加密一个信息，返回加密结果
     * @param src 原文
     * @param key key 一个工具类
     * @return 返回加密结果
     * @throws NoSuchAlgorithmException 异常
     */
    public static String getEncryptResultByAes(String src,String key) throws Exception {
        EncryptByAes encryptByDes = new EncryptByAes(key);
        return encryptByDes.encrypt(src);
    }
}
