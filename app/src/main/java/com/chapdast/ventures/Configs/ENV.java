package com.chapdast.ventures.Configs;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public class ENV {
    public static final String MEDIA_SERVER_ADDRESS = EnvKt.MEDIA_SERVER_ADDRESS;
    public static final String SERVER_ADDRESS = EnvKt.SERVER_ADDRESS;
    public static AppCompatActivity current_activity = null;
    public static Context current_context = null;
}
