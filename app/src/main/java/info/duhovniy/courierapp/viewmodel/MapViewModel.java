package info.duhovniy.courierapp.viewmodel;

import android.support.annotation.NonNull;

import info.duhovniy.courierapp.datamodel.IDataModel;
import info.duhovniy.courierapp.view.MapFragment;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by nickelAdmin on 15/11/2016.
 */

public class MapViewModel implements IViewModel {

    @NonNull
    private final CompositeSubscription mSubscription;

    @NonNull
    private final MapFragment mContext;

    @NonNull
    private final IDataModel mDataModel;

    public MapViewModel(@NonNull final IDataModel dataModel, @NonNull MapFragment context) {
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
}
