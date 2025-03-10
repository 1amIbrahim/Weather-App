package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    EditText city;
    Button search;
    TextView current,min,max,humidityView, windSpeedView, windDirectionView, weatherConditionView, error;
    ImageView image;
    ConstraintLayout layout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.city_id);
        search = findViewById(R.id.search_id);
        min = findViewById(R.id.minid);
        max = findViewById(R.id.maxid);
        current = findViewById(R.id.currentid);
        humidityView = findViewById(R.id.humidity_id);
        windSpeedView = findViewById(R.id.wind_speed_id);
        windDirectionView = findViewById(R.id.wind_direction_id);
        weatherConditionView = findViewById(R.id.weather_condition_id);
        error = findViewById(R.id.error_id);
        image = findViewById(R.id.ZAimage);
        layout = findViewById(R.id.main);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                error.setText("");
                String c = city.getText().toString();

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        HttpURLConnection urlConnection = null;
                        try {
                            URL u = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + c + "&appid=0cc4a362beb590b6f2acb5872e562a53");

                            urlConnection = (HttpURLConnection) u.openConnection();
                            urlConnection.setRequestMethod("GET");
                            urlConnection.connect();
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream()));
                                char[] buffer = new char[100000];
                                int count = br.read(buffer);

                                String s = new String(buffer, 0, count);

                                JSONObject j = new JSONObject(s);

                                JSONObject main = j.getJSONObject("main");

                                String tempString = main.getString("temp");
                                String minTempString = main.getString("temp_min");
                                String maxTempString = main.getString("temp_max");

                                Double temp = Double.parseDouble(tempString) - 273.15;
                                Double tmin = Double.parseDouble(minTempString) - 273.15;
                                Double tmax = Double.parseDouble(maxTempString) - 273.15;

                                int humidity = main.getInt("humidity");

                                JSONObject wind = j.getJSONObject("wind");
                                Double windSpeed = wind.getDouble("speed");
                                int windDirection = wind.getInt("deg");

                                JSONArray weatherArray = j.getJSONArray("weather");
                                String weatherCondition = weatherArray.getJSONObject(0).getString("description");


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        min.setText(String.format("Minimum Temperature: " + "%.2f °C", tmin));
                                        max.setText(String.format("Max Temperature: " + "%.2f °C", tmax));
                                        current.setText(String.format("Current Temperature: " + "%.2f °C", temp));
                                        humidityView.setText("Humidity: " + humidity + "%");
                                        windSpeedView.setText("Wind Speed: " + windSpeed + " m/s");
                                        windDirectionView.setText("Wind Direction: " + windDirection + "°");
                                        weatherConditionView.setText("Condition: " + weatherCondition);

                                        if (weatherCondition.equals("clear sky")) {
                                            image.setImageResource(R.drawable.d01);
                                            layout.setBackgroundResource(R.drawable.clear_sky);
                                        } else if (weatherCondition.equals("few clouds")) {
                                            image.setImageResource(R.drawable.d02);
                                            layout.setBackgroundResource(R.drawable.clouds);
                                        } else if (weatherCondition.equals("scattered clouds")) {
                                            image.setImageResource(R.drawable.d03);
                                            layout.setBackgroundResource(R.drawable.clouds);
                                        } else if (weatherCondition.equals("broken clouds")) {
                                            image.setImageResource(R.drawable.d04);
                                            layout.setBackgroundResource(R.drawable.clouds);
                                        } else if (weatherCondition.equals("shower rain")) {
                                            image.setImageResource(R.drawable.d09);
                                            layout.setBackgroundResource(R.drawable.rain);
                                        } else if (weatherCondition.equals("rain")) {
                                            image.setImageResource(R.drawable.d10);
                                            layout.setBackgroundResource(R.drawable.rain);
                                        } else if (weatherCondition.equals("light rain")) {
                                            image.setImageResource(R.drawable.d10);
                                            layout.setBackgroundResource(R.drawable.rain);
                                        } else if (weatherCondition.equals("heavy intensity rain")) {
                                            image.setImageResource(R.drawable.d10);
                                            layout.setBackgroundResource(R.drawable.rain);
                                        } else if (weatherCondition.equals("thunderstorm")) {
                                            image.setImageResource(R.drawable.d11);
                                            layout.setBackgroundResource(R.drawable.storm);
                                        } else if (weatherCondition.equals("snow")) {
                                            image.setImageResource(R.drawable.d13);
                                            layout.setBackgroundResource(R.drawable.snow);
                                        } else {
                                            image.setImageResource(R.drawable.d50);
                                            layout.setBackgroundResource(R.drawable.mist);
                                        }
                                    }
                                });
                            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        layout.setBackgroundResource(R.drawable.error);
                                        error.setText("City Not Found");

                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        layout.setBackgroundResource(R.drawable.no_internet);

                                    }
                                });
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    layout.setBackgroundResource(R.drawable.no_internet);

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
        });
    }
}