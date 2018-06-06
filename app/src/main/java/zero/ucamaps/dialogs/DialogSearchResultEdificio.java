package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

/**
 *
 */
public class DialogSearchResultEdificio extends DialogFragment{
    private List<Edificio> listaEdificio = new ArrayList<>();
    private Context contexto;
    private String nombre;

    private volleySingleton volley;
    private RequestQueue requestQueue;
    private ImageView img;


    private MapFragment mapFragment;

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        volley = volleySingleton.getInstance(getActivity().getApplicationContext());
        requestQueue = volley.getRequestQueue();

        final View vista = inflater.inflate(R.layout.edificio_search_result, container, false);
        View titulo_busqueda = vista.findViewById(R.id.nombreEdificio);
        View descripcion = vista.findViewById(R.id.description);
        img = (ImageView) vista.findViewById(R.id.imageView);
        //FALTA SETEAR IMG


        //Seteando vista
        ((TextView) titulo_busqueda).setText(listaEdificio.get(0).getNombreEdificio().toString());
        ((TextView)descripcion).setText(listaEdificio.get(0).getDescripcionEdificio().toString());
        obtenerImagen(listaEdificio.get(0).getRutaImg(), img);





        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return vista;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public void setListaEdificio(List<Edificio> listaEdificio) {
        this.listaEdificio = listaEdificio;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public void obtenerImagen(String ruta,  final ImageView imagen){

        String urlImagen = Constantes.BASE + ruta;
        ImageRequest request = new ImageRequest(
                urlImagen,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imagen.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("En el get imagen ruta",error.toString());
                        Bitmap bitFalso = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.logouca);
                        imagen.setImageBitmap(bitFalso);

                    }
                });

        addtoQueue(request);
    }

}
