package com.adu.springcloud.controller;

import com.adu.springcloud.entitys.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class DeptConsumerController {

//    private final String HTTP_URL = "http://localhost:8001";
    private final String HTTP_URL = "http://LIS-8001";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/dept/add")
    public boolean add(Dept dept) {

        /**
         * 使用的post方式访问
         *  url : 访问地址
         *  request : post提交的参数
         *  responseType : 返回的类型
         */
        Boolean aBoolean = restTemplate.postForObject(HTTP_URL + "/dept/add", dept, Boolean.class);
        return aBoolean;
    }

    @GetMapping("/dept/get/{id}")
    public Dept get(@PathVariable Long id) {
        return restTemplate.getForObject(HTTP_URL + "/dept/get/" + id,Dept.class);
    }

    @GetMapping("/dept/list")
    public List<Dept> list() {
        return restTemplate.getForObject(HTTP_URL + "/dept/list",List.class);
    }
}
