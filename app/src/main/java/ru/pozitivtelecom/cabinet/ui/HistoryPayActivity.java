package ru.pozitivtelecom.cabinet.ui;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.framed.Variant;
import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.adapters.HistoryPayAdapter;
import ru.pozitivtelecom.cabinet.app.ApplicationClass;
import ru.pozitivtelecom.cabinet.app.PreferencesClass;
import ru.pozitivtelecom.cabinet.models.HistoryPayItemModel;
import ru.pozitivtelecom.cabinet.models.UsersCabinetDataModel;
import ru.pozitivtelecom.cabinet.soap.JsonClass;
import ru.pozitivtelecom.cabinet.soap.OnSoapEventListener;
import ru.pozitivtelecom.cabinet.soap.SoapCalss;

public class HistoryPayActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //private reference
    private LinearLayoutManager mLayoutManager;
    private List<HistoryPayItemModel> mPaysList;
    private Type itemsListType = new TypeToken<List<HistoryPayItemModel>>(){}.getType();
    private HistoryPayAdapter mAdapter;
    private Calendar mCalendarBegin, mCalendarEnd;
    private boolean currentCalendarIsBegin;

    //public reference

    //UI reference
    private LinearLayout mMainView;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private EditText mDateBegin, mDateEnd;
    private CheckBox mPay, mWriteoff, mDebit;
    private SwipeRefreshLayout mSwipeRefresh;
    private View currentTextedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_pay);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.action_please_wait));
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });

        mMainView = findViewById(R.id.mainView);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_history_pay);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDateBegin = findViewById(R.id.txte_date_begin);
        mDateBegin.setOnClickListener(this);
        mDateEnd = findViewById(R.id.txte_date_end);
        mDateEnd.setOnClickListener(this);
        mPay = findViewById(R.id.chb_pay);
        mPay.setChecked(true);
        mWriteoff = findViewById(R.id.chb_writeoff);
        mWriteoff.setChecked(true);
        mDebit = findViewById(R.id.chb_debit);
        mDebit.setVisibility(View.GONE);
        mDebit.setChecked(false);

        mSwipeRefresh = findViewById(R.id.swipelayout);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(
                R.color.blue_swipe, R.color.green_swipe,
                R.color.orange_swipe, R.color.red_swipe);

        mCalendarBegin = GregorianCalendar.getInstance();
        mCalendarBegin.set(Calendar.DAY_OF_MONTH, mCalendarBegin.getActualMinimum(Calendar.DAY_OF_MONTH));
        setInitialDateTime(mDateBegin, mCalendarBegin);

        mCalendarEnd = GregorianCalendar.getInstance();
        mCalendarEnd.set(Calendar.DAY_OF_MONTH, mCalendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
        setInitialDateTime(mDateEnd, mCalendarEnd);

        mPaysList = new ArrayList<>();
        mAdapter = new HistoryPayAdapter(HistoryPayActivity.this, mPaysList);
        mRecyclerView.setAdapter(mAdapter);
        UpdateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_pay, menu);
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

    private void UpdateList()
    {
        mProgressDialog.show();

        final String token = PreferencesClass.Preferences.GetPreferences("token", "");

        Map<String, Object> mData = new HashMap<>();
        mData.put("accountid", ApplicationClass.getAppData(HistoryPayActivity.this).Users.get(0).Accounts.get(ApplicationClass.getCurrentAccount(HistoryPayActivity.this)).AccountID);
        mData.put("begin", Get1CDate(mCalendarBegin));
        mData.put("end", Get1CDate(mCalendarEnd));
        mData.put("pay", mPay.isChecked());
        mData.put("writeoff", mWriteoff.isChecked());
        mData.put("debit", mDebit.isChecked());
        String mDataString = new Gson().toJson(mData);

        Map<String, String> mProperty = new HashMap<>();
        mProperty.put("token", token);
        mProperty.put("command", "GetHistoryPay");
        mProperty.put("data", mDataString);

        SoapCalss soapObject = new SoapCalss("GetData", mProperty);
        soapObject.setSoapEventListener(new OnSoapEventListener() {
            @Override
            public void onChangeState(int state, String message) {}

            @Override
            public void onComplite(String Result) {
                mProgressDialog.dismiss();

                Map<String, Object> MapResult = JsonClass.json2map(Result);

                mPaysList = new Gson().fromJson(new Gson().toJson(MapResult.get("Data")), itemsListType);
                mAdapter = new HistoryPayAdapter(HistoryPayActivity.this, mPaysList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                });
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

    @Override
    public void onRefresh() {
        mSwipeRefresh.setRefreshing(false);
        UpdateList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txte_date_begin:
                OpenDatePicker(view, mCalendarBegin);
                break;
            case R.id.txte_date_end:
                OpenDatePicker(view, mCalendarEnd);
                break;
        }
    }

    private void OpenDatePicker(final View view, final Calendar calendar) {
        DatePickerDialog dialog = new DatePickerDialog(HistoryPayActivity.this, R.style.DialogTheme, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        currentCalendarIsBegin = view.getId() == R.id.txte_date_begin? true: false;
        currentTextedit = view;
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            Calendar currentCalendar = currentCalendarIsBegin? mCalendarBegin: mCalendarEnd;
            currentCalendar.set(Calendar.YEAR, year);
            currentCalendar.set(Calendar.MONTH, monthOfYear);
            currentCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime(currentTextedit, currentCalendar);
        }
    };

    private void setInitialDateTime(View view, Calendar calendar) {
        ((EditText)view).setText(DateUtils.formatDateTime(this, calendar.getTimeInMillis(),DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    private String Get1CDate(Calendar calendar) {
        return String.format("%d.%d.%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }
}
