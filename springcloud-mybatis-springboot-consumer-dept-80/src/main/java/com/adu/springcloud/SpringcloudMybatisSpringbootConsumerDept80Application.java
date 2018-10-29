package com.adu.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpringcloudMybatisSpringbootConsumerDept80Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringcloudMybatisSpringbootConsumerDept80Application.class, args);
	}
}
