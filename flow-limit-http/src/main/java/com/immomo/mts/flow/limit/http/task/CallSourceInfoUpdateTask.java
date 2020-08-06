package com.immomo.mts.flow.limit.http.task;


import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.MapUtils;
import com.immomo.mts.flow.limit.http.constant.HubbleConstants;
import com.immomo.mts.flow.limit.http.domain.CallSourceInfo;
import com.immomo.mts.flow.limit.http.domain.ServiceInfo;
import com.immomo.mts.flow.limit.http.service.CallSourceInfoService;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import com.immomo.mts.flow.limit.http.util.HttpRequestUtils;
import com.immomo.mts.flow.limit.http.util.LogFactory;
import com.immomo.mts.flow.limit.http.util.MoaHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : tao.kaili
 * @description : 定时向数据库里面导入调用来源的方法
 * @date : 2019/11/5 下午4:20
 */
@Service
public class CallSourceInfoUpdateTask {

    public static final Logger LOGGER = LogFactory.getAPPLICATION();


    @Autowired
    MoaHelper moaHelper;

    @Autowired
    ServiceInfoService serviceInfoService;

    @Autowired
    CallSourceInfoService callSourceInfoService;


    /**
     * 根据developers来拿groupName列表
     */
    public void updateCallSourceInfo() {
        LogUtils.info(LOGGER, "updateCallSourceInfo start");
        List<String> groupNameList = serviceInfoService.getAllGroupName();

        //根据服务名groupName去callsource_info_a表查数据，看看对应的groupName的callsource
        RateLimiter rateLimiter = RateLimiter.create(1);
        for (String serviceName : groupNameList) {
            rateLimiter.acquire();
            updateCallSourceInfoByServiceName(serviceName);
        }
        LogUtils.info(LOGGER, "updateCallSourceInfo Finish!!!");
    }

    public void updateCallSourceInfoByServiceName(String serviceName){

        List<CallSourceInfo> queryList = callSourceInfoService.getCallSourceInfoByGroupName(serviceName);
        List<ServiceInfo> serviceInfoList = serviceInfoService.getByGroupName(serviceName);
        if (CollectionUtils.isEmpty(serviceInfoList)){
            return;
        }
        ServiceInfo serviceInfo = serviceInfoList.get(0);
        List<CallSourceInfo> hubbleList = getHubbleCallSource(serviceInfo);
        LogUtils.info(LOGGER, "updateCallSourceInfoByServiceName queryList hubbleList: {0},{1}", JsonUtils.toJSON(queryList), JsonUtils.toJSON(hubbleList));
        //对数据库进行操作，少的增加多的删除
        if (CollectionUtils.isEmpty(hubbleList)) {
            LogUtils.info(LOGGER, "Hubble call_source is empty");
            return;
        }
        if (CollectionUtils.isEmpty(queryList)) {
            LogUtils.info(LOGGER, "queryDatabase call_source is empty");
            callSourceInfoService.batchAdd(hubbleList);
        }
        //复写了equal方法，取交集
        deleteAndAddCallSource(queryList, hubbleList);
        LogUtils.info(LOGGER, "updateCallSourceInfoByServiceName success serviceName:{0}", serviceName);

    }

    /**
     * 解析JSON,拿到callsource的值
     *
     * @param tagResult
     * @return
     */
    private List<CallSourceInfo> extractJson(String tagResult, String serviceName) {
        try {
            List<CallSourceInfo> list = new ArrayList<>();
            Map<String, Object> result = JsonUtils.toMap(tagResult);
            if (MapUtils.isEmpty(result)) {
                return list;
            } else {
                Map<String, Object> callSource = (Map<String, Object>) JSONPath.extract(tagResult, String.join(".", "$", "result", "sourceMap", "moa_callsource", "actionMap"));
                if (MapUtils.isEmpty(callSource)){
                    return list;
                }
                Map<String, Object> serviceUri = (Map<String, Object>) callSource.get(serviceName);
                if (MapUtils.isEmpty(serviceUri)){
                    return list;
                }
                Map<String, Object> tagMap = (Map<String, Object>) serviceUri.get("tagValueMap");
                if (MapUtils.isEmpty(tagMap)){
                    return list;
                }
                Map<String, Object> source = (Map<String, Object>) tagMap.get("source");
                if (MapUtils.isEmpty(source)){
                    return list;
                }
                List<Map<String, String>> callSourceList = (List<Map<String, String>>) source.get("tagValueList");
                if (CollectionUtils.isEmpty(callSourceList)){
                    return list;
                }
                for (Map<String, String> map : callSourceList) {
                    CallSourceInfo callSourceInfo = new CallSourceInfo();
                    callSourceInfo.setCallSource(map.get("value"));
                    callSourceInfo.setGroupName(serviceName);
                    callSourceInfo.setCreateTime(new Date());
                    callSourceInfo.setDescInfo("");
                    list.add(callSourceInfo);
                }
                return list;
            }
        } catch (Exception e){
            LogUtils.error(LOGGER, e,"extractJson error serviceName:{0},tagResult:{1}", serviceName, tagResult);
        }
        return null;
    }


    /**
     * 根据传入的serviceInfo集合来查对应groupName的所有的callSource http查hubble拿source
     *
     * @param serviceInfos
     * @return
     */
    private Map<String, List<CallSourceInfo>> getHubbleCallSource(List<ServiceInfo> serviceInfos) {
        LogUtils.info(LOGGER, "serviceInfos {0}", serviceInfos);
        Map<String, List<CallSourceInfo>> map = Maps.newHashMap();

        RateLimiter rateLimiter = RateLimiter.create(1);
        Map<String, String> headerNap = new HashMap<String, String>() {{
            put("token", HubbleConstants.TAG_TOKEN);
        }};
        for (ServiceInfo serviceInfo : serviceInfos) {
            rateLimiter.acquire();
            Map<String, String> bodyMap = new HashMap<String, String>() {{
                put("appKey", serviceInfo.getAppKey());
                put("source", HubbleConstants.CALL_SOURCE);
                put("action", serviceInfo.getGroupName());
            }};
            String tagResult = HttpRequestUtils.get(HubbleConstants.TAG_URL, bodyMap, headerNap);
            List<CallSourceInfo> tagCallSourceList = extractJson(tagResult, serviceInfo.getGroupName());
            if (CollectionUtils.isEmpty(tagCallSourceList)){
                continue;
            }
            map.put(serviceInfo.getGroupName(), tagCallSourceList);
        }
        return map;
    }

    /**
     * 根据传入的serviceInfo集合来查对应groupName的所有的callSource http查hubble拿source
     */
    private List<CallSourceInfo> getHubbleCallSource(ServiceInfo serviceInfo) {

        Map<String, String> headerNap = new HashMap<String, String>() {{
            put("token", HubbleConstants.TAG_TOKEN);
        }};

        Map<String, String> bodyMap = new HashMap<String, String>() {{
            put("appKey", serviceInfo.getAppKey());
            put("source", HubbleConstants.CALL_SOURCE);
            put("action", serviceInfo.getGroupName());
        }};
        String tagResult = HttpRequestUtils.get(HubbleConstants.TAG_URL, bodyMap, headerNap);
        List<CallSourceInfo> tagCallSourceList = extractJson(tagResult, serviceInfo.getGroupName());
        if (CollectionUtils.isEmpty(tagCallSourceList)){
            return null;
        }
        return tagCallSourceList;
    }


    private void deleteAndAddCallSource(List<CallSourceInfo> queryList, List<CallSourceInfo> hubbleList) {

        List<CallSourceInfo> listTmp = new ArrayList<>();
        listTmp.addAll(queryList);
        //取交集,这部分保持不动
        queryList.retainAll(hubbleList);
        //将多余的干掉,差集
        listTmp.removeAll(queryList);
        //现在的listTmp就是多余的需要被干掉的

        //删除多余的
        if (CollectionUtils.isNotEmpty(listTmp)) {
            for (CallSourceInfo callSourceInfo : listTmp
                    ) {
                callSourceInfoService.delete(callSourceInfo.getGroupName(), callSourceInfo.getCallSource());
            }
        }
        //hubbleList里面需要驾到数据库的
        hubbleList.removeAll(queryList);
        if (CollectionUtils.isNotEmpty(hubbleList)) {
            Integer i = callSourceInfoService.batchAdd(hubbleList);
            LogUtils.info(LOGGER, "deleteAndAddCallSource add number {0}", i);
        }

    }

}
