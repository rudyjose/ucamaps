package zero.ucamaps.database;



import android.content.Context;

//import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;



/**
 * Created by alf on 15/05/2016.
 */
public final class volleySingleton {

    private static volleySingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;

    private volleySingleton(Context context) {
        volleySingleton.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized volleySingleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new volleySingleton(context.getApplicationContext());
        }
        return singleton;
    }
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

}
