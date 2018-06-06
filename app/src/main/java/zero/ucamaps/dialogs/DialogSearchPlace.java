package zero.ucamaps.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.database.CargaBusqueda;
import zero.ucamaps.database.CargaBusquedaEdificio;
import zero.ucamaps.database.Edificio;

/**
 *
 */
public class DialogSearchPlace extends DialogFragment {

    private List<Edificio> listaEdificio = new ArrayList<>();

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
        final MapFragment mapFragment1 = this.mapFragment;
        final View vista = inflater.inflate(R.layout.search_form_place, container, false);
        View titulo_busqueda = vista.findViewById(R.id.titulo_formulario);


        View busqueda = vista.findViewById(R.id.txt_ingrese);
        final View txtbox = vista.findViewById(R.id.box_ingrese);
        View boton = vista.findViewById(R.id.btn_buscar);

        //Seteando vista
        ((TextView) titulo_busqueda).setText("Consulta información sobre un Edificio \n");

        ((TextView)busqueda).setText("Nombre del Edificio: \n");
        ((TextView)txtbox).setHint("...");
        ((Button)boton).setText("Buscar");
        boton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TextView) txtbox).getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Ingrese un texto para la busqueda", Toast.LENGTH_SHORT).show();
                        } else {


                            CargaBusquedaEdificio cb = new CargaBusquedaEdificio();
                            String busqueda = ((TextView) txtbox).getText().toString();
                            if (busqueda.equals("Plaza Central") ||
                                    busqueda.equals("plaza central") ||
                                    busqueda.equals("plaza") ||
                                    busqueda.equals("Plaza")) {
                                cb.setNombre("Plaza los Martires");
                            }else{
                                cb.setNombre(busqueda);
                            }

                            cb.setCategoria("edificio");        //buscara en la tabla edificio
                            cb.setMapFragment(mapFragment1);
                            cb.setFm(getFragmentManager());
                            cb.execute(getActivity());






                            vista.setVisibility(vista.GONE);
                            dismiss();
                        }
                    }
                });


        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return vista;
    }



}

