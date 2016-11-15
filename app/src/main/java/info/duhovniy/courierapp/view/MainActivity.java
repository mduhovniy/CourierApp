package info.duhovniy.courierapp.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import info.duhovniy.courierapp.CourierApplication;
import info.duhovniy.courierapp.R;
import info.duhovniy.courierapp.data.Courier;
import info.duhovniy.courierapp.databinding.ActivityMainBinding;
import info.duhovniy.courierapp.datamodel.IDataModel;
import info.duhovniy.courierapp.viewmodel.MainViewModel;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private MainViewModel mViewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        checkGPS();

        MapFragment mapFragment = new MapFragment();
        mapFragment.setUpMapFragment(getDataModel());

        if (findViewById(R.id.map) != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment, "MAP_FRAGMENT").commit();

        String fromPref = PreferenceManager.getDefaultSharedPreferences(this).getString("Me", "");
        if (!fromPref.equals("")) {
            Gson gson = new GsonBuilder().create();
            getDataModel().setMe(gson.fromJson(fromPref, Courier.class));
            binding.editTextUsername.setText(getDataModel().getMe().getName());
            binding.switchVisibility.setChecked(getDataModel().getMe().isOn());
        }
        mViewModel = new MainViewModel(getDataModel(), this);
    }

    private void checkGPS() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.GPS_check_response))
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSubscription.add(subscribeToNameChanges());
        mSubscription.add(subscribeToOnSwitchChanges());
        mViewModel.onResume();
    }

    private Subscription subscribeToNameChanges() {
        return RxTextView.textChanges(binding.editTextUsername)
                .map(String::valueOf)
                .filter(s -> (s.length() > 0))
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::changeMyName,
                        this::handleError);
    }

    private Subscription subscribeToOnSwitchChanges() {
        return RxCompoundButton.checkedChanges(binding.switchVisibility)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::turnMeOn,
                        this::handleError);
    }

    public void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void changeMyName(String name) {
        mViewModel.getMe().setName(name);
        FirebaseDatabase.getInstance().getReference().child(mViewModel.getMe().getId()).setValue(mViewModel.getMe());
    }

    private void turnMeOn(boolean isOn) {
        mViewModel.getMe().setOn(isOn);
        FirebaseDatabase.getInstance().getReference().child(mViewModel.getMe().getId()).setValue(mViewModel.getMe());
    }

    @Override
    protected void onPause() {
        mViewModel.onPause();
        mSubscription.clear();
        super.onPause();
    }

    @NonNull
    private IDataModel getDataModel() {
        return CourierApplication.get(this).getDataModel();
    }
}
