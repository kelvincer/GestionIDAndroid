package com.eeec.GestionEspresso.model;

import com.eeec.GestionEspresso.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class KCalendar extends KObject
{
    public ArrayList<KCalendarEdition> editions;

    public Date date;

    public KCalendar(JSONObject json)
    {
        long dateTimestamp = json.optLong("fecha", 0);
        if (dateTimestamp > 0)
        {
            this.date = new Date(dateTimestamp*1000);
        }

        try
        {
            this.editions = new ArrayList<>();
            JSONArray editions = json.getJSONArray("ediciones");
            for (int i = 0; i < editions.length(); i++)
            {
                try
                {
                    JSONObject obj = (JSONObject)editions.get(i);
                    KCalendarEdition calendarEdition = new KCalendarEdition(obj);
                    this.editions.add(calendarEdition);
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

    public static class KCalendarComparator implements Comparator<KCalendar>
    {
        public int compare(KCalendar left, KCalendar right) {
            return right.date.compareTo(left.date);
        }
    }
}


