package info.duhovniy.courierapp.viewmodel;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelvinapps.rxfirebase.RxFirebaseAuth;
import com.kelvinapps.rxfirebase.RxFirebaseUser;

import java.util.List;

import info.duhovniy.courierapp.data.Courier;
import info.duhovniy.courierapp.datamodel.IDataModel;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class MainViewModel implements IViewModel {

    private Courier mCourier;

    @NonNull
    private final CompositeSubscription mSubscription;

    @NonNull
    private final Context mContext;

    @NonNull
    private final IDataModel mDataModel;

    public MainViewModel(@NonNull final IDataModel dataModel, @NonNull Context context) {
        mContext = context;
        mDataModel = dataModel;
        mSubscription = new CompositeSubscription();
        restoreMyInstance();
    }

    public Courier getMe() {
        return mCourier;
    }

    public void setMe(Courier courier) {
        mCourier = courier;
    }

    private void signInAnonymously() {
        mSubscription.add(RxFirebaseAuth.signInAnonymously(FirebaseAuth.getInstance())
                .flatMap(x -> RxFirebaseUser.getToken(FirebaseAuth.getInstance().getCurrentUser(), false))
                .subscribe(token -> {
                    setMe(new Courier(FirebaseAuth.getInstance().getCurrentUser().getUid(), "", 1, false, 2, 3, 4));
                    FirebaseDatabase.getInstance().getReference().child(getMe().getId()).setValue(getMe());
                    Toast.makeText(mContext, getMe().getId(), Toast.LENGTH_LONG).show();
                }, throwable -> {
                    Toast.makeText(mContext, throwable.toString(), Toast.LENGTH_SHORT).show();
                }));
    }

    @NonNull
    private Observable<List<Courier>> getAllCouriers() {
        return mDataModel.getAllCouriers();
    }

    @Override
    public void destroy() {
        if (mSubscription.hasSubscriptions())
            mSubscription.unsubscribe();
        saveMyInstance();
    }

    private void saveMyInstance() {
        if (mCourier != null) {
            Gson gson = new GsonBuilder().create();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            prefs.edit().putString("Me", gson.toJson(mCourier)).apply();
        }
    }

    private void restoreMyInstance() {
        Gson gson = new GsonBuilder().create();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String fromPref = prefs.getString("Me", "");
        if (fromPref.equals("")) {
            signInAnonymously();
        } else {
            mCourier = gson.fromJson(fromPref, Courier.class);

        }
    }
}
