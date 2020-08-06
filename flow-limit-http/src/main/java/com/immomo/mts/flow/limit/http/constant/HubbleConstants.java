package com.immomo.mts.flow.limit.http.constant;

/**
 * @author : tao.kaili
 * @description
 * @date : 2019/10/29 下午2:25
 */
public interface HubbleConstants {
    String POST_URL = "http://hubble-tsdb-api-v3.momo.com/api/query";
    String VERSION = "hubble3";
    String PERSON = "tao.kaili";
    String TYPE = "literal_or";
    String GROUPBY = "true";
    String AGGREGATOR = "zimsum";
    String DOWNSAMPLE = "1m-sum";

    String _AGGREGATE = "RAW";
    String GET_URL = "http://hubble-v3.momo.com/api/appKeys";
    String TOKEN = "29ae89bb21442b83";
    String PAGE_NO = "1";
    String PAGE_SIZE = "1";
    String MOA = "moa";
    String MOA_CALLSOURCE = "moa_callsource";
    String BUSINESS = "business";
    String M_HISTOGRAM_COUNT = "m_histogram_count";
    String M_LIMIT_COUNT = "m_limit_count";
    String INVOCATION = "invocation";

    String TAG_URL = "http://hubble-v3.momo.com/api/dashboard/default/queryIndicatorAndTag";

    String TAG_TOKEN = "f7ecaaf229107a7c";
    String CALL_SOURCE = "moa_callsource";

    String STRATEGY_URL = "http://hubble-v3.momo.com/api/alertStrategy/queryStrategyList";

    String QUERY_STRATEGY_TOKEN = "e568f8229c0fdbaa";

    String ADD_STRATEGY_URL = "http://hubble-v3.momo.com/api/alertStrategy/addStrategyThreshold";

    String ADD_STRATEGY_TOKEN = "a88d06ece4d4351e";

    String ALARM_SOURCE = "business";

    String LEVEL = "NOTICE";

    String OPERATOR = "update";

}
