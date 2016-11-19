package info.duhovniy.courierapp.datamodel;


import android.support.annotation.NonNull;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public interface IDataModel {

    @NonNull
    Observable<Courier> getObservableMe();

    Courier getMe();

    boolean isMyNameEmpty();

    void saveMeToCloud();

    void saveMeLocally();

    void restoreMeLocally();
}
