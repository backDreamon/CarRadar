package com.example.back.clientradar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaExtractor;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    private LocationManager locationManager = null; // 위치 정보 프로바이더
    private TextView geolat;
    private TextView geolng;
    private TextView status;
    private Button btnStart;
    //    private Button btnStop;
    private EditText startPoint;
    private EditText stopPoint;
    double latitude;
    double longitude;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    private String loginAddress = "http://dev-dreamon.cloud.or.kr/android.php";

    private HashMap<String, String> map;
    private int flag = 0;

    private UserLogin userLogin;
    private SendGPS sendGPS;
    private String id = "";

    // 서버로부터 response하는 값
    private String resId = "";
    private String resState = "0";
    private String resNo = "";
    private String resDistance = "";

    private int serviceState = 0;

    private HttpURLConnection connection;

    private GPSListener gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        String password = intent.getStringExtra("password");

        geolat = (TextView) findViewById(R.id.geolat);
        geolng = (TextView) findViewById(R.id.geolng);
        btnStart = (Button) findViewById(R.id.btnStart);
//        btnStop = (Button) findViewById(R.id.btnStop);
        startPoint = (EditText) findViewById(R.id.startPoint);
        stopPoint = (EditText) findViewById(R.id.stopPoint);
        status = (TextView) findViewById(R.id.status);

        map = new HashMap<>();
        map.put("id", id);
        map.put("password", password);
        map.put("flag", flag + "");
        userLogin = new UserLogin(map, flag);
        userLogin.execute();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resState.equals("0")) {
                    getLocationService();

                    new SendGPS().execute();

                    btnStart.setText("운행종료");
                } else {
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    locationManager.removeUpdates(gps);
                    resState = "0";
                    btnStart.setText("운행시작");

                }
            }
        });
       /* btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                            }
        });*/


    }


    private class SendGPS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            map = new HashMap<String, String>();

            if (resState.equals("0")) {
                resState = "1";
                flag = 1;
                map.put("id", resId);
                map.put("state", resState);
                map.put("flag", flag + "");
                map.put("start_point", startPoint.getText().toString());
                map.put("stop_point", stopPoint.getText().toString());
                map.put("type", "0");

                Toast.makeText(LoginActivity.this, "운행시작", Toast.LENGTH_SHORT).show();
            }

            startPoint.setEnabled(false);
            stopPoint.setEnabled(false);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (resState.equals("0")) {
                Toast.makeText(LoginActivity.this, "운행정지", Toast.LENGTH_SHORT).show();
                Log.d("SERVICE", "운행정지");
            }
            startPoint.setEnabled(true);
            stopPoint.setEnabled(true);
            status.setText("총 이동 거리 : " + resDistance + "Km");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d("onProgress", "Doing...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                sendData(map);

                Log.d("SERVICE", "운행시작");
                do {
                    map = new HashMap<>();
                    map.put("id", id);
                    map.put("no", resNo);
                    map.put("lat", latitude + "");
                    map.put("lng", longitude + "");
                    map.put("flag", 2 + "");
                    sendData(map);

                    Thread.sleep(2000);
                    if(resState.equals("0")) {
                        break;
                    }

                } while (resState.equals("1"));

                Log.d("SERVICE", "운행종료중");

                map = new HashMap<>();
                map.put("id", id);
                map.put("no", resNo);
                map.put("flag", 3 + "");
                map.put("state", resState);
                sendData(map);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class UserLogin extends AsyncTask<Void, Void, Void> {
        private HashMap<String, String> postData;

        public UserLogin(HashMap<String, String> map, int flag) {
            this.postData = map;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.d("FLAG", flag + "");
                sendData(postData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(id.equals(resId)) {
                Toast.makeText(LoginActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();
            } else {

            }

        }
    }

    private void sendData(HashMap<String, String> postData) {
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
    private String getPostString(HashMap<String, String> map) {
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

    private void getLocationService() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // GPS 프로바이더 사용가능여부
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Main", "isGPSEnabled=" + isGPSEnabled);
        Log.d("Main", "isNetworkEnabled=" + isNetworkEnabled);


        if (isGPSEnabled || isNetworkEnabled) {


            gps = new GPSListener();

            //퍼미션 체크
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            /*
                GPS 프로바이더는 실내에서 작동하지 않기때문에 NETWORK 프로바이더로 전환한다
             */
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, gps); //GPS 프로바이더 로 위치값 받아오기
            else if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, gps); //NETWORK 프로바이더로 위치값 받아오기

        } else {
            alertbox("GPS", "GPS를 켜주세요!");
        }


    }

    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            geolat.setText(latitude + "");
            geolng.setText(longitude + "");

            Log.d("lat", geolat.getText().toString());
            Log.d("lng", geolng.getText().toString());


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("osc", provider);
            Log.d("osc", status + "");
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void jsonParsing(String json) {
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




