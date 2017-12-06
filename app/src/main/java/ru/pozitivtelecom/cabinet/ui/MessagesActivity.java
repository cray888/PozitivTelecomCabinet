package ru.pozitivtelecom.cabinet.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.adapters.MessageAdapter;
import ru.pozitivtelecom.cabinet.app.PreferencesClass;
import ru.pozitivtelecom.cabinet.models.MainModel;
import ru.pozitivtelecom.cabinet.models.MessageInModel;
import ru.pozitivtelecom.cabinet.models.MessageOutModel;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.soap.SoapClass;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //private reference
    private LinearLayoutManager mLayoutManager;
    private List<MessageInModel> mPaysList;
    private Type itemsListType = new TypeToken<List<MessageInModel>>(){}.getType();
    private MessageAdapter mAdapter;

    //public reference

    //UI reference
    private LinearLayout mMainView;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefresh;
    private EditText mMessage;
    private ImageButton mButtonSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        String extraMessage = "";
        if (getIntent().getExtras() != null) {
            extraMessage = getIntent().getExtras().getString("message");
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mMainView = findViewById(R.id.mainView);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_message);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefresh = findViewById(R.id.swipelayout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(
                R.color.blue_swipe, R.color.green_swipe,
                R.color.orange_swipe, R.color.red_swipe);

        mMessage = findViewById(R.id.txte_message);
        if (!TextUtils.isEmpty(extraMessage)) mMessage.setText(extraMessage);

        mButtonSendMessage = findViewById(R.id.btn_send_message);
        mButtonSendMessage.setOnClickListener(this);

        UpdateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_refresh:
                UpdateList();
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(false);
        UpdateList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_message:
                SendMessage();
                break;
        }
    }

    public void UpdateList() {
        mProgressDialog.show();

        final String token = PreferencesClass.Preferences.GetPreferences("token", "");

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", token);
        mProperty.put("command", "Message");
        mProperty.put("data", "");

        SoapClass soapObject = new SoapClass("GetData", mProperty);
        soapObject.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {}

            @Override
            public void onComplete(String Result) {
                mProgressDialog.dismiss();

                final MainModel resultClass = new Gson().fromJson(Result, MainModel.class);

                if (!resultClass.Error) {
                    mPaysList = new Gson().fromJson(new Gson().toJson(resultClass.Data), itemsListType);
                    mAdapter = new MessageAdapter(MessagesActivity.this, mPaysList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.scrollToPosition(mPaysList.size() - 1);
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mMainView, resultClass.Message, Snackbar.LENGTH_LONG).show();
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
        soapObject.execute();
    }

    private void SendMessage() {
        String message = mMessage.getText().toString();

        if (TextUtils.isEmpty(message)) return;

        mProgressDialog.show();

        final String token = PreferencesClass.Preferences.GetPreferences("token", "");

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", token);
        mProperty.put("command", "Message");
        mProperty.put("data", new Gson().toJson(new MessageOutModel(message)));

        SoapClass soapObject = new SoapClass("PutData", mProperty);
        soapObject.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {}

            @Override
            public void onComplete(String Result) {
                mProgressDialog.dismiss();

                final MainModel resultClass = new Gson().fromJson(Result, MainModel.class);

                if (!resultClass.Error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMessage.setText("");
                            UpdateList();
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(mMainView, resultClass.Message, Snackbar.LENGTH_LONG).show();
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
        soapObject.execute();
    }
}

