package com.ruubypay.framework.configx.encrypt.impl;

import com.ruubypay.framework.configx.Encrypt;
import com.ruubypay.framework.configx.encrypt.helper.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * AES加解密
 * @author chenhayyang
 */
public class EncryptByAes implements Encrypt{
    /**
     * 加密器
     */
    private Cipher encryptCipher;
    /**
     * 解密器
     */
    private Cipher decryptCipher;

    public EncryptByAes(String key) throws Exception {
        init(key);
    }
    /**
     * 初始化加解密组件
     * @param key 密钥
     * @throws Exception 异常
     */
    private void init(String key) throws Exception {
        byte[] bytesKey = Hex.hexStringToBytes(key);
        // KEY 转换
        Key convertSecretKey = new SecretKeySpec(bytesKey,"AES");
        this.encryptCipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
        this.decryptCipher= Cipher.getInstance("AES/ECB/PKCS5Padding");
        //初始化加密组件
        encryptCipher.init(Cipher.ENCRYPT_MODE, convertSecretKey);
        //初始化解密组件
        decryptCipher.init(Cipher.DECRYPT_MODE, convertSecretKey);
    }

    @Override
    public String encrypt(String src) throws Exception {
        byte[] resultBytes =  encryptCipher.doFinal(src.getBytes());
        return Hex.bytesToHexString(resultBytes);
    }

    @Override
    public String decrypt(String encrypt) throws Exception {
        byte[] encryptType = Hex.hexStringToBytes(encrypt);
        byte[] resultBytes =  decryptCipher.doFinal(encryptType);
        return new String(resultBytes);
    }
}
