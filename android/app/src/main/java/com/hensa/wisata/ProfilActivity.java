package com.hensa.wisata;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.Petugas;
import com.hensa.wisata.models.ResetPassword;
import com.hensa.wisata.models.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText nama = findViewById(R.id.nama);
        TextInputEditText hp = findViewById(R.id.hp);
        TextInputEditText email = findViewById(R.id.email);
        TextInputEditText level = findViewById(R.id.level);

        TextInputLayout namaLayout = findViewById(R.id.namaLayout);
        TextInputLayout hpLayout = findViewById(R.id.hpLayout);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);

        Call<ResponseBody<Petugas>> call = new APIClient(this).getService().user();
        call.enqueue(new Callback<ResponseBody<Petugas>>() {
            @Override
            public void onResponse(Call<ResponseBody<Petugas>> call, Response<ResponseBody<Petugas>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("sukses")) {
                        Petugas petugas = response.body().getData();
                        nama.setText(petugas.getNama());
                        hp.setText(petugas.getHp());
                        email.setText(petugas.getEmail());
                        level.setText(petugas.getLevel());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody<Petugas>> call, Throwable throwable) {

            }
        });

        Button ubahProfil = findViewById(R.id.ubahProfil);
        ubahProfil.setOnClickListener(v -> {
            Petugas petugas = new Petugas();
            petugas.setNama(nama.getText().toString());
            petugas.setHp(hp.getText().toString());
            petugas.setEmail(email.getText().toString());
            petugas.setLevel(level.getText().toString());

            Call<ResponseBody<Void>> callStore = new APIClient(this).getService().storeUser(petugas);
            callStore.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        Toast toast = Toast.makeText(getBaseContext(), response.body().getPesan(), Toast.LENGTH_SHORT);
                        toast.show();
                        if (response.body().getStatus().equals("sukses")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        } else {
                            if (response.body().getPesan().equals("Validasi gagal!")) {
                                namaLayout.setError((CharSequence) response.body().getError().get("nama"));
                                hpLayout.setError((CharSequence) response.body().getError().get("hp"));
                                emailLayout.setError((CharSequence) response.body().getError().get("email"));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody<Void>> call, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        });

        Button ubahPassword = findViewById(R.id.ubahPassword);
        ubahPassword.setOnClickListener(v -> {
            TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
            TextInputLayout passwordBaruLayout = findViewById(R.id.passwordBaruLayout);
            TextInputLayout passwordKonfirmasiLayout = findViewById(R.id.passwordKonfirmasiLayout);

            TextInputEditText password = findViewById(R.id.password);
            TextInputEditText passwordBaru = findViewById(R.id.passwordBaru);
            TextInputEditText passwordKonfirmasi = findViewById(R.id.passwordKonfirmasi);

            ResetPassword resetPassword = new ResetPassword(password.getText().toString(), passwordBaru.getText().toString(), passwordKonfirmasi.getText().toString());

            Call<ResponseBody<Void>> callReset = new APIClient(this).getService().resetPassword(resetPassword);
            callReset.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        Toast toast = Toast.makeText(getBaseContext(), response.body().getPesan(), Toast.LENGTH_SHORT);
                        toast.show();
                        if (response.body().getStatus().equals("sukses")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        } else {
                            if (response.body().getPesan().equals("Validasi gagal!")) {
                                passwordLayout.setError((CharSequence) response.body().getError().get("password"));
                                passwordBaruLayout.setError((CharSequence) response.body().getError().get("new_password"));
                                passwordKonfirmasiLayout.setError((CharSequence) response.body().getError().get("confirm_password"));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody<Void>> call, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        });
    }
}