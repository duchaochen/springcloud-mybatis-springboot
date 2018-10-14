# springcloud+springboot+mybatis

### lombok使用

    lombok解决免写getset方法、构造方法、tostring方法
       1.引入lombok引用
         <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
            
      2.实体类代码如下:
        /**
         * @AllArgsConstructor：全参的构造方法
         * @NoArgsConstructor：无参的构造方法
         * @Data:get和set方法
         * @Accessors(chain = true)使用链式编程
         */
        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        @Accessors(chain = true)
        public class Dept implements Serializable {
        
            private Long 	deptno; // 主键
            private String 	dname; // 部门名称
            private String 	db_source;// 来自那个数据库，因为微服务架构可以一个服务对应一个数据库，同一个信息被存储到不同数据库
        }
    
# 整合springboot+mybatis
    
    1.由于entity包下的所有的实体类会在其它工程中都会使用，所以我们创建一个单独的工程来保存实体类
    
    2.创建一个dept服务提供者工程,以及全局配置文件代码
            server:
              port: 8001                                                #默认启动端口
            
            mybatis:
              config-location: classpath:mybatis/mybatis.cfg.xml        # mybatis配置文件所在路径
              type-aliases-package: com.adu.springcloud.entitys         # 所有Entity别名类所在包(可以不要)
              mapper-locations:
              - classpath:mybatis/mapper/**/*.xml                       # mapper映射文件
            
            spring:
               application:
                name: springcloud-mybatis-springboot-provider-dept-8001 # 对外暴露的微服务名称
               datasource:
                type: com.alibaba.druid.pool.DruidDataSource            # 当前数据源操作类型
                driver-class-name: org.gjt.mm.mysql.Driver              # mysql驱动包
                url: jdbc:mysql://localhost:3306/cloudDB01?characterEncoding=utf-8              # 数据库名称              # 数据库名称
                username: root
                password: root
                dbcp2:
                  min-idle: 5                                           # 数据库连接池的最小维持连接数
                  initial-size: 5                                       # 初始化连接数
                  max-total: 5                                          # 最大连接数
                  max-wait-millis: 200                                  # 等待连接获取的最大超时时间
    
    
    2.mybatis.cfg.xml代码：
        <?xml version="1.0" encoding="UTF-8" ?>
        <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
        
        <configuration>
        
        	<settings>
        		<setting name="cacheEnabled" value="true" /><!-- 二级缓存开启 -->
        	</settings>
        
        </configuration>
        
    3.创建dao层代码:
        /**
         * 这个注解一定要写
         */
        @Mapper
        public interface DeptDao {
        
            boolean addDept(Dept dept);
        
            Dept findById(Long id);
        
            List<Dept> findAll();
        
        }
    注意：mapper注解一定要写。
    
    4.DeptMapper.xml代码：
        <?xml version="1.0" encoding="UTF-8" ?>
        <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        
        <mapper namespace="com.adu.springcloud.dao.DeptDao">
        
        	<select id="findById" resultType="com.adu.springcloud.entitys.Dept" parameterType="Long">
        		select deptno,dname,db_source from dept where deptno=#{deptno};
        	</select>
        	<select id="findAll" resultType="com.adu.springcloud.entitys.Dept">
        		select deptno,dname,db_source from dept;
        	</select>
        	<insert id="addDept" parameterType="com.adu.springcloud.entitys.Dept">
        		INSERT INTO dept(dname,db_source) VALUES(#{dname},DATABASE());
        	</insert>
        </mapper>
    
    5.然后service层代码：
       public interface DeptService {
       
           boolean add(Dept dept);
       
           Dept get(Long id);
       
           List<Dept> list();
       }
       
        @Service
        public class DeptServiceImpl implements DeptService {
        
            @Autowired
            private DeptDao deptDao;
        
            @Override
            public boolean add(Dept dept) {
                return deptDao.addDept(dept);
            }
        
            @Override
            public Dept get(Long id) {
                return deptDao.findById(id);
            }
        
            @Override
            public List<Dept> list() {
                return deptDao.findAll();
            }
        }
    6.controller层代码：
        @RestController
        public class DeptController {
        
            @Autowired
            private DeptService deptService;
        
            @PostMapping("/dept/add")
            public boolean add(@RequestBody Dept dept) {
                return deptService.add(dept);
            }
        
            @GetMapping("/dept/get/{id}")
            public Dept get(@PathVariable Long id) {
                return deptService.get(id);
            }
        
            @GetMapping("/dept/list")
            public List<Dept> list() {
                return deptService.list();
            }
        }
        
    7.启动类直接运行：
        @SpringBootApplication
        public class DeptProvider8001_App {
        
            public static void main(String[] args) {
                SpringApplication.run(DeptProvider8001_App.class,args);
            }
        }
        
    
