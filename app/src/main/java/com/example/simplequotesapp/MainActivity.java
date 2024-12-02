package com.example.simplequotesapp;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    Button  quotes_button;
    RadioButton life, motivation, success;
    TextView quotes_text;
    int num;

    //랜덤 0~10 랜덤 값 생성 함수
    private int randomNum(){
        Random random = new Random();
        num = random.nextInt(10);
        return num;
    }


    //명언 텍스트 변경 애니메이션 함수
    private void changeFadeAnimation(String newWiseSaying){
        // 페이드 아웃 애니메이션 적용
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        quotes_text.startAnimation(fadeOut);

        // 애니메이션이 끝난 후 명언 변경 및 페이드 인 적용
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                quotes_text.setText(newWiseSaying); // 새로운 명언 설정

                // 페이드 인 애니메이션 적용
                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                quotes_text.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
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

        life = (RadioButton) findViewById(R.id.life);
        motivation = (RadioButton) findViewById(R.id.motivation);
        success = (RadioButton) findViewById(R.id.success);
        quotes_text = (TextView) findViewById(R.id.wise);
        quotes_button = (Button) findViewById(R.id.showWise);

        String [] success_quotes = getResources().getStringArray(R.array.success_quotes);
        String [] life_quotes = getResources().getStringArray(R.array.life_quotes);
        String [] motivation_quotes = getResources().getStringArray(R.array.motivation_quotos);



        quotes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quotes_button.setText("Show New Quote");
                if(life.isChecked() == true){
                    changeFadeAnimation(life_quotes[randomNum()]);
                }else if(success.isChecked()==true){
                    changeFadeAnimation(success_quotes[randomNum()]);
                }else{changeFadeAnimation(motivation_quotes[randomNum()]); }
            }
        });



    }
}