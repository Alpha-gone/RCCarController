package com.example.controller.rest;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitHelper {
    private Retrofit retrofit;
    private ControlService service;

    public RetrofitHelper() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.251:5000/")
                .build();

        service = retrofit.create(ControlService.class);
    }

    public void changeAddress(String address) {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1." + address + ":5000/")
                .build();

        service = retrofit.create(ControlService.class);
    }

    public void joystickControl(ControlData data) throws IOException {
        service.joystickControl(data.getSpeed(), data.getAngle(), data.getFocus()).execute();
    }

    public Call<Void> getCodeBlockCall(String query){
        return service.codeBlockSend(query);
    }
}
