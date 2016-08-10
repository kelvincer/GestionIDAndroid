package com.eeec.GestionEspresso.model;

import com.eeec.GestionEspresso.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KCategory extends KObject implements Serializable
{
    public String name;
    public String icon;
    public List<KArticle> articles = new ArrayList<>();

    public KCategory(JSONObject json)
    {
        try
        {
            this.name = json.getString("category");
            this.icon = json.getString("icon");

            JSONArray notas = json.getJSONArray("notas");
            for (int i = 0; i < notas.length(); i++)
            {
                try
                {
                    JSONObject obj = (JSONObject) notas.get(i);
                    if (obj.getString("type").equals("nota"))
                    {
                        KArticle article = new KArticle(obj);
                        this.articles.add(article);
                    }
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
}
