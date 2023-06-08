package com.rcm.productpricelist.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rcm.productpricelist.MainActivity;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.adapters.StateAdapters;
import com.rcm.productpricelist.api.ApiInterface;
import com.rcm.productpricelist.models.Slide;
import com.rcm.productpricelist.models.State;
import com.rcm.productpricelist.utils.ConnectionReceiver;
import com.rcm.productpricelist.utils.PermissionUtil;
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

public class Home extends AppCompatActivity implements PermissionUtil.PermissionsCallBack, ConnectionReceiver.ReceiverListener {

    private RecyclerView StateRecyclerview;
    public static LinearLayout HomeStateLayout, HomeMainLayout;
    private LinearLayoutManager linearLayoutManager;
    private List<State> stateList;
    private StateAdapters stateAdapters;
    public static String state_url, state;
    private ImageSlider imageSlider;
    private List<SlideModel> imageSliderList;
    private CardView PriceListCard, RecentProduct, NewProductsCard, FavouritesCard, StoreCard, OtherAppsCard;
    public static TextView StateText;
    private LinearLayout MainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        stateList = new ArrayList<>();
        imageSliderList = new ArrayList<SlideModel>();

        state_url = Tools.getString(this,"state_url");
        state = Tools.getString(this,"state");


        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        StateRecyclerview = findViewById(R.id.home_state_recyclerview);
        HomeStateLayout = findViewById(R.id.home_state_layout);
        imageSlider = findViewById(R.id.home_image_slider);
        PriceListCard = findViewById(R.id.home_price_list);
        HomeMainLayout = findViewById(R.id.home_main_layout);
        RecentProduct = findViewById(R.id.home_recent_product);
        NewProductsCard = findViewById(R.id.home_new_products);
        FavouritesCard = findViewById(R.id.home_favourites);
        StoreCard = findViewById(R.id.home_rcm_store);
        OtherAppsCard = findViewById(R.id.home_other_apps);
        StateText = findViewById(R.id.home_state_text);
        MainLayout = findViewById(R.id.home_layout);

        setSupportActionBar(toolbar);

        requestPermissions();

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        StateRecyclerview.setLayoutManager(linearLayoutManager);
        StateRecyclerview.setHasFixedSize(true);
        stateAdapters = new StateAdapters(this,stateList);
        StateRecyclerview.setAdapter(stateAdapters);

        if (!state_url.isEmpty()) {
            StateText.setText(""+state);
            HomeStateLayout.setVisibility(View.GONE);
            HomeMainLayout.setVisibility(View.VISIBLE);
        }
        else {
            LoadStates();
        }


       /* imageSliderList.add(new SlideModel("https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072821_960_720.jpg",ScaleTypes.FIT));
        imageSliderList.add(new SlideModel("https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072821_960_720.jpg",ScaleTypes.FIT));
        imageSliderList.add(new SlideModel("https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072821_960_720.jpg",ScaleTypes.FIT));
        imageSlider.setImageList(imageSliderList);*/

        ApiInterface.getApiRequestInterface().getSlides().enqueue(new Callback<List<Slide>>() {
            @Override
            public void onResponse(Call<List<Slide>> call, Response<List<Slide>> response) {
                if (response.isSuccessful())
                {
                    List<Slide> slides = response.body();


                    for (Slide slide : slides)
                    {
                        imageSliderList.add(new SlideModel(slide.getImage(),ScaleTypes.CENTER_CROP));
                    }
                    imageSlider.setImageList(imageSliderList);
                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            String url = slides.get(i).getUrl();
                            if (url!=null)
                            {
                                Intent Slideintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(Slideintent);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Slide>> call, Throwable t) {

            }
        });


        PriceListCard.setOnClickListener(v -> startActivity(new Intent(this,ProductActivity.class)));
        RecentProduct.setOnClickListener(v -> startActivity(new Intent(this,RecentActivity.class)));
        NewProductsCard.setOnClickListener(v -> startActivity(new Intent(this,NewProductActivity.class)));
        FavouritesCard.setOnClickListener(v -> startActivity(new Intent(this,FavActivity.class)));
        StoreCard.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://store.jayrcm.com/"));
            startActivity(intent);
        });
        OtherAppsCard.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8398019413364187771"));
            startActivity(intent);
        });

    }

    private void LoadStates() {
        ApiInterface.getApiRequestInterface().getStates().enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, Response<List<State>> response) {
                if (response.isSuccessful())
                {
                    List<State> states = response.body();
                    stateList.addAll(states);
                    stateAdapters.notifyDataSetChanged();
                    HomeStateLayout.setVisibility(View.VISIBLE);
                    HomeMainLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<State>> call, Throwable t) {

            }
        });
    }

    public static void checkState(Context context)
    {
        state_url = Tools.getString(context,"state_url");
        state = Tools.getString(context,"state");


        if (!state_url.isEmpty()) {
            StateText.setText(""+state);
            HomeStateLayout.setVisibility(View.GONE);
            HomeMainLayout.setVisibility(View.VISIBLE);
        }
        else {
            HomeStateLayout.setVisibility(View.VISIBLE);
            HomeMainLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                HomeStateLayout.setVisibility(View.VISIBLE);
                HomeMainLayout.setVisibility(View.GONE);
                Tools.saveString(this,"state","");
                Tools.saveString(this,"state_url","");
                LoadStates();
                return true;
            case R.id.store:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://store.jayrcm.com/"));
                startActivity(intent);
                return true;
            case R.id.rate_us:
                String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            case R.id.disclaimer:
                Intent disIntent = new Intent(Home.this, WebActivity.class);
                disIntent.putExtra("url","https://www.jayrcm.com/disclaimer");
                startActivity(disIntent);
                return true;
            case R.id.privacy_policy:
                Intent priIntent = new Intent(Home.this, WebActivity.class);
                priIntent.putExtra("url","https://www.jayrcm.com/privacy-policy");
                startActivity(priIntent);
                return true;
            case R.id.terms_conditions:
                Intent termIntent = new Intent(Home.this, WebActivity.class);
                termIntent.putExtra("url","https://www.jayrcm.com/terms-conditions");
                startActivity(termIntent);
                return true;
            case R.id.contact_us:
                Intent contactIntent = new Intent(Intent.ACTION_SENDTO);
                contactIntent.setData(Uri.parse("mailto:app@jayrcm.com"));
                contactIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
                startActivity(Intent.createChooser(contactIntent, "Send Email"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        showExitDialog();

        /*if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }*/

        /*this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 4000);*/
    }

    private void showExitDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.custom_sheet_dialog);
        Button rateusBtn = dialog.findViewById(R.id.modal_sheet_rate_us);
        Button shareBtn = dialog.findViewById(R.id.modal_sheet_share);
        Button quitBtn = dialog.findViewById(R.id.modal_sheet_quit);

        quitBtn.setOnClickListener(v -> {
            finishAffinity();
        });

        rateusBtn.setOnClickListener(v -> {
            String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        shareBtn.setOnClickListener(v -> {
            String appPackageName = getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share using"));
        });

        if (dialog.isShowing())
            dialog.dismiss();
        else
            dialog.show();
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.checkAndRequestPermissions(this,
                    Manifest.permission.POST_NOTIFICATIONS)) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    @Override
    public void permissionsGranted() {
    }

    @Override
    public void permissionsDenied() {
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