package info.duhovniy.courierapp.viewmodel;


import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.kelvinapps.rxfirebase.RxFirebaseAuth;
import com.kelvinapps.rxfirebase.RxFirebaseUser;

import info.duhovniy.courierapp.data.Courier;
import info.duhovniy.courierapp.datamodel.IDataModel;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class MainViewModel implements IViewModel {
    // TAG for Shared Preferences
    private static final String PREF_TAG = "CourierApp_User";

    @NonNull
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    @NonNull
    private final IDataModel mDataModel;

    public MainViewModel(@NonNull final IDataModel dataModel) {
        mDataModel = dataModel;
        if (mDataModel.isMyNameEmpty())
            mDataModel.restoreMeLocally();
    }

    @Override
    public void onResume() {
        if (mDataModel.getMe() != null) {
            if (mDataModel.isMyNameEmpty())
                mSubscription.add(signInAnonymously(mDataModel.getMe()));
        }
    }

    private Subscription signInAnonymously(Courier courier) {
        return RxFirebaseAuth.signInAnonymously(FirebaseAuth.getInstance())
                .flatMap(x -> RxFirebaseUser.getToken(FirebaseAuth.getInstance().getCurrentUser(), false))
                .map(token -> FirebaseAuth.getInstance().getCurrentUser().getUid())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    courier.setId(id);
                    mDataModel.setMe(courier);
                    mDataModel.saveMeToCloud();
                }, this::handleError);
    }

    @Override
    public void onPause() {
        mSubscription.clear();
        mDataModel.saveMeLocally();
    }

    public void changeMyName(String name) {
        mDataModel.getMe().setName(name);
        mDataModel.saveMeToCloud();
    }

    public void turnMeOn(boolean isOn) {
        mDataModel.getMe().setOn(isOn);
        mDataModel.saveMeToCloud();
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public Observable<Courier> getObservableMe() {
        return mDataModel.getObservableMe();
    }
}
