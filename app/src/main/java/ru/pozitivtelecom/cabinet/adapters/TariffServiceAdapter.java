package ru.pozitivtelecom.cabinet.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.pozitivtelecom.cabinet.Interface.MyRecycleViewInterface;
import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.models.TariffServiceModel;

//http://stacktips.com/tutorials/android/android-recyclerview-example

public class TariffServiceAdapter extends RecyclerView.Adapter<TariffServiceAdapter.CustomViewHolder> {
    private List<TariffServiceModel> itemList;
    private Context mContext;

    public TariffServiceAdapter(Context context, List<TariffServiceModel> itemList) {
        this.itemList = itemList;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        TariffServiceModel item = itemList.get(position);
        return item.Type == 1? 1: 2;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == 1)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_service_description, null);
        else
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_service, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final TariffServiceModel item = itemList.get(i);

        customViewHolder.setItemClickListener(new MyRecycleViewInterface() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick == false)
                {
                    if (item.Type == 3) CustomDialog.ShowDialog(mContext, item.Name, item.FullDescription, item.GetServiceMessage);
                }
            }
        });

        if (customViewHolder.mCardview != null) {
            CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, item.Type == 2 && i > 0 ? 48 : 0, 0, 0);
            customViewHolder.mCardview.setLayoutParams(layoutParams);
        }

        if (!TextUtils.isEmpty(item.BackgroundColor)) {
            int backGroundColor = Color.parseColor("#" + item.BackgroundColor);
            if (customViewHolder.mCardview != null)
                customViewHolder.mCardview.setCardBackgroundColor(backGroundColor);
            else
                customViewHolder.mLinearLayout.setBackgroundColor(backGroundColor);
        }

        if (!TextUtils.isEmpty(item.TextColor)) {
            int textColor = Color.parseColor("#" + item.TextColor);
            customViewHolder.mName.setTextColor(textColor);
            customViewHolder.mDescription.setTextColor(textColor);
            customViewHolder.mSubDescription.setTextColor(textColor);
        }

        if (!TextUtils.isEmpty(item.ImageSrc)) {
            customViewHolder.mLogo.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(item.ImageSrc).into(customViewHolder.mLogo);
            //new ImageLoadTask(item.ImageSrc, customViewHolder.mLogo).execute();
        }
        else customViewHolder.mLogo.setVisibility(View.GONE);


        if (!TextUtils.isEmpty(item.Name)) {
            customViewHolder.mName.setVisibility(View.VISIBLE);
            customViewHolder.mName.setText(item.Name);
        }
        else customViewHolder.mName.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(item.Description)) {
            customViewHolder.mDescription.setVisibility(View.VISIBLE);
            customViewHolder.mDescription.setText(item.Description);
        }
        else customViewHolder.mDescription.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(item.SubDescription)) {
            customViewHolder.mSubDescription.setVisibility(View.VISIBLE);
            customViewHolder.mSubDescription.setText(item.SubDescription);
        }
        else customViewHolder.mSubDescription.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        protected CardView mCardview;
        protected LinearLayout mLinearLayout;
        protected ImageView mLogo;
        protected TextView mName;
        protected TextView mDescription;
        protected TextView mSubDescription;

        private MyRecycleViewInterface recycleViewItemClickListener;

        public CustomViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            this.mCardview = view.findViewById(R.id.cardview);
            if (this.mCardview != null) {
                this.mCardview.setOnClickListener(this);
                this.mCardview.setOnLongClickListener(this);
            }
            this.mLinearLayout = view.findViewById(R.id.linlayout_root);

            this.mLogo = view.findViewById(R.id.img_logo);
            this.mName = view.findViewById(R.id.txt_name);
            this.mDescription = view.findViewById(R.id.txt_description);
            this.mSubDescription = view.findViewById(R.id.txt_sub_description);
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