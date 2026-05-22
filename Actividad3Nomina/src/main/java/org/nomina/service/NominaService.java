package org.nomina.service;

import org.nomina.model.Empleado;
import org.nomina.repository.EmpleadoRepository;
import org.nomina.exception.ValidacionNominaException;

import java.util.List;

public class NominaService {


    private final EmpleadoRepository repository;

    public NominaService(EmpleadoRepository repository) {
        this.repository = repository;
    }
    
    public void procesarNominaCompleta() {
        try {
            // Extrae la lista polimórfica desde el repositorio
            List<Empleado> empleados = repository.obtenerTodos();

            if (empleados.isEmpty()) {
                System.out.println("⚠️ Advertencia: No se encontraron empleados registrados en el sistema.");
                return;
            }

            // Variables acumuladoras para métricas globales de la empresa
            double totalBrutoEmpresa = 0;
            double totalBeneficiosEmpresa = 0;
            double totalDeduccionesEmpresa = 0;
            double totalNetoEmpresa = 0;

            System.out.println("==========================================================================================");
            System.out.println("                       REPORTE CONSOLIDADO DE NÓMINA EMPRESARIAL                          ");
            System.out.println("==========================================================================================");
            System.out.printf("%-4s | %-18s | %-12s | %-12s | %-10s | %-11s | %-12s%n",
                    "ID", "NOMBRE", "CONTRATO", "BRUTO", "BENEFICIOS", "DEDUCCIONES", "NETO FINAL");
            System.out.println("------------------------------------------------------------------------------------------");

            for (Empleado emp : empleados) {
                // Ejecución Polimórfica en tiempo de ejecución de las fórmulas matemáticas
                double bruto = emp.calcularSalarioBruto();
                double beneficios = emp.calcularBeneficios();
                double deducciones = emp.calcularDeducciones();
                double neto = emp.calcularSalarioNeto();

                // Acumulación de totales corporativos
                totalBrutoEmpresa += bruto;
                totalBeneficiosEmpresa += beneficios;
                totalDeduccionesEmpresa += deducciones;
                totalNetoEmpresa += neto;

                // Formateo limpio de salida (Clean Code) para variables de tipo moneda string locale
                String tipoContrato = emp.getClass().getSimpleName().replace("Empleado", "").toUpperCase();

                System.out.printf("%04d | %-18s | %-12s | $%,11.2f | $%,9.2f | $%,10.2f | $%,11.2f%n",
                        emp.getId(), emp.getNombre(), tipoContrato, bruto, beneficios, deducciones, neto);
            }

            System.out.println("==========================================================================================");
            System.out.println("                                RESUMEN DE COSTOS CORPORATIVOS                            ");
            System.out.println("==========================================================================================");
            System.out.printf(" 💰 Gasto Total de Salarios Brutos:   $%,15.2f%n", totalBrutoEmpresa);
            System.out.printf(" 🎁 Gasto Total en Beneficios/Bonos:  $%,15.2f%n", totalBeneficiosEmpresa);
            System.out.printf(" 🛡️ Retenciones y Deducciones Totales: $%,15.2f%n", totalDeduccionesEmpresa);
            System.out.printf(" 🚀 Desembolso Total Neto a Pagar:    $%,15.2f%n", totalNetoEmpresa);
            System.out.println("==========================================================================================");

        } catch (ValidacionNominaException e) {
            // Captura defensiva de errores semánticos controlados
            System.err.println("❌ Error en el procesamiento de la nómina: " + e.getMessage());
        }
    }
}