package com.immomo.mts.flow.limit.http.mapper;


import com.immomo.mts.flow.limit.http.domain.CallSourceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CallSourceInfoMapper {
    List<CallSourceInfo> queryCallSourceInfoByPage(@Param("start") Integer limitStart, @Param("size") Integer limitSize);

    void save(CallSourceInfo callSourceInfo);

    boolean delete(@Param("groupName") String groupName, @Param("callSource") String callSource);

    List<CallSourceInfo> getCallSourceByGroupName(@Param("groupName") String groupName);

    List<CallSourceInfo> searchGroupNameAndCallSource(@Param("groupName") String groupName, @Param("callSource") String callSource);

    boolean deleteAll();

    void updateDescInfo(@Param("descInfo") String descInfo, @Param("groupName") String groupName, @Param("callSource") String callSource);

    CallSourceInfo getOneCallInfo(@Param("groupName") String groupName, @Param("callName") String callName);


}
