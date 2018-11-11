package com.chapdast.ventures.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chapdast.ventures.Adapters.WelcomeSlideAdapter;
import com.chapdast.ventures.ChapActivity;
import com.chapdast.ventures.Configs.EnvKt;
import com.chapdast.ventures.Configs.VarsKt;
import com.chapdast.ventures.Handlers.Ana;
import com.chapdast.ventures.HelloApp;
import com.chapdast.ventures.R;

public class Welcome extends ChapActivity {

    private ViewPager viewPager;
    private WelcomeSlideAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext, startApp, start;
    private boolean firstTimeBackPress=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            setupPermissions();
        }

        Ana ana = new Ana(getApplicationContext());
        ana.splash(0);
        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.wlc_viewpager);
        dotsLayout = findViewById(R.id.wlc_dotsLayout);
        btnSkip = findViewById(R.id.wlc_prv);
        btnNext = findViewById(R.id.wlc_next);
        startApp = findViewById(R.id.slide5_start);
        start = findViewById(R.id.slide5_run);

        btnSkip.setTypeface(HelloApp.IRANSANS);
        btnNext.setTypeface(HelloApp.IRANSANS);
        startApp.setTypeface(HelloApp.IRANSANS);
        start.setTypeface(HelloApp.IRANSANS);

        startApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                btnNext.setVisibility(View.VISIBLE);
                start.setVisibility(View.GONE);
            }
        });
        layouts = new int[]{
                R.layout.w_slide1,
                R.layout.w_slide2,
                R.layout.w_slide4,
                R.layout.w_slide3,
                R.layout.w_slide5
        };

        addBottomDots(0);
        changeStatusBarColor();
        myViewPagerAdapter = new WelcomeSlideAdapter(layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // checking for last page
                // if last page home screen will be launched
                int current = getItem(-1);
                if (current < layouts.length) {
                    startApp.setVisibility(View.GONE);
                    // move to prev screen
                    viewPager.setCurrentItem(current);
                    if (current == 0) {
                        btnSkip.setVisibility(View.INVISIBLE);
                        btnNext.setVisibility(View.INVISIBLE);
                        start.setVisibility(View.VISIBLE);
                    } else {
                        btnNext.setVisibility(View.VISIBLE);
                    }
                }


            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.fr_yellow));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.mkWhite));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        startActivity(new Intent(Welcome.this, SplashPage.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            Ana ana = new Ana(getApplicationContext());
            ana.splash(position);
//             changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
//                btnNext.setText(getString(R.string.wlc_start));
//                btnSkip.setVisibility(View.INVISIBLE);
                startApp.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.INVISIBLE);
            } else if (position != layouts.length - 1) {
                start.setVisibility(View.GONE);
                startApp.setVisibility(View.INVISIBLE);
                if (position == 0) {
                    startApp.setVisibility(View.VISIBLE);
                    start.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                    btnSkip.setVisibility(View.INVISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnSkip.setVisibility(View.VISIBLE);
                }
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.wlc_next));

                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupPermissions() {
        int permission = getApplication().checkSelfPermission("android.permission.RECEIVE_SMS");

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest();
        }
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{"android.permission.RECEIVE_SMS", "android.permission.READ_PHONE_STATE"},
                EnvKt.SMS_REC_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        int currentSlide = getItem(0);
        if(currentSlide==0){
            if(firstTimeBackPress){
                firstTimeBackPress=false;
                VarsKt.sToast(getApplicationContext(),getResources().getString(R.string.exit),true);
                HelloApp.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        firstTimeBackPress=true;
                    }
                },2000);

            }else {
                finish();
                finishAffinity();
            }
        }else{
            viewPager.setCurrentItem(currentSlide-1);
        }

    }
}
