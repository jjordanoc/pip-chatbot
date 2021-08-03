package com.example.pip;

import android.app.Application;
import android.util.AndroidException;

import com.androidnetworking.AndroidNetworking;

public class Chatbot extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
