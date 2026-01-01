package com.example.sae302_scanner;

import com.google.gson.annotations.SerializedName;

public class Faille {


    private String id;
    private String nom;
    private String description;
    private String ip;
    private String severite;
    private String source;
    private String dateDetection;

    // --- GETTERS (Pour récupérer les infos) ---

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public String getIp() { return ip; }
    public String getSeverite() { return severite; }
    public String getSource() { return source; }
    public String getDateDetection() { return dateDetection; }
}