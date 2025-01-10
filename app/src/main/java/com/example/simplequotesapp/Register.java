package com.example.simplequotesapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button btn_pwdcheck, btn_regiser;
    private EditText et_email, et_pwd1, et_pwd2;
    private TextView tv_back;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_pwdcheck = (Button) findViewById(R.id.btn_pwdcheck);
        btn_regiser = (Button) findViewById(R.id.btn_regiser);
        et_email = (EditText) findViewById(R.id.et_email);
        et_pwd1 = (EditText) findViewById(R.id.et_pwd1);
        et_pwd2 = (EditText) findViewById(R.id.et_pwd2);
        tv_back = (TextView) findViewById(R.id.tv_back);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("SimpleQuotesApp");


        btn_regiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = et_email.getText().toString();
                String pwdStr = et_pwd1.getText().toString();
                if (check == true) {
                    firebaseAuth.createUserWithEmailAndPassword(emailStr, pwdStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i("btn_register", "check : " + check);
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                UserInfo userInfo = new UserInfo();
                                userInfo.setUid(firebaseUser.getUid());
                                userInfo.setEmail(firebaseUser.getEmail());
                                userInfo.setPassword(pwdStr);

                                databaseReference.child("userInfo").child(firebaseUser.getUid()).setValue(userInfo);
                                Toast.makeText(Register.this, "회원가입 성공! 다시 로그인 해주세요", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Register.this, "회원가입 실패", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                } else {
                    Log.i("btn_register", "check : " + check);
                    Toast.makeText(Register.this, "비밀번호가 일치하지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_pwdcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("checkpwd", "확인시작");
                check = false;
                String pwd1, pwd2;
                pwd1 = et_pwd1.getText().toString();
                pwd2 = et_pwd2.getText().toString();
                if (pwd1.equals(pwd2)) {
                    check = true;
                    Log.i("checkpwd", "check : " + check);
                    Toast.makeText(Register.this, "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.i("checkpwd", "check : " + check);
                    Toast.makeText(Register.this, "비밀번호가 일치하지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}