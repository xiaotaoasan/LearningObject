package com.immomo.mts.flow.limit.http.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mts.flow.limit.http.domain.MoaInfo;
import com.immomo.mts.flow.limit.http.domain.ServiceInfo;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import com.immomo.mts.flow.limit.http.util.LogFactory;
import com.immomo.mts.flow.limit.http.util.MoaHelper;
import com.immomo.mts.flow.limit.http.util.RedisDistributedLockUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author xing.xiantao.
 * @date 2019/11/1.
 */
@Service
public class ServiceInfoUpdateTask {

    public static final Logger LOGGER = LogFactory.getAPPLICATION();

    private static final String DEVS_TO_ADD = "devsToAdd";
    private static final String DEVS_TO_DELETE = "devsToDelete";
    private static final String REDIS_LOCK_KEY = "updateServiceInfo_mts_flow_limit_11";

    @Autowired
    MoaHelper moaHelper;

    @Autowired
    ServiceInfoService serviceInfoService;

    public void updateServiceInfo(){
        boolean lock = RedisDistributedLockUtils.lock(REDIS_LOCK_KEY, "1", 10L);
        if (!lock){
            return;
        }
        LogUtils.info(LOGGER, "updateServiceInfo start ...");
        try {
            List<MoaInfo> serverMoas = moaHelper.queryAllServiceUri();
            if (CollectionUtils.isEmpty(serverMoas)){
                LogUtils.info(LOGGER, "updateServiceInfo serverMoas is empty");
            }
            Set<String> groupNameSet = Sets.newHashSet(serviceInfoService.getAllGroupName());
            for (MoaInfo moaInfo : serverMoas){
                if (groupNameSet.contains(moaInfo.getServiceUri())){
                    //更新成员
                    Map<String, Object> resMap = getDevelopersToDeleteAndAdd(moaInfo.getServiceUri(), moaInfo);
                    List<ServiceInfo> devsToAdd = (List<ServiceInfo>) resMap.get(DEVS_TO_ADD);
                    if (CollectionUtils.isEmpty(devsToAdd)){
                        continue;
                    }
                    LogUtils.info(LOGGER, "updateServiceInfo addInfo to DB devsToAdd:{0}", JsonUtils.toJSON(devsToAdd));
                    serviceInfoService.batchAdd(devsToAdd);

                } else { //添加新的url
                    List<ServiceInfo> infos = moaInfoToAuthInfos(moaInfo);
                    LogUtils.info(LOGGER, "updateServiceInfo addInfo to DB infos:{0}", JsonUtils.toJSON(infos));
                    serviceInfoService.batchAdd(infos);
                }
            }
        } catch (Exception e){
            LogUtils.error(LOGGER, e, "updateServiceInfo error");
        } finally {
            RedisDistributedLockUtils.releaseLock(REDIS_LOCK_KEY);
        }
        LogUtils.info(LOGGER, "updateServiceInfo Finish!!!");

    }

    private List<ServiceInfo> moaInfoToAuthInfos(MoaInfo moaInfo) {
        List<ServiceInfo> result = new ArrayList<ServiceInfo>();
        for (String developer : moaInfo.getDevelopers()) {
            ServiceInfo developAuth = new ServiceInfo();
            developAuth.setGroupName(moaInfo.getServiceUri());
            developAuth.setCreateTime(new Date());
            developAuth.setDeveloper(developer);
            developAuth.setAppKey(moaInfo.getAppKey());
            developAuth.setAppKeyId(moaInfo.getAppKeyId());
            result.add(developAuth);
        }

        return result;
    }

    private Map<String, Object> getDevelopersToDeleteAndAdd(String groupName, MoaInfo serverMoa) {
        Map<String, Object> res = new HashMap<String, Object>();
        Set<String> localDevelopers = serviceInfoService.getDevelopersByGroupName(groupName);
        Set<String> serverDevelopers = Sets.newHashSet(serverMoa.getDevelopers());
        if (serverDevelopers.isEmpty() || null == serverDevelopers) {
            return res;
        }

        //获得交集
        Set<String> intersection = new HashSet<String>();
        intersection.addAll(localDevelopers);
        intersection.retainAll(serverDevelopers);

        //从各自的set中去除交集，分别得到要『删除』的用户和要『添加』的用户
        localDevelopers.removeAll(intersection);
        serverDevelopers.removeAll(intersection);

        List<String> devsToDelete = new ArrayList<String>();
        devsToDelete.addAll(localDevelopers);

        List<ServiceInfo> devsToAdd = new ArrayList<ServiceInfo>();
        Iterator<String> iterator = serverDevelopers.iterator();
        while (iterator.hasNext()) {
            String developer = iterator.next();
            ServiceInfo authInfo = new ServiceInfo();
            authInfo.setGroupName(groupName);
            authInfo.setCreateTime(new Date());
            authInfo.setDeveloper(developer);
            authInfo.setAppKey(serverMoa.getAppKey());
            authInfo.setAppKeyId(serverMoa.getAppKeyId());
            devsToAdd.add(authInfo);
        }

        res.put(DEVS_TO_DELETE, devsToDelete);
        res.put(DEVS_TO_ADD, devsToAdd);
        return res;
    }
}
