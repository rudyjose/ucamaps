package zero.ucamaps.database;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zero.ucamaps.dialogs.DialogSpecialRoutes;

/**
 * Created by alf on 24/05/2016.
 */

public class InsertAsinc extends AsyncTask<Activity,Void,Context> {
    private static final String TAG = InsertAsinc.class.getSimpleName();
    private volleySingleton volley;
    private RequestQueue requestQueue;
    private List<RutaEspecial> listaRutas = new ArrayList<RutaEspecial>();
    private Context contexto ;

    //Insert quemado solo para probar si funciona el insert a la base

    @Override
    protected Context doInBackground(Activity... activities){

        volley = volleySingleton.getInstance(activities[0].getApplicationContext());
        requestQueue = volley.getRequestQueue();
        contexto = activities[0].getApplicationContext();
        insertarRuta();
        return  contexto;
    }

    @Override
    protected void onPostExecute(Context contexto){

    }


    public void insertarRuta(){
        //se crea un mapa donde se guardaran los elementos
        HashMap<String ,String> mapa = new HashMap<>();
        //se guardan los elementos en el mapa
        mapa.put("nombre","ruta2");
        mapa.put("descripcion","algo algo");
        mapa.put("puntos","punto1,punto2,punto3");
        //y se crea un objeto JSON, basado en el mapa
        JSONObject jsOb = new JSONObject(mapa);
        //luego hacemos un request de insercion
        Log.d("JSON", jsOb.toString());
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.POST,Constantes.INSERT,jsOb,new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //cuando obtenemos una respuesta, la procesamos
                                procesarRespuesta(response);
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            Toast.makeText(contexto,"Se produjo un error: "+ error,Toast.LENGTH_LONG).show();
                            }
                        }
                );
        addtoQueue(request);

    }

    private void procesarRespuesta(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            contexto,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            contexto,
                            mensaje,
                            Toast.LENGTH_LONG).show();
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


