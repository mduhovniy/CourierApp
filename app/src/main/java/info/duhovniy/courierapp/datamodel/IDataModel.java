package info.duhovniy.courierapp.datamodel;


import android.support.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public interface IDataModel {

    @NonNull
    Observable<List<Courier>> getAllCouriers();

    @NonNull
    Observable<Courier> getCourierById(int id);
}
