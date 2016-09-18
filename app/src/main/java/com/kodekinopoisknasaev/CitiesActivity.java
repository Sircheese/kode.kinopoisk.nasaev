package com.kodekinopoisknasaev;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import java.util.ArrayList;

import com.kodekinopoisknasaev.DataClasses.City;

public class CitiesActivity extends AppCompatActivity {
    private ListView mcitiesListView;
    private EditText mchooseCityField;
    private ArrayList<City> mcitiesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mcitiesListView = (ListView) findViewById(R.id.citiesListView);

        mchooseCityField = (EditText) findViewById(R.id.chooseCityField);

        mcitiesArray = new ArrayList<>();
        try {
            InputStream is = this.getResources().openRawResource(R.raw.cities);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);

            String jsontext = new String(buffer);
            JSONArray entries = new JSONArray(jsontext);

            ListAdapter adapter = new SimpleAdapter(this, mcitiesArray, R.layout.cities_list,
                    new String[] {City.cityName},
                    new int [] {R.id.cityName});

            mcitiesListView.setAdapter(adapter);

            for (int i = 0; i < entries.length(); i++) {
                JSONObject post = entries.getJSONObject(i);
                mcitiesArray.add(new City(post.getString("name"), post.getString("id")));
            }
            adapter.notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mcitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                mchooseCityField.setText(mcitiesArray.get(position).get("cityName"));
            }
        });
    }

    public void onClick(View view) {
        String currentCity = mchooseCityField .getText().toString().toLowerCase();
        boolean isCity = false;
        for (int i = 0; i < mcitiesArray.size(); i++) {
            City city = mcitiesArray.get(i);
            String citySTR = city.get("cityName").toLowerCase();//чтобы читалось немного лучше

            //тут я ищу нужный город, и, если он найден - сразу готовлю ответ в MaintActivity и заверщаю работу с этой активити
            if (citySTR.equals(currentCity)) {
                Intent answerIntent = new Intent();
                answerIntent.putExtra(R.string.intent_key + ".cityName", city.get("cityName"));
                answerIntent.putExtra(R.string.intent_key + ".cityID", city.get("cityID"));
                setResult(RESULT_OK, answerIntent);
                isCity = true;
                finish();
                break;
            }
        }
        if (!isCity) {
            Toast.makeText(this, "Город: " + currentCity + " не найден", Toast.LENGTH_SHORT).show();
        }
    }
}
