package cl.duoc.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public final class Validador {

    private Validador() {
    }

    public static String texto(String valor, String campo, int largoMaximo) {
        String limpio = valor == null ? "" : valor.trim();

        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" es obligatorio.");
        }
        if (limpio.length() > largoMaximo) {
            throw new IllegalArgumentException(
                    "El campo \"" + campo + "\" admite como máximo " + largoMaximo + " caracteres.");
        }
        return limpio;
    }

    public static String nombre(String valor, String campo, int largoMaximo) {
        String limpio = texto(valor, campo, largoMaximo);

        if (!limpio.matches("[\\p{L} .'-]+")) {
            throw new IllegalArgumentException(
                    "El campo \"" + campo + "\" solo puede contener letras y espacios.");
        }
        return limpio;
    }

    public static String direccion(String valor) {
        String limpio = texto(valor, "Dirección", 100);

        if (limpio.length() < 5) {
            throw new IllegalArgumentException("La dirección es demasiado corta (mínimo 5 caracteres).");
        }
        return limpio;
    }

    public static LocalDate fecha(String valor) {
        String limpio = texto(valor, "Fecha", 10);
        try {
            return LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La fecha debe tener el formato AAAA-MM-DD (por ejemplo: 2026-10-04).");
        }
    }

    public static LocalTime hora(String valor) {
        String limpio = texto(valor, "Hora", 8);
        try {
            return LocalTime.parse(limpio).withNano(0);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La hora debe tener el formato HH:MM en 24 horas (por ejemplo: 14:30).");
        }
    }

    public static <T> T seleccion(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("Debe seleccionar un " + campo + ".");
        }
        return valor;
    }
}