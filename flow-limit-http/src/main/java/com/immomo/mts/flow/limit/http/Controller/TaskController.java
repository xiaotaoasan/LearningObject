package com.immomo.mts.flow.limit.http.Controller;

import com.immomo.mcf.util.LogUtils;
import com.immomo.mts.flow.limit.http.domain.EcEm;
import com.immomo.mts.flow.limit.http.domain.Response;
import com.immomo.mts.flow.limit.http.task.CallSourceInfoUpdateTask;
import com.immomo.mts.flow.limit.http.task.ServiceInfoUpdateTask;
import com.immomo.mts.flow.limit.http.util.LogFactory;
import com.immomo.mts.flow.limit.http.util.R;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : tao.kaili
 * @description :定时更新表里面的数据
 * @date : 2019/11/13 下午6:33
 */

@Controller
@RequestMapping(value = "/task")
public class TaskController {

    @Autowired
    private CallSourceInfoUpdateTask callSourceInfoUpdateTask;

    @Autowired
    private ServiceInfoUpdateTask serviceInfoUpdateTask;

    public static final Logger LOGGER = LogFactory.getAPPLICATION();

    @RequestMapping("/updateCallSourceInfo")
    @ResponseBody
    public R updateCallSourceInfo(HttpServletRequest request) {
        String developer = "aa";
        callSourceInfoUpdateTask.updateCallSourceInfo( );
        return R.ok();
    }

    @RequestMapping("/updateServiceInfo")
    @ResponseBody
    public R updateServiceInfo(HttpServletRequest request) {

        serviceInfoUpdateTask.updateServiceInfo();
        return R.ok();
    }

    /**
     * 检查是否登录
     * @param request
     * @return
     */
    @RequestMapping("/test")
    @ResponseBody
    public Response test(HttpServletRequest request) {
        LogUtils.info(LOGGER, "test momoId={0},userName={1}", request.getAttribute("momoId"), request.getAttribute("userName"));
        return Response.init(EcEm.SUCCESS);
    }

}
