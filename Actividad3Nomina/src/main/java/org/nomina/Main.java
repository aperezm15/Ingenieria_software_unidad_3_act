package org.nomina;

import org.nomina.repository.EmpleadoRepository;
import org.nomina.service.NominaService;
import org.nomina.model.*;
import org.nomina.exception.ValidacionNominaException;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * Consola Maestra Interactiva para la gestión del Sistema Automatizado de Nómina.
 * Expone de forma explícita cada requerimiento, validación y persistencia relacional.
 */
public class Main {
    private static final EmpleadoRepository repositorio = new EmpleadoRepository();
    private static final NominaService servicioNomina = new NominaService(repositorio);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean ejecutar = true;

        System.out.println("=====================================================================");
        System.out.println("🛡️  SISTEMA DE GESTIÓN DE NÓMINA - CIPA INGENIERÍA DE SOFTWARE 🛡️");
        System.out.println("=====================================================================");

        while (ejecutar) {
            try {
                System.out.println("\n💼 === PANEL DE CONTROL PRINCIPAL ===");
                System.out.println("1. 📝 Registrar Nuevo Empleado (Persistencia en MySQL)");
                System.out.println("2. 📊 Procesar Liquidación Masiva y Generar Reporte Polimórfico");
                System.out.println("3. 🧪 Simular Prueba Defensiva (Validación de Datos Negativos)");
                System.out.println("4. 🖥️  Verificar Estado de Infraestructura y Entorno Local");
                System.out.println("5. ❌ Salir del Sistema");
                System.out.print("👉 Seleccione una función del sistema: ");

                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer de entrada

                switch (opcion) {
                    case 1 -> registrarEmpleadoMenu();
                    case 2 -> {
                        System.out.println("\n⏳ Extrayendo tuplas desde MySQL y ejecutando cálculos en tiempo real...");
                        servicioNomina.procesarNominaCompleta();
                    }
                    case 3 -> probarValidacionDefensiva();
                    case 4 -> mostrarMetadatosSistema();
                    case 5 -> {
                        System.out.println("\n👋 Cerrando canales de comunicación y saliendo del sistema. ¡Éxito!");
                        ejecutar = false;
                    }
                    default -> System.out.println("⚠️ Opción no válida. Ingrese un dígito entre 1 y 5.");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error en el flujo de entrada del menú. Reiniciando...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /**
     * Submenú dinámico encargado de capturar y construir instancias del modelo de dominio.
     */
    private static void registrarEmpleadoMenu() {
        System.out.println("\n--- 📝 FORMULARIO DE REGISTRO DE EMPLEADOS ---");
        System.out.print("Nombre Completo: ");
        String nombre = scanner.nextLine();

        System.out.println("Seleccione el Tipo de Contrato:");
        System.out.println("  1. Asalariado Fijo (Planta Permanente)");
        System.out.println("  2. Por Horas (Con recargos de horas extras)");
        System.out.println("  3. Por Comisión (Sueldo base + incentivo de ventas)");
        System.out.println("  4. Temporal (Término fijo sin beneficios adicionales)");
        System.out.print("👉 Opción de contrato: ");
        int tipo = scanner.nextInt();

        System.out.print("¿Hace cuántos años ingresó a la empresa? (0 para ingreso hoy): ");
        int aniosAntiguedad = scanner.nextInt();
        LocalDate fechaIngreso = LocalDate.now().minusYears(aniosAntiguedad);

        Empleado nuevoEmpleado = null;

        // Construcción polimórfica según la selección del usuario
        switch (tipo) {
            case 1 -> {
                System.out.print("Monto del Salario Mensual Fijo: $");
                double salario = scanner.nextDouble();
                // El ID se autogenera en la DB, pasamos 0 provisionalmente
                nuevoEmpleado = new EmpleadoAsalariado(0, nombre, fechaIngreso, salario);
            }
            case 2 -> {
                System.out.print("Tarifa pactada por hora ordinaria: $");
                double tarifa = scanner.nextDouble();
                System.out.print("Número de horas totales laboradas en el mes: ");
                int horas = scanner.nextInt();
                System.out.print("¿Autoriza descuento para el Fondo de Ahorro corporativo? (true/false): ");
                boolean fondo = scanner.nextBoolean();
                nuevoEmpleado = new EmpleadoPorHoras(0, nombre, fechaIngreso, tarifa, horas, fondo);
            }
            case 3 -> {
                System.out.print("Monto del Salario Base: $");
                double base = scanner.nextDouble();
                System.out.print("Porcentaje acordado de Comisión (ej: 5.5): ");
                double porcentaje = scanner.nextDouble();
                System.out.print("Volumen bruto de ventas alcanzado en el mes: $");
                double ventas = scanner.nextDouble();
                nuevoEmpleado = new EmpleadoComision(0, nombre, fechaIngreso, base, porcentaje, ventas);
            }
            case 4 -> {
                System.out.print("Monto del Salario Mensual Pactado: $");
                double salario = scanner.nextDouble();
                nuevoEmpleado = new EmpleadoTemporal(0, nombre, fechaIngreso, salario);
            }
            default -> {
                System.out.println("⚠️ Tipo de contrato inexistente. Registro cancelado.");
                return;
            }
        }

        // Delegar la persistencia segura a la capa de infraestructura
        try {
            repositorio.guardar(nuevoEmpleado);
        } catch (ValidacionNominaException e) {
            System.err.println("❌ Fallo en la capa de datos: " + e.getMessage());
        }
    }

    /**
     * Demostración en vivo de cómo el sistema detiene datos menores a cero.
     */
    private static void probarValidacionDefensiva() {
        System.out.println("\n🔬 [Simulación] Intentando instanciar un Empleado por Horas con -15 horas trabajadas...");
        try {
            // Esto forzará la activación instantánea de DatoNegativoException
            new EmpleadoPorHoras(99, "Usuario Test Fallo", LocalDate.now(), 25000, -15, false);
        } catch (ValidacionNominaException e) {
            System.out.println("🛡️  ¡ÉXITO! La arquitectura defensiva interceptó el fraude:");
            System.err.println("👉 Mensaje capturado: " + e.getMessage());
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
        System.out.println("-------------------------------------------------------------------------");

    }

    /**
     * Diagnóstico rápido de la infraestructura de ejecución.
     */
    private static void mostrarMetadatosSistema() {
        System.out.println("\n💻 === DIAGNÓSTICO DE COMPONENTES DEL ENTORNO ===");
        System.out.println("⚙️  Motor de Ejecución Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        System.out.println("📂 Gestor de Construcción: Apache Maven Integrado Globalmente");
        System.out.println("🗄️  Persistencia Activa: Servidor MySQL Relacional local");
        System.out.println("🎯 Suite de Calidad: JUnit 5 Jupiter configurado en entorno /test");
        System.out.println("🧬 Estado general de acoplamiento: Débil (Basado en SOLID)");
    }
}