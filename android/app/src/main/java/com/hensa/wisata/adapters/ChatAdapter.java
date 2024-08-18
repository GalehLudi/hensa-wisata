package com.hensa.wisata.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hensa.wisata.R;
import com.hensa.wisata.models.Chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static List<Chat> list;
    private static OnClickListener listener;

    public ChatAdapter(List<Chat> list, OnClickListener listener) {
        ChatAdapter.list = list;
        ChatAdapter.listener = listener;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat = list.get(position);

        holder.foto.setImageResource(R.drawable.twotone_person);
        holder.nama.setText(chat.getPelanggan().getNama());
        holder.chat.setText(chat.getPesan());
        holder.waktu.setText(chat.getTime());
        if (Objects.equals(chat.getDate(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")))) {
            holder.waktu.setText(chat.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onClickListener(int position, Chat chat);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nama, chat, waktu;

        public ViewHolder(@NonNull View view, OnClickListener listener) {
            super(view);
            foto = view.findViewById(R.id.foto);
            nama = view.findViewById(R.id.nama);
            chat = view.findViewById(R.id.chat);
            waktu = view.findViewById(R.id.waktu);

            view.setOnClickListener(l -> {
                listener.onClickListener(getAdapterPosition(), list.get(getAdapterPosition()));
            });
        }
    }
}
