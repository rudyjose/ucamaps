package zero.ucamaps.beans;

/**
 * Created by Uguizu on 21/5/2016.
 */
public class MapPoint {
    private double startLongitud;
    private double startLatitud;
    private String name;
    private int order;

    public MapPoint(){

    }

    public MapPoint(String name, double lat, double lon){
        setName(name);
        setStartLatitud(lat);
        setStartLongitud(lon);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStartLongitud() {
        return startLongitud;
    }

    public void setStartLongitud(double startLongitud) {
        this.startLongitud = startLongitud;
    }

    public double getStartLatitud() {
        return startLatitud;
    }

    public void setStartLatitud(double startLatitud) {
        this.startLatitud = startLatitud;
    }
}
