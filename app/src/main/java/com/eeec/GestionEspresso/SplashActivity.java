package com.eeec.GestionEspresso;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.comscore.analytics.comScore;
import com.eeec.GestionEspresso.manager.GestionRequestManager;
import com.eeec.GestionEspresso.util.Constants;
import com.eeec.GestionEspresso.views.KTextView;
import com.eeec.GestionEspresso.views.UpdateDialogFragment;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
/*
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
*/
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Long serverTimestamp;
    private Double newVersion;
    private String messageVersion;

    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference ref = database.getReference();
    //DatabaseReference counterRef = ref.child("Counter");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(getApplicationContext());

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().setup();

        /*
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.d("RIX_TRACE","Firebase counter increment failed.");
                } else {
                    Log.d("RIX_TRACE","Firebase counter increment succeeded.");
                }
            }
        });
        */

        AnalyticsTrackers.getInstance().trackScreenView("Inicio");
        loadConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        comScore.onEnterForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
        comScore.onExitForeground();
    }

    private void loadConfig() {
        String url = Constants.kServiceUrl + "/app_gestion/config";
        //GestionRequestManager.getInstance(this).performJsonRequest(Request.Method.GET, url, "", new Response.Listener<JSONObject>()
        GestionRequestManager.getInstance(this).performJsonRequest(Request.Method.POST, url, "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("RIX_TRACE","onCONFIG:" + response.toString());

                if (response.has("error")) {
                    showHome();
                } else {

                    SharedPreferences settings = getSharedPreferences("appSettings", 0);
                    final SharedPreferences.Editor editor = settings.edit();

                    String sponsorImage = response.optString("imagen");
                    String sponsorTitle = response.optString("title");
                    String shareText = response.optString("mcompartir");
                    serverTimestamp = response.optLong("currentTimestamp") * 1000l;
                    newVersion = response.optDouble("InstallNewVersion-Android");
                    messageVersion = response.optString("MensajeVersion");
                    editor.putString("sponsorImage", sponsorImage);
                    editor.putString("sponsorTitle", sponsorTitle);
                    editor.putString("shareText", shareText);
                    editor.putBoolean("showTutorial", true);


                    showSponsor();
                    editor.apply();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showHome();
            }
        });
    }


    private long calculateSkipUpdateDays(SharedPreferences settings){
        String firstDateUpdateShown = settings.getString("firstDateUpdateShown", null);
        if( firstDateUpdateShown == null ) return 0l;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar firstDateCalendar = null, currentDateCalendar = Calendar.getInstance();
        currentDateCalendar.setTimeInMillis(serverTimestamp);


        try {
            Date firstDateUpdate = sdf.parse(firstDateUpdateShown);
            firstDateCalendar = Calendar.getInstance();
            firstDateCalendar.setTime(firstDateUpdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if( firstDateCalendar == null ) return 0l;

        long diff = currentDateCalendar.getTimeInMillis() - firstDateCalendar.getTimeInMillis();
        long days = diff / ( 24 * 60 * 60 * 1000 );
        long skipUpdateDays = Math.abs(days);

        return skipUpdateDays;
    }

    private void showSponsor() {

        SharedPreferences settings = getSharedPreferences("appSettings", 0);
        String sponsorImage = settings.getString("sponsorImage", "");
        String sponsorTitle = settings.getString("sponsorTitle", "");

        if (sponsorImage.length() == 0) {
            showHome();
            return;
        }

        KTextView sponsorTitleView = (KTextView) findViewById(R.id.sponsorTitle);
        sponsorTitleView.setText(sponsorTitle);

        final ImageView image = (ImageView) findViewById(R.id.sponsorImage);
        ImageLoader imageLoader = GestionRequestManager.getInstance(this).getImageLoader();
        imageLoader.get(sponsorImage, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    float scale = Math.max(1, metrics.scaledDensity - 1);
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                    image.setImageBitmap(resizedBitmap);

                    sponsorLoaded(true);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                sponsorLoaded(false);
            }
        });
    }

    private void sponsorLoaded(boolean success) {
        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);

        long waitTime = success ? 4000 : 1000;
        if (Constants.kDisableSplash) {
            waitTime = 0;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showHome();
            }
        }, waitTime);
    }

    private void showHome() {
        SharedPreferences settings = getSharedPreferences("appSettings", 0);
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        boolean showUpdateDialog =  newVersion != null && Constants.version < newVersion.doubleValue();
        intent.putExtra("showUpdate", showUpdateDialog);
        if( showUpdateDialog ){
            final long skipUpdateDays = calculateSkipUpdateDays(settings);
            final boolean forceUpdate = skipUpdateDays >= 30l;

            intent.putExtra("newVersion", newVersion);
            intent.putExtra("forceUpdate", forceUpdate);
            intent.putExtra("messageVersion", messageVersion);
            intent.putExtra("saveSkipDate", skipUpdateDays == 0l);
            intent.putExtra("serverTimestamp", serverTimestamp);
        }

        startActivity(intent);
    }


}
