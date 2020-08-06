package com.immomo.mts.flow.limit.http.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.immomo.mcf.command.Command;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.StringUtils;
import com.immomo.moa.remoting.redis.MOARedisClient;
import com.immomo.moaservice.mtssuite.config.ConfigFactory;
import com.immomo.moaservice.mtssuite.config.VariableParamStrategy;
import com.immomo.mts.flow.limit.http.domain.MoaInfo;
import com.immomo.mts.flow.limit.http.domain.ServiceInfo;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class MoaHelper {

    private VariableParamStrategy whiteList = ConfigFactory.createVariableParamStrategy("/moa/seal/whiteList");

    private List<String> trueWhiteList;

    public static final Logger LOG = LogFactory.getAPPLICATION();

    @Autowired
    private ServiceInfoService authService;

    public List<String> queryServiceUriByName(String userName) {
        //超级用户所有
        if (whiteList.get().contains(userName)){
            return queryAllServiceUriFromMysql();
        }

        //普通用户
        return queryServiceUriByNameFromMysql(userName);
    }

    private List<String> queryServiceUriByNameFromMoa(String userName) {

        List<String> userServiceUri = new ArrayList<String>();
        List<Map<String, Object>> result = (List<Map<String, Object>>) MOARedisClient.getInstance("/service/moa-admin")
                .execute(new Command("/service/moa-admin", "queryAllServiceOwner"))
                .getResult();
        for (Map<String, Object> moaService : result) {
            List<String> developers = (List<String>) moaService.get("developers");
            String service = (String) moaService.get("service");
            if (CollectionUtils.isEmpty(developers) || whiteList.get().contains(userName)) {
                userServiceUri.add(service);
                continue;
            }
            if (developers.contains(userName)) {
                userServiceUri.add(service);
            }
        }

        return userServiceUri;
    }

    public List<String> queryMethodByServiceUri(String serviceUri) {
        List<String> methods = new ArrayList<String>();
        List<String> result = (List<String>) MOARedisClient.getInstance("/service/lookup")
                .execute("getServiceInfo", serviceUri)
                .getResult();

        if (CollectionUtils.isEmpty(result)) {
            return methods;
        }

        for (String method : result) {
            int subIndex = method.indexOf(" ");
            String methodName = method.substring(subIndex);
            subIndex = methodName.indexOf("(");
            methodName = methodName.substring(0, subIndex).trim();
            methods.add(methodName);
        }

        return methods;
    }

    /**
     * 取所有serviceUri及成员信息
     * @return
     */
    static class AppKey {
        int id;

        String appkey;

        String leader;

        String firstAlertUser;

        String[] alertUsers;

        public void setId(int id) {
            this.id = id;
        }

        public void setAppkey(String appkey) {
            this.appkey = appkey;
        }

        public void setLeader(String leader) {
            this.leader = leader;
        }

        public void setFirstAlertUser(String firstAlertUser) {
            this.firstAlertUser = firstAlertUser;
        }

        public void setAlertUsers(String[] alertUsers) {
            this.alertUsers = alertUsers;
        }

        public List<String> getDevelopers() {
            Set<String> result = new HashSet<>();
            if (alertUsers != null) {
                result.addAll(Arrays.asList(alertUsers));
            }
            if (firstAlertUser != null) {
                result.add(firstAlertUser);
            }
            if (leader!= null) {
                result.add(leader);
            }
            return new ArrayList<>(result);
        }
    }

    private JSONArray loadServiceUriData() {
        String cmdbUrl = StringUtils.defaultString(System.getenv("CMDB_URL"), "cmdb.momo.com");
        String cmdbToken = StringUtils.defaultString(System.getenv("CMDB_TOKEN"),
                "Token 7f2442eca80d981f230d7e91b42b5cab16f471d9");

        JSONArray keys = new JSONArray();
        int page = 0;
        final int pageSize = 50;
        try {
            while(true) {
                Map<String, String> headerMap = new HashMap<String, String>(){{
                    put("Authorization", cmdbToken);
                }};
                String result = HttpRequestUtils.get(
                        String.format("http://%s/open/getappkeybyserviceuri/?verbose=1&page_size=%d&page=%d", cmdbUrl, pageSize, ++page),
                        null,
                        headerMap);
                if (result == null) {
                    LogUtils.warn(LOG, "loadServiceUriData . get appkey error . result is null");
                    break;
                }
                try {
                    JSONObject json = JSONObject.parseObject(result);
                    keys.addAll(json.getJSONArray("results"));
                    if (json.getInteger("count") <= keys.size()) {
                        break;
                    }
                } catch (JSONException e) {
                    LogUtils.warn(LOG,
                            "syncMoaInfo . getappkeybyserviceuri result data format error: {0}", result);
                    break;
                }
            }
        } catch (Exception e) {
            LogUtils.error(LOG, e, "syncMoaInfo . error when query data from cmdb");
        }
        LogUtils.warn(LOG, "syncMoaInfo . loadServiceUri page: {0}, size: {1}", page, keys.size());
        return keys;
    }

    public List<MoaInfo> queryAllServiceUri() {
        JSONArray keys = loadServiceUriData();
        if (CollectionUtils.isEmpty(keys)) {
            LogUtils.error(LOG, "syncMoaInfo . query serviceuri is empty");
            return Collections.EMPTY_LIST;
        }

        Set<MoaInfo> moaInfos = new HashSet<>();

        for (Object keyJson : keys) {
            if (!(keyJson instanceof JSONArray)) {
                LogUtils.warn(LOG, "syncMoaInfo . appkey data format error . {0}", JSON.toJSONString(keyJson));
                continue;
            }
            JSONArray key = ((JSONArray) keyJson);
            if (key.size() != 2 || key.get(1) == null) {
                continue;
            }
            String serviceUri = key.getString(0);

            AppKey appKey = JSONObject.parseObject(key.getString(1), AppKey.class);

            MoaInfo moaInfo = new MoaInfo();
            moaInfo.setOwner(appKey.leader);
            moaInfo.setDevelopers(appKey.getDevelopers());
            moaInfo.setServiceUri(serviceUri);
            moaInfo.setAppKey(appKey.appkey);
            moaInfo.setAppKeyId(String.valueOf(appKey.id));
            moaInfos.add(moaInfo);
        }

        LogUtils.info(LOG, "syncMoaInfo . load all serviceuri . size:{0}", moaInfos.size());
        return new ArrayList<>(moaInfos);
    }

    //check auth new use mysql check
    private List<String> queryServiceUriByNameFromMysql(String userName) {
        List<ServiceInfo> authInfos = authService.getByDeveloper(userName);
        if (CollectionUtils.isEmpty(authInfos)) {
            return new ArrayList<String>();
        }

        List<String> result = new ArrayList<String>();
        for (ServiceInfo authInfo : authInfos) {
            result.add(authInfo.getGroupName());
        }

        return result;
    }


    private List<String> queryAllServiceUriFromMysql(){
        return authService.getAllGroupName();
    }


    public List<String> searchByUserNameAndGroupName(String userName, String groupName) {
        if (whiteList.get().contains(userName)){
            return queryAllServiceUriFromMysql();
        }
        return authService.searchByUserNameAndGroupName(userName, groupName);
    }

    @PostConstruct
    public void initWhiteList() {
        try {
            LogUtils.info(LOG, "whiteList = {0}", whiteList.get());
            trueWhiteList = Arrays.asList(whiteList.get().split(","));
        } catch (Exception e) {

            LogUtils.error(LOG, e, "initWhiteList failure.");
        }
    }

    public boolean isAdmin(String name) {
        return whiteList.get().contains(name);
    }

    public List<String> getWhiteList() {
        return trueWhiteList;
    }

    public String getAdmin() {
        return whiteList.get();
    }
}
