package com.immomo.mts.flow.limit.http.util;

import com.google.common.collect.Maps;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.MapUtils;
import com.immomo.mcf.util.StringUtils;
import com.immomo.mts.flow.limit.http.constant.HubbleConstants;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : tao.kaili
 * @description :该类用来提供增加和更新hubble的报警信息
 * @date : 2019/11/8 下午2:35
 */
public class AlarmUtils {

    public static final Logger LOG = LogFactory.getAPPLICATION();


    /**
     * 增加报警策略  需要传appKey,报警策略的名字，报警的阈值。
     *
     * @param serviceUri
     * @param methodName
     * @param userId
     * @return
     */
    public static boolean addQpsAlarm(String serviceUri, String methodName, String userId) {
        Map<String, String> paramMap = Maps.newHashMap();
        String appKey = MoaServiceInfoUtils.getAppkeyFromUri(serviceUri);
        if (StringUtils.isNotBlank(appKey) && StringUtils.isNotBlank(methodName)) {
            paramMap.put("appKey", appKey);
            paramMap.put("source", HubbleConstants.ALARM_SOURCE);
            String strategyName = new StringBuilder().append("methodName: ").append(methodName).append("调用方QPS超过阈值").toString();
            paramMap.put("strategyName", strategyName);
            //未上报的时候需要指定，目前先指定后面测试的时候可以看。
            paramMap.put("triggerValue", "1");
            paramMap.put("tag", "action");
            paramMap.put("tagOperator", "is");
            paramMap.put("tagValue", "m_" + methodName);
            paramMap.put("tagAggType", "tagKey");
            paramMap.put("tagAggKeys", "callSource");
            paramMap.put("tagAggFunc", "sum");
            paramMap.put("userId", userId);
            paramMap.put("level", HubbleConstants.LEVEL);
            paramMap.put("triggerOperator", "ge");
            paramMap.put("indicator", "m_limit_count");

            return addStrategy(paramMap);
        } else {
            LogUtils.error(LOG, "addQpsAlarm param is error");
            return false;
        }

    }

    /**
     * 添加策略的时候不穿id和 operator
     *
     * @param serviceUri
     * @param methodName
     * @param userId
     * @param callSource
     * @param levelValue
     * @return
     */

    public static boolean addLevelAlarm(String serviceUri, String methodName, String userId, String callSource, String levelValue) {
        Map<String, String> paramMap = Maps.newHashMap();

        String appKey = MoaServiceInfoUtils.getAppkeyFromUri(serviceUri);
        if (StringUtils.isNotBlank(appKey) && StringUtils.isNotBlank(methodName)) {
            paramMap.put("appKey", appKey);
            paramMap.put("source", HubbleConstants.ALARM_SOURCE);
            String strategyName = new StringBuilder().append("调用方:").append(callSource)
                    .append("方法名:").append(methodName).append("调用方QPS超过水位阈值").toString();

            paramMap.put("strategyName", strategyName);
            //未上报的时候需要指定，目前先指定后面测试的时候可以看。

            paramMap.put("triggerValue", levelValue);

            paramMap.put("tag", "action" + ",callSource");
            paramMap.put("tagOperator", "is" + ",is");
            paramMap.put("tagValue", "m_" + methodName + "," + callSource);

            paramMap.put("tagAggType", "tagKey");
            paramMap.put("tagAggKeys", "callSource");
            paramMap.put("tagAggFunc", "avg");
            paramMap.put("userId", userId);
            paramMap.put("level", HubbleConstants.LEVEL);
            paramMap.put("triggerOperator", "ge");
            paramMap.put("indicator", "m_count_level");
            //paramMap.put("indicator", "m_limit_count");

            //后面修改下

            //判断是否存在这个报警策略。
            String id = getHubbleStrategyInfo(appKey, strategyName);
            if (StringUtils.isNotBlank(id)) {
                paramMap.put("id", id);
                paramMap.put("operator", "update");
            }
            return addStrategy(paramMap);
        } else {
            LogUtils.error(LOG, "addLevelAlarm param is error");
            return false;
        }

    }


    /**
     * 执行get 请求判断执行结果
     *
     * @param paramMap
     * @return
     */

    private static boolean addStrategy(Map<String, String> paramMap) {
        //增加之前先要判断是否存在
        String appKey = paramMap.get("appKey");
        String strategyName = paramMap.get("strategyName");
        Map<String, String> mapHeader = new HashMap<>();
        mapHeader.put("token", HubbleConstants.ADD_STRATEGY_TOKEN);

        if (StringUtils.isNotBlank(appKey) && StringUtils.isNotBlank(strategyName)) {
            String strategyResult = HttpRequestUtils.get(HubbleConstants.ADD_STRATEGY_URL, paramMap, mapHeader);
            Map<String, Object> resultMap = JsonUtils.toMap(strategyResult);
            //如果200返回码是200那么就是成功了
            LogUtils.info(LOG, "addStrategy result {0}", resultMap);
            // LogUtils.info(LOG, "addStrategy resultMap {0}", resultMap);;
            if (MapUtils.isEmpty(resultMap) || 200 == ((Integer) resultMap.get("ec")).intValue()) {
                return true;
            }
        }
        LogUtils.info(LOG, "addStrategy is error");
        return false;

    }


    /**
     * 更新的时候只需要传前面的和更新的谁位置
     *
     * @param appKey
     * @param methodName
     * @param userId
     * @param callSource
     * @param levelValue
     * @return
     */

//    public static boolean updateLevelAlarm(String appKey, String methodName, String userId, String callSource, String levelValue) {
//
//        String strategyInfoId = getHubbleStrategyInfo(appKey, "调用方QPS超过水位阈值");
//        LogUtils.info(LOG, "updateLevelAlarm {0}", strategyInfoId);
//
//        return addLevelAlarm(appKey, methodName, userId, callSource, levelValue, strategyInfoId, "update");
//    }


    /**
     * 获取某一个报警策略下的hubble报警信息
     *
     * @param
     * @param appKey
     * @return
     */

    private static String getHubbleStrategyInfo(String appKey, String strategyName) {

        String source = "business";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("appKey", appKey);
        paramMap.put("source", source);

        Map<String, String> mapHeader = new HashMap<>();
        mapHeader.put("token", HubbleConstants.QUERY_STRATEGY_TOKEN);

        String strageResult = HttpRequestUtils.get(HubbleConstants.STRATEGY_URL, paramMap, mapHeader);


        return extractStrategyJson(strageResult, strategyName);

    }

    /**
     * 根据hubble的json结果来拿策略的id
     *
     * @return
     */
    private static String extractStrategyJson(String hubbleResult, String strategyName) {

        try {
            if (StringUtils.isNotBlank(hubbleResult) && StringUtils.isNotBlank(strategyName)) {
                Map<String, Object> hubbleResultMap = JsonUtils.toMap(hubbleResult);

                Map<String, Object> resultMap = (Map<String, Object>) hubbleResultMap.get("result");
                List<Map<String, Object>> strategyListMap = (List<Map<String, Object>>) resultMap.get("strategyList");

                for (Map<String, Object> map : strategyListMap) {
                    Map<String, String> alterStrategyMap = (Map<String, String>) map.get("alertStrategy");
                    if (strategyName.equals(alterStrategyMap.get("name"))) {
                        //存在这个
                        return alterStrategyMap.get("_id");
                    }
                }

            }
        } catch (Exception e) {
            LogUtils.error(LOG, e, "error message");
        }
        return null;
    }


}
