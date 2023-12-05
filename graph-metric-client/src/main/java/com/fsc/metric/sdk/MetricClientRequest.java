package com.fsc.metric.sdk;

import com.fsc.metric.model.GraphAll;
import com.fsc.metric.model.GraphReq;
import com.fsc.common.utils.StreamResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface MetricClientRequest {

    @POST("api/graph/realTime/props/get/v1")
    Call<StreamResponse<Map<String, String>>> getRealTimeProps();

    @POST("api/graph/realTime/props/update/v1")
    Call<StreamResponse<Boolean>> updateRealTimeProps(@Body Map<String, String> req);

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

}
