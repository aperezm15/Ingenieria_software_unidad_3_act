package org.nomina.model;

import java.time.LocalDate;
//Clase hija extendida de la Clase abstracta Empleado
public class EmpleadoAsalariado extends Empleado {
    private double salarioMensual;
    private static final double BONO_ALIMENTACION = 1000000;

    //Constructor
    public EmpleadoAsalariado(int id, String nombre, LocalDate fechaIngreso, double salarioMensual) {
        super(id, nombre, fechaIngreso);
        this.salarioMensual = salarioMensual;
    }

    //Metodo extendido de Empleado, para calcular el SalarioBruto, dedpendiendo de los años
    //que tenga el empleado.
    @Override
    public double calcularSalarioBruto() {
        double bruto = this.salarioMensual;
        //Tenemos que si supera los 5 años de antiguedad, este salario se multiplicara
        //por el 10% como bono mensual.
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
