package ru.pozitivtelecom.cabinet.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.pozitivtelecom.cabinet.R;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private View mView;

    public AboutFragment() {}

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_about, container, false);

        mView.findViewById(R.id.nav_vk).setOnClickListener(this);
        mView.findViewById(R.id.nav_fb).setOnClickListener(this);
        mView.findViewById(R.id.nav_inst).setOnClickListener(this);

        return mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_vk:
                OpenURLInBrowser(R.string.url_vk);
                break;
            case R.id.nav_fb:
                OpenURLInBrowser(R.string.url_fb);
                break;
            case R.id.nav_inst:
                OpenURLInBrowser(R.string.url_inst);
                break;
            default:
        }
    }

    private void OpenURLInBrowser(int UrlResource)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(UrlResource)));
        startActivity(intent);
    }
}
