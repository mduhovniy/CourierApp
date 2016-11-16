package info.duhovniy.courierapp.viewmodel;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import info.duhovniy.courierapp.datamodel.IDataModel;
import rx.subscriptions.CompositeSubscription;


public class MapViewModel implements IViewModel {

    @NonNull
    private final CompositeSubscription mSubscription;

    @NonNull
    private final Context mContext;

    @NonNull
    private final IDataModel mDataModel;

    public MapViewModel(@NonNull final IDataModel dataModel, @NonNull final Context context) {
        mContext = context;
        mDataModel = dataModel;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        mSubscription.clear();
    }

    public void storeMyLocation(Location location) {
        mDataModel.getMe().setLat(location.getLatitude());
        mDataModel.getMe().setLng(location.getLongitude());
        mDataModel.saveMeToFirebase();
    }

    public void storeMyState(int state) {
        mDataModel.getMe().setState(state);
        mDataModel.saveMeToFirebase();
    }

    // TODO: delete after location updates testing
    public int getState() {
        return mDataModel.getMe().getState();
    }
}
