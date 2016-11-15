package info.duhovniy.courierapp.datamodel;


import android.support.annotation.NonNull;

import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public interface IDataModel {

    @NonNull
    Observable<List<Courier>> getAllCouriers();
}
