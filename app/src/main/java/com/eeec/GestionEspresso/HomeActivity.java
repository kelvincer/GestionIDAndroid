package com.eeec.GestionEspresso;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.comscore.analytics.comScore;
import com.eeec.GestionEspresso.adapters.EditionAdapter;
import com.eeec.GestionEspresso.gcm.KRegistrationIntentService;
import com.eeec.GestionEspresso.manager.GestionRequestManager;
import com.eeec.GestionEspresso.model.KArticle;
import com.eeec.GestionEspresso.model.KCalendarEdition;
import com.eeec.GestionEspresso.model.KCategory;
import com.eeec.GestionEspresso.model.KEdition;
import com.eeec.GestionEspresso.model.KCalendar;
import com.eeec.GestionEspresso.util.Constants;
import com.eeec.GestionEspresso.util.FormatUtil;
import com.eeec.GestionEspresso.util.LogUtil;
import com.eeec.GestionEspresso.util.VolleyErrorHelper;
import com.eeec.GestionEspresso.views.KTextView;
import com.eeec.GestionEspresso.views.UpdateDialogFragment;
import com.eeec.GestionEspresso.views.UpdateDialogListener;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditionAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<KCalendar> calendarItems;
    String currentEditionId;

    static UpdateDialogFragment updateDialog;

    private BroadcastReceiver registrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
            }
        };

        registerReceiver();
        if (checkPlayServices()) {
            Log.d("RIX_TRACE", "startingService KRegistrationIntentservice");
            Intent intent = new Intent(this, KRegistrationIntentService.class);
            startService(intent);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        expListView = (ExpandableListView) findViewById(R.id.homeListView);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.home_header, null);
        expListView.addHeaderView(header);

        listAdapter = new EditionAdapter(this);
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                KCategory category = HomeActivity.this.listAdapter.getEdition().categories.get(groupPosition);
                KArticle article = category.articles.get(childPosition);
                AnalyticsTrackers.getInstance().trackEvent("FLUJO", "Click", category.name + "/" + article.title + "-" + article.id);

                Intent intent = new Intent(HomeActivity.this, ArticlesActivity.class);
                intent.putExtra("categoryIndex", groupPosition);
                intent.putExtra("articleIndex", childPosition);
                intent.putExtra("edition", listAdapter.getEdition());
                startActivity(intent);
                return false;
            }
        });
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                expandableListView.expandGroup(i);
                return true;
            }
        });

        loadEdition(null);

        View menuComments = HomeActivity.this.findViewById(R.id.menuComments);

        menuComments.setClickable(true);
        menuComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

        View menuShare = HomeActivity.this.findViewById(R.id.menuShare);
        menuShare.setClickable(true);
        menuShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences settings = getSharedPreferences("appSettings", 0);
                String shareText = settings.getString("shareText", "");

                if (shareText.length() > 0) {
                    if (HomeActivity.this.currentEditionId == null) {
                        AnalyticsTrackers.getInstance().trackEvent("Boton-menuCompartir-Aplicacion", "MENU", null);
                    } else if (HomeActivity.this.listAdapter.getEdition() != null) {
                        KEdition edition = HomeActivity.this.listAdapter.getEdition();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
                        AnalyticsTrackers.getInstance().trackEvent("Compartir-Aplicacion", "MENU", format.format(edition.date) + "/Home");
                    }

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(sharingIntent, "Compartir"));
                }
            }
        });

        View menuTerms = HomeActivity.this.findViewById(R.id.menuTerms);
        menuTerms.setClickable(true);
        menuTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://gestion.pe/terminos"));
                startActivity(browserIntent);
            }
        });


        showUpdateDialog();

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadCalendar();

        comScore.onEnterForeground();
        registerReceiver();

        if (this.currentEditionId == null) {
            AnalyticsTrackers tracker = AnalyticsTrackers.getInstance();
            if( tracker != null ) tracker.trackScreenView("HOME");
        } else if (this.listAdapter.getEdition() != null && this.listAdapter.getEdition().id.equals(this.currentEditionId)) {
            //selectEdition(this.listAdapter.getEdition());
            trackScreenView();
        }
    }

    @Override
    protected void onPause() {
        comScore.onExitForeground();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void trackScreenView() {
        if (this.currentEditionId != null) {
            KEdition edition = this.listAdapter.getEdition();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
            AnalyticsTrackers tracker = AnalyticsTrackers.getInstance();
            if( tracker != null ) tracker.trackScreenView(format.format(edition.date) + "/Home");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (this.currentEditionId == null) {
            AnalyticsTrackers.getInstance().trackEvent("Boton-menu", "MENU", null);
        } else if (this.listAdapter.getEdition() != null) {
            KEdition edition = this.listAdapter.getEdition();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));
            AnalyticsTrackers.getInstance().trackEvent("Boton-menu", "MENU", format.format(edition.date) + "/Home");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (item != null && item.getItemId() == R.id.menuButton) {
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void loadEdition(String id) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);

        //this.currentEditionId = id;
        String url = Constants.kServiceUrl + "/app_gestion/flujo";
        if (id != null) {
            url += "?eid=" + id;
        }

        GestionRequestManager.getInstance(this).performJsonRequest(Request.Method.POST, url, "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.d("RIX_TRACE", "OnFLUJO:" + response.toString());


                if (response.has("error")) {
                    try {
                        showError(response.getString("error"));
                    } catch (JSONException e) {
                        LogUtil.logException(e);
                    }
                } else {

                    KEdition edition = new KEdition(response);
                    KEdition.kArticles = null;
                    KEdition.kAds = null;

                    HomeActivity.this.currentEditionId = edition.id;

                    SharedPreferences settings = getSharedPreferences("editionSettings", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong("lastRest-" + edition.id, System.currentTimeMillis());
                    editor.putInt("articleViewCount-" + edition.id, 0);
                    editor.apply();

                    setEdition(edition);
                    trackScreenView();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(VolleyErrorHelper.getMessage(error, HomeActivity.this));
            }
        });
    }

    private void setEdition(KEdition edition) {
        listAdapter.setEdition(edition);
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expListView.expandGroup(i);
        }

        ImageView image = (ImageView) expListView.findViewById(R.id.coverImage);
        image.setImageResource(0);

        SharedPreferences settings = getSharedPreferences("appSettings", 0);
        String sponsorImage = settings.getString("sponsorImage", "");
        String sponsorTitle = settings.getString("sponsorTitle", "");

        SimpleDateFormat dayFormat = new SimpleDateFormat("d", new Locale("es", "ES"));
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new Locale("es", "ES"));
        String coverDate = FormatUtil.capitalize( dayNameFormat.format(edition.date) );
        String coverDateFormat = dayFormat.format(edition.date) + " de " + FormatUtil.capitalize(monthFormat.format(edition.date));
        int editionId = edition.edition;//.name();
        String editionName = edition.nameEdition;


        LinearLayout editionHeader = (LinearLayout)expListView.findViewById(R.id.editionHeader);

        KTextView coverDateView = (KTextView) expListView.findViewById(R.id.coverDate);
        coverDateView.setText(coverDate);

        KTextView coverDateFormatView = (KTextView) expListView.findViewById(R.id.coverDateFormat);
        coverDateFormatView.setText(coverDateFormat);

        KTextView coverVersionView = (KTextView) expListView.findViewById(R.id.coverVersion);

        if( editionName == null )
            coverVersionView.setVisibility(View.INVISIBLE);
        else
            coverVersionView.setText(editionName);

        KTextView sponsorTitleView = (KTextView) expListView.findViewById(R.id.homeSponsorTitle);
        sponsorTitleView.setText(sponsorTitle);

        ImageLoader imageLoader = GestionRequestManager.getInstance(this).getImageLoader();

        NetworkImageView sponsorImageView = (NetworkImageView) expListView.findViewById(R.id.homeSponsorImage);
        sponsorImageView.setImageUrl(sponsorImage, imageLoader);

        imageLoader.get(edition.coverImage, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    ImageView image = (ImageView) HomeActivity.this.expListView.findViewById(R.id.coverImage);
                    image.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        listAdapter.notifyDataSetChanged();
        expListView.setSelection(0);

        editionHeader.setVisibility(View.VISIBLE);

        selectEdition(edition);
    }

    private void showError(String errorMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setMessage(errorMessage);
        alertDialog.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loadEdition(HomeActivity.this.currentEditionId);
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void loadCalendar() {
        String url = Constants.kServiceUrl + "/app_gestion/calendario";
        GestionRequestManager.getInstance(HomeActivity.this).performJsonRequest(Request.Method.POST, url, "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("RIX_TRACE","onCALENDAR:" + response.toString());

                if (!response.has("error")) {
                    HomeActivity.this.calendarItems = new ArrayList<>();
                    try {
                        JSONArray items = response.getJSONArray("calendario");
                        for (int i = 0; i < items.length(); i++) {
                            KCalendar calendar = new KCalendar((JSONObject) items.get(i));
                            HomeActivity.this.calendarItems.add(calendar);
                        }

                    } catch (JSONException e) {
                        LogUtil.logException(e);
                    }

                    Collections.sort(HomeActivity.this.calendarItems, new KCalendar.KCalendarComparator());
                    setCalendar();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private void setCalendar() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout calendarContainer = (LinearLayout) HomeActivity.this.findViewById(R.id.calendarContainer);

        calendarContainer.removeAllViews();

        KEdition currentEdition = null;
        if(this.currentEditionId != null && this.listAdapter.getEdition() != null && this.listAdapter.getEdition().id.equals(this.currentEditionId)){
            currentEdition = this.listAdapter.getEdition();
        }

        Log.d("RIX_TRACE", "CurrentEdition:" + (currentEdition!= null ? currentEdition.id : "NONE" ));

        int l = HomeActivity.this.calendarItems.size();
        for (int i = 0; i < 5 && i < l; i++) {
            KCalendar calendar = HomeActivity.this.calendarItems.get(i);

            SimpleDateFormat weekDayFormatter = new SimpleDateFormat("EE", new Locale("es", "ES"));
            SimpleDateFormat dayFormatter = new SimpleDateFormat("d", new Locale("es", "ES"));

            String weekDay = weekDayFormatter.format(calendar.date);
            KTextView calendarItem = (KTextView) inflater.inflate(R.layout.calendar_day_item, null);
            String dateString = weekDay.substring(0, 1).toUpperCase() + weekDay.substring(1, 2) + "\n" + dayFormatter.format(calendar.date);
            calendarItem.setText(dateString);
            calendarItem.setTag(calendar);
            calendarItem.setClickable(true);
            calendarItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KCalendar calendar = (KCalendar) view.getTag();
                    showSelectEditionDialog(calendar);
                }
            });

            if( currentEdition != null ) selectEditionInCalendar(currentEdition, calendarItem);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            calendarContainer.addView(calendarItem, 0, params);
        }
    }

    private void showSelectEditionDialog(KCalendar calendar) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(HomeActivity.this);
        final ArrayAdapter<KCalendarEdition> arrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.select_dialog_item);

        if (calendar.editions.size() == 1) {
            KCalendarEdition edition = calendar.editions.get(0);
            loadEdition(edition.id);
        } else {
            for (KCalendarEdition edition : calendar.editions) {
                arrayAdapter.add(edition);
            }

            builderSingle.setNegativeButton(
                    "Cancelar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            KCalendarEdition edition = arrayAdapter.getItem(which);
                            loadEdition(edition.id);
                        }
                    });
            builderSingle.show();
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(registrationBroadcastReceiver, new IntentFilter(Constants.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return resultCode == ConnectionResult.SUCCESS;
    }


    private void selectEdition(KEdition edition){
        LinearLayout calendarContainer = (LinearLayout) HomeActivity.this.findViewById(R.id.calendarContainer);
        for (int i = 0; i < calendarContainer.getChildCount(); i++) {
            View v = calendarContainer.getChildAt(i);
            if (v instanceof KTextView) {
                KTextView calendarItem = (KTextView)v;
                selectEditionInCalendar(edition, calendarItem);
            }
        }
    }

    private void selectEditionInCalendar(KEdition edition, KTextView calendarItem){
        KCalendar calendar = (KCalendar)calendarItem.getTag();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd", new Locale("es", "ES"));
        String calendarDate = dateFormatter.format(calendar.date);
        String editionDate = dateFormatter.format(edition.date);

        if( calendarDate.equals(editionDate)){
            Log.d("RIX_TRACE", "ComparingCal:" + calendarDate + "-" + editionDate + " BACKGROUND!!!!!!");
            calendarItem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            calendarItem.setBackgroundColor(0x00000000);
            Log.d("RIX_TRACE", "ComparingCal:" + calendarDate + "-" + editionDate + " OK NO!!!!!");
        }

    }


    private void showUpdateDialog(){
        boolean showUpdate = getIntent().getBooleanExtra("showUpdate", false);
        if(!showUpdate) return;

        boolean forceUpdate = getIntent().getBooleanExtra("forceUpdate", false);
        double newVersion = getIntent().getDoubleExtra("newVersion", -1d);
        String messageVersion = getIntent().getStringExtra("messageVersion");

        final boolean saveSkipDate = getIntent().getBooleanExtra("saveSkipDate", true);
        final long serverTimestamp = getIntent().getLongExtra("serverTimestamp", -1l);

        UpdateDialogFragment updateDialog = new UpdateDialogFragment();
        updateDialog.setForceUpdate(forceUpdate);
        updateDialog.setMessageVersion(messageVersion, newVersion);
        updateDialog.setListener(new UpdateDialogListener() {
            @Override
            public void onUpdateButtonClicked() {
                redirectPlaystore();
            }

            @Override
            public void onSkipButtonClicked() {
                if( saveSkipDate ) registerSkipUpdate(serverTimestamp);
            }
        });
        updateDialog.show(getFragmentManager(), UpdateDialogFragment.TAG);
    }


    private void registerSkipUpdate(long serverTimestamp){
        SharedPreferences settings = getSharedPreferences("appSettings", 0);
        SharedPreferences.Editor editor = settings.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date(serverTimestamp));
        String strCurrentDate = sdf.format(currentDate.getTime());
        editor.putString("firstDateUpdateShown", strCurrentDate);
        editor.apply();
    }


    private void redirectPlaystore(){
        Context context = getBaseContext();

        Log.d("RIX_TRACE", "PACKAGENAME:" + context.getPackageName());

        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
            context.startActivity(webIntent);
        }
    }

}

