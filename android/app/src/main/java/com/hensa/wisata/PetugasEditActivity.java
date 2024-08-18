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

public class PetugasEditActivity extends AppCompatActivity {
    Petugas petugas = new Petugas();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_petugas_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputLayout namaLayout = findViewById(R.id.namaLayout);
        TextInputLayout hpLayout = findViewById(R.id.hpLayout);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);
        TextInputLayout levelLayout = findViewById(R.id.levelLayout);

        TextInputEditText nama = findViewById(R.id.nama);
        TextInputEditText hp = findViewById(R.id.hp);
        TextInputEditText email = findViewById(R.id.email);
        TextInputEditText level = findViewById(R.id.level);

        nama.addTextChangedListener(new InputOnChange(namaLayout));
        hp.addTextChangedListener(new InputOnChange(hpLayout));
        email.addTextChangedListener(new InputOnChange(emailLayout));
        level.addTextChangedListener(new InputOnChange(levelLayout));

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        Call<ResponseBody<Petugas>> call = new APIClient(this).getService().getPetugas(id);
        call.enqueue(new Callback<ResponseBody<Petugas>>() {
            @Override
            public void onResponse(Call<ResponseBody<Petugas>> call, Response<ResponseBody<Petugas>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("sukses")) {
                        petugas = response.body().getData();
                        nama.setText(petugas.getNama());
                        hp.setText(petugas.getHp());
                        email.setText(petugas.getEmail());
                        level.setText(petugas.getLevel());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody<Petugas>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        MaterialButton simpan = findViewById(R.id.simpan);
        simpan.setOnClickListener(v -> {
            petugas.setNama(nama.getText().toString());
            petugas.setHp(hp.getText().toString());
            petugas.setEmail(email.getText().toString());
            petugas.setLevel(level.getText().toString());

            Call<ResponseBody<Void>> callSimpan = new APIClient(this).getService().editPetugas(id, petugas);
            callSimpan.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals("sukses")) {
                            Toast toast = Toast.makeText(PetugasEditActivity.this, response.body().getPesan(), Toast.LENGTH_SHORT);
                            toast.show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(PetugasEditActivity.this, PetugasActivity.class);
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
                        }
                    } else {
                        new MaterialAlertDialogBuilder(PetugasEditActivity.this)
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