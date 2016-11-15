package info.duhovniy.courierapp.view;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    // Permissions constants block
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static MapFragment instance = null;

    public static MapFragment getInstance() {
        if (instance == null) {
            instance = new MapFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of GoogleAPIClient.
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            return;
        }
        useMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == INITIAL_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted
            Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Granted", Toast.LENGTH_SHORT).show();
            useMap();
        } else {
            // Permission Denied
            Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void useMap() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getContext());
                locationProvider.getLastKnownLocation()
                        .subscribe(location -> {
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        });
            }
        }
    }

}
