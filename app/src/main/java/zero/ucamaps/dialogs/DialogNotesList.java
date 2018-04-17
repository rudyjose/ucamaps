package zero.ucamaps.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import zero.ucamaps.MainActivity;
import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.database.Nota;
import zero.ucamaps.database.RutaEspecial;
import zero.ucamaps.util.GlobalPoints;

/**
 * Created by alf on 29/06/2016.
 */
public class DialogNotesList extends DialogFragment{
    Context contexto;
    MapFragment fragmento;
    private FragmentManager manager;


    @Override
    public void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        List<Nota> listaNotas = recuperar();
        final NoteAdapter adaptador = new NoteAdapter(contexto,listaNotas,fragmento,this);
        ListView lista = new ListView(getActivity().getApplicationContext());
        adaptador.setListaNotas(listaNotas);
        adaptador.setManager(manager);
        lista.setAdapter(adaptador);
        getDialog().setTitle("Anotaciones");
        return  lista;


    }



    public List<Nota> recuperar(){
        List<Nota> listaNota = new LinkedList<Nota>();
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
        dir.mkdirs();
        File file = new File(dir.getAbsolutePath(),"reminders");
        ObjectInputStream objectinputstream = null;
        FileInputStream streamIn = null;
        try {
            if(file != null) {
                try {
                    streamIn = new FileInputStream(file);
                    objectinputstream = new ObjectInputStream(streamIn);
                    listaNota = (List<Nota>) objectinputstream.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (objectinputstream != null) {
                        objectinputstream.close();
                    }
                    if (streamIn != null) {
                        streamIn.close();
                    }
                }
            }
            return listaNota;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaNota;
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
