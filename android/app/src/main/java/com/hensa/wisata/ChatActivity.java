package com.hensa.wisata;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hensa.wisata.adapters.ChatAdapter;
import com.hensa.wisata.libs.APIClient;
import com.hensa.wisata.models.Chat;
import com.hensa.wisata.models.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    List<Chat> list = new ArrayList<>();
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView rv = findViewById(R.id.rv_chat);
        adapter = new ChatAdapter(list, (position, chat) -> {
            Intent intent = new Intent(ChatActivity.this, ChatUserActivity.class);
            intent.putExtra("nama", chat.getPelanggan().getNama());
            intent.putExtra("hp", chat.getPelanggan().getHp());

            startActivity(intent);
            finish();
        });

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        Call<ResponseBody<List<Chat>>> call = new APIClient(this).getService().chat();
        call.enqueue(new Callback<ResponseBody<List<Chat>>>() {
            @Override
            public void onResponse(Call<ResponseBody<List<Chat>>> call, Response<ResponseBody<List<Chat>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("sukses")) {
                        list.clear();
                        list.addAll(response.body().getData());

                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody<List<Chat>>> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
