package com.immomo.mts.flow.limit.http.Controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.MapUtils;
import com.immomo.mcf.util.StringUtils;
import com.immomo.mts.flow.limit.http.domain.*;
import com.immomo.mts.flow.limit.http.mapper.ServiceLimitInfoMapper;
import com.immomo.mts.flow.limit.http.service.CallSourceInfoService;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import com.immomo.mts.flow.limit.http.task.CallSourceInfoUpdateTask;
import com.immomo.mts.flow.limit.http.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xing.xiantao.
 * @date 2019/10/28.
 */

@Controller
@RequestMapping(value = "/data")
public class DataController {

    @Autowired
    private ServiceInfoService serviceInfoService;

    @Autowired
    private CallSourceInfoUpdateTask updateTask;

    @Autowired
    HubbleUtils hubbleUtils;

    @Autowired
    PanguUtil panguUtil;

    @Autowired
    private CallSourceInfoService callSourceInfoService;

    @Autowired
    private ServiceLimitInfoMapper limitInfoMapper;

    public static final Logger LOGGER = LogFactory.getAPPLICATION();

    /**
     * 查询服务列表
     */
    @RequestMapping("/getServiceByName")
    @ResponseBody
    public Response getServiceByName(HttpServletRequest request, @RequestParam("serviceName") String serviceName) {
        Object userName = request.getAttribute("userName");
        LogUtils.info(LOGGER, "getServiceByName userName={0}", userName);
        userName = "xing.xiantao";

        List<String> list = serviceInfoService.searchByUserNameAndGroupName((String) userName, serviceName);
        return Response.init(list, EcEm.SUCCESS);
    }

    /**
     * 查询方法列表请求量
     */
    @RequestMapping("/getMethodsOps")
    @ResponseBody
    public Response getMethodsOps(HttpServletRequest request,@RequestParam("serviceName") String serviceName,
                                  @RequestParam(value = "methodName", required = false) String methodName,
                                  @RequestParam("startTime") String startTimeStamp,
                                  @RequestParam("endTime") String endTimeStamp,
                                  @RequestParam("currPage") int currPage,
                                  @RequestParam("pageSize") int pageSize) {
        LogUtils.info(LOGGER, "getMethodsOps serviceName={0},methodName={1},startTime={2},endTime={3},currPage={4},pageSize={5}", serviceName, methodName, startTimeStamp, endTimeStamp, currPage, pageSize);
        Long startTime = TimeUtil.timeStampToLong(startTimeStamp);
        Long endTime = TimeUtil.timeStampToLong(endTimeStamp);
        List<String> dataMehtodName = Lists.newArrayList();
        List<String> allMethodList = serviceInfoService.getMethodsByServiceName(serviceName);
        if (CollectionUtils.isEmpty(allMethodList)){
            return Response.init(EcEm.NO_DATA);
        }
        if (StringUtils.isBlank(methodName)){
            dataMehtodName.addAll(allMethodList);
        } else {
            for (String name : allMethodList){
                if (name.contains(methodName)){
                    dataMehtodName.add(name);
                }
            }
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        List<List<String>> partList = Lists.partition(dataMehtodName, pageSize);
        if (currPage > partList.size()){
            return Response.init(resultMap, EcEm.SUCCESS);
        }
        List<String> queryMehtodList = Lists.newArrayList(partList.get(currPage-1));
        Map<String, Map<String, Double>> opsDataMap = hubbleUtils.queryMethodsCountData(startTime, endTime, serviceName, queryMehtodList);
        List<Double> reqColList = Lists.newArrayList();
        for (String methed : queryMehtodList){
            Map<String, Double> opsMap = opsDataMap.get(methed);
            if (MapUtils.isEmpty(opsMap)){
                reqColList.add(0D);
            }
            Double value = MapUtils.getDouble(opsMap, String.valueOf(TimeUtil.fixEndTime(endTime)), 0D);
            reqColList.add(value);
        }
        List<Map<String, Object>> rowData = Lists.newArrayList();
        for (int i = 0; i < queryMehtodList.size(); i++){
            Map<String, Object> rowMap = Maps.newHashMap();
            rowMap.put("name", queryMehtodList.get(i));
            rowMap.put("requestNum", reqColList.get(i));
            //设置 charts
            Map<String, Object> chartsMap = Maps.newHashMap();
            chartsMap.put("requestData",MapUtils.getMap(opsDataMap,queryMehtodList.get(i), Maps.newHashMap()));
            rowMap.put("charts", chartsMap);
            rowData.add(rowMap);
        }
//        resultMap.put("ospData", opsDataMap);
        resultMap.put("pageCount", partList.size());
        resultMap.put("total", dataMehtodName.size());
        resultMap.put("rowData", rowData);
        return Response.init(resultMap, EcEm.SUCCESS);
    }

    /**
     * 查询调用方请求量或限流量
     */
    @RequestMapping("/getCallOps")
    @ResponseBody
    public Response getCallOps(HttpServletRequest request,
                               @RequestParam("serviceName") String serviceName,
                               @RequestParam("methodName") String methodName,
                               @RequestParam(value = "callName", required = false) String callName,
                               @RequestParam("startTime") String startTimeStamp,
                               @RequestParam("endTime") String endTimeStamp,
                               @RequestParam("currPage") int currPage,
                               @RequestParam("pageSize") int pageSize) {
        LogUtils.info(LOGGER, "getCallOps serviceName={0},methodName={1},callName={2},startTime={3},endTime={4},currPage={5},pageSize={6}", serviceName, methodName, callName, startTimeStamp, endTimeStamp, currPage, pageSize);
        Long startTime = TimeUtil.timeStampToLong(startTimeStamp);
        Long endTime = TimeUtil.timeStampToLong(endTimeStamp);
        Map resultMap = Maps.newHashMap();
        List<CallSourceInfo> allCallSourceInfoList = callSourceInfoService.getCallSourcesByGroupName(serviceName, callName);
        LogUtils.info(LOGGER, "getCallOps allCallSourceInfoList={0}", JsonUtils.toJSON(allCallSourceInfoList));
        if (CollectionUtils.isEmpty(allCallSourceInfoList)){
            return Response.init(resultMap, EcEm.SUCCESS);
        }
        List<List<CallSourceInfo>> partList = Lists.partition(allCallSourceInfoList, pageSize);
        if (currPage > pageSize){
            return Response.init(resultMap, EcEm.SUCCESS);
        }
        List<CallSourceInfo> dadaList = Lists.newArrayList(partList.get(currPage-1));
        List<String> callList = Lists.newArrayList();
        List<String> infoList = Lists.newArrayList();
        for (CallSourceInfo callSourceInfo : dadaList){
            callList.add(callSourceInfo.getCallSource());
            infoList.add(callSourceInfo.getDescInfo());
        }
        Map<String, Map<String, Double>> limitMap = hubbleUtils.queryLimitCountData(startTime, endTime, serviceName, methodName, callList);
//        resultMap.put("limit", limitMap);
        Map<String, Map<String, Double>> requestMap = hubbleUtils.queryCallSourceOpsData(startTime, endTime, serviceName, methodName, callList);
//        resultMap.put("request", requestMap);
        resultMap.put("pageCount", partList.size());
        resultMap.put("total", allCallSourceInfoList.size());
        List<Map<String, Object>> rowData = Lists.newArrayList();
        for (int i = 0; i < callList.size(); i++){
            Map<String, Object> rowMap = Maps.newHashMap();
            rowMap.put("name", callList.get(i));
            rowMap.put("info", infoList.get(i));
            //设置请求列
            Map<String, Double> reqTimeMap = requestMap.get(callList.get(i));
            if (MapUtils.isEmpty(reqTimeMap)){
                rowMap.put("requestNum", 0D);
            } else {
                rowMap.put("requestNum", MapUtils.getDouble(reqTimeMap, String.valueOf(TimeUtil.fixEndTime(endTime)), 0D));
            }
            //设置限流列
            Map<String, Double> limitTimeMap = limitMap.get(callList.get(i));
            if (MapUtils.isEmpty(limitMap)){
                rowMap.put("limitNum", 0D);
            } else {
                rowMap.put("limitNum", MapUtils.getDouble(limitTimeMap, String.valueOf(TimeUtil.fixEndTime(endTime)), 0D));
            }
            //设置 charts
            Map<String, Object> chartsMap = Maps.newHashMap();
            chartsMap.put("requestData",MapUtils.getMap(requestMap,callList.get(i), Maps.newHashMap()));
            chartsMap.put("limitData",MapUtils.getMap(limitMap,callList.get(i), Maps.newHashMap()));
            rowMap.put("charts", chartsMap);
            rowData.add(rowMap);
        }
        resultMap.put("rowData", rowData);
        return Response.init(resultMap, EcEm.SUCCESS);
    }

    /**
     * 查询配置列表
     */
    @RequestMapping("/getLimitConfig")
    @ResponseBody
    public Response getLimitConfig(HttpServletRequest request,
                                   @RequestParam("serviceName") String serviceName,
                                   @RequestParam("methodName") String methodName,
                                   @RequestParam("callName") String callName) {
        Map<String, LimitConfig> limitConfigMap = panguUtil.getLimitConfigByMethodName(serviceName,methodName);
        CallSourceInfo callSourceInfo = callSourceInfoService.getOneCallInfo(serviceName, callName);
        Map<String, Object> resultMap = Maps.newHashMap();
        if (callSourceInfo != null){
            resultMap.put("info", callSourceInfo.getDescInfo());
        }
        resultMap.putAll(JsonUtils.toMap(JsonUtils.toJSON(limitConfigMap.get(callName))));
        return Response.init(resultMap, EcEm.SUCCESS);
    }

    /**
     * 更新配置列表
     */
    @RequestMapping(value ="/updateLimitConfig", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Response updateLimitConfig(HttpServletRequest request,@RequestBody String json) {

        LimitConfig limitConfig = jsonToLimitConfig(json);
        String info = MapUtils.getString(JsonUtils.toMap(json), "info");
        callSourceInfoService.updateDescInfo(info,limitConfig.getServiceName(),limitConfig.getCallSource());
        String momoId = (String) request.getAttribute("momoId");
        //TODO 干掉
        if (StringUtils.isBlank(momoId)){
            momoId = "555488506";
        }
        LogUtils.info(LOGGER, "updateLimitConfig momoId={0},limitConfig={1}",momoId, JsonUtils.toJSON(limitConfig));
        if (StringUtils.isBlank(limitConfig.getCallSource())
                || StringUtils.isBlank(limitConfig.getMethodName())
                || StringUtils.isBlank(limitConfig.getServiceName())
                || limitConfig.getLimitSwitch() == null
                || limitConfig.getLimitNum() == null
                || StringUtils.isBlank(momoId)){ //参数校验
            return Response.init(EcEm.PARAM_ERROR);
        }
        return Response.init(panguUtil.saveOrUpdateConfig(limitConfig, momoId), EcEm.SUCCESS);
    }

    private LimitConfig jsonToLimitConfig(String json) {
        LimitConfig limitConfig = new LimitConfig();
        Map<String, Object> jsonMap = JsonUtils.toMap(json);
        limitConfig.setServiceName(MapUtils.getString(jsonMap, "serviceName"));
        limitConfig.setMethodName(MapUtils.getString(jsonMap, "methodName"));
        limitConfig.setLimitSwitch(MapUtils.getBoolean(jsonMap, "limitSwitch"));
        limitConfig.setLimitNum(MapUtils.getInteger(jsonMap, "limitNum"));
        limitConfig.setCallSource(MapUtils.getString(jsonMap, "callSource"));
        limitConfig.setCapacityLevel(MapUtils.getDouble(jsonMap, "capacityLevel"));
        return limitConfig;
    }

    /**
     * 查询配置列表
     */
    @RequestMapping("/isAccessLimit")
    @ResponseBody
    public Response isAccessLimit(HttpServletRequest request,
                                  @RequestParam("serviceName") String serviceName) {
        ServiceLimitInfo serviceLimitInfo = limitInfoMapper.getInfoByGroupName(serviceName);
        if (serviceLimitInfo != null && serviceLimitInfo.getLimitStatus().equals(1)) {
            return Response.init(true, EcEm.SUCCESS);
        }
        return Response.init(false, EcEm.SUCCESS);
    }

    /**
     * 服务接入通知
     */
    @RequestMapping("/accessLimitNotice")
    @ResponseBody
    public Response accessLimitNotice(HttpServletRequest request,
                            @RequestParam("serviceName") String serviceName) {
        String momoId = (String) request.getAttribute("momoId");
        LogUtils.info(LOGGER, "accessLimitNotice serviceName={0},momoId={1}", serviceName, momoId);
        ServiceLimitInfo serviceLimitInfo = new ServiceLimitInfo();
        serviceLimitInfo.setLimit_status(1);
        serviceLimitInfo.setGroupName(serviceName);
        serviceLimitInfo.setCreateTime(new Date());
        limitInfoMapper.save(serviceLimitInfo);
        panguUtil.firstAccessNotice(serviceName, momoId);
        return Response.init(EcEm.SUCCESS);
    }

    /**
     * 更新某个服务的调用方信息
     */
    @RequestMapping("/syncCallSourceInfo")
    @ResponseBody
    public Response syncCallSourceInfo(HttpServletRequest request, @RequestParam("serviceName") String serviceName) {
        LogUtils.info(LOGGER, "syncCallSourceInfo serviceName={0}", serviceName);
        updateTask.updateCallSourceInfoByServiceName(serviceName);
        return Response.init(true, EcEm.SUCCESS);
    }

    @RequestMapping(value ="/addLevelAlarm", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Response addLevelAlarm(HttpServletRequest request,@RequestBody String json) {
        String userName = (String) request.getAttribute("userName");
        String momoId = (String) request.getAttribute("momoId");
        LogUtils.info(LOGGER, "userName={0}, addLevelAlarm json={1}",userName, json);
        Map<String, Object> jsonMap = JsonUtils.toMap(json);
        String serviceName = MapUtils.getString(jsonMap, "serviceName");
        String methodName = MapUtils.getString(jsonMap, "methodName");
        String callName = MapUtils.getString(jsonMap, "callSource");
        String levelValue = MapUtils.getString(jsonMap, "level");
        if (StringUtils.isBlank(userName) ||
                StringUtils.isBlank(serviceName) ||
                StringUtils.isBlank(methodName) ||
                StringUtils.isBlank(callName) ||
                StringUtils.isBlank(momoId)||
                StringUtils.isBlank(levelValue)){
            return Response.init(EcEm.PARAM_ERROR);
        }
        AlarmUtils.addQpsAlarm(serviceName, methodName, userName);
        //修改配置中心
        Map<String, LimitConfig> limitConfigMap = panguUtil.getLimitConfigByMethodName(serviceName, methodName);
        if (MapUtils.isNotEmpty(limitConfigMap)){
            LimitConfig limitConfig = limitConfigMap.get(callName);
            if (limitConfig != null){
                limitConfig.setCapacityLevel(Double.valueOf(levelValue));
                panguUtil.saveOrUpdateConfig(limitConfig, momoId);
            }
        }
        Boolean result = AlarmUtils.addLevelAlarm(serviceName, methodName, userName, callName, levelValue);
        return Response.init(result, EcEm.SUCCESS);
    }
}
