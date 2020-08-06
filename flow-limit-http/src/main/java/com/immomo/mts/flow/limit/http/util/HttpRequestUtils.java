package com.immomo.mts.flow.limit.http.util;

import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.LogWrapper;
import com.immomo.mcf.util.MapUtils;
import com.immomo.msc.http.HttpClient;
import com.immomo.msc.http.builder.FormPostBuilder;
import com.immomo.msc.http.builder.GetBuilder;
import com.immomo.msc.http.builder.JsonPostBuilder;
import com.immomo.mts.flow.limit.http.constant.MoaConstants;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author : tao.kaili
 * @description : 该类主要用于执行http请求。
 * @date : 2019/10/30 下午7:13
 */
public class HttpRequestUtils {

    public static final Logger APPLICATION = LogWrapper.getLogger("application");
    private static HttpClient httpClient;

    /**
     * 初始化加载httpClient
     */

    static {

        httpClient = HttpClient.newBuilder(MoaConstants.BIZ_NAME)
                .connTimeOut(15000)
                .readTimeOut(15000)
                .writeTimeOut(10000)
                .build();
    }

    /**
     * http 发送post请求的处理方式
     *
     * @param url
     * @param paramMap
     * @return
     */
    public static String postReq(String url, Map<String, Object> paramMap) {
        JsonPostBuilder jsonPostBuilder = httpClient.jsonPost().url(url);
        jsonPostBuilder.content(JsonUtils.toJSON(paramMap));
        String result = "";
        try {
            result = jsonPostBuilder.build().execute().body().string();
        } catch (Exception e) {
            LogUtils.error(APPLICATION, "IOException {0}", e);
        }

        return result;
    }


    public static String get(String url, Map<String, String> paramMap, Map<String, String> headerMap) {

        GetBuilder getBuilder = httpClient.get().url(url);
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Map.Entry<String, String> paramEntry : paramMap.entrySet()) {
                getBuilder.addQueryParam(paramEntry.getKey(), paramEntry.getValue());
            }
        }
        if (MapUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                getBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        String result = "";
        try {
            result = getBuilder.build().execute().body().string();
        } catch (Exception e) {
            LogUtils.error(APPLICATION, e, "HttpRequestUtils get error");
        }
        return result;
    }
    public static String post(String url, Map<String, String> paramMap, Map<String, String> headerMap) {

        FormPostBuilder postBuilder = httpClient.basicPost().url(url);
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Map.Entry<String, String> paramEntry : paramMap.entrySet()) {
                postBuilder.addFieldParam(paramEntry.getKey(), paramEntry.getValue());
            }
        }
        postBuilder.addFieldParam("default", "default");
        if (MapUtils.isNotEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                postBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        String result = "";
        try {
            result = postBuilder.build().execute().body().string();
        } catch (Exception e) {
            LogUtils.error(APPLICATION, e, "HttpRequestUtils post error");
        }
        return result;
    }
}
