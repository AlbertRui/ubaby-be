Java 企业级电商-服务端项目
=====================
* [点击](http://ubaby.rzhang.xin)预览项目最终上线效果
* [点击](http://admin.ubaby.rzhang.xin)预览后台管理上线效果
* [点击](http://github.com/albertrui/ubaby-fe)访问商城前端项目
* [点击](http://github.com/albertrui/admin-fe)访问后台管理前端项目
## 一、项目简介
>>整合SSM框架（Spring+SpringMVC+Mybatis），采用前后端分离的架构方式开发的仿天猫在线电商平台项目，现为本项目的服务器端实现。通过完成本项目，掌握了如下技能：
* 首先，当然是java web开发，包括javaEE的核心技术的实际应用；
* 其次，学习理解并运用目前最流行的java web框架——SSM框架的整合；
* 再者，前后端分离式设计的具体实现。
* 最后，就是如何真正将一个项目部署在生产环境上（我使用的是阿里云服务器） 在之后的开发中，会将本项目采用RESTfulAPI的方式实现。并且加入一些新鲜的东西。 此外，本项目遵守开源协议GPL-3.0。
## 二、技术选型
* 数据库：MySQL5.7
* 数据源：dbcp
* 框架组合：Spring + SpringMVC + Mybatis
* 日志处理：logback
* 工具类：guava
* 分页处理：pagehelper
* 后端缓存：Redis
* Restful风格接口设计
* web服务器：阿里云centos7虚拟主机（Linux操作系统）
* 文件服务器：基于阿里云centos搭建ftp服务器
* 层之间解耦方案：工厂设计模式
* 数据库连接及事物处理均交给Spring
## 三、功能分析设计
>>主要分为六大模块：
* 用户管理系统
* 商品分类模块
* 购物车模块
* 收货地址模块
* 商品模块
* 订单模块
## 四、用户模块
>>用户模块技术要点：
1. 横向越权和纵向越权
2. MD5明文加密、guava缓存
3. 高复用服务响应对象的设计思想和封装
### （一）、用户模块功能：
1. 登录功能
2. 用户名校验
3. 注册功能
4. 忘记密码
5. 提交问题答案
6. 重置密码
7. 获取用户信息
8. 更新用户信息
9. 退出登录
### （二）、登录功能：
1. 用户名是否存在
2. 如果存在则将密码转换为MD5加密形式
3. 校验用户名和密码是否正确
4. 正确则将用户放入到Session中 
### （三）、注册功能：
1. 用户名是否存在
2. 校验邮箱是否存在
3. 将密码转化为MD5形式
4. 将用户放入数据库中 
### （四）、忘记密码重置密码功能：
1. 用户名是否存在
2. 根据用户名查询问题
3. 答案正确则生成token
4. 将token存入到guava cache本地缓存中，有效期为12小时（防止横向越权）
5. 校验用户名是否存在
6. 校验token是否正确
7. 正确则重新设置密码 
### （五）、登录状态下重置密码功能：
1. 从session中取出用户
2. 校验旧密码是否正确（防止横向越权）
3. 正确则修改密码
### （六）、更新用户信息
1. 判断用户是否登录
2. 取出用户的id和username
3. 判断邮箱是否重复
4. 不重复则更新用户信息并将其放入到session中 
### （七）、获取用户信息和退出登录
* 获取用户信息：
    * 判断用户是否登录
    * 登录则将用户信息取出来
* 退出登录：将用户从session中移除 
### （八）、高复用服务响应对象的设计思想和封装
在web开发中，现在比较流行的是从控制层往前台返回json格式的数据，而若每次的返回都设计一个类的话，不方便使用的同时也会显得很臃肿。因此可以利用泛型的设计思想设计一个高可用复用的对象，来统一返回的json格式的数据。 
### （九）、横向越权和纵向越权
1. 什么是横向越权和纵向越权：
* 横向越权：攻击者尝试访问与他拥有相同权限的用户的资源
* 纵向越权：低级别攻击者尝试访问高级别用户的资源
2. 如何解决：
>>横向越权可能出现的场景有：
* 在用户忘记密码重置密码时，回答对了问题进入密码重置阶段时，如果知道其他用户的用户名，很容易改变此用户的密码，然后就可以进行越权访问了。
* 在删除收货地址的时候，如果用户登录了，输入的是其他用户的收货地址id，则把其他的用户的收货地址删了，项目中通过用户id和收货地址id两项才能删除，防止横向越权
代码如下：
```
public ServerResponse<String> del(Integer userId,Integer shippingId){
   int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
   if(resultCount > 0)
       return ServerResponse.createBySuccess("删除地址成功");
   return ServerResponse.createByErrorMessage("删除地址失败");
}
```
>>横向越权解决：

这种情况下为了防止横向越权，使用缓存来进行辅助，当问题回答正确时，在缓存中存储一对由用户名和一个唯一的数字组成的字符串。在重置密码时我们的参数不仅需要用户名和密码还需要前面生成的唯一数字符串，根据用户名在缓存中取出对应的字符串，如果取出的字符串和参数中传入的相等，则可以重置的当前用户的密码，否则不是，且不予以重置。
>>纵向越权解决：

通过设置用户角色，为不同的角色提供不同的权限来避免。
### （十）、项目中的Token
>>主要用做身份验证。
1. 点击忘记密码之后，检验用户名是否存在
2. 存在则根据用户名查询问题
3. 回答答案正确的话生产Token，并将其放到guava cache本地缓存中
4. 将Token返回给客户端
5. 重置密码的时候需要携带Token
### （十一）、缓存（Guava Cache）
## 五、分类模块
>>分类模块技术要点：
1. 递归算法
2. 复杂对象排重
3. 无限层级树结构设计
### （一）、添加分类
1. 判断用户是否登录
2. 判断用户是否是管理员
3. 如果是管理员则根据分类名和父类id添加分类节点 
### （二）、修改分类
1. 判断用户是否登录
2. 判断用户是否是管理员
3. 根据分类id修改分类名 
### （三）、获取平级节点
1. 判断用户是否登录
2. 判断用户是否是管理员
3. 根据id查询评级节点 
### （四）、获取平级节点及其所有子节点
1. 判断用户是否登录
2. 判断用户是否是管理员
3. 使用set集合保存Category对象
4. 重写category对象的hashcode和equals方法
5. 根据父类id使用递归算法算出子节点 
### （五）、递归算法和无限层级树结构设计
>>无限层级树结构设计：

给每条数据加个 parent_id字段，通过parent_id来建立数据之间的父子(层级)关系，parent_id为0是根节点。 
### （六）、复杂对象排重
使用hashSet并重写其hashcode和equal。这样递归得到的分类节点中就没有重复的。
## 六、商品模块
>>商品模块技术要点
1. POJO、BO、VO抽象模型
2. 高效分页及动态排序
3. FTP服务对接、富文本上传
### （一）、商品模块功能
>>前台功能：
1. 产品搜索
2. 动态排序列表
3. 商品详情
>>后台功能：
1. 商品列表
2. 商品搜索
3. 图片上传
4. 增加商品、更新商品、商品上下架
### （二）、后台新增和更新商品 
### （三）、后台获取产商品详情
### （四）、简单分析下VO（view object）：
包装类其实就是我们项目中的pojo类，字段与数据库表的字段是相同的，而vo类可以简单理解成专门用于展示给用户看的类。
这里使用VO，这样可以减少大量的工作量（也就意味着减少bug，减少风险），也不需要担心未来的维护工作！VO还有一个好处就是是例如时间类型，返回给前台的时候需要转String类型，pojo转vo是为了封装，例如时间做成字符串，或者枚举转换成汉字，或者增加其他属性，这样vo的灵活性就突显出来了。
### （五）、使用流读取配置properties配置文件：
1. 为什么使用properties
有些常量需要动态的配置，如果项目上线后，每次修改Constants.java然后再编译，再上传Constants.class文件，再重启服务器。这样导致很繁琐。
如果将需要修改的配置项写成properties文件，程序写好后，以后要修改数据，直接在配置文件里修改，程序就不用修改，也不用重新编译了，将会在项目后期维护带来很大的方便~！
2. propertie文件读取方式
① 基于 ClassLoader 读取配置文件：
注意：该方式只能读取类路径下的配置文件，有局限但是如果配置文件在类路径下比较方便。
```
Properties properties = new Properties();
// 使用ClassLoader加载properties配置文件生成对应的输入流
InputStream in = PropertiesMain.class.getClassLoader().getResourceAsStream("config/config.properties");
// 使用properties对象加载输入流
properties.load(in);
//获取key对应的value值
properties.getProperty(String key);
```
② 基于 InputStream 读取配置文件：
注意：该方式的优点在于可以读取任意路径下的配置文件
```
Properties properties = new Properties();
// 使用InPutStream流读取properties文件
BufferedReader bufferedReader = new BufferedReader(new FileReader("E:/config.properties"));
properties.load(bufferedReader);
// 获取key对应的value值
properties.getProperty(String key);
```
③ 通过 java.util.ResourceBundle 类来读取，这种方式比使用 Properties 要方便一些：
注意：该方式的优点在于这种方式来获取properties属性文件不需要加.properties后缀名，只需要文件名即可
```
properties.getProperty(String key);
//config为属性文件名，放在包com.test.config下，如果是放在src下，直接用config即可  
ResourceBundle resource = ResourceBundle.getBundle("com/test/config/config");
String key = resource.getString("keyWord"); 
```
因为配置文件在类路径下使用第一种方式比较方便，所以本项目采用的是第一种方式：
### （六）、joda-time和JDK自带的SimpleDateformat的区别是什么？
因为jdk的Date/Calender等api使用具有一定的难度,且jdk默认的有多线程问题，Joda-Time 令时间和日期值变得易于管理、操作和理解。事实上，易于使用是 Joda 的主要设计目标。其他目标包括可扩展性、完整的特性集以及对多种日历系统的支持。并且 Joda 与 JDK 是百分之百可互操作的。
### （七）、后台商品列表
* Mybatis流程：
![Mybatis流程](/src/main/resources/img/img2.png)
从图中可以看出，mybatis中首先要在配置文件中配置一些东西，然后根据这些配置去创建一个会话工厂，再根据会话工厂创建会话，会话发出操作数据库的sql语句，然后通过执行器操作数据，再使用mappedStatement对数据进行封装，这就是整个mybatis框架的执行情况。那么mybatis的插件作用在哪一环节呢？它主要作用在Executor执行器与mappedeStatement之间，也就是说mybatis可以在插件中获得要执行的sql语句，在sql语句中添加limit语句，然后再去对sql进行封装，从而可以实现分页处理。
* 动态分页
pageHelper分页的底层主要是通过 aop来实现，在执行sql之前会在sql语句中添加limit offset这两个参数。这样就完成了动态的分页。
我们需要用vo返回给前端。如果我们用vo里的字段，是和pojo总会有不一致的地方。例如时间的类型，又例如添加的一些枚举状态等。那么为了自动分页，我们会用dao层找到原始的pojoList，(因为pageHelper是对dao层在执行mapper的时候才会动态分页，所以我们要先执行一下mapper)然后转换成vo。那么其实这两个list的集合的分页参数是一致的。所以用了一个比较巧妙的办法，来把vo进行分页。 
### （八）、后台商品搜索功能
### （九）、后台商品图片上传功能
### （十）、后台富文本图片上传
### （十一）、前台功能
>>动态排序、两种方法：
1. 方式1
```
//其中A为排序依据的字段名，B为排序规律，desc为降序，asc为升序
PageHelper.startPage(pageNum , pageSize);
PageHelper.orderBy("A B");
```
2. 方式2
```
//其中A为排序依据的字段名，B为排序规律，desc为降序，asc为升序
String orderBy="A B"；
PageHelper.startPage(pageNum, pageSize, orderBy)；
```
### （十二）、FTP服务器
1. FTP是什么？
FTP是File Transfer Protocol(文件传输协议)的英文简称，用于Internet上的控制文件的双向传输。简单的说，支持FTP协议的服务器就是FTP服务器。FTP是一个客户机/服务器系统。用户通过一个支持FTP协议的客户机程序，连接到在远程主机上的FTP服务器程序。用户通过客户机程序向服务器程序发出命令，服务器程序执行用户所发出的命令，并将执行的结果返回到客户机。
2. 为什么使用FTP服务器
从一个小网站说起。一台服务器也就足够了。文件服务器，数据库，还有应用都部署在一台机器，俗称ALL IN ONE。随着我们用户越来越多，访问越来越大，硬盘，CPU，内存等都开始吃紧。一台服务器已经满足不了，我们将数据服务和应用服务分离，给应用服务器配置更好的 CPU，内存。而给数据服务器配置更好更大的硬盘。分离之后提高一定的可用性，例如Files Server挂了，我们还是可以操作应用和数据库等。
![ftp服务器](/src/main/resources/img/img3.png)
3. 对比FTP和FastDFS
单独部署的文件服务器是互联网项目必不可少的一项。简单的话可以采用FTP做文件服务器，但是项目访问量持续增加的话，必要考虑文件服务器的扩展性与高可用。这时候采用FastDFS是比较明智的，当服务器容量不够用时，FastDFS可以快速的进行线性扩容。FastDFS是用c语言编写的一款开源的分布式文件系统。FastDFS为互联网量身定制，充分考虑了冗余备份、负载均衡、线性扩容等机制，并注重高可用、高性能等指标，使用FastDFS很容易搭建一套高性能的文件服务器集群提供文件上传、下载等服务。 
## 七、购物车模块
>>购物车模块技术要点：
1. 商品总价计算复用封装
2. 高复用的逻辑方法封装思想
3. 解决商业运算丢失精度的坑
### （一）、购物车模块功能
1. 购物车添加商品
2. 更新商品数量
3. 查询商品数量
4. 移除购物车中商品
5. 单选/全选
### （二）、购物车添加商品
>>CartProductVo

单个商品的信息，限制商品数量以及计算某项商品的总价描述
>>CartVo

用来描述购物车中的情况，封装所有商品总价，全选描述
>>解决商业运算丢失精度的问题

可以使用BigDecimal
### （三）、更新商品数量
### （四）、移除购物车中的商品
### （五）、单选全选问题
## 八、收货地址模块
>>收货地址模块技术要点：
1. 同步获取自增主键
2. 数据绑定的对象绑定
3. 越权问题升级巩固
### （一）、新增收货地址
>>同步获取自增主键

Mybatis 配置文件 useGeneratedKeys 参数只针对 insert 语句生效，默认为 false。当设置为 true 时，表示如果插入的表以自增列为主键，则允许 JDBC 支持自动生成主键，并可将自动生成的主键返回。

```
    <insert id="insert" parameterType="com.mmall.pojo.Shipping" useGeneratedKeys="true" keyProperty="id">
```
### （二）、删除收货地址
### （三）、更新收货地址

>>SpringMVC绑定对象

SpringMVC会按请求参数名和POJO属性名进行自动匹配，自动为该对象填充属性值，支持级联属性。
注意：pojo对象的属性名和表单中input的name属性一致
表单：
```
    <form action="${pageContext.request.contextPath}/insertItem.action" method="post">
        编号:<input name="id" ><br/>
        名称:<input name="name" ><br/>
        出厂时间:<input name="time" ><br/>
        备注:<input name="remark" ><br/>
        <input type="submit" value="提交">
    </form>
```
>>处理器映射方法：

```
    @RequestMapping(value={"/insertItem.action"})
    public String insertItem(Model model,Item item){
        return "itemList";
    }
```
* 注意：提交的表单不要有日期类型的数据，否则会报错400，如果需要提交日期类型的数据，需要自定义参数绑定的内容
>>SpringMVC时间处理

1. 在控制器中使用@InitBind注解
```
@Controller
public class TestFileController {
    @InitBinder
    public void initBind(WebDataBinder binder){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
        dateFormat.setLenient(false);  
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));   
    }
    @RequestMapping("/doAdd")
    public void doAdd(@ModelAttribute User user, HttpServletRequest request){
        System.out.println(user.getName());
        System.out.println(user.getBirthtime());
    }
}
```
2. 在POJO中日期属性上添加@DateTimeFormat
```
public class User{
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date birthtime;

    // 属性的setter/getter方法略
}
```
>>SpringMVC乱码解决
1. post中文乱码：
```
<!-- 解决post提交乱码 -->
<filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>*.action</url-pattern>
</filter-mapping>
```
2. get中文乱码：
* 方法1:修改tomcat的server.xml 
```
<Connector URIEncoding="utf-8" connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443"/>
```
* 方式2:手动转码
```
new String(request.getParameter("id").getBytes("iso8859-1"), "utf-8");
```
### （四）、查询收货地址列表
## 九、订单模块
>>订单模块技术要点
1. 安全漏洞解决方案
2. 订单号生成规则
3. 强大的常量、枚举设计
### （一）、前台创建订单
>>强大的常量、枚举设计

用接口定义常量最大的好处是有一个group的概念，很简单，我们相关的常量放在一个interface里，以后维护的成本会低很多。例如课程里的一些具有组概念的，就放在一个interface里，而相对独立的常量就用类常量。
枚举的话，key-value，有状态码，还有对应的中文说明，例如我们的订单状态。
>>订单号生成规则
1. 订单号无重复性；
2. 如果方便客服的话，最好是“日期+自增数”样式的订单号，客服一看便知道订单是否在退货保障期限内容；
3. 订单号长度尽量保持短（10位以内），方便用户，尤其电话投诉时，长的号码报错几率高，影响客服效率；
```
//此函数用于生成订单号
private long generateOrderNo(){
    long currentTime = System.currentTimeMillis();
    return currentTime + new Random().nextInt(100);
}
```
## 十、项目功能接口清单
![项目功能接口清单](/src/main/resources/img/img1.png)
## 十一、分散技术点总结
* 高可用的响应对象ServerResponse编写，实现自己的
* 越权访问的问题：横向越权和纵向越权
* Guava Cache的简单使用（LoadingCache实现了ConcurrentMap接口）
* 忘记密码重置密码时要回答对问题，返回一个token，拿token重置密码
* FTP服务对接
* SpringMVC文件上传
* Properties配置文件读取
* joda-time编写日期格式工具
* mybatis-pagehelper使用
* mybatis批量插入，主键生成
* Jackson 序列化json的工具类封装
* 分布式Session管理：原生实现与Spring Session原理
* SpringMVC 全局异常处理
* SpringMVC 拦截器处理权限与判断登录
* Spring Schedule 定时任务
* Redis分布式锁实现原理
* 一致性哈希原理，Redis客户端分片ShardedJedis
* Nginx 静态资源映射，反向代理配置
* 阿里云线上项目自动化部署
