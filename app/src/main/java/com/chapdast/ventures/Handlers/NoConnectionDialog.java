package com.chapdast.ventures.Handlers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.chapdast.ventures.ChapActivity;
import com.chapdast.ventures.Configs.VarsKt;
import com.chapdast.ventures.HelloApp;
import com.chapdast.ventures.R;

public class NoConnectionDialog extends AppCompatActivity {

    private Context context;
    private Button tryAgain;
    private TextView msg;
//
//    public NoConnectionDialog(@NonNull Context context) {
//        super(context);
//        this.context = context;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.stat_no_connection);
        tryAgain = findViewById(R.id.hub_try_again);
        msg = findViewById(R.id.hub_no_connection);
        msg.setTypeface(HelloApp.IRANSANS);
        tryAgain.setTypeface(HelloApp.IRANSANS);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(VarsKt.isNetworkAvailable(getApplicationContext())) {
                    Intent checkAgain = new Intent(getApplicationContext(), HelloApp.activity.getClass());
                    startActivity(checkAgain);
                    finish();
                }else{
                    VarsKt.sToast(getApplicationContext(),"اتصال با سرور برقرار نشد", true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    //    @Override
//    public void show() {
//        super.show();
//        Log.e("NO_CON_DLG", "IN CHECK");
//    }

//    @Override
//    public void setOnDismissListener(@Nullable OnDismissListener listener) {
//        Intent checkAgain = new Intent(HelloApp.activity, HelloApp.activity.getClass());
//        HelloApp.activity.finish();
//        HelloApp.activity.startActivity(checkAgain);
//    }
}
