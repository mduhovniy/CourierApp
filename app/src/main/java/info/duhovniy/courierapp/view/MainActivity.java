package info.duhovniy.courierapp.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import info.duhovniy.courierapp.R;

public class MainActivity extends AppCompatActivity {

    private MapFragment mapFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = new MapFragment();

        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.map) != null) {
            fragmentManager.beginTransaction().replace(R.id.map,
                    mapFragment, "MAP_FRAGMENT").commit();
        }
    }
}
