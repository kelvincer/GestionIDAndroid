package com.eeec.GestionEspresso.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eeec.GestionEspresso.AnalyticsTrackers;
import com.eeec.GestionEspresso.ArticlesActivity;
import com.eeec.GestionEspresso.R;
import com.eeec.GestionEspresso.adapters.ArticlePagerAdapter;
import com.eeec.GestionEspresso.adapters.GalleryPagerAdapter;
import com.eeec.GestionEspresso.manager.GestionRequestManager;
import com.eeec.GestionEspresso.model.KArticle;
import com.eeec.GestionEspresso.util.Constants;
import com.eeec.GestionEspresso.util.LogUtil;
import com.eeec.GestionEspresso.util.VolleyErrorHelper;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.ocpsoft.pretty.time.PrettyTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArticleFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String ARG_ARTICLE = "article";

    private KArticle article;
    private Context context;
    private View rootView;
    private ArticlePagerAdapter adapter;
    private String currentError;

    private  ImageButton nextNoteButton;
    private ArticleEventListener articleListener;

    public ArticleFragment() {
    }

    public static ArticleFragment newInstance(int position, KArticle article, ArticleEventListener listener ) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable(ARG_ARTICLE, article);
        fragment.setArguments(args);
        fragment.setArticleListener(listener);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity();
        this.rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        this.currentError = null;

        LinearLayout shareLayout = (LinearLayout) this.rootView.findViewById(R.id.shareLayout);
        shareLayout.setVisibility(View.INVISIBLE);
        shareLayout.setClickable(true);
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KArticle article = ArticleFragment.this.article;
                if (article != null) {
                    String shareText = article.shareUrl;
                    if (shareText.length() > 0) {
                        Log.d("RIX_TRACE", "SHARING!!!! URL:" + shareText);

                        AnalyticsTrackers.getInstance().trackEvent("COMPARTIR-NOTA", "Click", article.categoryName + "/" + article.title + "-" + article.id);

                        sharingOptions( article );

                    }
                }
            }
        });

        Bundle args = getArguments();
        final int currentPosition = args.getInt(ARG_POSITION);

        LinearLayout sliceTextLayout = (LinearLayout)this.rootView.findViewById(R.id.sliceTextLayout);
        sliceTextLayout.setVisibility(View.INVISIBLE);

        nextNoteButton = (ImageButton) this.rootView.findViewById(R.id.nextNoteButton);
        //nextNoteButton.setVisibility(View.GONE);

        nextNoteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( event.getAction() != MotionEvent.ACTION_DOWN ) {
                    if (articleListener != null) articleListener.onNextNotePressed(currentPosition);
                }
                return false;
            }
        });


        this.article = (KArticle) args.getSerializable(ARG_ARTICLE);
        if (this.article.isLoaded) {
            setArticle(article);
        } else {
            loadArticle();
        }
        return rootView;
    }

    private void loadArticle() {
        if (this.article.isLoaded) {
            setArticle(this.article);
        } else {
            String url = Constants.kServiceUrl + "/app_gestion/nota?nid=" + this.article.id;
            GestionRequestManager.getInstance(this.context).performJsonRequest(Request.Method.POST, url, "", new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.d("RIX_TRACE","onNOTA:" + response.toString());

                    if (response.has("error")) {
                        try {
                            ArticleFragment.this.currentError = response.getString("error");
                        } catch (JSONException e) {
                            LogUtil.logException(e);
                        }
                    } else {

                        ArticleFragment.this.currentError = null;
                        ArticleFragment.this.article.load(response);
                        ArticleFragment.this.article.isLoaded = true;
                        setArticle(article);
                        if (ArticleFragment.this.getUserVisibleHint()) {
                            AnalyticsTrackers.getInstance().trackScreenView(article.categoryName + "/" + article.title + "-" + article.id);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ArticleFragment.this.currentError = VolleyErrorHelper.getMessage(error, ArticleFragment.this.context);
                }
            });
        }
    }

    private void sharingOptions(KArticle article){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, article.title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, article.shareUrl);
        startActivity(Intent.createChooser(sharingIntent, "Compartir"));
    }

    public void showNextButton(boolean flag){
        nextNoteButton.setVisibility( flag ? View.VISIBLE : View.GONE );
    }

    private void setArticle(KArticle article) {
        this.article = article;

        TextView title = (TextView) this.rootView.findViewById(R.id.articleTitle);
        title.setText(this.article.title);

        TextView categoryName = (TextView) this.rootView.findViewById(R.id.categoryName);
        categoryName.setText(this.article.categoryName);

        PrettyTime p = new PrettyTime(new Locale("es"));
        TextView articleDate = (TextView) this.rootView.findViewById(R.id.articleDate);
        articleDate.setText(p.format(this.article.date));

        final FrameLayout videoContainer = (FrameLayout)this.rootView.findViewById(R.id.videoContainer);
        final RelativeLayout imageVideo = (RelativeLayout)this.rootView.findViewById(R.id.imageVideo);
        ImageButton playButton = (ImageButton)this.rootView.findViewById(R.id.playButton);
        final ImageView image = (ImageView)this.rootView.findViewById(R.id.image);
        //ImageButton nextNoteButton = (ImageButton)this.rootView.findViewById(R.id.nextNoteButton);
        //if( nextPosition >= 0 ) nextNoteButton.setVisibility(View.VISIBLE);
        final VideoView video = (VideoView) this.rootView.findViewById(R.id.video);
/*
        nextNoteButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {



                return true;
            }
        });*/

        Log.d("RIX_TRACE", "videoUrl" + this.article.video );
        Log.d("RIX_TRACE", "imageUrl" + this.article.image );

        if (this.article.image != null) {
            ImageLoader imageLoader = GestionRequestManager.getInstance(this.context).getImageLoader();
            imageLoader.get(this.article.image, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        image.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }

        if( this.article.video != null ) {

            playButton.setVisibility(View.VISIBLE);

            video.setVideoURI(Uri.parse(this.article.video));
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);
            video.requestFocus();
            //video.start();

            playButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    videoContainer.setVisibility(View.VISIBLE);
                    imageVideo.setVisibility(View.GONE);
                    video.start();
                    return true;
                }
            });

        } else {
            videoContainer.setVisibility(View.GONE);
        }


        if (this.article.categoryIcon != null) {
            NetworkImageView categoryIcon = (NetworkImageView) this.rootView.findViewById(R.id.categoryIcon);
            ImageLoader imageLoader = GestionRequestManager.getInstance(this.context).getImageLoader();
            categoryIcon.setImageUrl(this.article.categoryIcon, imageLoader);
        }

        String line, data = "";
        try {
            InputStream input = this.context.getAssets().open("html/article.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                data += line;
            }
            reader.close();
        } catch (Exception e) {
            LogUtil.logException(e);
        }

        data = data.replace("{{content}}", this.article.content);
        data = data.replace("href=\"galery\"", "href=\"galery://\"");

        WebView contentView = (WebView) this.rootView.findViewById(R.id.contentWebView);
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.getSettings().setLoadWithOverviewMode(true);
        contentView.getSettings().setUseWideViewPort(true);
        contentView.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("galery:")) {
                    ArticlesActivity activity = (ArticlesActivity) ArticleFragment.this.context;

                    GalleryPagerAdapter adapter = new GalleryPagerAdapter();
                    adapter.gallery = ArticleFragment.this.article.gallery;

                    View container = activity.findViewById(R.id.galleyContainer);
                    container.setVisibility(View.VISIBLE);

                    ViewPager pager = (ViewPager)activity.findViewById(R.id.galleyPager);
                    pager.setAdapter(adapter);
                }
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            }
        };
        contentView.setWebViewClient(webViewClient);

        LinearLayout shareLayout = (LinearLayout) this.rootView.findViewById(R.id.shareLayout);
        shareLayout.setVisibility(View.VISIBLE);

        LinearLayout sliceTextLayout = (LinearLayout)this.rootView.findViewById(R.id.sliceTextLayout);
        sliceTextLayout.setVisibility(View.VISIBLE);
    }

    public void onShow() {
        if (this.currentError != null) {
            showError(this.currentError);
        }
    }

    private void showError(String errorMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
        alertDialog.setMessage(errorMessage);
        alertDialog.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loadArticle();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void setArticleListener(ArticleEventListener articleListener) {
        this.articleListener = articleListener;
    }
}