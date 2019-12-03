package com.harshit.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText cityName, weatherUpdate;

    public class DownloadWeb extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url ;
            HttpURLConnection connections = null;
            try {

                url = new URL(urls[0]);
                connections = (HttpURLConnection) url.openConnection();
                InputStream in = connections.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){

                    result = result + (char)data;
                    data = reader.read();
                }
                return result;


            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        protected  void onPostExecute(String result){
            super.onPostExecute(result);
            try{

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                String tempInfo = jsonObject.getString("main");
                JSONArray weatherArray = new JSONArray(weatherInfo);
                JSONObject tempObject = new JSONObject(tempInfo);
                weatherUpdate.setSingleLine(false);
                weatherUpdate.setText("Main : " + weatherArray.getJSONObject(0).getString("main") + "\n"  +
                                      "Description : " + weatherArray.getJSONObject(0).getString("description") + "\n" +
                                      "Min. Temperature(Fahrenheit) : " + tempObject.getString("temp_min") + "\n" +
                                      "Max Temperature(Fahrenheit) : " + tempObject.getString("temp_max"));


            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to Find City", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick (View view){

        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert keyboard != null;
        keyboard.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
        DownloadWeb downloadWeb = new DownloadWeb();

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            String result = downloadWeb.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&APPID=32f9dae84ef415a9d8f68165430b278d").get();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to Find Weather", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.editText);
        weatherUpdate = findViewById(R.id.editText2);
    }
}
