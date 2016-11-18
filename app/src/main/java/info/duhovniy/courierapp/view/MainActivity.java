package info.duhovniy.courierapp.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import info.duhovniy.courierapp.CourierApplication;
import info.duhovniy.courierapp.R;
import info.duhovniy.courierapp.databinding.ActivityMainBinding;
import info.duhovniy.courierapp.viewmodel.MainViewModel;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private MainViewModel mViewModel;
    private ActivityMainBinding binding;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new MainViewModel(CourierApplication.get(this).getDataModel());

        checkGPS();

        mapFragment = new MapFragment();
        if (findViewById(R.id.map) != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment, "MAP_FRAGMENT").commit();
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
        mSubscription.add(subscribeToMe());
        mViewModel.onResume();
    }

    @Override
    protected void onPause() {
        mViewModel.onPause();
        mSubscription.clear();
        super.onPause();
    }

    private Subscription subscribeToMe() {
        return mViewModel.getObservableMe()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(c -> {
                    binding.editTextUsername.setText(c.getName());
                    binding.switchVisibility.setChecked(c.isOn());
                }, this::handleError);
    }

    private Subscription subscribeToNameChanges() {
        return RxTextView.textChanges(binding.editTextUsername)
                .map(String::valueOf)
                .filter(s -> (s.length() > 0))
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> mViewModel.changeMyName(s),
                        this::handleError);
    }

    private Subscription subscribeToOnSwitchChanges() {
        return RxCompoundButton.checkedChanges(binding.switchVisibility)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(b -> mViewModel.turnMeOn(b),
                        this::handleError);
    }

    @Override
    protected void onDestroy() {
        mapFragment = null;
        super.onDestroy();
    }

    public void handleError(Throwable throwable) {
        throwable.printStackTrace();
    }
}
