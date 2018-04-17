package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import zero.ucamaps.R;

/**
 * Created by alf on 01/05/2016.
 */
public class DialogReplaceFavorite extends DialogFragment{

    public String ruta_cambiar;

    @Override


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] listaRutas = {"", "", "", "", "", "", "", "", "" , ""};

        //Leer rutas favoritas para ponerlas en la lista
        listaRutas = recuperar();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


                    builder.setView(inflater.inflate(R.layout.favorites, null))
                    .setTitle("Rutas Favoritas")
                    .setItems(listaRutas, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Reemplazar(which);
                        }
                    });
            return builder.create();
        }




    public String[] recuperar(){
        String nombrearchivo = "favorites_routes";
        String[] lista = {"", "", "", "", "", "", "", "", "" , ""};
        int i = 0;
        File tarjeta = Environment.getExternalStorageDirectory();
        File file = new File(tarjeta.getAbsolutePath(), nombrearchivo);
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader archivo = new InputStreamReader(fIn);
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String todo = "";
            while (linea != null && i<10) {
                lista[i] = linea.split("_")[0];
                i+=1;
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            return lista;

        } catch (IOException e) {
            e.printStackTrace();
        }
        lista = new String[0];
        return lista;
    }


    public void Reemplazar(int indice) {
        Toast.makeText(getActivity(), "empiezo a reemplazar", Toast.LENGTH_SHORT).show();
        String nombrearchivo = "favorites_routes";
        File tarjeta = Environment.getExternalStorageDirectory();
        File file = new File(tarjeta.getAbsolutePath(), nombrearchivo);
        String[] listaRutas = {"", "", "", "", "", "", "", "", "", ""};
        listaRutas = recuperar();
        String quitar = listaRutas[indice];
        try {
            FileReader fr = new FileReader(file);
            String s;
            String totalStr = "";
            try {
                BufferedReader br = new BufferedReader(fr);
                while ((s = br.readLine()) != null) {
                    if (s.contains(quitar)) {
                        Toast.makeText(getActivity(), "encontre la ruta", Toast.LENGTH_SHORT).show();
                        s = ruta_cambiar;
                    }
                    totalStr += s;
                }
                FileWriter fw = new FileWriter(file);
                fw.write(totalStr);
                fw.close();
                Toast.makeText(getActivity(), "ya termine de escribir el nuevo doc", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRuta_cambiar() {
        return ruta_cambiar;
    }

    public void setRuta_cambiar(String ruta_cambiar) {
        this.ruta_cambiar = ruta_cambiar;
    }


}
