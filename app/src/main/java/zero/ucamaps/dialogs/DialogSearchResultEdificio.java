package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.database.Edificio;
import zero.ucamaps.database.Sitio;

/**
 *
 */
public class DialogSearchResultEdificio extends DialogFragment{
    private List<Edificio> listaEdificio = new ArrayList<>();
    private Context contexto;



    private MapFragment mapFragment;

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }
/*

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listaEdificio.add(new Edificio("BIBLIOTECA","EDIFICIO PARA ESTUDIAR Y PRESTAR LIBROS","/ucamaps/imagenes/biblio.png"));




        String[] listaEdificioString = new String[listaEdificio.size()];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        for(int i = 0;i < listaEdificio.size() ;i++){
            listaEdificioString[i] =  listaEdificio.get(i).getNombreEdificio();
        }
        String titulo = listaEdificio.size()+" Edificios Encontrados";

        if(listaEdificio.size()==1){
            builder.setView(inflater.inflate(R.layout.edificio_search_result, null))
                    .setTitle(titulo);

        }



        return builder.create();


    }
*/
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
      //  listaEdificio.add(new Edificio("BIBLIOTECA","EDIFICIO PARA ESTUDIAR Y PRESTAR LIBROS","/ucamaps/imagenes/biblio.png"));

        final View vista = inflater.inflate(R.layout.edificio_search_result, container, false);
        View titulo_busqueda = vista.findViewById(R.id.nombreEdificio);
        View descripcion = vista.findViewById(R.id.description);
        //FALTA SETEAR IMG


        //Seteando vista
        ((TextView) titulo_busqueda).setText(listaEdificio.get(0).getNombreEdificio().toString());
        ((TextView)descripcion).setText(listaEdificio.get(0).getDescripcionEdificio().toString());




        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return vista;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public void setListaEdificio(List<Edificio> listaEdificio) {
        this.listaEdificio = listaEdificio;
    }
}
