package com.example.back.clientradar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    private Button btnMainRegist;
    private Button btnMainLogin;
    private EditText et_user_id;
    private EditText et_user_pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_user_id = (EditText) findViewById(R.id.et_user_id);
        et_user_pwd = (EditText) findViewById(R.id.et_user_pwd);
        btnMainRegist = (Button) findViewById(R.id.bt_user_register);
        btnMainLogin = (Button) findViewById(R.id.bt_user_log_in);

        btnMainRegist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


                startActivityForResult(intent, 1000); //회원가입 코드
            }
        });

        btnMainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id", et_user_id.getText().toString());
                intent.putExtra("password", et_user_pwd.getText().toString());

                setResult(RESULT_OK);
                startActivityForResult(intent, 2000);
            }
        });
    }

    // 액티비티 결과 받아옴
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
            et_user_id.setText(data.getStringExtra("id"));
        }
    }


}
