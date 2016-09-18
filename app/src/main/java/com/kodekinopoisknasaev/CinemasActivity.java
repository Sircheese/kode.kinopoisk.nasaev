package com.kodekinopoisknasaev;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.kodekinopoisknasaev.DataClasses.Cinema;
import com.kodekinopoisknasaev.DataClasses.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sirch_000 on 17.09.2016.
 */
public class CinemasActivity extends AppCompatActivity {
    static final public int CHOOSE_CITY = 2;

    private ListView mcinemasListView;
    private ArrayList<Cinema> mcinemasArrayList;
    private String mjsonResult;

    public ListAdapter adapter;
    public String cityName;
    public String cityID;
    public String filmNameRU;
    public String filmNameEN;
    public String filmID;
    @Override
    public void onCreate (Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_cinemas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cityName = getIntent().getExtras().getString(R.string.intent_key + ".mainCityName");
        cityID = getIntent().getExtras().getString(R.string.intent_key + ".mainCityID");
        filmNameRU = getIntent().getExtras().getString(R.string.intent_key + ".mainFilmNameRU");
        filmNameEN = getIntent().getExtras().getString(R.string.intent_key + ".mainFilmNameEN");
        filmID = getIntent().getExtras().getString(R.string.intent_key + ".mainFilmID");


        setTitle(cityName);

        mcinemasListView = (ListView) findViewById(R.id.cinemasList);
        mcinemasArrayList = new ArrayList<>();
        adapter = new SimpleAdapter(this, mcinemasArrayList, R.layout.cinemas_list,
                new String[] {Cinema.cinemaName, Cinema.cinemaAddress},
                new int[] {R.id.cinemaName, R.id.cinemaAddress});
        mcinemasListView.setAdapter(adapter);

        AsyncJson asyncJsonParser = new AsyncJson();
        String getQuery = "http://api.kinopoisk.cf/getCinemas?cityID=" + cityID;

        asyncJsonParser.execute(getQuery);

        mjsonResult = asyncJsonParser.getResultJson();

        JSONArray jsonArr = null;
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(mjsonResult);
            jsonArr = jsonObj.getJSONArray("items");

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject cinema = jsonArr.getJSONObject(i);

                mcinemasArrayList.add(new Cinema (cinema.getString("cinemaName"), cinema.getString("cinemaID"),
                        cinema.getString("address"), cinema.getString("lon"), cinema.getString("lat")));

                Cinema cinema1 = mcinemasArrayList.get(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mcinemasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Intent intent = new Intent(CinemasActivity.this, SeancesActivity.class);
                //без toString() не всегда работает. надо разобраться!
                intent.putExtra(R.string.intent_key + ".cinemaID", mcinemasArrayList.get(position).get("cinemaID").toString());
                intent.putExtra(R.string.intent_key + ".cinemaName", mcinemasArrayList.get(position).get("cinemaName").toString());
                intent.putExtra(R.string.intent_key + ".cinemaLon", mcinemasArrayList.get(position).get("cinemaLon").toString());
                intent.putExtra(R.string.intent_key + ".cinemaLat", mcinemasArrayList.get(position).get("cinemaLat").toString());
                intent.putExtra(R.string.intent_key + ".filmNameRU", filmNameRU).toString();
                intent.putExtra(R.string.intent_key + ".filmNameEN", filmNameEN).toString();
                intent.putExtra(R.string.intent_key + ".filmID", filmID).toString();
                intent.putExtra(R.string.intent_key + ".cityName", cityName).toString();
                intent.putExtra(R.string.intent_key + ".cityID", cityID).toString();
                startActivity(intent);
            }
        });
    }
}
