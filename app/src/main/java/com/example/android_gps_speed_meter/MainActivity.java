package com.example.android_gps_speed_meter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.android_gps_speed_meter.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.util.Rational;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1000;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //check for gps permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            //permission granted
            speedCalculation();
        }

        this.updateSpeed(null);

        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.this.updateSpeed(null);
            }
        });


        binding.fabPip.setOnClickListener(View-> {
            enterPipMode();
        });
    }

    public void speedCalculation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        Toast.makeText(this,"waiting for GPS connection!",Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CLocation location){
        float nCurrentSpeed = 0;
        if (location != null){
            //location.setbUserMetricUnits(this.userMetricUnit());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US,"%3.1f",nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ","0");

        if (this.userMetricUnit()){
            //true means meter system
            binding.tvSpeedMainActivity.setText(strCurrentSpeed + "km/h");
        } else {
            //false means mile system
            binding.tvSpeedMainActivity.setText(strCurrentSpeed + "miles/h");
        }

    }

    private boolean userMetricUnit(){
        return binding.switch1.isChecked();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

        if (location!=null){
            CLocation myLocation = new CLocation(location,this.userMetricUnit()) ;
            this.updateSpeed(myLocation);
        }
    }


    // Override the onRequestPermissionsResult() method to handle the user's response
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing the user's location
                speedCalculation();
            } else {
                // Permission denied, handle the error or inform the user
                finish();
            }
        }
    }

    //This method is triggered when
    //Home button is pressed.
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        enterPipMode();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode){
            binding.switch1.setVisibility(View.GONE);
            binding.fabPip.setVisibility(View.GONE);

        } else {
            binding.switch1.setVisibility(View.VISIBLE);
            binding.fabPip.setVisibility(View.VISIBLE);
        }
    }

    private void enterPipMode() {
        ArrayList<RemoteAction> actions = new ArrayList<>();

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nazmos-sakib/GPS_Speed_Meter")),
                    PendingIntent.FLAG_MUTABLE
            );
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nazmos-sakib/GPS_Speed_Meter")),
                    PendingIntent.FLAG_ONE_SHOT
            );
        }


        RemoteAction remoteAction = new RemoteAction(
                Icon.createWithResource(this,R.drawable.ic_info_24),
                "Info","Info Details",
                pendingIntent
                );

        actions.add(remoteAction);
        Rational aspectRatio = new Rational(1, 1);

        PictureInPictureParams params = new PictureInPictureParams
                .Builder()
                .setAspectRatio(aspectRatio)
                .setActions(actions)
                .build();
        enterPictureInPictureMode(params);
    }

}