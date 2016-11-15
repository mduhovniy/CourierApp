package info.duhovniy.courierapp.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Position implements Parcelable {

    private long lat;
    private long lng;
    private int state;

    public Position(long lat, long lng, int state) {
        this.lat = lat;
        this.lng = lng;
        this.state = state;
    }

    protected Position(Parcel in) {
        lat = in.readLong();
        lng = in.readLong();
        state = in.readInt();
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
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
        parcel.writeLong(lat);
        parcel.writeLong(lng);
        parcel.writeInt(state);
    }
}
