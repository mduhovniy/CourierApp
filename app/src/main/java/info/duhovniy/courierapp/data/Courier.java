package info.duhovniy.courierapp.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.maps.android.clustering.ClusterItem;

@IgnoreExtraProperties
public class Courier implements ClusterItem {
    private String id;
    private String name;
    private int color;
    private boolean isOn;
    private double lat;
    private double lng;
    private int state;

    public Courier() {
    }

    public Courier(String id, String name, int color, boolean isOn, double lat, double lng, int state) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.isOn = isOn;
        this.lat = lat;
        this.lng = lng;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }
}
