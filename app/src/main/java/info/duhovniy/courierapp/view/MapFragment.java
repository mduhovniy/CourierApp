package info.duhovniy.courierapp.view;


import android.Manifest;
import android.content.Context;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.kelvinapps.rxfirebase.DataSnapshotMapper;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.List;
import java.util.concurrent.TimeUnit;

import info.duhovniy.courierapp.CourierApplication;
import info.duhovniy.courierapp.data.Courier;
import info.duhovniy.courierapp.data.Utils;
import info.duhovniy.courierapp.viewmodel.MapViewModel;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    // Permissions constant block
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1975;
    // Map & Location constants
    private static final int ZOOM_LEVEL = 14;
    private static final long LOCATION_INTERVAL = 1000;         // ms
    private static final float LOCATION_DISPLACEMENT = 1;      // meters
    private static final long CLUSTER_RENDERING_INTERVAL = 1000;// ms

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private final LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(LOCATION_INTERVAL * 5)
            .setFastestInterval(LOCATION_INTERVAL)
            .setSmallestDisplacement(LOCATION_DISPLACEMENT);
    private ReactiveLocationProvider mLocationProvider;
    private ClusterManager<Courier> mClusterManager;

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private MapViewModel mViewModel;

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
        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mClusterManager.setRenderer(new OwnRendering(getContext(), mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mLocationProvider = new ReactiveLocationProvider(getContext());
        mViewModel.onResume();
        mSubscription.add(subscribeToFirstLocation());
        mSubscription.add(subscribeToUpdateLocation());
        mSubscription.add(subscribeToUpdateAction());
        mSubscription.add(subscribeToCouriers());
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
            return mLocationProvider.getLastKnownLocation()
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
                == PackageManager.PERMISSION_GRANTED)
            return mLocationProvider.getUpdatedLocation(locationRequest)
                    .debounce(LOCATION_INTERVAL, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.io())
                    .subscribe(location -> mViewModel.storeMyLocation(location),
                            mViewModel::handleError);
        else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        return Observable.just(null).subscribe();
    }

    private Subscription subscribeToUpdateAction() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            return mLocationProvider.getDetectedActivity((int) LOCATION_INTERVAL)
                    .debounce(LOCATION_INTERVAL, TimeUnit.MILLISECONDS)
                    .map(a -> a.getMostProbableActivity().getType())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.io())
                    .subscribe(state -> mViewModel.storeMyState(state),
                            mViewModel::handleError);
        else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        return Observable.just(null).subscribe();
    }

    private Subscription subscribeToCouriers() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            return RxFirebaseDatabase.observeValueEvent(FirebaseDatabase.getInstance().getReference(),
                    DataSnapshotMapper.listOf(Courier.class))
                    .debounce(CLUSTER_RENDERING_INTERVAL, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setUpCluster,
                            this::handleError);
        else
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
            }
        } else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
    }

    private void setUpCluster(List<Courier> list) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null && mClusterManager != null) {
                mClusterManager.clearItems();
                for (Courier courier : list) {
                    if (courier.isOn())
                        mClusterManager.addItem(courier);
                }
            }
        } else
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
    }

    private class OwnRendering extends DefaultClusterRenderer<Courier> {

//       Bitmap markerBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.map_marker);

        OwnRendering(Context context, GoogleMap map,
                     ClusterManager<Courier> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Courier item, MarkerOptions markerOptions) {

            markerOptions.title(item.getName());
            markerOptions.snippet(Utils.getCourierState(item.getState()));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(item.getColor()));
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

/*
    private static Bitmap prepareMarkerImage(Bitmap myBitmap, int color) {

        int[] allPixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];

        myBitmap.getPixels(allPixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

        for (int i = 0; i < allPixels.length; i++) {
            if (allPixels[i] == Color.BLACK) {
                allPixels[i] = color;
            }
        }

        myBitmap.setPixels(allPixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
        return myBitmap;
    }
*/

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
