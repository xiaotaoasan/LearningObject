package com.immomo.mts.flow.limit.http.util;


import com.immomo.mts.flow.limit.http.constant.ENV;

/**
 * 单点登陆配置
 */
public class SsoConfig {

    public static String getKey() {
        return getByEnv(ENV.get()).getKey();
    }

    public static String getSecret() {
        return getByEnv(ENV.get()).getSecret();
    }

    public static String getRedirectUrl() {
        return getByEnv(ENV.get()).getRedirectUrl();
    }

    public static String getHost() {
        return getByEnv(ENV.get()).getHost();
    }

    public static Config getByEnv(ENV env) {
        switch (env) {
            case ONLINE: return Config.ONLINE;
            case DEV: return Config.DEV;
            default: return Config.ONLINE;
        }
    }

    public enum Config {

        ONLINE("bbdd5fae-3569-4940-ad1b-1eceaedab740", "fbe89d12-889d-4429-9644-1ea191ab26fa", "https://aegis.immomo.com/sso/login/", "http://aegis.momo.com"),
        DEV("fd760083-1f94-4efa-8a77-05d73ff231dc", "3cc61e73-aaae-441b-adbe-97e4d5e2e579", "https://aegis.immomo.com/sso/login/", "http://aegis.momo.com");

        private String key;
        private String secret;
        private String redirectUrl;
        private String host;

        Config(String key, String secret, String redirectUrl, String host) {
            this.key = key;
            this.secret = secret;
            this.redirectUrl = redirectUrl;
            this.host = host;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "key='" + key + '\'' +
                    ", secret='" + secret + '\'' +
                    ", redirectUrl='" + redirectUrl + '\'' +
                    ", host='" + host + '\'' +
                    '}';
        }
    }

}
