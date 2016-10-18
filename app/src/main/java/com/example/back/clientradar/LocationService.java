package com.example.back.clientradar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by back on 2016-10-12.
 */

public class LocationService extends Service {

    // 위치 불러오는 시간
    private static final int LOCATION_INTERVAL = 5000;
    // 위치 불러오는 거리
    private static final float LOCATION_DISTANCE = 20f;

    private LocationManager locationManager;
    public PowerManager.WakeLock wakeLock;

    public static double latitude;
    public static double longitude;

    public GPSListener gps;

    private boolean isNetworkEnabled;
    private boolean isGPSEnabled;
    private Location location;


    public LocationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        wakeLock.acquire();

        Toast.makeText(this, "위치정보를 수신중입니다", Toast.LENGTH_SHORT).show();

        super.onCreate();

        Log.e("SERVICE", "CREATED");
    }

    @Override
    public void onDestroy() {

        stopSelf();
        wakeLock.release();
        super.onDestroy();

        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Common.resState = "0"; // 조욜

        locationManager.removeUpdates(gps);


        Log.e("SERVICE", "DESTROYED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("SERVICE", "STARTED");

        getLocation();

        return super.onStartCommand(intent, flags, startId);
    }

    private void getLocation() {

        if (Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            gps = new GPSListener();

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.e("STATUS GPS", String.valueOf(isGPSEnabled));
            Log.e("STATUS NETWORK", String.valueOf(isNetworkEnabled));

            if (!isNetworkEnabled && !isGPSEnabled) {
                /*if (!isGPSEnabled){
                    Log.d("GPS", "NONONONONONO");

                    Intent setIntent = new Intent(this, AlertDialogGPS.class);
                    setIntent.addFlags(setIntent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, setIntent, PendingIntent.FLAG_ONE_SHOT);

                    try {
                        pi.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return;
                }*/
                onDestroy();
                return;
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            LOCATION_INTERVAL,
                            LOCATION_DISTANCE,
                            gps); //NETWORK
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                } else if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            LOCATION_INTERVAL,
                            LOCATION_DISTANCE,
                            gps);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GPSListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                Log.e("Location", "NULL");
                return;
            } else {
                Log.e("Location", "CHANGED");
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                LoginActivity.geolat.setText(latitude + "");
                LoginActivity.geolng.setText(longitude + "");

                Log.d("LATITUDE", String.valueOf(latitude));
                Log.d("LONGITUDE", String.valueOf(longitude));

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
