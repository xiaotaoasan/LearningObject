package com.immomo.mts.flow.limit.http.util;

import com.immomo.mcf.dao.redis.IRedisDao;
import com.immomo.mcf.util.LogUtils;
import com.immomo.momostore.proxy.IStoreDao;
import com.immomo.momostore.proxy.StoreDaoFactory;
import org.apache.log4j.Logger;

/**
 * redis分布式锁
 * <p>
 * Created by whc on 18/8/15.
 */
public class RedisDistributedLockUtils {

    public static final Logger LOGGER = LogFactory.getAPPLICATION();
    private static IStoreDao redisStoreDao = StoreDaoFactory.createStoreDao("momostore_redis_node_6570");

    /**
     * 上锁
     *
     * @param key
     * @param value
     * @param expireTime 单位：秒
     * @return
     */
    public static boolean lock(String key, String value, Long expireTime) {
        boolean flag = false;
        try {
            String result = redisStoreDao.setpx("1", key, value, IRedisDao.NXXX.NX, expireTime * 1000);
            flag = "OK".equals(result);
        } catch (Exception e) {
            LogUtils.error(LOGGER, e, "lock error|{0}|{1}|{2}", key, value, expireTime);
        }
        LogUtils.info(LOGGER, "lock:{0}|{1}s result={2}", key, expireTime, flag);
        return flag;
    }

    /**
     * 释放锁
     * @param key
     */
    public static void releaseLock(String key) {
        try {
            redisStoreDao.del("1", key);
        } catch (Exception e) {
            LogUtils.error(LOGGER, e, "release lock error|{0}|{1}|{2}", key);
        }
    }


}
