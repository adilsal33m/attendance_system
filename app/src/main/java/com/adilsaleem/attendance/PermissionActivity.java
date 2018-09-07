package com.adilsaleem.attendance;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.adilsaleem.attendance.qrcodescanner.QrCodeActivity;

public class PermissionActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_PHONE = 123;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 122;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 121;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private Switch gpsSwitch;
    private Switch cameraSwitch;
    private Switch phoneSwitch;
    private Button goButton;
    LocationTrack locationTrack;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        setTitle(R.string.permission_title);

        name = getIntent().getStringExtra("name");
        gpsSwitch = findViewById(R.id.gps_switch);
        cameraSwitch = findViewById(R.id.camera_switch);
        goButton = findViewById(R.id.go_button);
        phoneSwitch = findViewById(R.id.phone_switch);

        if (checkLocationPermission()) {
            locationTrack = new LocationTrack(this);
            if(locationTrack.checkGPS) {
                gpsSwitch.setChecked(true);
                gpsSwitch.setEnabled(false);
                gpsSwitch.setText("Enabled");
            }else{
                locationTrack.showSettingsAlert();
            }
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

        if (checkPhonePermission()) {
            phoneSwitch.setChecked(true);
            phoneSwitch.setEnabled(false);
            phoneSwitch.setText("Enabled");
        }else{
            phoneSwitch.setChecked(false);
        }

        phoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    requestPhonePermission();
                    phoneSwitch.setEnabled(false);
                    phoneSwitch.setText("Enabled");
                }
            }
        });


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
                    gpsSwitch.setEnabled(false);
                    gpsSwitch.setText("Enabled");
                    requestLocationPermission();
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCameraPermission() && checkLocationPermission() && locationTrack.checkGPS && checkPhonePermission()){
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

    private void requestPhonePermission() {
        if (!checkPhonePermission()) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_PHONE);
        }
    }

    private boolean checkPhonePermission() {
        return ContextCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
        if(!locationTrack.checkGPS){
            gpsSwitch.setChecked(false);
            gpsSwitch.setEnabled(true);
            gpsSwitch.setText("");
            locationTrack.showSettingsAlert();
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
        }else if(requestCode == MY_PERMISSIONS_REQUEST_PHONE){
            if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {
                phoneSwitch.setChecked(false);
                phoneSwitch.setEnabled(true);
                phoneSwitch.setText("");
            }
        }else if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            if (PackageManager.PERMISSION_GRANTED != grantResults[0] || PackageManager.PERMISSION_GRANTED != grantResults[1]) {
                locationTrack = new LocationTrack(this);
                if (locationTrack.checkGPS){
                    gpsSwitch.setChecked(false);
                    gpsSwitch.setEnabled(true);
                    gpsSwitch.setText("");
                }else{
                    locationTrack.showSettingsAlert();
                }
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
            if (locationTrack.canGetLocation()) {
                double longitude = locationTrack.getLongitude();
                double latitude = locationTrack.getLatitude();
                TelephonyManager telephonyManager;
                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = telephonyManager.getDeviceId();

                //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Scan result");
                alertDialog.setMessage("ID: "+ name + "\nLongitude: " + Double.toString(longitude) + "\nLatitude: " + Double.toString(latitude)+
                        "\nDevice ID: "+deviceId + "\nTime: "+System.nanoTime());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                locationTrack.showSettingsAlert();
            }
        }
    }
}
