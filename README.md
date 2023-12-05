# SpringBoot项目，集成Neoj4
# 封装Neo4j SQL操作，申明式调用Neo4j的节点和关系的CRUD操作，并暴露出HTTP接口。
# 使用本地缓存提高性能
# 修改接口都为异步操作，操作封装为任务，写入任务队列，并发执行，HTTP接口迅速返回。

common-utils 通用工具类
common-config 通用配置读取类，读取本地配置
common-cache 通用本地缓存
graph-metric-common 通用MODEL
graph-metric-client 通用SDK，可以打成jar包给服务节点进行数据上报
graph-metric-server 服务端，接受客户端上报数据
