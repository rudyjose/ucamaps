package zero.ucamaps.database;

import java.io.Serializable;

/**
 * Created by alf on 29/06/2016.
 */
public class Nota implements Serializable {

    public String titulo;
    public String nota;
    public String edificio;

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getNota() {
        return nota;
    }

    public String getEdificio() {
        return edificio;
    }
}
