package com.eeec.GestionEspresso.model;

import com.eeec.GestionEspresso.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class KAd extends KObject implements Serializable
{
    public String id;
    public String campaignCode;
    public String content;
    public int position;
    public boolean shown;

    public KAd(JSONObject json)
    {
        try
        {
            this.id = json.getString("adID");
            this.content = json.getString("htmlSm");
            this.position = json.getInt("position");
            this.campaignCode = json.getString("codCampaign");
        }
        catch (JSONException e)
        {
            LogUtil.logException(e);
        }
    }
}
