package org.nomina.model;

import org.nomina.exception.*;
import java.time.LocalDate;


public class EmpleadoComision extends Empleado {
    private double salarioBase;
    private double porcentajeComision;
    private double ventasMes;

    // Constante de negocio según el requerimiento
    private static final double BONO_ALIMENTACION = 1000000.0;
    private static final double UMBRAL_VENTAS_BONO = 20000000.0;

    public EmpleadoComision(int id, String nombre, LocalDate fechaIngreso,
                            double salarioBase, double porcentajeComision, double ventasMes) {
        super(id, nombre, fechaIngreso);

        // Excepciones
        if (ventasMes < 0) {
            throw new DatoNegativoException("ventasMes", ventasMes);
        }

        if (salarioBase < 0 || porcentajeComision < 0) {
            throw new DatoNegativoException("Error de Nómina: El salario base o porcentaje no pueden ser negativos.");
        }

        this.salarioBase = salarioBase;
        this.porcentajeComision = porcentajeComision;
        this.ventasMes = ventasMes;
    }

    @Override
    public double calcularSalarioBruto() {
        // Cálculo de la comisión estándar pactada
        double comisionEstandar = this.ventasMes * (this.porcentajeComision / 100.0);
        double brutoTotal = this.salarioBase + comisionEstandar;

        // Regla: Si las ventas superan los $20.000.000, recibe un bono adicional del 3% sobre las ventas
        if (this.ventasMes > UMBRAL_VENTAS_BONO) {
            brutoTotal += this.ventasMes * 0.03;
        }

        return brutoTotal;
    }

    @Override
    public double calcularBeneficios() {
        // Al ser empleado permanente, goza del Bono de Alimentación
        return BONO_ALIMENTACION;
    }

    // Getters y Setters para mantener el Encapsulamiento
    public double getSalarioBase() { return salarioBase; }
    public double getPorcentajeComision() { return porcentajeComision; }
    public double getVentasMes() { return ventasMes; }
}