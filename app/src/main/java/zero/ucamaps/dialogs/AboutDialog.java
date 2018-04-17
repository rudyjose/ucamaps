package zero.ucamaps.dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import zero.ucamaps.R;

public class AboutDialog extends Dialog{
    private int contador = 0;
    private static Context mContext = null;
    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

/**
 * Creates the template of the Dialog
 */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about);
        TextView tv = (TextView)findViewById(R.id.legal_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.legal)));
        tv = (TextView)findViewById(R.id.info_text);
        tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
        tv.setLinkTextColor(Color.DKGRAY);
        Linkify.addLinks(tv, Linkify.ALL);
        ImageView imagen = (ImageView)findViewById(R.id.logoImage);
        imagen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (contador == 0)
                            Toast.makeText(getContext(), "*tap*", Toast.LENGTH_SHORT).show();
                        contador++;
                        switch (contador) {
                            case 10:
                                Toast.makeText(getContext(), "gg izi, plz nerf", Toast.LENGTH_SHORT).show();
                                break;
                            case 20:
                                Toast.makeText(getContext(), "¿que estas haciendo?", Toast.LENGTH_SHORT).show();
                                break;
                            case 30:
                                Toast.makeText(getContext(), "deja de darle tap al buho", Toast.LENGTH_SHORT).show();
                                break;
                            case 40:
                                Toast.makeText(getContext(), "no va a pasar nada", Toast.LENGTH_SHORT).show();
                                break;
                            case 50:
                                Toast.makeText(getContext(), "de verdad, este es el ultimo easter egg", Toast.LENGTH_SHORT).show();
                                break;
                            case 60:
                                Toast.makeText(getContext(), "que persistente", Toast.LENGTH_SHORT).show();
                                break;
                            case 70:
                                Toast.makeText(getContext(), "...", Toast.LENGTH_SHORT).show();
                                break;
                            case 80:
                                Toast.makeText(getContext(), "ERROR 404: EASTER EGG NO ENCONTRADO", Toast.LENGTH_SHORT).show();
                                break;
                            case 90:
                                Toast.makeText(getContext(), "aqui solia haber un easter egg. PERO YA NO.", Toast.LENGTH_SHORT).show();
                                break;
                            case 100:
                                Toast.makeText(getContext(), "felicidades, le dice tap 100 veces al buho", Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                }
        );
    }

    /**
     * Read the text from archives
     */

    public static String readRawTextFile(int id) {
        InputStream inputStream = mContext.getResources().openRawResource(id);

        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;

        StringBuilder text = new StringBuilder();
        try {
            while (( line = buf.readLine()) != null) text.append(line);
        } catch (IOException e) {
            return null;
        }

        return text.toString();
    }
}