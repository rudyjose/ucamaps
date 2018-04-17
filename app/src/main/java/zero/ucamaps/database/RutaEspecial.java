package zero.ucamaps.database;

import java.io.Serializable;
import java.util.List;

import zero.ucamaps.beans.MapPoint;

/**
 * Created by alf on 18/05/2016.
 */
public class RutaEspecial implements Serializable {

    /*
           Atributos
            */
    private String idRutaEspecial;
    private String nombre;
    private String descripcion;
    private String imagen;
    private String puntos;

    public RutaEspecial() {
    }

    public RutaEspecial(String nombre,List<MapPoint> listaPuntos){
        setIdRutaEspecial("0");
        setNombre(nombre);
        setDescripcion("Ruta Multiple");

        String ruta = "";
        for(int i=0;i<listaPuntos.size();i++){
            ruta += listaPuntos.get(i).getName()+","+listaPuntos.get(i).getStartLatitud()+","+listaPuntos.get(i).getStartLongitud();
            if(!((i+1)==listaPuntos.size())){
                ruta += "/";
            }
        }

        setPuntos(ruta);

    }

    public String getIdRutaEspecial() {
        return idRutaEspecial;
    }

    public void setIdRutaEspecial(String idRutaEspecial) {
        this.idRutaEspecial = idRutaEspecial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPuntos() {
        return puntos;
    }

    public void setPuntos(String puntos) {
        this.puntos = puntos;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
