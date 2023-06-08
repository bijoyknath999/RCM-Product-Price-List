package com.rcm.productpricelist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewProduct {

    @SerializedName("SrNo")
    @Expose
    private Integer srNo;

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

}