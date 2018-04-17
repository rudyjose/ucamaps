package zero.ucamaps.dialogs;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import zero.ucamaps.MainActivity;
import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.database.Nota;

/**
 * Created by alf on 29/06/2016.
 */
public class NoteAdapter extends ArrayAdapter<Nota> {
    private MapFragment mapFragment;
    private Context cont;
    DialogNotesList dial;
    private List<Nota> listaNotas;
    private FragmentManager manager;

    public NoteAdapter(Context contexto,List<Nota> elementos,MapFragment fragmento,DialogNotesList dialogo){
        super(contexto, 0, elementos);
        cont = contexto;
        mapFragment = fragmento;
        dial = dialogo;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Nota nota = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_list, parent, false);
        }
        TextView titulo = (TextView) convertView.findViewById(R.id.titulo);
        final TextView edificio = (TextView) convertView.findViewById(R.id.edificio);
        ImageView btn_editar = (ImageView) convertView.findViewById(R.id.btn_editar);
        ImageView btn_eliminar = (ImageView) convertView.findViewById(R.id.btn_eliminar);
        //modificar la anotacion
        btn_editar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogModifyNote dmn = new DialogModifyNote();
                        dmn.setNotaext(nota);
                        dmn.setContexto(cont);
                        dmn.setListaSinMod(listaNotas);
                        dmn.setPosicion(position);
                        dmn.show(manager,"DialogModify");
                        dial.dismiss();
                    }
                });
        //Eliminar la anotacion
        btn_eliminar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder build = new AlertDialog.Builder(cont);
                        build.setTitle("Alerta");
                        build.setMessage("Esta a punto de borrar una anotacion, ¿Está seguro?");
                        build.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eliminarNota(position);
                                dial.dismiss();
                            }
                        });
                        build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        build.setIcon(android.R.drawable.ic_dialog_alert);
                        AlertDialog alerta = build.create();
                                alerta.show();
                    }
                }
        );

        titulo.setText(nota.getTitulo());
        titulo.setTextColor(Color.BLACK);
        edificio.setText(nota.getEdificio());

        //Ver detalle de la nota, asi como crear la ruta o marcar el edificio
        convertView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder build = new AlertDialog.Builder(cont);
                            build.setTitle(nota.getTitulo()+" - "+nota.getEdificio());
                            build.setPositiveButton("Trazar ruta", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Sitio sitioEscogido = listaSitio.get(which);
                                    mapFragment.onGetRouteMarked(getContext().getString(R.string.my_location), nota.getEdificio());
                                    Toast.makeText(getContext(), "Ruta trazada, ¡a por el!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    dial.dismiss();
                                }

                            ;
                        });
                        build.setNegativeButton("Marcar Edificio", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mapFragment.onAdvanceSearchLocate(nota.getEdificio());
                                Toast.makeText(getContext(), "Objetico Marcado", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                dial.dismiss();
                            }

                            ;
                        });

                        build.setMessage(nota.getNota());
                        AlertDialog detalle = build.create();
                        detalle.show();

                    }
                }
        );


        return convertView;
    }

    public void eliminarNota(int elemento){
        List<Nota> listaActual;
        listaActual = listaNotas;
        listaActual.remove(elemento);
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
        dir.mkdirs();
        File file = new File(dir.getAbsolutePath(),"reminders");
        //verificamos si el archivo existe
        try {
            FileOutputStream fOut = null;
            ObjectOutputStream oos = null;
            try {
                fOut = new FileOutputStream(file);
                oos = new ObjectOutputStream(fOut);
                oos.writeObject(listaActual);
                Toast.makeText(getContext(),"anotacion eliminada Exitosamente",Toast.LENGTH_SHORT).show();
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




    public List<Nota> getListaNotas() {
        return listaNotas;
    }

    public void setListaNotas(List<Nota> listaNotas) {
        this.listaNotas = listaNotas;
    }

    public FragmentManager getManager() {
        return manager;
    }

    public void setManager(FragmentManager manager) {
        this.manager = manager;
    }
}
