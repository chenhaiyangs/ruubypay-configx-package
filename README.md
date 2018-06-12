# 分布式配置工具包

分布式环境中，不可能将配置文件都和服务写在一起。因为每次服务更新，各个服务器逐一更改配置，工作量巨大，且容易出错。<br>
因此需要分布式配置工具：一处修改，全局生效。<br>
本工具 基于zookeeper实现。<br>
支持配置实时热更新，云端配置更新后能迅速将配置变更推送到各个服务端并使得新配置立即生效。如业务配置。<br>
一些程序初始化时就加载的资源，如数据库连接等，并不能实时热更新，但云端可以统一管理配置。<br>
支持配置加解密功能：一些配置如数据库密码。账户token等，可以在配置中心加密存储，在应用内解密，保护配置的安全。<br>
支持配置组和配置Bean的映射功能：可以使用javaBean中的每一个字段来映射一个配置。<br>
javaBean较map相比，可能更具有维护性，每一个字段自解释每一个配置。<br>

# 实现原理

配置在zookeeper上的存储格式如下。<br>
有三个概念：<br>
配置根节点，区分项目配置的。如/configserver/yourproject<br>
版本节点，用于区分版本的。如 /configserver/yourproject/1.0.0 <br>
组节点，用于分类配置的。如 ／configserver/yourproject/1.0.0/datasource-group <br>
配置节点，存储每一个配置如 ／configserver/yourproject/1.0.0/datasource-group/username <br>
<br/>

```
├─/configxroot                                          #配置根节点，请自行命名
│  ├─/yourproject                                       #你的项目节点。value是密码
│  │  ├─/version                                        #版本节点。如1.0.0
│  │  │  └─/group1                                      #组节点 如 datasource-group 数据库配置组
│  │  │      └─congfig1                                 #配置节点 key-value
│  │  │      └─congfig2 
│  │  │      └─congfig3
│  │  │      └─......
│  │  │  └─/group2
│  │  │      └─......
│  │  ├─/version2
│  │  │ ......
│  │  │       
│  │  ├─/version$                                       #配置版本对应的注释组。如果1.0.0版本，就会有一个1.0.0$版本。version版本下的key-value，key为配置key，value为注释
│  ├─/yourproject2 
│  │    ......                                                      
```
<br/>

# 使用要求
   
    需要jdk1.8+

# 使用方式

### 创建配置根节点

    以根路径为/configserver/userproject,密码为root为例。
    几乎所有类Unit系统都带有python，可以利用python轻松生成密码：
    python -c "import hashlib;print hashlib.sha1('root').hexdigest();"
    # dc76e9f0c0006e8f919e0c515c66dbba3982f785 
    登录zookeeper客户端
    zkCli.sh -server localhost:2181
    create /configserver 1
    create /configserver/userproject dc76e9f0c0006e8f919e0c515c66dbba3982f785
    
### 部署配置管理中心界面

    git clone https://github.com/chenhaiyangs/ruubypay-configx-package.git
    cd ruubypay-configx-package/ruubypay-configx-web
    mvn package 生成一个zip地址
    解压缩zip，修改application.yml的zookeeper和端口地址。即可调用./start.sh启动配置界面
    
    访问http://localhost:8082/
    出现登录页面，用户名和密码。请分别输入/configserver/userproject root
    点击"新建版本"，输入1.0.0
    左侧的组管理，输入group，点击"创建"
    在右侧添加两个配置，分别为str=hello, int=7758
    
配置中心页面示例，请访问：http://chen.onhaiyang.com/login <br>
输入用户名：/configserver/demoproject <br>
密码：root <br>

### 在代码里使用配置中心的配置

一，导入依赖：
```xml
    <!-- 配置中心工具包 -->
    <dependency>
        <groupId>com.github.chenhaiyangs</groupId>
        <artifactId>ruubypay-configx-core</artifactId>
        <version>1.0.0</version>
     </dependency>
```   
二，在java代码里直接获取配置
```java
  //项目配置。zk地址，配置节点，要加载的版本号
  ZookeeperConfigProfile zookeeperConfigProfile = new ZookeeperConfigProfile("localhost:2181","/configserver/userproject","1.0.0");
  Map<String,String> configs = new ZookeeperConfigGroup(zookeeperConfigProfile,"group");
  System.out.Print("str:"+configs.get("str"));
  System.out.Print("int:"+configs.get("int"));

```
三，和spring集成

spring xml schema
```xml
  <beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xmlns:config="https://com.github.chenhaiyangs/ruubypay-framework-configx/config"
  	xsi:schemaLocation="http://www.springframework.org/schema/beans 
  	http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
              https://com.github.chenhaiyangs/ruubypay-framework-configx/config
              https://com.github.chenhaiyangs/ruubypay-framework-configx/config/config.xsd">
    </beans>         
``` 
spring的配置
```xml
   <config:profile connect-str="localhost:2181" root-node="/configserver/userproject" version="1.0.0"/>
   <!-- <config:group node为配置中心的group名称。id表示加载到Spring中的Map实例Id -->
   <config:group id="groupProp" node="group"/>     
``` 
在Spring项目中使用方式一
                
       受Spring管理的javaBean可以使用@value注解实例化
             
       @Value("#{groupProp['str']}")
       private String strKey
       @Value("#{groupProp['int']}")
       private Integer intKey

此种绑定方式不支持配置热更新，仅仅在应用初始化的时候给实例赋值

在Spring项目中使用方式二
    
    <!-- 
        使用配置中心的配置初始化其他的配置，例如，服务的数据库连接等配置     
    -->
    <config:group id="datasourceGroup" node="datasource-group"/>
     <!--1.数据库连接池-->
     <bean id="MySqldataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
            <property name="url" value="#{datasourceGroup['mysql-url']}" />
            <property name="username" value="#{datasourceGroup['mysql-username']}" />
            <property name="password" value="#{datasourceGroup['mysql-password']}" />
            <!-- ...... -->
     </bean>
此种绑定方式不支持配置热更新，仅仅在应用初始化的时候给实例赋值
      
在Spring项目中使用方式三    
```java
  @Resource(name="groupProp")
  private Map<String,String> configs
```    
configs里面的配置会热更新，通过配置中心控制台修改配置，configs里面的配置会变化。

# 扩展功能

### 实现基于javaBean的动态配置
    
有时，我们不想使用map来描述配置，我们希望能够提供基于javaBean的配置类实现动态配置。
该工具支持两种基于javaBean的绑定方式：外部绑定和内部绑定。

##### 外部绑定
    
外部绑定，指的是配置Bean自己负责实例化。然后将Bean交给绑定容器。之后，该javaBean中的属性就可以自动更新了。<br/>
直接编写java代码实现：
```java
//javaBean：
 public class DemoBean {
 
     @ProperKey(key="log_name")
     private String name;
     private Long longS;
     @ProperKey(key = "long_n")
     private long longN;
     private Double aDouble;
     private double bDouble;
 
     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }
 
     public Long getLongS() {
         return longS;
     }
 
     public void setLongS(Long longS) {
         this.longS = longS;
     }
 
     public long getLongN() {
         return longN;
     }
 
     public void setLongN(long longN) {
         this.longN = longN;
     }
 
     public Double getaDouble() {
         return aDouble;
     }
 
     public void setaDouble(Double aDouble) {
         this.aDouble = aDouble;
     }
 
     public double getbDouble() {
         return bDouble;
     }
 
     public void setbDouble(double bDouble) {
         this.bDouble = bDouble;
     }
 }
    //客户端代码
    ZookeeperConfigProfile zookeeperConfigProfile = new ZookeeperConfigProfile("127.0.0.1:2181","/configserver/userproject","1.0.0");
    AbstractGeneralConfigGroup node = new ZookeeperConfigGroup(zookeeperConfigProfile,"group");
 
    DemoBean demoBean = new DemoBean();
    ConfigReflectWithOuterBean<DemoBean> outerBind =new ConfigReflectWithOuterBean<>(demoBean,node);
    
   //只要全局变量outerBind 不会被垃圾回收，demoBean.getXxx获取的值会实现热更新。
```
javaBean和group的绑定默认是按照javaBean的字段名。即，会以字段名去寻找group中的对应的key。<br/>
如果java的字段名和key无法对应。使用@ProperKey注解声明即可。例如：
```java
     @ProperKey(key="user_name")
     private String userName;
```
使用Spring的方式集成
```xml
    <config:profile connect-str="localhost:2181" root-node="/configserver/userproject" version="1.0.0"/>
    <!-- <config:group node为配置中心的group名称。id表示加载到Spring中的Map实例Id -->
    <config:group id="groupProp" node="group"/>     
    <bean id="groupBean" class="com.xxx.xxx.xxx.GroupBean"/>
    <config:outerbean bind-ref="groupBean" node="groupProp" id="groupPropBind"/>
```
在业务代码里使用：
```java
     @Resource(name="groupBean")
     private GroupBean groupBean;
```
注意事项：
* 外部绑定方式不负责Bean的初始化。程序需要自行实例化配置Bean并给Bean中的配置赋值初始值,否则程序启动时，如果绑定器没有被加载。可能配置Bean中的配置值都为null
* 在Spring应用中，可以使用 @Value("#{groupProp['str']}")此种方式使Bean中的字段初始化。避免业务方在程序刚启动时拿到值为null的配置

##### 内部绑定

内部绑定，指的是该工具负责实例化配置Bean。并使得配置Bean实现属性自动更新。需要传递配置Bean的类元信息<br/>
直接编写java代码实现：
```java
     //客户端代码
    ZookeeperConfigProfile zookeeperConfigProfile = new ZookeeperConfigProfile("127.0.0.1:2181","/configserver/userproject","1.0.0");
    AbstractGeneralConfigGroup node = new ZookeeperConfigGroup(zookeeperConfigProfile,"group");
    ConfigReflectWithInnerBean<DemoBean> innerBind = new ConfigReflectWithInnerBean<>(DemoBean.class,node);
    DemoBean demoBean = innerBind.getConfigBean();
    //只要全局变量innerBind 不会被垃圾回收，demoBean.getXxx获取的值会实现热更新。
```
使用Spring的方式集成
```xml
    <config:profile connect-str="localhost:2181" root-node="/configserver/userproject" version="1.0.0"/>
    <!-- <config:group node为配置中心的group名称。id表示加载到Spring中的Map实例Id -->
    <config:group id="groupProp" node="group"/>     
    <config:innerbean id="groupPropInnerBind" bind-class="com.xxx.xxx.xxx.DemoBean.class" node="groupProp"/> 
```
在业务代码里使用：
```java
     @Resource(name="groupPropInnerBind")
     private ConfigReflectWithInnerBean<DemoBean> innerBindWithGroup;
```
```java
    String value = innerBind.getConfigBean().getXxx();
```
### 配置加密解密支持

本工具支持配置加解密。<br/>
框架支持了基于AES算法和DES算法的加解密。用户请自行选择。<br/>
如果两种方式都不满足要求，用户可以实现com.ruubypay.framework.configx.Encrypt接口自行扩展。<br/>
直接java代码使用：AES或者DES。构造函数的参数为密钥
```java
   ZookeeperConfigProfile zookeeperConfigProfile = new ZookeeperConfigProfile("127.0.0.1:2181","/configserver/userproject","1.0.0");
   //Encrypt encrypt = new EncryptByAes("1870577f29b17d6787782f35998c4a79");
   //1870577f29dfrkfs为Des加解密使用的密匙
   Encrypt encrypt = new EncryptByDes("1870577f29dfrkfs");
   AbstractGeneralConfigGroup node = new ZookeeperConfigGroup(zookeeperConfigProfile,"group",encrypt);
``` 
在Spring中使用：
```xml
   <config:profile connect-str="localhost:2181" root-node="/configserver/userproject" version="1.0.0"/>
   <!-- 使用DES 算法 -->
   <config:encrypt-by-des id="encryptByDes" key="0bd38368ae2cb5d6"/>
   <!-- 
        使用AES算法：
        <config:encrypt-by-aes id="encryptByAes" key="1870577f29b17d6787782f35998c4a79"/>
   -->
   
   <!-- 
        自己实现的加解密算法
        <Bean id="yourEncrypt" class="xxx.xxx.xxx.xxx.Xxx">
            <peoperty key="secret"  value="xxxxx"/>
        </Bean>
   -->
   <!-- <config:group node为配置中心的group名称。id表示加载到Spring中的Map实例Id -->
   <config:group id="groupProp" node="group" encrypt="encryptByDes"/>     
``` 
需要加密的配置在配置中心中value的格式：<br/>
格式为{cipher}密文，{cipher}为前缀。<br/>
{cipher}612d3e4326aa6c43 <br/>
此种格式的配置。客户端会按照指定的解密方式解密612d3e4326aa6c43为明文。<br/>


开发阶段的帮助工具类：
在com.ruubypay.framework.configx.encrypt.helper包下提供了开发阶段用于配置加解密的一些工具。帮助加密和生成可以使用的密钥key。<br/>
```java
     //生成一个使用AES算法加密时可以使用的密钥
     String aesSecret = GenerateKeyUtil.getStringSecturyKeyByAes();
```
```java
     //生成一个使用DES算法加密时可以使用的密钥
     String desSecret = GenerateKeyUtil.getStringSecturyKeyByDes();
```
```java
     //使用AES算法加密原文，获取配置中心可以设置的密文，入参1：原文。入参2，密钥
     String result = GenerateKeyUtil.getEncryptResultByAes("helloWorld","xxdfsdedsdsd");
```
```java
    //使用DES算法加密原文，获取配置中心可以设置的密文，入参1：原文。入参2，密钥
    String result = GenerateKeyUtil.getEncryptResultByDes("helloWorld","xxdfsdedsdsd");
```
   
# 注意事项

一，源代码编译问题
    
    项目中使用了lombok插件，该插件可以将常见的样板代码getter/setter/toString等等。延迟到编译时动态生成。
    这样做的目的是可以使得java代码简洁。
    因此，你可能会发现，代码在你本地提示编译错误，但实际上并不影响正常编译。如果想抑制编译错误。可以在IDE中添加lombok插件。
    <!-- lombok 消除java中必须要有，但是又显得很臃肿的代码。此插件实现了java 极简代码 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.16.10</version>
    </dependency>
    
二，jdk版本的问题

    鉴于目前大多数企业的jdk已经迁移至java8。因此本工具只支持java8以上版本使用。
    configx-web使用了SpringBoot 2.0编写。
    
# 结束语

本工具借鉴了其他分布式配置框架的源码思想和设计思路。<br/>
但又针对我们自身的业务做了一些扩展和改进。<br/>
框架本身更加轻量级，专注于使用zookeeper作为分布式配置中心的实现，并且简化了日常项目的配置类代码。<br/>
开发者qq:2421809256 欢迎一起讨论和改进。<br/>