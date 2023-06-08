package com.rcm.productpricelist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("maintenance")
    @Expose
    private String maintenance;
    @SerializedName("AdsStatus")
    @Expose
    private String adsStatus;
    @SerializedName("AdsType")
    @Expose
    private String adsType;
    @SerializedName("AdsCount")
    @Expose
    private String adsCount;
    @SerializedName("underAdsCount")
    @Expose
    private String underAdsCount;
    @SerializedName("bannerAdmob")
    @Expose
    private String bannerAdmob;
    @SerializedName("interstitialAdmob")
    @Expose
    private String interstitialAdmob;
    @SerializedName("nativeAdmob")
    @Expose
    private String nativeAdmob;
    @SerializedName("rewardAdmob")
    @Expose
    private String rewardAdmob;
    @SerializedName("bannerFacebook")
    @Expose
    private String bannerFacebook;
    @SerializedName("interstitialFacebook")
    @Expose
    private String interstitialFacebook;
    @SerializedName("rewardFacebook")
    @Expose
    private String rewardFacebook;
    @SerializedName("path")
    @Expose
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(String maintenance) {
        this.maintenance = maintenance;
    }

    public String getAdsStatus() {
        return adsStatus;
    }

    public void setAdsStatus(String adsStatus) {
        this.adsStatus = adsStatus;
    }

    public String getAdsType() {
        return adsType;
    }

    public void setAdsType(String adsType) {
        this.adsType = adsType;
    }

    public String getAdsCount() {
        return adsCount;
    }

    public void setAdsCount(String adsCount) {
        this.adsCount = adsCount;
    }

    public String getUnderAdsCount() {
        return underAdsCount;
    }

    public void setUnderAdsCount(String underAdsCount) {
        this.underAdsCount = underAdsCount;
    }

    public String getBannerAdmob() {
        return bannerAdmob;
    }

    public void setBannerAdmob(String bannerAdmob) {
        this.bannerAdmob = bannerAdmob;
    }

    public String getInterstitialAdmob() {
        return interstitialAdmob;
    }

    public void setInterstitialAdmob(String interstitialAdmob) {
        this.interstitialAdmob = interstitialAdmob;
    }

    public String getNativeAdmob() {
        return nativeAdmob;
    }

    public void setNativeAdmob(String nativeAdmob) {
        this.nativeAdmob = nativeAdmob;
    }

    public String getRewardAdmob() {
        return rewardAdmob;
    }

    public void setRewardAdmob(String rewardAdmob) {
        this.rewardAdmob = rewardAdmob;
    }

    public String getBannerFacebook() {
        return bannerFacebook;
    }

    public void setBannerFacebook(String bannerFacebook) {
        this.bannerFacebook = bannerFacebook;
    }

    public String getInterstitialFacebook() {
        return interstitialFacebook;
    }

    public void setInterstitialFacebook(String interstitialFacebook) {
        this.interstitialFacebook = interstitialFacebook;
    }

    public String getRewardFacebook() {
        return rewardFacebook;
    }

    public void setRewardFacebook(String rewardFacebook) {
        this.rewardFacebook = rewardFacebook;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
