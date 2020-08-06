package com.immomo.mts.flow.limit.http.domain;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 2019071038945094L;
    private Object data;                     //数据
    private int ec = 200;               //状态码
    private String em = "success";      //状态信息

    public static Response init(Object o, EcEm ecEm) {
        Response response = new Response();
        response.setData(o);
        response.setEc(ecEm.getEc());
        response.setEm(ecEm.getEm());
        return response;
    }

    public static Response init(EcEm ecEm) {
        Response response = new Response();
        response.setEc(ecEm.getEc());
        response.setEm(ecEm.getEm());
        return response;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getEc() {
        return ec;
    }

    public void setEc(int ec) {
        this.ec = ec;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }
}
