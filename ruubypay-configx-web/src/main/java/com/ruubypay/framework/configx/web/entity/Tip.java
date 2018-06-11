package com.ruubypay.framework.configx.web.entity;

import lombok.Data;

/**
 * 自定义后台提醒
 * @author chenhaiyang
 */
@Data
public class Tip {

    private String value;
    private String data;

    public Tip(String value, String data) {
        this.value = value;
        this.data = data;
    }
}
