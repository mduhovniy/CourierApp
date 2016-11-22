package info.duhovniy.courierbackend;


import java.util.ArrayList;
import java.util.List;

class CourierRepo implements ICourierRepo {

    private List<Courier> list;

    CourierRepo() {
        list = new ArrayList<>();
    }

    @Override
    public Courier getCourierById(String id) {
        Courier result = new Courier();
        for (Courier c : list)
            if (c.getId().equals(id))
                result = c;
        return result;
    }

    @Override
    public List<Courier> getAllCouriers() {
        return list;
    }

    @Override
    public void createCourier(Courier courier) {
        list.add(courier);
    }

    @Override
    public void updateCourier(Courier courier) {

    }

    @Override
    public void deleteCourier(Courier courier) {

    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
