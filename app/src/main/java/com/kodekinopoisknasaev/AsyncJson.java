package com.kodekinopoisknasaev;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by sirch_000 on 07.09.2016.
 */

public class AsyncJson extends AsyncTask <String, Void, String> {

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = null;

    @Override
    protected String doInBackground(String... params) {
        // получаем данные с внешнего ресурса
        try {
            URL url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
                e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String resultJson) {
        super.onPostExecute(resultJson);
    }

    //Зато не надо в другом активити ловить это исключение. Так проще читать
    public String getResultJson () {
        try {
            String resultJson = this.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultJson;
    };
}
