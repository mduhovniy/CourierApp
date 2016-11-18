package info.duhovniy.courierapp.viewmodel;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import info.duhovniy.courierapp.datamodel.IDataModel;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;


public class MapViewModel implements IViewModel {

    @NonNull
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    @NonNull
    private final IDataModel mDataModel;

    public MapViewModel(@NonNull final IDataModel dataModel) {
        mDataModel = dataModel;
    }

    @Override
    public void onResume() {
//        mSubscription.add(fetchFromFirebase());
    }

    @Override
    public void onPause() {
        mSubscription.clear();
    }

    // TODO: move down to IDataModel -> DataModel
//    private Subscription fetchFromFirebase() {
//        return
//    }

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

    // TODO: remove this - for test purposes only!
    public Courier getMe() {
        return mDataModel.getMe();
    }

    public Observable<List<Courier>> getObservableCouriers() {
        return mDataModel.getObservableCouriers();
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
