<?php

require 'database.php';

class Detalle
{
    function __construct()
    {
    }

	public static function getDetallebyID($nombre)
    {
        // Consulta de la meta
        $consulta = "SELECT * FROM edificio WHERE NOMBRE = ?";

        try {
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute(array($nombre));
            // Capturar primera fila del resultado
			$row = $comando->fetch(PDO::FETCH_ASSOC);
            return $row;
            
        } catch (PDOException $e) {
			print $e;
			
            // Aquí puedes clasificar el error dependiendo de la excepción
            // para presentarlo en la respuesta Json
        }
    }
	
}
?>