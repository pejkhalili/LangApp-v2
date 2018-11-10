package com.chapdast.ventures;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import com.chapdast.ventures.Configs.VarsKt;
import com.chapdast.ventures.Handlers.NoConnectionDialog;


public class HelloApp extends Application {
    //LANG APP V2
    public static AppCompatActivity activity;
    public static Context context;
    public final static Handler HANDLER = new Handler();
    public static Typeface IRANSANS;
    public static Typeface IRANSANS_BLACK;
    public static Typeface BEBAS_FONT;
    public  static String UserId;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();


        IRANSANS = Typeface.createFromAsset(context.getAssets(),"fonts/iransans.ttf");
        IRANSANS_BLACK = Typeface.createFromAsset(context.getAssets(),"fonts/iranblack.ttf");
        BEBAS_FONT = Typeface.createFromAsset(context.getAssets(),"fonts/bebas.otf");

    }

}
