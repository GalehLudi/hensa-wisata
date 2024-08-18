package com.hensa.wisata;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.libs.SessionManager;
import com.hensa.wisata.models.Auth;
import com.hensa.wisata.models.ResponseBody;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        ImageView logo = findViewById(R.id.logo);
        try {
            InputStream inputStream = getAssets().open("logo.jpg");
            Drawable drawable = Drawable.createFromStream(inputStream, "logo.jpg");
            logo.setImageDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MaterialButton loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

            TextInputEditText userInput = findViewById(R.id.user);
            TextInputEditText passwordInput = findViewById(R.id.password);

            Auth auth = new Auth(getBaseContext(), userInput.getText().toString(), passwordInput.getText().toString());

            Call<ResponseBody<Void>> call = new APIClient(this).getService().login(auth);
            call.enqueue(new Callback<ResponseBody<Void>>() {
                @Override
                public void onResponse(Call<ResponseBody<Void>> call, Response<ResponseBody<Void>> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals("sukses")) {
                            SessionManager sessionManager = new SessionManager(getBaseContext());
                            sessionManager.setToken(response.body().getToken());

                            Toast toast = Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT);
                            toast.show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                toast.addCallback(new Toast.Callback() {
                                    @Override
                                    public void onToastHidden() {
                                        super.onToastHidden();

                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }

                        new MaterialAlertDialogBuilder(LoginActivity.this)
                                .setTitle("Login Gagal")
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
}
