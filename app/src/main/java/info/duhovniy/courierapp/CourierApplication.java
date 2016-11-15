package info.duhovniy.courierapp;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

import info.duhovniy.courierapp.datamodel.DataModel;
import info.duhovniy.courierapp.datamodel.IDataModel;


public class CourierApplication extends Application {

    @NonNull
    private static IDataModel mDataModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mDataModel = new DataModel();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static CourierApplication get(Context context) {
        return (CourierApplication) context.getApplicationContext();
    }

    @Override
    public void onTerminate() {
        // TODO: the DataModel backup to SharedPreferences
        super.onTerminate();
    }

    @NonNull
    public IDataModel getDataModel() {
        return mDataModel;
    }

}
