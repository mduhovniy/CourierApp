package info.duhovniy.courierapp.datamodel;

import android.support.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public class DataModel implements IDataModel {

    private List<Courier> mCouriers = new ArrayList<>();
    private Courier me;

    public DataModel(Courier me) {
        this.me = me;
        mockAllCouriers();
    }

    @NonNull
    @Override
    public Observable<List<Courier>> getAllCouriers() {
        return Observable.just(mCouriers);
    }

    @Override
    public Courier getMe() {
        return me;
    }

    @Override
    public void setMe(Courier courier) {
        me = courier;
    }

    @Override
    public void saveMeToFirebase() {
        FirebaseDatabase.getInstance().getReference().child(getMe().getId()).setValue(getMe());
    }

    private void mockAllCouriers() {
        for(int i = 0; i < 10; i++) {
            Courier courier = new Courier("qwjjkdsh;dsagh;lg", "Sarah " + i, i, true, 32 + i, 34 + i, i);
            mCouriers.add(courier);
        }
    }
}
