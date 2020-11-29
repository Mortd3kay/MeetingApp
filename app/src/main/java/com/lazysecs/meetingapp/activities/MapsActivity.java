package com.lazysecs.meetingapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.models.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        initButtons();
    }

    private void checkPrevLocation() {
        Intent intent = getIntent();
        GeoPoint prevLocation = intent.getParcelableExtra("EXTRA_PREV_LOCATION");
        if (prevLocation != null) {
            currentLocation.setLatitude(prevLocation.getLatitude());
            currentLocation.setLongitude(prevLocation.getLongitude());
        }
    }

    private void initButtons() {
        MaterialButton addLocationButton = findViewById(R.id.setLocationButton);
        MaterialButton cancelButton = findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(v -> onBackPressed());

        addLocationButton.setOnClickListener(v -> {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses == null)
                Toast.makeText(this, "Не удалось выбрать точку, попробуйте снова", Toast.LENGTH_SHORT).show();

            else if (addresses.size() > 0) {
                Intent intent = new Intent();
                GeoPoint geoPoint = new GeoPoint(
                        map.getCameraPosition().target.latitude,
                        map.getCameraPosition().target.longitude,
                        addresses.get(0).getAddressLine(0));
                intent.putExtra("EXTRA_NEW_LOCATION", geoPoint);
                setResult(0, intent);
            }

            onBackPressed();
        });
    }

    private void fetchLastLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                new AlertDialog.Builder(this)
                        .setTitle("Использование Вашей геопозиции")
                        .setMessage("Чтобы находить события вблизи от Вас приложению потребутся доступ к Вашей геопозиции во время работы приложения.")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE);
                        })
                        .setNegativeButton("НЕТ", (dialogInterface, i) -> {

                        })
                        .create()
                        .show();

                return;
            }
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                checkPrevLocation();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                if (supportMapFragment != null) {
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(MapsActivity.this);
        }
    }

    private String getAddressByLatLng(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Objects.requireNonNull(addresses).size() > 0) {
            return addresses.get(0).getAddressLine(0);
        } else return "";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastLocation();
            }
        }
    }
}
