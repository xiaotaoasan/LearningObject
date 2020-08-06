package com.immomo.mts.flow.limit.http.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.MapUtils;
import com.immomo.mcf.util.StringUtils;
import com.immomo.mts.flow.limit.http.domain.EcEm;
import com.immomo.mts.flow.limit.http.domain.Response;
import com.immomo.mts.flow.limit.http.util.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * 权限拦截
 * 1.
 * 2.
 */
@Component("loginInterceptor")
public class LoginInterceptor implements HandlerInterceptor {

    public static final Logger LOGGER = LogFactory.getAPPLICATION();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LogUtils.info(LOGGER, "preHandle url:{0}", request.getRequestURI());
        //TOOD 修改不需要进行Aegis校验的URL
        if (request.getRequestURI().startsWith("/info") || request.getRequestURI().startsWith("/error")){
            LogUtils.info(LOGGER, "not need login . url :{0}", request.getRequestURI());
            return true;
        }

        String cookieToken = getTokenString(request);
        String paramToken = request.getParameter("token");
        LogUtils.info(LOGGER, "LoginInterceptor cookieToken={0},paramToken={1}",cookieToken,paramToken);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            writeJson(response);
            return false;
        }

        String token;
        if (StringUtils.isEmpty(cookieToken)) {       //cookie token empty
            token = paramToken;
        } else {                                    //param token empty
            token = cookieToken;
        }

        if (!checkLoginAndSetRequest(token, request)) {
            writeJson(response);
            return false;
        }
        return true;
    }

    private void writeJson(HttpServletResponse resp){
        PrintWriter out = null;
        try {
            //设定类容为json的格式
            resp.setContentType("application/json;charset=UTF-8");
            out = resp.getWriter();
            //写到客户端
            out.write(JsonUtils.toJSON(Response.init(SsoConfig.getKey(),EcEm.NO_PERMISSION)));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(out != null){
                out.close();
            }
        }
    }

    private void responseResult(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
//        ServletOutputStream out = null;
        PrintWriter writer=null;
        try {
            writer=response.getWriter();
            writer.write(JsonUtils.toJSON(Response.init(EcEm.NO_PERMISSION)));
            writer.flush();
        } catch (IOException e) {
            LogUtils.error(LOGGER, e, "responseResult error");
        }finally {
            if(writer!=null) {
                writer.close();
            }
        }
    }

    private void redirectToLogin(HttpServletResponse response, String sessionId) {
        try {
            response.sendRedirect(SsoConfig.getRedirectUrl() + SsoConfig.getKey()+"?sessionId="+sessionId);
        } catch (IOException e) {
            LogUtils.error(LOGGER, e, "err in redirectToLogin {0}", JsonUtils.toJSON(response));
        }
    }

    private void redirectToLogin(HttpServletResponse response) {
        try {
            response.sendRedirect(SsoConfig.getRedirectUrl() + SsoConfig.getKey());
        } catch (IOException e) {
            LogUtils.error(LOGGER, e, "err in redirectToLogin {0}", JsonUtils.toJSON(response));
        }
    }

    private String getTokenString(HttpServletRequest request) {
        Cookie cookieToken = getToken(request);
        if (cookieToken == null) {
            return null;
        }

        return CookieUtil.decodeToken(cookieToken.getValue());
    }

    private Cookie getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    private boolean checkLoginAndSetRequest(String token, HttpServletRequest request) {
        LogUtils.info(LOGGER, "check token : {0}", token);
        try {
            JSONObject jsonObject = new JSONObject()
                    .fluentPut("key", SsoConfig.getKey())
                    .fluentPut("secret", SsoConfig.getSecret())
                    .fluentPut("token", token);
            String resp = HttpUtil.post("http://aegis.momo.com/sso/check", jsonObject);
            LogUtils.info(LOGGER, "checkLoginAndSetRequest resp={0}", resp);
            Map<String, Object> jsonMap = JsonUtils.toMap(resp);
            Map<String, Object> userMap = MapUtils.getMap(jsonMap, "user");
            //请求域设置momoid和userName
            request.setAttribute("momoId", MapUtils.getString(userMap, "momoid"));
            request.setAttribute("userName", StringUtils.split(MapUtils.getString(userMap, "email"), "@")[0]);
            LogUtils.info(LOGGER, "checkLoginAndSetRequest finish!!!");
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, e,  "check token|execute error ! token:{0}", token);
            return false;
        }

    }

    private boolean checkLoginAndSetRequestPro(String token, HttpServletRequest request) {
        LogUtils.info(LOGGER, "check token : {0}", token);
        try {
            JSONObject jsonObject = new JSONObject()
                    .fluentPut("key", SsoConfig.getKey())
                    .fluentPut("secret", SsoConfig.getSecret())
                    .fluentPut("token", token);
            String resp = HttpUtil.post("http://aegis.momo.com/sso/check", jsonObject);
            LogUtils.info(LOGGER, "checkLoginAndSetRequest resp={0}", resp);
            Map<String, Object> jsonMap = JsonUtils.toMap(resp);
            Map<String, Object> userMap = MapUtils.getMap(jsonMap, "user");
            //请求域设置momoid和userName
            request.setAttribute("momoId", MapUtils.getString(userMap, "momoid"));
            request.setAttribute("userName", StringUtils.split(MapUtils.getString(userMap, "email"), "@")[0]);

            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, e,  "check token|execute error ! token:{0}", token);
            return false;
        }

    }

}
