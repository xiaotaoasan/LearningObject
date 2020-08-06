package com.immomo.mts.flow.limit.http.domain;

import java.io.Serializable;

/**
 * @author xing.xiantao.
 * @date 2019/11/8.
 */
public class LimitConfig implements Serializable {

    private static final long serialVersionUID = 2566585708845367915L;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 调用方
     */
    private String callSource;

    /**
     * 限流开关
     */
    private Boolean limitSwitch;

    /**
     * 限流阈值
     */
    private Integer limitNum;

    /**
     * 流量的容量水位qps超过limitNum*CapacityLevel 触发报警
     */
    private Double capacityLevel;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCallSource() {
        return callSource;
    }

    public void setCallSource(String callSource) {
        this.callSource = callSource;
    }

    public Boolean getLimitSwitch() {
        return limitSwitch;
    }

    public void setLimitSwitch(Boolean limitSwitch) {
        this.limitSwitch = limitSwitch;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Double getCapacityLevel() {
        return capacityLevel;
    }

    public void setCapacityLevel(Double capacityLevel) {
        this.capacityLevel = capacityLevel;
    }
}
