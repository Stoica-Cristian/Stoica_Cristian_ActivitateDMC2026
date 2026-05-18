package com.example.laborator10;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button buttonSearch;
    private TextView textViewResult;
    private Spinner spinnerDays;

    private static final String API_KEY = "REPLACE_WITH_ACCUWEATHER_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewResult = findViewById(R.id.textViewResult);
        spinnerDays = findViewById(R.id.spinnerDays);

        String[] options = {"1 zi", "5 zile"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDays.setAdapter(adapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString().trim();
                String selectedOption = spinnerDays.getSelectedItem().toString();

                String daysParam = selectedOption.equals("5 zile") ? "5day" : "1day";

                if (!city.isEmpty()) {
                    new WeatherAsyncTask().execute(city, daysParam);
                } else {
                    textViewResult.setText("Introduceți un oraș.");
                }
            }
        });
    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String cityNameQuery = params[0];
            String daysParam = params[1];

            String citySearchUrl = "https://dataservice.accuweather.com/locations/v1/cities/search?apikey="
                    + API_KEY + "&q=" + cityNameQuery;

            try {
                String searchResponse = makeHttpRequest(citySearchUrl);
                if (searchResponse == null) return "Error: Conexiune eșuată.";

                JSONArray cityArray = new JSONArray(searchResponse);
                if (cityArray.length() == 0) return "Error: Oraș negăsit.";

                JSONObject cityObject = cityArray.getJSONObject(0);
                String cityKey = cityObject.getString("Key");
                String localizedName = cityObject.getString("LocalizedName");

                String forecastUrl = "https://dataservice.accuweather.com/forecasts/v1/daily/"
                        + daysParam + "/" + cityKey + "?apikey=" + API_KEY + "&metric=true";

                String forecastResponse = makeHttpRequest(forecastUrl);
                if (forecastResponse == null) return "Error: Date meteo indisponibile.";

                JSONObject result = new JSONObject();
                result.put("cityName", localizedName);
                result.put("cityKey", cityKey);
                result.put("forecastData", new JSONObject(forecastResponse));

                return result.toString();

            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Error:")) {
                textViewResult.setText(result);
                return;
            }

            try {
                JSONObject json = new JSONObject(result);
                String cityName = json.getString("cityName");
                String cityKey = json.getString("cityKey");
                JSONArray dailyForecasts = json.getJSONObject("forecastData").getJSONArray("DailyForecasts");

                StringBuilder display = new StringBuilder();
                display.append("Oraș: ").append(cityName).append("\n");
                display.append("Cod (Key): ").append(cityKey).append("\n\n");

                for (int i = 0; i < dailyForecasts.length(); i++) {
                    JSONObject day = dailyForecasts.getJSONObject(i);
                    String date = day.getString("Date").substring(0, 10);
                    JSONObject temp = day.getJSONObject("Temperature");

                    double min = temp.getJSONObject("Minimum").getDouble("Value");
                    double max = temp.getJSONObject("Maximum").getDouble("Value");

                    display.append("Data: ").append(date)
                            .append("\n  Min: ").append(min).append("°C")
                            .append(", Max: ").append(max).append("°C\n\n");
                }

                textViewResult.setText(display.toString());

            } catch (JSONException e) {
                textViewResult.setText("Eroare parsare JSON.");
            }
        }

        private String makeHttpRequest(String urlString) throws Exception {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } finally {
                if (conn != null) conn.disconnect();
            }
        }
    }
}
