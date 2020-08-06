package com.immomo.mts.flow.limit.http.service;

import com.immomo.mts.flow.limit.http.domain.ServiceInfo;

import java.util.List;
import java.util.Set;

/**
 * @author xing.xiantao.
 * @date 2019/11/1.
 */
public interface ServiceInfoService {
    public List<ServiceInfo> getAllAuthInfo();

    public boolean add(ServiceInfo authInfo);

    public Integer batchAdd(List<ServiceInfo> authInfos);

    List<String> getAllGroupName();

    List<ServiceInfo> getByGroupName(String groupName);

    boolean delete(String groupName, String developer);

    int batchDelete(String groupName, List<String> developers);

    ServiceInfo getByGroupNameAndDeveloper(String groupName, String developer);

    boolean checkAuth(String groupName, String developer);

    boolean checkAuthBatch(List<String> groupNames, String developer);

    List<ServiceInfo> getByDeveloper(String developer);

    List<String> searchByUserNameAndGroupName(String userName, String groupName);

    Set<String> getDevelopersByGroupName(String groupName);

    List<String> getMethodsByServiceName(String serviceName);

    ServiceInfo getAppKeyIdAndAppKeyByGroupName(String groupName);
}
