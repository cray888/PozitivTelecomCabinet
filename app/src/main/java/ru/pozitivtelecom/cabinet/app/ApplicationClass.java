package ru.pozitivtelecom.cabinet.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import ru.pozitivtelecom.cabinet.models.PersonModel;
import ru.pozitivtelecom.cabinet.ui.ForgotPasswordActivity;
import ru.pozitivtelecom.cabinet.ui.LoginActivity;
import ru.pozitivtelecom.cabinet.ui.SignupActivity;
import ru.pozitivtelecom.cabinet.ui.SplashScreenActivity;

public class ApplicationClass extends Application implements Application.ActivityLifecycleCallbacks  {

    //Private variable

    //Global variable
    public PersonModel SessionData;
    public int CurrentAccountID;
    public Activity CurrentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        CurrentAccountID = 0;
        PreferencesClass.Preferences.InitPreferences(this);
        registerActivityLifecycleCallbacks(this);
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
        if (SessionData == null) {
            PreferencesClass.Preferences.InitPreferences(this);

            if (bundle == null) {
                startSplashScreen();
                return;
            }

            CurrentAccountID = bundle.getInt("saveCurentAccount");

            String SavePreference = bundle.getString("saveData", "");
            if (SavePreference == "" || SavePreference == null) {
                startSplashScreen();
            }
            else {
                try {
                    SessionData = new Gson().fromJson(SavePreference, PersonModel.class);
                }
                catch (Exception e) {
                    startSplashScreen();
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (SessionData != null)
        {
            bundle.putInt("saveCurentAccount", CurrentAccountID);
            bundle.putString("saveData", new Gson().toJson(SessionData));
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        CurrentActivity = activity;
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

    static public PersonModel getAppData(Activity context) {
        return ((ApplicationClass)context.getApplicationContext()).SessionData;
    }

    static public void setAppData(Activity context, PersonModel data) {
        ((ApplicationClass)context.getApplicationContext()).SessionData = data;
    }

    static public int getCurrentAccount(Activity context) {
        return ((ApplicationClass)context.getApplicationContext()).CurrentAccountID;
    }

    static public void setCurrentAccount(Activity context, int curentAccount) {
        ((ApplicationClass)context.getApplicationContext()).CurrentAccountID = curentAccount;
    }

    public Class getCurrentActivityClass() {
        return CurrentActivity.getClass();
    }

    public boolean currentActivityClassIs(Class isClass) {
        return getCurrentActivityClass() == isClass;
    }

    private void startSplashScreen() {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
