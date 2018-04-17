<?php
/**
 * Insertar una nueva meta en la base de datos
 */

require 'ruta.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    // Decodificando formato Json
    $body = json_decode(file_get_contents("php://input"), true);

    // Insertar ruta
    $retorno = Ruta::insert(
        $body['nombre'],
        $body['descripcion'],
        $body['puntos']);

    if ($retorno) {
        // Codigo de exito
        print json_encode(
            array(
                'estado' => '1',
                'mensaje' => 'Creacion exitosa')
        );
    } else {
        // C�digo de falla
        print json_encode(
            array(
                'estado' => '2',
                'mensaje' => 'Creacion fallida')
        );
    }
}
?>

