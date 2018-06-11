package com.ruubypay.framework.configx.test;

import com.ruubypay.framework.configx.bean.annotation.ProperKey;


public class DemoBean {

    @ProperKey(key="log_name")
    private String name;

    private Long longS;
    @ProperKey(key = "long_n")
    private long longN;

    private Double aDouble;

    private double bDouble;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLongS() {
        return longS;
    }

    public void setLongS(Long longS) {
        this.longS = longS;
    }

    public long getLongN() {
        return longN;
    }

    public void setLongN(long longN) {
        this.longN = longN;
    }

    public Double getaDouble() {
        return aDouble;
    }

    public void setaDouble(Double aDouble) {
        this.aDouble = aDouble;
    }

    public double getbDouble() {
        return bDouble;
    }

    public void setbDouble(double bDouble) {
        this.bDouble = bDouble;
    }
}
