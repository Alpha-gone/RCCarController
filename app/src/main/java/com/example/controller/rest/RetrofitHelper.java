package com.example.controller.rest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class RetrofitHelper {
    private OkHttpClient client;
    private Retrofit retrofit;
    private ControlService service;

    public RetrofitHelper() {
        client = new OkHttpClient().newBuilder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.251:5000/")
                .client(client)
                .build();

        service = retrofit.create(ControlService.class);
    }

    public void changeAddress(String address) {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1." + address + ":5000/")
                .client(client)
                .build();

        service = retrofit.create(ControlService.class);
    }

    public void joystickControl(int speed, double angle, String focus) throws IOException {
        service.joystickControl(speed, angle, focus).execute();
    }

    public void joystickControl(ControlData data) throws IOException {
        joystickControl(data.getSpeed(), data.getAngle(), data.getFocus());
    }

    public void codeBlockSend(String query) throws IOException {
        service.codeBlockSend(query).execute();
    }
}
