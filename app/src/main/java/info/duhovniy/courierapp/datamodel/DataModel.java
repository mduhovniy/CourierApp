package info.duhovniy.courierapp.datamodel;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public class DataModel implements IDataModel {

    private List<Courier> mockedCouriers;

    public DataModel() {
        mockAllCouriers();
    }

    @NotNull
    @Override
    public Observable<List<Courier>> getAllCouriers() {
        return Observable.just(mockedCouriers);
    }

    @NotNull
    @Override
    public Observable<Courier> getCourierById(int id) {
        return Observable.just(mockedCouriers.get(id));
    }

    private void mockAllCouriers() {
        for(int i = 0; i < 10; i++) {
            mockedCouriers.add(new Courier(i, "RED", "Sarah " + i, 32 + i, 34 + i));
        }
    }
}
