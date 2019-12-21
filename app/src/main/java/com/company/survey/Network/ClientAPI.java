package com.company.survey.Network;

import com.company.survey.PotholeResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ClientAPI {

    @POST("pothole/")
    @FormUrlEncoded
    Call<PotholeResponse> check(
            @Field("url") String url
    );
}
