package com.hensa.wisata;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.Petugas;
import com.hensa.wisata.models.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetugasTambahActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_petugas_tambah);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton simpan = findViewById(R.id.simpan);
        simpan.setOnClickListener(l -> {
            Petugas petugas = new Petugas();

            TextInputLayout namaLayout = findViewById(R.id.namaLayout);
            TextInputLayout hpLayout = findViewById(R.id.hpLayout);
            TextInputLayout emailLayout = findViewById(R.id.emailLayout);
            TextInputLayout levelLayout = findViewById(R.id.levelLayout);
            TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);

            TextInputEditText nama = findViewById(R.id.nama);
            TextInputEditText hp = findViewById(R.id.hp);
            TextInputEditText email = findViewById(R.id.email);
            TextInputEditText level = findViewById(R.id.level);
            TextInputEditText password = findViewById(R.id.password);
            TextInputEditText passwordKonfirmasi = findViewById(R.id.passwordKonfirmasi);

            nama.addTextChangedListener(new InputOnChange(namaLayout));
            hp.addTextChangedListener(new InputOnChange(hpLayout));
            email.addTextChangedListener(new InputOnChange(emailLayout));
            level.addTextChangedListener(new InputOnChange(levelLayout));
            password.addTextChangedListener(new InputOnChange(passwordLayout));

            petugas.setNama(nama.getText().toString());
            petugas.setHp(hp.getText().toString());
            petugas.setEmail(email.getText().toString());
            petugas.setLevel(level.getText().toString());
            petugas.setPassword(password.getText().toString());
            petugas.setPasswordConfirm(passwordKonfirmasi.getText().toString());

            Call<ResponseBody<Void>> call = new APIClient(this).getService().storePetugas(petugas);
            call.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals("sukses")) {
                            Toast toast = Toast.makeText(PetugasTambahActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT);
                            toast.show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(PetugasTambahActivity.this, PetugasActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        } else if (response.body().getStatus().equals("Validasi gagal!")) {
                            namaLayout.setError((CharSequence) response.body().getError().get("nama"));
                            hpLayout.setError((CharSequence) response.body().getError().get("hp"));
                            emailLayout.setError((CharSequence) response.body().getError().get("email"));
                            levelLayout.setError((CharSequence) response.body().getError().get("level"));
                            passwordLayout.setError((CharSequence) response.body().getError().get("password"));
                        }
                    } else {
                        new MaterialAlertDialogBuilder(PetugasTambahActivity.this)
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

    private static class InputOnChange implements TextWatcher {
        TextInputLayout input;

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
            input.setError(null);
        }
    }
}