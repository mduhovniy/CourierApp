package info.duhovniy.courierapp.datamodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import rx.Observable;

public class DataModel implements IDataModel {

    // TAG for Shared Preferences
    private static final String PREF_TAG = "CourierApp_User";

    private List<Courier> mCouriers = new ArrayList<>();
    private Courier me;
    private Context mContext;

    public DataModel(Context context) {
        this.me = new Courier("", "", 0, false, 0, 0, 0);
        mContext = context;
        if (isMyNameEmpty())
            restoreMeFromPrefs();
        mockAllCouriers();
    }

    @NonNull
    @Override
    public Observable<List<Courier>> getObservableCouriers() {
        return Observable.create(o -> {
            o.onNext(mCouriers);
            o.onCompleted();
        });
    }

    @NonNull
    @Override
    public Observable<Courier> getObservableMe() {
        return Observable.create(o -> {
            o.onNext(me);
            o.onCompleted();
        });
    }

    @Override
    public Courier getMe() {
        return me;
    }

    @Override
    public boolean isMyNameEmpty() {
        return me.getName() == null || me.getName().isEmpty();
    }

    @Override
    public void setMe(Courier courier) {
        me = courier;
    }

    @Override
    public List<Courier> getCouriers() {
        return mCouriers;
    }

    @Override
    public void setCouriers(List<Courier> couriers) {
        mCouriers = couriers;
    }

    @Override
    public void saveMeToCloud() {
        saveMeToFirebase();
    }

    @Override
    public void saveMeLocally() {
        saveMeToPrefs();
    }

    @Override
    public void restoreMeLocally() {
        restoreMeFromPrefs();
    }

    private void saveMeToFirebase() {
        if (!me.getId().isEmpty())
            FirebaseDatabase.getInstance().getReference().child(me.getId()).setValue(me);
    }

    private void mockAllCouriers() {
        for(int i = 0; i < 10; i++) {
            Courier courier = new Courier("qwjjkdsh;dsagh;lg", "Sarah " + i, i, true, 32 + i, 34 + i, i);
            mCouriers.add(courier);
        }
    }

    private void restoreMeFromPrefs() {
        String fromPref = PreferenceManager.getDefaultSharedPreferences(mContext).getString(PREF_TAG, "");
        if (!fromPref.isEmpty()) {
            Gson gson = new GsonBuilder().create();
            Courier courier = gson.fromJson(fromPref, Courier.class);
            if (courier != null) {
                if (!courier.getName().equals("") && courier.getName() != null)
                    setMe(courier);
            }
        }
    }

    private void saveMeToPrefs() {
        if (me != null) {
            Gson gson = new GsonBuilder().create();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            prefs.edit().putString(PREF_TAG, gson.toJson(me)).apply();
        }
    }
}
