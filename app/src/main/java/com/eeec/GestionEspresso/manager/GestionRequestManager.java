package com.eeec.GestionEspresso.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eeec.GestionEspresso.model.KEdition;
import com.eeec.GestionEspresso.util.ConnectivityUtils;
import com.eeec.GestionEspresso.util.Constants;
import com.eeec.GestionEspresso.util.LogUtil;
import com.eeec.GestionEspresso.util.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GestionRequestManager {
    private static GestionRequestManager mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private GestionRequestManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized GestionRequestManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GestionRequestManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void performJsonRequest(int method, final String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        Cache cache = this.getRequestQueue().getCache();
        Cache.Entry entry = cache.get(method + ":" + url);

        Log.d("RIX_TRACE", "URL:" + method + ":" + url + " entryCache:" + entry);

        if (!ConnectivityUtils.hasInternetAvailable(this.mCtx) && entry != null) {
            Log.d("RIX_TRACE", "if1");
            try {
                if (listener != null) {
                    String jsonString = new String(entry.data, HttpHeaderParser.parseCharset(entry.responseHeaders, "UTF-8"));
                    Log.d("RIX_TRACE", "json:"+ jsonString + " --> from:" + url );
                    listener.onResponse(new JSONObject(jsonString));
                }
            } catch (UnsupportedEncodingException | JSONException e) {
                Log.d("RIX_TRACE", "error1:" + e.getMessage());
                if (errorListener != null) {
                    errorListener.onErrorResponse(new ParseError(e));
                }
            }
        } else {
            Log.d("RIX_TRACE", "if2");

            JsonObjectRequest request = new JsonObjectRequest(method, url, requestBody, listener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("apikey", Constants.kApiKey);

                    Log.d("RIX_TRACE", "url:" + url + " >> addingApiKey:" + Constants.kApiKey );

                    return params;
                }
            };
            request.setShouldCache(true);
            this.addToRequestQueue(request);
        }
    }
}
