package zero.ucamaps.database;

/**
 * Created by alf on 24/05/2016.
 */
public class Constantes {

    private static final String PUERTO_HOST = "80";

    /**
     * Dirección IP del servidor
     */
    private static final String IP = "http://sql10.freesqldatabase.com:";
    /**
     * URLs del Web Service
     */
    public static final String BASE = IP + PUERTO_HOST;
    public static final String GET = IP + PUERTO_HOST + "/sql10229217/get_rutas.php";
    public static final String INSERT = IP + PUERTO_HOST + "/sql10229217/insert_ruta.php";
    public static final String GET_BY_NOMBRE = IP + PUERTO_HOST + "/sql10229217/get_detalle.php";
    public static final String GET_SITIOS = IP + PUERTO_HOST + "/sql10229217/get_sitios.php";
    public static final String GET_SITIOS2 = IP + PUERTO_HOST + "/sql10229217/get_sitiosall.php";
    public static final String GET_RUTA_BY_ID = IP + PUERTO_HOST + "/sql10229217/get_detalle_ruta.php";

}
