package com.example.alex.shouldiwashmycarr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.alex.shouldiwashmycarr.clients.WeatherData;
import com.example.alex.shouldiwashmycarr.clients.WeatherDay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.acl.Permission;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Button findOutButton;
    Context context;
    View snackbar;
    GoogleApiClient googleApiClient;
    Location googleLocation;
    private static String TAG = "shouldi.mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findOutButton = (Button)findViewById(R.id.find_out_btn);
        context = this;
        snackbar = (View)findViewById(R.id.snackbar);

        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }

        final int REQUEST_ACCESS_COARSE_LOCATION = 111;

        findOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Toast toast = Toast.makeText(context, "ACCESS COARSE LOCATION GRANTED", Toast.LENGTH_LONG);
                    toast.show();

                    if(googleLocation!=null) {
                        Log.i(TAG, String.valueOf(googleLocation.getLatitude()) +
                                ", " + String.valueOf(googleLocation.getLongitude()));
                    }

                    callWeatherApi();

                }else{
                    //called when the user has revoked the permission
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)){
                        Toast toast = Toast.makeText(context, "Permission revoked before", Toast.LENGTH_LONG);
                        toast.show();

                        Snackbar.make(snackbar, "We need access to your location in order to calculate " +
                                "if you can wash your car today", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);

                                    }
                                })
                                .show();



                    }else{

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);

                    }
                }
            }
        });

    }

    private String callWeatherApi() {
        String weather = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/forecast?lat=44.4268&lon=26.1025&mode=json&appid=e325be842a2673057367f4bad87b0b61")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String weather = response.body().string();

                Gson gson = new Gson();
                WeatherData weatherData = gson.fromJson(weather, WeatherData.class);

                Log.i(TAG, weather);

                Log.i(TAG, "City name: "+weatherData.getCity().getName());

                final boolean doesItRan =  doesItRain(weatherData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = doesItRan?"It rains, don't wash it!": "It doesn rain";
                        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });

        return weather;

    }


    Boolean doesItRain(WeatherData weatherData){
        Boolean shouldWash;
        for(WeatherDay weatherDay : weatherData.getList()){
            if(weatherDay.getWeather().get(0).getMain().equalsIgnoreCase("RAIN")){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults){
        switch (requestCode){
            case 111: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast toast = Toast.makeText(context, "Permission was granted", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    Toast permissionDenied = Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG);
                    permissionDenied.show();
                }
            }
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (googleApiClient != null) {
                googleLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if(googleLocation!=null) {
                    Log.i(TAG, String.valueOf(googleLocation.getLatitude()) +
                            ", " + String.valueOf(googleLocation.getLongitude()));
                }
            }
        }else{
            Log.w(TAG, "Permission not granted");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
