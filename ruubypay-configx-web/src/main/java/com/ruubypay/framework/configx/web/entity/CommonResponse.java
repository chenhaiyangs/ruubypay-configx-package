package com.ruubypay.framework.configx.web.entity;

/**
 * 通用返回层
 * @author xx
 * @param <T>
 */
public class CommonResponse<T> {

    private boolean suc;
    private T body;
    private String message;

    public CommonResponse(boolean suc, T body, String message) {
        this.suc = suc;
        this.body = body;
        this.message = message;
    }

    public boolean isSuc() {
        return suc;
    }

    public T getBody() {
        return body;
    }

    public String getMessage() {
        return message;
    }
}