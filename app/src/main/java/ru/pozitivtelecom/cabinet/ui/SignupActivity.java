package ru.pozitivtelecom.cabinet.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.models.Capcha;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.soap.SoapCalss;

public class SignupActivity extends AppCompatActivity {

    //UI reference
    private Toolbar mToolbar;
    private EditText mCapcha;
    private ImageView mCapchaImg;
    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder mAlertDialog;

    //Private reference
    private String mCapchaUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_pay);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mCapcha = findViewById(R.id.etxt_capcha_number);
        mCapchaImg = findViewById(R.id.img_capcha);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {}
        });

        mAlertDialog = new AlertDialog.Builder(this);

        TextView mAcceptPD = findViewById(R.id.txtv_accept_personal_data);
        mAcceptPD.setText(Html.fromHtml(getString(R.string.accept_personal_data)));
        mAcceptPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy_policy)));
                startActivity(browserIntent);
            }
        });

        getCapcha();
    }

    private void getCapcha() {
        mCapcha.setText("");
        mProgressDialog.show();
        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", "");
        mProperty.put("command", "GetCapcha");
        mProperty.put("data", "");

        SoapCalss soap = new SoapCalss("GetData", mProperty);
        soap.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {
            }

            @Override
            public void onComplite(String Result) {
                Capcha soapResult = new Gson().fromJson(Result, Capcha.class);
                mCapchaUID = soapResult.UID;
                byte[] decodedString = Base64.decode(soapResult.Image, Base64.DEFAULT);
                final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        mCapchaImg.setImageBitmap(decodedByte);
                    }
                });
            }

            @Override
            public void onError(String Result) {
                showDialog(getString(R.string.no_internet), true);
            }
        });
        soap.execute();
    }

    private void showDialog(final String message, final boolean finish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                mAlertDialog.setTitle(null);
                mAlertDialog.setMessage(message);
                mAlertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (finish) finish(); else getCapcha();
                    }
                });
                AlertDialog dialog = mAlertDialog.create();
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });
    }
}

class SoapResultSingUp {
    public boolean Error;
    public String Message;
}