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

public class FailleFullAdapter extends RecyclerView.Adapter<FailleFullAdapter.ViewHolder> {

    private Context context;
    private List<Faille> list;

    public FailleFullAdapter(Context context, List<Faille> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // On lie le fichier XML qu'on vient de créer
        View v = LayoutInflater.from(context).inflate(R.layout.item_faille_full, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Faille f = list.get(position);

        holder.tvNom.setText(f.getNom());
        holder.tvDescription.setText(f.getDescription());
        holder.tvSeverite.setText(f.getSeverite());
        holder.tvDateSource.setText(f.getSource() + " • " + f.getDateDetection());

        // Gestion des couleurs
        int color;
        if (f.getSeverite().toUpperCase().contains("HIGH") || f.getSeverite().toUpperCase().contains("CRIT")) {
            color = ContextCompat.getColor(context, R.color.severity_high);
        } else if (f.getSeverite().toUpperCase().contains("MED")) {
            color = ContextCompat.getColor(context, R.color.severity_medium);
        } else {
            color = ContextCompat.getColor(context, R.color.severity_low);
        }

        holder.viewIndicator.setBackgroundColor(color);
        holder.tvSeverite.setTextColor(color);
    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvDescription, tvSeverite, tvDateSource;
        View viewIndicator;

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