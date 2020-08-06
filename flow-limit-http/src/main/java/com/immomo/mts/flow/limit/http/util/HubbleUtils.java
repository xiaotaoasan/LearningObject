package com.immomo.mts.flow.limit.http.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.MapUtils;
import com.immomo.mcf.util.StringUtils;
import com.immomo.mts.flow.limit.http.constant.HubbleConstants;
import com.immomo.mts.flow.limit.http.domain.ServiceInfo;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author : tao.kaili
 * @description : 该类主要用于查询hubble界面数据
 * @date : 2019/10/29 下午2:23
 */
@Component
public class HubbleUtils {

    @Autowired
    private  ServiceInfoService serviceInfoService;

    private static final Logger LOG = LogFactory.getAPPLICATION();

    /**
     * 由于Hubble那边的数据只提供1天分钟级的聚合，而不提供多于1天的分钟级聚合，为了得到多余1天分钟级聚合，那么需要分批次查，将n天拆成n个一天以内的数据查。
     *
     * @param startTime
     * @param endTime
     * @param serviceName
     * @return
     */
    public Map<String, Map<String, Double>> queryMethodsCountData(Long startTime, Long endTime, String serviceName, List<String> methods) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (startTime > endTime || StringUtils.isBlank(serviceName)) {
            return resultMap;
        }
        //如果起始时间和终止时间的差值大于24小时那么就需要分成多个一天来查
        long dec = endTime - startTime;
        long oneDay = TimeUnit.DAYS.toMillis(1);
        long pageSize = (dec + oneDay - 1) / oneDay;
        //如果不超过一天那么直接查询返回
        for (int i = 0; i < pageSize; i++) {
            long start = startTime + i * oneDay;
            long end = start + oneDay;
            Map<String, Map<String, Double>> beAddMap = getBatchMethodHubbleData(start, end < endTime ? end : endTime, serviceName, methods);
            addMap(resultMap, beAddMap);
        }
        return resultMap;
    }

    /**
     * 查询某个调用方请求量
     */
    public Map<String, Map<String, Double>> queryCallSourceOpsData(Long startTime, Long endTime, String serviceName, String methodName, List<String> callers) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (startTime > endTime || CollectionUtils.isEmpty(callers)) {
            return resultMap;
        }
        //如果起始时间和终止时间的差值大于24小时那么就需要分成多个一天来查
        long dec = endTime - startTime;
        long oneDay = TimeUnit.DAYS.toMillis(1);
        long pageSize = (dec + oneDay - 1) / oneDay;
        //如果不超过一天那么直接查询返回
        for (int i = 0; i < pageSize; i++) {
            long start = startTime + i * oneDay;
            long end = start + oneDay;
            Map<String, Map<String, Double>> beAddMap = queryCallSourceOpsDataOneDay(start, end < endTime ? end : endTime, serviceName, methodName, callers);
            addMap(resultMap, beAddMap);
        }
        return resultMap;
    }

    /**
     * 查询限流量
     */
    public Map<String, Map<String, Double>> queryLimitCountData(Long startTime, Long endTime, String serviceName, String methodName, List<String> callers) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();
        if (startTime > endTime || CollectionUtils.isEmpty(callers)) {
            return resultMap;
        }
        //如果起始时间和终止时间的差值大于24小时那么就需要分成多个一天来查
        long dec = endTime - startTime;
        long oneDay = TimeUnit.DAYS.toMillis(1);
        long pageSize = (dec + oneDay - 1) / oneDay;
        //如果不超过一天那么直接查询返回
        for (int i = 0; i < pageSize; i++) {
            long start = startTime + i * oneDay;
            long end = start + oneDay;
            Map<String, Map<String, Double>> beAddMap = getLimitCountOneDay(start, end < endTime ? end : endTime, serviceName, methodName, callers);
            addMap(resultMap, beAddMap);
        }
        return resultMap;
    }

    /**
     * 查询调用方单日请求量
     */
    private Map<String, Map<String, Double>> queryCallSourceOpsDataOneDay(Long startTime, Long endTime, String serviceName, String methodName, List<String> callers) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();

        ServiceInfo serviceInfo = serviceInfoService.getAppKeyIdAndAppKeyByGroupName(serviceName);
        String appKey=serviceInfo.getAppKey();
        if (StringUtils.isBlank(appKey)) {
            return resultMap;
        }

        String appKeyId = serviceInfo.getAppKeyId();
        LogUtils.info(LOG, "getLimitCountOneDay appKeyId: {0}", appKeyId);
        //用appKey和appKeyId拼接
        String[] appKeyArray = StringUtils.split(appKey, '.');
        String appContact = new StringBuilder().append(appKeyArray[appKeyArray.length - 1]).append("_").append(appKeyId).toString();

        Map<String, Object> map = new HashMap<String, Object>();
        String metric = new StringBuilder().append(appContact).append(".").append(HubbleConstants.MOA_CALLSOURCE).append(".").append(serviceName).append(".").append(HubbleConstants.INVOCATION).toString();

        map = configToJsonMapForTag(startTime, endTime, metric, Lists.newArrayList("m-name", "source"), methodName, callers);
        String postResult = HttpRequestUtils.postReq(HubbleConstants.POST_URL, map);
        if (StringUtils.isBlank(postResult)) {
            return resultMap;
        }
        resultMap = getTimeList(postResult, "source");
        return resultMap;
    }

    /**
     * 查询单日限流量
     */
    private Map<String, Map<String, Double>> getLimitCountOneDay(Long startTime, Long endTime, String serviceName, String methodName, List<String> callers) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();

        ServiceInfo serviceInfo = serviceInfoService.getAppKeyIdAndAppKeyByGroupName(serviceName);
        String appKey=serviceInfo.getAppKey();
        if (StringUtils.isBlank(appKey)) {
            return resultMap;
        }

        String appKeyId = serviceInfo.getAppKeyId();
        LogUtils.info(LOG, "getLimitCountOneDay appKeyId: {0}", appKeyId);
        //用appKey和appKeyId拼接
        String[] appKeyArray = StringUtils.split(appKey, '.');
        String appContact = new StringBuilder().append(appKeyArray[appKeyArray.length - 1]).append("_").append(appKeyId).toString();

        Map<String, Object> map = new HashMap<String, Object>();
        String metric = new StringBuilder().append(appContact).append(".").append(HubbleConstants.BUSINESS).append(".").append("m_" + methodName).append(".").append(HubbleConstants.M_LIMIT_COUNT).toString();

        map = configToJsonMapForBatchMethod(startTime, endTime, metric, "callSource", callers);
        String postResult = HttpRequestUtils.postReq(HubbleConstants.POST_URL, map);
        if (StringUtils.isBlank(postResult)) {
            return resultMap;
        }
        resultMap = getTimeList(postResult, "callSource");
        return resultMap;
    }


    /**
     * 将两个map中相同的键对应的值加起来。
     *
     * @param queryMap
     * @param beAddMap
     */
    private void addMap(Map<String, Map<String, Double>> queryMap, Map<String, Map<String, Double>> beAddMap) {
        if (MapUtils.isEmpty(beAddMap)) {
            return;
        }

        for (Map.Entry<String, Map<String, Double>> entry : beAddMap.entrySet()) {
            String methodName = entry.getKey();
            Map<String, Double> qpsMap = entry.getValue();
            if (MapUtils.isEmpty(qpsMap)) {
                continue;
            }
            if (queryMap.containsKey(methodName)) {
                //将原来的那个map更新。
                queryMap.get(methodName).putAll(qpsMap);
            } else {
                queryMap.put(methodName, qpsMap);
            }
        }

    }


    /**
     * 根据多个方法名字批量获取hubble的数据，一天以内的都可以用这个方法来查找
     *
     * @param startTime
     * @param endTime
     * @param serviceName
     * @return
     */
    private Map<String, Map<String, Double>> getBatchMethodHubbleData(Long startTime, Long endTime, String serviceName, List<String> methodNameList) {
        Map<String, Map<String, Double>> resultMap = Maps.newHashMap();

        ServiceInfo serviceInfo = serviceInfoService.getAppKeyIdAndAppKeyByGroupName(serviceName);
        String appKey=serviceInfo.getAppKey();
        if (StringUtils.isBlank(appKey)) {
            return resultMap;
        }

        String appKeyId = serviceInfo.getAppKeyId();
        LogUtils.info(LOG, "getBatchMethodHubbleData appKeyId {0}", appKeyId);
        //用appKey和appKeyId拼接
        String[] appKeyArray = StringUtils.split(appKey, '.');
        String appContact = new StringBuilder().append(appKeyArray[appKeyArray.length - 1]).append("_").append(appKeyId).toString();

        Map<String, Object> map = new HashMap<String, Object>();
        String metric = new StringBuilder().append(appContact).append(".").append(HubbleConstants.MOA).append(".").append(serviceName).append(".").append(HubbleConstants.M_HISTOGRAM_COUNT).toString();

        map = configToJsonMapForBatchMethod(startTime, endTime, metric, "m-name", methodNameList);
        String postResult = HttpRequestUtils.postReq(HubbleConstants.POST_URL, map);
        if (StringUtils.isBlank(postResult)) {
            return resultMap;
        }
        resultMap = getTimeList(postResult, "m-name");
        return resultMap;
    }

    @Deprecated
    private String getAppKeyId(String appKey) {
        //调用http get接口来查询appKey的appKeyID
        Map<String, String> paramMap = getAppKeyIdReqMap(appKey);
        Map<String, String> headerNap = new HashMap<String, String>() {{
            put("token", HubbleConstants.TOKEN);
        }};
        String response = HttpRequestUtils.get(HubbleConstants.GET_URL, paramMap, headerNap);
        if (StringUtils.isBlank(response)) {
            return "";
        }
        Map<String, Object> tmpMap = JsonUtils.toMap(response);
        Map<String, Object> resultMap = MapUtils.getMap(tmpMap, "result");
        List<Object> data = (List<Object>) resultMap.get("data");
        Map<String, Object> appKeyMap = (Map<String, Object>) data.get(0);
        return MapUtils.getString(appKeyMap, "appKeyId");
    }

    /**
     * 拼凑get请求的json
     *
     * @param appKey
     * @return
     */
    private Map<String, String> getAppKeyIdReqMap(String appKey) {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("userId", HubbleConstants.PERSON);
        paramMap.put("appKey", appKey);
        paramMap.put("pageNo", HubbleConstants.PAGE_NO);
        paramMap.put("pageSize", HubbleConstants.PAGE_SIZE);
        return paramMap;
    }

    /**
     * 将相关的参数转成jsonmap结构
     *
     * @param startTime
     * @param endTime
     * @param metric
     * @param methodNameList
     * @return
     */
    private Map<String, Object> configToJsonMapForBatchMethod(Long startTime, Long endTime, String metric, String tagKey, List<String> methodNameList) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fillZero", "1");
        map.put("version", HubbleConstants.VERSION);
        map.put("person", HubbleConstants.PERSON);
        map.put("start", startTime.toString());
        map.put("end", endTime.toString());

        Map<String, String> param = new HashMap<String, String>();
        param.put("type", HubbleConstants.TYPE);
        param.put("tagk", tagKey);
        //将list改成字符串
        String filters = StringUtils.join(methodNameList, '|');
        param.put("filter", filters);
        param.put("groupBy", HubbleConstants.GROUPBY);

        Map<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("metric", metric);
        tmp.put("filters", Arrays.asList(param));
        tmp.put("aggregator", HubbleConstants.AGGREGATOR);
        tmp.put("downsample", HubbleConstants.DOWNSAMPLE);

        map.put("queries", Arrays.asList(tmp));
        return map;
    }


    private Map<String, Object> configToJsonMapForTag(Long startTime, Long endTime, String metric, List<String> tagKeyList, String methodName, List<String> callList) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fillZero", "1");
        map.put("version", HubbleConstants.VERSION);
        map.put("person", HubbleConstants.PERSON);
        map.put("start", startTime.toString());
        map.put("end", endTime.toString());

        //指定方法
        Map<String, String> param = new HashMap<String, String>();
        param.put("type", HubbleConstants.TYPE);
        param.put("tagk", tagKeyList.get(0));
        String filters = methodName;
        param.put("filter", filters);
        param.put("groupBy", HubbleConstants.GROUPBY);
        //指定call
        Map<String, String> param1 = new HashMap<String, String>();
        param1.put("type", HubbleConstants.TYPE);
        param1.put("tagk", tagKeyList.get(1));
        String filters1 = StringUtils.join(callList, '|');
        param1.put("filter", filters1);
        param1.put("groupBy", HubbleConstants.GROUPBY);

        Map<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("metric", metric);
        tmp.put("filters", Arrays.asList(param, param1));
        tmp.put("aggregator", HubbleConstants.AGGREGATOR);
        tmp.put("downsample", HubbleConstants.DOWNSAMPLE);

        map.put("queries", Arrays.asList(tmp));
        return map;
    }


    /**
     * 从查询的结果里面抽取出时间的集合，方法对应方法的请求量，每分钟级别的。
     *
     * @param json
     * @return
     */
    private Map<String, Map<String, Double>> getTimeList(String json, String tagKey) {
       // LogUtils.info(LOG, "getTimeList param:{0}", json);
        //得到的结果是一个List的Json串
        List<Map<String, Object>> jsonList = Lists.newArrayList();
        jsonList = JsonUtils.toT(json, new ArrayList<Map<String, Object>>().getClass());
        LogUtils.info(LOG, "getTimeList jsonList:{0}", JsonUtils.toJSON(jsonList));
        if (CollectionUtils.isEmpty(jsonList)) {
            return null;
        }
        Map<String, Map<String, Double>> queryMap = new HashMap<String, Map<String, Double>>();
        Map<String, Object> tmpObject = null;
        //遍历整个List
        for (int i = 0; i < jsonList.size(); i++) {
            tmpObject = jsonList.get(i);
            String methodName = null;
            TreeMap<String, Double> orderMap = null;
            //拿方法名字m-name
            if (tmpObject.get("tags") != null) {
                Map<String, String> tmpMap = (Map<String, String>) tmpObject.get("tags");
                methodName = tmpMap.get(tagKey);
            }
            if (tmpObject.get("dps") != null) {
                Map<String, Double> dpsMap = (Map<String, Double>) tmpObject.get("dps");
                //转成有序的map集合
                orderMap = new TreeMap<String, Double>(dpsMap);
            }
            queryMap.put(methodName, orderMap);
        }
        return queryMap;

    }

}
