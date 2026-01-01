package com.example.sae302_scanner;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // La liste déroulante
    private HostAdapter adapter; // Celui qui rempli la liste

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On lie avec le bon ID défini dans le XML juste au-dessus
        recyclerView = findViewById(R.id.recyclerViewHosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // On lance la récupération des données
        fetchAndGroupFailles();
    }

    private void fetchAndGroupFailles() {
        ApiService apiService = RetrofitClient.getService(); // ON récupère le serveur
        Call<List<Faille>> call = apiService.getFailles(); // Attends une liste contenant les failles
        // enqueue lance la commande en arrière plan
        call.enqueue(new Callback<List<Faille>>() {
            @Override
            public void onResponse(Call<List<Faille>> call, Response<List<Faille>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Faille> allFailles = response.body();

                    // Logique de regroupement par IP
                    List<String> uniqueIps = new ArrayList<>();
                    Map<String, Integer> ipCounts = new HashMap<>();

                    for (Faille f : allFailles) {
                        String ip = f.getIp();
                        if (ip == null || ip.isEmpty()) continue;

                        ipCounts.put(ip, ipCounts.getOrDefault(ip, 0) + 1);

                        if (!uniqueIps.contains(ip)) {
                            uniqueIps.add(ip);
                        }
                    }

                    adapter = new HostAdapter(MainActivity.this, uniqueIps, ipCounts);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Faille>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur connexion : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}