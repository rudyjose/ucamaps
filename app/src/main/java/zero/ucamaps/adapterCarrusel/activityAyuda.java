package zero.ucamaps.adapterCarrusel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import zero.ucamaps.R;


public class activityAyuda extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    private MyRecyclerViewAdapter adapter;
    ArrayList<Integer> viewImage;
    ArrayList<String> texto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle args = getIntent().getExtras();
        Integer pos = args.getInt("opcion"); //obtenemos la posición del elemento seleccionado en el menu

        // llenando datos para el carrusel
        clasificarData(pos);

        // configuración RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvInstrucciones);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        adapter = new MyRecyclerViewAdapter(this, viewImage, texto);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public void clasificarData(Integer posicion){
        ArrayList<pasosInfo> data=pasosInfo.pasos(posicion);
        viewImage = new ArrayList<>();
        texto = new ArrayList<>();

        for(int i =0; i< data.size(); i++){
            viewImage.add(data.get(i).getImages());
            texto.add(data.get(i).getDesc());
        }

    }

}
