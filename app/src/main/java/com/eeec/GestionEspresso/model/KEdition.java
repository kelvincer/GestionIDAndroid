package com.eeec.GestionEspresso.model;

import android.content.SharedPreferences;
import android.util.Log;

import com.eeec.GestionEspresso.util.EditionVersion;
import com.eeec.GestionEspresso.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class KEdition extends KObject implements Serializable
{
    public static List<KObject> kArticles;
    public static List<KAd> kAds;

    public String id;
    public String coverImage;
    public Date date;
    public int edition;
    public String nameEdition;
    public List<KCategory> categories = new ArrayList<>();
    public List<KAd> ads = new ArrayList<>();

    public KEdition(JSONObject json)
    {
        try
        {
            this.id = json.getString("edition");
            this.coverImage = json.optString("imageHome");
            this.edition =  Integer.parseInt(this.id.substring(8,9));// EditionVersion.get(versionId);
            this.nameEdition = json.optString("name_edition");

            long dateTimestamp = json.optLong("fecha", 0);
            if (dateTimestamp > 0)
            {
                this.date = new Date(dateTimestamp*1000);
            }

            JSONArray categories = json.getJSONArray("list");
            for (int i = 0; i < categories.length(); i++)
            {
                try
                {
                    JSONObject obj = (JSONObject)categories.get(i);
                    KCategory category = new KCategory(obj);
                    this.categories.add(category);
                }
                catch (JSONException e)
                {
                    LogUtil.logException(e);
                }
            }

            JSONArray ads = json.getJSONArray("ads");
            for (int i = 0; i < ads.length(); i++)
            {
                try
                {
                    JSONObject obj = (JSONObject)ads.get(i);
                    KAd ad = new KAd(obj);
                    this.ads.add(ad);
                }
                catch (JSONException e)
                {
                    LogUtil.logException(e);
                }
            }

        }
        catch (JSONException e)
        {
            LogUtil.logException(e);
        }
    }

    public List<KObject> getArticles()
    {
        if (kArticles == null)
        {
            kArticles = new ArrayList<>();
            for (KCategory category : this.categories)
            {
                kArticles.addAll(category.articles);
            }
        }
        return kArticles;
    }

    public List<KAd> getAds()
    {
        if (kAds == null)
        {
            kAds = new ArrayList<KAd>(this.ads);
        }
        return kAds;
    }
}
