<?php
/**
 * Obtiene el detalle de una meta especificada por
 * su identificador "idMeta"
 */

require 'detalle.php';

if ($_SERVER['REQUEST_METHOD'] == 'GET') {

    if (isset($_GET['nombre'])) {

        // Obtener parámetro nombre
        $parametro = $_GET['nombre'];

        // Tratar retorno
        $retorno = Detalle::getDetallebyID($parametro);


        if ($retorno) {

            $detalle["estado"] = "1";
            $detalle["detalles"] = $retorno;
			//$retorno;
            // Enviar objeto json de la meta
            print json_encode($detalle);
        } else {
            // Enviar respuesta de error general
            print json_encode(
                array(
                    'estado' => '2',
                    'mensaje' => 'No se obtuvo el registro'
                )
            );
        }

    } else {
        // Enviar respuesta de error
        print json_encode(
            array(
                'estado' => '3',
                'mensaje' => 'Se necesita un identificador'
            )
        );
    }
}

?>