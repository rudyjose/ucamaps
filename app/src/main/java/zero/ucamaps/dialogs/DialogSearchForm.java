package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
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

import org.w3c.dom.Text;

import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.database.CargaBusqueda;

/**
 * Created by alf on 12/06/2016.
 */
public class DialogSearchForm extends DialogFragment {



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
        final View vista = inflater.inflate(R.layout.search_form, container, false);
        View titulo_busqueda = vista.findViewById(R.id.titulo_formulario);
        View pregunta = vista.findViewById(R.id.pregunta_formulario);
        View boton1 = vista.findViewById(R.id.rbSitio);
        View boton2 = vista.findViewById(R.id.rbPersonal);
        View boton3 = vista.findViewById(R.id.rbEdificio);
        View busqueda = vista.findViewById(R.id.txt_ingrese);
        final View txtbox = vista.findViewById(R.id.box_ingrese);
        View boton = vista.findViewById(R.id.btn_buscar);
        final RadioGroup grupo = (RadioGroup) vista.findViewById(R.id.rgBotones);
        //Seteando las cosas del formulario
        ((TextView) titulo_busqueda).setText("Formulario de Busqueda");
        ((TextView)pregunta).setText("¿Que desea buscar?");
        ((RadioButton)boton1).setText("Depto,Unidad,Aula,etc.");
        ((RadioButton)boton2).setText("Personal");
        ((RadioButton)boton3).setText("Edificio");
        ((TextView)busqueda).setText("Ingrese el nombre de lo que busca:");
        ((TextView)txtbox).setHint("...");
        ((Button)boton).setText("Buscar");
        boton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TextView) txtbox).getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "Ingrese un texto para la busqueda", Toast.LENGTH_SHORT).show();
                        } else {
                            int id = grupo.getCheckedRadioButtonId();
                            RadioButton valBoton = (RadioButton) vista.findViewById(id);
                            CargaBusqueda cb = new CargaBusqueda();
                            String busqueda = ((TextView) txtbox).getText().toString();
                            if (busqueda.equals("Plaza Central") ||
                                    busqueda.equals("plaza central") ||
                                    busqueda.equals("plaza") ||
                                    busqueda.equals("Plaza")) {
                                cb.setNombre("Plaza los Martires");
                            }else{
                                cb.setNombre(busqueda);
                            }

                            if (valBoton.getText().toString().equals("Depto,Unidad,Aula,etc.")) {
                                cb.setCategoria("sitio");
                            } else {
                                cb.setCategoria(valBoton.getText().toString());
                            }

                            cb.setMapFragment(mapFragment1);
                            cb.setFm(getFragmentManager());
                            cb.execute(getActivity());

                            vista.setVisibility(vista.GONE);
                            dismiss();
                        }
                    }
                }
        );

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return vista;
    }



}

