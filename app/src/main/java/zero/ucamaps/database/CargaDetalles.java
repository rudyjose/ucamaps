package zero.ucamaps.database;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.MainActivity;
import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.dialogs.DialogInfoPlaces;
import zero.ucamaps.dialogs.DialogSpecialRoutes;
import zero.ucamaps.dialogs.ProgressDialogFragment;

/**
 * Created by alf on 01/06/2016.
 */
public class CargaDetalles extends AsyncTask<Activity,Void,Context> {
    private static final String TAG_INFO_SEARCH_PROGRESS_DIALOG = "TAG_INFO_SEARCH_PROGRESS_DIALOG";
    private static final int REQUEST_CODE_PROGRESS_DIALOG = 1;
    private Exception mException;
    private ProgressDialog mProgressDialog;
    private volleySingleton volley;
    private RequestQueue requestQueue;
    public DialogFragment dip;
    public String nombreEdificio;
    public FragmentManager fm;
    public Activity act;
    public DetalleEdificio detalle = new DetalleEdificio();
    public Context contexto;

    public CargaDetalles(ProgressDialog progress, Activity act) {
        this.mProgressDialog = progress;
        this.act = act;
    }



    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

        // set the target fragment to receive cancel notification


    }

    @Override
    protected Context doInBackground(Activity... activities) {
        //asignamos valores al volley y a la queue.
        mException=null;
        volley = volleySingleton.getInstance(activities[0].getApplicationContext());
        requestQueue = volley.getRequestQueue();

            selectEdificio();


        contexto = activities[0].getApplicationContext();
        return contexto;
    }

    @Override
    protected void onPostExecute(Context contexto) {
        //relleno




        if (mException != null) {
            Log.w("DETALLES:","Falló recuperar info de la base:");
            mException.printStackTrace();
            //Toast.makeText(,"Falló Recuperación de Información Edificio",Toast.LENGTH_LONG).show();
            return;
        }
        mProgressDialog.dismiss();
    }

    public void selectEdificio() {
        // Petición GET
        String paramEncode = null;
            paramEncode = nombreEdificio.replaceAll("\n","");
            paramEncode = URLEncoder.encode(paramEncode);

        String url = Constantes.GET_BY_NOMBRE + "?nombre=" + paramEncode;
        Log.d("Esta es la URL",url);
        //creamos un object request, y lo añadimos a la cola
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //cuando obtenemos una respuesta, la procesamos
                        procesarRespuesta(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(contexto,"Se produjo un error: "+ error,Toast.LENGTH_LONG).show();
                    }
                });
        addtoQueue(request);
    }

    private void procesarRespuesta(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado) {
                case "1": // EXITO
                    Log.d("Dentro de C Detalles","Exito" );
                    // Obtener detalles del edificio
                    JSONObject obj = response.getJSONObject("detalles");
                    Log.d("El JSON: ",obj.toString());
                    String idEdificio = obj.getString("idEDIFICIO");
                    String nombre = obj.getString("NOMBRE");
                    String descripcion = obj.getString("DESCRIPCION");
                    String codigo = obj.getString("CODIGO");
                    String imagen = obj.getString("IMAGEN");
                    String enlace = obj.getString("ENLACE");
                    if(!enlace.isEmpty() || !enlace.equals(null)){
                        detalle.setEnlace(enlace);
                    }
                    detalle.setNombre(nombre);
                    detalle.setIdEdificio(idEdificio);
                    detalle.setCodigo(codigo);
                    detalle.setDescripcion(descripcion);
                    //Hacemos un segundo request, para obtener la imagen
                    Log.d("Dentro de C Detalles", "voy a sacar la imagen");
                    obtenerImagen(imagen);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(contexto,"Se produjo un error: "+ mensaje2,Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


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
                        detalle.setImagen(bitmap);
                        //una vez tengamos la data, creamos un dialogo de info, y le pasamos la data
                        dip = DialogInfoPlaces.newInstance(detalle);
                        dip.show(fm, "Nombre");
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Bitmap bitFalso = BitmapFactory.decodeResource(contexto.getResources(),R.drawable.parking);
                        detalle.setImagen(bitFalso);
                        //En caso no haya una imagen, ponemos una imagen falsa
                        dip = DialogInfoPlaces.newInstance(detalle);
                        dip.show(fm, "Nombre");
                    }
                });
        addtoQueue(request);
    }

    public String getNombreEdificio() {
        return nombreEdificio;
    }

    public void setNombreEdificio(String nombreEdificio) {
        this.nombreEdificio = nombreEdificio;
    }
}