package info.duhovniy.courierapp.data;


import static com.google.android.gms.location.DetectedActivity.IN_VEHICLE;
import static com.google.android.gms.location.DetectedActivity.ON_BICYCLE;
import static com.google.android.gms.location.DetectedActivity.ON_FOOT;
import static com.google.android.gms.location.DetectedActivity.RUNNING;
import static com.google.android.gms.location.DetectedActivity.STILL;
import static com.google.android.gms.location.DetectedActivity.TILTING;
import static com.google.android.gms.location.DetectedActivity.WALKING;

public class Utils {

    public static String getCourierState(int state) {
        switch (state) {
            case IN_VEHICLE:
                return "Now driving";
            case ON_BICYCLE:
                return "Now riding on bike";
            case ON_FOOT:
                return "Now walking or running";
            case RUNNING:
                return "Now running";
            case STILL:
                return "Now staying";
            case TILTING:
                return "Now titling";
            case WALKING:
                return "Now walking";
            default:
                return "Activity unreachable";
        }
    }
}
