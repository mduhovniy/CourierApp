package info.duhovniy.courierapp.datamodel;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public class DataModel implements IDataModel {

    private List<Courier> mCouriers = new ArrayList<>();

    public DataModel() {
        mockAllCouriers();
    }

    @NonNull
    @Override
    public Observable<List<Courier>> getAllCouriers() {
        return Observable.just(mCouriers);
    }

    private void mockAllCouriers() {
        for(int i = 0; i < 10; i++) {
            Courier courier = new Courier("qwjjkdsh;dsagh;lg", "Sarah " + i, i, true, 32 + i, 34 + i, i);
            mCouriers.add(courier);
        }
    }
}
