package com.blake.gamevault.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;

import java.util.List;

public class KeyAdapter extends RecyclerView.Adapter<KeyAdapter.KeyViewHolder> {

    public static class KeyData {
        public String platform;
        public String date;
        public String keyString;
        public int copyNumber;

        public KeyData(String platform, String date, String keyString, int copyNumber) {
            this.platform = platform;
            this.date = date;
            this.keyString = keyString;
            this.copyNumber = copyNumber;
        }
    }

    private List<KeyData> keyList;

    public KeyAdapter(List<KeyData> keyList) {
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activation_key, parent, false);
        return new KeyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        KeyData data = keyList.get(position);
        Context context = holder.itemView.getContext();

        holder.keyPlatform.setText(data.platform);
        holder.keyPurchaseDate.setText(data.date);
        holder.keyCopyLabel.setText("COPY " + data.copyNumber);
        holder.keyCode.setText(data.keyString);

        holder.btnCopyKey.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Game Key", data.keyString);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Key copied to clipboard!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    public static class KeyViewHolder extends RecyclerView.ViewHolder {
        TextView keyPlatform, keyPurchaseDate, keyCopyLabel, keyCode;
        ImageView btnCopyKey;

        public KeyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your 3rd XML layout exactly
            keyPlatform = itemView.findViewById(R.id.keyPlatform);
            keyPurchaseDate = itemView.findViewById(R.id.keyPurchaseDate);
            keyCopyLabel = itemView.findViewById(R.id.keyCopyLabel);
            keyCode = itemView.findViewById(R.id.keyCode);
            btnCopyKey = itemView.findViewById(R.id.btnCopyKey);
        }
    }
}