package ru.pozitivtelecom.cabinet.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.adapters.PopupMapAdapter;
import ru.pozitivtelecom.cabinet.map.OnlineTileProvider;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLongClickListener {

    //Private
    private GoogleMap mMap;

    //Public

    //UI
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.title_activity_map);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //TileProvider provider = new OnlineTileProvider("http://tile.tgt72.ru/%d/%d/%d.png");
        TileProvider provider = new OnlineTileProvider("http://a.tile.openstreetmap.org/%d/%d/%d.png");
        if (provider != null) {
            mMap.setMapType(0);
            mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        }
        mMap.setOnCameraIdleListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setInfoWindowAdapter(new PopupMapAdapter(getLayoutInflater())); //https://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet

        LatLng latLng = new LatLng(57.107648, 65.586107);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        mMap.addMarker(new MarkerOptions().position(latLng).title("ООО \"Позитив Телеком\"").snippet(
                "Телефон: (3452) 51-44-51\n" +
                "График работы: круглосуточно\n" +
                "Техническая поддержка: круглосуточно\n" +
                "Абонентский отдел: ежедневно с 10:00 - 22:00").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));

        map.setMapType(GoogleMap.MAP_TYPE_NONE);
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}
