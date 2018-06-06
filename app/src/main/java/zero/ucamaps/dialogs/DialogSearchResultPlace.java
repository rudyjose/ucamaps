package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.database.DetalleEdificio;
import zero.ucamaps.database.Edificio;
import zero.ucamaps.database.Sitio;


public class DialogSearchResultPlace extends DialogFragment{
    private List<Edificio> listaSitio = new ArrayList<>();
    private DialogSearchResultEdificio dsr= new DialogSearchResultEdificio();
    private FragmentManager fm;

    private Context contexto;



    private MapFragment mapFragment;

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        //Leer rutas favoritas para ponerlas en la lista
        String[] listaSitioString = new String[listaSitio.size()];



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        for(int i = 0;i < listaSitio.size() ;i++){
            listaSitioString[i] =  listaSitio.get(i).getNombreEdificio();
        }
        String titulo = listaSitio.size()+" coincidencias encontradas";

        if(listaSitio.size()==1){
            dsr.setListaEdificio(listaSitio);
            dsr.setMapFragment(this.mapFragment);
            dsr.setContexto(contexto);
            dsr.show(getActivity().getFragmentManager(),"Resultado");
        }else {
            builder.setView(inflater.inflate(R.layout.favorites, null))
                    .setTitle(titulo)
                    .setItems(listaSitioString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Edificio sitioEscogido = listaSitio.get(which);
                            List<Edificio> temp = new ArrayList<>();
                            temp.add(sitioEscogido);
                            dsr.setListaEdificio(temp);
                            dsr.setMapFragment(getMapFragment());
                            dsr.setContexto(contexto);
                            dsr.show(getActivity().getFragmentManager(),"Resultado");



                        }
                    });
        }



        return builder.create();


    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public void setListaSitio(List<Edificio> listaSitio) {
        this.listaSitio = listaSitio;
    }
}
