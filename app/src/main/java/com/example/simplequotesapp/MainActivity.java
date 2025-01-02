package com.example.simplequotesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    Button quotes_button;
    RadioButton life, motivation, success;
    TextView quotes_text;
    View dialog;
    MyDbHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;


    /*****************************************************************
     랜덤 0~10 랜덤 값 생성 함수
     ******************************************************************/
    private int randomNum() {
        Random random = new Random();
        int num = random.nextInt(10);
        return num;
    }


    /*****************************************************************
     옵션메뉴 설정
     ******************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = new Intent(getApplicationContext(), ShowSavedQuotes.class);
        if (item.getItemId() == R.id.savedSuccess) {
            intent.putExtra("categoryid", 1);
            startActivity(intent);
        } else if (item.getItemId() == R.id.savedLife) {
            intent.putExtra("categoryid", 2);
            startActivity(intent);
        } else if (item.getItemId() == R.id.savedMotivation) {
            intent.putExtra("categoryid", 3);
            startActivity(intent);
        }
        return false;
    }


    /*****************************************************************
     명언 텍스트 변경 애니메이션 함수
     ******************************************************************/
    private void changeFadeAnimation(String newWiseSaying) {
        // 페이드 아웃 애니메이션 적용
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        quotes_text.startAnimation(fadeOut);

        // 애니메이션이 끝난 후 명언 변경 및 페이드 인 적용
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                quotes_text.setText(newWiseSaying); // 새로운 명언 설정

                // 페이드 인 애니메이션 적용
                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                quotes_text.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }


    /*****************************************************************
     기본적으로 제공하는 명언들 추가
     ******************************************************************/
    public void insertSQLite(String[] success_quotes, String[] life_quotes, String[] motivation_quotes) {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        Log.i("insert검사", "insert 시작");

        // 카테고리가 이미 존재하는지 확인 후 없으면 삽입
        insertCategoryIfNotExists("Success");
        insertCategoryIfNotExists("Life");
        insertCategoryIfNotExists("Motivation");

        // Success 카테고리 명언 추가
        insertQuotesIfNotExists(success_quotes, 1);
        // Life 카테고리 명언 추가
        insertQuotesIfNotExists(life_quotes, 2);
        // Motivation 카테고리 명언 추가
        insertQuotesIfNotExists(motivation_quotes, 3);

        sqLiteDatabase.close();
        Log.i("insert검사", "insert 종료");
    }

    // 카테고리가 이미 존재하는지 확인하고 없으면 추가
    private void insertCategoryIfNotExists(String categoryName) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM Categories WHERE category_name = ?", new String[]{categoryName});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if (count == 0) {
            sqLiteDatabase.execSQL("INSERT INTO Categories(category_name) VALUES(?)", new Object[]{categoryName});
        }
        cursor.close();
    }

    // 명언이 이미 존재하는지 확인하고 없으면 추가
    private void insertQuotesIfNotExists(String[] quotes, int categoryId) {
        for (String quote : quotes) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM Quotes WHERE quote_text = ? AND category_id = ?",
                    new String[]{quote, String.valueOf(categoryId)});
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            if (count == 0) {
                sqLiteDatabase.execSQL("INSERT INTO Quotes(quote_text, category_id) VALUES(?, ?)", new Object[]{quote, categoryId});
            }
            cursor.close();
        }
    }


    /*****************************************************************
     카테고리별 랜덤 명언을 출력하는 메서드
     ******************************************************************/
    public void ShowRandomQuotes(int categroyid) {
        Log.i("명언 출력", "카테고리id " + categroyid + " 명언 출력시작");

        // 'Quotes' 테이블에서 category_id가 2인 명언을 랜덤하게 하나 뽑아오는 SQL 쿼리 실행
        // category_id가 1인 항목들을 'random()' 함수로 무작위로 정렬하고, 'limit 1'로 하나만 가져옴
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT quote_text " +
                "FROM Quotes " +
                "WHERE category_id = ? " +
                "ORDER BY RANDOM() LIMIT 1", new String[]{String.valueOf(categroyid)});
        // 커서(cursor)가 첫 번째 데이터 행을 가리키면
        if (cursor.moveToFirst()) {
            // 커서에서 첫 번째 열(0번째)의 텍스트 데이터를 가져와서 변수에 저장
            String succusstxt = cursor.getString(0);

            // 가져온 텍스트를 애니메이션에 전달하여 화면을 변경
            changeFadeAnimation(succusstxt);
        }
        cursor.close();
        sqLiteDatabase.close();
        Log.i("명언 출력", "카테고리id " + categroyid + " 명언 출력시작");
    }

    /*****************************************************************
     새로운 명언 추가 메서드
     ******************************************************************/

    public void addNewQuotes(int Rlayout, String dialogTitle, int categoryid, int Rid) {
        Log.i("addNewQuotes", dialogTitle);
        dialog = (View) View.inflate(MainActivity.this, Rlayout, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle(dialogTitle);
        dlg.setIcon(R.drawable.book);
        dlg.setView(dialog);

        dlg.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("addNewQuotes", "명언 추가시작");
                EditText editText = dialog.findViewById(Rid);
                String newQuotes = editText.getText().toString().trim();

                //유효성 검사
                if (newQuotes == null) {
                    Toast.makeText(MainActivity.this, "명언을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    sqLiteDatabase = dbHelper.getWritableDatabase();
                    sqLiteDatabase.execSQL("INSERT INTO Quotes(quote_text, category_id) VALUES (?, ?)", new Object[]{newQuotes, categoryid});
                    Toast.makeText(MainActivity.this, "명언이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.i("AddQuotes", "명언 추가 완료");

                } catch (SQLException e) {
                    Log.e("AddQuotes", "데이터베이스 작업 중 오류 발생", e);
                    Toast.makeText(MainActivity.this, "명언 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

                } finally {
                    if (sqLiteDatabase != null) {
                        sqLiteDatabase.close();
                    }
                }
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();


    }


    /*****************************************************************
     새로운 명언 추가할 수 있는 dialog 출력
     ******************************************************************/
    private void newSaveQuotes() {

        //success 롱 클릭 했을때
        success.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                addNewQuotes(R.layout.successdialog, "새로운 success 명언 입력", 1, R.id.successEdit);
                return true;
            }
        });


        //life 롱 클릭 했을때
        life.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                addNewQuotes(R.layout.lifedialog, "새로운 life 명언 입력", 2, R.id.lifeEdit);
                return true;
            }
        });


        //motivation 롱 클릭 했을때
        motivation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                addNewQuotes(R.layout.motivationdialog, "새로운 motivation 명언 입력", 3, R.id.motivationEdit);
                return true;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // ActionBar를 Toolbar로 설정


        life = (RadioButton) findViewById(R.id.life);
        motivation = (RadioButton) findViewById(R.id.motivation);
        success = (RadioButton) findViewById(R.id.success);
        quotes_text = (TextView) findViewById(R.id.wise);
        quotes_button = (Button) findViewById(R.id.showWise);
        dbHelper = new MyDbHelper(this);

        String[] success_quotes = getResources().getStringArray(R.array.success_quotes);
        String[] life_quotes = getResources().getStringArray(R.array.life_quotes);
        String[] motivation_quotes = getResources().getStringArray(R.array.motivation_quotos);


        quotes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quotes_button.setText("Show New Quote");
                sqLiteDatabase = dbHelper.getWritableDatabase();
                if (success.isChecked()) {
                    ShowRandomQuotes(1);

                } else if (life.isChecked()) {
                    ShowRandomQuotes(2);

                } else if (motivation.isChecked()) {
                    ShowRandomQuotes(3);
                }
            }
        });

        newSaveQuotes();
        insertSQLite(success_quotes, life_quotes, motivation_quotes);

    }

}