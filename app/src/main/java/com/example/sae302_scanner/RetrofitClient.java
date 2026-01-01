package com.example.sae302_scanner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {


    // Ne pas oublier pas le '/' à la fin, c'est obligatoire pour Retrofit !
    private static final String BASE_URL = "http://192.168.204.128/";

    private static Retrofit retrofit = null;

    public static ApiService getService() {
        if (retrofit == null) {
            // Création de l'instance Retrofit (Singleton)
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Pour convertir le JSON en objets Java
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}