package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import zero.ucamaps.R;
import zero.ucamaps.adapterCarrusel.activityAyuda;
import zero.ucamaps.database.OpcionMenu;
import zero.ucamaps.database.RutaEspecial;
import zero.ucamaps.location.RoutingDialogFragment;
import zero.ucamaps.util.GlobalPoints;

/**
 *
 */
public class DialogMenuList2 extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        ArrayList<String> listaOpcionMenus= new ArrayList<>();
        listaOpcionMenus.add("Cambiando colores Mapa");
        listaOpcionMenus.add("Trazando Rutas [Modo Edición]");
        listaOpcionMenus.add("Buscando lugares [barra busqueda]");
        listaOpcionMenus.add("Busqueda avanzada de lugares");
        listaOpcionMenus.add("Obteniendo rutas con mi ubicación");
        listaOpcionMenus.add("Uso de Lupa");
        listaOpcionMenus.add("Realizando Anotaciones");
        listaOpcionMenus.add("Consultando anotaciones");
        listaOpcionMenus.add("Guardando Rutas favoritas");
        listaOpcionMenus.add("Consultando Rutas favoritas");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        MenuAdapter2 adapter = new MenuAdapter2(getActivity(), listaOpcionMenus);

        builder.setView(inflater.inflate(R.layout.favorites, null))
                .setTitle("Menu Ayuda")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent i = new Intent(getActivity(), activityAyuda.class);
                    i.putExtra("opcion",which);
                    startActivity(i);
                }
            });

        return builder.create();

    }



}