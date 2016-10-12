package com.example.back.clientradar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REGISTER_CODE = 1000;
    private final int LOGIN_CODE = 1001;

    private final int flag = 0;
    private EditText et_user_id;
    private EditText et_user_pwd;
    private Button btnMainRegist;
    private Button btnMainLogin;

    public Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_user_id = (EditText) findViewById(R.id.et_user_id);
        et_user_pwd = (EditText) findViewById(R.id.et_user_pwd);
        btnMainRegist = (Button) findViewById(R.id.bt_user_register);
        btnMainLogin = (Button) findViewById(R.id.bt_user_log_in);

        btnMainRegist.setOnClickListener(this);
        btnMainLogin.setOnClickListener(this);

        et_user_pwd.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_user_log_in:
                new UserLogin().execute();
                break;
            case R.id.bt_user_register:
                intent = new Intent(getApplicationContext(), RegistActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivityForResult(intent, REGISTER_CODE); //회원가입 코드
                break;
            default:
                break;
        }

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

    public class UserLogin extends AsyncTask<Void, Void, Void> {
        private HashMap<String, String> postDataMap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            postDataMap = new HashMap<>();
            postDataMap.put("id", et_user_id.getText().toString());
            postDataMap.put("password", et_user_pwd.getText().toString());
            postDataMap.put("flag", flag + "");
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Common.sendData(postDataMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (et_user_id.getText().toString().equals(Common.resId) && Common.resState.equals("0")) {

                Toast.makeText(MainActivity.this, Common.resId + "님 로그인 되었습니다", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(intent);

            } else if (!et_user_id.equals(Common.resId) || !Common.resState.equals("0")) {

                Toast.makeText(MainActivity.this, "올바른 접근 경로가 아닙니다", Toast.LENGTH_SHORT).show();
                return ;
            }
        }
    }



}
