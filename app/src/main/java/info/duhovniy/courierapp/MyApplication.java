package info.duhovniy.courierapp;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import info.duhovniy.courierapp.datamodel.DataModel;
import info.duhovniy.courierapp.datamodel.IDataModel;


public class MyApplication extends Application {
    @NonNull
    private final IDataModel mDataModel;

    public static Context CONTEXT;

    public MyApplication(Context context) {
        mDataModel = new DataModel();
        CONTEXT = context;
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
