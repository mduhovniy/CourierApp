package info.duhovniy.courierapp.viewmodel;

import android.location.Location;
import android.support.annotation.NonNull;

import info.duhovniy.courierapp.data.Courier;
import info.duhovniy.courierapp.datamodel.IDataModel;
import rx.subscriptions.CompositeSubscription;


public class MapViewModel implements IViewModel {

    @NonNull
    private final CompositeSubscription mSubscription;

    @NonNull
    private final IDataModel mDataModel;

    public MapViewModel(@NonNull final IDataModel dataModel) {
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
        mDataModel.saveMeToCloud();
    }

    public void storeMyState(int state) {
        mDataModel.getMe().setState(state);
        mDataModel.saveMeToCloud();
    }

    public void storeMyColor(int color) {
        mDataModel.getMe().setColor(color);
        mDataModel.saveMeToCloud();
    }

    public Courier getMe() {
        return mDataModel.getMe();
    }
}
