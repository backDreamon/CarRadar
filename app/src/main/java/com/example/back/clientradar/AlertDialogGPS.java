package com.example.back.clientradar;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlertDialogGPS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog_gps);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //set title
        alertDialogBuilder.setTitle("GPS 확인");

        //set content
        alertDialogBuilder
                .setMessage("GPS가 꺼져있습니다. 설정으로 이동하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
