package com.immomo.mts.flow.limit.http;

import com.immomo.mcf.util.LogUtils;
import com.immomo.mts.flow.limit.http.util.LogFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@EnableConfigurationProperties
@ServletComponentScan
@MapperScan("com.immomo.mts.flow.limit.http.mapper")
// @EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("start i ok");
        LogUtils.info(LogFactory.APPLICATION, "flow-limit-http init");
    }

}