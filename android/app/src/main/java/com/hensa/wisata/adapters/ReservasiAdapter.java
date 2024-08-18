package com.hensa.wisata.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hensa.wisata.R;
import com.hensa.wisata.models.Penumpang;

import java.util.List;

public class ReservasiAdapter extends RecyclerView.Adapter<ReservasiAdapter.ViewHolder> {
    private final List<Penumpang> list;
    private final OnClickListener listener;

    public ReservasiAdapter(List<Penumpang> list, OnClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, umur;
        MaterialButton hapus;

        public ViewHolder(@NonNull View view, OnClickListener listener) {
            super(view);
            nama = view.findViewById(R.id.nama);
            umur = view.findViewById(R.id.umur);
            hapus = view.findViewById(R.id.hapus);

            hapus.setOnClickListener(l -> {
                listener.onClickListener(getAdapterPosition());
            });
        }
    }

    public interface OnClickListener {
        void onClickListener(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservasi, parent, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Penumpang penumpang = list.get(position);

        holder.nama.setText(penumpang.getNama());
        holder.umur.setText(String.valueOf(penumpang.getUmur()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
