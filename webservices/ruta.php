<?php

require 'database.php';

class Ruta
{
    function __construct()
    {
    }

   
    public static function getAll()
    {
        $consulta = "SELECT * FROM rutaespecial";
        try {
            // Preparar sentencia
            $comando = Database::getInstance()->getDb()->prepare($consulta);
            // Ejecutar sentencia preparada
            $comando->execute();

            return $comando->fetchAll(PDO::FETCH_ASSOC);

        } catch (PDOException $e) {
            return false;
        }
    }
	
	public static function insert(
	$nombre,
	$descripcion,
	$puntos
	)
	{//sentencia para insert
	$comando = "INSERT INTO rutaespecial ( ".
	"NOMBRE," .
	" DESCRIPCION," .
	" PUNTOS)" .
	" VALUES( ?,?,?)";
	$sentencia = Database::getInstance()->getDb()->prepare($comando);
	$sentencia->execute(
	array(
	$nombre,
	$descripcion,
	$puntos)
	);
	return $sentencia;
	}	
}
?>