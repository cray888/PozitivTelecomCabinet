package ru.pozitivtelecom.cabinet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class LoginActivity extends AppCompatActivity {

    // private references.
    private boolean mFirstInputPassword = false;
    //private AuthDataModel authData;

    // UI references.
    private RelativeLayout mMainView;
    private EditText mLoginView;
    private EditText mPasswordView;
    private TextInputLayout mPasswordInputLayout;
    private CheckBox mRememberMe;
    private TextView mForgotPassword;
    private TextView mSignup;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mMainView = findViewById(R.id.mainView);
        String mMessage = getIntent().getExtras().getString("message");
        if (mMessage != null && !TextUtils.isEmpty(mMessage))
        {
            Snackbar.make(mMainView, mMessage, Snackbar.LENGTH_LONG).show();
        }

        mLoginView = findViewById(R.id.txt_login);
        mLoginView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) hideSoftKeyboard(view);
            }
        });

        mPasswordInputLayout = findViewById(R.id.inputl_password);

        mPasswordView = findViewById(R.id.txt_password);
        mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) hideSoftKeyboard(view);
                else {
                    if (mFirstInputPassword == true) {
                        return;
                    }
                    mPasswordView.setText("");
                    mPasswordInputLayout.setPasswordVisibilityToggleEnabled(true);
                    mFirstInputPassword = true;
                }
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.txt_login || id == EditorInfo.IME_NULL) {
                    Login();
                    return true;
                }
                return false;
            }
        });

        Button mLoginSignInButton = findViewById(R.id.btn_login);
        mLoginSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        mRememberMe = findViewById(R.id.chb_remember_password);

        if (PreferencesClass.Preferences.GetPreferences("rememberpassword", false)) {
            mPasswordInputLayout.setPasswordVisibilityToggleEnabled(false);
            mRememberMe.setChecked(true);
            mLoginView.setText(PreferencesClass.Preferences.GetPreferences("login", ""));
            mPasswordView.setText(PreferencesClass.Preferences.GetPreferences("password", ""));
        }

        mForgotPassword = findViewById(R.id.txtv_forgot_password);
        mForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startForgotPasswordActivity();
            }
        });
        ImageView mImaegeViewForgotPassword = findViewById(R.id.img_forgot_password);
        mImaegeViewForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startForgotPasswordActivity();
            }
        });


        mSignup = findViewById(R.id.txtv_signup);
        mSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignUpActivity();
            }
        });
        ImageView mImageViewSignUp = findViewById(R.id.img_signup);
        mImageViewSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignUpActivity();
            }
        });
    }

    private void startForgotPasswordActivity() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void Login() {
        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid login.
        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else if (!isLoginValid(login)) {
            mLoginView.setError(getString(R.string.error_invalid_login));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            LoginOnServer(login, password);
        }
    }

    private void LoginOnServer(final String login, final String password) {
        mProgressDialog.show();

        String mDataString = new Gson().toJson(PreferencesClass.GetDeviceInfo(this));

        Map<String, String> mProperty = new HashMap<String, String>();
        mProperty.put("login", login);
        mProperty.put("password", password);
        mProperty.put("data", mDataString);

        SoapCalss authorization = new SoapCalss("Authorization", mProperty);
        authorization.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {

            }

            @Override
            public void onComplite(String Result) {
                mProgressDialog.dismiss();

                PreferencesClass.Preferences.SetPreferences("rememberpassword", mRememberMe.isChecked());

                final Map<String,Object> RsultMap = JsonClass.json2map(Result);

                if ((Boolean)RsultMap.get("Allowed") == true) {
                    ApplicationClass.setAppData(LoginActivity.this, new Gson().fromJson(new Gson().toJson(RsultMap.get("Data")), UsersCabinetDataModel.class));
                    if (mRememberMe.isChecked() == true) {
                        String Token = ApplicationClass.getAppData(LoginActivity.this).Token;
                        PreferencesClass.Preferences.SetPreferences("login", mLoginView.getText().toString());
                        PreferencesClass.Preferences.SetPreferences("password", mPasswordView.getText().toString());
                        PreferencesClass.Preferences.SetPreferences("token", Token);
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    PreferencesClass.Preferences.SetPreferences("rememberpassword", false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mMainView, RsultMap.get("Message").toString(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String Result) {
                mProgressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(mMainView, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
        authorization.execute();
    }

    private boolean isLoginValid(String login) {
        return true;
        //return login.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //return true;
        return !TextUtils.isEmpty(password);
    }
}

