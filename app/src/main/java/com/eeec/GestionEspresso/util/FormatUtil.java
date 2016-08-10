package com.eeec.GestionEspresso.util;

/**
 * Created by rrodriguez on 7/07/16.
 */
public class FormatUtil {

    public static String capitalize(String text){
        if( text == null) return null;
        text = text.toLowerCase();
        return text.substring(0,1).toUpperCase() + text.substring(1,text.length());
    }



}
