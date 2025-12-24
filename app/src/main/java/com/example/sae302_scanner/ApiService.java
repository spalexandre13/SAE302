package com.example.sae302_scanner;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // C'est ici qu'on dit d'aller chercher le fichier PHP
    // L'URL finale sera : http://192.168.204.128/failles.php
    @GET("api_failles.php")
    Call<List<Faille>> getFailles();
}