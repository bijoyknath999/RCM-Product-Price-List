package com.rcm.productpricelist.utils;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.NetworkInfo.State.CONNECTING;
import static android.net.wifi.p2p.WifiP2pDevice.CONNECTED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import com.facebook.ads.Ad;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.activity.Home;
import com.rcm.productpricelist.activity.NewProductActivity;
import com.rcm.productpricelist.activity.ProductActivity;
import com.rcm.productpricelist.activity.RecentActivity;
import com.rcm.productpricelist.activity.Splash;

public class Tools {

    public static InterstitialAd mInterstitialAd;
    public static com.facebook.ads.InterstitialAd FInterstitialAd;
    public static boolean failed;
    public static ReviewManager reviewManager;
    public static ReviewInfo reviewInfo;
    public static boolean isShowing;

    public static String getString(Context context, String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PRE_NAME,Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key,"");
        return value;
    }

    public static void saveString(Context context, String key, String value)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PRE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PRE_NAME,Context.MODE_PRIVATE);
        int value = sharedPreferences.getInt(key,0);
        return value;
    }

    public static void saveInt(Context context, String key, int value)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PRE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void showUpdateDialog(Context context)
    {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Please update the app")
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String packageName = context.getPackageName();
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                });
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connManager.getNetworkCapabilities(connManager.getActiveNetwork());
            if (networkCapabilities == null) {
                return false;
            } else {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            // below Marshmallow
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting() && activeNetwork.isAvailable()) {
                switch (activeNetwork.getState()) {
                    case CONNECTED:
                        return true;
                    case CONNECTING:
                        return true;
                    default:
                        return false;
                }
            } else {
                return false;
            }
        }
    }

    public static void showDialogNoInternet(Context context, String page)
    {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Please check your internet connection...")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isInternetConnected(context)) {
                            dialog.dismiss();
                            if (page.equals("splash")) {
                                context.startActivity(new Intent(context, Splash.class));
                                ((Activity) context).finish();
                            }
                            else if (page.equals("home"))
                                context.startActivity(new Intent(context, Home.class));
                            else if (page.equals("product"))
                                context.startActivity(new Intent(context, ProductActivity.class));
                            else if (page.equals("recent"))
                                context.startActivity(new Intent(context, RecentActivity.class));
                            else if (page.equals("new"))
                                context.startActivity(new Intent(context, NewProductActivity.class));
                            else if (page.equals("single"))
                                Toast.makeText(context, "Please refresh the page...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            showDialogNoInternet(context,page);
                        }
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finishAffinity();
                    }
                })
                .setCancelable(false);
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    public static void loadInterstitialAdfromAdmob(Context context) {

        String interstitialAdmob = Tools.getString(context,"interstitialAdmob");

        String TAG = "Interstitial Admob";

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.load(context ,interstitialAdmob, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.e(TAG, "Interstitial ad Loaded.");
                        getReviewInfo(context);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                        Log.e(TAG, ""+loadAdError.getMessage());
                    }
                });
    }

    public static void showInterstitialAdfromAdmob(Activity activity) {
        if (mInterstitialAd!=null) {
            if (!isShowing)
                mInterstitialAd.show(activity);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isShowing = false;
                    super.onAdDismissedFullScreenContent();
                    int adCounts = Integer.parseInt(Tools.getString(activity,"AdsCount"));
                    int value = Tools.getInt(activity,"click");
                    if (value==adCounts)
                        Tools.saveInt(activity,"click",1);
                    else
                        Tools.saveInt(activity,"click",value+1);

                    startReviewFlow(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Tools.showInterstitialAdfromFacebook(activity);
                    isShowing = false;
                }


                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    isShowing = true;
                }
            });

        }
    }

    public static void loadInterstitialAdromFacebook(Context context) {

        AudienceNetworkAds.initialize(context);

        String interstitialFacebook = Tools.getString(context,"interstitialFacebook");

        String TAG = "Interstitial Facebook";

        FInterstitialAd = new com.facebook.ads.InterstitialAd(context, interstitialFacebook);
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
                isShowing = true;
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                isShowing = false;
                int adCounts = Integer.parseInt(Tools.getString(context,"AdsCount"));
                int value = Tools.getInt(context,"click");
                if (value==adCounts)
                    Tools.saveInt(context,"click",1);
                else
                    Tools.saveInt(context,"click",value+1);

                startReviewFlow((Activity) context);
            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                failed = true;
                isShowing = false;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                failed = false;
                isShowing = false;
                getReviewInfo(context);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        FInterstitialAd.loadAd(
                FInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    public static void showInterstitialAdfromFacebook(Activity activity) {
        if (FInterstitialAd.isAdLoaded()) {
            if (!isShowing)
                FInterstitialAd.show();
        }
        else {
            if (failed) {
                Tools.showInterstitialAdfromAdmob(activity);
            }
        }
    }

    public static void showSnackbar(Context context, View view, String title, String Btntext)
    {
        Snackbar snackbar = Snackbar.make(view,"",Snackbar.LENGTH_LONG);
        View customView = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_snackbar,null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0,0,0,0);

        TextView titleText = customView.findViewById(R.id.custom_snackbar_text);
        Button Btn = customView.findViewById(R.id.custom_snackbar_btn);

        titleText.setText(title);
        Btn.setText(Btntext);

        Btn.setOnClickListener(v -> snackbar.dismiss());

        snackbarLayout.addView(customView,0);
        snackbar.show();
    }

    private static void getReviewInfo(Context context) {
        reviewManager = ReviewManagerFactory.create(context.getApplicationContext());
        Task<ReviewInfo> manager = reviewManager.requestReviewFlow();
        manager.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    reviewInfo = task.getResult();
                }
            }
        });
    }

    public static void startReviewFlow(Activity activity) {
        if (reviewInfo != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        } else {

        }
    }
}
