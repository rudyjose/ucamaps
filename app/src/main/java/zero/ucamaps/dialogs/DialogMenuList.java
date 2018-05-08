package zero.ucamaps.dialogs;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.MapFragment;
import zero.ucamaps.database.OpcionMenu;

/**

 */
public class DialogMenuList extends DialogFragment{
    Context contexto;
    MapFragment fragmento;
    private FragmentManager manager;


    @Override
    public void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        List<OpcionMenu> listaOpcionesMenu= new ArrayList<OpcionMenu>();

        listaOpcionesMenu.add(new OpcionMenu("Cambiando colores Mapa","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Trazando Rutas [Modo Edición]","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Buscando lugares [barra busqueda]","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Busqueda avanzada de lugares","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Obteniendo rutas con mi ubicación","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Uso de Lupa","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Realizando Anotaciones","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Consultando anotaciones","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Guardando Rutas favoritas","instrucciones","ruta"));
        listaOpcionesMenu.add(new OpcionMenu("Consultando Rutas favoritas","instrucciones","ruta"));

        final MenuAdapter adaptador = new MenuAdapter(contexto,listaOpcionesMenu,fragmento,this);
        ListView lista = new ListView(getActivity().getApplicationContext());
        adaptador.setListaMenuOpciones(listaOpcionesMenu);
        adaptador.setManager(manager);
        lista.setAdapter(adaptador);
        getDialog().setTitle("Menu Ayuda");
        return  lista;


    }




    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }


    public MapFragment getFragmento() {
        return fragmento;
    }

    public void setFragmento(MapFragment fragmento) {
        this.fragmento = fragmento;
    }

    public FragmentManager getManager() {
        return manager;
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }
}
