package com.eeec.GestionEspresso.util;

import android.util.Log;

public class LogUtil
{
    public static void logException(Exception e)
    {
        Log.d(Constants.kLogExceptionTag, Log.getStackTraceString(e));
    }

    public static void d(String text)
    {
        Log.d(Constants.kLogDebugTag, text);
    }
}
