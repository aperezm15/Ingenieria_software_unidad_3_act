package org.nomina.exception;
//Clase para crear nuestra propia excepcion para nuestra nomina
public class ValidacionNominaException extends RuntimeException {
    public ValidacionNominaException(String message) {
        super(message);
    }
}
