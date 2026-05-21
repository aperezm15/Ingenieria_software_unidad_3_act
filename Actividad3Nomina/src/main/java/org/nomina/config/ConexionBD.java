package org.nomina.config;
import org.nomina.exception.ValidacionNominaException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
     private static final String URL = "jdbc:mysql://localhost:3306/sistema_nomina?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
     private static final String USUARIO = "root";
     private static final String CONTRASEÑA = "root";

     public static Connection obtenerConexion() {
         try{
             Class.forName("com.mysql.cj.jdbc.Driver");
             return DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
         } catch (ClassNotFoundException e) {
             throw new ValidacionNominaExcepcion("Error de Infraestructura: No se encontro el drier de MySQL en el Classpath");
         }catch (SQLException) {
             throw new ValidacionNominaException("Error de Conexion a la base de Datos: " + e.getMessage());
         }
     }
}
