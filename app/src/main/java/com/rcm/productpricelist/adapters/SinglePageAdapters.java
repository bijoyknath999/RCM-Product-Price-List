package com.rcm.productpricelist.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.snackbar.Snackbar;
import com.jsibbold.zoomage.ZoomageView;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.activity.SingleProduct;
import com.rcm.productpricelist.models.Product;
import com.rcm.productpricelist.sqlite.FavProductDBController;
import com.rcm.productpricelist.utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SinglePageAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private static final int ITEM_VIEW_TYPE_CONTENT = 0;
    private static final int ITEM_VIEW_TYPE_AD = 1;
    private int ITEM_FEED_COUNT = 0;

    public SinglePageAdapters(Context context, List<Product> productList,int ITEM_FEED_COUNT) {
        this.context = context;
        this.productList = productList;
        this.ITEM_FEED_COUNT = ITEM_FEED_COUNT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_VIEW_TYPE_AD:
                View adView = inflater.inflate(R.layout.layout_ad, parent, false);
                return new AdViewHolder(adView);
            case ITEM_VIEW_TYPE_CONTENT:
            default:
                View contentView = inflater.inflate(R.layout.item_single_product, parent, false);
                return new ContentViewHolder(contentView);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_VIEW_TYPE_AD:
                ((AdViewHolder) holder).bindAdData();
                break;
            case ITEM_VIEW_TYPE_CONTENT:
            default:
                int pos = position - Math.round(position / ITEM_FEED_COUNT);
                ((ContentViewHolder) holder).bindData(pos);
                break;
        }
    }

    // Override getItemViewType to determine the view type for each item
    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % ITEM_FEED_COUNT == 0) {
            return ITEM_VIEW_TYPE_AD;
        }
        return ITEM_VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        if (productList.size() > 0) {
            return productList.size() + Math.round(productList.size() / ITEM_FEED_COUNT);
        }
        return productList.size();
    }

    private void populateNativeADView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.GONE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {

        private TextView MRPText, SPText, PVText, TitleText;
        private ZoomageView zoomageView;
        private Button FavBtn;
        private Button ShareBtn;

        public ContentViewHolder(View itemView) {
            super(itemView);
            zoomageView = itemView.findViewById(R.id.item_single_product_image);
            MRPText = itemView.findViewById(R.id.item_single_product_mrp);
            SPText = itemView.findViewById(R.id.item_single_product_saleprice);
            PVText = itemView.findViewById(R.id.item_single_product_pv);
            TitleText = itemView.findViewById(R.id.item_single_product_title);
            FavBtn = itemView.findViewById(R.id.item_single_product_fav);
            ShareBtn = itemView.findViewById(R.id.item_single_product_share);
        }

        private void bindData(int position) {

            Product product = productList.get(position);
            if (product != null) {
                Glide.with(context.getApplicationContext())
                        .load(product.getImageUrl())
                        .into(zoomageView);

                TitleText.setText(product.getName());
                MRPText.setText("MRP : ₹" + product.getMrp());
                SPText.setText("SP : ₹" + product.getSalePrice());
                PVText.setText("PV : " + product.getPv());

                FavProductDBController favProductDBController = new FavProductDBController(context);

                if (favProductDBController.checkFav(product.getSrNo().toString()))
                    FavBtn.setText("Remove from Favourite");
                else
                    FavBtn.setText("Add to Favourite");

                FavBtn.setOnClickListener(v -> {
                    if (favProductDBController.checkFav(product.getSrNo().toString())) {
                        favProductDBController.deleteFav(product.getSrNo().toString(), context);
                        FavBtn.setText("Add to Favourite");
                    } else {
                        favProductDBController.insertData(product.getSrNo());
                        FavBtn.setText("Remove from Favourite");
                    }
                });

                ShareBtn.setOnClickListener(v -> {

                    Snackbar snackbar = Snackbar.make(v,"",Snackbar.LENGTH_LONG);
                    View customView = LayoutInflater.from(context).inflate(R.layout.custom_snackbar,null);
                    snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
                    Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                    snackbarLayout.setPadding(0,0,0,0);

                    TextView titleText = customView.findViewById(R.id.custom_snackbar_text);
                    Button Btn = customView.findViewById(R.id.custom_snackbar_btn);

                    titleText.setText("Please wait, Image Downloading");
                    Btn.setText("Okay");

                    Btn.setOnClickListener(v2 ->
                    {
                        snackbar.dismiss();
                    });

                    snackbarLayout.addView(customView,0);
                    snackbar.show();

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    String imageUrl = ""+product.getImageUrl();
                    String title = ""+product.getName();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");

                    Bitmap bitmap = null;
                    try {
                        URL url = new URL(imageUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Uri imageUri = null;
                    if (bitmap != null) {
                        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, null);
                        imageUri = Uri.parse(path);
                    }
                    String appPackageName = context.getPackageName();
                    String fullText = title+"\nMRP : ₹"+product.getMrp()+"\nSP : ₹"+product.getSalePrice()+"\nPV : "+product.getPv()+"\n\n\nDownload "+context.getResources().getString(R.string.app_name)+" App : https://play.google.com/store/apps/details?id=" + appPackageName;
                    shareIntent.putExtra(Intent.EXTRA_TEXT,fullText);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                });
            }
        }
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout adLayout;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adLayout = itemView.findViewById(R.id.adLayout);
        }

        private void bindAdData() {
            String nativeAdmob = Tools.getString(context,"nativeAdmob");
            AdLoader.Builder builder = new AdLoader.Builder(context, nativeAdmob)
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.item_native_ad, null);
                            populateNativeADView(nativeAd, nativeAdView);
                            adLayout.removeAllViews();
                            adLayout.addView(nativeAdView);
                        }
                    });

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Toast.makeText(context, loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }
}