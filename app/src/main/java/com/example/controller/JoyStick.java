package com.example.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoyStick extends Fragment {
    private JoystickView joystick;
    private TextView textView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)inflater
                .inflate(R.layout.fragment_joy_stick,container,false);

        init(viewGroup);

        joystick.setOnMoveListener((angle, strength) ->
                textView.setText("angle: " + angle + " | " + "strength: " + strength));
        return viewGroup;
    }

    private void init(ViewGroup viewGroup){
        joystick = viewGroup.findViewById(R.id.joyStick);
        textView = viewGroup.findViewById(R.id.status);
    }
}