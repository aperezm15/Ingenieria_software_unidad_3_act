package org.nomina.repository;

import org.nomina.config.ConexionBD;
import org.nomina.model.*;
import org.nomina.exception.ValidacionNominaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Clase Repository, para tener metodos como obtener todos los empleados, con sus datos,
//y tambien el metodo guardar para registrar empledaos.
public class EmpleadoRepository {
    //Sentencia SQL para buscar datos de empleados de la tabla empleados
    public List<Empleado> obtenerTodos() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT id, nombre, tipo_empleado, fecha_ingreso, salario_base, " +
                "tarifa_hora, horas_trabajadas, porcentaje_comision, ventas_mes, " +
                "acepta_fondo_ahorro FROM empleados";

        //Se hace conexion de esta clase con el driver para la base de datos atravez de el metodo
        //obtenerConexion de ConexionBD
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            //Decimos que datos son los que se van a sincronizar
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                LocalDate fechaIngreso = rs.getDate("fecha_ingreso").toLocalDate();
                String tipoStr = rs.getString("tipo_empleado");

                // Mapear los registros planos a la jerarquía de objetos POO (Polimorfismo de Construcción)
                // Basicamente buscamos los datos que necesitemos, dependiendo el tipo de empleado.
                switch (tipoStr) {
                    case "ASALARIADO" -> {
                        double salario = rs.getDouble("salario_base");
                        empleados.add(new EmpleadoAsalariado(id, nombre, fechaIngreso, salario));
                    }
                    case "POR_HORAS" -> {
                        double tarifa = rs.getDouble("tarifa_hora");
                        int horas = rs.getInt("horas_trabajadas");
                        boolean fondo = rs.getBoolean("acepta_fondo_ahorro");
                        empleados.add(new EmpleadoPorHoras(id, nombre, fechaIngreso, tarifa, horas, fondo));
                    }
                    case "COMISION" -> {
                        double base = rs.getDouble("salario_base");
                        double porcentaje = rs.getDouble("porcentaje_comision");
                        double ventas = rs.getDouble("ventas_mes");
                        empleados.add(new EmpleadoComision(id, nombre, fechaIngreso, base, porcentaje, ventas));
                    }
                    case "TEMPORAL" -> {
                        double salario = rs.getDouble("salario_base");
                        empleados.add(new EmpleadoTemporal(id, nombre, fechaIngreso, salario));
                    }
                    default -> throw new ValidacionNominaException(
                            "Error de Datos: Tipo de contrato desconocido en la base de datos: " + tipoStr
                    );
                }
            }

        } catch (SQLException e) {
            // Abstracción de Excepciones: No propagamos el error de SQL crudo (infraestructura)
            // a las capas de arriba, lo envolvemos en nuestra semántica de negocio.
            throw new ValidacionNominaException("Error en el motor de persistencia al leer empleados: " + e.getMessage());
        }

        return empleados;
    }
    //Sentencia SQL para guardar un empleado, el cual nos servira para registrar en el sistema.
    public void guardar(Empleado emp) {
        String sql = "INSERT INTO empleados (nombre, tipo_empleado, fecha_ingreso, salario_base, " +
                "tarifa_hora, horas_trabajadas, porcentaje_comision, ventas_mes, acepta_fondo_ahorro) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getNombre());
            ps.setDate(3, java.sql.Date.valueOf(emp.getFechaIngreso()));

            // Determinar el discriminador y mapear atributos específicos según la subclase
            // Lo mismo que vimos arriba, guardamos datos dependiendo que necesitemos
            if (emp instanceof EmpleadoAsalariado ea) {
                ps.setString(2, "ASALARIADO");
                ps.setDouble(4, ea.getSalarioMensual());
                ps.setNull(5, java.sql.Types.DOUBLE);
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setNull(7, java.sql.Types.DOUBLE);
                ps.setNull(8, java.sql.Types.DOUBLE);
                ps.setNull(9, java.sql.Types.BOOLEAN);
            } else if (emp instanceof EmpleadoPorHoras eph) {
                ps.setString(2, "POR_HORAS");
                ps.setNull(4, java.sql.Types.DOUBLE);
                ps.setDouble(5, eph.getTarifaHora());
                ps.setInt(6, eph.getHorasTrabajadas());
                ps.setNull(7, java.sql.Types.DOUBLE);
                ps.setNull(8, java.sql.Types.DOUBLE);
                ps.setBoolean(9, eph.isAceptaFondoAhorro());
            } else if (emp instanceof EmpleadoComision ec) {
                ps.setString(2, "COMISION");
                ps.setDouble(4, ec.getSalarioBase());
                ps.setNull(5, java.sql.Types.DOUBLE);
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setDouble(7, ec.getPorcentajeComision());
                ps.setDouble(8, ec.getVentasMes());
                ps.setNull(9, java.sql.Types.BOOLEAN);
            } else if (emp instanceof EmpleadoTemporal et) {
                ps.setString(2, "TEMPORAL");
                ps.setDouble(4, et.getSalarioMensual());
                ps.setNull(5, java.sql.Types.DOUBLE);
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setNull(7, java.sql.Types.DOUBLE);
                ps.setNull(8, java.sql.Types.DOUBLE);
                ps.setNull(9, java.sql.Types.BOOLEAN);
            }
            //Mensaje de exito
            ps.executeUpdate();
            System.out.println("✨ [Repository] Empleado registrado exitosamente en MySQL.");
        //Si no es exito, saltara la excepcion
        } catch (SQLException e) {
            throw new ValidacionNominaException("Error al insertar el empleado en la base de datos: " + e.getMessage());
        }
    }
}