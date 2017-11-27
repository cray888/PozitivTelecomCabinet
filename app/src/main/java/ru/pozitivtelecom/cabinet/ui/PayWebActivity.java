package ru.pozitivtelecom.cabinet.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.pozitivtelecom.cabinet.R;

public class PayWebActivity extends AppCompatActivity {

    //Public

    //Private
    private boolean isClosed = false;

    //UI
    private ProgressDialog mProgressDialog;
    private WebView mWebView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        String method = bundle.getString("provider");
        String conract = bundle.getString("contract");
        String summ = bundle.getString("summ").replace(',', '.');

        setContentView(R.layout.activity_pay_web);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_pay);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mWebView = findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25");
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressDialog.dismiss();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!checkURL(url)) mProgressDialog.show();
                super.onPageStarted(view, url, favicon);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mProgressDialog.dismiss();
                if (!request.getUrl().toString().contains("127.0.0.1")) Snackbar.make(view, getText(R.string.error_load_provider_site), Snackbar.LENGTH_SHORT).show();
            }
        });
        String postData;
        switch (method)
        {
            case "sberbank":
                postData = String.format("account=%s&amount=%s&user_account=&mode=", conract, summ)  ;
                mWebView.postUrl("https://lk.pozitivtelecom.ru:8010/sberbank_a/confirm", postData.getBytes());
                break;
            case "yandex":
                postData = String.format("shopId=&scid=47023&CustomerNumber=%s&Sum=%s&orderNumber=&signature=&paymentType=PC&mode=&user_account=%s", conract, summ, conract)  ;
                mWebView.postUrl("https://lk.pozitivtelecom.ru:8010/yandex/confirm", postData.getBytes());
                break;
            case "terminal":
                postData = String.format("shopId=&scid=47023&CustomerNumber=%s&Sum=%s&orderNumber=&signature=&paymentType=GP&mode=&user_account=%s", conract, summ, conract)  ;
                mWebView.postUrl("https://lk.pozitivtelecom.ru:8010/yandex/confirm", postData.getBytes());
                break;
            default:
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
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

    private boolean checkURL(String url) {
        if ((url.indexOf("https://lk.pozitivtelecom.ru/") != -1) || (url.indexOf("https://pozitivtelecom.ru/") != -1) || (url.indexOf("http://pozitivtelecom.ru/") != -1)) {
            if (!isClosed) {
                setResult(RESULT_OK);
                finish();
            }
            isClosed = true;
            return true;
        }
        return false;
    }
}
