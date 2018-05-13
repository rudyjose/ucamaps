package zero.ucamaps.dialogs;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.adapterCarrusel.activityAyuda;
import zero.ucamaps.database.OpcionMenu;

/**
 * Created by SS on 28/04/2018.
 */
public class MenuAdapter extends ArrayAdapter<OpcionMenu> {

    private MapFragment mapFragment;
    private Context cont;
    DialogMenuList dialmenu;
    private List<OpcionMenu> listaMenuOpciones;
    private FragmentManager manager;

    public MenuAdapter(Context contexto, List<OpcionMenu> elementos, MapFragment fragmento, DialogMenuList dialogo){
        super(contexto, 0, elementos);
        cont = contexto;
        mapFragment = fragmento;
        dialmenu = dialogo;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OpcionMenu opcion = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_list, parent, false);
        }
        TextView titulo = (TextView) convertView.findViewById(R.id.tituloOpcionMenu);
        ImageView btn_ver = (ImageView) convertView.findViewById(R.id.btn_ver_ayuda_op);
       // ImageView btn_back = (ImageView) convertView.findViewById(R.id.btn_back_ayuda_op);

        // ver carrusel
        btn_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DEBO REALIZAR LLAMADA A UNA VENTANA QUE ME MUESTRE EL CARRUSEL
                // Context contexto = getActivity().getApplicationContext();
                Intent i = new Intent(cont, activityAyuda.class);
                i.putExtra("opcion",position);

                cont.startActivity(i);

                // Toast.makeText(getContext(),"OPCION NUMERO:"+opcion.getTituloOpcion(),Toast.LENGTH_SHORT).show();
                //dialmenu.dismiss();
            }
        });

        //regresar

        return convertView;
    }



    public List<OpcionMenu> getListaMenuOpciones() {
        return listaMenuOpciones;
    }

    public void setListaMenuOpciones(List<OpcionMenu> listaMenuOpciones) {
        this.listaMenuOpciones = listaMenuOpciones;
    }

    public FragmentManager getManager() {
        return manager;
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }
}
