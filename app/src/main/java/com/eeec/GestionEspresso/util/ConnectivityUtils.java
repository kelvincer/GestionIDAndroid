package com.eeec.GestionEspresso.util;

import static android.provider.Settings.Global.AIRPLANE_MODE_ON;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Utility class to manage connectivity.
 *
 * @author Jean Marie Pascal
 */
public final class ConnectivityUtils
{
    private ConnectivityUtils()
    {
    }

    public static boolean isAirplaneModeOn(Context context)
    {
        ContentResolver contentResolver = context.getContentResolver();
        return Settings.System.getInt(contentResolver, AIRPLANE_MODE_ON, 0) != 0;
    }

    public static boolean hasPermission(Context context, String permission)
    {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasInternetAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) { return true; }
        return false;
    }

    public static boolean isWifiAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) { return true; }
        return false;
    }

    public static boolean isMobileNetworkAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInfo != null && mobileInfo.isConnected()) { return true; }
        return false;
    }

    public static boolean hasNetwork(Context context)
    {
        return ConnectivityUtils.hasInternetAvailable(context);
    }

    public static boolean hasMobileConnectivity(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mobileInfo != null);
    }
}