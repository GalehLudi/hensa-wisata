package com.hensa.wisata.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hensa.wisata.R;
import com.hensa.wisata.models.Transaksi;

import java.util.List;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {
    private final List<Transaksi> list;

    public LaporanAdapter(List<Transaksi> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public LaporanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi transaksi = list.get(position);
        holder.kode.setText(String.format("Kode: %s", transaksi.getKode()));
        holder.nama.setText(String.format("Nama: %s", transaksi.getPelanggan().getNama()));
        holder.hp.setText(String.format("Hp: %s", transaksi.getPelanggan().getHp()));
        holder.tanggal.setText(String.format("Tanggal: %s", transaksi.getWaktu()));

        String status = "-";
        if (transaksi.getHarga() != null) {
            if (transaksi.getHarga().doubleValue() - transaksi.getPembayaran().stream().mapToDouble(p -> p.getJumlah().doubleValue()).sum() == 0)
                status = "Lunas";
            else
                status = "Belum Lunas";
        }
        holder.status.setText(status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView kode, nama, hp, tanggal, status;

        public ViewHolder(@NonNull View view) {
            super(view);
            kode = view.findViewById(R.id.kode);
            nama = view.findViewById(R.id.nama);
            hp = view.findViewById(R.id.hp);
            tanggal = view.findViewById(R.id.tanggal);
            status = view.findViewById(R.id.status);
        }
    }
}
