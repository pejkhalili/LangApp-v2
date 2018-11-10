package com.chapdast.ventures.Configs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Vars {

    public static SharedPreferences SPref(Context context, String name) {
       return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor SPEdit(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
    }

    public static String ServerLevelDetector(int level){
        switch (level){
            case 1 : return "elementry";
            case 2 : return "intermediate";
            case 3 : return "advance";
            case 4 : return "504";
            case 5 : return "tofel";
            default: return "elementry";
        }
    }

    public static int ServerLevelDetector(String level){
        switch (level){
            case "elementry" : return 1;
            case "intermediate" : return 2;
            case "advance" : return 3;
            case "504" : return 4;
            case "tofel" : return 5;
            default: return 1;
        }
    }



}
