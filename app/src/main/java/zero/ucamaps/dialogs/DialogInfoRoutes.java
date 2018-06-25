package zero.ucamaps.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import zero.ucamaps.InfoActivity;
import zero.ucamaps.R;
import zero.ucamaps.database.getREinfo;

/**
 * Created by alf on 28/06/2016.
 */
public class DialogInfoRoutes extends DialogFragment {
    private String nombreRuta ;
    private String descripcion;
    private Bitmap imagen;
    private int contador;
    private List<String> edificios;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creo las VIEWS
        View vista = inflater.inflate(R.layout.dialog_info_places2, container, false);
        View titulo_edificio = vista.findViewById(R.id.titulo_edificio);
        View info_edificio = vista.findViewById(R.id.info_edificio);
        //Set VIEWS
        ImageView imagen_edificio = (ImageView) vista.findViewById(R.id.foto_edificio);
        ((TextView)titulo_edificio).setText(nombreRuta);
        String desc_corta = descripcion;
        if(desc_corta.length()>=100){
            desc_corta = desc_corta.substring(0,97) + "...";
        }
        ((TextView)info_edificio).setText(desc_corta);
        imagen_edificio.setImageBitmap(Bitmap.createScaledBitmap(imagen, 500, 300, false));

        View ver_mas = vista.findViewById(R.id.see_more_button);
         ver_mas.setOnClickListener(
                 new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Context contexto = getActivity().getApplicationContext();
                         Intent i = new Intent(contexto, InfoActivity.class);
                         i.putExtra("nombre_edificio", nombreRuta);
                         i.putExtra("descripcion_edificio", descripcion);
                         i.putExtra("enlace", "www.uca.edu.sv");
                         ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         imagen.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                         i.putExtra("imagen", baos.toByteArray());
                         //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         try {
                             startActivity(i);
                         } catch (Exception ex) {
                             String error = ex.getMessage();
                             Log.d("error en info route", error);
                         }

                     }
                 }
         );
        View atras = vista.findViewById(R.id.goBackButton);
        atras.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getREinfo getAnterior = new getREinfo();
                        getAnterior.setEdificios(edificios);
                        getAnterior.setContador(contador - 1);
                        getAnterior.fm = getFragmentManager();
                        getAnterior.execute(getActivity());
                        dismiss();
                    }
                }
        );
        if(contador==0){
            atras.setEnabled(false);
            atras.setVisibility(View.INVISIBLE);
        }
        View adelante = vista.findViewById(R.id.goForthButton);
        adelante.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getREinfo getSiguiente = new getREinfo();
                        getSiguiente.setEdificios(edificios);
                        getSiguiente.setContador(contador+1);
                        getSiguiente.fm = getFragmentManager();
                        getSiguiente.execute(getActivity());
                        dismiss();
                        //buscar si hay un metodo que libere la memoria de una vez
                    }
                }
        );
        if(contador==(edificios.size()-1)){
            adelante.setEnabled(false);
            adelante.setVisibility(View.INVISIBLE);
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return vista;
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public List<String> getEdificios() {
        return edificios;
    }

    public void setEdificios(List<String> edificios) {
        this.edificios = edificios;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
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