package zero.ucamaps.database;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import zero.ucamaps.dialogs.DialogSpecialRoutes;


/**
 * Created by alf on 23/05/2016.
 */
public class CargaAsinc extends AsyncTask<Activity,Void,Context> {
    private volleySingleton volley;
    private RequestQueue requestQueue;
    private List<RutaEspecial> listaRutas = new ArrayList<>();
    public DialogSpecialRoutes dsr = new DialogSpecialRoutes();
    private Context contexto ;

    @Override
    protected Context  doInBackground(Activity... activities) {

        //asignamos valores al volley y a la queue.
        volley = volleySingleton.getInstance(activities[0].getApplicationContext());
        requestQueue = volley.getRequestQueue();
        contexto = activities[0].getApplicationContext();
        //llamamos a select ruta, la cual se encarga de obtener la lista de rutas, y setearla
        selectRuta();
        return contexto;
    }

    @Override
    protected void onPostExecute(Context contexto){
        //hacemos un toast indicando que ya se termino de cargar la data del servidor
        Toast.makeText(contexto,"Se han cargado las rutas especiales",Toast.LENGTH_SHORT).show();
    }

    public void selectRuta() {
        // Petición GET
        String url = Constantes.GET;
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
                        Toast.makeText(contexto,"Se produjo un error:"+ error,Toast.LENGTH_LONG).show();
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
                    // Obtener array "rutas" Json
                    JSONArray arrayRutas = response.getJSONArray("rutas");
                    // Parsear
                    for (int i = 0; i < arrayRutas.length(); i++) {
                        //como se obtiene un arreglo de rutas, se añade cada ruta a una lista

                        JSONObject ruta = (JSONObject) arrayRutas.get(i);
                        String idRuta = ruta.getString("idRUTAESPECIAL");
                        String nombre = ruta.getString("NOMBRE");
                        String descripcion = ruta.getString("DESCRIPCION");
                        String imagen = ruta.getString("IMAGEN");
                        String puntos = ruta.getString("PUNTOS");
                        //creamos una ruta auxiliar
                        RutaEspecial rutaEspecialAux = new RutaEspecial();
                        //la llenamos
                        rutaEspecialAux.setNombre(nombre);
                        rutaEspecialAux.setImagen(imagen);
                        rutaEspecialAux.setDescripcion(descripcion);
                        rutaEspecialAux.setIdRutaEspecial(idRuta);
                        rutaEspecialAux.setPuntos(puntos);
                        //y la añadimos a la lista global
                        listaRutas.add(rutaEspecialAux);

                    }
                    //una vez salimos del bucle de llenado, le asignamos la lista al Display de rutas especiales
                    dsr.setListaRutas(listaRutas);
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(contexto,"Se produjo un error:"+ mensaje2,Toast.LENGTH_LONG).show();
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


}
