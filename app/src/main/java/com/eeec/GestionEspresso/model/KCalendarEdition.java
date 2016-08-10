package com.eeec.GestionEspresso.model;

import com.eeec.GestionEspresso.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class KCalendarEdition extends KObject {
    public String id;
    public String name;

    public KCalendarEdition(JSONObject json) {
        try {
            this.id = json.getString("eid");
            this.name = json.getString("name");
        } catch (JSONException e) {
            LogUtil.logException(e);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}


