package com.example.sae302_scanner;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String targetIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_detail); // On lie le XML qu'on vient de faire

        // 1. Récupérer l'IP envoyée par la page d'accueil
        targetIp = getIntent().getStringExtra("TARGET_IP");

        // 2. Mettre à jour le titre
        TextView tvTitle = findViewById(R.id.tvTitleDetail);
        tvTitle.setText("Machine : " + targetIp);

        // 3. Bouton retour
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Ferme l'activité

        // 4. Configurer la liste
        recyclerView = findViewById(R.id.recyclerViewDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 5. Charger les données
        loadData();
    }

    private void loadData() {
        ApiService api = RetrofitClient.getService();
        api.getFailles().enqueue(new Callback<List<Faille>>() {
            @Override
            public void onResponse(Call<List<Faille>> call, Response<List<Faille>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Faille> filteredList = new ArrayList<>();

                    // On filtre : on ne garde que les failles de CETTE IP
                    for (Faille f : response.body()) {
                        if (f.getIp() != null && f.getIp().equals(targetIp)) {
                            filteredList.add(f);
                        }
                    }

                    // On affiche avec le "FailleFullAdapter" (Grosses cartes)
                    recyclerView.setAdapter(new FailleFullAdapter(HostDetailActivity.this, filteredList));
                }
            }

            @Override
            public void onFailure(Call<List<Faille>> call, Throwable t) {
                Toast.makeText(HostDetailActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
}