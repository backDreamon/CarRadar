package com.example.back.clientradar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class RegistActivity extends AppCompatActivity {

    Button btRegister;
    Button btBackAct;
    EditText et_id;
    EditText et_pwd;
    EditText et_phone;
    EditText et_car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        btRegister = (Button) findViewById(R.id.bt_register);
        btBackAct = (Button) findViewById(R.id.bt_back);

        et_id = (EditText) findViewById(R.id.et_reg_id);
        et_pwd = (EditText) findViewById(R.id.et_reg_pwd);
        et_phone = (EditText) findViewById(R.id.et_reg_phone);
        et_car = (EditText) findViewById(R.id.et_reg_car);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                String password = et_pwd.getText().toString();
                String phone = et_phone.getText().toString();
                String cars_id = et_car.getText().toString();

                RegisterData rd = new RegisterData(id, password, phone, cars_id);
                rd.execute();

                Intent result = new Intent();
                result.putExtra("id", id);

                setResult(RESULT_OK, result);
                finish();
            }
        });

        btBackAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public class RegisterData extends AsyncTask<Void, Void, Void> {

        String id;
        String password;
        String phone;
        String cars_id;

        String dbAddress = "http://dev-dreamon.cloud.or.kr/join.php";

        public RegisterData (String id, String password, String phone, String cars_id) {
            this.id = id;
            this.password = password;
            this.phone = phone;
            this.cars_id = cars_id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String data = URLEncoder.encode("id","UTF-8")
                        + "=" + URLEncoder.encode(id,"UTF-8") +"&"
                        + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode(password,"UTF-8") +"&"
                        + URLEncoder.encode("phone", "UTF-8")
                        + "=" + URLEncoder.encode(phone,"UTF-8") +"&"
                        + URLEncoder.encode("cars_id", "UTF-8")
                        + "=" + URLEncoder.encode(cars_id,"UTF-8");
                URL url = new URL(dbAddress);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                Log.d("data", data);

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                osw.write(data);
                osw.flush();

                // server로 부터 메시지 받아오는 곳
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String msg = "";

                while ((msg = br.readLine()) != null) {
                    sb.append(msg);
                    break;
                }
                Log.d("response", sb.toString());




            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}


