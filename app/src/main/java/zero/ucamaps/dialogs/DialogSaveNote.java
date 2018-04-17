package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import zero.ucamaps.R;
import zero.ucamaps.beans.MapPoint;
import zero.ucamaps.database.Nota;
import zero.ucamaps.database.Nota;

/**
 * Created by alf on 01/07/2016.
 */
public class DialogSaveNote extends DialogFragment{
    private String edificio;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View vista = inflater.inflate(R.layout.new_note, container, false);
        View tituloLabel = vista.findViewById(R.id.txt_titulo);
        final View tituloBox = vista.findViewById(R.id.box_titulo);
        View notaLabel = vista.findViewById(R.id.txt_nota);
        final View notaBox = vista.findViewById(R.id.box_nota);
        View btn_guardar = vista.findViewById(R.id.btn_guardar_nota);
        ((TextView) tituloLabel).setText("Titulo:");
        ((TextView) notaLabel).setText("Escriba su anotacion:");
        ((TextView) tituloBox).setHint("Titulo de la anotacion...");
        ((TextView) notaBox).setHint("Contenido de la anotacion...");
        ((Button)btn_guardar).setText("Guardar Anotacion");
        btn_guardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TextView) notaBox).getText().toString().isEmpty() ||
                                ((TextView) tituloBox).getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "No puede haber campos en blanco", Toast.LENGTH_SHORT).show();
                        } else {
                            if (((TextView) tituloBox).getText().toString().length() > 20) {
                                Toast.makeText(getActivity(), "El titulo es demasiado largo, el maximo es 20 caracteres", Toast.LENGTH_SHORT).show();
                            }else {
                            String titulo = ((TextView) tituloBox).getText().toString();
                            String nota = ((TextView) notaBox).getText().toString();
                            guardarNota(titulo,nota);
                            vista.setVisibility(vista.GONE);
                            dismiss();
                        }}
                    }
                }
        );
        getDialog().setTitle("Crear Nueva Anotacion");

        return vista;

    }

    public void guardarNota(String titulo,String nota){
        try {
            File tarjeta = Environment.getExternalStorageDirectory();
                File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
                dir.mkdirs();
                File file = new File(dir.getAbsolutePath(),"reminders");
                //verificamos si el archivo existe
                if (file.createNewFile()) {
                    //si la condicion da true, es por que el archivo no existia, y se creo, por ende, esta es la primera ruta creada
                    ObjectOutputStream oos = null;
                    List<Nota> listaNotas = new LinkedList<>();
                    Nota nuevaNota= new Nota();
                    nuevaNota.setEdificio(edificio);
                    nuevaNota.setNota(nota);
                    nuevaNota.setTitulo(titulo);
                    listaNotas.add(nuevaNota);
                    FileOutputStream fout = null;
                    try {
                        fout = new FileOutputStream(file);
                        oos = new ObjectOutputStream(fout);
                        oos.writeObject(listaNotas);
                        Toast.makeText(getActivity(), "Anotacion Guardada Exitosamente", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (oos != null) {
                            oos.close();
                        }
                        if (fout != null) {
                            oos.close();
                        }
                    }

                } else {
                    //si la consdicion da false, es por que el archivo ya existe, por ende se usa el filewrite
                    ObjectInputStream objectinputstream = null;
                    ObjectOutputStream oos = null;
                    FileOutputStream fout = null;
                    try {
                        FileInputStream streamIn = new FileInputStream(file);
                        objectinputstream = new ObjectInputStream(streamIn);
                        List<Nota> listaNotas = (List<Nota>) objectinputstream.readObject();
                        Nota nuevaNota= new Nota();
                        nuevaNota.setEdificio(edificio);
                        nuevaNota.setNota(nota);
                        nuevaNota.setTitulo(titulo);
                        listaNotas.add(nuevaNota);
                        fout = new FileOutputStream(file);
                        oos = new ObjectOutputStream(fout);
                        oos.writeObject(listaNotas);
                        Toast.makeText(getActivity(), "Anotacion Guardada Exitosamente", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (objectinputstream != null) {
                            objectinputstream.close();
                        }
                        if (oos != null) {
                            oos.close();
                        }
                        if (fout != null) {
                            fout.close();
                        }
                    }
                }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }
}
