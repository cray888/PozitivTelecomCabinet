package ru.pozitivtelecom.cabinet.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class PreferencesClass {
    private SharedPreferences mPrefs;

    public static PreferencesClass Preferences = new PreferencesClass();

    public void InitPreferences(Context context)
    {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /////////////////////////////
    public void SetPreferences(String key, String value)
    {
        SharedPreferences.Editor mPerfsEditor = mPrefs.edit();
        mPerfsEditor.putString(key, value);
        mPerfsEditor.apply();
    }

    public void SetPreferences(String key, boolean value)
    {
        SharedPreferences.Editor mPerfsEditor = mPrefs.edit();
        mPerfsEditor.putBoolean(key, value);
        mPerfsEditor.apply();
    }

    public void SetPreferences(String key, int value)
    {
        SharedPreferences.Editor mPerfsEditor = mPrefs.edit();
        mPerfsEditor.putInt(key, value);
        mPerfsEditor.apply();
    }

    /////////////////////////////
    public String GetPreferences(String key, String defValue)
    {
        return mPrefs.getString(key, defValue);
    }

    public boolean GetPreferences(String key, boolean defValue)
    {
        return mPrefs.getBoolean(key, defValue);
    }

    public int GetPreferences(String key, int defValue)
    {
        return mPrefs.getInt(key, defValue);
    }

    public static Map<String, String> GetDeviceInfo(Context context)
    {
        Map<String, String> Result = new HashMap<>();

        Result.put("FirebaseID", FirebaseInstanceId.getInstance().getToken());
        Result.put("SDKVersion", Build.VERSION.SDK);
        Result.put("DevName", Build.MODEL);
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Result.put("AppVersion", pInfo.versionName);

        return Result;
    }
}
