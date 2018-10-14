package com.adu.springcloud.dao;

import com.adu.springcloud.entitys.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 这个注解一定要写
 */
@Mapper
public interface DeptDao {

    boolean addDept(Dept dept);

    Dept findById(Long id);

    List<Dept> findAll();

}
