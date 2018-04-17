package zero.ucamaps;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.core.geometry.MapGeometry;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.client.android.CaptureActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import zero.ucamaps.basemaps.BasemapsDialogFragment;
import zero.ucamaps.beans.FavoriteRoute;
import zero.ucamaps.database.CargaAsinc;
import zero.ucamaps.database.InsertAsinc;
import zero.ucamaps.database.Nota;
import zero.ucamaps.database.RutaEspecial;
import zero.ucamaps.dialogs.AboutDialog;
import zero.ucamaps.dialogs.DialogFavoriteList;
import zero.ucamaps.dialogs.DialogNotesList;
import zero.ucamaps.dialogs.DialogSearchForm;


public class MainActivity extends ActionBarActivity {

    public static DrawerLayout mDrawerLayout;
    private String changeSound = "";
    private String baseColor = "";
    public CargaAsinc ca = new CargaAsinc();



    MapFragment mapFragment;
    /**
     * The list of menu items in the navigation drawer
     */
    @InjectView(R.id.uca_maps_activity_left_drawer)
    ListView mDrawerList;

    private final List<DrawerItem> mDrawerItems = new ArrayList<>();

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.uca_maps_activity);
        ButterKnife.inject(this);
        setupDrawer();
        try {
            String[] colorMapa = obtenerMapa();
            if(colorMapa[0].equals("none")){
                setView("2161ba8a41114947bc7c533a24bdb150","Sonido Apagado");
            }else {
                setView(colorMapa[0],colorMapa[1]);
            }
        }catch(Exception e){
            e.printStackTrace();

        }






        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MapFragment.editMode){
            mapFragment.showEditionMenu();
        }
        updateDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // first check if the drawer toggle button was selected
        if(MapFragment.editMode){
            Toast.makeText(this,"Hay un tiempo y lugar para todo, pero no ahora.", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean handled = mDrawerToggle.onOptionsItemSelected(item);
        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

    /**
     * Initializes the navigation drawer.
     */
    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.uca_maps_activity_drawer_layout);

        //mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // set a custom shadow that overlays the main content when the drawer
        // opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        //ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //MapFragment.showBar=true;
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //MapFragment.showBar=false;
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        updateDrawer();
    }

    //Initialize default view
    private void setView(String temaMapa,String sonido) {
        // show the default map
        setChangeSound(sonido);
        showMapWithSound(temaMapa, changeSound,0);

    }

    /**
     * Opens the map represented by the specified portal item or if null, opens a default map.
     */

    public void showMapWithSound(String basemapPortalItemId, String changeSound,int momento) {

        if (momento == 0) {

            setBasemapItem(basemapPortalItemId);

            // remove existing MapFragment explicitly, simply replacing it can cause the app to freeze when switching basemaps
            FragmentTransaction transaction;
            FragmentManager fragmentManager = getFragmentManager();

            mapFragment = MapFragment.newSoundInstance(basemapPortalItemId, changeSound);

            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.uca_maps_activity_content_frame, mapFragment, MapFragment.TAG);
            transaction.addToBackStack(null);
            transaction.commit();

            invalidateOptionsMenu(); // reload the options menu
        } else{
            Intent intent = getIntent();
            finish();
            startActivity(intent);
    }
    }

    /**
     * Updates the navigation drawer items.
     */
    private void updateDrawer() {
        mDrawerItems.clear();

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        DrawerItem item = null;
        // Adding the theme item in the drawer
        LinearLayout view_basemap = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_drawer_basemap = (TextView) view_basemap.findViewById(R.id.drawer_item_textview);
        ImageView icon_drawer_basemap = (ImageView) view_basemap.findViewById(R.id.drawer_item_icon);

        text_drawer_basemap.setText(getString(R.string.menu_basemaps));
        icon_drawer_basemap.setImageResource(R.drawable.action_map);
        item = new DrawerItem(view_basemap, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {
                // Show BasemapsDialogFragment to offer a choice if basemaps.
                // This calls back to onBasemapChanged() if one is selected.
                BasemapsDialogFragment basemapsFrag = new BasemapsDialogFragment();
                basemapsFrag.setBasemapsDialogListener(new BasemapsDialogFragment.BasemapsDialogListener() {

                    @Override
                    public void onBasemapChanged(String itemId) {
                        guardarTema(itemId,getChangeSound(),0);
                        showMapWithSound(itemId, changeSound,1);
                    }
                });
                basemapsFrag.show(getFragmentManager(), null);
                mDrawerLayout.closeDrawers();
            }

        });
        mDrawerItems.add(item);

        // Adding the QR item in the Drawer
        LinearLayout view_qr = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_drawer_qr = (TextView) view_qr.findViewById(R.id.drawer_item_textview);
        ImageView icon_drawer_qr = (ImageView) view_qr.findViewById(R.id.drawer_item_icon);

        text_drawer_qr.setText(getString(R.string.action_qr));
        icon_drawer_qr.setImageResource(R.drawable.action_qr_code);
        item = new DrawerItem(view_qr, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {

                Intent myIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(myIntent, 1);
                //Close and lock the drawer
                mDrawerLayout.closeDrawers();
            }
        });
        mDrawerItems.add(item);

        // Adding the sound item
        LinearLayout view_sound = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        final TextView text_drawer_sound = (TextView) view_sound.findViewById(R.id.drawer_item_textview);
        ImageView icon_drawer_sound = (ImageView) view_sound.findViewById(R.id.drawer_item_icon);

        if(getChangeSound().equals("Sonido Apagado")) {
            text_drawer_sound.setText(getString(R.string.action_sound_1));}
        else {
            text_drawer_sound.setText(getString(R.string.action_sound_2));
        }

        icon_drawer_sound.setImageResource(R.drawable.action_sound);

        item = new DrawerItem(view_sound, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {

                //Sends the parameter to the dynamic fragment
                if (getChangeSound().equals("Sonido Apagado")) {
                    guardarTema(baseColor,"Sonido Encendido",1);
                    setChangeSound("Sonido Encendido");
                    showMapWithSound(baseColor, changeSound,1);
                } else {
                    guardarTema(baseColor,"Sonido Apagado",1);
                    setChangeSound("Sonido Apagado");
                    showMapWithSound(baseColor, changeSound,1);
                }
                //Close and lock the drawer
                mDrawerLayout.closeDrawers();
            }

        });
        mDrawerItems.add(item);


        // añandiendo el item de rutas favoritas
        LinearLayout favorites = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_favorites = (TextView) favorites.findViewById(R.id.drawer_item_textview);
        ImageView icon_favorites = (ImageView) favorites.findViewById(R.id.drawer_item_icon);

        text_favorites.setText("Rutas Favoritas");
        icon_favorites.setImageResource(R.drawable.ic_star_black_24dp);
        item = new DrawerItem(favorites, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {


                DialogFavoriteList favFrag = new DialogFavoriteList();
                favFrag.setRoutingDialogListener(mapFragment);
                List<RutaEspecial> recuperar = favFrag.recuperar();
                if(!recuperar.isEmpty()){
                    favFrag.show(getFragmentManager(), "Rutas Favoritas");
                }
                else{

                    Toast.makeText(getApplicationContext(),"No hay rutas favoritas", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawers();
            }

        });

        mDrawerItems.add(item);

        // añandiendo el item de rutas Especiales
        LinearLayout specials = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_specials = (TextView) specials.findViewById(R.id.drawer_item_textview);
        ImageView icon_specials = (ImageView) specials.findViewById(R.id.drawer_item_icon);

        text_specials.setText("Rutas Especiales");
        icon_specials.setImageResource(R.drawable.ic_business_black_24dp);
        item = new DrawerItem(specials, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {

                ca.dsr.show(getFragmentManager(),"Usando Base de datos");
                mDrawerLayout.closeDrawers();
            }

        });

        mDrawerItems.add(item);

        // añandiendo el item de busqueda avanzada
        LinearLayout advanced_search = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_search = (TextView) advanced_search.findViewById(R.id.drawer_item_textview);
        ImageView icon_search = (ImageView) advanced_search.findViewById(R.id.drawer_item_icon);

        text_search.setText("Busqueda Avanzada");
        icon_search.setImageResource(R.drawable.ic_find_in_page_black_24dp);
        item = new DrawerItem(advanced_search, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {
                DialogSearchForm diaSEFO = new DialogSearchForm();
                diaSEFO.setMapFragment(mapFragment);
                diaSEFO.show(getFragmentManager(), "Dialog Search");
                mDrawerLayout.closeDrawers();
            }

        });

        mDrawerItems.add(item);

        // añandiendo el item de rutas favoritas
        LinearLayout notes = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_notes = (TextView) notes.findViewById(R.id.drawer_item_textview);
        ImageView icon_notes = (ImageView) notes.findViewById(R.id.drawer_item_icon);

        text_notes.setText("Anotaciones");
        icon_notes.setImageResource(R.drawable.ic_book_black_24dp);
        item = new DrawerItem(notes, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {

                DialogNotesList noteFrag = new DialogNotesList();
                noteFrag.setContexto(MainActivity.this);
                noteFrag.setFragmento(mapFragment);
                noteFrag.setManager(getFragmentManager());
                List<Nota> recuperar = noteFrag.recuperar();
                if(!recuperar.isEmpty()){
                    noteFrag.show(getFragmentManager(), "Reminders");
                }
                else{
                    Toast.makeText(getApplicationContext(),"No hay anotaciones guardadas", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawers();
            }

        });

        mDrawerItems.add(item);


        // Item para añadir rutas al servidor, para comprobar si funciona
        /*LinearLayout insert = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_insert = (TextView) insert.findViewById(R.id.drawer_item_textview);
        ImageView icon_insert = (ImageView) insert.findViewById(R.id.drawer_item_icon);

        text_insert.setText("boton de insert");
        icon_insert.setImageResource(R.drawable.ic_business_black_24dp);
        item = new DrawerItem(insert, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {
                InsertAsinc ia = new InsertAsinc();
                ia.execute(MainActivity.this);

            }

        });

        mDrawerItems.add(item);*/

        // Adding the about item
        LinearLayout view_about = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_item_layout, null);
        TextView text_drawer_about = (TextView) view_about.findViewById(R.id.drawer_item_textview);
        ImageView icon_drawer_about = (ImageView) view_about.findViewById(R.id.drawer_item_icon);

        text_drawer_about.setText(getString(R.string.action_about));
        icon_drawer_about.setImageResource(R.drawable.action_about);
        item = new DrawerItem(view_about, new DrawerItem.OnClickListener() {

            @Override
            public void onClick() {

                AboutDialog about = new AboutDialog(MainActivity.this);
                about.setTitle("Sobre esta app");
                about.show();
                //Close and lock the drawer
                mDrawerLayout.closeDrawers();
            }

        });

        mDrawerItems.add(item);

        BaseAdapter adapter = (BaseAdapter) mDrawerList.getAdapter();
        if (adapter == null) {
            adapter = new DrawerItemListAdapter();
            mDrawerList.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if (ca.getStatus() == AsyncTask.Status.FINISHED);
        else{
        ca.execute(MainActivity.this);
        ca.dsr.setRoutingDialogListener(mapFragment);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://zero.ucamaps/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://zero.ucamaps/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * Handles selection of items in the navigation drawer.
     */
    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDrawerItems.get(position).onClicked();
        }
    }

    /**
     * Populates the navigation drawer list with items.
     */
    private class DrawerItemListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDrawerItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mDrawerItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DrawerItem drawerItem = (DrawerItem) getItem(position);
            return drawerItem.getView();
        }
    }

    /**
     * Changes the String Values for the Sound.
     */

    public String getChangeSound() {
        return this.changeSound;
    }

    public void setChangeSound(String changeSound) {
        this.changeSound = changeSound;
    }

    /**
     * Changes the String Values for the Menu.
     */

    public String getBasemapItem() {
        return baseColor;
    }

    public void setBasemapItem(String baseColor) {
        this.baseColor = baseColor;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int result = data.getIntExtra("result", 0);
                switch (result) {
                    case 0:
                        mapFragment.executeLocatorTask(data.getStringExtra("location"));
                        mapFragment.drawRoute();
                        break;
                }
                if (resultCode == RESULT_CANCELED) {
                }
            }
        }
    }

    private String[] obtenerMapa() throws IOException {
        //cuenta las lineas del archivo para que no se pase de 10 rutas favoritas
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
        dir.mkdirs();
        File file = new File(dir.getAbsolutePath(),"preferences");
        ObjectInputStream objectinputstream = null;
        if (file.exists()) {
            try {
                FileInputStream streamIn = new FileInputStream(file);
                objectinputstream = new ObjectInputStream(streamIn);
                String tema = objectinputstream.readObject().toString();
                String[] partes = tema.split("/");
                return partes;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (objectinputstream != null) {
                    objectinputstream.close();
                }
            }
            String [] wrong = {"none"};
            return wrong;
        } else{
            String [] wrong = {"none"};
        return wrong;
        }
    }


    private void guardarTema(String tema,String sonido,int guardado) {
        File tarjeta = Environment.getExternalStorageDirectory();
        File dir = new File(tarjeta.getAbsolutePath(), "/ucamaps/");
        dir.mkdirs();
        File file = new File(dir.getAbsolutePath(),"preferences");
        //verificamos si el archivo existe
        try {
            FileOutputStream fOut = null;
            ObjectOutputStream oos = null;
            try {


                fOut = new FileOutputStream(file);
                oos = new ObjectOutputStream(fOut);
                oos.writeObject(tema + "/" + sonido);
                if(guardado==0){
                    Toast.makeText(getApplicationContext(), "Tema Guardado", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Opcion de Sonido Guardada", Toast.LENGTH_SHORT).show();
                }
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
}
