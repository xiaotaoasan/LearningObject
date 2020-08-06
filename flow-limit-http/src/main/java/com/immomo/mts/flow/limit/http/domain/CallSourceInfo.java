package com.immomo.mts.flow.limit.http.domain;

import java.util.Date;
import java.util.Objects;

/**
 * @author : tao.kaili
 * @description :
 * @date : 2019/11/4 下午4:42
 */
public class CallSourceInfo {
    private Integer id;
    private String groupName;
    private String callSource;
    private Date createTime;
    private String descInfo;

    public String getDescInfo() {
        return descInfo;
    }
    public void setDescInfo(String descInfo) {
        this.descInfo = descInfo;
    }
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

    public String getCallSource() {
        return callSource;
    }

    public void setCallSource(String callSource) {
        this.callSource = callSource;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CallSourceInfo callSourceInfo = (CallSourceInfo) obj;
        return Objects.equals(groupName, callSourceInfo.groupName) &&
                Objects.equals(callSource, callSourceInfo.callSource);
    }

    @Override
    public int hashCode() {

        return Objects.hash(groupName,callSource);
    }

    @Override
    public String toString() {
        return "CallSourceInfo{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", callSource='" + callSource + '\'' +
                ", createTime=" + createTime +
                ", descInfo='" + descInfo + '\'' +
                '}';
    }
}
