package info.duhovniy.courierapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Courier implements Parcelable {
    private int id;
    private String name;
    private String color;
    private double lat;
    private double lng;

    public Courier(int id, String name, String color, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.lat = lat;
        this.lng = lng;
    }

    protected Courier(Parcel in) {
        id = in.readInt();
        name = in.readString();
        color = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Courier> CREATOR = new Creator<Courier>() {
        @Override
        public Courier createFromParcel(Parcel in) {
            return new Courier(in);
        }

        @Override
        public Courier[] newArray(int size) {
            return new Courier[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(color);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }
}
