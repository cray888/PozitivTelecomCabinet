package ru.pozitivtelecom.cabinet.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.app.ApplicationClass;
import ru.pozitivtelecom.cabinet.models.AccountsDataModel;
import ru.pozitivtelecom.cabinet.models.UsersCabinetDataModel;

public class MainFragment extends Fragment implements View.OnClickListener {

    private ViewGroup mView;

    public MainFragment() {}

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (ViewGroup)inflater.inflate(R.layout.fragment_main, container, false);

        mView.findViewById(R.id.cardv_account).setOnClickListener(this);
        mView.findViewById(R.id.cardv_internet).setOnClickListener(this);
        mView.findViewById(R.id.cardv_tv).setOnClickListener(this);
        mView.findViewById(R.id.cardv_tvn).setOnClickListener(this);
        mView.findViewById(R.id.cardv_akcii).setOnClickListener(this);
        mView.findViewById(R.id.cardv_pay).setOnClickListener(this);
        mView.findViewById(R.id.cardv_po).setOnClickListener(this);

        mView.findViewById(R.id.txt_history_pay).setOnClickListener(this);
        mView.findViewById(R.id.txt_feedback).setOnClickListener(this);

        UpdateData();
        return mView;
    }

    public void UpdateData() {
        int CurentAccaunt = ApplicationClass.getCurrentAccount(getActivity());
        UsersCabinetDataModel CabinetData = ApplicationClass.getAppData(getActivity());

        TextView mAccountNumber = mView.findViewById(R.id.txt_account);
        mAccountNumber.setText(Html.fromHtml(String.format(getString(R.string.main_fragment_account_number), CabinetData.Users.get(0).Accounts.get(CurentAccaunt).AccountNO)));

        TextView mBalance = mView.findViewById(R.id.txt_balance);
        mBalance.setText(String.format("%.2f %s", CabinetData.Users.get(0).Accounts.get(CurentAccaunt).Balance, getContext().getString(R.string.valuta)));

        TextView mPeriodStart = mView.findViewById(R.id.txt_periond_start);
        mPeriodStart.setText(CabinetData.Users.get(0).Accounts.get(CurentAccaunt).PeriodStart);

        TextView mPeriodEnd = mView.findViewById(R.id.txt_periond_end);
        mPeriodEnd.setText(CabinetData.Users.get(0).Accounts.get(CurentAccaunt).PeriodEnd);

        TextView mSumNextMonth = mView.findViewById(R.id.txt_sum_next_month);
        mSumNextMonth.setText(String.format("%.2f %s", CabinetData.Users.get(0).Accounts.get(CurentAccaunt).SummNextMonth, getContext().getString(R.string.valuta)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.cardv_account:
                OpenAccountActivity();
                break;
            case R.id.cardv_internet:
                OpenURLInBrowser(R.string.url_internet);
                break;
            case R.id.cardv_tv:
                OpenURLInBrowser(R.string.url_tv);
                break;
            case R.id.cardv_tvn:
                OpenURLInBrowser(R.string.url_tvn);
                break;
            case R.id.cardv_akcii:
                OpenURLInBrowser(R.string.url_akcii);
                break;
            case R.id.cardv_pay:
                OpenPayActivity();
                break;
            case R.id.cardv_po:
                OpenURLInBrowser(R.string.url_po);
                break;
            case R.id.txt_history_pay:
                OpenHistoryPayActivity();
                break;
            case R.id.txt_feedback:
                OpenFeedbackActivity();
                break;
            default:
        }
    }

    private void OpenFeedbackActivity() {
        Intent intent = new Intent(getActivity(), MessagesActivity.class);
        startActivity(intent);
    }

    private void OpenHistoryPayActivity() {
        Intent intent = new Intent(getActivity(), HistoryPayActivity.class);
        startActivity(intent);
    }

    private void OpenURLInBrowser(int UrlResource)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(UrlResource)));
        startActivity(intent);
    }

    private void OpenAccountActivity()
    {
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        startActivity(intent);
    }

    private void OpenPayActivity()
    {
        int mCurrentAccountID = ApplicationClass.getCurrentAccount(getActivity());
        AccountsDataModel mAccount =  ApplicationClass.getAppData(getActivity()).Users.get(0).Accounts.get(mCurrentAccountID);

        Intent intent = new Intent(getActivity(), PayActivity.class);
        intent.putExtra("accountNO", mAccount.AccountNO);
        intent.putExtra("recommendedPay", String.format("%.2f", mAccount.RecommendedPay));
        startActivity(intent);
    }
}
