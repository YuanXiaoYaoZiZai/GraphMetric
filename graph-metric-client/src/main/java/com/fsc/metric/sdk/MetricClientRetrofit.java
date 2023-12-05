package com.fsc.metric.sdk;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fsc.common.utils.StreamResponse;
import com.fsc.common.utils.StreamResponseHttpCode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class MetricClientRetrofit {

    private static Logger logger = LoggerFactory.getLogger(MetricClientRetrofit.class);
    private MetricClientRequest metricClientRequest = null;
    private String address = "";

    public MetricClientRetrofit(String address) {
        this.address = address;
        logger.info("Graph metric address: {}", address);
        initRetrofitClient();
    }

    public void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        // 初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        metricClientRequest = retrofit.create(MetricClientRequest.class);
    }

    public <T> StreamResponse<T> execute(Call<StreamResponse<T>> call) {
        String url = call.request().url().toString();
        StreamResponse<T> response = new StreamResponse<>();
        try {
            Response<StreamResponse<T>> resp = call.execute();
            if (resp.isSuccessful()) {
                return resp.body();
            }
        } catch (Exception e) {
            logger.error("request operate url: {} error", url, e);
        }
        response.setCode(String.valueOf(StreamResponseHttpCode.SERVER_ERROR));
        response.setMsg(StreamResponseHttpCode.SERVER_ERROR_MSG);
        return response;
    }

    public void asyncExecute(Call<StreamResponse<Boolean>> call) {
        String url = call.request().url().toString();
        try {
            call.enqueue(new Callback<StreamResponse<Boolean>>() {
                @Override
                public void onResponse(Call<StreamResponse<Boolean>> call, Response<StreamResponse<Boolean>> response) {

                }

                @Override
                public void onFailure(Call<StreamResponse<Boolean>> call1, Throwable t) {
                    logger.error("async request operate url {} failed. {}", url, call.request().toString());
                }

            });
        } catch (Exception e) {
            logger.error("async request operate url: {} error", url, e);
        }
    }

    public MetricClientRequest getMetricClientRequest() {
        return metricClientRequest;
    }
}
