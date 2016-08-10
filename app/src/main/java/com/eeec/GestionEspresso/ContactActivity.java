package com.eeec.GestionEspresso;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.comscore.analytics.comScore;
import com.eeec.GestionEspresso.util.LogUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String line, data = "";
        try
        {
            InputStream input = getAssets().open("html/contact.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            while ((line = reader.readLine()) != null)
            {
                data += line;
            }
            reader.close();
        }
        catch (Exception e)
        {
            LogUtil.logException(e);
        }

        WebView contentView = (WebView) findViewById(R.id.contactWebView);
        contentView.getSettings().setLoadWithOverviewMode(true);
        contentView.getSettings().setUseWideViewPort(true);
        contentView.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
        WebViewClient webViewClient = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return true;
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url)
            {
            }
        };
        contentView.setWebViewClient(webViewClient);

        AnalyticsTrackers.getInstance().trackScreenView("Formulario-envianos-comentarios");
    }

    @Override
    protected void onResume() {
        super.onResume();
        comScore.onEnterForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        comScore.onExitForeground();
    }
}
