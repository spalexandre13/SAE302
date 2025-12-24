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

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.HostViewHolder> {

    private Context context;
    private List<String> ipList;
    private Map<String, Integer> ipCounts;

    public HostAdapter(Context context, List<String> ipList, Map<String, Integer> ipCounts) {
        this.context = context;
        this.ipList = ipList;
        this.ipCounts = ipCounts;
    }

    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_host, parent, false);
        return new HostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HostViewHolder holder, int position) {
        String ip = ipList.get(position);
        int count = ipCounts.getOrDefault(ip, 0);

        // Remplissage des textes
        if (holder.tvIp != null) holder.tvIp.setText(ip);
        if (holder.tvCount != null) holder.tvCount.setText(count + " Vulnérabilité(s)");

        // --- LA CORRECTION EST ICI ---
        holder.itemView.setOnClickListener(v -> {
            // On pointe vers HostDetailActivity (la page 2) et plus MachineActivity
            Intent intent = new Intent(context, HostDetailActivity.class);
            intent.putExtra("TARGET_IP", ip);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ipList.size();
    }

    public class HostViewHolder extends RecyclerView.ViewHolder {
        TextView tvIp, tvCount;

        public HostViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vérifie que ces IDs existent bien dans ton fichier res/layout/item_host.xml
            tvIp = itemView.findViewById(R.id.tvHostIp);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }
}