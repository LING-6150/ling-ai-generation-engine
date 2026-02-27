package com.ling.lingaicodegeneration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.ling.lingaicodegeneration.mapper")
public class LingAiCodeGenerationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LingAiCodeGenerationApplication.class, args);
    }

}
