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


public class EmpleadoRepository {

    public List<Empleado> obtenerTodos() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT id, nombre, tipo_empleado, fecha_ingreso, salario_base, " +
                "tarifa_hora, horas_trabajadas, porcentaje_comision, ventas_mes, " +
                "acepta_fondo_ahorro FROM empleados";


        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                LocalDate fechaIngreso = rs.getDate("fecha_ingreso").toLocalDate();
                String tipoStr = rs.getString("tipo_empleado");

                // Mapear los registros planos a la jerarquía de objetos POO (Polimorfismo de Construcción)
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
}