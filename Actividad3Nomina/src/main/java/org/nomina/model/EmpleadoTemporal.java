package org.nomina.model;

import org.nomina.exception.ValidacionNominaException;
import java.time.LocalDate;

public class EmpleadoTemporal extends Empleado {
    private double salarioMensual;

    public EmpleadoTemporal(int id, String nombre, LocalDate fechaIngreso, double salarioMensual) {
        super(id, nombre, fechaIngreso);

        // Validación defensiva básica
        if (salarioMensual < 0) {
            throw new ValidacionNominaException("Error de Nómina: El salario mensual de un temporal no puede ser negativo.");
        }

        this.salarioMensual = salarioMensual;
    }

    @Override
    public double calcularSalarioBruto() {
        // Retorna estrictamente el salario fijo pactado en su contrato
        return this.salarioMensual;
    }

    @Override
    public double calcularBeneficios() {
        // Restricción: "No aplican bonos ni beneficios adicionales"
        return 0.0;
    }

    // Getter y Setter
    public double getSalarioMensual() { return salarioMensual; }
}