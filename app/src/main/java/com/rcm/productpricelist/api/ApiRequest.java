package com.rcm.productpricelist.api;

import com.rcm.productpricelist.BuildConfig;
import com.rcm.productpricelist.models.NewProduct;
import com.rcm.productpricelist.models.Product;
import com.rcm.productpricelist.models.Recent;
import com.rcm.productpricelist.models.Settings;
import com.rcm.productpricelist.models.Slide;
import com.rcm.productpricelist.models.State;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRequest {

    @GET(BuildConfig.setting)
    Call<List<Settings>> getSettings();

    @GET(BuildConfig.slide)
    Call<List<Slide>> getSlides();

    @GET(BuildConfig.states)
    Call<List<State>> getStates();

    @GET("{state}")
    Call<List<Product>> getProductAsState(@Path("state") String state);

    @GET(BuildConfig.recentPrice)
    Call<List<Recent>> getRecentProduct();

    @GET(BuildConfig.newProducts)
    Call<List<NewProduct>> getNewProduct();
}
