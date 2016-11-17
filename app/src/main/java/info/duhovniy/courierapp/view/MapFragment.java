package info.duhovniy.courierapp.view;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import info.duhovniy.courierapp.CourierApplication;
import info.duhovniy.courierapp.viewmodel.MapViewModel;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    // Permissions constants block
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1975;
    // Map constants
    private static final int ZOOM_LEVEL = 16;
    private static final long LOCATION_INTERVAL = 1000;         // ms
    private static final float LOCATION_DISPLACEMENT = 100;     // meters

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private ReactiveLocationProvider locationProvider;
    private MapViewModel mViewModel;
    private final LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(LOCATION_INTERVAL * 5)
            .setFastestInterval(LOCATION_INTERVAL)
            .setSmallestDisplacement(LOCATION_DISPLACEMENT);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new MapViewModel(CourierApplication.get(getActivity()).getDataModel());
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
            switch (requestCode) {
                case INITIAL_REQUEST:
            }
            // Permission Granted
            Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Granted", Toast.LENGTH_SHORT).show();
            useMap();
        } else {
            // Permission Denied
            Toast.makeText(getContext(), "ACCESS_FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void useMap() {
        mSubscription.add(subscribeToFirstLocation());
        mSubscription.add(subscribeToUpdateLocation());
        mSubscription.add(subscribeToUpdateAction());
    }

    @Override
    public void onDestroy() {
        mViewModel.onPause();
        mSubscription.clear();
        super.onDestroy();
    }

    private Subscription subscribeToFirstLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationProvider = new ReactiveLocationProvider(getContext());
            return locationProvider.getLastKnownLocation()
                    .subscribe(location -> {
                        startMyTracking(location);
                        mViewModel.storeMyLocation(location);
                    }, this::handleError);
        } else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        return Observable.just(null).subscribe();
    }

    private Subscription subscribeToUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationProvider = new ReactiveLocationProvider(getContext());
            return locationProvider.getUpdatedLocation(locationRequest)
                    .subscribe(location -> {
                        mViewModel.storeMyLocation(location);
                        mViewModel.storeMyColor(mViewModel.getMe().getColor() + 1);
                    }, this::handleError);
        } else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        return Observable.just(null).subscribe();
    }

    private Subscription subscribeToUpdateAction() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationProvider = new ReactiveLocationProvider(getContext());
            return locationProvider.getDetectedActivity((int) LOCATION_INTERVAL)
                    .map(a -> a.getMostProbableActivity().getType())
                    .subscribe(state -> mViewModel.storeMyState(state),
                            this::handleError);
        } else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        return Observable.just(null).subscribe();
    }

    private void startMyTracking(Location location) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM_LEVEL));
                mMap.setMyLocationEnabled(true);
                mViewModel.storeMyLocation(location);
            }
        } else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
    }

    public void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
