package com.immomo.mts.flow.limit.http.domain;

import java.util.List;
import java.util.Objects;

public class MoaInfo {

    private String serviceUri;
    private String owner;
    private List<String> developers;
    private String appKey;
    private String appKeyId;

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
        this.developers = developers;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoaInfo moaInfo = (MoaInfo) o;
        return Objects.equals(serviceUri, moaInfo.serviceUri) &&
                Objects.equals(owner, moaInfo.owner) &&
                Objects.equals(developers, moaInfo.developers) &&
                Objects.equals(appKey, moaInfo.appKey) &&
                Objects.equals(appKeyId, moaInfo.appKeyId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceUri, owner, developers, appKey, appKeyId);
    }

    @Override
    public String toString() {
        return "MoaInfo{" +
                "serviceUri='" + serviceUri + '\'' +
                ", owner='" + owner + '\'' +
                ", developers=" + developers +
                ", appKey='" + appKey + '\'' +
                ", appKeyId='" + appKeyId + '\'' +
                '}';
    }
}
