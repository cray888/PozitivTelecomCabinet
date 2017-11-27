package ru.pozitivtelecom.cabinet.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.models.Capcha;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.soap.SoapCalss;

public class ForgotPasswordActivity extends AppCompatActivity {

    //UI reference
    private LinearLayout mMainView;
    private Toolbar mToolbar;
    private MaskedEditText mPhone;
    private EditText mContract, mCapcha;
    private ImageView mCapchaImg;
    private Button mSendPassword;
    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder mAlertDialog;

    //Private reference
    private String mCapchaUID;

    //Global reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mMainView = findViewById(R.id.llay_main_view);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_pay);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {}
        });

        mAlertDialog = new AlertDialog.Builder(this);

        mPhone = findViewById(R.id.etxt_phone_number);
        mContract = findViewById(R.id.etxt_contract_number);
        mCapcha = findViewById(R.id.etxt_capcha_number);

        mSendPassword = findViewById(R.id.btn_send_password);
        mSendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputData();
            }
        });

        mCapchaImg = findViewById(R.id.img_capcha);
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

    private void checkInputData() {
        boolean isCancel = false;

        String phone = mPhone.getRawText();
        String contract = mContract.getText().toString();
        String capcha = mCapcha.getText().toString();

        mPhone.setError(null);
        mContract.setError(null);
        mCapcha.setError(null);


        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(contract)) {
            mPhone.setError(getString(R.string.error_phone_no_empty));
            isCancel = true;
        }

        if (TextUtils.isEmpty(capcha)) {
            mCapcha.setError(getString(R.string.error_capcha_no_empty));
            isCancel = true;
        }

        if (isCancel) return;

        sendData(phone, contract, capcha);
    }

    private void sendData(String phone, String contract, String capcha) {
        mProgressDialog.show();
        Map<String, String> mData = new HashMap<>();
        mData.put("CapchaUID", mCapchaUID);
        mData.put("CapchaValue", capcha);
        mData.put("Phone", phone);
        mData.put("Contract", contract);

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", "");
        mProperty.put("command", "ForgotPassword");
        mProperty.put("data", new Gson().toJson(mData));

        SoapCalss soap = new SoapCalss("PutData", mProperty);
        soap.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {
            }

            @Override
            public void onComplite(String Result) {
                SoapResultForgotPassword soapResult = new Gson().fromJson(Result, SoapResultForgotPassword.class);
                if (soapResult.Error) {
                    showDialog(soapResult.Message, false);
                } else {
                    showDialog(soapResult.Message, true);
                }
            }

            @Override
            public void onError(String Result) {
                showDialog(getString(R.string.no_internet), false);
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

class SoapResultForgotPassword {
    public boolean Error;
    public String Message;
}
