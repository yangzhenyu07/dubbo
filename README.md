# SpringBoot 集成 Dubbo

## 前提:
+ mysql
+ jdk 1.8
+ dubbo 2.7.1

## demo【maven分层架构】:
- consumer【服务消费方】
- provider-demo 【服务提供方】
- interface-demo 【接口api】
- common-demo【工具层】
- dao-demo 【dao层】
- log-demo 【日志层】

## demo用到的主要技术点:
+ springBoot + dubbo 
+ 配置文件信息加密 + AES加解密 + 优雅启停 + swagger
+ dubbo【2.7】异步化接口调用 + 传统多任务并行处理 + dubbo【2.7】多任务并行处理 
+ 调用拦截 + 自定义负载均衡 + 灰度发布

