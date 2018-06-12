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
        ((TextView) tituloLabel).setText("Título:");
        ((TextView) notaLabel).setText("Escriba su anotación:");
        ((TextView) tituloBox).setHint("Título de la anotación...");
        ((TextView) notaBox).setHint("Contenido de la anotación...");
        ((Button)btn_guardar).setText("Guardar Anotación");
        btn_guardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TextView) notaBox).getText().toString().isEmpty() ||
                                ((TextView) tituloBox).getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), "No puede haber campos en blanco", Toast.LENGTH_SHORT).show();
                        } else {
                            if (((TextView) tituloBox).getText().toString().length() > 20) {
                                Toast.makeText(getActivity(), "El título es demasiado largo, el maximo es 20 caracteres", Toast.LENGTH_SHORT).show();
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
        getDialog().setTitle("Crear Nueva Anotación");

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
                        //RECUPERO LAS NOTAS
                        List<Nota> listaNotas = recuperar();
                        //BUSCO QUE NO HAYAN TITULOS REPETIDOS
                        boolean banderaTitulo=false;

                        for (int t=0;t< listaNotas.size();t++) {
                            if(listaNotas.get(t).getTitulo().toLowerCase().equals(titulo.toLowerCase())){
                                banderaTitulo=true;
                            }
                        }
                        if(banderaTitulo){
                            Toast.makeText(getActivity(),"Titulo de Anotación Repetida, ingrese otro título",Toast.LENGTH_SHORT).show();
                        }else{
                            Nota nuevaNota= new Nota();
                            nuevaNota.setEdificio(edificio);
                            nuevaNota.setNota(nota);
                            nuevaNota.setTitulo(titulo);
                            listaNotas.add(nuevaNota);
                            fout = new FileOutputStream(file);
                            oos = new ObjectOutputStream(fout);
                            oos.writeObject(listaNotas);
                            Toast.makeText(getActivity(), "Anotacion Guardada Exitosamente", Toast.LENGTH_SHORT).show();
                        }
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

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }
}
