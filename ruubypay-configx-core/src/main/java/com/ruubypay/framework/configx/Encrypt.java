package com.ruubypay.framework.configx;

/**
 * 加密接口
 * @author chenhaiyang
 */
public interface Encrypt {
    /**
     * 对字符串进行加密存储
     * @param src 源
     * @return 返回加密后的密文
     * @throws Exception 算法异常
     */
    String encrypt(String src) throws Exception;

    /**
     * 对加密后的字符串进行解密
     * @param encrypt 加密后的字符串
     * @return 返回解密后的原文
     * @throws Exception 算法异常
     */
    String decrypt(String encrypt) throws Exception;
}
