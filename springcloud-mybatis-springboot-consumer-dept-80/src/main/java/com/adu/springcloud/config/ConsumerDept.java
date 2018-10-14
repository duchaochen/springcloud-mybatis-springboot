package com.adu.springcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConsumerDept {

    /**
     * 后端调用http请求类
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
