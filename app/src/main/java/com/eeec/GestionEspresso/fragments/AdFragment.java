package com.eeec.GestionEspresso.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eeec.GestionEspresso.AnalyticsTrackers;
import com.eeec.GestionEspresso.R;
import com.eeec.GestionEspresso.model.KAd;

public class AdFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String ARG_AD = "ad";

    private KAd ad;
    private Context context;
    private View rootView;

    public AdFragment() {
    }

    public static AdFragment newInstance(int position, KAd ad) {
        AdFragment fragment = new AdFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putSerializable(ARG_AD, ad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity();
        this.rootView = inflater.inflate(R.layout.fragment_ad, container, false);

        Bundle args = getArguments();
        setAd((KAd) args.getSerializable(ARG_AD));
        return rootView;
    }

    private void setAd(KAd ad) {
        this.ad = ad;

        WebView contentView = (WebView) this.rootView.findViewById(R.id.contentWebView);
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.getSettings().setLoadWithOverviewMode(true);
        contentView.getSettings().setUseWideViewPort(true);
        contentView.setInitialScale(1);

        contentView.loadDataWithBaseURL("", this.ad.content, "text/html", "UTF-8", "");
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                AnalyticsTrackers.getInstance().trackEvent("Publicidad-Click", "Click", AdFragment.this.ad.campaignCode);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            }
        };
        contentView.setWebViewClient(webViewClient);
    }
}