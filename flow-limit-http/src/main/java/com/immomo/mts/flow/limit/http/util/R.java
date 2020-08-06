package com.immomo.mts.flow.limit.http.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xing.xiantao.
 * @date 2019/10/30.
 */
public class R extends HashMap<String, Object> {

    private static final long serialVersionUID = -5256159546214919637L;

    public R() {
        put("code", 200);
    }

    public static R error() {
        return error(500, "服务器异常");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("data", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("data", msg);
        return r;
    }

    public static R ok(Object o) {
        R r = new R();
        r.put("data", o);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
