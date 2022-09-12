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

public class JoyStick extends Fragment {
    private JoystickView joystick;
    private Button breakBtn;
    private RetrofitHelper helper;
    private Disposable disposable;

    public JoyStick(RetrofitHelper helper) {
        this.helper = helper;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup)inflater
                .inflate(R.layout.fragment_joy_stick,container,false);

        init(viewGroup);

        disposable = getMoveEventObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .mergeWith(getBreakEventObservable())
                .debounce(100L, TimeUnit.MILLISECONDS)
                .subscribeWith(getObserver());

        return viewGroup;
    }

    private void init(ViewGroup viewGroup){
        joystick = viewGroup.findViewById(R.id.joyStick);
        breakBtn = viewGroup.findViewById(R.id.breakBtn);
    }

    private Observable<ControlData> getMoveEventObservable(){
        return Observable.create(emitter ->
            joystick.setOnMoveListener(((angle, strength) -> {
                double steering = (strength != 0) ? getSteering(angle) : 0;
                String focus = (angle != 0 && strength != 0) ? getFocus(angle) : "break";

                emitter.onNext(new ControlData(strength, steering, focus));
            }))
        );
    }

    private Observable<ControlData> getBreakEventObservable(){
        return Observable.create(emitter -> breakBtn.setOnClickListener(view ->
                emitter.onNext(new ControlData(0, 0, "break"))));
    }

    private DisposableObserver<ControlData> getObserver(){
        return new DisposableObserver<ControlData>() {
            @Override
            public void onNext(@NonNull ControlData controlData) {
                try {
                    System.out.println("controlData = " + controlData);
                    helper.joystickControl(controlData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        };
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
        int quadrant = getQuadrant(angle);

        return (quadrant < 2) ? -1 : 1;
    }

    private String getFocus(int angle){
        int quadrant = getQuadrant(angle);

        return (quadrant < 2) ? "forward" : "backward";
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}