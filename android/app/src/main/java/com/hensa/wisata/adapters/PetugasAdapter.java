package com.hensa.wisata.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hensa.wisata.R;
import com.hensa.wisata.models.Petugas;

import java.util.List;

public class PetugasAdapter extends RecyclerView.Adapter<PetugasAdapter.ViewHolder> {
    private final List<Petugas> list;
    private final OnClickListener listener;

    public PetugasAdapter(List<Petugas> list, OnClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetugasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_petugas, parent, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PetugasAdapter.ViewHolder holder, int position) {
        Petugas petugas = list.get(position);

        holder.nama.setText(petugas.getNama());
        holder.kontak.setText("HP: " + petugas.getHp() + " | Email: " + petugas.getEmail());
        holder.level.setText(petugas.getLevel());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, kontak, level;

        public ViewHolder(@NonNull View view, OnClickListener listener) {
            super(view);
            nama = view.findViewById(R.id.nama);
            kontak = view.findViewById(R.id.kontak);
            level = view.findViewById(R.id.level);

            view.setOnClickListener(l -> {
                listener.onClickListener(getAdapterPosition());
            });
        }
    }
}
