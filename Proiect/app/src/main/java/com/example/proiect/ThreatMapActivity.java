package com.example.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proiect.data.ThreatEvent;
import com.example.proiect.data.ThreatRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ThreatMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final List<ThreatEvent> events = new ArrayList<>();
    private GoogleMap googleMap;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private View bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threat_map);

        progressBar = findViewById(R.id.progressMap);
        tvStatus = findViewById(R.id.tvMapStatus);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        AppNavigation.setup(this, bottomNavigation, R.id.nav_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            AppNavigation.select(bottomNavigation, R.id.nav_map);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.setOnInfoWindowClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof String) {
                Intent intent = new Intent(this, ThreatDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_EVENT_ID, (String) tag);
                startActivity(intent);
            }
        });
        renderMarkers();
    }

    private void loadEvents() {
        progressBar.setVisibility(View.VISIBLE);
        ThreatRepository.getInstance().loadEvents(this, (loadedEvents, error) -> {
            progressBar.setVisibility(View.GONE);
            events.clear();
            events.addAll(loadedEvents);
            tvStatus.setText("Incidente: " + events.size());
            renderMarkers();
        });
    }

    private void renderMarkers() {
        if (googleMap == null || events.isEmpty()) {
            return;
        }
        googleMap.clear();
        for (ThreatEvent event : events) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .title(event.getLoaderFamily() + " - " + event.getSeverity())
                    .snippet(event.getCity() + " | risc " + event.getRiskScore())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor(event.getSeverity()))));
            if (marker != null) {
                marker.setTag(event.getId());
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.5, 12.0), 4.2f));
    }

    private float markerColor(String severity) {
        if ("Ridicat".equalsIgnoreCase(severity)) {
            return BitmapDescriptorFactory.HUE_RED;
        }
        if ("Mediu".equalsIgnoreCase(severity)) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        }
        return BitmapDescriptorFactory.HUE_GREEN;
    }
}
