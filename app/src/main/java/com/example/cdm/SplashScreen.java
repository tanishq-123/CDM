package com.example.cdm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences pref;
    private ProgressDialog loadingbar;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loadingbar = new ProgressDialog(this);

        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences("user_details", MODE_PRIVATE);
        Toast.makeText(SplashScreen.this,pref.getString("Chapter",null),Toast.LENGTH_SHORT).show();

        if(!pref.contains("Chapter")){
            Toast.makeText(SplashScreen.this,"No location",Toast.LENGTH_SHORT).show();
            getlocation();
        }
        else{
            if (pref.contains("username") && pref.contains("password")) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashScreen.this, Login.class));
                finish();
            }
        }

    }
    public void getlocation(){
        if(ActivityCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(SplashScreen.this, "Permission granted", Toast.LENGTH_SHORT).show();
            getuserlocation();
        }
        else{
            ActivityCompat.requestPermissions(SplashScreen.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            getlocation();
           // Toast.makeText(Registration.this, "Click get location button again", Toast.LENGTH_SHORT).show();
        }
    }
    private void getuserlocation() {
        loadingbar = new ProgressDialog(this);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location=task.getResult();
                Toast.makeText(SplashScreen.this,"Location",Toast.LENGTH_SHORT).show();

                if(location!=null){
                    try {

                        loadingbar.setTitle("Location");
                        loadingbar.setMessage("Please wait, while we updating your location...");
                        loadingbar.show();
                        loadingbar.setCanceledOnTouchOutside(true);

                        Geocoder geocoder=new Geocoder(SplashScreen.this, Locale.getDefault());
                        List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                        String Chapter=addresses.get(0).getLocality();
                        SharedPreferences.Editor editor = getSharedPreferences("user_details", MODE_PRIVATE).edit();
                        editor.putString("Chapter",Chapter);
                        editor.apply();
                        SharedPreferences preferences=getSharedPreferences("user_details",MODE_PRIVATE);
                        Toast.makeText(SplashScreen.this,preferences.getString("Chapter",null),Toast.LENGTH_SHORT).show();

                        loadingbar.dismiss();
                        if (pref.contains("username") && pref.contains("password")) {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashScreen.this, Login.class));
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    public boolean isNetworkAvailable(ProgressDialog loadingbar) {

        Context context=SplashScreen.this;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) {loadingbar.dismiss(); return false;}
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            loadingbar.dismiss();
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            loadingbar.dismiss();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
}
