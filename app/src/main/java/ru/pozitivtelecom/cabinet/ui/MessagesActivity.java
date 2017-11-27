package ru.pozitivtelecom.cabinet.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.adapters.HistoryPayAdapter;
import ru.pozitivtelecom.cabinet.adapters.MessageAdapter;
import ru.pozitivtelecom.cabinet.models.HistoryPayItemModel;
import ru.pozitivtelecom.cabinet.models.MessageModel;

public class MessagesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    //private reference
    private LinearLayoutManager mLayoutManager;
    private List<MessageModel> mPaysList;
    private Type itemsListType = new TypeToken<List<MessageModel>>(){}.getType();
    private MessageAdapter mAdapter;

    //public reference

    //UI reference
    private LinearLayout mMainView;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbac);

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

        mPaysList = new ArrayList<>();

        MessageModel mNewMessage = new MessageModel();
        mNewMessage.IsAnswer = true;
        mNewMessage.Message = "sdfsdf";
        mNewMessage.Sender = "Служба поддержки";
        mPaysList.add(mNewMessage);

        mAdapter = new MessageAdapter(MessagesActivity.this, mPaysList);
        mRecyclerView.setAdapter(mAdapter);
        UpdateList();
    }

    private void UpdateList() {
    }

    @Override
    public void onRefresh() {

    }
}
