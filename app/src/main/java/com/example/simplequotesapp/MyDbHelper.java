package com.example.simplequotesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*****************************************************************
 QSLiteDB 파일 생성 및 테이블 생성
 ******************************************************************/
class MyDbHelper extends SQLiteOpenHelper {
    MyDbHelper(Context context) {
        super(context, "Quotes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDB) {
        sqLiteDB.execSQL(
                "CREATE TABLE Categories (" +
                        "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "category_name TEXT NOT NULL);"
        );

        // Quotes 테이블 생성
        sqLiteDB.execSQL(
                "CREATE TABLE Quotes (" +
                        "quote_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "quote_text TEXT NOT NULL, " +
                        "category_id INTEGER, " +
                        "FOREIGN KEY (category_id) REFERENCES Categories (category_id));"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDB, int i, int i1) {
        sqLiteDB.execSQL("DROP TABLE IF EXISTS Quotes;");
        sqLiteDB.execSQL("DROP TABLE IF EXISTS Categories;");
        onCreate(sqLiteDB);
    }
}