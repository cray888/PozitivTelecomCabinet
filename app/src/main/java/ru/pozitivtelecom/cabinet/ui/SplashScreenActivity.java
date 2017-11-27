package ru.pozitivtelecom.cabinet.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ru.pozitivtelecom.cabinet.app.ApplicationClass;
import ru.pozitivtelecom.cabinet.models.UsersCabinetDataModel;
import ru.pozitivtelecom.cabinet.soap.JsonClass;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.app.PreferencesClass;
import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.soap.SoapCalss;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkToken();
    }

    private void checkToken()
    {
        final String token = PreferencesClass.Preferences.GetPreferences("token", "");
        if (token == "") {
            startLoginActiviry("");
            return;
        }

        String mDataString = new Gson().toJson(PreferencesClass.GetDeviceInfo(this));

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", token);
        mProperty.put("data", mDataString);

        SoapCalss authentication = new SoapCalss("Authentication", mProperty);
        authentication.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {}

            @Override
            public void onComplite(String Result) {
                Map<String, Object> MapResult = JsonClass.json2map(Result);
                ApplicationClass.setAppData(SplashScreenActivity.this, new Gson().fromJson(new Gson().toJson(MapResult.get("Data")), UsersCabinetDataModel.class));

                if ((boolean)MapResult.get("Allowed")) {
                    startMainActivity();
                } else {
                    PreferencesClass.Preferences.SetPreferences("token", "");
                    startLoginActiviry((String)MapResult.get("Message"));
                }
            }

            @Override
            public void onError(String Result) {
                startLoginActiviry(getString(R.string.no_internet));
            }
        });
        authentication.execute();
    }

    private void startLoginActiviry(String message)
    {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        intent.putExtra("message", message);
        startActivity(intent);
        finish();
    }

    private void startMainActivity()
    {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
