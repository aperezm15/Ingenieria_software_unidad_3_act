package org.nomina.config;
import org.nomina.exception.ValidacionNominaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//Clase para conexion a base de datos
public class ConexionBD {
    //Direccion para conectar a base de datos localhost, con el nombre 'sistema_nomina'
     private static final String URL = "jdbc:mysql://localhost:3306/sistema_nomina?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
     //usuario root para conexion
     private static final String USUARIO = "root";
     //contraseña root para conexion
     private static final String CONTRASEÑA = "root";

     //Metodo para obtener conexion
     public static Connection obtenerConexion() {
        //Usamos las herramientas de la dependencia connection
         try{
             //nos cargara el driver
             Class.forName("com.mysql.cj.jdbc.Driver");
             //nos pedira URL, USUARIO, y CONTRASEÑA, que ya lo tenemos guardado en las variables de arriba
             return DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
             //Excepcion si el driver no es encontrado, puede ser por la version de la dependencia
             //o que en el pom.xml no haya agregado la dependencia correctamente
         } catch (ClassNotFoundException e) {
             //si es asi, nos mandara este error
             throw new ValidacionNominaException("Error de Infraestructura: No se encontro el drier de MySQL en el Classpath");
         }catch (SQLException e) {
             //En caso de haber algun dato erroneo, nos mandara este mensaje.
             //e.getMessage nos dira exactamente que problema tenemos.
             throw new ValidacionNominaException("Error de Conexion a la base de Datos: " + e.getMessage());
         }
     }
}
