package ru.pozitivtelecom.cabinet.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.models.HistoryPayItemModel;

//http://stacktips.com/tutorials/android/android-recyclerview-example

public class HistoryPayAdapter extends RecyclerView.Adapter<HistoryPayAdapter.CustomViewHolder> {
    private List<HistoryPayItemModel> payItemList;
    private Context mContext;

    public HistoryPayAdapter(Context context, List<HistoryPayItemModel> historyPayItemList) {
        this.payItemList = historyPayItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_history_pay, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        HistoryPayItemModel payItem = payItemList.get(i);

        customViewHolder.mPayType.setVisibility(View.VISIBLE);
        switch (payItem.State) {
            case "":
                customViewHolder.mPayType.setVisibility(View.INVISIBLE);
                break;
            case "in":
                customViewHolder.mPayType.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
                customViewHolder.mPayType.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_green));
                break;
            case "out":
                customViewHolder.mPayType.setImageResource(R.drawable.ic_arrow_upward_black_24dp);
                customViewHolder.mPayType.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_red));
                break;
        }

        customViewHolder.mSumm.setText(payItem.Summ);
        customViewHolder.mComment.setText(payItem.Comment);
        if (TextUtils.isEmpty(payItem.Service)) customViewHolder.mService.setVisibility(View.GONE); else customViewHolder.mService.setVisibility(View.VISIBLE);
        customViewHolder.mService.setText(payItem.Service);
        customViewHolder.mDate.setText(payItem.Date);
        customViewHolder.mSender.setText(payItem.Sender);
    }

    @Override
    public int getItemCount() {
        return (null != payItemList ? payItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView mPayType;
        protected TextView mSumm;
        protected TextView mComment;
        protected TextView mService;
        protected TextView mDate;
        protected TextView mSender;

        public CustomViewHolder(View view) {
            super(view);
            this.mPayType = view.findViewById(R.id.img_pay_type);
            this.mSumm = view.findViewById(R.id.txt_summ);
            this.mComment = view.findViewById(R.id.txt_comment);
            this.mService = view.findViewById(R.id.txt_service);
            this.mDate = view.findViewById(R.id.txt_date);
            this.mSender = view.findViewById(R.id.txt_sender);
        }
    }
}