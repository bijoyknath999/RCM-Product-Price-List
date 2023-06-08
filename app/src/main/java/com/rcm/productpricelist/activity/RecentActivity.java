package com.rcm.productpricelist.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.adapters.RecentProductAdapters;
import com.rcm.productpricelist.adapters.SearchAdapters;
import com.rcm.productpricelist.api.ApiInterface;
import com.rcm.productpricelist.models.Recent;
import com.rcm.productpricelist.sqlite.SearchModels;
import com.rcm.productpricelist.sqlite.SearchProductDBController;
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

public class RecentActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    private GridLayoutManager gridLayoutManager;
    public static RecyclerView recyclerView, searchRecyclerview;
    public static List<Recent> recentList;
    public static RecentProductAdapters recentProductAdapters;
    private LinearLayout MainLayout;
    private int Click = 0,countInt;
    private String adType, adStatus, adCounts, adMobBannerID, FacebookBannerID, interstitialAdmob, interstitialFacebook, nativeAdmob;
    private LinearLayout admobBanner;
    private com.facebook.ads.AdView adView;
    private LinearLayout facebookBanner;
    private ProgressBar progressBar;
    private TextView ToolbarTitle;
    private LinearLayout SearchLayout, ToolbarLayout;
    private ImageView SearchBack, SearchBtn, SearchClear;
    public static EditText SearchInput;
    private SearchAdapters searchAdapters;
    private List<SearchModels> searchModelsList;
    private SearchProductDBController searchProductDBController;
    private final long DELAY = 2000;
    private boolean running = false;
    private boolean notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_product);

        recyclerView = findViewById(R.id.recent_product_recyclerview);
        MainLayout = findViewById(R.id.recent_product_main_layout);
        admobBanner = findViewById(R.id.recent_product_adView);
        facebookBanner = findViewById(R.id.recent_product_facebook_banner);
        progressBar = findViewById(R.id.progressbar);
        ToolbarTitle = findViewById(R.id.recent_product_toolbar_title);
        SearchLayout = findViewById(R.id.recent_product_search_layout);
        ToolbarLayout = findViewById(R.id.recent_product_toolbar_layout);
        SearchBack = findViewById(R.id.recent_product_search_back);
        SearchInput = findViewById(R.id.recent_product_search_edit);
        SearchBtn = findViewById(R.id.recent_product_search_btn);
        SearchClear = findViewById(R.id.recent_product_search_clear);
        searchRecyclerview = findViewById(R.id.recent_product_search_history_recyclerview);
        notify = getIntent().getBooleanExtra("notify",false);


        recentList = new ArrayList<>();
        searchModelsList = new ArrayList<>();

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

        SearchBtn.setOnClickListener(v -> {
            loadSearchHistoryData();
            ToolbarLayout.setVisibility(View.GONE);
            SearchLayout.setVisibility(View.VISIBLE);
            showSoftKeyboard(SearchInput);
        });

        SearchBack.setOnClickListener(v -> {
            ToolbarLayout.setVisibility(View.VISIBLE);
            SearchLayout.setVisibility(View.GONE);
            SearchInput.setText(null);
            hideSoftKeyboard(SearchInput, this);
        });

        SearchClear.setOnClickListener(v ->
        {
            SearchInput.setText(null);
            filter("");
            loadSearchHistoryData();
        });


        SearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                searchRecyclerview.setVisibility(View.GONE);

                if (!running)
                {
                    running = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!s.toString().isEmpty())
                                searchProductDBController.insertData(s.toString());

                            running = false;
                        }
                    },DELAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recentProductAdapters = new RecentProductAdapters(this,recentList,"https://team.jayrcm.com/PriceListApp/recentPrice.json");
        recyclerView.setAdapter(recentProductAdapters);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        searchRecyclerview.setLayoutManager(linearLayoutManager);

        LoadData();
    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideSoftKeyboard(View view, Context context){
        InputMethodManager imm =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loadSearchHistoryData() {
        if (searchModelsList.size()>0)
            searchModelsList.clear();

        searchProductDBController = new SearchProductDBController(this);
        searchModelsList = searchProductDBController.getAllData();
        if (searchModelsList.size()>0)
        {
            searchAdapters = new SearchAdapters(searchModelsList,this,"recent");
            searchRecyclerview.setAdapter(searchAdapters);
            searchAdapters.notifyDataSetChanged();
            searchRecyclerview.setVisibility(View.VISIBLE);
        }
        else
            searchRecyclerview.setVisibility(View.GONE);
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

    private void LoadData() {
        ApiInterface.getApiRequestInterface().getRecentProduct().enqueue(new Callback<List<Recent>>() {
            @Override
            public void onResponse(Call<List<Recent>> call, Response<List<Recent>> response) {
                if (response.isSuccessful())
                {
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    List<Recent> recents = response.body();
                    recentList.addAll(recents);

                    if (recentList.size()<=0)
                        Tools.showSnackbar(RecentActivity.this,MainLayout,"No Products Found...","Okay");

                    recentProductAdapters.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Recent>> call, Throwable t) {

            }
        });
    }

    public static void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Recent> filteredlist = new ArrayList<Recent>();

        // running a for loop to compare elements.
        for (Recent item : recentList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getProductName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            recentProductAdapters.filterList(filteredlist);
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
        if (SearchLayout.getVisibility()==View.VISIBLE)
        {
            ToolbarLayout.setVisibility(View.VISIBLE);
            SearchLayout.setVisibility(View.GONE);
            SearchInput.setText(null);
            hideSoftKeyboard(SearchInput,this);
        }
        else
        {
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
            if (notify)
                startActivity(new Intent(RecentActivity.this, Home.class));
            else
                super.onBackPressed();
        }
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
    }

    public static void SelectSearch(String searchText, Context context)
    {
        SearchInput.setText(searchText);
        searchRecyclerview.setVisibility(View.GONE);
        hideSoftKeyboard(SearchInput, context);
        filter(searchText);
    }

}