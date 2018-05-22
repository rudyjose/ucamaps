package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import zero.ucamaps.R;
import zero.ucamaps.database.RutaEspecial;
import zero.ucamaps.location.RoutingDialogFragment;
import zero.ucamaps.util.GlobalPoints;

/**
 * Created by alf on 23/04/2016.
 */
public class DialogFavoriteList extends DialogFragment {

    private RoutingDialogFragment.RoutingDialogListener mRoutingDialogListener;

    public void setRoutingDialogListener(RoutingDialogFragment.RoutingDialogListener dialogListener){
        this.mRoutingDialogListener = dialogListener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Leer rutas favoritas para ponerlas en la lista
        List<RutaEspecial> listaRutas = recuperar();

        ArrayList<String> listaRutasString = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        GlobalPoints globalListRoute = (GlobalPoints) getActivity().getApplicationContext();
        globalListRoute.setListaRutas(listaRutas);

        for(int i = 0;i < listaRutas.size() ;i++){
                listaRutasString.add(listaRutas.get(i).getNombre());
         }

        FavoritesAdapter adapter = new FavoritesAdapter(getActivity(), listaRutasString, listaRutas);

        TextView title = new TextView(getActivity());
        title.setTextColor(Color.parseColor("#ffffff") );
        title.setBackgroundColor(Color.parseColor("#2471A3"));
        title.setShadowLayer(0,10,10,Color.parseColor("#C4C4C4"));
        title.setText("Rutas Favoritas");
        title.setTextSize(20);
        title.setPadding(10,5,0,0);
        builder.setView(inflater.inflate(R.layout.favorites, null))
                .setTitle("Rutas Favoritas")
                .setCancelable(true)
                .setIcon(R.drawable.ic_star_black_24dp)
                .setCustomTitle(title)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GlobalPoints globalListRoute = (GlobalPoints) getActivity().getApplicationContext() ;
                    List<RutaEspecial> listaRutas = globalListRoute.getListaRutas();
                    RutaEspecial favorito = listaRutas.get(which);
                    mRoutingDialogListener.onGetRouteMultiple(favorito,0);
                }
            });

        return builder.create();

    }


    public List<RutaEspecial> recuperar(){
        List<RutaEspecial> listaFavoritos = new LinkedList<RutaEspecial>();
        int i = 0;
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
        dir.mkdirs();
        File file = new File(dir.getAbsolutePath(),"favorites_routes");
        ObjectInputStream objectinputstream = null;
        FileInputStream streamIn = null;
        try {
            if(file != null) {
                try {
                    streamIn = new FileInputStream(file);
                    objectinputstream = new ObjectInputStream(streamIn);
                    listaFavoritos = (List<RutaEspecial>) objectinputstream.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (objectinputstream != null) {
                        objectinputstream.close();
                    }
                    if (streamIn != null) {
                        streamIn.close();
                    }
                }
            }
            return listaFavoritos;
        } catch (IOException e) {
            e.printStackTrace();
            }
        return listaFavoritos;
    }
}