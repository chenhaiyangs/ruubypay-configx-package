package com.ruubypay.framework.configx.encrypt.impl;

import com.ruubypay.framework.configx.Encrypt;
import com.ruubypay.framework.configx.encrypt.helper.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;

/**
 * 采用DES的加解密算法进行对称加解密
 * @author chenhaiyang
 */
@SuppressWarnings("all")
public class EncryptByDes implements Encrypt{
    /**
     * 缓存加密时使用的Cipher
     */
    private ThreadLocal<Cipher> encryptThreadLocal=null;
    /**
     * 缓存解密时使用的Cipher
     */
    private ThreadLocal<Cipher> decryptThreadLocal=null;
    /**
     * 密钥key
     */
    private Key convertSecretKey;

    public EncryptByDes(String key) throws Exception {
        init(key);
    }

    /**
     * 初始化加解密组件
     * @param key 密钥
     * @throws Exception 异常
     */
    private void init(String key) throws Exception {
        //生成密钥
        byte[] bytesKey = Hex.hexStringToBytes(key);
        DESKeySpec desKeySpec = new DESKeySpec(bytesKey);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        this.convertSecretKey = factory.generateSecret(desKeySpec);

        //pre-test 在初始化时尝试生成栈内实例，防止ThreadLocal生成时发生错误
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.convertSecretKey);

        encryptThreadLocal = ThreadLocal.withInitial(()->newCipher(Cipher.ENCRYPT_MODE));
        decryptThreadLocal = ThreadLocal.withInitial(()->newCipher(Cipher.DECRYPT_MODE));
    }

    /**
     * 为线程生成一个实例副本
     * @param mode 加密模式为1，解密模式为2
     * @return 返回Cipher
     */
    private Cipher newCipher(int mode){
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(mode, this.convertSecretKey);
            return cipher;
        }catch (Exception e){
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public String encrypt(String src) throws Exception {
        byte[] resultBytes =  encryptThreadLocal.get().doFinal(src.getBytes());
        return Hex.bytesToHexString(resultBytes);
    }

    @Override
    public String decrypt(String encrypt) throws Exception{
        byte[] encryptType = Hex.hexStringToBytes(encrypt);
        byte[] resultBytes =  decryptThreadLocal.get().doFinal(encryptType);
        return new String(resultBytes);
    }
}
