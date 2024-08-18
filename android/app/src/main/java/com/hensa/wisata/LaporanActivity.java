package com.hensa.wisata;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hensa.wisata.adapters.LaporanAdapter;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.ResponseBody;
import com.hensa.wisata.models.Transaksi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaporanActivity extends AppCompatActivity {
    private final List<Transaksi> list = new ArrayList<>();
    LaporanAdapter adapter;
    String filter = "";
    TextInputLayout bulanLayout, tahunLayout;
    MaterialAutoCompleteTextView bulanEdit;
    TextInputEditText tahunEdit;
    List<String> bulan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_laporan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView rv = findViewById(R.id.rv_laporan);
        adapter = new LaporanAdapter(list);
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
                getTransaksi();
            }
        });

        BottomSheetDialog bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setContentView(R.layout.sheet_laporan);

        bottomSheet.findViewById(R.id.cetak_bulanan).setOnClickListener(v -> {
            bottomSheet.hide();
            alertPrint();
            bulanLayout.setVisibility(View.VISIBLE);
        });

        bottomSheet.findViewById(R.id.cetak_tahunan).setOnClickListener(v -> {
            bottomSheet.hide();
            alertPrint();
            bulanLayout.setVisibility(View.GONE);
        });

        findViewById(R.id.print).setOnClickListener(v -> {
            bottomSheet.show();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void alertPrint() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        View alertDialogView = getLayoutInflater().inflate(R.layout.dialog_laporan, null);

        bulanLayout = alertDialogView.findViewById(R.id.bulanLayout);
        tahunLayout = alertDialogView.findViewById(R.id.tahunLayout);

        bulanEdit = alertDialogView.findViewById(R.id.bulan);
        tahunEdit = alertDialogView.findViewById(R.id.tahun);

        for (int i = 1; i <= 12; i++) {
            bulan.add(String.valueOf(LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), i, 1).format(DateTimeFormatter.ofPattern("MMMM"))));
        }

        bulanEdit.setSimpleItems(bulan.toArray(new String[0]));
        tahunEdit.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        alertDialogBuilder.setIcon(R.drawable.twotone_print);
        alertDialogBuilder.setTitle("Cetak");
        alertDialogBuilder.setView(alertDialogView);

        alertDialogBuilder.setPositiveButton("Cetak", (dialog, which) -> {
            printLaporan(bulanEdit.getText().toString(), tahunEdit.getText().toString());
            dialog.dismiss();
        });

        alertDialogBuilder.setNegativeButton("Batal", (dialog, which) -> {
            dialog.dismiss();
        });

        alertDialogBuilder.show();
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

    private void printLaporan(String bulan, String tahun) {
        String finalBulan = bulan;
        for (int i = 0; i < this.bulan.size(); i++) {
            if (this.bulan.get(i).equals(bulan)) {
                bulan = String.valueOf(i + 1);
                break;
            }
        }

        Call<okhttp3.ResponseBody> call = new APIClient(this).getService().laporan(bulan, tahun);
        call.enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    File dir = new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "Hensa Wisata");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String fileName = "laporan " + finalBulan + " " + tahun + " (Generated at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ")" + ".pdf";

                    File file = new File(dir, fileName);
                    InputStream inputStream = null;
                    OutputStream outputStream = null;

                    try {
                        byte[] fileReader = new byte[16384];

                        inputStream = response.body().byteStream();
                        outputStream = new FileOutputStream(file);
                        while (true) {
                            int read = inputStream.read(fileReader);
                            if (read == -1) {
                                break;
                            }

                            outputStream.write(fileReader, 0, read);
                        }
                        Log.d("TAG", "onResponse: " + file.getAbsolutePath());
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
