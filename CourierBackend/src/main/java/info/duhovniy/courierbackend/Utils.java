package info.duhovniy.courierbackend;


public class Utils {

    public static String getCourierState(int state) {
        switch (state) {
            case 0:
                return "Now driving";
            case 1:
                return "Now riding on bike";
            case 2:
                return "Now walking or running";
            case 8:
                return "Now running";
            case 3:
                return "Now staying";
            case 5:
                return "Now titling";
            case 7:
                return "Now walking";
            default:
                return "Activity unreachable";
        }
    }
}
