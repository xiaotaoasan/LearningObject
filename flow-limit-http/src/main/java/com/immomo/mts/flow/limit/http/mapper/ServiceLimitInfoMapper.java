package com.immomo.mts.flow.limit.http.mapper;


import com.immomo.mts.flow.limit.http.domain.CallSourceInfo;
import com.immomo.mts.flow.limit.http.domain.ServiceLimitInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceLimitInfoMapper {
    List<CallSourceInfo> queryInfoByPage(@Param("start") Integer limitStart, @Param("size") Integer limitSize);

    void save(ServiceLimitInfo serviceLimitInfo);

    boolean delete(@Param("groupName") String groupName);

    ServiceLimitInfo getInfoByGroupName(@Param("groupName") String groupName);

    boolean deleteAll();



}
