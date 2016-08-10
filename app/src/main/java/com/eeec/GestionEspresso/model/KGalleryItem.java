package com.eeec.GestionEspresso.model;

import com.eeec.GestionEspresso.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KGalleryItem implements Serializable {

    public String url;
    public String legend;

    public KGalleryItem(JSONObject json)
    {
        try
        {
            this.url = json.getString("imageURL");
            this.legend = json.getString("legend");
        }
        catch (JSONException e)
        {
            LogUtil.logException(e);
        }
    }
}
