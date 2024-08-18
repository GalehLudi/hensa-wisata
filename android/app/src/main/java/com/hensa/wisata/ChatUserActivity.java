package com.hensa.wisata;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.hensa.wisata.adapters.ChatUserAdapter;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.Chat;
import com.hensa.wisata.models.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatUserActivity extends AppCompatActivity {
    ChatUserAdapter adapter;
    private List<Chat> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String namaIntent = intent.getStringExtra("nama");
        String hpIntent = intent.getStringExtra("hp");

        getSupportActionBar().setTitle(namaIntent);
        getSupportActionBar().setSubtitle(hpIntent);

        RecyclerView rv = findViewById(R.id.rv_chat_user);
        adapter = new ChatUserAdapter(list);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        Call<ResponseBody<List<Chat>>> call = new APIClient(this).getService().userChat(hpIntent);
        call.enqueue(new Callback<ResponseBody<List<Chat>>>() {
            @Override
            public void onResponse(Call<ResponseBody<List<Chat>>> call, Response<ResponseBody<List<Chat>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("sukses")) {
                        list.clear();
                        list.addAll(response.body().getData());

                        adapter.notifyDataSetChanged();
                        rv.scrollToPosition(list.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody<List<Chat>>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        TextInputLayout pesan = findViewById(R.id.pesan);
        pesan.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pesan.setEndIconVisible(!s.toString().isEmpty());
            }
        });
        pesan.setEndIconOnClickListener(l -> {
            String konten = pesan.getEditText().getText().toString();

            if (!konten.isEmpty()) {
                Chat chat = new Chat();
                chat.setPesan(konten);
                chat.setDateTime();

                Call<ResponseBody<Chat>> kirim = new APIClient(this).getService().storeChat(hpIntent, chat);
                kirim.enqueue(new Callback<ResponseBody<Chat>>() {
                    @Override
                    public void onResponse(Call<ResponseBody<Chat>> call, Response<ResponseBody<Chat>> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus().equals("sukses")) {
                                pesan.getEditText().setText("");

                                list.add(response.body().getData());
                                adapter.notifyItemInserted(list.size() - 1);
                                rv.scrollToPosition(list.size() - 1);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody<Chat>> call, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }
        });
    }
}
