package info.duhovniy.courierapp.datamodel;


import android.support.annotation.NonNull;

import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public interface IDataModel {

    @NonNull
    Observable<List<Courier>> getObservableCouriers();

    @NonNull
    Observable<Courier> getObservableMe();

    Courier getMe();

    boolean isMyNameEmpty();

    List<Courier> getCouriers();

    void setCouriers(List<Courier> couriers);

    void addCourier(Courier courier);

    void saveMeToCloud();

    void saveMeLocally();

    void restoreMeLocally();
}
