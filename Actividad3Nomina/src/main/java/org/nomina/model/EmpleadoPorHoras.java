package org.nomina.model;
import org.nomina.exception.*;

import java.time.LocalDate;

public class EmpleadoPorHoras extends Empleado {
    private double tarifaHora;
    private int horasTrabajadas;
    private boolean aceptaFondoAhorro;

    public EmpleadoPorHoras(int id, String nombre, LocalDate fechaIngreso, double tarifaHora, int horasTrabajadas, boolean aceptaFondoAhorro) {
        super(id, nombre, fechaIngreso);
        //Excepciones
        if (horasTrabajadas <= 0) {
            throw new DatoNegativoException("horasTrabajadas", horasTrabajadas);
        }
        if (tarifaHora <= 0) {
            throw new DatoNegativoException("tarifaHora", tarifaHora);
        }
        setHorasTrabajadas(horasTrabajadas); // Validación en el setter
        this.tarifaHora = tarifaHora;
        this.aceptaFondoAhorro = aceptaFondoAhorro;
    }

    public void setHorasTrabajadas(int horasTrabajadas) {
        if (horasTrabajadas < 0) {
            throw new IllegalArgumentException("Las horas trabajadas no pueden ser negativas.");
        }
        this.horasTrabajadas = horasTrabajadas;
    }

    @Override
    public double calcularSalarioBruto() {
        if (horasTrabajadas <= 40) {
            return horasTrabajadas * tarifaHora;
        } else {
            int horasNormales = 40;
            int horasExtras = horasTrabajadas - 40;
            return (horasNormales * tarifaHora) + (horasExtras * tarifaHora * 1.5);
        }
    }

    @Override
    public double calcularBeneficios() {
        double beneficios = 0;
        // Si tiene más de 1 año y acepta el fondo de ahorro (2% aportado por la empresa)
        if (getAniosEnEmpresa() >= 1 && aceptaFondoAhorro) {
            beneficios += calcularSalarioBruto() * 0.02;
        }
        return beneficios;
    }

    public double getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(double tarifaHora) {
        this.tarifaHora = tarifaHora;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public boolean isAceptaFondoAhorro() {
        return aceptaFondoAhorro;
    }

    public void setAceptaFondoAhorro(boolean aceptaFondoAhorro) {
        this.aceptaFondoAhorro = aceptaFondoAhorro;
    }
}