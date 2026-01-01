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
    private String targetIp; // L'IP qu'on veut afficher

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_detail); // Définit le contenu de la vue

        // 1. On récupère l'IP transmise par l'activité précédente (MainActivity)
        targetIp = getIntent().getStringExtra("TARGET_IP");

        // 2. On met à jour le titre en haut de la page
        TextView tvTitle = findViewById(R.id.tvTitleDetail);
        tvTitle.setText("Machine : " + targetIp);

        // 3. Gestion du bouton retour (ferme l'activité actuelle)
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 4. Init de la liste (RecyclerView)
        recyclerView = findViewById(R.id.recyclerViewDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 5. On lance la récupération des données
        loadData();
    }

    private void loadData() {
        // Appel API via Retrofit
        ApiService api = RetrofitClient.getService();

        // On récupère TOUTES les failles
        api.getFailles().enqueue(new Callback<List<Faille>>() {
            @Override
            public void onResponse(Call<List<Faille>> call, Response<List<Faille>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Faille> filteredList = new ArrayList<>();

                    // --- FILTRAGE CÔTÉ CLIENT ---
                    // On parcourt tout pour ne garder que les failles de NOTRE IP cible
                    for (Faille f : response.body()) {
                        if (f.getIp() != null && f.getIp().equals(targetIp)) {
                            filteredList.add(f);
                        }
                    }

                    // On passe la liste triée à l'adaptateur pour l'affichage
                    // Note : On utilise FailleFullAdapter ici pour avoir le détail complet
                    recyclerView.setAdapter(new FailleFullAdapter(HostDetailActivity.this, filteredList));
                }
            }

            @Override
            public void onFailure(Call<List<Faille>> call, Throwable t) {
                // Petit message si ça plante serveur down ou pas de 4G
                Toast.makeText(HostDetailActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
}