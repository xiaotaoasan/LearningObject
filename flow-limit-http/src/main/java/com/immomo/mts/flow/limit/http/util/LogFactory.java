package com.immomo.mts.flow.limit.http.util;

import com.immomo.mcf.util.LogWrapper;
import org.apache.log4j.Logger;

public class LogFactory {

    public static final Logger APPLICATION = LogWrapper.getLogger("application");

    public static Logger getAPPLICATION() {
        return APPLICATION;
    }
}
