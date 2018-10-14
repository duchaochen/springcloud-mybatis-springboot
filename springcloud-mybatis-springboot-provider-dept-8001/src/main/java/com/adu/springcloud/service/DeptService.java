package com.adu.springcloud.service;

import com.adu.springcloud.entitys.Dept;

import java.util.List;

public interface DeptService {

    boolean add(Dept dept);

    Dept get(Long id);

    List<Dept> list();
}
