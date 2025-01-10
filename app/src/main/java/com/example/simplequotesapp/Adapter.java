package com.example.simplequotesapp;

import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {
    private final ArrayList<RecylcerData> arrayList;
    private final MyDbHelper dbHelper;

    public Adapter(ArrayList<RecylcerData> arrayList, MyDbHelper dbHelper) {
        this.arrayList = arrayList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, parent, false);
        return new CustomViewHolder(view);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected final TextView textView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.quote_text);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        RecylcerData data = arrayList.get(position);
        holder.textView.setText(data.getQuote_text());

        holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int adapterPosition = holder.getBindingAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // SQLite 및 RecyclerView에서 항목 삭제
                    deleteQuoteFromDb(data.getQuote_text());
                    arrayList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);

                    // 토스트 메시지로 알림
                    Toast.makeText(view.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void deleteQuoteFromDb(String quoteText) {
        SQLiteDatabase sqLiteDb = null;
        try {
            sqLiteDb = dbHelper.getWritableDatabase();
            sqLiteDb.delete("Quotes", "quote_text = ?", new String[]{quoteText});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDb != null) {
                sqLiteDb.close();
            }
        }
    }
}
