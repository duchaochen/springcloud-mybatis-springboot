# springcloud+springboot+mybatis-第一天

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
    
### 提供方整合springboot+mybatis,生产方
    
    1.由于entity包下的所有的实体类会在其它工程中都会使用，所以我们创建一个单独的工程来保存实体类，供其它工程引用
        <dependencies>
                <!-- 引入自己定义的api通用包，可以使用Dept部门Entity -->
                <dependency>
                    <groupId>com.adu.springcloud</groupId>
                    <artifactId>springcloud-mybatis-springboot-api</artifactId>
                    <version>${project.version}</version>
                </dependency>
                <dependency>
                    <groupId>junit</groupId>
                    <artifactId>junit</artifactId>
                </dependency>
        
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                </dependency>
                <dependency>
                    <groupId>com.alibaba</groupId>
                    <artifactId>druid</artifactId>
                </dependency>
                <dependency>
                    <groupId>ch.qos.logback</groupId>
                    <artifactId>logback-core</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.mybatis.spring.boot</groupId>
                    <artifactId>mybatis-spring-boot-starter</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-jetty</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-web</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-test</artifactId>
                </dependency>
                <!-- 修改后立即生效，热部署 -->
                <!-- https://mvnrepository.com/artifact/org.springframework/springloaded -->
                <dependency>
                    <groupId>org.springframework</groupId>
                    <artifactId>springloaded</artifactId>
                </dependency>
        
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-devtools</artifactId>
                </dependency>
         </dependencies>
        
    
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
        
### 调用方springboot工程

    1.pom.xml文件代码:
        <dependencies>
        		<!-- 引入自己定义的api通用包，可以使用Dept部门Entity -->
        		<dependency>
        			<groupId>com.adu.springcloud</groupId>
        			<artifactId>springcloud-mybatis-springboot-api</artifactId>
        			<version>${project.version}</version>
        		</dependency>
        		
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-web</artifactId>
        		</dependency>
        
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-test</artifactId>
        			<scope>test</scope>
        		</dependency>
        
        		<!-- 修改后立即生效，热部署 -->
        		<!-- https://mvnrepository.com/artifact/org.springframework/springloaded -->
        		<dependency>
        			<groupId>org.springframework</groupId>
        			<artifactId>springloaded</artifactId>
        			<version>${springloaded.version}</version>
        		</dependency>
        
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-devtools</artifactId>
        		</dependency>
        	</dependencies>

    2.application.yml文件代码
        server:
          port: 9001
          
    3.创建一个调用方的配置类，导入一个后端调用http请求类：RestTemplate，代码如下:
         /**
         * 后端调用http请求类
         * @return
         */
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
        
    4.controller层代码：
        @RestController
        public class DeptConsumerController {
        
            private final String HTTP_URL = "http://localhost:8001";
        
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
        
    然后启动2个工程，直接使用链接：http://localhost:9001/dept/list调用成功。
    
   

# springcloud+springboot+mybatis-第二天

### 配置eureka注册中心

    1.父工程中pom.xml代码：
        <springloaded.version>1.2.6.RELEASE</springloaded.version>
        <springboot.version>2.0.3.RELEASE</springboot.version>
        <springcloud.version>Finchley.RELEASE</springcloud.version>
        <springcloud.eureka.version>2.0.0.RELEASE</springcloud.eureka.version>
        <dependencyManagement>
            <dependencies>
                <!--导入springcloud版本-->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>${springcloud.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
                <!--导入springboot版本-->
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-dependencies</artifactId>
                    <version>${springboot.version}</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
                <!--springboot启动器-->
                 <dependency>
                    <groupId>org.mybatis.spring.boot</groupId>
                    <artifactId>mybatis-spring-boot-starter</artifactId>
                    <version>1.3.0</version>
                </dependency>
                 <!--eureka注册中心-->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
                    <version>${springcloud.eureka.version}</version>
                </dependency>
                <dependency>
                    <groupId>org.springframework</groupId>
                    <artifactId>springloaded</artifactId>
                    <version>${springloaded.version}</version>
                </dependency>
            </dependencies>
            ....
            ....
        </dependencyManagement>
        
    2.子工程pom.xml(springcloud-mybatis-springboot-eureka)
        <dependencies>
            <!--eureka-server服务端 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
            </dependency>
    
            <!-- 修改后立即生效，热部署 -->
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>springloaded</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
            </dependency>
        </dependencies>
        
    3.子工程的全局配置文件
        server:
          port: 7001
        
        eureka:
          instance:
            hostname: localhost                         #eureka服务端的实例名称
        
          client:
            register-with-eureka: false                 #false表示不向注册中心注册自己
            fetch-registry: false                       #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
            service-url:
              defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/    #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址（单机）。
        
        #      defaultZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/   #设置集群

    4.启动类需要加上@EnableEurekaServer注解即可
        @SpringBootApplication
        @EnableEurekaServer    // EurekaServer服务器端启动类,接受其它微服务注册进来
        public class SpringcloudMybatisSpringbootEurekaApplication {
        
        	public static void main(String[] args) {
        		SpringApplication.run(SpringcloudMybatisSpringbootEurekaApplication.class, args);
        	}
        }
        
    5.子工程pom.xml(springcloud-mybatis-springboot-provider-dept-8001)
        在该工程的pom文件中添加
            <!-- actuator监控信息完善 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
            <!-- 将微服务provider侧注册进eureka -->
            <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-eureka -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-eureka</artifactId>
                <version>1.4.5.RELEASE</version>
            </dependency>
    
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-config</artifactId>
            </dependency>
            
    6.然后在启动类中添加@EnableEurekaClient注解
        @SpringBootApplication
        @EnableEurekaClient
        public class DeptProvider8001_App {
        
            public static void main(String[] args) {
                SpringApplication.run(DeptProvider8001_App.class,args);
            }
        }

### Ribbon

    1.在调用方(springcloud-mybatis-springboot-consumer-dept-80)pom.xml的配置文件中添加
        <!-- Ribbon相关 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
    
    2.在全局yml文件里
        #直接可以调用eureka中的服务名称
        eureka:
          client:
            register-with-eureka: false                 #false表示不向注册中心注册自己
            service-url:
              defaultZone: http://localhost:7001/eureka     #调用eureka的服务,如果是集群就加上集群的所有链接以逗号隔开
              
    3.在配置类中添加@LoadBalanced注解
        /**
         * 后端调用http请求类
         * @LoadBalanced: 配置负载均衡
         * @return
         */
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
        
    4.在启动类中添加@EnableEurekaClient
        @SpringBootApplication
        @EnableEurekaClient
        public class SpringcloudMybatisSpringbootConsumerDept80Application {
        
        	public static void main(String[] args) {
        		SpringApplication.run(SpringcloudMybatisSpringbootConsumerDept80Application.class, args);
        	}
        }
    5.然后将controller类中的访问地址代码修改为： private final String HTTP_URL = "http://LIS-8001";
   