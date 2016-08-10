package com.eeec.GestionEspresso.model;

import com.eeec.GestionEspresso.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KArticle extends KObject implements Serializable {
    public boolean isLoaded;

    public String id;
    public String title;
    public String content;
    public String image;
    public String video;
    public String categoryName;
    public String categoryIcon;
    public String shareUrl;
    public Date date;
    public ArrayList<KGalleryItem> gallery = new ArrayList<>();

    public KArticle(JSONObject json) {
        this.isLoaded = false;
        this.load(json);
    }

    public void load(JSONObject json) {
        try {
            this.id = json.getString("nid");
            this.title = json.getString("title");
            this.content = json.optString("content", null);
            this.image = json.optString("imageUrl", null);
            this.video = json.optString("videoURL", null);
            this.shareUrl = json.optString("shareUrl", null);
            this.categoryIcon = json.optString("iconCategory", null);
            this.categoryName = json.optString("category", null);

            long dateTimestamp = json.optLong("pubtime", 0);
            if (dateTimestamp > 0) {
                this.date = new Date(dateTimestamp * 1000);
            }

            JSONArray gallery = json.optJSONArray("gallery");
            if (gallery != null) {
                for (int i = 0; i < gallery.length(); i++) {
                    try {
                        JSONObject obj = (JSONObject) gallery.get(i);
                        KGalleryItem galleryItem = new KGalleryItem(obj);
                        this.gallery.add(galleryItem);
                    } catch (JSONException e) {
                        LogUtil.logException(e);
                    }
                }
            }
        } catch (JSONException e) {
            LogUtil.logException(e);
        }
    }
}
