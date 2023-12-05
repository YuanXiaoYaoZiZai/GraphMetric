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

对外接口：

    @POST("api/graph/cql/execute/v1")
    Call<StreamResponse<Boolean>> executeCQL(@Body GraphReq req);

    @POST("api/graph/node/save/v1")
    Call<StreamResponse<Boolean>> saveNode(@Body GraphReq req);

    @POST("api/graph/node/update/v1")
    Call<StreamResponse<Boolean>> updateNode(@Body GraphReq req);

    @POST("api/graph/node/get/v1")
    Call<StreamResponse<GraphAll>> getNode(@Body GraphReq req);

    @POST("api/graph/node/delete/v1")
    Call<StreamResponse<Boolean>> deleteNode(@Body GraphReq req);

    @POST("api/graph/relationship/save/v1")
    Call<StreamResponse<Boolean>> saveRelationship(@Body GraphReq req);

    @POST("api/graph/relationship/update/v1")
    Call<StreamResponse<Boolean>> updateRelationship(@Body GraphReq req);

    @POST("api/graph/relationship/get/v1")
    Call<StreamResponse<GraphAll>> getRelationship(@Body GraphReq req);

    @POST("api/graph/relationship/delete/v1")
    Call<StreamResponse<Boolean>> deleteRelationship(@Body GraphReq req);
