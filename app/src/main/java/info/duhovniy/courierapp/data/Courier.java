package info.duhovniy.courierapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Courier implements Parcelable {
    private String id;
    private String name;
    private int color;
    private boolean isOn;
    private double lat;
    private double lng;
    private int state;

    public Courier(String id, String name, int color, boolean isOn, double lat, double lng, int state) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.isOn = isOn;
        this.lat = lat;
        this.lng = lng;
        this.state = state;
    }

    protected Courier(Parcel in) {
        id = in.readString();
        name = in.readString();
        color = in.readInt();
        isOn = in.readByte() != 0;
        lat = in.readDouble();
        lng = in.readDouble();
        state = in.readInt();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeInt(color);
        parcel.writeByte((byte) (isOn ? 1 : 0));
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeInt(state);
    }
}
