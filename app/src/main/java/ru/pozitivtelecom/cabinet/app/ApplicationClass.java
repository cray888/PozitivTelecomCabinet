package ru.pozitivtelecom.cabinet.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import java.lang.reflect.TypeVariable;

import ru.pozitivtelecom.cabinet.models.UsersCabinetDataModel;
import ru.pozitivtelecom.cabinet.ui.ForgotPasswordActivity;
import ru.pozitivtelecom.cabinet.ui.LoginActivity;
import ru.pozitivtelecom.cabinet.ui.SignupActivity;
import ru.pozitivtelecom.cabinet.ui.SplashScreenActivity;

public class ApplicationClass extends Application implements Application.ActivityLifecycleCallbacks  {

    //Private variable

    //Global variable
    public int CurrentAccount;
    public UsersCabinetDataModel Data;


    @Override
    public void onCreate() {
        super.onCreate();
        CurrentAccount = 0;
        PreferencesClass.Preferences.InitPreferences(this);
        registerActivityLifecycleCallbacks(this);
    }

    static public UsersCabinetDataModel getAppData(Activity context) {
        return ((ApplicationClass)context.getApplicationContext()).Data;
    }

    static public void setAppData(Activity context, UsersCabinetDataModel data) {
        ((ApplicationClass)context.getApplicationContext()).Data = data;
    }

    static public int getCurrentAccount(Activity context) {
        return ((ApplicationClass)context.getApplicationContext()).CurrentAccount;
    }

    static public void setCurrentAccount(Activity context, int curentAccount) {
        ((ApplicationClass)context.getApplicationContext()).CurrentAccount = curentAccount;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (activity.getClass() == SplashScreenActivity.class ||
                activity.getClass() == LoginActivity.class ||
                activity.getClass() == ForgotPasswordActivity.class ||
                activity.getClass() == SignupActivity.class) return;
        if (Data == null) {
            PreferencesClass.Preferences.InitPreferences(this);

            if (bundle == null) {
                startSpalshScreen();
                return;
            }

            CurrentAccount = bundle.getInt("saveCurentAccount");

            String SavePreference = bundle.getString("saveData", "");
            if (SavePreference == "" || SavePreference == null) {
                startSpalshScreen();
            }
            else {
                try {
                    Data = new Gson().fromJson(SavePreference, UsersCabinetDataModel.class);
                }
                catch (Exception e) {
                    startSpalshScreen();
                }
            }
        }
    }

    private void startSpalshScreen() {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (Data != null)
        {
            bundle.putInt("saveCurentAccount", CurrentAccount);
            bundle.putString("saveData", new Gson().toJson(Data));
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
