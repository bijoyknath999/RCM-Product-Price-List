package com.rcm.productpricelist.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.adapters.RecentSinglePageAdapters;
import com.rcm.productpricelist.adapters.SinglePageAdapters;
import com.rcm.productpricelist.api.ApiInterface;
import com.rcm.productpricelist.models.NewProduct;
import com.rcm.productpricelist.models.Product;
import com.rcm.productpricelist.models.Recent;
import com.rcm.productpricelist.utils.ConnectionReceiver;
import com.rcm.productpricelist.utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentSingleProduct extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    private ViewPager2 viewPager2;
    private List<Recent> recentList;
    private List<Recent> recents;
    private RecentSinglePageAdapters recentSinglePageAdapters;
    private int item_limit;
    private TextView ToolbarTitle;
    private ImageView BackImg;
    private int Click = 0,countInt;
    private String adType, adStatus, adCounts, adMobBannerID, FacebookBannerID, interstitialAdmob, interstitialFacebook, nativeAdmob;
    private LinearLayout admobBanner;
    private com.facebook.ads.AdView adView;
    private LinearLayout facebookBanner, MainLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_single_product);

        recentList = new ArrayList<>();
        recents = new ArrayList<>();

        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        int id = Integer.parseInt(getIntent().getStringExtra("id"));
        float newmrp = Float.parseFloat(getIntent().getStringExtra("newmrp"));
        float newsp = Float.parseFloat(getIntent().getStringExtra("newsp"));
        float newpv = Float.parseFloat(getIntent().getStringExtra("newpv"));
        float oldmrp = Float.parseFloat(getIntent().getStringExtra("oldmrp"));
        float oldsp = Float.parseFloat(getIntent().getStringExtra("oldsp"));
        float oldpv = Float.parseFloat(getIntent().getStringExtra("oldpv"));
        item_limit = Integer.parseInt(Tools.getString(this,"underAdsCount"));

        adType = Tools.getString(this,"AdsType");
        adStatus = Tools.getString(this,"AdsStatus");
        adCounts = Tools.getString(this,"AdsCount");
        adMobBannerID = Tools.getString(this,"bannerAdmob");
        FacebookBannerID = Tools.getString(this,"bannerFacebook");
        interstitialAdmob = Tools.getString(this,"interstitialAdmob");
        interstitialFacebook = Tools.getString(this,"interstitialFacebook");
        nativeAdmob = Tools.getString(this,"nativeAdmob");
        Click = Tools.getInt(this,"click");
        countInt = Integer.parseInt(adCounts);

        Recent recent = new Recent(id,title,oldmrp,oldsp,oldpv,newmrp,newsp,newpv,url);

        recentList.add(0,recent);

        viewPager2 = findViewById(R.id.recent_single_product_viewpager);
        ToolbarTitle = findViewById(R.id.recent_single_product_toolbar_title);
        BackImg = findViewById(R.id.recent_single_product_toolbar_back);
        admobBanner = findViewById(R.id.recent_single_product_adView);
        facebookBanner = findViewById(R.id.recent_single_product_facebook_banner);
        MainLayout = findViewById(R.id.recent_single_product_layout);
        progressBar = findViewById(R.id.progressbar);

        if (title!=null)
            ToolbarTitle.setText(title);

        if (adStatus.equals("1"))
        {
            if (adType.equals("1"))
            {
                loadAdMobBanner();
            }
            else if(adType.equals("2"))
            {
                LoadFacebookBanner();
            }
        }


        ApiInterface.getApiRequestInterface().getRecentProduct().enqueue(new Callback<List<Recent>>() {
            @Override
            public void onResponse(Call<List<Recent>> call, Response<List<Recent>> response) {
                if (response.isSuccessful())
                {
                    viewPager2.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recents = response.body();

                    for (int i = 0; i<recents.size(); i++)
                    {
                        if (recents.get(i).getSrNo().equals(id))
                        {
                            recentList = recents.subList(i,recents.size());
                        }
                    }

                    recentSinglePageAdapters = new RecentSinglePageAdapters(RecentSingleProduct.this,recentList,item_limit);
                    viewPager2.setAdapter(recentSinglePageAdapters);
                    recentSinglePageAdapters.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Recent>> call, Throwable t) {

            }
        });


        BackImg.setOnClickListener(v -> onBackPressed());

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if ((position + 1) % item_limit == 0)
                    ToolbarTitle.setText("Ads");
                else
                {
                    int pos = position - Math.round(position / item_limit);
                    ToolbarTitle.setText(recentList.get(pos).getProductName());
                }
            }
        });
    }

    private void LoadFacebookBanner() {
        admobBanner.setVisibility(View.GONE);
        facebookBanner.setVisibility(View.VISIBLE);
        AudienceNetworkAds.initialize(this);
        adView = new com.facebook.ads.AdView(this, FacebookBannerID, AdSize.BANNER_HEIGHT_50);
        facebookBanner.addView(adView);
        com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                loadAdMobBanner();
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    private void loadAdMobBanner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        admobBanner.setVisibility(View.VISIBLE);
        facebookBanner.setVisibility(View.GONE);
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        mAdView.setAdUnitId(adMobBannerID);
        AdRequest adRequest = new AdRequest.Builder().build();
        if(mAdView.getAdSize() != null || mAdView.getAdUnitId() != null)
            mAdView.loadAd(adRequest);

        admobBanner.addView(mAdView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                LoadFacebookBanner();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected,false);
    }

    private void checkConnection(boolean recreate) {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new ConnectionReceiver(), intentFilter);

        // Initialize listener
        ConnectionReceiver.Listener = this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
        showSnackBar(isConnected, recreate);
    }

    private void showSnackBar(boolean isConnected, boolean recreate) {
        if (isConnected)
        {
            if (recreate)
                recreate();
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
                checkConnection(true);
            });

            snackbarLayout.addView(customView,0);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call method
        checkConnection(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection(false);
    }
}