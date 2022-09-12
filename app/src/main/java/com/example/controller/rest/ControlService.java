package com.example.controller.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ControlService {
    @GET("joystick")
    Call<Void> sendControl(@Query("speed")int speed,
                             @Query("angle") double angle,
                             @Query("focus") String focus);
}
