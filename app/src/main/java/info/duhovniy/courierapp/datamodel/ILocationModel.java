package info.duhovniy.courierapp.datamodel;


import android.location.Location;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import rx.Observable;

public interface ILocationModel {

    @NotNull
    Observable<Location> getLastLocation();

    @NotNull
    Observable<List<Location>> getLocations();
}
