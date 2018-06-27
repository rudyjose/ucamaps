package zero.ucamaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity2 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String titulo = getIntent().getStringExtra("nombre_edificio");
        TextView titulo_edificio;
        String descripcion = getIntent().getStringExtra("descripcion_edificio");
        TextView descripcion_edificio;
        String enlace = getIntent().getStringExtra("enlace");
        TextView enlaceview;
        Bitmap imagen = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("imagen"), 0, getIntent().getByteArrayExtra("imagen").length);
        ImageView imagen_edificio;

        try{
            titulo_edificio = (TextView) findViewById(R.id.info_titulo);
            titulo_edificio.setText(titulo);
            descripcion_edificio = (TextView) findViewById(R.id.info_descripcion);
            descripcion_edificio.setText(descripcion);
            enlaceview = (TextView)findViewById(R.id.enlace);
            enlaceview.setText(enlace);
            Linkify.addLinks(enlaceview, Linkify.ALL);
            imagen_edificio = (ImageView) findViewById((R.id.info_imagen));
            imagen_edificio.setImageBitmap(imagen);
        }
        catch(Exception ex){
            String error = ex.getMessage();
            error= error;
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);

    }

    @Override
    public android.support.v4.app.FragmentManager getSupportFragmentManager() {
        return null;
    }
}
