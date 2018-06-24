package zero.ucamaps.database;

import android.app.Activity;
import android.app.FragmentManager;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.MapFragment;
import zero.ucamaps.dialogs.DialogSearchResultPlace;

/**
 *
 */
public class CargaBusquedaEdificio extends AsyncTask<Activity,Void,Context> {

    private volleySingleton volley;
    private RequestQueue requestQueue;
    private List<Edificio> listaEdificio = new ArrayList<>();
    private String nombre;
    private String categoria;
    private Context contexto;

    private DialogSearchResultPlace d = new DialogSearchResultPlace();
    private FragmentManager fm;
    private MapFragment mapFragment;

    public MapFragment getMapFragment() {
        return mapFragment;
    }

    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }




    @Override
    protected Context doInBackground(Activity... activities){
        //asignamos valores al volley y a la queue.
        volley = volleySingleton.getInstance(activities[0].getApplicationContext());
        requestQueue = volley.getRequestQueue();
        contexto = activities[0].getApplicationContext();
        //llamamos a getSitios, donde obtenemos las cosas que necesitamos
        getSitios();
        return contexto;
    }
    @Override
    protected void onPostExecute(Context contexto){
        //relleno
    }

    public void getSitios() {
        // Petición GET
        nombre = nombre.replaceAll("\n","");
        nombre = URLEncoder.encode(nombre);
        categoria = categoria.toLowerCase();
        String url = Constantes.GET_SITIOS + "?busqueda="+nombre+"&categoria="+categoria;
        //creamos un object request, y lo añadimos a la cola
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
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
                    // Obtener array "sitios" Json
                    JSONArray arraySitios = response.getJSONArray("sitios");
                    // Parsear
                    for (int i = 0; i < arraySitios.length(); i++) {
                        //como se obtiene un arreglo, se guarda cada sitio en una lista
                        JSONObject sitio = (JSONObject) arraySitios.get(i);
                        String nombreEdificio = sitio.getString("NOMBRE");
                        String descripcionEdificio = sitio.getString("DESCRIPCION");
                        String imgEdificio = sitio.getString("IMAGEN");
                        String enlace = sitio.getString("ENLACE");

                        //creamos un edificio auxiliar
                        Edificio edificioAux = new Edificio();

                        edificioAux.setNombreEdificio(nombreEdificio);
                        edificioAux.setDescripcionEdificio(descripcionEdificio);
                        edificioAux.setRutaImg(imgEdificio);
                        if(enlace.isEmpty() || enlace.equals(null)){
                            edificioAux.setEnlace("www.uca.edu.sv");
                        }else{
                            edificioAux.setEnlace(enlace);

                        }


                        //y lo añadimos a la lista
                        listaEdificio.add(edificioAux);
                    }


                    d.setListaSitio(listaEdificio);
                    d.setFm(fm);
                   // d.setMapFragment(this.mapFragment);
                    d.show(fm,"Search Results");
                    d.setContexto(contexto);

                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(contexto,"Lo sentimos, "+ mensaje2,Toast.LENGTH_LONG).show();
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;
    }
}

