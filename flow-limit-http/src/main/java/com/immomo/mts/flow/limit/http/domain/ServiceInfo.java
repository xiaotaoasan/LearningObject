package com.immomo.mts.flow.limit.http.domain;

import java.util.Date;

public class ServiceInfo {

    private Integer id;
    private String groupName;
    private String developer;
    private String appKey;
    private String appKeyId;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppKeyId() {
        return appKeyId;
    }

    public void setAppKeyId(String appKeyId) {
        this.appKeyId = appKeyId;
    }

    @Override
    public String toString() {
        return "ServiceInfo{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", developer='" + developer + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appKeyId='" + appKeyId + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
