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

import com.kodekinopoisknasaev.DataClasses.Genre;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by sirch_000 on 16.09.2016.
 */
public class GenresActivity extends AppCompatActivity {
    private ListView mgenresListView;
    private EditText mgenresEditText;
    private ArrayList<Genre> mgenresArrayList;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mgenresListView = (ListView) findViewById(R.id.genresListView);
        mgenresEditText = (EditText) findViewById(R.id.chooseGenreEditText);
        mgenresArrayList = new ArrayList<>();

        try {
            InputStream is = this.getResources().openRawResource(R.raw.genres);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);

            String jsontext = new String(buffer);
            JSONArray entries = new JSONArray(jsontext);

            ListAdapter adapter = new SimpleAdapter(this, mgenresArrayList, R.layout.genres_list,
                    new String[] {Genre.genreName},
                    new int [] {R.id.genreName});

            mgenresListView.setAdapter(adapter);

            for (int i = 0; i < entries.length(); i++) {
                JSONObject post = entries.getJSONObject(i);
                mgenresArrayList.add(new Genre(post.getString("genreName"), post.getString("genreID")));
            }
            adapter.notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mgenresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                mgenresEditText.setText(mgenresArrayList.get(position).get("genreName"));
            }
        });
    }

    public void onClick(View view) {
        String currentGenre = mgenresEditText.getText().toString().toLowerCase();
        boolean isGenre = false;
        for (int i = 0; i < mgenresArrayList.size(); i++) {
            Genre genre = mgenresArrayList.get(i);
            String citySTR = genre.get("genreName").toLowerCase();//чтобы читалось немного лучше

            if (citySTR.equals(currentGenre)) {
                Intent answerIntent = new Intent();
                answerIntent.putExtra(R.string.intent_key + ".genreName", genre.get("genreName"));
                setResult(RESULT_OK, answerIntent);
                isGenre = true;
                finish();
                break;
            }
        }
        if (!isGenre) {
            Toast.makeText(this, "Жанр: " + currentGenre + " не найден", Toast.LENGTH_SHORT).show();
        }
    }
}
