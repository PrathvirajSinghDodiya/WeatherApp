package com.example.weatherapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class WeatherActivity extends AppCompatActivity {
    private TextView cityName, tempshow, weatherForecast, conditionShow,humidity;
    private EditText enterCity;
    LottieAnimationView  weathImage;
    private ImageView SearchImg;
    private RecyclerView recyclerView;
    private ArrayList<WeatherModel> weatherModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private String newCity="Delhi";
    final private  int PERMISSION_CODE = 1;
    ConstraintLayout constraintLayout;
    ProgressBar progressBar;
    LinearLayout layout,layout2,layout3;
    Animation animation , animation2;
    CardView cardView;
    private static final String CHANNEL_ID = "com.example.notification.notify";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        cityName = findViewById(R.id.CityName);
        tempshow = findViewById(R.id.TempShowText);
        weatherForecast = findViewById(R.id.WeatherForecast);
        conditionShow = findViewById(R.id.condition);
        enterCity = findViewById(R.id.citySearch);
        weathImage = findViewById(R.id.WeatherImage);
        humidity = findViewById(R.id.humidity);
        SearchImg = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.loop);
        constraintLayout = findViewById(R.id.constrat);
        layout = findViewById(R.id.linu);
        layout3 = findViewById(R.id.linearSearchTab);
        layout2 = findViewById(R.id.linearLayoutAnimation);
        cardView = findViewById(R.id.cardViewLayout);
        weatherModelArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModelArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(weatherAdapter);
        animation = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        layout2.setAnimation(animation);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        }


        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);






       try {
           newCity = getLocation(location.getLatitude(),location.getLongitude());
       }catch (Exception e){
           e.printStackTrace();
       }
        getWeatherInfo(newCity);
        SearchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String getCityName = enterCity.getText().toString();
                if(getCityName.isEmpty()){
                    Toast.makeText(WeatherActivity.this,"Please enter your City name...",Toast.LENGTH_SHORT).show();
                }else {
                    getWeatherInfo(getCityName);
                }
            }
        });
        enterCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(WeatherActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(WeatherActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }

        }
    }

    private String getLocation(double latitude, double longitude){
        String city_name = "Delhi";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);

            for(Address adr: addresses){
               // Toast.makeText(WeatherActivity.this,"adr"+adr.toString(),Toast.LENGTH_SHORT).show();
                if(adr!=null){
                    String newCitynew = adr.getLocality();
                    if(newCitynew!=null){
                        city_name=newCitynew;
                        //Toast.makeText(WeatherActivity.this,"city"+city_name,Toast.LENGTH_SHORT).show();

                    }
                    else {
                       // Toast.makeText(WeatherActivity.this,"...NOT FOUND..."+newCitynew,Toast.LENGTH_LONG).show();
                        Log.i("Tag","City_name...notfound");
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  city_name;

    }




    private void getWeatherInfo(String cityName1) {

        cityName.setText(cityName1);
        //Toast.makeText(WeatherActivity.this, "cityName" + cityName1, Toast.LENGTH_SHORT).show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://api.weatherapi.com/v1/forecast.json?key=3bf906b9ab144746a22121728210610&q="+cityName1+"&days=1&aqi=no&alerts=no", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                weatherModelArrayList.clear();
                try {
                    String apiTemp = response.getJSONObject("current").getString("temp_c");
                    tempshow.setText(apiTemp + "°c");
                    int apiDay = response.getJSONObject("current").getInt("is_day");
                    String apiConditionImage = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                    int hmudty = response.getJSONObject("current").getInt("humidity");
                    humidity.setText("Humidity "+hmudty+"%");

                    String apiCondition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    conditionShow.setText(apiCondition);
                            if(apiDay==1){
                                constraintLayout.setBackground(getDrawable(R.drawable.back_two));
                                layout.setBackground(getDrawable(R.drawable.back_one));
                                layout2.setBackground(getDrawable(R.drawable.bacground));
                                layout3.setBackgroundColor(ContextCompat.getColor(WeatherActivity.this,R.color.mycolor55));

                                if(apiCondition.equals("Mist")==true)
                                {
                                   // Toast.makeText(WeatherActivity.this,"this is working",Toast.LENGTH_SHORT).show();
                                    weathImage.setAnimation(R.raw.weathermist);
                                }
                                else  if (apiCondition.equals("Partly cloudy")==true){
                                    weathImage.setAnimation(R.raw.weatherpartlycloudy);

                                }else  if (apiCondition.equals("Sunny")==true){
                                    weathImage.setAnimation(R.raw.clearsky);
                                }else  if (apiCondition.equals("Cloudy")==true){
                                    weathImage.setAnimation(R.raw.weathercloudy);
                                }else if(apiCondition.equals("Light rain")==true|| apiCondition.equals("Moderate rain")==true || apiCondition.equals("Light drizzle")==true){
                                    weathImage.setAnimation(R.raw.lightrain);
                                }
                                else if(apiCondition.equals("Patchy light rain with thunder")==true){
                                    weathImage.setAnimation(R.raw.weatherdaythunderstorm);
                                }
                                else if(apiCondition.equals("Overcast")==true){
                                    weathImage.setAnimation(R.raw.overcast);
                                }else if(apiCondition.equals("Light snow")==true){
                                    weathImage.setAnimation(R.raw.weathersnow);
                                }
                                else {
                                    Picasso.get().load(apiCondition).into(weathImage);
                                }
                                weathImage.playAnimation();
                            }
                            else {
                                layout3.setBackgroundColor(ContextCompat.getColor(WeatherActivity.this,R.color.mycolor56));

                                constraintLayout.setBackground(getDrawable(R.drawable.nightback));
                                layout.setBackground(getDrawable(R.drawable.backrecycle));
                                layout2.setBackground(getDrawable(R.drawable.background2));
                                if(apiCondition.equals("Mist")==true)
                                {
                                    //Toast.makeText(WeatherActivity.this,"this is working",Toast.LENGTH_SHORT).show();
                                    weathImage.setAnimation(R.raw.weathernightmist);
                                }
                                else  if (apiCondition.equals("Partly cloudy")==true){
                                    weathImage.setAnimation(R.raw.weathernightpartlyclouds);

                                }else  if (apiCondition.equals("Clear")==true){
                                    weathImage.setAnimation(R.raw.weathernightclearskydark);
                                }else  if (apiCondition.equals("Cloudy")==true){
                                    weathImage.setAnimation(R.raw.weathernightpartlyclouds);
                                }
                                else if(apiCondition.equals("Patchy light rain with thunder")==true){
                                    weathImage.setAnimation(R.raw.weathernightthunderstorm);
                                }
                                else if(apiCondition.equals("Moderate rain")==true || apiCondition.equals("Light drizzle")==true){
                                    weathImage.setAnimation(R.raw.lightrain);
                                }
                                else if(apiCondition.equals("Light rain")==true){
                                    weathImage.setAnimation(R.raw.weatherdayrain);
                                }
                                else if(apiCondition.equals("Overcast")==true){
                                    weathImage.setAnimation(R.raw.overcast);
                                }
                                else if(apiCondition.equals("Light snow")==true){
                                    weathImage.setAnimation(R.raw.weathernightsnow);
                                }else {


                                }
                                weathImage.playAnimation();

                            }

                    //notification start.................

                    if(apiCondition.equals("Light rain")==true||apiCondition.equals("Moderate rain")==true || apiCondition.equals("Light drizzle")==true){
                        createNotificationChannel();
                        Intent intent = new Intent(WeatherActivity.this, AlertDetails.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(WeatherActivity.this, 0, intent, 0);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(WeatherActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle("Hey wait....")
                                .setContentText("It's raining, don't forget to take your umbrella...Have a nice day")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(WeatherActivity.this);
                        int notificationId = 512333;
                        notificationManager.notify(notificationId, builder.build());
                    }



                    //notification end.........................



                    cardView.setVisibility(View.VISIBLE);
                   //layout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastArray = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastArray.getJSONArray("hour");
                    //Toast.makeText(WeatherActivity.this, "Check--"+hourArray.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("check1----",hourArray.toString());
                    Log.i("check2----", String.valueOf(hourArray.length()));
                    for(int i=0;i< hourArray.length();i++){
                        //Toast.makeText(WeatherActivity.this,"num"+i,Toast.LENGTH_SHORT).show();
                        //Toast.makeText(WeatherActivity.this,"array:-"+hourArray.length(),Toast.LENGTH_SHORT).show();
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String windSpeed = hourObj.getString("wind_kph");
                        //Toast.makeText(WeatherActivity.this, "Check:---"+time+temp+img+windSpeed, Toast.LENGTH_SHORT).show();
                        weatherModelArrayList.add(new WeatherModel(time, temp, img, windSpeed,apiDay));
                    }
                    weatherAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WeatherActivity.this,"error:-please check your internet connection...",Toast.LENGTH_SHORT).show();
                Toast.makeText(WeatherActivity.this, "OR Entered CityNot found", Toast.LENGTH_SHORT).show();
                //Log.i("Tag",error.getMessage());

            }
        });
        queue.add(jsonObjectRequest);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
