package zero.ucamaps.database;

public class Edificio {

    private String nombreEdificio;
    private String descripcionEdificio;
    private String rutaImg;


    public Edificio(){

    }

    public Edificio(String nombreEdificio, String descripcionEdificio, String rutaImg) {
        this.nombreEdificio = nombreEdificio;
        this.descripcionEdificio = descripcionEdificio;
        this.rutaImg = rutaImg;
    }

    public String getNombreEdificio() {
        return nombreEdificio;
    }

    public void setNombreEdificio(String nombreEdificio) {
        this.nombreEdificio = nombreEdificio;
    }

    public String getDescripcionEdificio() {
        return descripcionEdificio;
    }

    public void setDescripcionEdificio(String descripcionEdificio) {
        this.descripcionEdificio = descripcionEdificio;
    }

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {
        this.rutaImg = rutaImg;
    }

}
