package org.nomina.model;

import java.time.LocalDate;
import java.time.Period;
//Clase abstracta empleado Empleado, como tendremos varios tipos de empleado
//lo ideal es tener una clase abstracta con los datos que estan relacionados entre todos
public abstract class Empleado {
    private int id;
    private String nombre;
    private LocalDate fechaIngreso;
    //Constructor
    public Empleado(int id, String nombre, LocalDate fechaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.fechaIngreso = fechaIngreso;
    }
    //Metodo para tener los años trabajado del empleado en la empresa, desde la fecha puesta
    //Hasta la fecha actual.
    public int getAniosEnEmpresa() {
        return Period.between(fechaIngreso, LocalDate.now()).getYears();
    }

    //implementamos metodos abstractos, estos metodos no calcularán nada aquí en esta clase
    //hace falta la version de las clases hijas para hacer sus propios calculos.
    public abstract double calcularSalarioBruto();
    public abstract double calcularBeneficios();

    //Centraliza la regla de negocio de las deducciones. No importa el tipo de contrato que
    //tenga el empleado, la ley de salud y pensión se aplica igual para todos sobre el salario
    //bruto obtenido.
    public double calcularDeducciones() {
        double salarioBruto = calcularSalarioBruto();
        double saludYPension = salarioBruto * 0.04;
        double arl = calcularARL();
        return saludYPension + arl;
    }

    //Este método fue diseñado originalmente para calcular el porcentaje de la Administradora
    // de Riesgos Laborales (tasa base del 0.522% en Colombia). El modificador protected permite
    // que las subclases tengan acceso a él si necesitan alterar el nivel de riesgo.
            protected double  calcularARL() {
        return calcularDeducciones() * 0.00522;
    }

    //Este método determina el dinero real que recibirá el empleado en su cuenta bancaria.
    public double calcularSalarioNeto() {
        double neto = calcularDeducciones() * calcularBeneficios() - calcularDeducciones();
        return Math.max(neto, 0);
    }


    //Getters y setters.
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
