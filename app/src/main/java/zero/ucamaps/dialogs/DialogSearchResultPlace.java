package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.database.Constantes;
import zero.ucamaps.database.DetalleEdificio;
import zero.ucamaps.database.Edificio;
import zero.ucamaps.database.Sitio;
import zero.ucamaps.database.volleySingleton;


public class DialogSearchResultPlace extends DialogFragment{
    private List<Edificio> listaSitio = new ArrayList<>();
    private FragmentManager fm;
    //private MapFragment mapFragment;
    private DialogFragment dip;
    private Context contexto;
    private volleySingleton volley;
    private RequestQueue requestQueue;
    private DetalleEdificio de;

    public FragmentManager getFm() {
        return fm;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        volley = volleySingleton.getInstance(getActivity());
        requestQueue = volley.getRequestQueue();
        //Leer rutas favoritas para ponerlas en la lista
        final String[] listaSitioString = new String[listaSitio.size()];
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        for(int i = 0;i < listaSitio.size() ;i++){
            listaSitioString[i] =  listaSitio.get(i).getNombreEdificio();
        }
        String titulo = listaSitio.size()+" Coincidencias Encontradas";


        if(listaSitio.size()==1){
            de = new DetalleEdificio();

            de.setNombre(listaSitio.get(0).getNombreEdificio());
            de.setDescripcion(listaSitio.get(0).getDescripcionEdificio());
            de.setEnlace(listaSitio.get(0).getEnlace());
            obtenerImagen(listaSitio.get(0).getRutaImg());


        }else {
            builder.setView(inflater.inflate(R.layout.favorites, null))
                    .setTitle(titulo)
                    .setItems(listaSitioString, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            de = new DetalleEdificio();
                            de.setNombre(listaSitio.get(which).getNombreEdificio());
                            de.setDescripcion(listaSitio.get(which).getDescripcionEdificio());
                            de.setEnlace(listaSitio.get(0).getEnlace());
                            obtenerImagen(listaSitio.get(which).getRutaImg());



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


    public void addtoQueue(Request request) {
        if (request != null) {
            request.setTag(this);
            if (requestQueue == null)
                requestQueue = volley.getRequestQueue();
            request.setRetryPolicy(new DefaultRetryPolicy(
                    600000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            requestQueue.add(request);
        }
    }

    public void obtenerImagen(String ruta){
        String urlImagen = Constantes.BASE + ruta;
        ImageRequest request = new ImageRequest(
                urlImagen,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        de.setImagen(bitmap);

                        dip = DialogInfoPlaces.newInstance(de);
                        dip.show(fm, "Nombre");

                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Bitmap bitFalso = BitmapFactory.decodeResource(contexto.getResources(),R.drawable.parking);
                        de.setImagen(bitFalso);
                        dip = DialogInfoPlaces.newInstance(de);
                        dip.show(fm, "Nombre");
                    }
                });
        addtoQueue(request);
    }
}
