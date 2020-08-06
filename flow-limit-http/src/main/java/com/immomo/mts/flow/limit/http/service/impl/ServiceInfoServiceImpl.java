package com.immomo.mts.flow.limit.http.service.impl;

import com.immomo.mcf.util.LogUtils;
import com.immomo.mts.flow.limit.http.domain.ServiceInfo;
import com.immomo.mts.flow.limit.http.mapper.ServiceInfoMapper;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import com.immomo.mts.flow.limit.http.util.LogFactory;
import com.immomo.mts.flow.limit.http.util.MoaServiceInfoUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author xing.xiantao.
 * @date 2019/11/1.
 */
@Service
public class ServiceInfoServiceImpl implements ServiceInfoService {

    public static final Logger LOGGER = LogFactory.getAPPLICATION();

    @Autowired
    private ServiceInfoMapper serviceInfoMapper;

    @Override
    public List<ServiceInfo> getAllAuthInfo() {
        return null;
    }

    @Override
    public boolean add(ServiceInfo authInfo) {
        try {
            serviceInfoMapper.save(authInfo);
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, e, "save moa info error . groupName:{0}", authInfo.getGroupName());
            return false;
        }
    }

    @Override
    public Integer batchAdd(List<ServiceInfo> authInfos) {
        Integer addSize = 0;
        for (ServiceInfo authInfo : authInfos) {
            try {
                boolean success = add(authInfo);
                if (success) {
                    addSize++;
                }
            } catch (Exception e) {
                LogUtils.error(LOGGER, e, "add error authInfo error. |{0}|", authInfo);
            }
        }
        return addSize;
    }

    @Override
    public List<String> getAllGroupName() {
        return serviceInfoMapper.getAllGroupName();
    }

    @Override
    public List<ServiceInfo> getByGroupName(String groupName) {
        return serviceInfoMapper.getByGroupName(groupName);
    }

    @Override
    public boolean delete(String groupName, String developer) {
        return false;
    }

    @Override
    public int batchDelete(String groupName, List<String> developers) {
        return 0;
    }

    @Override
    public ServiceInfo getByGroupNameAndDeveloper(String groupName, String developer) {
        return null;
    }

    @Override
    public boolean checkAuth(String groupName, String developer) {
        return false;
    }

    @Override
    public boolean checkAuthBatch(List<String> groupNames, String developer) {
        return false;
    }

    @Override
    public List<ServiceInfo> getByDeveloper(String developer) {
        return serviceInfoMapper.getByDeveloper(developer);
    }

    @Override
    public List<String> searchByUserNameAndGroupName(String userName, String groupName) {
        return serviceInfoMapper.searchByUserNameAndGroupName(userName, groupName);
    }

    @Override
    public Set<String> getDevelopersByGroupName(String groupName) {
        return serviceInfoMapper.getDevelopersByGroupName(groupName);
    }

    @Override
    public List<String> getMethodsByServiceName(String serviceName) {
        return MoaServiceInfoUtils.getMethodListFromMoa(serviceName);
    }

    @Override
    public ServiceInfo getAppKeyIdAndAppKeyByGroupName(String groupName) {
        return serviceInfoMapper.getAppKeyIdAndAppKeyByGroupName(groupName);
    }
}
