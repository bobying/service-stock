package com.newdun.cloud.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.newdun.cloud")
public class FeignConfiguration {

}
