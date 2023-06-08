package com.rcm.productpricelist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("SrNo")
    @Expose
    private Integer srNo;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Mrp")
    @Expose
    private float mrp;
    @SerializedName("SalePrice")
    @Expose
    private float salePrice;
    @SerializedName("Pv")
    @Expose
    private float pv;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;

    public Product(Integer srNo, String name, float mrp, float salePrice, float pv, String imageUrl) {
        this.srNo = srNo;
        this.name = name;
        this.mrp = mrp;
        this.salePrice = salePrice;
        this.pv = pv;
        this.imageUrl = imageUrl;
    }

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMrp() {
        return mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public float getPv() {
        return pv;
    }

    public void setPv(float pv) {
        this.pv = pv;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}