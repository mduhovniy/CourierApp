package info.duhovniy.courierapp.datamodel;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationListener;

import java.util.List;

import info.duhovniy.courierapp.MyApplication;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

public class LocationModel implements ILocationModel {

    private static final long LOCATION_MIN_TIME_BETWEEN_UPDATES = 0L;
    private static final float LOCATION_MIN_DISTANCE_BETWEEN_UPDATES = 0f;

    private static final LocationManager LOCATION_MANAGER = (LocationManager) MyApplication.CONTEXT.getSystemService(Context.LOCATION_SERVICE);

    private static final Observable<Location> mLocationObservable = createLocationObservable();

    private static Observable<Location> createLocationObservable() {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(final Subscriber<? super Location> subscriber) {
                // Starting Location Services
                Criteria locationCriteria = new Criteria();
                locationCriteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

                final LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        subscriber.onNext(location);
                    }
                };

                if (ActivityCompat.checkSelfPermission(MyApplication.CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.CONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LOCATION_MANAGER.requestLocationUpdates(LOCATION_MIN_TIME_BETWEEN_UPDATES, LOCATION_MIN_DISTANCE_BETWEEN_UPDATES,
                        locationCriteria, (android.location.LocationListener) locationListener, Looper.getMainLooper());

                // On Unsubscribe
                subscriber.add(Subscriptions.create(() -> {
                    // Stopping Location Services
                    LOCATION_MANAGER.removeUpdates((android.location.LocationListener) locationListener);
                }));
            }
        }).publish()
                .refCount();
    }

    /**
     * @return Observable that observes on UI Thread.
     */
    public static Observable<Location> getLocationObservable() {
        return mLocationObservable.observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return Observable that observes on UI Thread.
     */
    public static Observable<Float> createSpeedObservable() {
        return mLocationObservable.map(Location::getSpeed).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<Location> getLastLocation() {
        return null;
    }

    @NonNull
    @Override
    public Observable<List<Location>> getLocations() {
        return null;
    }
}
