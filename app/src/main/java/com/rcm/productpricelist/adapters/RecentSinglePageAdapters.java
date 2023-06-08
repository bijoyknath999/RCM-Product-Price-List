package com.rcm.productpricelist.adapters;

import android.content.Context;
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
import com.jsibbold.zoomage.ZoomageView;
import com.rcm.productpricelist.R;
import com.rcm.productpricelist.models.Product;
import com.rcm.productpricelist.models.Recent;
import com.rcm.productpricelist.sqlite.FavProductDBController;

import java.util.List;

public class RecentSinglePageAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Recent> recentList;
    private static final int ITEM_VIEW_TYPE_CONTENT = 0;
    private static final int ITEM_VIEW_TYPE_AD = 1;
    private int ITEM_FEED_COUNT = 0;

    public RecentSinglePageAdapters(Context context, List<Recent> recentList, int ITEM_FEED_COUNT) {
        this.context = context;
        this.recentList = recentList;
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
                View contentView = inflater.inflate(R.layout.item_single_recent_product, parent, false);
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
        if (recentList.size() > 0) {
            return recentList.size() + Math.round(recentList.size() / ITEM_FEED_COUNT);
        }
        return recentList.size();
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

        private ImageView imageView;
        private TextView Title, NewMrp, OldMrp, NewSp, OldSp, NewPV, OldPV, PriceIncrease;

        public ContentViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.item_single_recent_product_title);
            imageView = itemView.findViewById(R.id.item_single_recent_product_image);
            NewMrp = itemView.findViewById(R.id.item_single_recent_product_new_mrp);
            OldMrp = itemView.findViewById(R.id.item_single_recent_product_old_mrp);
            NewSp = itemView.findViewById(R.id.item_single_recent_product_new_sp);
            OldSp = itemView.findViewById(R.id.item_single_recent_product_old_sp);
            NewPV = itemView.findViewById(R.id.item_single_recent_product_new_pv);
            OldPV = itemView.findViewById(R.id.item_single_recent_product_old_pv);
            PriceIncrease = itemView.findViewById(R.id.item_single_recent_product_price_increase);
        }

        private void bindData(int position) {
            Recent recent = recentList.get(position);
            if (recent!=null)
            {

                Title.setText(""+recent.getProductName());
                NewMrp.setText("New MRP : ₹"+recent.getNewMrp());
                NewSp.setText("SP : ₹"+recent.getNewSP());
                NewPV.setText("PV: "+recent.getNewPV());
                OldMrp.setText("Old MRP : ₹"+recent.getOldMrp());
                OldSp.setText("SP : ₹"+recent.getOldSP());
                OldPV.setText("PV: ₹"+recent.getOldPV());
                Glide.with(context.getApplicationContext())
                        .load(recent.getImageUrl())
                        .into(imageView);

                if (recent.getNewSP()>recent.getOldSP())
                {
                    float increase = recent.getNewSP()-recent.getOldSP();
                    PriceIncrease.setText("Price Increase : ₹"+increase);
                }
                else
                {
                    float increase = recent.getOldSP()-recent.getNewSP();
                    PriceIncrease.setText("Price Decrease : ₹"+increase);
                }

            }
        }
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout adLayout;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adLayout = itemView.findViewById(R.id.adLayout);
        }

        private void bindAdData() {
            AdLoader.Builder builder = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
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
