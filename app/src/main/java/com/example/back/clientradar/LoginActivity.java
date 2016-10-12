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

import java.util.HashMap;

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


    private HashMap<String, String> map;
    private int flag = 0;

    private String id = "";


    private GPSListener gps;


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

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.resState.equals("0")) {
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
                    Common.resState = "0";
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
                Log.d("SERVICE", "운행정지");
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

                Log.d("SERVICE", "운행시작");
                do {
                    map = new HashMap<>();
                    map.put("id", Common.resId);
                    map.put("no", Common.resNo);
                    map.put("lat", latitude + "");
                    map.put("lng", longitude + "");
                    map.put("flag", 2 + "");
                    Common.sendData(map);

                    Thread.sleep(2000);
                    if (Common.resState.equals("0")) {
                        break;
                    }

                } while (Common.resState.equals("1"));

                Log.d("SERVICE", "운행종료중");

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
        builder.setMessage("위치 정보 비활성화")
                .setCancelable(false)
                .setTitle("위치 정보")
                .setPositiveButton("활성화",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("취소",
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



}




