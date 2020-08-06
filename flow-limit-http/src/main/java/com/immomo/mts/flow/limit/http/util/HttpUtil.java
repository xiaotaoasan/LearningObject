package com.immomo.mts.flow.limit.http.util;

import com.alibaba.fastjson.JSONObject;
import com.immomo.mcf.util.LogUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * 专用于存储cookie
 */

public class HttpUtil {

    public static final Logger LOGGER = LogFactory.getAPPLICATION();

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3000)
            .setConnectTimeout(3000)
            .setConnectionRequestTimeout(3000)
            .build();

    public static String post(String url, JSONObject params) {

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
                .register("easy", new EasySpecProvider())
                .build();

        CookieStore cookieStore = new BasicCookieStore();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec("easy")
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setDefaultCookieSpecRegistry(r)
                .setDefaultRequestConfig(requestConfig)
                .build();

        try {
            StringEntity entity = new StringEntity(params.toJSONString(), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            CloseableHttpResponse result = httpClient.execute(httpPost);
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(result.getEntity(), "utf-8");
            } else {
                LogUtils.info(LOGGER, "HttpUtil post url={0}, param={1} error", url, params.toJSONString());
            }
        } catch (Exception e) {
            LogUtils.error(LOGGER, e,"HttpUtil post url={0}, param={1} error", url, params.toJSONString());
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }



    static class EasyCookieSpec extends DefaultCookieSpec {
        @Override
        public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {

        }
    }

    static class EasySpecProvider implements CookieSpecProvider {
        @Override
        public CookieSpec create(HttpContext httpContext) {
            return new EasyCookieSpec();
        }
    }
}
