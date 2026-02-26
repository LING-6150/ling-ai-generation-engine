package com.ling.lingaicodegeneration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class LingAiCodeGenerationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LingAiCodeGenerationApplication.class, args);
    }

}
