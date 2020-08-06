package com.immomo.mts.flow.limit.http;

import com.google.common.collect.Lists;
import com.immomo.mcf.util.JsonUtils;
import com.immomo.mts.flow.limit.http.util.HubbleUtils;

/**
 * @author xing.xiantao.
 * @date 2019/10/28.
 */
public class Test {

    public static String testQueryBatchMethod() {

        // String startTime = "1572397386000";
        long startTime = 1571965386000L;
        //半个小时
        long endTime = 1572399186000L;
        //一个小时
        //      String endTime = "1572400986000";
        //一天
        //       String endTime = "1572483786000";
        //7天
        // String endTime="1572397386000";


        String action = "/service/user-relation";

        return null;
    }

    public static void main(String[] args) {
        System.out.println(testQueryBatchMethod());
    }
}
