package com.example.simplequotesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowSavedQuotes extends AppCompatActivity {

    private TextView tv_back;
    private Intent intent, getIntent;
    private SQLiteDatabase sqLiteDb;
    private Adapter adapter;
    private ArrayList<RecylcerData> quotesList;
    private RecyclerView recyclerView;
    private MyDbHelper myDbHelper;

    /*****************************************************************
     명언 리스트 삽입 메서드
     ******************************************************************/
    private void fetchData(int categoryId) {
        sqLiteDb = myDbHelper.getReadableDatabase();

        Log.i("fetchData", "Fetching data for categoryId: " + categoryId);  // 카테고리 ID 로그

        Cursor cursor = sqLiteDb.rawQuery("SELECT quote_text FROM Quotes WHERE category_id = ?",
                new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                String quote = cursor.getString(0);
                Log.i("fetchData", "Fetched quote: " + quote);  // 가져온 명언 로그

                RecylcerData recyclerData = new RecylcerData(quote);
                quotesList.add(recyclerData);

                Log.i("fetchData", "Quote added to list: " + quote);  // 리스트에 추가된 명언 로그
            } while (cursor.moveToNext());
        } else {
            Log.i("fetchData", "No quotes found for categoryId: " + categoryId);  // 결과가 없을 때 로그
        }

        cursor.close();
        sqLiteDb.close();

        Log.i("fetchData", "Database closed.");
        adapter.notifyDataSetChanged();  // 어댑터 갱신
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.showsavedquotes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.showsavedquotes), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // MyDbHelper 초기화
        myDbHelper = new MyDbHelper(this);

        quotesList = new ArrayList<>();
        recyclerView = findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(quotesList, myDbHelper);
        recyclerView.setAdapter(adapter);

        tv_back = findViewById(R.id.tv_back);
        intent = new Intent(ShowSavedQuotes.this, MainActivity.class);

        // Intent로 전달받은 categoryId 가져오기
        getIntent = getIntent();
        int categoryId = getIntent.getIntExtra("categoryid", -1);
        if (categoryId == -1) {
            Log.i("인텐트 테스트", "categoryid값이 전달되지 않음 ");
        } else {
            Log.i("인텐트 테스트", "categoryid : " + categoryId);
        }

        // 데이터 불러오기
        fetchData(categoryId);

        /*****************************************************************
         이전 액티비티로 이동
         ******************************************************************/
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                finish();
            }
        });
    }
}

