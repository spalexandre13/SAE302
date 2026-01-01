package com.example.sae302_scanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

// --- LE CŒUR DE LA LISTE : L'ADAPTER ---
// Cette classe fait le pont (Design Pattern "Adapter") entre tes données brutes (List/Map)
// et l'interface visuelle (RecyclerView). C'est elle qui gère l'affichage ligne par ligne.
public class HostAdapter extends RecyclerView.Adapter<HostAdapter.HostViewHolder> {

    // Le Context est nécessaire pour "gonfler" le layout et pour lancer une nouvelle Activité.
    private Context context;

    // La liste des IPs sert à définir l'ORDRE d'affichage et le nombre de lignes.
    private List<String> ipList;

    // La Map sert à récupérer le nombre de failles instantanément (O(1)) pour chaque IP.
    private Map<String, Integer> ipCounts;

    // Constructeur : On reçoit les données depuis le MainActivity
    public HostAdapter(Context context, List<String> ipList, Map<String, Integer> ipCounts) {
        this.context = context;
        this.ipList = ipList;
        this.ipCounts = ipCounts;
    }

    // --- ÉTAPE 1 : LA CRÉATION (Coûteuse en ressources) ---
    // Cette méthode n'est appelée que quelques fois (environ 10-12 fois pour remplir l'écran).
    // Elle "gonfle" (inflate) le fichier XML item_host.xml pour en faire un objet Java manipulable.
    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater : Transforme le XML en View.
        // false : Important ! On dit "Ne l'attache pas tout de suite au parent",
        // c'est le RecyclerView qui décidera quand l'ajouter (pour le recyclage).
        View view = LayoutInflater.from(context).inflate(R.layout.item_host, parent, false);

        // On renvoie un "ViewHolder" qui garde en mémoire les références (les IDs)
        return new HostViewHolder(view);
    }

    // --- ÉTAPE 2 : LE REMPLISSAGE (Rapide et fréquent) ---
    // Cette méthode est appelée à CHAQUE fois qu'une ligne apparaît à l'écran (scroll).
    // On prend une vue existante (holder) et on change juste les textes dedans.
    @Override
    public void onBindViewHolder(@NonNull HostViewHolder holder, int position) {
        // 1. Récupération de la donnée brute à cette position
        String ip = ipList.get(position);
        int count = ipCounts.getOrDefault(ip, 0); // Sécurité : 0 si pas trouvé

        // 2. Mise à jour de l'UI (User Interface)
        if (holder.tvIp != null) holder.tvIp.setText(ip);
        if (holder.tvCount != null) holder.tvCount.setText(count + " Vulnérabilité(s)");

        // 3. GESTION DU CLIC (Navigation)
        // On définit ce qui se passe quand l'utilisateur tape sur CETTE ligne précise.
        holder.itemView.setOnClickListener(v -> {
            // Création de l'Intent explicite pour aller vers la page de détail
            Intent intent = new Intent(context, HostDetailActivity.class);

            // PASSAGE DE PARAMÈTRE : On "colle" l'IP dans le message pour que la page suivante sache qui afficher.
            intent.putExtra("TARGET_IP", ip);

            // Lancement de la nouvelle activité via le Context
            context.startActivity(intent);
        });
    }

    // Indique au RecyclerView combien de lignes il doit prévoir au total.
    @Override
    public int getItemCount() {
        return ipList.size();
    }

    // --- PATTERN VIEWHOLDER (Optimisation mémoire) ---
    // Cette classe interne sert de "Cache".
    // Elle évite de faire des `findViewById` (qui sont lents) à chaque scroll.
    // On le fait une fois à la création, et après on réutilise les variables `tvIp` et `tvCount`.
    public class HostViewHolder extends RecyclerView.ViewHolder {
        TextView tvIp, tvCount;

        public HostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liaison unique avec le XML
            tvIp = itemView.findViewById(R.id.tvHostIp);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }
}