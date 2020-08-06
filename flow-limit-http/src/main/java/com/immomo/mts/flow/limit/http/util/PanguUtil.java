package com.immomo.mts.flow.limit.http.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.immomo.mcf.util.*;
import com.immomo.mts.flow.limit.http.domain.LimitConfig;
import com.immomo.mts.flow.limit.http.domain.ServiceInfo;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author xing.xiantao.
 * @date 2019/11/8.
 */
@Component
public class PanguUtil {

    @Autowired
    private ServiceInfoService serviceInfoService;

    private static final Logger LOG = LogFactory.getAPPLICATION();

    private static String DEMOTE_UUID = "68f7ac3a-6a6d-4e0d-a906-bae79838d2ce";

    private static String URL_UPDATE = "http://api-pangu.momo.com/mtsLimit/saveOrUpdateLimitConfig";

    private static String URL_GET_ALL = "http://api-pangu.momo.com/mtsLimit/getAllLimitConfig";

    private boolean saveOrUpdateConfigInner(String key, String value, String momoId){
        Map<String, String> headerMap = new HashMap<String, String>(){{
            put("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            put("authorization", createToken());
        }};

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("configKey",key);
        paramMap.put("configValue",value);
        paramMap.put("momoId",momoId);
        paramMap.put("momoName",momoId);
        String result = HttpRequestUtils.post(URL_UPDATE, paramMap, headerMap);
        LogUtils.info(LOG, "saveOrUpdateConfig result={0}", result);
        Map<String, Object> rsultMap = JsonUtils.toMap(result);
        if (MapUtils.getInteger(rsultMap,"ec").equals(200)){
            return true;
        }
        return false;
    }

    public boolean saveOrUpdateConfig(LimitConfig limitConfig, String momoid ) {
        //生成key和value
        String appKey = serviceInfoService.getAppKeyIdAndAppKeyByGroupName(limitConfig.getServiceName()).getAppKey();
        String key = appKey+"::mts_flow_limit_config";
        LogUtils.info(LOG, "saveOrUpdateConfig key={0}", key);
        String value = getAllConfig(appKey);
        if (StringUtils.isBlank(value)){  //配置不存在
            Map<String, Object> jsonMap = Maps.newHashMap();
            jsonMap.put(limitConfig.getMethodName(),Lists.newArrayList(transBeanToValue(limitConfig)));
            return saveOrUpdateConfigInner(key,JsonUtils.toJSON(jsonMap), momoid);
        }
        Map<String, Object> jsonMap = JsonUtils.toMap(value);
        if (jsonMap.containsKey(limitConfig.getMethodName())){
            List<Map<String, Object>> configList = (List<Map<String, Object>>)jsonMap.get(limitConfig.getMethodName());
            boolean flag = false;
            for (Map<String, Object> configMap : configList){
                String callSource = MapUtils.getString(configMap, "appKey","");
                if (StringUtils.equals(callSource, limitConfig.getCallSource())){  //修改
                    configMap.putAll(transBeanToValue(limitConfig));
                    flag = true;
                }
            }
            if (!flag){   //增加调用方配置
                configList.add(transBeanToValue(limitConfig));
            }
        } else {
            jsonMap.put(limitConfig.getMethodName(), Lists.newArrayList(transBeanToValue(limitConfig)));
        }
        return saveOrUpdateConfigInner(key, JsonUtils.toJSON(jsonMap), momoid);
    }

    private Map<String, Object> transBeanToValue(LimitConfig limitConfig) {
        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put("limit", limitConfig.getLimitSwitch());
        configMap.put("appKey", limitConfig.getCallSource());
        configMap.put("value", limitConfig.getLimitNum());
        configMap.put("capacityLevel", limitConfig.getCapacityLevel());
        return configMap;
    }

    public boolean firstAccessNotice(String serviceName, String momoid){
        String appKey = getAppKeyByServiceName(serviceName);
        String key = appKey+"::mts_flow_limit_config";
        String value = "{}";
        return saveOrUpdateConfigInner(key, value, momoid);
    }

    /**
     * 获取限流信息根据方法名称
     * @param serviceName
     * @param methodName
     * @return  callSource:LimitConfig
     */
    public Map<String, LimitConfig> getLimitConfigByMethodName(String serviceName, String methodName){
        Map<String, LimitConfig> result = Maps.newHashMap();
        //通过serviceName拿appKeyId
        String appKey = getAppKeyByServiceName(serviceName);
        String value = getAllConfig(appKey);
//        LogUtils.info(LOG, "getLimitConfigByMethodName appKey={0},value={1}", appKey, value);
        //解析配置值
        Map<String, Object> jsonMap = JsonUtils.toMap(value);
        if (MapUtils.isEmpty(jsonMap)){
            return result;
        }
        for (Map.Entry<String,Object> entry : jsonMap.entrySet()){
            String method= entry.getKey();
            List<Map<String, Object>> callList = (List<Map<String, Object>>)entry.getValue();
            for (Map<String, Object> callMap : callList) {
                LimitConfig limitConfig = new LimitConfig();
                limitConfig.setCallSource(MapUtils.getString(callMap, "appKey"));
                limitConfig.setCapacityLevel(MapUtils.getDouble(callMap,"capacity"));
                limitConfig.setLimitNum(MapUtils.getInteger(callMap, "value"));
                limitConfig.setLimitSwitch(MapUtils.getBoolean(callMap, "limit"));
                limitConfig.setMethodName(methodName);
                limitConfig.setServiceName(serviceName);
                result.put(MapUtils.getString(callMap,"appKey"), limitConfig);
            }
        }
        return result;
    }

    private String getAppKeyByServiceName(String serviceName){
        ServiceInfo serviceInfo = serviceInfoService.getAppKeyIdAndAppKeyByGroupName(serviceName);
        return serviceInfo.getAppKey();
    }

    private String getAllConfig(String appKey){
        Map<String, String> headerMap = new HashMap<String, String>(){{
            put("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            put("authorization", createToken());
        }};
        LogUtils.info(LOG, "getAllConfig headerMap={0}", JsonUtils.toJSON(headerMap));
        String json = HttpRequestUtils.post(URL_GET_ALL, Maps.newHashMap(), headerMap);
        LogUtils.info(LOG, "getAllConfig json={0}", json);
        Map<String, Object> resultMap = JsonUtils.toMap(json);
        List<Map<String, Object>> configList = (List<Map<String, Object>>)resultMap.get("result");
        if (CollectionUtils.isEmpty(configList)){
            return null;
        }
        for (Map<String, Object> map : configList){
            String configKey = MapUtils.getString(map, "configKey");
            String tempAppKey = getAppKeyFromConfigKey(configKey);
            if (StringUtils.equals(appKey, tempAppKey)){
                return MapUtils.getString(map, "configValue");
            }
        }
        return null;
    }

    private static String getAppKeyFromConfigKey(String configKey) {
        return StringUtils.split(configKey, "::")[0];
    }

    private static String createToken(){
        String temp = DEMOTE_UUID + "_" + getTime();
        return Base64.getEncoder().encodeToString(ByteUtils.getBytes(temp));
    }

    private static String getTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        String year = String.valueOf(cal.get(Calendar.YEAR));
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        String month = currentMonth < 10 ? "0" + currentMonth : currentMonth + "";
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        String day = currentDay < 10 ? 0 + (currentDay + "") : currentDay + "";
        int currentHours = cal.get(Calendar.HOUR_OF_DAY);
        String hours = currentHours < 10 ? 0 + (currentHours + "") : currentHours + "";
        return year + month + day + hours;
    }
}
