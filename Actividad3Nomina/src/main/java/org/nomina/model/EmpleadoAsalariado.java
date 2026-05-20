package org.nomina.model;

import java.time.LocalDate;

public class EmpleadoAsalariado extends Empleado {
    private double salarioMensual;
    private static final double BONO_ALIMENTACION = 1000000;

    public EmpleadoAsalariado(int id, String nombre, LocalDate fechaIngreso, double salarioMensual) {
        super(id, nombre, fechaIngreso);
        this.salarioMensual = salarioMensual;
    }

    @Override
    public double calcularSalarioBruto() {
        double bruto = this.salarioMensual;
        if (getAniosEnEmpresa() > 5) {
            bruto += this.salarioMensual * 0.10;
        }
        return bruto;
    }

    @Override
    public double calcularBeneficios() {
        return BONO_ALIMENTACION;
    }

}
