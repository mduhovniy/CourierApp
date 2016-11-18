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
                mSubscription.add(signInAnonymously());
        }
    }

    @Override
    public void onPause() {
        mSubscription.clear();
        mDataModel.saveMeLocally();
    }

    // TODO: move down to IDataModel -> DataModel
    private Subscription signInAnonymously() {
        return RxFirebaseAuth.signInAnonymously(FirebaseAuth.getInstance())
                .flatMap(x -> RxFirebaseUser.getToken(FirebaseAuth.getInstance().getCurrentUser(), false))
                .map(token -> FirebaseAuth.getInstance().getCurrentUser().getUid())
                // TODO: test subscription on different thread
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    mDataModel.getMe().setId(id);
                    mDataModel.saveMeToCloud();
                }, this::handleError);
    }

    public void changeMyName(String name) {
        mDataModel.getMe().setName(name);
        mDataModel.saveMeToCloud();
    }

    public void turnMeOn(boolean isOn) {
        mDataModel.getMe().setOn(isOn);
        mDataModel.saveMeToCloud();
    }

    public Observable<Courier> getObservableMe() {
        return mDataModel.getObservableMe();
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
