package com.adu.springcloud.entitys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @AllArgsConstructor：全参的构造方法
 * @NoArgsConstructor：无参的构造方法
 * @Data:get和set方法
 * @Accessors(chain = true)使用链式编程
 *  Dept d = new Dept();
 * d.setDb_source("ddd").setDeptno(1L).setDname("555");
 */
@SuppressWarnings("all")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class Dept implements Serializable {
    private Long 	deptno; // 主键
    private String 	dname; // 部门名称
    private String 	db_source;// 来自那个数据库，因为微服务架构可以一个服务对应一个数据库，同一个信息被存储到不同数据库

}
