package com.hensa.wisata;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.hensa.wisata.adapters.TransaksiAdapter;
import com.hensa.wisata.libs.API;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.ResponseBody;
import com.hensa.wisata.models.Transaksi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransaksiActivity extends AppCompatActivity {
    private final List<Transaksi> list = new ArrayList<>();
    TransaksiAdapter adapter;
    String filter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaksi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView rv = findViewById(R.id.rv_transaksi);
        adapter = new TransaksiAdapter(list, (position, transaksi) -> {
            if (filter.equals("pembayaran")) {
                View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_transaksi, null);
                TextView kode = dialogLayout.findViewById(R.id.kode);
                TextView nama = dialogLayout.findViewById(R.id.nama);
                TextView hp = dialogLayout.findViewById(R.id.hp);
                TextView harga = dialogLayout.findViewById(R.id.harga);
                TextView jumlah = dialogLayout.findViewById(R.id.jumlah);
                TextView waktu = dialogLayout.findViewById(R.id.waktu);
                ImageView bukti = dialogLayout.findViewById(R.id.bukti);

                kode.setText(String.format("%-16s: %s", "Kode Reservasi", transaksi.getKode()));
                nama.setText(String.format("%-16s: %s", "Nama", transaksi.getPelanggan().getNama()));
                hp.setText(String.format("%-16s: %s", "HP", transaksi.getPelanggan().getHp()));
                waktu.setText(String.format("%-16s: %s", "Waktu Pembayaran", transaksi.getPembayaran().get(0).getWaktu()));
                harga.setText(String.format("%-16s: %s", "Harga", transaksi.getHarga()));
                jumlah.setText(String.format("%-16s: %s", "Jumlah", transaksi.getPembayaran().get(0).getJumlah()));

                Glide.with(TransaksiActivity.this).
                        load(API.BASE_URL + "/storage/media/" + transaksi.getPembayaran().get(0).getBukti())
                        .into(bukti);

                new MaterialAlertDialogBuilder(TransaksiActivity.this)
                        .setTitle("Pembayaran")
                        .setView(dialogLayout)
                        .setPositiveButton("Terima", (dialog, which) -> pembayaran(transaksi.getPembayaran().get(0).getId(), "terima"))
                        .setNeutralButton("Tolak", (dialog, which) -> pembayaran(transaksi.getPembayaran().get(0).getId(), "tolak"))
                        .setNegativeButton("Batal", null)
                        .show();
            } else {
                Intent intent = new Intent(TransaksiActivity.this, TransaksiProsesActivity.class);
                intent.putExtra("id", transaksi.getId() + "");
                startActivity(intent);
                finish();
            }
        });
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        getTransaksi();

        TabLayout tab = findViewById(R.id.tab);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filter = "";
                if (!tab.getText().toString().equals("Semua")) {
                    filter = tab.getText().toString().toLowerCase();
                }

                getTransaksi();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getTransaksi() {
        Call<ResponseBody<List<Transaksi>>> call = new APIClient(this).getService().transaksi(filter);
        call.enqueue(new Callback<ResponseBody<List<Transaksi>>>() {
            @Override
            public void onResponse(Call<ResponseBody<List<Transaksi>>> call, Response<ResponseBody<List<Transaksi>>> response) {
                if (response.isSuccessful()) {
                    list.clear();
                    list.addAll(response.body().getData());

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody<List<Transaksi>>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void pembayaran(Number id, String status) {
        Call<ResponseBody<Void>> call = new APIClient(this).getService().bayarTransaksi(id, status);
        call.enqueue(new Callback<ResponseBody<Void>>() {
            @Override
            public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("sukses")) {
                        Toast toast = Toast.makeText(TransaksiActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    getTransaksi();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody<Void>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
