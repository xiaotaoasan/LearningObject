package com.immomo.mts.flow.limit.http.util;

/**
 * @author xing.xiantao.
 * @date 2019/11/16.
 */
public class TimeUtil {

    public static Long timeStampToLong(String timeStamp){
        if(timeStamp != null && timeStamp.length() == 10){
            return Long.parseLong( timeStamp )*1000;
        }else if(timeStamp != null &&  timeStamp.length() == 13 ){
            return Long.parseLong( timeStamp );
        }
        return null;
    }

    public static Long fixEndTime(Long endTime){
        return new Long((endTime - 4 * 60 * 1000L) / 60000L*60L);
    }

    /**
     * 最近一分钟的秒级时间戳
     * @param time
     * @return
     */
    public static Long fixTime(Long time){
        return new Long(time  / 60000L*60L);
    }
}
