package com.immomo.mts.flow.limit.http.domain;

import java.util.Date;

public class ServiceLimitInfo {

    private Integer id;
    private String groupName;
    private Integer limitStatus;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getLimitStatus() {
        return limitStatus;
    }

    public void setLimit_status(Integer limit_status) {
        this.limitStatus = limit_status;
    }

    @Override
    public String toString() {
        return "ServiceLimitInfo{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", createTime=" + createTime +
                ", limitStatus=" + limitStatus +
                '}';
    }
}
