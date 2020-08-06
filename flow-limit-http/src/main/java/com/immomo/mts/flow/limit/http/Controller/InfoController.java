package com.immomo.mts.flow.limit.http.Controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mcf.util.LogUtils;
import com.immomo.mcf.util.MapUtils;
import com.immomo.mcf.util.StringUtils;
import com.immomo.mts.flow.limit.http.domain.EcEm;
import com.immomo.mts.flow.limit.http.domain.Response;
import com.immomo.mts.flow.limit.http.mapper.CallSourceInfoMapper;
import com.immomo.mts.flow.limit.http.mapper.ServiceInfoMapper;
import com.immomo.mts.flow.limit.http.mapper.ServiceLimitInfoMapper;
import com.immomo.mts.flow.limit.http.service.CallSourceInfoService;
import com.immomo.mts.flow.limit.http.service.ServiceInfoService;
import com.immomo.mts.flow.limit.http.task.CallSourceInfoUpdateTask;
import com.immomo.mts.flow.limit.http.task.ServiceInfoUpdateTask;
import com.immomo.mts.flow.limit.http.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author xing.xiantao.
 * @date 2019/10/28.
 */

@Controller
@RequestMapping(value = "/info")
public class InfoController {

    @Autowired
    private ServiceInfoService serviceInfoService;

    @Autowired
    private ServiceInfoMapper serviceInfoMapper;

    @Autowired
    private ServiceLimitInfoMapper limitInfoMapper;

    @Autowired
    ServiceInfoUpdateTask serviceInfoUpdateTask;

    @Autowired
    CallSourceInfoUpdateTask callSourceInfoUpdateTask;

    @Autowired
    private CallSourceInfoService callSourceInfoService;

    @Autowired
    private CallSourceInfoMapper callSourceInfoMapper;

    @Autowired
    HubbleUtils hubbleUtils;

    public static final Logger LOGGER = LogFactory.getAPPLICATION();

    /**
     * 删除测试数据
     *
     * @return
     */
    @RequestMapping("/delLimit")
    @ResponseBody
    public Response delLimit(HttpServletRequest request, @RequestParam("serviceName") String serviceName) {
        LogUtils.info(LOGGER, "delLimit serviceName={0}", serviceName);
        limitInfoMapper.delete(serviceName);
        return Response.init(EcEm.SUCCESS);
    }


    /**
     * 检查是否登录
     * @param request
     * @return
     */
    @RequestMapping("/checkLogin")
    @ResponseBody
    public Response checkLogin(HttpServletRequest request) {
        String cookieToken = getTokenString(request);
        LogUtils.info(LOGGER, "checkLogin cookieToken={0}", cookieToken);
        if (StringUtils.isBlank(cookieToken)){
            return Response.init(SsoConfig.getKey(), EcEm.NO_PERMISSION);
        }
        if (checkLoginAndSetRequest(cookieToken, request)){
            return Response.init(SsoConfig.getKey(), EcEm.SUCCESS);
        }
        return Response.init(SsoConfig.getKey(), EcEm.NO_PERMISSION);
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

            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, e,  "check token|execute error ! token:{0}", token);
            return false;
        }

    }

    private String getTokenString(HttpServletRequest request) {
        try {
            Cookie cookieToken = getToken(request);
            if (cookieToken == null) {
                return null;
            }

            return CookieUtil.decodeToken(cookieToken.getValue());
        }catch (Exception e){
            return null;
        }
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

    /**
     * 查询调用方限流量
     *
     * @param request
     * @return
     */
    @RequestMapping("/callback")
    @ResponseBody
    public Response callback(HttpServletRequest request, HttpServletResponse response, @RequestParam("token") String token, @RequestParam(value = "cb_redirect_uri", required = false) String url) {

        LogUtils.info(LOGGER, "callback token:{0},url:{1}", token, url);
        if (StringUtils.isEmpty(token)) {
            LogUtils.error(LOGGER, "AuthController, aegis returns null to callback uri");
        } else {
            //种cookie
            CookieUtil.setToken(response, CookieUtil.encodeToken(token));
            LogUtils.info(LOGGER, "AuthController, token = {0}", token);
        }
        if (StringUtils.isNotBlank(url)){
            try {
                //重定向
                response.sendRedirect(url);
            } catch (Exception e){
                LogUtils.error(LOGGER, e, "callback url={0}", url);
            }
        }
        return Response.init(EcEm.SUCCESS);
    }
}
