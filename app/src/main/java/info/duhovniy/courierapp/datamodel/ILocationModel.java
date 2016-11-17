package info.duhovniy.courierapp.datamodel;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import info.duhovniy.courierapp.data.MarkerColor;
import rx.Observable;

public interface ILocationModel {

    @NotNull
    Observable<MarkerColor> getLastPosition();

    @NotNull
    Observable<List<MarkerColor>> getPositions();
}
