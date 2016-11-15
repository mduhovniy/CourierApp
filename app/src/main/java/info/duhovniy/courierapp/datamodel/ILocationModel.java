package info.duhovniy.courierapp.datamodel;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import info.duhovniy.courierapp.data.Position;
import rx.Observable;

public interface ILocationModel {

    @NotNull
    Observable<Position> getLastPosition();

    @NotNull
    Observable<List<Position>> getPositions();
}
