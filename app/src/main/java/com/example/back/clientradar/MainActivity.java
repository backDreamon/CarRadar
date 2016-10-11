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
    private final int flag = 0;
    private EditText et_user_id;
    private EditText et_user_pwd;
    private Button btnMainRegist;
    private Button btnMainLogin;

    private UserLogin userLogin;
    public Intent intent;

    public static String resId;
    public static String resState;
    public static String resNo;
    public static String resDistance;

    private int loginState;

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_user_log_in :
                new UserLogin().execute();
                Log.d("loginState", loginState+"");

                if(loginState == 1) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                    /*intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("id", resId);*/

                    startActivity(intent);
                } else {

                }
                /*intent = new Intent(getApplicationContext(), LoginActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id", et_user_id.getText().toString());
                intent.putExtra("password", et_user_pwd.getText().toString());

                setResult(RESULT_OK);
                startActivityForResult(intent, 2000);*/
                break;
            case R.id.bt_user_register :
                intent = new Intent(getApplicationContext(), RegistActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


                startActivityForResult(intent, 1000); //회원가입 코드
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
            postDataMap.put("flag", flag+"");
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                sendData(postDataMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(et_user_id.getText().toString().equals(resId) && resState.equals("0")){
                loginState = 1;
                Toast.makeText(MainActivity.this, "로그인 중 입니다", Toast.LENGTH_SHORT).show();
            } else if (!et_user_id.equals(resId) || !resState.equals("0")){
                loginState = 0;
                Toast.makeText(MainActivity.this, "올바른 접근 경로가 아닙니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void sendData(HashMap<String, String> postData) {

         final String loginAddress = "http://dev-dreamon.cloud.or.kr/android.php";
         HttpURLConnection connection;

        try {
            URL url = new URL(loginAddress);
            //url 커넥션 생성
            connection = (HttpURLConnection) url.openConnection();
            //요청 방식
            connection.setRequestMethod("POST");
            // 요청 응답 타임아웃
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setDoOutput(true);
            //Log.d("data", data);

            String postDataString = getPostString(postData);
            Log.d("POSTDATA", postDataString);

            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(postDataString);
            osw.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String json;

            // server로 부터 메시지 받아오는 곳

            while ((json = br.readLine()) != null) {
                sb.append(json);
                break;
            }
            Log.d("response", sb.toString().trim());
            jsonParsing(sb.toString().trim());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =key&value 형태로 Map을 변경해주는 메소드
    private static String getPostString(HashMap<String, String> map) {
        StringBuilder result = new StringBuilder();
        boolean first = true; // 첫 번째 매개변수 여부

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first)
                first = false;
            else // 첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
                result.append("&");

            try { // UTF-8로 주소에 키와 값을 붙임
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ue) {
                ue.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    public static void jsonParsing(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);

            // JSON 오브젝트 한개만 받아옴
            JSONObject jsonObject = jsonArray.getJSONObject(0);


            if (jsonObject.has("id")) {
                resId = jsonObject.getString("id");
                Log.d("ResID", resId);
            }

            if (jsonObject.has("no")) {
                resNo = jsonObject.getString("no");
                Log.d("ResNO", resNo);
            }

            if (jsonObject.has("state")) {
                resState = jsonObject.getString("state");
                Log.d("ResSTATE", resState);
            }

            if (jsonObject.has("distance")) {
                resDistance = jsonObject.getString("distance");
                Log.d("ResDistance", resDistance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
