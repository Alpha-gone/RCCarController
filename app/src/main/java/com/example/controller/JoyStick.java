package com.example.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.controller.rest.ControlData;
import com.example.controller.rest.ControlService;
import com.example.controller.rest.RetrofitHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoyStick extends Fragment {
    private JoystickView joystick;
    private Button initBtn;
    private RetrofitHelper helper;
    private Disposable disposable;
    private TextView status;

    public JoyStick(RetrofitHelper helper) {
        this.helper = helper;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup)inflater
                .inflate(R.layout.fragment_joy_stick,container,false);

        init(viewGroup);

        initBtn.setOnClickListener(view -> {
                    Call<Void> initCall = helper.getCodeBlockCall("I");
                    System.out.println(initCall.request());

                    initCall.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                });

        return viewGroup;
    }

    private void init(ViewGroup viewGroup){
        joystick = viewGroup.findViewById(R.id.joyStick);
        initBtn = viewGroup.findViewById(R.id.initBtn);
        status = viewGroup.findViewById(R.id.status);
    }

    @Override
    public void onResume() {
        super.onResume();

        disposable = getMoveEventObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .sample(66L, TimeUnit.MILLISECONDS)
                .subscribe(controlData -> helper.joystickControl(controlData),
                        throwable -> System.out.println(throwable.getLocalizedMessage()));
    }

    private Observable<ControlData> getMoveEventObservable(){
        return Observable.create(emitter ->
            joystick.setOnMoveListener(((angle, strength) -> {
                double steering = (strength != 0) ? getSteering(angle) : 0;
                String focus = (strength == 0 && angle == 0) ? "break" : getFocus(angle);

                status.setText(angle + " | " + focus);
                emitter.onNext(new ControlData((int)(strength * 0.15), steering, focus));
            }))
        );
    }


    private double getSteering(int angle){
        double steering = (getAnglePer90(angle) - getQuadrant(angle) - getDirection(angle))
                * getSign(angle);

        return Math.floor(steering * 10) /10.0;
    }

    private double getAnglePer90(int angle){
        return angle / 90.0;
    }

    private int getQuadrant(int angle){
        return (int)getAnglePer90(angle);
    }

    private int getDirection(int angle){
        int quadrant = getQuadrant(angle);

        return (quadrant == 1 || quadrant == 3) ? 0 : 1;
    }

    private int getSign(int angle){
       return (getQuadrant(angle) < 2) ? -1 : 1;
    }

    private String getFocus(int angle){
        return ((angle  >= 0 &&angle >= 358) || angle <= 182 ||
                getQuadrant(angle) < 2) ? "forward" : "backward";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}