package org.nomina.exception;
//Clase para crear una excepcion especificamente para cuando se ingrese un numero negativo en
//horasTrabajadas. Aplica en EmpleadoPorHoras
public class DatoNegativoException extends ValidacionNominaException {
    public DatoNegativoException(String campo, double valorInvalido) {
        //Mensaje que nos dira si esta excepcion esta aplicando, donde nos dara a saber el campo, y
        //las horas ingresadas de forma incorrecta.
        super(String.format("Error de nomina: El campo '%s' no puede recibir valores negativos. Valor ingresado: [%.2f]", campo, valorInvalido));
    }
}
