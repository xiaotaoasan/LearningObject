package com.immomo.mts.flow.limit.http.service;


import com.immomo.mts.flow.limit.http.domain.CallSourceInfo;

import java.util.List;

public interface CallSourceInfoService {
    boolean add(CallSourceInfo callSourceInfo);

    boolean delete(String groupName, String callSource);


    List<CallSourceInfo> queryCallSourceInfoByPage(int start, int limitSize);

    List<CallSourceInfo> getCallSourceInfoByGroupName(String groupName);

    List<CallSourceInfo> getCallSourcesByGroupName(String groupName, String callSource);

    Integer batchAdd(List<CallSourceInfo> callSourceInfoList);

    boolean updateDescInfo(String descInfo, String groupName, String callSource);

    CallSourceInfo getOneCallInfo(String groupName, String callSource);


}
