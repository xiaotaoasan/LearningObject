package com.immomo.mts.flow.limit.http.constant;

import java.util.Map;

public enum ENV {

    ONLINE(),
    DEV();

    public static ENV get() {
        Map<String, String> env = System.getenv();

        if ("dev".equals(env.get("MOMO_ENV"))){
            return DEV;
        } else {
            return ONLINE;
        }
    }

}
