package zero.ucamaps.dialogs;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import zero.ucamaps.MapFragment;
import zero.ucamaps.R;
import zero.ucamaps.adapterCarrusel.activityAyuda;
import zero.ucamaps.database.OpcionMenu;
import zero.ucamaps.database.RutaEspecial;

/**
 * Created by SS on 28/04/2018.
 */
public class MenuAdapter2 extends BaseAdapter {

    Context context;
    ArrayList<String> items;
    List<OpcionMenu> listaOpcionMenu;

    public MenuAdapter2(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;

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
        ImageView ver;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.menu_list, null);

            holder.txtName = (TextView) convertView.findViewById(R.id.tituloOpcionMenu);
            holder.ver = (ImageView) convertView.findViewById(R.id.btn_ver_ayuda_op);
            convertView.setTag(holder);
        } else {
            holder = (MenuAdapter2.ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(items.get(position));
        holder.ver.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //DEBO REALIZAR LLAMADA A UNA VENTANA QUE ME MUESTRE EL CARRUSEL
                // Context contexto = getActivity().getApplicationContext();
                Intent i = new Intent(context, activityAyuda.class);
                i.putExtra("opcion",position);

                context.startActivity(i);

                // Toast.makeText(getContext(),"OPCION NUMERO:"+opcion.getTituloOpcion(),Toast.LENGTH_SHORT).show();
                //dialmenu.dismiss();
            }
        });

        return convertView;
    }

}



