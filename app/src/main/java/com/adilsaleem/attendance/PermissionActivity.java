package com.adilsaleem.attendance;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.adilsaleem.attendance.qrcodescanner.QrCodeActivity;

public class PermissionActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 122;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 121;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private Switch gpsSwitch;
    private Switch cameraSwitch;
    private Button goButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        setTitle(R.string.permission_title);

        gpsSwitch = findViewById(R.id.gps_switch);
        cameraSwitch = findViewById(R.id.camera_switch);
        goButton = findViewById(R.id.go_button);

        if (checkLocationPermission()) {
            gpsSwitch.setChecked(true);
            gpsSwitch.setEnabled(false);
            gpsSwitch.setText("Enabled");
        }else{
            gpsSwitch.setChecked(false);
        }

        if (checkCameraPermission()) {
            cameraSwitch.setChecked(true);
            cameraSwitch.setEnabled(false);
            cameraSwitch.setText("Enabled");
        }else{
            cameraSwitch.setChecked(false);
        }

        cameraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestCameraPermission();
                    cameraSwitch.setEnabled(false);
                    cameraSwitch.setText("Enabled");
                }
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestLocationPermission();
                    gpsSwitch.setEnabled(false);
                    gpsSwitch.setText("Enabled");
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCameraPermission() && checkLocationPermission()){
                    Intent i = new Intent(PermissionActivity.this,QrCodeActivity.class);
                    startActivityForResult( i,REQUEST_CODE_QR_SCAN);
                }else{
                    Toast.makeText(PermissionActivity.this, getResources().getText(R.string.get_all_permission), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_REQUEST_CAMERA){
            if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {
                cameraSwitch.setChecked(false);
                cameraSwitch.setEnabled(true);
                cameraSwitch.setText("");
            }
        }else if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            if (PackageManager.PERMISSION_GRANTED != grantResults[0] || PackageManager.PERMISSION_GRANTED != grantResults[1]) {
                gpsSwitch.setChecked(false);
                gpsSwitch.setEnabled(true);
                gpsSwitch.setText("");
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != AppCompatActivity.RESULT_OK)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }
}
