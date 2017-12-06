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
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.models.CapchaModel;
import ru.pozitivtelecom.cabinet.models.MainModel;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.soap.SoapClass;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    //UI reference
    private Toolbar mToolbar;
    private EditText mName, mAddress, mCapcha;
    private MaskedEditText mPhone;
    private ImageView mCapchaImg;
    private Button mSendContact;
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

        mName = findViewById(R.id.txte_name);
        mPhone = findViewById(R.id.txte_phone);
        mAddress = findViewById(R.id.txte_address);
        mCapcha = findViewById(R.id.txte_capcha);
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

        mSendContact = findViewById(R.id.btn_send_contact);
        mSendContact.setOnClickListener(this);

        getCapcha();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCapcha() {
        mCapcha.setText("");
        mProgressDialog.show();
        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", "");
        mProperty.put("command", "Capcha");
        mProperty.put("data", "");

        SoapClass soap = new SoapClass("GetData", mProperty);
        soap.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {
            }

            @Override
            public void onComplete(String Result) {
                CapchaModel soapResult = new Gson().fromJson(Result, CapchaModel.class);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_contact:
                checkInputData();
                break;
        }
    }

    private void checkInputData() {
        boolean isCancel = false;

        String name = mName.getText().toString();
        String phone = mPhone.getRawText();
        String address = mAddress.getText().toString();
        String capcha = mCapcha.getText().toString();

        mName.setError(null);
        mPhone.setError(null);
        mAddress.setError(null);
        mCapcha.setError(null);

        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(name)) {
            mPhone.setError(getString(R.string.error_capcha_no_empty));
            isCancel = true;
        }

        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(phone)) {
            mPhone.setError(getString(R.string.error_capcha_no_empty));
            isCancel = true;
        }

        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(address)) {
            mPhone.setError(getString(R.string.error_capcha_no_empty));
            isCancel = true;
        }

        if (TextUtils.isEmpty(capcha)) {
            mCapcha.setError(getString(R.string.error_capcha_no_empty));
            isCancel = true;
        }

        if (isCancel) return;

        sendData(name, phone, address, capcha);
    }

    private void sendData(String name, String phone, String address, String capcha) {
        mProgressDialog.show();
        Map<String, String> mData = new HashMap<>();
        mData.put("CapchaUID", mCapchaUID);
        mData.put("CapchaValue", capcha);
        mData.put("Name", name);
        mData.put("Phone", phone);
        mData.put("Address", address);

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", "");
        mProperty.put("command", "Contact");
        mProperty.put("data", new Gson().toJson(mData));

        SoapClass soap = new SoapClass("PutData", mProperty);
        soap.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {
            }

            @Override
            public void onComplete(String Result) {
                MainModel resultClass = new Gson().fromJson(Result, MainModel.class);
                if (resultClass.Error) {
                    showDialog(resultClass.Message, false);
                } else {
                    showDialog(resultClass.Message, true);
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