package ru.pozitivtelecom.cabinet.task;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import ru.pozitivtelecom.cabinet.app.ApplicationClass;
import ru.pozitivtelecom.cabinet.app.PreferencesClass;
import ru.pozitivtelecom.cabinet.models.MainModel;
import ru.pozitivtelecom.cabinet.models.PersonModel;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.soap.SoapClass;

public class AppTaskTimer extends TimerTask {
    private Activity context;

    public AppTaskTimer(Activity context) {
        this.context = context;
    }

    @Override
    public void run() {
        checkToken();
    }

    private void checkToken() {
        final String token = PreferencesClass.Preferences.GetPreferences("token", "");
        if (token == "") {return;}

        String mDataString = new Gson().toJson(PreferencesClass.GetDeviceInfo(context));

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", token);
        mProperty.put("data", mDataString);

        SoapClass authentication = new SoapClass("Authentication", mProperty);
        authentication.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {}

            @Override
            public void onComplete(String Result) {
                final MainModel resultClass = new Gson().fromJson(Result, MainModel.class);

                ApplicationClass.setAppData(context, new Gson().fromJson(new Gson().toJson(resultClass.Data), PersonModel.class));

                if (!resultClass.Error) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateMainActivitySpinner"));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UpdateMainFragmentData"));
                } else {
                    PreferencesClass.Preferences.SetPreferences("token", "");
                }
            }

            @Override
            public void onError(String Result) { }
        });
        authentication.execute();
    }
}
