package com.kodekinopoisknasaev;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kodekinopoisknasaev.DataClasses.*;


public class MainActivity extends AppCompatActivity {
    static final public int CHOOSE_CITY = 0;
    static final public int CHOOSE_FILTER = 1;

    private ArrayList<Film> mbufferFilmsArray;
    private ArrayList<Film> mfilmsArray;
    private ArrayList<Film> mfilmsListArray;
    private ListView mfilmsListView;
    private String mjsonResult;
    private String mcityID;
    private String mcityName = "";
    private String mgenre;

    public ListAdapter adapter;
    public CheckBox sortCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle(R.string.app_default_title);//TODO название перебивает при повороте телефона

        mfilmsListView = (ListView) findViewById(R.id.filmsListView);

        mfilmsArray = new ArrayList<>();
        mfilmsListArray = new ArrayList<>();
        mbufferFilmsArray = new ArrayList<>();
        adapter = new SimpleAdapter(this, mfilmsListArray, R.layout.films_list,
                new String [] {Film.filmNameRU, Film.filmNameEN, Film.filmGenre, Film.filmRating},
                new int[] {R.id.filmNameRU, R.id.filmNameEN, R.id.filmGenre, R.id.filmRating});
        mfilmsListView.setAdapter(adapter);

        //работа с json
        AsyncJson asyncJsonParser = new AsyncJson();
        asyncJsonParser.execute("http://api.kinopoisk.cf/getTodayFilms");
        mjsonResult = asyncJsonParser.getResultJson();

        JSONArray jsonArr = null;
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(mjsonResult);
            jsonArr = jsonObj.getJSONArray("filmsData");

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject film = jsonArr.getJSONObject(i);

                String nameRU = film.getString("nameRU");
                String nameEN = film.getString("nameEN");
                String id = film.getString("id");
                String genre = film.getString("genre");
                String[] ratingToken = film.getString("rating").split(" ");
                String rating = ratingToken[0];
                mfilmsListArray.add(new Film (nameRU, nameEN, id, genre, rating));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        copyFilms(mfilmsArray, mfilmsListArray);
        copyFilms(mbufferFilmsArray, mfilmsListArray);

        sortCheckBox = (CheckBox) findViewById(R.id.sortCheckBox);

        mfilmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                if (mcityName.equals("")) {
                    Toast.makeText(MainActivity.this, "Чтобы продолжить - выберите город", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, CitiesActivity.class);
                    startActivityForResult(intent, CHOOSE_CITY);
                } else {
                    if (!mcityName.equals("")) {
                        Intent intent = new Intent(MainActivity.this, CinemasActivity.class);
                        intent.putExtra(R.string.intent_key + ".mainCityID", mcityID);
                        intent.putExtra(R.string.intent_key + ".mainCityName", mcityName);

                        //не знаю как ловить объект. сделаю пока так
                        Film film = mfilmsListArray.get(position);
                        intent.putExtra(R.string.intent_key + ".mainFilmNameRU", film.get("filmNameRU").toString());
                        intent.putExtra(R.string.intent_key + ".mainFilmNameEN", film.get("filmNameEN").toString());
                        intent.putExtra(R.string.intent_key + ".mainFilmID", film.get("filmID").toString());

                        startActivity(intent);
                    }
                }
            }
        });
    }




    public void onSortCheckBoxClick (View view) {//Баги при повороте устройства. Думаю...
        if (sortCheckBox.isChecked()) {
            sortByRating(mfilmsListArray);
            mfilmsListView.setAdapter(adapter); // на notifyAll() приложение падает
        }
        else {
            copyFilms(mfilmsListArray, mbufferFilmsArray);
            mfilmsListView.setAdapter(adapter);
        }
    }


    @Override
    //что делать при получении результата от другого активити
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_CITY) {
            if (resultCode == RESULT_OK) {
                setTitle("Текущий город: " + data.getStringExtra(R.string.intent_key + ".cityName"));
                mcityID = data.getStringExtra(R.string.intent_key + ".cityID");
                mcityName = data.getStringExtra(R.string.intent_key + ".cityName");
            } else {
                setTitle(R.string.app_default_title);
                mcityID = "";
                mcityName = "";
                Toast.makeText(this, "Идентификатор города сброшен", Toast.LENGTH_SHORT).show();
            }
        }
        //Вообще тут логика сломана...
        if (requestCode == CHOOSE_FILTER) {
            if (resultCode == RESULT_OK) {
                mgenre = data.getStringExtra(R.string.intent_key + ".genreName");
               // Log.d("MY_LOG", mgenre);
                copyFilms(mfilmsListArray, mfilmsArray);//сьрасываем список фильмов на начальное значаение
                filterByGenre(mfilmsListArray, mgenre);
                copyFilms(mbufferFilmsArray, mfilmsListArray);
            } else {
                copyFilms(mfilmsListArray, mfilmsArray);//Да, если список отсортирован, но фильтр снят, то список рассортируется
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.choose_city_menu:
                Intent intent = new Intent(MainActivity.this, CitiesActivity.class);
                startActivityForResult(intent, CHOOSE_CITY);
                return true;
            case  R.id.choose_genre_menu:
                intent = new Intent(MainActivity.this, GenresActivity.class);
                startActivityForResult(intent, CHOOSE_FILTER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sortByRating(ArrayList<Film> arrayList) {
        //Ну не нашел я готовую сортировку для моего случая. Не так много данных чтобы пузырьковая была плохой
        for (int i = 0; i < arrayList.size(); i++) {
            Film film3 = arrayList.get(i);
            for (int k = 0; k < arrayList.size()-i-1; k++) {
                Film film1 = arrayList.get(k);
                Film film2 = arrayList.get(k+1);
                if (Double.parseDouble(film1.get("filmRating")) < Double.parseDouble(film2.get("filmRating"))) {
                    arrayList.set(k, film2);
                    arrayList.set(k+1, film1);
                }
            }
        }
    }

    //Даже конструктор копирования не помог...
    public void copyFilms (ArrayList<Film> to, ArrayList<Film> from) {
        to.clear();
        for (int i = 0; i < from.size(); i++) {
            Film film = from.get(i);
            to.add(film);
        }
    }

    public int filterByGenre(ArrayList<Film> filmsArray, String genre) {
        if (genre == "") return 1;
        ArrayList<Film> bufferArrayList = new ArrayList<Film>();
        for (int i = 0; i < filmsArray.size(); i++) {
            Film film = filmsArray.get(i);
            if (film.get("filmGenre").contains(genre)) {
                bufferArrayList.add(film);
            }
        }
        if (bufferArrayList.size() <= 0) {
            Toast.makeText(this, "Фильмов с жанром: " + mgenre + " не найдено", Toast.LENGTH_SHORT).show();
            return 2;
        }
        copyFilms(filmsArray, bufferArrayList);
        mfilmsListView.setAdapter(adapter);
        return 0;
    }
}