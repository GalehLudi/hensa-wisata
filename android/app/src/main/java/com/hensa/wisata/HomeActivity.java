package com.hensa.wisata;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.InputStream;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        ImageView logo = findViewById(R.id.logo);
//        File file = new File("/logo.jpg");
//        try {
//            InputStream inputStream =  getAssets().open("logo.jpg");
//            Drawable drawable = Drawable.createFromStream(inputStream, file.getName());
//            logo.setImageDrawable(drawable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        MaterialCardView reservasi = findViewById(R.id.reservasi);
        reservasi.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ReservasiActivity.class);
            startActivity(intent);
        });

        MaterialCardView transaksi = findViewById(R.id.transaksi);
        transaksi.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TransaksiActivity.class);
            startActivity(intent);
        });

        MaterialCardView chat = findViewById(R.id.chat);
        chat.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        MaterialCardView petugas = findViewById(R.id.petugas);
        petugas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PetugasActivity.class);
            startActivity(intent);
        });

        MaterialCardView profil = findViewById(R.id.profil);
        profil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfilActivity.class);
            startActivity(intent);
        });

        MaterialCardView laporan = findViewById(R.id.laporan);
        laporan.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LaporanActivity.class);
            startActivity(intent);
        });
    }
}
