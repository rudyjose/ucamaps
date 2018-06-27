package zero.ucamaps.database;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import zero.ucamaps.R;
import zero.ucamaps.dialogs.DialogInfoPlaces;
import zero.ucamaps.dialogs.DialogInfoRoutes;

/**
 * Created by alf on 07/07/2016.
 */
public class getREinfo extends AsyncTask<Activity,Void,Context> {
    private volleySingleton volley;
    private RequestQueue requestQueue;
    public FragmentManager fm;
    public DetalleEdificio detalle = new DetalleEdificio();
    private int contador;
    private List<String> edificios;
    private Context contexto;

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Context doInBackground(Activity... activities) {
        //asignamos valores al volley y a la queue.
        volley = volleySingleton.getInstance(activities[0].getApplicationContext());
        requestQueue = volley.getRequestQueue();
        selectEdificio();
        contexto = activities[0].getApplicationContext();
        return contexto;
    }

    @Override
    protected void onPostExecute(Context contexto) {
        //relleno
    }

    public void selectEdificio() {
        // Petición GET
        String paramEncode = null;
        String nombre = null;
        String url = null;
        if(contador==0){
            nombre = edificios.get(0);
        }else{
            nombre = edificios.get(contador);
        }
        paramEncode = nombre.replaceAll("\n", "");
        paramEncode = URLEncoder.encode(paramEncode);

        if(contador==0){
                url = Constantes.GET_RUTA_BY_ID + "?nombre=" + paramEncode;
        }else{

             url = Constantes.GET_BY_NOMBRE + "?nombre=" + paramEncode;
        }


        Log.d("!!!!Esta es la URL", url);
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
                        Toast.makeText(contexto, "Se produjo un error: " + error, Toast.LENGTH_LONG).show();
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
                    String nombre = obj.getString("NOMBRE");
                    String descripcion = obj.getString("DESCRIPCION");
                    String imagen = obj.getString("IMAGEN");

                    detalle.setNombre(nombre);
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
                        DialogInfoRoutes dir = new DialogInfoRoutes();
                        dir.setImagen(detalle.getImagen());
                        dir.setNombreRuta(detalle.getNombre());
                        dir.setDescripcion(detalle.getDescripcion());
                        dir.setContador(contador);
                        dir.setEdificios(edificios);
                        dir.show(fm,"dialogo");
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Bitmap bitFalso = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.parking);
                        detalle.setImagen(bitFalso);
                        //En caso no haya una imagen, ponemos una imagen falsa
                        DialogInfoRoutes dir = new DialogInfoRoutes();
                        dir.setImagen(detalle.getImagen());
                        dir.setNombreRuta(detalle.getNombre());
                        dir.setDescripcion(detalle.getDescripcion());
                        dir.setContador(contador);
                        dir.setEdificios(edificios);
                        dir.show(fm,"dialogo");
                    }
                });
        addtoQueue(request);
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
}