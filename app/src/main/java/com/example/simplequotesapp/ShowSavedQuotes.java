package com.example.simplequotesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ShowSavedQuotes extends AppCompatActivity {
    ImageButton backbtn;
    Button deletebtn;
    Intent intent, getIntent;
    SQLiteDatabase sqLiteDb;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> quotesList;

    private void fetchData(int categoryId) {
        MyDbHelper dbHelper = new MyDbHelper(this);
        sqLiteDb = dbHelper.getReadableDatabase();

        Cursor cursor = sqLiteDb.rawQuery("select quote_text from Quotes where category_id = ? ", new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                String quote = cursor.getString(0);
                quotesList.add(quote);
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDb.close();

    }

    private void deleteQuotesFromDb(ArrayList<String> selectedQuotes) {
        MyDbHelper dbHelper = new MyDbHelper(this);
        sqLiteDb = dbHelper.getWritableDatabase();

        for (String quote : selectedQuotes) {
            // SQL 쿼리로 해당 명언 삭제
            sqLiteDb.execSQL("DELETE FROM Quotes WHERE quote_text = ?", new Object[]{quote});
        }

        sqLiteDb.close();
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

        listView = (ListView) findViewById(R.id.list);
        backbtn = (ImageButton) findViewById(R.id.backBtn);
        deletebtn = (Button) findViewById(R.id.deleteBtn);
        intent = new Intent(getApplicationContext(), MainActivity.class);

        getIntent = getIntent();
        int categoryId = getIntent.getIntExtra("categoryid", -1);
        if (categoryId == -1) {
            Log.i("인텐트 테스트", "categroyid값이 전달되지 않음 ");
        } else {
            Log.i("인텐트 테스트", "categroyid : " + categoryId);
        }

        quotesList = new ArrayList<>();

        fetchData(categoryId);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, quotesList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });


        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checkedItems = listView.getCheckedItemPositions(); // 선택된 항목들
                ArrayList<String> selectedQuotes = new ArrayList<>();

                for (int i = 0; i < checkedItems.size(); i++) {
                    int position = checkedItems.keyAt(i);
                    boolean isChecked = checkedItems.valueAt(i);
                    if (isChecked) {
                        selectedQuotes.add(quotesList.get(position)); // 체크된 항목 저장
                    }
                }

                // 선택된 명언 삭제 처리
                if (!selectedQuotes.isEmpty()) {
                    deleteQuotesFromDb(selectedQuotes);
                    // 삭제 후 리스트 업데이트
                    quotesList.removeAll(selectedQuotes);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "선택된 명언 삭제 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "삭제할 명언을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
