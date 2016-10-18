package com.renmaituan.shop.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.renmaituan.shop")
public class FeignConfiguration {

}
