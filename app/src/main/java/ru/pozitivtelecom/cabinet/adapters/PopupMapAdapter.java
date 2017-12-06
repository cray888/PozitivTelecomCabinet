package ru.pozitivtelecom.cabinet.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ru.pozitivtelecom.cabinet.R;

public class PopupMapAdapter implements GoogleMap.InfoWindowAdapter {
    private View view = null;
    private LayoutInflater inflater = null;

    public PopupMapAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (view == null) view = inflater.inflate(R.layout.map_popup_window, null);

        TextView textView = view.findViewById(R.id.txt_title);
        textView.setText(marker.getTitle());

        textView = view.findViewById(R.id.txt_snippet);
        textView.setText(marker.getSnippet());

        return view;
    }
}
