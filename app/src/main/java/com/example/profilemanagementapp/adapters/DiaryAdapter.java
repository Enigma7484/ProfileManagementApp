package com.example.profilemanagementapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.profilemanagementapp.R;
import com.example.profilemanagementapp.activities.AddEditDiaryActivity;
import com.example.profilemanagementapp.models.DiaryEntry;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private Context context;
    private List<DiaryEntry> diaryList;
    private int userId;

    public DiaryAdapter(Context context, List<DiaryEntry> diaryList, int userId) {
        this.context = context;
        this.diaryList = diaryList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaryEntry entry = diaryList.get(position);
        holder.tvTitle.setText(entry.getTitle());
        holder.tvTimestamp.setText(entry.getTimestamp());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditDiaryActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("entryId", entry.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}