package com.example.weather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1 ;
    LinearLayout whole;
    TextView temp;
    TextView city;
    TextView minmax;
    LocationManager locationManager;
    String latitude;
    String longitude;
    String apikey = "76d042c17320259413cd6a9a2788caa5";


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                minmax.setText("Request Failed");
            } else if(msg.what == 1) {
                minmax.setText((String)msg.obj);
                try {
                    JSONObject obj = new JSONObject((String)msg.obj);
                    int _temp = (int)(obj.getJSONObject("main").getDouble("temp"));
                    temp.setText(String.valueOf(_temp)+"°C");
                    int _min = (int)(obj.getJSONObject("main").getDouble("temp_min"));
                    int _max = (int)(obj.getJSONObject("main").getDouble("temp_max"));
                    minmax.setText(String.valueOf(_min)+" °/ " +String.valueOf(_max)+" °");
                    String _city = (obj.getString("name"));
                    Log.d("My App", obj.toString());
                    city.setText(_city);
                    String weather = new JSONObject(obj.getJSONArray("weather").get(0).toString()).getString("main").toString();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        switch (weather){
                            case "Rain":
                                whole.setBackgroundResource(R.drawable.rain);
                                break;
                            case "Clear":
                                whole.setBackgroundResource(R.drawable.sunny);
                                break;
                            case "Mist":
                                whole.setBackgroundResource(R.drawable.mist);
                                break;
                            case "Clouds":
                                whole.setBackgroundResource(R.drawable.cloudy);
                                break;
                            default:
                                break;
                        }

                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON");
                }
            }
        }
    };
    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

            }
            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }

            //Thats All Run Your App
        }

    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temp = findViewById(R.id.temp);
        minmax = findViewById(R.id.minmax);
        city = findViewById(R.id.city);
        whole = findViewById(R.id.whole);
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Check gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Write Function To enable gps

            OnGPS();
        }
        else
        {
            //GPS is already On then

            getLocation();
        }


    }
    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
        HttpThread httpThread = new HttpThread("https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&APPID="+apikey+"&units=metric");
        httpThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }




    class HttpThread extends Thread {
        private String url;

        public HttpThread(String url) {
            this.url = url;
        }
        @Override
        public void run() {
            try {
                URL serverUrl = new URL(this.url);
                HttpURLConnection http = (HttpURLConnection)serverUrl.openConnection();
                http.setRequestMethod("GET");
                http.setConnectTimeout(10*1000);
                http.setReadTimeout(10*1000);
                http.setDoInput(true);
                http.setDoOutput(false);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                http.getInputStream(), "UTF-8"));
                StringBuffer sb = new StringBuffer();
                String strLine = null;
                while((strLine = in.readLine()) != null) {
                    sb.append(strLine);
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = sb.toString();
                handler.sendMessage(msg);
            } catch (Exception e) {
                handler.sendEmptyMessage(0);
            }
        }
    }
}
