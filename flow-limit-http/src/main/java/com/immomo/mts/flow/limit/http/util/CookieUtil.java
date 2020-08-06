package com.immomo.mts.flow.limit.http.util;

import com.alibaba.fastjson.JSONObject;
import com.immomo.mcf.util.LogUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;


public class CookieUtil {


    private static String seed = "924cf4c7f3e01aa2427c1d4df0c743d2";

    public static final Logger LOG = LogFactory.getAPPLICATION();

    public static String encodeToken(String token) {
        try {
            Long timestamp = System.currentTimeMillis();
            String sign = new String(DigestUtils.md5(token + timestamp + seed));
            JSONObject json = new JSONObject();
            json.put("token", token);
            json.put("timestamp", timestamp);
            json.put("sign", sign);
            String res = Base64Utils.encodeToString(json.toJSONString().getBytes("utf-8"));
            return res;
        } catch (UnsupportedEncodingException e) {
            LogUtils.error(LOG,e, "CookieUtil encodeToken, encode error");
        }
        return null;
    }

    public static String decodeToken(String cookie) {
        String tokenCookie = new String(Base64Utils.decodeFromString(cookie));
        JSONObject tokenJson = JSONObject.parseObject(tokenCookie);
        String token = tokenJson.getString("token");
        String timestamp = tokenJson.getString("timestamp");
        String sign = tokenJson.getString("sign");

        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(sign)) {
            return null;
        }

        if (!sign.equals(new String(DigestUtils.md5(token + timestamp + seed)))){
            return null;
        }
        return token;
    }

    public static void setToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge(3600 * 24);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
