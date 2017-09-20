package mobilesecurity.mobileone;

import java.io.Serializable;

class Record implements Serializable {
    private String name;
    private String date;
    private int time;
    private double lon;
    private double alt;
    private double lat;

    public Record(String name, String date, int time, double lon, double alt, double lat) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.lon = lon;
        this.alt = alt;
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
