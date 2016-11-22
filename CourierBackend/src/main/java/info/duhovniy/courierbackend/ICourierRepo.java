package info.duhovniy.courierbackend;


import java.util.List;

public interface ICourierRepo {

    Courier getCourierById(String id);

    List<Courier> getAllCouriers();

    void createCourier(Courier courier);

    void updateCourier(Courier courier);

    void deleteCourier(Courier courier);

    void clear();

    boolean isEmpty();
}
