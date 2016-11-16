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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainViewModel implements IViewModel {
    // TAG for Shared Preferences
    private static final String PREF_TAG = "CourierApp_User";

    @NonNull
    private final CompositeSubscription mSubscription;

    @NonNull
    private final Context mContext;

    @NonNull
    private final IDataModel mDataModel;

    public MainViewModel(@NonNull final IDataModel dataModel, @NonNull final Context context) {
        mContext = context;
        mDataModel = dataModel;
        mSubscription = new CompositeSubscription();
        if (mDataModel.getMe().getName().equals(""))
            restoreMeFromPrefs();
    }

    @Override
    public void onResume() {
        if (mDataModel.getMe() != null) {
            if (mDataModel.getMe().getName().equals(""))
                mSubscription.add(signInAnonymously(mDataModel.getMe()));
        }
    }

    public Courier getMe() {
        return mDataModel.getMe();
    }

    private Subscription signInAnonymously(Courier courier) {
        return RxFirebaseAuth.signInAnonymously(FirebaseAuth.getInstance())
                .flatMap(x -> RxFirebaseUser.getToken(FirebaseAuth.getInstance().getCurrentUser(), false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(token -> {
                    courier.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference().child(courier.getId()).setValue(courier);
                    Toast.makeText(mContext, courier.getId(), Toast.LENGTH_LONG).show();
                    mDataModel.setMe(courier);
                }, throwable -> {
                    Toast.makeText(mContext, throwable.toString(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onPause() {
        mSubscription.clear();
        saveMeToPrefs();
    }

    private void restoreMeFromPrefs() {
        String fromPref = PreferenceManager.getDefaultSharedPreferences(mContext).getString(PREF_TAG, "");
        if (!fromPref.equals("")) {
            Gson gson = new GsonBuilder().create();
            Courier courier = gson.fromJson(fromPref, Courier.class);
            if (courier != null) {
                if (!courier.getName().equals("") && courier.getName() != null)
                    mDataModel.setMe(courier);
            }
        }
    }

    private void saveMeToPrefs() {
        if (mDataModel.getMe() != null) {
            Gson gson = new GsonBuilder().create();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            prefs.edit().putString(PREF_TAG, gson.toJson(mDataModel.getMe())).apply();
        }
    }

    public void changeMyName(String name) {
        mDataModel.getMe().setName(name);
        mDataModel.saveMeToFirebase();
    }

    public void turnMeOn(boolean isOn) {
        mDataModel.getMe().setOn(isOn);
        mDataModel.saveMeToFirebase();
    }

    @NonNull
    private Observable<List<Courier>> getAllCouriers() {
        return mDataModel.getAllCouriers();
    }
}
