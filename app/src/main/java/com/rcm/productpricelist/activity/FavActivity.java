package com.rcm.productpricelist.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.adapters.FavProductAdapters;
import com.rcm.productpricelist.adapters.ProductAdapters;
import com.rcm.productpricelist.api.ApiInterface;
import com.rcm.productpricelist.models.NewProduct;
import com.rcm.productpricelist.models.Product;
import com.rcm.productpricelist.sqlite.FavModels;
import com.rcm.productpricelist.sqlite.FavProductDBController;
import com.rcm.productpricelist.utils.ConnectionReceiver;
import com.rcm.productpricelist.utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    private List<Product> productList;
    private List<FavModels> favModelsList;
    private FavProductAdapters productAdapters;
    private RecyclerView recyclerView;
    private String state_url, path;
    private TextView ClearAll;
    private LinearLayout MainLayout;
    private int Click = 0,countInt;
    private String adType, adStatus, adCounts, adMobBannerID, FacebookBannerID, interstitialAdmob, interstitialFacebook, nativeAdmob;
    private LinearLayout admobBanner;
    private com.facebook.ads.AdView adView;
    private LinearLayout facebookBanner;
    private FavProductDBController favProductDBController;
    private ProgressBar progressBar;
    private LinearLayout NoErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        recyclerView = findViewById(R.id.fav_product_recyclerview);
        ClearAll = findViewById(R.id.fav_product_toolbar_clear_all);
        MainLayout = findViewById(R.id.fav_product_main_layout);
        admobBanner = findViewById(R.id.fav_product_adView);
        facebookBanner = findViewById(R.id.fav_product_facebook_banner);
        progressBar = findViewById(R.id.progressbar);
        NoErrorLayout = findViewById(R.id.fav_product_no_error);

        productList = new ArrayList<>();
        favModelsList = new ArrayList<>();

        favProductDBController = new FavProductDBController(this);

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

        state_url = Tools.getString(this, "state_url");
        path = Tools.getString(this, "path");

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        productAdapters = new FavProductAdapters(this,productList, path +""+ state_url,"fav");
        recyclerView.setAdapter(productAdapters);

        productAdapters.setItemClickListener((position, view) -> {
            Product product = productList.get(position);
            favProductDBController.deleteFav(product.getSrNo().toString(),FavActivity.this);
            if (productList.size()>0)
                productList.clear();
            if (favModelsList.size()>0)
                favModelsList.clear();

            Loaddata();
        });

        ClearAll.setOnClickListener(v ->
        {
            showDialog();
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

    private void showDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to clear all...")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavProductDBController favProductDBController = new FavProductDBController(FavActivity.this);
                        favProductDBController.deleteAllFav();
                        if (productList.size()>0)
                            productList.clear();
                        if (favModelsList.size()>0)
                            favModelsList.clear();

                        Loaddata();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    private void Loaddata() {
        if (state_url !=null && path !=null)
        {
            ApiInterface.getProductApiRequestInterface(this).getProductAsState(state_url)
                    .enqueue(new Callback<List<Product>>() {
                        @Override
                        public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                            if (response.isSuccessful())
                            {
                                recyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                NoErrorLayout.setVisibility(View.GONE);
                                List<Product> products = response.body();
                                favModelsList = favProductDBController.getAllData();
                                for (FavModels favModel : favModelsList)
                                {
                                    for (Product product : products)
                                    {
                                        if (favModel.getSlno()==product.getSrNo())
                                        {
                                            productList.add(product);
                                        }
                                    }
                                }
                                productAdapters.notifyDataSetChanged();

                                if (productList.size()>0) {
                                    ClearAll.setVisibility(View.VISIBLE);
                                } else {
                                    NoErrorLayout.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    ClearAll.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Product>> call, Throwable t) {

                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (adStatus.equals("1"))
        {
            if (adType.equals("1"))
            {
                Click = Tools.getInt(this,"click");

                if (Click==0){
                    Tools.showInterstitialAdfromAdmob(this);
                    Tools.loadInterstitialAdfromAdmob(this);
                    Tools.loadInterstitialAdromFacebook(this);
                }
                else if (Click==countInt)
                {
                    Tools.showInterstitialAdfromAdmob(this);
                    Tools.loadInterstitialAdfromAdmob(this);
                    Tools.loadInterstitialAdromFacebook(this);
                }
                else
                {
                    int value = Tools.getInt(this,"click");
                    Tools.saveInt(this,"click",1+value);
                }
            }
            else if(adType.equals("2"))
            {
                Click = Tools.getInt(this,"click");

                if (Click==0){
                    Tools.showInterstitialAdfromFacebook(this);
                    Tools.loadInterstitialAdfromAdmob(this);
                    Tools.loadInterstitialAdromFacebook(this);
                }
                else if (Click==countInt)
                {
                    Tools.showInterstitialAdfromAdmob(this);
                    Tools.loadInterstitialAdfromAdmob(this);
                    Tools.loadInterstitialAdromFacebook(this);
                }
                else
                {
                    int value = Tools.getInt(this,"click");
                    Tools.saveInt(this,"click",1+value);
                }
            }
        }
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
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adStatus.equals("1"))
        {
            Tools.loadInterstitialAdfromAdmob(this);
            Tools.loadInterstitialAdromFacebook(this);
        }
        checkConnection(false);


        if (productList.size()>0)
            productList.clear();
        if (favModelsList.size()>0)
            favModelsList.clear();

        Loaddata();

    }
}