package com.hensa.wisata;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hensa.wisata.adapters.ReservasiAdapter;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.Penumpang;
import com.hensa.wisata.models.Reservasi;
import com.hensa.wisata.models.ResponseBody;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasiActivity extends AppCompatActivity {
    static TextInputEditText penumpang;
    static TextInputEditText umur;
    static MaterialButton tambah;
    Reservasi reservasi = new Reservasi();
    List<Penumpang> penumpangList = new ArrayList<>();
    RecyclerView rv;
    ReservasiAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservasi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv = findViewById(R.id.rv_reservasi);
        adapter = new ReservasiAdapter(penumpangList, position -> {
            penumpangList.remove(position);
            notifyAdapter(position);
        });

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        penumpang = findViewById(R.id.penumpang);
        umur = findViewById(R.id.umur);

        penumpang.addTextChangedListener(new InputOnChange());
        umur.addTextChangedListener(new InputOnChange());

        tambah = findViewById(R.id.tambah);
        tambah.setOnClickListener(v -> {
            penumpangList.add(new Penumpang(penumpang.getText().toString(), Integer.parseInt(umur.getText().toString())));

            penumpang.setText("");
            umur.setText("");

            notifyAdapter(penumpangList.size() - 1);
        });

        TextInputLayout kodeLayout = findViewById(R.id.kodeLayout);
        TextInputLayout berangkatLayout = findViewById(R.id.berangkatLayout);
        TextInputLayout tujuanLayout = findViewById(R.id.tujuanLayout);
        TextInputLayout waktuBerangkatLayout = findViewById(R.id.waktuBerangkatLayout);
        TextInputLayout waktuTibaLayout = findViewById(R.id.waktuTibaLayout);
        TextInputLayout pelangganLayout = findViewById(R.id.pelangganLayout);
        TextInputLayout hargaLayout = findViewById(R.id.hargaLayout);

        TextInputEditText kode = findViewById(R.id.kode);
        TextInputEditText berangkat = findViewById(R.id.berangkat);
        TextInputEditText tujuan = findViewById(R.id.tujuan);
        TextInputEditText waktu_berangkat = findViewById(R.id.waktu_berangkat);
        TextInputEditText waktu_tiba = findViewById(R.id.waktu_tiba);
        TextInputEditText pelanggan = findViewById(R.id.pelanggan);
        TextInputEditText harga = findViewById(R.id.harga);

        Calendar waktuBerangkat = Calendar.getInstance();
        waktu_berangkat.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                new DatePickerDialog(v.getContext(), (__, year, month, dayOfMonth) -> {
                    waktuBerangkat.set(year, month, dayOfMonth);

                    new TimePickerDialog(v.getContext(), (___, hourOfDay, minute) -> {
                        waktuBerangkat.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        waktuBerangkat.set(Calendar.MINUTE, minute);

                        waktu_berangkat.setText(LocalDateTime.ofInstant(waktuBerangkat.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")));
                    }, waktuBerangkat.get(Calendar.HOUR_OF_DAY), waktuBerangkat.get(Calendar.MINUTE), true).show();
                }, waktuBerangkat.get(Calendar.YEAR), waktuBerangkat.get(Calendar.MONTH), waktuBerangkat.get(Calendar.DAY_OF_MONTH)).show();
                v.clearFocus();
            }
        });

        Calendar waktuTiba = Calendar.getInstance();
        waktu_tiba.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                new DatePickerDialog(v.getContext(), (viewDate, year, month, dayOfMonth) -> {
                    waktuTiba.set(year, month, dayOfMonth);

                    new TimePickerDialog(v.getContext(), (viewTime, hourOfDay, minute) -> {
                        waktuTiba.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        waktuTiba.set(Calendar.MINUTE, minute);

                        waktu_tiba.setText(LocalDateTime.ofInstant(waktuTiba.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")));
                    }, waktuTiba.get(Calendar.HOUR_OF_DAY), waktuTiba.get(Calendar.MINUTE), true).show();
                }, waktuTiba.get(Calendar.YEAR), waktuTiba.get(Calendar.MONTH), waktuTiba.get(Calendar.DAY_OF_MONTH)).show();
                v.clearFocus();
            }
        });

        kode.addTextChangedListener(new InputOnChange(kodeLayout));
        berangkat.addTextChangedListener(new InputOnChange(berangkatLayout));
        tujuan.addTextChangedListener(new InputOnChange(tujuanLayout));
        waktu_berangkat.addTextChangedListener(new InputOnChange(waktuBerangkatLayout));
        waktu_tiba.addTextChangedListener(new InputOnChange(waktuTibaLayout));
        pelanggan.addTextChangedListener(new InputOnChange(pelangganLayout));
        harga.addTextChangedListener(new InputOnChange(hargaLayout));

        MaterialButton pesan = findViewById(R.id.pesan);
        pesan.setOnClickListener(v -> {
            reservasi.setKode(kode.getText().toString());
            reservasi.setBerangkat(berangkat.getText().toString());
            reservasi.setTujuan(tujuan.getText().toString());
            if (!waktu_berangkat.getText().toString().isEmpty())
                reservasi.setWaktuBerangkat(LocalDateTime.ofInstant(waktuBerangkat.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm")));
            if (!waktu_tiba.getText().toString().isEmpty())
                reservasi.setWaktuTiba(LocalDateTime.ofInstant(waktuTiba.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm")));
            reservasi.setPelanggan(pelanggan.getText().toString());
            reservasi.setHarga(harga.getText().toString());
            reservasi.setPenumpang(penumpangList);

            Call<ResponseBody<Void>> call = new APIClient(this).getService().storeReservasi(reservasi);
            call.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals("sukses")) {
                            Toast toast = Toast.makeText(ReservasiActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT);
                            toast.show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(ReservasiActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        } else if (response.body().getStatus().equals("validasi gagal")) {
                            kodeLayout.setError((CharSequence) response.body().getError().get("kode"));
                            berangkatLayout.setError((CharSequence) response.body().getError().get("berangkat"));
                            tujuanLayout.setError((CharSequence) response.body().getError().get("tujuan"));
                            waktuBerangkatLayout.setError((CharSequence) response.body().getError().get("waktu_berangkat"));
                            waktuTibaLayout.setError((CharSequence) response.body().getError().get("waktu_tiba"));
                            pelangganLayout.setError((CharSequence) response.body().getError().get("pelanggan"));
                            hargaLayout.setError((CharSequence) response.body().getError().get("harga"));
                        } else if (response.body().getPesan().equals("pelanggan gagal")) {
                            new MaterialAlertDialogBuilder(ReservasiActivity.this)
                                    .setTitle("Pelanggan Tidak Ditemukan!")
                                    .setMessage("Tambah Data Pelanggan Baru?")
                                    .setPositiveButton("OK", null)
                                    .setNegativeButton("Tidak", null)
                                    .show();
                        }
                    } else {
                        new MaterialAlertDialogBuilder(ReservasiActivity.this)
                                .setTitle(response.body().getStatus())
                                .setMessage(response.body().getPesan())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody<Void>> call, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        });
    }

    private void notifyAdapter(int position) {
        rv.setVisibility(View.VISIBLE);
        if (penumpangList.isEmpty())
            rv.setVisibility(View.GONE);

        this.adapter.notifyItemChanged(position);
    }

    private static class InputOnChange implements TextWatcher {
        TextInputLayout input = null;

        public InputOnChange() {
        }

        public InputOnChange(TextInputLayout input) {
            this.input = input;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            tambah.setVisibility(View.VISIBLE);
            if (penumpang.getText().toString().isEmpty() || umur.getText().toString().isEmpty())
                tambah.setVisibility(View.GONE);

            if (input != null)
                input.setError(null);
        }
    }
}