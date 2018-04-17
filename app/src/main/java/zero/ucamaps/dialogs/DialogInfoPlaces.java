package zero.ucamaps.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import zero.ucamaps.DrawerItem;
import zero.ucamaps.InfoActivity;
import zero.ucamaps.R;
import zero.ucamaps.database.DetalleEdificio;

/**
 * Created by francisco herrera on 15/05/2016.
 */
public class DialogInfoPlaces extends DialogFragment implements View.OnClickListener{
    private static DetalleEdificio detalles = new DetalleEdificio();


    public static DialogInfoPlaces newInstance(DetalleEdificio info) {
        DialogInfoPlaces f = new DialogInfoPlaces();
        detalles = info;
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("titulo", detalles.getNombre());
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        //Creo las VIEWS
        View vista = inflater.inflate(R.layout.dialog_info_places, container, false);
        View titulo_edificio = vista.findViewById(R.id.titulo_edificio);
        View info_edificio = vista.findViewById(R.id.info_edificio);
        //Set VIEWS
        ImageView imagen_edificio = (ImageView) vista.findViewById(R.id.foto_edificio);
        ((TextView)titulo_edificio).setText(detalles.getNombre());
        String desc_corta = detalles.getDescripcion();
        if(desc_corta.length()>=100){
            desc_corta = desc_corta.substring(0,97) + "...";
        }
        ((TextView)info_edificio).setText(desc_corta);
        imagen_edificio.setImageBitmap(Bitmap.createScaledBitmap(detalles.getImagen(), 250, 200, false));

        View ver_mas = vista.findViewById(R.id.see_more_button);
        ((Button) ver_mas).setOnClickListener(this);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return vista;
    }

    @Override
    public void onClick(View v){
        Context contexto = getActivity().getApplicationContext();
        Intent i= new Intent(contexto,InfoActivity.class);
        i.putExtra("nombre_edificio",detalles.getNombre());
        i.putExtra("descripcion_edificio", detalles.getDescripcion());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        detalles.getImagen().compress(Bitmap.CompressFormat.PNG,50,baos);
        i.putExtra("imagen",baos.toByteArray());
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            startActivity(i);
        }
        catch(Exception ex){
            String error = ex.getMessage();

        }

    }

    public DetalleEdificio getInfoEdificio() {
        return detalles;
    }

    public void setInfoEdificio(DetalleEdificio infoEdificio) {
        this.detalles = infoEdificio;
    }
}
