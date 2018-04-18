package zero.ucamaps.dialogs;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import zero.ucamaps.R;
import zero.ucamaps.database.RutaEspecial;

public class FavoritesAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> items;
    List<RutaEspecial> listaRutas;

    public FavoritesAdapter(Context context, ArrayList<String> items, List<RutaEspecial> listaRutas) {
        this.context = context;
        this.items = items;
        this.listaRutas=listaRutas;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(items.get(position));
    }

    static class ViewHolder {
        TextView txtName;
        ImageView delete;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.row_item_favorite_route, null);

            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);

            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(items.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Elimina el elemento de las listas
                items.remove(position);
                listaRutas.remove(position);
                //abre el archivo de rutas favoritas
                try {
                    File tarjeta = Environment.getExternalStorageDirectory();
                    File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
                    dir.mkdirs();
                    File file = new File(dir.getAbsolutePath(),"favorites_routes");
                    //Sobreescribe el archivo con la lista actualizada
                    ObjectInputStream objectinputstream = null;
                    ObjectOutputStream oos = null;
                    FileOutputStream fout = null;
                    try {
                        FileInputStream streamIn = new FileInputStream(file);
                        objectinputstream = new ObjectInputStream(streamIn);
                        fout = new FileOutputStream(file);
                        oos = new ObjectOutputStream(fout);
                        oos.writeObject(listaRutas);
                        Toast.makeText(v.getContext(), "Ruta Eliminada Exitosamente", Toast.LENGTH_SHORT).show();
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
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

}

