package com.rcm.productpricelist.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.api.ApiInterface;
import com.rcm.productpricelist.models.Settings;
import com.rcm.productpricelist.models.State;
import com.rcm.productpricelist.utils.ConnectionReceiver;
import com.rcm.productpricelist.utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {

    private String versionCode = "0";
    private int versionCodeInt = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    private RelativeLayout MainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MainLayout = findViewById(R.id.splash_layout);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("plistnew")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        loadData();
    }

    private void loadData() {
        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (isConnected)
        {
            ApiInterface.getApiRequestInterface().getSettings().enqueue(new Callback<List<Settings>>() {
                @Override
                public void onResponse(Call<List<Settings>> call, Response<List<Settings>> response) {
                    if (response.isSuccessful())
                    {
                        List<Settings> settingsList = response.body();
                        Settings settings = settingsList.get(0);
                        Tools.saveString(Splash.this,"id",settings.getId());
                        Tools.saveString(Splash.this,"version",settings.getVersion());
                        Tools.saveString(Splash.this,"AdsStatus",settings.getAdsStatus());
                        Tools.saveString(Splash.this,"AdsType",settings.getAdsType());
                        Tools.saveString(Splash.this,"AdsCount",settings.getAdsCount());
                        Tools.saveString(Splash.this,"underAdsCount",settings.getUnderAdsCount());
                        Tools.saveString(Splash.this,"bannerAdmob",settings.getBannerAdmob());
                        Tools.saveString(Splash.this,"interstitialAdmob",settings.getInterstitialAdmob());
                        Tools.saveString(Splash.this,"nativeAdmob",settings.getNativeAdmob());
                        Tools.saveString(Splash.this,"bannerFacebook",settings.getBannerFacebook());
                        Tools.saveString(Splash.this,"interstitialFacebook",settings.getInterstitialFacebook());
                        Tools.saveString(Splash.this,"path",settings.getPath());

                        versionCodeInt = Integer.parseInt(versionCode);
                        if (versionCode.equals(settings.getVersion()) || versionCodeInt>Integer.parseInt(settings.getVersion()))
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Splash.this,Home.class));
                                    finish();
                                    finishAffinity();
                                }
                            },2000);

                        }
                        else
                        {
                            if (!versionCode.equals("0"))
                            {
                                Tools.showUpdateDialog(Splash.this);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Settings>> call, Throwable t) {

                }
            });
        }
        else
        {
            Snackbar snackbar = Snackbar.make(MainLayout,"",Snackbar.LENGTH_INDEFINITE);
            View customView = getLayoutInflater().inflate(R.layout.custom_snackbar,null);
            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);

            TextView titleText = customView.findViewById(R.id.custom_snackbar_text);
            Button Btn = customView.findViewById(R.id.custom_snackbar_btn);

            titleText.setText("Not Connected to Internet");
            Btn.setText("Retry");

            Btn.setOnClickListener(v ->
                    {
                        snackbar.dismiss();
                        loadData();
                    }
            );

            snackbarLayout.addView(customView,0);
            snackbar.show();
        }
    }
}