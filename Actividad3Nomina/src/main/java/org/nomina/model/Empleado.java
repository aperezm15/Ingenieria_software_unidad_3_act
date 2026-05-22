package org.nomina.model;

import java.time.LocalDate;
import java.time.Period;

public abstract class Empleado {
    private int id;
    private String nombre;
    private LocalDate fechaIngreso;

    public Empleado(int id, String nombre, LocalDate fechaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.fechaIngreso = fechaIngreso;
    }

    public int getAniosEnEmpresa() {
        return Period.between(fechaIngreso, LocalDate.now()).getYears();
    }

    public abstract double calcularSalarioBruto();
    public abstract double calcularBeneficios();

    /**
     * Calcula de forma consolidada las deducciones de Salud, Pensión y ARL.
     * Basado independientemente en el Salario Bruto para evitar recursión.
     */
    public double calcularDeducciones() {
        double salarioBruto = calcularSalarioBruto();
        // Salud (4%) + Pensión (4%) = 8% en total a cargo del empleado
        double saludYPension = salarioBruto * 0.08;
        double arl = calcularARL();
        return saludYPension + arl;
    }

    /**
     * Calcula la ARL aplicando la tasa base (0.522%) de manera directa
     * sobre el salario bruto, rompiendo el bucle infinito.
     */
    protected double calcularARL() {
        return calcularSalarioBruto() * 0.00522;
    }

    /**
     * Ecuación financiera estándar: Bruto + Beneficios - Deducciones.
     */
    public double calcularSalarioNeto() {
        double bruto = calcularSalarioBruto();
        double beneficios = calcularBeneficios();
        double deducciones = calcularDeducciones();

        double neto = bruto + beneficios - deducciones;
        return Math.max(neto, 0); // Control defensivo contra netos negativos
    }

    // --- GETTERS Y SETTERS ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}