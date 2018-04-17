package zero.ucamaps.util;

/**
 * Created by francisco herrera on 23/04/2016.
 */

import android.app.Application;

import java.util.List;

import zero.ucamaps.beans.FavoriteRoute;
import zero.ucamaps.beans.MapPoint;
import zero.ucamaps.database.RutaEspecial;

public class GlobalPoints extends Application{

    private List<RutaEspecial> listaRutas;
    private List<MapPoint> listaPuntos;

    public List<MapPoint> getListaPuntos() {
        return listaPuntos;
    }

    public void setListaPuntos(List<MapPoint> listaPuntos) {
        this.listaPuntos = listaPuntos;
    }

    public List<RutaEspecial> getListaRutas() {
        return listaRutas;
    }

    public void setListaRutas(List<RutaEspecial> listaRutas) {
        this.listaRutas = listaRutas;
    }
}
