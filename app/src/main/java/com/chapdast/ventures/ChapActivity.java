package com.chapdast.ventures;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chapdast.ventures.Configs.ENV;
import com.chapdast.ventures.Handlers.NoConnectionDialog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.chapdast.ventures.Configs.VarsKt.isNetworkAvailable;

public class ChapActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        HelloApp.activity = this;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }

    public static boolean netCheck(Activity activity) {
        HelloApp.activity = (AppCompatActivity) activity;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            if (!isNetworkAvailable(HelloApp.context)) {
                Intent noCon = new Intent(HelloApp.context, NoConnectionDialog.class);
                noCon.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HelloApp.context.startActivity(noCon);
                activity.finish();
                Log.d("NET_STAT", "0_NO NET CON");
                return false;
            }

            boolean stat = ChapActivity.executeReq(new URL(ENV.SERVER_ADDRESS));
            if (!stat) {
                Intent noCon = new Intent(HelloApp.context, NoConnectionDialog.class);
                noCon.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HelloApp.context.startActivity(noCon);
                activity.finish();
                Log.d("NET_STAT", "1_NO SERVER CON");
                return false;
            } else {
                Log.d("NET_STAT", "2_SERVER CON " + stat);
                return true;
            }

        } catch (IOException e) {
            Log.d("NET_STAT", "3_" + e.toString());
        }


        return isNetworkAvailable(HelloApp.context);
    }

    private static boolean executeReq(URL urlObject){
        try {
            HttpURLConnection conn = null;

            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setReadTimeout(8000); //Milliseconds
            conn.setConnectTimeout(8000); //Milliseconds
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            // Start connect
            conn.connect();
            int code = conn.getResponseCode();
            Log.d("NET_STAT", "4_" + code);
            if (code == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
           Log.e("NET_STAT", e.getMessage());
        }
        return false;
    }


}
