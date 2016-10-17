package com.example.back.clientradar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private LocationManager locationManager = null; // 위치 정보 프로바이더
    public static TextView geolat;
    public static TextView geolng;
    private TextView status;
    private Button btnStart;
    //    private Button btnStop;
    private EditText startPoint;
    private EditText stopPoint;
    private Chronometer drivingTime;

    private HashMap<String, String> map;
    private int flag = 0;

    private String id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        geolat = (TextView) findViewById(R.id.geolat);
        geolng = (TextView) findViewById(R.id.geolng);
        btnStart = (Button) findViewById(R.id.btnStart);
        startPoint = (EditText) findViewById(R.id.startPoint);
        stopPoint = (EditText) findViewById(R.id.stopPoint);
        status = (TextView) findViewById(R.id.status);
        drivingTime = (Chronometer) findViewById(R.id.chronometer);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);

                if (Common.resState.equals("0")) {
                    drivingTime.setBase(SystemClock.elapsedRealtime());
                    startService(serviceIntent);
                    SendGPS sendGPS = new SendGPS();
                    sendGPS.execute();
                    drivingTime.start();
                    btnStart.setText("운행종료");
                } else {
                    Common.resState = "0";
                    stopService(serviceIntent);
                    drivingTime.stop();
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

            if (Common.resState.equals("0")) {
                Common.resState = "1";
                flag = 1;
                map.put("id", Common.resId);
                map.put("state", Common.resState);
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
            if (Common.resState.equals("0")) {
                Toast.makeText(LoginActivity.this, "운행정지", Toast.LENGTH_SHORT).show();
                Log.e("SERVICE", "운행정지");
            }
            startPoint.setEnabled(true);
            stopPoint.setEnabled(true);
            status.setText("총 이동 거리 : " + Common.resDistance + "Km");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d("onProgress", "Doing...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Common.sendData(map);

                Log.e("SERVICE", "운행시작");
                do {
                    map = new HashMap<>();
                    map.put("id", Common.resId);
                    map.put("no", Common.resNo);
                    map.put("lat", LocationService.latitude + "");
                    map.put("lng", LocationService.longitude + "");
                    map.put("flag", 2 + "");
                    Common.sendData(map);

                    Thread.sleep(5000);

                    if (Common.resState.equals("0")) {
                        break;
                    }

                } while (Common.resState.equals("1"));

                Log.e("SERVICE", "운행종료중");

                map = new HashMap<>();
                map.put("id", Common.resId);
                map.put("no", Common.resNo);
                map.put("flag", 3 + "");
                map.put("state", Common.resState);
                Common.sendData(map);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {

        }
    }
}




