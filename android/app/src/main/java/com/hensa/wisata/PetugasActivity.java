package com.hensa.wisata;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hensa.wisata.adapters.PetugasAdapter;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.Petugas;
import com.hensa.wisata.models.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetugasActivity extends AppCompatActivity {
    List<Petugas> list = new ArrayList<>();
    RecyclerView rv;
    PetugasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_petugas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv = findViewById(R.id.rv_petugas);

        adapter = new PetugasAdapter(list, position -> {
            Intent intent = new Intent(PetugasActivity.this, PetugasEditActivity.class);
            intent.putExtra("id", list.get(position).getId());
            startActivity(intent);
            finish();
        });

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        Call<ResponseBody<List<Petugas>>> call = new APIClient(this).getService().petugas();
        call.enqueue(new Callback<ResponseBody<List<Petugas>>>() {
            @Override
            public void onResponse(Call<ResponseBody<List<Petugas>>> call, Response<ResponseBody<List<Petugas>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("sukses")) {
                        list.clear();
                        list.addAll(response.body().getData());

                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody<List<Petugas>>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        FloatingActionButton tambah = findViewById(R.id.tambah);
        tambah.setOnClickListener(l -> {
            Intent intent = new Intent(PetugasActivity.this, PetugasTambahActivity.class);
            startActivity(intent);
            finish();
        });
    }
}