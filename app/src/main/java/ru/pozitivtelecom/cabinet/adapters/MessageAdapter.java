package ru.pozitivtelecom.cabinet.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.pozitivtelecom.cabinet.Interface.MyRecycleViewInterface;
import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.models.MessageInModel;

//http://stacktips.com/tutorials/android/android-recyclerview-example

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CustomViewHolder> {
    private List<MessageInModel> itemList;
    private Context mContext;

    public MessageAdapter(Context context, List<MessageInModel> itemList) {
        this.itemList = itemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_message, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        MessageInModel item = itemList.get(i);

        customViewHolder.setItemClickListener(new MyRecycleViewInterface() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //On item click
            }
        });

        CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!item.IsGroup) {
            customViewHolder.mCardview.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
            if (item.IsAnswer)
                layoutParams.setMargins(0, 0, 50, 0);
            else
                layoutParams.setMargins(50, 0, 0, 0);
        }
        else {
            customViewHolder.mCardview.setCardBackgroundColor(mContext.getResources().getColor(R.color.message_group_background));
            layoutParams.setMargins(0, 0, 0, 0);
        }
        customViewHolder.mCardview.setLayoutParams(layoutParams);

        customViewHolder.mMessage.setText(Html.fromHtml(item.Message));

        if (TextUtils.isEmpty(item.Date) && TextUtils.isEmpty(item.Sender))
            customViewHolder.mLinearLayoutBottom.setVisibility(View.GONE);
        else
            customViewHolder.mLinearLayoutBottom.setVisibility(View.VISIBLE);

        customViewHolder.mDate.setText(item.Date);
        customViewHolder.mSender.setText(item.Sender);
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        protected CardView mCardview;
        protected TextView mMessage;
        protected TextView mDate;
        protected TextView mSender;
        protected LinearLayout mLinearLayoutBottom;

        private MyRecycleViewInterface recycleViewItemClickListener;

        public CustomViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            this.mCardview = view.findViewById(R.id.cardview);
            this.mCardview.setOnClickListener(this);
            this.mCardview.setOnLongClickListener(this);
            this.mMessage = view.findViewById(R.id.txt_message);
            this.mDate = view.findViewById(R.id.txt_date);
            this.mSender = view.findViewById(R.id.txt_sender);
            this.mLinearLayoutBottom = view.findViewById(R.id.linlayout_bottom);
        }

        public void setItemClickListener(MyRecycleViewInterface itemClickListener) {
            recycleViewItemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            recycleViewItemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            recycleViewItemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }
}