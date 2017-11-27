package ru.pozitivtelecom.cabinet.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.app.ApplicationClass;
import ru.pozitivtelecom.cabinet.models.AccountItemModel;
import ru.pozitivtelecom.cabinet.models.AccountsDataModel;
import ru.pozitivtelecom.cabinet.models.UsersDataModel;

public class AccountsSpinnerAdapter extends BaseAdapter {
    private Activity mContext;
    private List<AccountItemModel> mItems;

    public AccountsSpinnerAdapter(Activity context)
    {
        mContext = context;
        UpdateAdapter();
    }

    public void UpdateAdapter()
    {
        mItems = new ArrayList<>();
        for (UsersDataModel user: ApplicationClass.getAppData(mContext).Users) {
            for (AccountsDataModel account : user.Accounts) {
                mItems.add(new AccountItemModel(account.AccountID, account.AccountNO, account.Balance, account.Overdraft, account.OverdraftEnd, account.RecommendedPay));
            }
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LinearLayout view = convertView == null ? (LinearLayout)mContext.getLayoutInflater().inflate(R.layout.custom_spinner_items, viewGroup, false) : (LinearLayout)convertView;
        AccountItemModel curentItem = mItems.get(i);
        TextView accountNo = view.findViewById(R.id.txt_accountNo);
        accountNo.setText(curentItem.AccountNO);
        TextView accountDesc = view.findViewById(R.id.txt_accountDesc);
        accountDesc.setText(String.format("%.2f %s", curentItem.Balance, mContext.getString(R.string.valuta)));
        return view;
    }

    public AccountItemModel getListItem(int i)
    {
        return mItems.get(i);
    }
}

