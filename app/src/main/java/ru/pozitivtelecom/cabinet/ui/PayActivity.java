package ru.pozitivtelecom.cabinet.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import ru.pozitivtelecom.cabinet.R;

public class PayActivity extends AppCompatActivity {

    //UI
    private WebView mWebView;
    private Toolbar mToolbar;
    private EditText mContract;
    private EditText mSumm;
    private Button mDebitCard;
    private Button YandexMoney;
    private Button mTerminalCode;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_pay);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });

        mContract = findViewById(R.id.etxt_contract);
        mContract.setText(getIntent().getExtras().getString("accountNO"));

        mSumm = findViewById(R.id.etxt_summ_pay);
        mSumm.setText(getIntent().getExtras().getString("recommendedPay"));

        mDebitCard = findViewById(R.id.btn_debit_card);
        mDebitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPayMethod(view);
            }
        });

        YandexMoney = findViewById(R.id.btn_yandex);
        YandexMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPayMethod(view);
            }
        });

        mTerminalCode = findViewById(R.id.btn_terminal);
        mTerminalCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPayMethod(view);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) finish();
    }

    private void selectPayMethod(View view)
    {
        hideSoftKeyboard(view);

        boolean err = false;
        boolean startIntent = false;

        if (!checkContract()) err = true;
        if (!checkSumm()) err = true;

        if (err) return;

        Intent intent = new Intent(this, PayWebActivity.class);
        intent.putExtra("contract", mContract.getText().toString());
        intent.putExtra("summ", mSumm.getText().toString());
        switch (view.getId())
        {
            case R.id.btn_debit_card:
                intent.putExtra("provider", "sberbank");
                startIntent = true;
                break;
            case R.id.btn_yandex:
                intent.putExtra("provider", "yandex");
                startIntent = true;
                break;
            case R.id.btn_terminal:
                intent.putExtra("provider", "terminal");
                startIntent = true;
                break;
            default:
        }
        if (startIntent) startActivityForResult(intent, 0);
    }

    private void hideSoftKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean checkContract() {
        mContract.setError(null);
        if (TextUtils.isEmpty(mContract.getText().toString())) {
            mContract.setError(getString(R.string.error_required_contract));
            return false;
        }
        return true;
    }

    private boolean checkSumm() {
        mSumm.setError(null);
        if (TextUtils.isEmpty(mSumm.getText().toString())) {
            mSumm.setError(getString(R.string.error_required_summ));
            return false;
        }
        return true;
    }
}
