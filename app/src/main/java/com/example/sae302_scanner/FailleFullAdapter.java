package com.example.sae302_scanner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// ADAPTER "DÉTAIL" : C'est celui qui affiche les grosses cartes avec la couleur (Rouge/Orange/Vert).
// Il est utilisé dans HostDetailActivity.
public class FailleFullAdapter extends RecyclerView.Adapter<FailleFullAdapter.ViewHolder> {

    private Context context; // Nécessaire pour récupérer les couleurs (R.color...)
    private List<Faille> list; // La liste des failles filtrées pour UNE machine

    public FailleFullAdapter(Context context, List<Faille> list) {
        this.context = context;
        this.list = list;
    }

    // --- ÉTAPE 1 : FABRICATION ---
    // Appelé pour créer une nouvelle "carte" vide.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // On "gonfle" (inflate) le fichier XML item_faille_full.xml
        // Ce fichier contient le design avec la barre latérale colorée (Indicator)
        View v = LayoutInflater.from(context).inflate(R.layout.item_faille_full, parent, false);
        return new ViewHolder(v);
    }

    // --- ÉTAPE 2 : REMPLISSAGE & LOGIQUE MÉTIER ---
    // C'est ici qu'on décide de la couleur selon la gravité.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 1. On récupère la faille courante
        Faille f = list.get(position);

        // 2. On remplit les textes simples
        holder.tvNom.setText(f.getNom());
        holder.tvDescription.setText(f.getDescription());
        holder.tvSeverite.setText(f.getSeverite());

        // Petite concaténation pour gagner de la place : "NMAP • 2024-06-12"
        holder.tvDateSource.setText(f.getSource() + " • " + f.getDateDetection());

        // --- 3. GESTION DYNAMIQUE DES COULEURS (Point important) ---
        // On analyse le texte de la sévérité pour choisir la couleur visuelle.
        int color;
        String sev = f.getSeverite().toUpperCase(); // On met en majuscule pour éviter les erreurs (High vs high)

        if (sev.contains("HIGH") || sev.contains("CRIT")) {
            // ROUGE : On va chercher la couleur définie dans colors.xml (Bonne pratique)
            color = ContextCompat.getColor(context, R.color.severity_high);
        } else if (sev.contains("MED")) {
            // ORANGE
            color = ContextCompat.getColor(context, R.color.severity_medium);
        } else {
            // VERT / BLEU (Low)
            color = ContextCompat.getColor(context, R.color.severity_low);
        }

        // 4. Application de la couleur sur les éléments graphiques
        holder.viewIndicator.setBackgroundColor(color); // La petite barre à gauche
        holder.tvSeverite.setTextColor(color);          // Le texte "HIGH"
    }

    @Override
    public int getItemCount() { return list.size(); }

    // --- LE VIEWHOLDER (Mémoire) ---
    // Fait le lien entre le Java et les IDs du fichier XML (item_faille_full)
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvDescription, tvSeverite, tvDateSource;
        View viewIndicator; // C'est la View vide qui sert juste à faire une barre de couleur

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNom);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSeverite = itemView.findViewById(R.id.tvSeveriteBadge);
            tvDateSource = itemView.findViewById(R.id.tvDateSource);
            viewIndicator = itemView.findViewById(R.id.viewIndicator);
        }
    }
}