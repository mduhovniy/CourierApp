package info.duhovniy.courierapp.viewmodel;

import android.content.Context;
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
}
