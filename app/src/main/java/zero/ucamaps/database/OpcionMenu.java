package zero.ucamaps.database;

import java.io.Serializable;

public class OpcionMenu implements Serializable {

    private String tituloOpcion;
    private String pasoDescripcion;
    private String idSource;

    public OpcionMenu(String tituloOpcion, String pasoDescripcion, String idSource) {
        this.tituloOpcion = tituloOpcion;
        this.pasoDescripcion = pasoDescripcion;
        this.idSource = idSource;
    }

    public String getTituloOpcion() {
        return tituloOpcion;
    }

    public void setTituloOpcion(String tituloOpcion) {
        this.tituloOpcion = tituloOpcion;
    }

    public String getPasoDescripcion() {
        return pasoDescripcion;
    }

    public void setPasoDescripcion(String pasoDescripcion) {
        this.pasoDescripcion = pasoDescripcion;
    }

    public String getIdSource() {
        return idSource;
    }

    public void setIdSource(String idSource) {
        this.idSource = idSource;
    }
}
