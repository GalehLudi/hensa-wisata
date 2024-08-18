package com.hensa.wisata.adapters;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.hensa.wisata.R;
import com.hensa.wisata.models.Chat;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {
    private final List<Chat> list;

    public ChatUserAdapter(List<Chat> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ViewHolder holder, int position) {
        Chat chat = list.get(position);

        holder.chat.setText(chat.getPesan());
        holder.waktu.setText(chat.getTime());
        holder.tanggal.setText(chat.getDate());

        if (position == 0 && !list.get(position).getDate().equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")))) {
            holder.tanggal.setVisibility(View.VISIBLE);
        }

        if (position >= 1 && !list.get(position).getDate().equals(list.get(position - 1).getDate())) {
            holder.tanggal.setVisibility(View.VISIBLE);
        }

        if (list.get(position).getDari().equals("petugas")){
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            holder.wrapper.setLayoutParams(params);
//            holder.wrapper.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.soft));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView wrapper;
        TextView chat, waktu, tanggal;

        public ViewHolder(@NonNull View view) {
            super(view);
            wrapper = view.findViewById(R.id.wrapper);
            chat = view.findViewById(R.id.chat);
            waktu = view.findViewById(R.id.waktu);
            tanggal = view.findViewById(R.id.tanggal);
        }
    }
}
