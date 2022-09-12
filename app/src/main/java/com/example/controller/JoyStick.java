package com.example.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.controller.rest.ControlService;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class JoyStick extends Fragment {
    private JoystickView joystick;
    private TextView textView;
    private Button breakBtn;
    private Retrofit retrofit;
    private ControlService service;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup)inflater
                .inflate(R.layout.fragment_joy_stick,container,false);

        init(viewGroup);

        joystick.setOnMoveListener((angle, strength) ->{
                    double steering = (strength != 0) ? getSteering(angle) : 0;
                    String focus = (angle != 0 && strength != 0) ? getFocus(angle) : "break";

                    textView.setText("steering: " + steering + "| speed: " + strength +
                            "| forcus: " + focus );


                    service.sendControl((int)(strength * 0.3), steering, focus)
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                    }, 300);




        breakBtn.setOnClickListener(view -> service.sendControl(0,0,"break").enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        }));
        return viewGroup;
    }

    private void init(ViewGroup viewGroup){
        joystick = viewGroup.findViewById(R.id.joyStick);
        textView = viewGroup.findViewById(R.id.status);
        breakBtn = viewGroup.findViewById(R.id.breakBtn);
    }

    private double getSteering(int angle){
        double steering = ((angle / 90.0) - getQuadrant(angle) - getDirection(angle))
                * getSign(angle);

        return Math.floor(steering * 10) /10.0;
    }

    private int getQuadrant(int angle){
        return angle / 90;
    }

    private int getDirection(int angle){
        int quadrant = getQuadrant(angle);

        return (quadrant == 1 || quadrant == 3) ? 0 : 1;
    }

    private int getSign(int angle){
        int quadrant = getQuadrant(angle);

        return (quadrant < 2) ? -1 : 1;
    }

    private String getFocus(int angle){
        int quadrant = getQuadrant(angle);

        return (quadrant < 2) ? "forward" : "backward";
    }


    public void setRetrofit(Retrofit retrofit){
        this.retrofit = retrofit;
        service = this.retrofit.create(ControlService.class);
    }
}