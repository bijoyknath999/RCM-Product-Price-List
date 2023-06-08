package com.rcm.productpricelist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recent {

    @SerializedName("SrNo")
    @Expose
    private Integer srNo;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("oldMrp")
    @Expose
    private float oldMrp;
    @SerializedName("oldSP")
    @Expose
    private float oldSP;
    @SerializedName("OldPV")
    @Expose
    private float oldPV;
    @SerializedName("newMrp")
    @Expose
    private float newMrp;
    @SerializedName("newSP")
    @Expose
    private float newSP;
    @SerializedName("newPV")
    @Expose
    private float newPV;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;

    public Recent(Integer srNo, String productName, float oldMrp, float oldSP, float oldPV, float newMrp, float newSP, float newPV, String imageUrl) {
        this.srNo = srNo;
        this.productName = productName;
        this.oldMrp = oldMrp;
        this.oldSP = oldSP;
        this.oldPV = oldPV;
        this.newMrp = newMrp;
        this.newSP = newSP;
        this.newPV = newPV;
        this.imageUrl = imageUrl;
    }

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getOldMrp() {
        return oldMrp;
    }

    public void setOldMrp(float oldMrp) {
        this.oldMrp = oldMrp;
    }

    public float getOldSP() {
        return oldSP;
    }

    public void setOldSP(float oldSP) {
        this.oldSP = oldSP;
    }

    public float getOldPV() {
        return oldPV;
    }

    public void setOldPV(float oldPV) {
        this.oldPV = oldPV;
    }

    public float getNewMrp() {
        return newMrp;
    }

    public void setNewMrp(float newMrp) {
        this.newMrp = newMrp;
    }

    public float getNewSP() {
        return newSP;
    }

    public void setNewSP(float newSP) {
        this.newSP = newSP;
    }

    public float getNewPV() {
        return newPV;
    }

    public void setNewPV(float newPV) {
        this.newPV = newPV;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}