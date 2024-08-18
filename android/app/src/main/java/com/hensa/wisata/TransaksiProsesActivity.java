package com.hensa.wisata;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.hensa.wisata.libs.API;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.ResponseBody;
import com.hensa.wisata.models.Transaksi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransaksiProsesActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> resultLauncher;
    Uri tiketUri;
    PDFView tiket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaksi_proses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Number id = Integer.parseInt(intent.getStringExtra("id"));

        TextView kode = findViewById(R.id.kode);
        TextView nama = findViewById(R.id.nama);
        TextView hp = findViewById(R.id.hp);
        TextView tanggal = findViewById(R.id.tanggal);
        TextView keberangkatan = findViewById(R.id.keberangkatan);
        TextView tujuan = findViewById(R.id.tujuan);
        TextView penumpang = findViewById(R.id.penumpang);
        TextView status = findViewById(R.id.status);

        TextInputLayout kodeLayout = findViewById(R.id.kodeTerbangLayout);
        TextInputLayout waktuBerangkatLayout = findViewById(R.id.waktuBerangkatLayout);
        TextInputLayout waktuTibaLayout = findViewById(R.id.waktuTibaLayout);
        TextInputLayout hargaLayout = findViewById(R.id.hargaLayout);

        TextInputEditText kodeEdit = findViewById(R.id.kodeTerbang);
        TextInputEditText waktuBerangkatEdit = findViewById(R.id.waktu_berangkat);
        TextInputEditText waktuTibaEdit = findViewById(R.id.waktu_tiba);
        TextInputEditText hargaEdit = findViewById(R.id.harga);

        kodeEdit.addTextChangedListener(new InputOnChange(kodeLayout));
        waktuBerangkatEdit.addTextChangedListener(new InputOnChange(waktuBerangkatLayout));
        waktuTibaEdit.addTextChangedListener(new InputOnChange(waktuTibaLayout));
        hargaEdit.addTextChangedListener(new InputOnChange(hargaLayout));

        tiket = findViewById(R.id.pdfView);
        TextView tiketText = findViewById(R.id.tiketText);

        Calendar waktuBerangkat = Calendar.getInstance();
        waktuBerangkatEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                new DatePickerDialog(v.getContext(), (__, year, month, dayOfMonth) -> {
                    waktuBerangkat.set(year, month, dayOfMonth);

                    new TimePickerDialog(v.getContext(), (___, hourOfDay, minute) -> {
                        waktuBerangkat.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        waktuBerangkat.set(Calendar.MINUTE, minute);

                        waktuBerangkatEdit.setText(LocalDateTime.ofInstant(waktuBerangkat.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")));
                    }, waktuBerangkat.get(Calendar.HOUR_OF_DAY), waktuBerangkat.get(Calendar.MINUTE), true).show();
                }, waktuBerangkat.get(Calendar.YEAR), waktuBerangkat.get(Calendar.MONTH), waktuBerangkat.get(Calendar.DAY_OF_MONTH)).show();
                v.clearFocus();
            }
        });

        Calendar waktuTiba = Calendar.getInstance();
        waktuTibaEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                new DatePickerDialog(v.getContext(), (viewDate, year, month, dayOfMonth) -> {
                    waktuTiba.set(year, month, dayOfMonth);

                    new TimePickerDialog(v.getContext(), (viewTime, hourOfDay, minute) -> {
                        waktuTiba.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        waktuTiba.set(Calendar.MINUTE, minute);

                        waktuTibaEdit.setText(LocalDateTime.ofInstant(waktuTiba.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")));
                    }, waktuTiba.get(Calendar.HOUR_OF_DAY), waktuTiba.get(Calendar.MINUTE), true).show();
                }, waktuTiba.get(Calendar.YEAR), waktuTiba.get(Calendar.MONTH), waktuTiba.get(Calendar.DAY_OF_MONTH)).show();
                v.clearFocus();
            }
        });

        MaterialButton batal = findViewById(R.id.batalkan);
        batal.setOnClickListener(v -> {
            Call<ResponseBody<Void>> callBatal = new APIClient(this).getService().batalTransaksi(id);
            callBatal.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals("sukses")) {
                            Toast toast = Toast.makeText(TransaksiProsesActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT);
                            toast.show();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(TransaksiProsesActivity.this, TransaksiActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        } else {
                            new MaterialAlertDialogBuilder(TransaksiProsesActivity.this)
                                    .setTitle(response.body().getStatus())
                                    .setMessage(response.body().getPesan())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody<Void>> call, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        });

        MaterialButton proses = findViewById(R.id.proses);
        proses.setOnClickListener(v -> {
            Transaksi transaksi = new Transaksi();
            Transaksi.Penerbangan penerbangan = new Transaksi.Penerbangan();
            penerbangan.setKode(kodeEdit.getText().toString());
            penerbangan.setWaktuBerangkat(waktuBerangkatEdit.getText().toString());
            penerbangan.setWaktuTiba(waktuTibaEdit.getText().toString());
            transaksi.setPenerbangan(penerbangan);
            transaksi.setHarga(Long.parseLong(hargaEdit.getText().toString()));

            Map<String, RequestBody> body = new HashMap<>();
            MultipartBody.Part tiket = null;

            InputStream inputStream = null;
            ByteArrayOutputStream buffer = null;
            try {
                if (tiketUri != null && getContentResolver().getType(tiketUri) != null) {
                    inputStream = getContentResolver().openInputStream(tiketUri);
                    buffer = new ByteArrayOutputStream();

                    int nRead;
                    byte[] data = new byte[16384];
                    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] fileBytes = buffer.toByteArray();

                    tiket = MultipartBody.Part.createFormData("tiket", getFileName(tiketUri), RequestBody.create(MediaType.parse(getContentResolver().getType(tiketUri)), fileBytes));
                }

                if (transaksi.getHarga().doubleValue() > 0)
                    body.put("harga", RequestBody.create(MediaType.parse("text/plain"), transaksi.getHarga().toString()));
                body.put("penerbangan", RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(transaksi.getPenerbangan())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (buffer != null) {
                    try {
                        buffer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Call<ResponseBody<Void>> callProses = new APIClient(this).getService().prosesTransaksi(id, body, tiket);
            callProses.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals("sukses")) {
                            Toast toast = Toast.makeText(TransaksiProsesActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT);
                            toast.show();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(TransaksiProsesActivity.this, TransaksiActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        } else if (response.body().getStatus().equals("validasi gagal")) {
                            kodeLayout.setError((CharSequence) response.body().getError().get("kode"));
                            waktuBerangkatLayout.setError((CharSequence) response.body().getError().get("waktu_berangkat"));
                            waktuTibaLayout.setError((CharSequence) response.body().getError().get("waktu_tiba"));
                            hargaLayout.setError((CharSequence) response.body().getError().get("harga"));

                            if (response.body().getError().get("tiket") != null) {
                                new MaterialAlertDialogBuilder(TransaksiProsesActivity.this)
                                        .setTitle("Validasi Gagal!")
                                        .setMessage(response.body().getError().get("tiket"))
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        } else {
                            new MaterialAlertDialogBuilder(TransaksiProsesActivity.this)
                                    .setTitle(response.body().getStatus())
                                    .setMessage(response.body().getPesan())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody<Void>> call, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        });

        Call<ResponseBody<Transaksi>> call = new APIClient(this).getService().getTransaksi(id);
        call.enqueue(new Callback<ResponseBody<Transaksi>>() {
            @Override
            public void onResponse(Call<ResponseBody<Transaksi>> call, Response<ResponseBody<Transaksi>> response) {
                if (response.isSuccessful()) {
                    Transaksi transaksi = response.body().getData();
                    kode.setText(String.format("%-15s: %s", "Kode Reservasi", transaksi.getKode()));
                    nama.setText(String.format("%-15s: %s", "Nama", transaksi.getPelanggan().getNama()));
                    hp.setText(String.format("%-15s: %s", "HP", transaksi.getPelanggan().getHp()));
                    tanggal.setText(String.format("%-15s: %s", "Tanggal", transaksi.getWaktu()));
                    keberangkatan.setText(String.format("%-15s: %s", "Keberangkatan", transaksi.getPenerbangan().getBerangkat()));
                    tujuan.setText(String.format("%-15s: %s", "Tujuan", transaksi.getPenerbangan().getTujuan()));
                    status.setText(String.format("%-15s: %s", "Status", transaksi.getStatus()));

                    if (transaksi.getStatus().equals("batal")) {
                        batal.setVisibility(View.GONE);
                        status.setVisibility(View.VISIBLE);
                    }

                    StringBuilder mergePenumpang = new StringBuilder();
                    mergePenumpang.append("Penumpang:\n");
                    for (int i = 0; i < transaksi.getPenumpang().size(); i++) {
                        mergePenumpang
                                .append(i + 1)
                                .append(". ")
                                .append(transaksi.getPenumpang().get(i).getNama())
                                .append(" (")
                                .append(transaksi.getPenumpang().get(i).getUmur())
                                .append(" tahun)");
                        if (i < transaksi.getPenumpang().size() - 1) {
                            mergePenumpang.append("\n");
                        }
                    }
                    penumpang.setText(mergePenumpang.toString());

                    kodeEdit.setText(transaksi.getPenerbangan().getKode());
                    waktuBerangkatEdit.setText(transaksi.getPenerbangan().getWaktuBerangkat());
                    waktuTibaEdit.setText(transaksi.getPenerbangan().getWaktuTiba());
                    hargaEdit.setText((transaksi.getHarga() != null ? transaksi.getHarga() : "").toString());

                    new Thread(() -> {
                        try {
                            HttpURLConnection connection = getUrlConnection(transaksi);

                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(() -> {
                                    tiketText.setVisibility(View.GONE);
                                    tiket.setVisibility(View.VISIBLE);
                                });

                                InputStream inputStream = connection.getInputStream();
                                File file = new File(getCacheDir(), transaksi.getKode() + "-" + transaksi.getPenerbangan().getKode() + ".pdf");

                                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                                    byte[] buffer = new byte[16384];
                                    int bytesRead;
                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                    }

                                    tiket.fromFile(file)
                                            .enableDoubletap(false)
                                            .enableSwipe(false)
                                            .onLoad(l -> {
                                                try {
                                                    parsePDF(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
                                                } catch (FileNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            })
                                            .onError(l -> {
                                                tiketText.setVisibility(View.VISIBLE);
                                                tiket.setVisibility(View.GONE);
                                            })
                                            .load();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            }

            private HttpURLConnection getUrlConnection(Transaksi transaksi) throws IOException {
                URL url = new URL(API.BASE_URL + "/storage/tiket/" + transaksi.getKode() + "-" + transaksi.getPenerbangan().getKode() + ".pdf");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/pdf");
                connection.setRequestProperty("Content-Type", "application/pdf");
                connection.connect();
                return connection;
            }

            @Override
            public void onFailure(Call<ResponseBody<Transaksi>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    tiketText.setVisibility(View.GONE);
                    tiket.setVisibility(View.VISIBLE);

                    Uri path = data.getData();
                    tiketUri = path;

                    tiket.fromUri(path)
                            .enableDoubletap(false)
                            .enableSwipe(false)
                            .onLoad(l -> {
                                ParcelFileDescriptor fileDescriptor = null;
                                try {
                                    fileDescriptor = getContentResolver().openFileDescriptor(path, "r");
                                    this.parsePDF(fileDescriptor);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            })
                            .onError(l -> {
                                tiketText.setVisibility(View.VISIBLE);
                                tiket.setVisibility(View.GONE);
                            })
                            .load();
                }
            }
        });

        MaterialCardView pilihTiket = findViewById(R.id.pilihTiket);
        pilihTiket.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                selectTicket();
            } else {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectTicket();
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
        });
    }

    private void selectTicket() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        resultLauncher.launch(Intent.createChooser(intent, "Select PDF"));
    }

    private void parsePDF(ParcelFileDescriptor fileDescriptor) {
        int height = tiket.getWidth() * 2;

        try (PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor)) {
            PdfRenderer.Page page = pdfRenderer.openPage(0);
            height = page.getHeight();
            page.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        tiket.setMinimumHeight(height);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectTicket();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private static class InputOnChange implements TextWatcher {
        TextInputLayout input = null;

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
            if (input != null)
                input.setError(null);
        }
    }
}
