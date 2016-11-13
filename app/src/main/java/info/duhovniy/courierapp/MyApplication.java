package info.duhovniy.courierapp;

import android.app.Application;
import android.support.annotation.NonNull;

import info.duhovniy.courierapp.datamodel.DataModel;
import info.duhovniy.courierapp.datamodel.IDataModel;


public class MyApplication extends Application {

    @NonNull
    private final IDataModel mDataModel;

    public MyApplication() {
        mDataModel = new DataModel();
    }

    @NonNull
    public IDataModel getDataModel() {
        return mDataModel;
    }

//    @NonNull
//    public ISchedulerProvider getSchedulerProvider() {
//        return SchedulerProvider.getInstance();
//    }
//
//    @NonNull
//    public MainViewModel getViewModel() {
//        return new MainViewModel(getDataModel(), getSchedulerProvider());
//    }
}
