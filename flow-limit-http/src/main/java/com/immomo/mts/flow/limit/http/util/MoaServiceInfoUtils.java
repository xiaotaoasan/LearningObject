package com.immomo.mts.flow.limit.http.util;


import com.immomo.mcf.command.Command;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.LogWrapper;
import com.immomo.mcf.util.MapUtils;
import com.immomo.mcf.util.StringUtils;
import com.immomo.moa.api.Response;
import com.immomo.moa.remoting.redis.MOARedisClient;
import com.immomo.moa.util.CommandUtils;
import com.immomo.moaservice.moa.admin.enums.DataSourceEnum;
import com.immomo.mts.flow.limit.http.constant.MoaConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : tao.kaili
 * @description : 使用moa-admin的接口来查询appKey和所有的方法List集合
 * @date : 2019/10/29 下午6:52
 */
public class MoaServiceInfoUtils {

    public static final Logger APPLICATION = LogWrapper.getLogger("application");
    private static MOARedisClient moaAdminService;

    static {
        moaAdminService = MOARedisClient.getInstance(MoaConstants.MOA_SERVICEURI);
    }

    /**
     * 该方法主要是根据前端提供的uri来查询该服务的appKey
     *
     * @param serviceUri
     * @return
     */
    public static String getAppkeyFromUri(String serviceUri) {
        // 调用moa-admin的getAppKeyByServiceUri(serviceUri, DataSourceEnum.ONLINE_DATA_SOURCE);方法
        Command command = CommandUtils.buildCommand(MoaConstants.MOA_SERVICEURI, MoaConstants.GET_APPKEY_METHOD, serviceUri, DataSourceEnum.ONLINE_DATA_SOURCE);
        Response response = moaAdminService.execute(command);
        if (response != null && response.getErrorCode() == 0) {
            String appKey = (String) response.getResult();
            LogUtils.info(APPLICATION, "getAppkeyFromUri {0} appKey: {1}", serviceUri, appKey);
            return appKey;
        } else {
            return null;
        }

    }

    /**
     * 该方法主要用于根据前端提供的uri来查找该服务提供接口的所有的方法
     *
     * @param serviceUri
     * @return
     */

    public static List<String> getMethodListFromMoa(String serviceUri) {
        List<String> methodNameList = new ArrayList<String>();
        Command command = CommandUtils.buildCommand(MoaConstants.MOA_SERVICEURI, MoaConstants.GET_METHOD_LIST, serviceUri, DataSourceEnum.ONLINE_DATA_SOURCE);
        Response response = moaAdminService.execute(command);
        if (response != null && response.getErrorCode() == 0) {

            Map<String, Object> responseResult = (Map<String, Object>) response.getResult();
            if (MapUtils.isEmpty(responseResult)){
                return methodNameList;
            }
            List list = (List) responseResult.get("methodDefinitions");
            if (CollectionUtils.isEmpty(list)){
                return methodNameList;
            }
            for (Object obj : list) {
                Map<String, String> tmpMap = (Map<String, String>) obj;
                String methodName = MapUtils.getString(tmpMap, "methodName");
                if (StringUtils.isNotBlank(methodName)) {
                    methodNameList.add(methodName);
                }
            }
        }
        return methodNameList;
    }
}
