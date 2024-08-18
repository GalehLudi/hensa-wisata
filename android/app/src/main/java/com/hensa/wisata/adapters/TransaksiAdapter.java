package com.hensa.wisata.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hensa.wisata.R;
import com.hensa.wisata.models.Chat;
import com.hensa.wisata.models.Reservasi;
import com.hensa.wisata.models.Transaksi;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
    private static List<Transaksi> list;
    private static OnClickListener listener;

    public TransaksiAdapter(List<Transaksi> list, OnClickListener listener) {
        TransaksiAdapter.list = list;
        TransaksiAdapter.listener = listener;
    }

    @NonNull
    @Override
    public TransaksiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiAdapter.ViewHolder holder, int position) {
        Transaksi transaksi = list.get(position);
        holder.kode.setText(String.format("Kode: %s", transaksi.getKode()));
        holder.nama.setText(String.format("Nama: %s", transaksi.getPelanggan().getNama()));
        holder.tanggal.setText(String.format("Tanggal: %s", transaksi.getWaktu()));
        holder.status.setText(transaksi.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onClickListener(int position, Transaksi transaksi);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView kode, nama, tanggal, status;

        public ViewHolder(@NonNull View view) {
            super(view);
            kode = view.findViewById(R.id.kode);
            nama = view.findViewById(R.id.nama);
            tanggal = view.findViewById(R.id.tanggal);
            status = view.findViewById(R.id.status);

            view.setOnClickListener(l -> {
                listener.onClickListener(getAdapterPosition(), list.get(getAdapterPosition()));
            });
        }
    }
}
