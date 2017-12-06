package ru.pozitivtelecom.cabinet.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.ui.MessagesActivity;

public class CustomDialog {
    static public void ShowDialog(final Context context, final String title, String description, final String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tariff_service_description);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        TextView mTitle = dialog.findViewById(R.id.txt_title);
        mTitle.setText(title);

        TextView mDescription = dialog.findViewById(R.id.txt_description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
        else
            mDescription.setText(Html.fromHtml(description));
        if (TextUtils.isEmpty(description)) mDescription.setVisibility(View.GONE);

        Button mConnect = dialog.findViewById(R.id.btn_connect);
        Button mCancel = dialog.findViewById(R.id.btn_cancel);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                intent.putExtra("message", message);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.95);
        dialog.getWindow().setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }
}
