package com.rcm.productpricelist.api;

import android.content.Context;

import com.rcm.productpricelist.utils.Tools;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Path;

public class ApiInterface {

    public static Retrofit retrofit = null, retrofit2 = null;


    public static ApiRequest getApiRequestInterface() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiRequest.class);
    }

    public static ApiRequest getProductApiRequestInterface(Context context) {
        String path = Tools.getString(context, "path");
        if (path!=null)
        {
            if (retrofit2 == null) {
                retrofit2 = new Retrofit.Builder()
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .baseUrl(path)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit2.create(ApiRequest.class);
    }
}