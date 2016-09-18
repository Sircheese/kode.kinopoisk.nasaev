package com.kodekinopoisknasaev;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sirch_000 on 18.09.2016.
 */
public class SeancesActivity extends AppCompatActivity{
    private String mjsonResult;
    private WebView mWebView;

    public String lon;
    public String lat;
    public void onCreate (Bundle savedinstance) {
        super.onCreate(savedinstance);
        setContentView(R.layout.activity_seances);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String cityName = getIntent().getExtras().getString(R.string.intent_key + ".cityName");
        String cinemaName = getIntent().getExtras().getString(R.string.intent_key + ".cinemaName");
        String filmNameRU = getIntent().getExtras().getString(R.string.intent_key + ".filmNameRU");
        String filmNameEN = getIntent().getExtras().getString(R.string.intent_key + ".filmNameEN");
        String filmID = getIntent().getExtras().getString(R.string.intent_key + ".filmID");
        String cityID = getIntent().getExtras().getString(R.string.intent_key + ".cityID");
        lon = getIntent().getExtras().getString(R.string.intent_key + ".cinemaLon");
        lat = getIntent().getExtras().getString(R.string.intent_key + ".cinemaLat");

        setTitle(cinemaName + ", " + cityName);
        TextView filmNameTextView = (TextView) findViewById(R.id.filmTextView);
        filmNameTextView.setText(filmNameRU + " (" + filmNameEN + ")");


        String getQuery = "http://api.kinopoisk.cf/getSeance?filmID=" + filmID;
        AsyncJson asyncJsonParser = new AsyncJson();
        asyncJsonParser.execute(getQuery);

        mjsonResult = asyncJsonParser.getResultJson();
        String seancesURL = null;
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(mjsonResult);

            seancesURL = jsonObj.getString("seanceURL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //да, костыли...
        //Может мне кажется, но api сломан
        //Хотя я просто чего-то не знаю. Но неудобно как-то
        String bufferStr[] = seancesURL.split("/");
        seancesURL = bufferStr[0] + "/";
        for (int i = 1; i < bufferStr.length - 1; i++) {
            seancesURL = seancesURL + bufferStr[i] + "/";
        }
        seancesURL = seancesURL + cityID + "/";
        Log.d("MY_LOG", seancesURL);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new MyWebViewClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(seancesURL);
    }

    public void onShowMapClick (View view) {
        String geoUriString = "geo:" + lat + "," + lon + "?z=18";
        Uri geoUri = Uri.parse(geoUriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        startActivity(mapIntent);
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
