package info.duhovniy.courierapp.view;


import android.Manifest;
import android.content.Context;
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

import info.duhovniy.courierapp.CourierApplication;
import info.duhovniy.courierapp.datamodel.IDataModel;
import info.duhovniy.courierapp.viewmodel.MapViewModel;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.subscriptions.CompositeSubscription;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {


    // Permissions constants block
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private MapViewModel mViewModel;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mViewModel = new MapViewModel(getDataModel(), mContext);
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
        // TODO: here to subscribe for all events!!!
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
                            mMap.setMyLocationEnabled(true);
                        });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.onPause();
        mSubscription.clear();
    }

    @NonNull
    private IDataModel getDataModel() {
        return CourierApplication.get(mContext).getDataModel();
    }
}
