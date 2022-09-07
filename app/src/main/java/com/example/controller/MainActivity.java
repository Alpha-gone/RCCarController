package com.example.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private JoyStick joyStick;
    private CodeBlock codeBlock;
    private BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        containerReplace(codeBlock);

        menu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.joyStick: containerReplace(joyStick); return true;
                case R.id.codeBlock: containerReplace(codeBlock); return true;
            }
            return false;
        });
    }

    private void init(){
        joyStick = new JoyStick();
        codeBlock = new CodeBlock();
        menu = findViewById(R.id.menu);
    }

    private void containerReplace(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.controlContainer, fragment)
                .commit();
    }
}