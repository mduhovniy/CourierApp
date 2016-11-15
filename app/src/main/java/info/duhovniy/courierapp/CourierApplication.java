package info.duhovniy.courierapp;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

import info.duhovniy.courierapp.data.Courier;
import info.duhovniy.courierapp.datamodel.DataModel;
import info.duhovniy.courierapp.datamodel.IDataModel;


public class CourierApplication extends Application {

    @NonNull
    private final IDataModel mDataModel = new DataModel(new Courier("", "", 0, false, 0, 0, 0));

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static CourierApplication get(Context context) {
        return (CourierApplication) context.getApplicationContext();
    }

    @Override
    public void onTerminate() {
        // TODO: the DataModel backup on
        super.onTerminate();
    }

    @NonNull
    public IDataModel getDataModel() {
        return mDataModel;
    }

}
