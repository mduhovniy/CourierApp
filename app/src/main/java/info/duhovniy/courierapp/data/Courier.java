package info.duhovniy.courierapp.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.maps.android.clustering.ClusterItem;

@IgnoreExtraProperties
public class Courier implements ClusterItem {
    private String id;
    private String name;
    private float color;
    private boolean isOn;
    private int state;
    private double lat;
    private double lng;

    // need for FireBase implementation
    public Courier() {
    }

    public Courier(String id, String name, float color, boolean isOn, int state, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.isOn = isOn;
        this.state = state;
        this.lat = lat;
        this.lng = lng;
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

    public float getColor() {
        return color;
    }

    public void setColor(float color) {
        this.color = color;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    @Exclude
    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }
}
