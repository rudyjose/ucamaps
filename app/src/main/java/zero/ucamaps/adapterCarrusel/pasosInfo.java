package zero.ucamaps.adapterCarrusel;


import java.util.ArrayList;

import zero.ucamaps.R;

public class pasosInfo {

    private Integer images;
    private String desc;
    public pasosInfo() {

    }

    public pasosInfo(int images, String desc) {
        this.images = images;

        this.desc = desc;

    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String pubDate;




    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }


    public static  ArrayList<pasosInfo> pasos(Integer pos) {
        ArrayList<pasosInfo> op = null;

        switch (pos) {
            case 0:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.menu, "1. En el menú selecciona la opción\nCambiar tema"));
                op.add(new pasosInfo(R.drawable.galeria1, "2. Selecciona el tema"));
                op.add(new pasosInfo(R.drawable.mapa2, "3. Se cargará el tema seleccionado\nen el mapa"));
                break;

            case 2:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.buscador, "1. Utiliza el buscador para encontrar\nlugares específicos del campus"));
                op.add(new pasosInfo(R.drawable.buscador_pos, "2. te ubicará el lugar en el mapa"));

                break;

            case 1:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.barra_icono1, "1. Selecciona Modo Edición"));
                op.add(new pasosInfo(R.drawable.edicion, "2. Utiliza los iconos para dibujar\nen el mapa:\n a)Borrar punto\n b)Crear Ruta\n c)Cancelar"));
                op.add(new pasosInfo(R.drawable.ruta_edicion, "3. Si seleccionaste icono Crear Ruta,\nte la marcará en el mapa"));
                op.add(new pasosInfo(R.drawable.instruccion_ruta1, "4. Indica cómo quieres recibir las instrucciones"));
                op.add(new pasosInfo(R.drawable.instruccion_ruta, "5. Ver indicaciones paso a paso, se presentara una lista"));
                op.add(new pasosInfo(R.drawable.consulta_instruccion_ruta, "6. te guiará en el mapa en \ncada instrucción"));

                break;

            case 3:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.menu, "1. En el menú selecciona la opción\nBúsqueda Avanzada"));
                op.add(new pasosInfo(R.drawable.busqueda_avanzada, "2. Llena el filtro y selecciona Buscar"));
                op.add(new pasosInfo(R.drawable.busqueda_avanzada1, "3. "));
                break;

            case 4:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.enruta, "1. Selecciona el icono Enrutar"));
                op.add(new pasosInfo(R.drawable.ruta_mi_ubicacion, "2. Indica el lugar de destino, puedes\nintercambiar origen y destino\nSelecciona obtener Ruta"));
                op.add(new pasosInfo(R.drawable.ruta_mi_ubicacion_generada, "3. Marcará la ruta en el mapa"));
                op.add(new pasosInfo(R.drawable.iconos_enrutamiento1, "4. Ver indicaciones paso a paso"));
                op.add(new pasosInfo(R.drawable.instruccion_ruta, "5. Ver indicaciones paso a paso, se presentara una lista"));
                op.add(new pasosInfo(R.drawable.consulta_instruccion_ruta, "6. te guiará en el mapa en \ncada instrucción"));

                break;

            case 5:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.uso_lupa, "1. Presiona sobre un lugar del mapa,\nte aparecerá una Lupa"));
                op.add(new pasosInfo(R.drawable.lupa_icon1, "2. Te posicionará en el mapa"));
                op.add(new pasosInfo(R.drawable.lupa_icon1, "3. Puedes generar ruta de tu ubicación hasta\nel lugar"));
                op.add(new pasosInfo(R.drawable.lupa_icon2, "4. Crea una anotación del lugar"));
                op.add(new pasosInfo(R.drawable.agregar_anotacion, "5. Ingresa los datos y selecciona Aceptar"));
                op.add(new pasosInfo(R.drawable.lupa_icon3, "6. Obtén información detallada del lugar"));
                break;

            case 6:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.menu, "1. Seleccionar en el meú lateral \n la opción Rutas especiales"));
                op.add(new pasosInfo(R.drawable.ruta_especial1, "2."));
                op.add(new pasosInfo(R.drawable.ruta_especial2, "3."));
                op.add(new pasosInfo(R.drawable.ruta_especial3, "4."));

                break;

            case 7:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.menu, "1. En el menú selecciona la opción\nAnotaciones"));
                op.add(new pasosInfo(R.drawable.consultar_anotacion, "2. Puedes editar o eliminar la nota"));

                break;

            case 8:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.ruta_mi_ubicacion_generada, "1. Cuando se genere una ruta, se puede guardar\ncomo ruta favorita"));
                op.add(new pasosInfo(R.drawable.iconos_enrutamiento2, "2. Selecciona el icono Guardar"));
                op.add(new pasosInfo(R.drawable.guardar_ruta, "3. Ingresa un nombre para la ruta"));
                break;

            case 9:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.menu, "1. En el menú selecciona la opción\nRutas Favoritas"));
                op.add(new pasosInfo(R.drawable.consulta_ruta, "2. Mostrará una lista de Rutas guardadas (Max 10)\nSelecciona para que se marque en el mapa"));
                op.add(new pasosInfo(R.drawable.ruta_mi_ubicacion_generada, "3. Se cargará en el mapa"));
                op.add(new pasosInfo(R.drawable.consulta_ruta, "3. Puedes Eliminar una ruta, Selecciona el icono X"));

                break;


            case 10:
                op = new ArrayList<>();
                op.add(new pasosInfo(R.drawable.menu, "1. En el menú selecciona la opción\nConsulta Edificio"));
                op.add(new pasosInfo(R.drawable.consultaedificio, "2. Ingresa el nombre del edificio"));
                op.add(new pasosInfo(R.drawable.edificio2, "3. Se mostrará una imagen y un texto descriptivo"));
                op.add(new pasosInfo(R.drawable.edificio1, "4. Selecciona ver más para ampliar la descripción"));


                break;

        }
        return op;
    }
}








