package com.immomo.mts.flow.limit.http.mapper;

import com.immomo.mts.flow.limit.http.domain.ServiceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ServiceInfoMapper {

    List<ServiceInfo> queryAuthInfoByPage(@Param("start") Integer limitStart, @Param("size") Integer limitSize);

    void save(ServiceInfo moaModel);

    List<String> getAllGroupName();

    List<ServiceInfo> getByGroupName(@Param("groupName") String groupName);

    boolean delete(@Param("groupName") String groupName, @Param("developer") String developer);

    ServiceInfo getByGroupNameAndDeveloper(@Param("groupName") String groupName, @Param("developer") String developer);

    List<ServiceInfo> getByGroupNamesAndDeveloper(@Param("groupNames") List<String> groupNames, @Param("developer") String developer);

    List<ServiceInfo> getByDeveloper(@Param("developer") String developer);

    List<String> searchByUserNameAndGroupName(@Param("userName") String userName, @Param("groupName") String groupName);

    Set<String> getDevelopersByGroupName(@Param("groupName") String groupName);

    boolean deleteAll();

    ServiceInfo getAppKeyIdAndAppKeyByGroupName(@Param("groupName") String groupName);
}
