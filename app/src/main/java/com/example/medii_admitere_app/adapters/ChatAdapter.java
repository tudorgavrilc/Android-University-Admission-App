package com.example.medii_admitere_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medii_admitere_app.R;
import com.example.medii_admitere_app.classes.Mesaj;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Mesaj> messages;

    public void setMessages(List<Mesaj> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mesaj message = messages.get(position);
        holder.usernameTextView.setText(message.getUsername());
        holder.messageTextView.setText(message.getMessage());
        if (message.getProfileImageUrl() != null) {
            Picasso.get().load(message.getProfileImageUrl()).into(holder.profileImageView);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String timestamp = sdf.format(new Date(message.getTimestamp().toDate().getTime()));
        holder.timestampTextView.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView messageTextView;
        TextView timestampTextView;
        ImageView profileImageView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
