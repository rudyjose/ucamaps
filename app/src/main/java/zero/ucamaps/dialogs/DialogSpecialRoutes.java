package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.R;
import zero.ucamaps.database.RutaEspecial;
import zero.ucamaps.location.RoutingDialogFragment;

/**
 * Created by alf on 14/05/2016.
 */
public class DialogSpecialRoutes extends DialogFragment{

    private List<RutaEspecial> listaRutas = new ArrayList<RutaEspecial>();
    private static final String TAG = DialogSpecialRoutes.class.getSimpleName();
    private RoutingDialogFragment.RoutingDialogListener mRoutingDialogListener;

    public void setRoutingDialogListener(RoutingDialogFragment.RoutingDialogListener dialogListener) {
        this.mRoutingDialogListener = dialogListener;
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Leer rutas favoritas para ponerlas en la lista


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        Log.d(TAG,"esto lleva la lista " + listaRutas);
        if(listaRutas.size()< 1){
            Toast.makeText(
                    getActivity(),
                    "Ocurrió un error, intente de nuevo mas tarde",
                    Toast.LENGTH_LONG).show();
            getActivity().finish();
        }else {
            String[] listaRutasString = new String[listaRutas.size()];


            for (int i = 0; i < listaRutas.size(); i++) {
                listaRutasString[i] = listaRutas.get(i).getNombre();
            }

            ListView lv = (ListView) getActivity().findViewById(R.id.lista_favoritos);
            builder.setView(inflater.inflate(R.layout.favorites, null))
                    .setTitle("Rutas Especiales")
                    .setItems(listaRutasString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            RutaEspecial rutaEspecial = listaRutas.get(which);

                            mRoutingDialogListener.onGetRouteMultiple(rutaEspecial,1);

                        }
                    });

        }
            return builder.create();


    }

    public List<RutaEspecial> getListaRutas() {
        return listaRutas;
    }

    public void setListaRutas(List<RutaEspecial> listaRutas) {
        this.listaRutas = listaRutas;
    }
}
