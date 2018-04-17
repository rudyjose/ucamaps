package zero.ucamaps.database;

import android.graphics.Bitmap;

/**
 * Created by alf on 31/05/2016.
 */
public class DetalleEdificio {

    private String idEdificio;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Bitmap imagen;

    public DetalleEdificio() {
    }

    public String getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(String idEdificio) {
        this.idEdificio = idEdificio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }
}




