Java 企业级电商-服务端项目
=====================
* [点击](http://ubaby.rzhang.xin)预览项目最终上线效果
* [点击](http://admin.ubaby.rzhang.xin)预览后台管理上线效果
* [点击](http://github.com/albertrui/ubaby-fe)访问商城前端项目
* [点击](http://github.com/albertrui/admin-fe)访问后台管理前端项目
## 项目简介
  >> 整合SSM框架（Spring+SpringMVC+Mybatis），采用前后端分离的架构方式开发的仿天猫在线电商平台项目，现为本项目的服务器端实现。通过完成本项目，掌握了如下技能：
* 首先，当然是java web开发，包括javaEE的核心技术的实际应用；
* 其次，学习理解并运用目前最流行的java web框架——SSM框架的整合；
* 再者，前后端分离式设计的具体实现。
* 最后，就是如何真正将一个项目部署在生产环境上（我使用的是阿里云服务器） 在之后的开发中，会将本项目采用RESTfulAPI的方式实现。并且加入一些新鲜的东西。 此外，本项目遵守开源协议GPL-3.0。
## 技术选型
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
## 功能分析设计
>>主要分为六大模块：
* 用户管理系统
* 商品分类模块
* 购物车模块
* 收货地址模块
* 商品模块
* 订单模块
## 项目功能接口清单
![项目功能接口清单](/src/main/resources/img/img1.png)
## 分散技术点总结
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
