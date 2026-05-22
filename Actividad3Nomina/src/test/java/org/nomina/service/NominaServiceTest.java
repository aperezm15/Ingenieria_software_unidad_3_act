package org.nomina.service;

import org.nomina.model.*;
import org.nomina.exception.ValidacionNominaException;
import org.nomina.exception.DatoNegativoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de Pruebas Unitarias para certificar las Reglas de Negocio del Sistema de Nómina.
 * Aplica principios de Caja Blanca para validar escenarios límite y control defensivo.
 */
public class NominaServiceTest {

    @Test
    @DisplayName("Debería calcular salario bruto con Bono de Antigüedad (> 5 años) para Asalariado")
    public void testEmpleadoAsalariadoConBonoAntiguedad() {
        // Configuración: Empleado con fecha de ingreso de hace 6 años (Sueldo: $3.000.000)
        LocalDate fechaHaceSeisAnios = LocalDate.now().minusYears(6);
        EmpleadoAsalariado emp = new EmpleadoAsalariado(1, "Carlos Mendoza", fechaHaceSeisAnios, 3000000.0);

        // Bruto esperado: Base ($3.000.000) + Bono 10% ($300.000) = $3.300.000
        double brutoEsperado = 3300000.0;
        assertEquals(brutoEsperado, emp.calcularSalarioBruto(), 0.01,
                "El cálculo del bono del 10% por antigüedad superior a 5 años falló.");

        // Beneficio esperado por ser permanente: Bono Alimentación = $1.000.000
        assertEquals(1000000.0, emp.calcularBeneficios(),
                "El beneficio de alimentación para empleados asalariados permanentes es incorrecto.");
    }

    @Test
    @DisplayName("Debería calcular horas extras con recargo de 1.5x para Empleado por Horas")
    public void testEmpleadoPorHorasConRecargoExtra() {
        // Configuración: Tarifa $20.000, laboró 45 horas (5 horas extras)
        // Matemáticas: (40 * 20.000) + (5 * 20.000 * 1.5) = 800.000 + 150.000 = 950.000
        EmpleadoPorHoras emp = new EmpleadoPorHoras(2, "Andrés Silva", LocalDate.now(), 20000.0, 45, false);

        assertEquals(950000.0, emp.calcularSalarioBruto(), 0.01,
                "La ecuación de liquidación de horas extras con recargo de 1.5 falló.");
    }

    @Test
    @DisplayName("Debería activar el Fondo de Ahorro (2%) si cumple antigüedad y autorización")
    public void testEmpleadoPorHorasConFondoAhorro() {
        // Configuración: Más de 1 año de antigüedad (hace 2 años) y aceptaFondoAhorro = true
        LocalDate fechaHaceDosAnios = LocalDate.now().minusYears(2);
        EmpleadoPorHoras emp = new EmpleadoPorHoras(3, "María Ortega", fechaHaceDosAnios, 10000.0, 40, true);

        // Bruto: 40 * 10.000 = 400.000. Beneficio Fondo: 400.000 * 0.02 = 8.000
        double beneficioEsperado = 8000.0;
        assertEquals(beneficioEsperado, emp.calcularBeneficios(), 0.01,
                "El cálculo del beneficio del fondo de ahorro del 2% no coincide.");
    }

    @Test
    @DisplayName("Debería inyectar bono extra del 3% si las ventas del Comisionista superan los $20M")
    public void testEmpleadoComisionConBonoVentasMasivas() {
        // Configuración: Base $1.500.000, Comisión 5%, Ventas $25.000.000 (Supera los $20M)
        // Matemáticas: Base (1.500.000) + Comisión (25M * 5% = 1.250.000) + Bono Extra (25M * 3% = 750.000) = 3.500.000
        EmpleadoComision emp = new EmpleadoComision(4, "Diana Diana", LocalDate.now(), 1500000.0, 5.0, 25000000.0);

        assertEquals(3500000.0, emp.calcularSalarioBruto(), 0.01,
                "El bono de incentivo del 3% sobre el total de ventas superiores a $20M no se aplicó.");
    }

    @Test
    @DisplayName("Debería retornar cero beneficios para Empleados Temporales")
    public void testEmpleadoTemporalSinBeneficios() {
        EmpleadoTemporal emp = new EmpleadoTemporal(5, "Jorge Ruiz", LocalDate.now(), 2000000.0);

        assertEquals(2000000.0, emp.calcularSalarioBruto());
        assertEquals(0.0, emp.calcularBeneficios(),
                "Restricción violada: El empleado temporal no debe percibir bonos ni auxilios.");
    }

    @Test
    @DisplayName("Debería lanzar DatoNegativoException ante parámetros numéricos menores a cero")
    public void testControlDefensivoLanzaExcepcion() {
        // Intento de instanciación inválida con horas negativas
        assertThrows(DatoNegativoException.class, () -> {
            new EmpleadoPorHoras(6, "Error Test", LocalDate.now(), 15000.0, -10, false);
        }, "El sistema permitió horas negativas sin disparar la excepción especializada.");
    }
}