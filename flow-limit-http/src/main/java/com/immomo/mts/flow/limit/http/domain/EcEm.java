package com.immomo.mts.flow.limit.http.domain;

public enum EcEm {
    SUCCESS(200, "success"),
    NO_PERMISSION(401, "no permission"),
    OUTSIDE_OF_CHINA(402, "outside of China"),
    NOT_OPENED(403, "not opened"),
    NO_DATA(404, "this area does not have any data currently"),
    PARAM_ERROR(500, "param_error");


    EcEm(int ec, String em){
        this.ec = ec;
        this.em = em;
    }
    private int ec;

    private String em;

    public void setEc(int ec) {
        this.ec = ec;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public int getEc() {
        return ec;
    }

    public String getEm() {
        return em;
    }
}
