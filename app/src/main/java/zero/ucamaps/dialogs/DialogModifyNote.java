package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import zero.ucamaps.R;
import zero.ucamaps.database.Nota;

/**
 * Created by alf on 01/07/2016.
 */
public class DialogModifyNote extends DialogFragment {
    private Nota notaext = new Nota();
    private Context contexto;
    private List<Nota> listaSinMod = new LinkedList<>();
    private int posicion;


    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View vista = inflater.inflate(R.layout.new_note, container, false);
        View tituloLabel = vista.findViewById(R.id.txt_titulo);
        final View tituloBox = vista.findViewById(R.id.box_titulo);
        View notaLabel = vista.findViewById(R.id.txt_nota);
        final View notaBox = vista.findViewById(R.id.box_nota);
        View btn_guardar = vista.findViewById(R.id.btn_guardar_nota);
        ((TextView) tituloLabel).setText("Titulo:");
        ((TextView) notaLabel).setText("Escriba su anotacion:");
        ((TextView) tituloBox).setText(notaext.getTitulo());
        ((TextView) notaBox).setText(notaext.getNota());
        ((Button)btn_guardar).setText("Guardar Anotacion");
        btn_guardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TextView) notaBox).getText().toString().isEmpty() ||
                                ((TextView) tituloBox).getText().toString().isEmpty())
                            {
                            Toast.makeText(getActivity(), "No puede haber campos en blanco", Toast.LENGTH_SHORT).show();
                        } else {
                            if (((TextView) tituloBox).getText().toString().length() > 20) {
                                Toast.makeText(getActivity(), "El titulo es demasiado largo, el maximo es 20 caracteres", Toast.LENGTH_SHORT).show();
                            }else {
                            String titulo = ((TextView) tituloBox).getText().toString();
                            String nota = ((TextView) notaBox).getText().toString();
                            modificarNota(posicion,titulo,nota,notaext.getEdificio());
                            vista.setVisibility(vista.GONE);
                            dismiss();
                        }}
                    }
                }
        );
        getDialog().setTitle("Modificar Anotacion");

        return vista;
    }

    public void modificarNota(int elemento,String titulo,String nota,String edificio){
        List<Nota> auxiliar = new LinkedList<>();

        for(int i=0;i<listaSinMod.size();i++){
            if(i==elemento){
                Nota notaMod = new Nota();
                notaMod.setNota(nota);
                notaMod.setTitulo(titulo);
                notaMod.setEdificio(edificio);
                auxiliar.add(notaMod);
            }else {
                auxiliar.add(listaSinMod.get(i));
            }
        }

        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
        dir.mkdirs();
        File file = new File(dir.getAbsolutePath(),"reminders");
        try {
            FileOutputStream fOut = null;
            ObjectOutputStream oos = null;
            try {
                fOut = new FileOutputStream(file);
                oos = new ObjectOutputStream(fOut);
                oos.writeObject(auxiliar);
                Toast.makeText(contexto,"anotacion editada Exitosamente",Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (oos != null) {
                    oos.close();
                }
                if (fOut != null) {
                    oos.close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public List<Nota> getListaSinMod() {
        return listaSinMod;
    }

    public void setListaSinMod(List<Nota> listaSinMod) {
        this.listaSinMod = listaSinMod;
    }

    public Nota getNotaext() {
        return notaext;
    }

    public void setNotaext(Nota notaext) {
        this.notaext = notaext;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
