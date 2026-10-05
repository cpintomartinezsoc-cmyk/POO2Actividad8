package cl.duoc.util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;


public final class Mensajes {

    private Mensajes() {
    }

    public static void exito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Revise los datos", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(
                padre, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }

    public static void errorBD(Component padre, String accion, SQLException e) {
        System.err.println("Error SQL (" + e.getErrorCode() + "): " + e.getMessage());
        JOptionPane.showMessageDialog(padre, traducir(accion, e), "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private static String traducir(String accion, SQLException e) {
        switch (e.getErrorCode()) {
            case 1451:
                return "No se puede " + accion + " porque tiene entregas asociadas.\n"
                        + "Elimine primero esas entregas en la pestaña Entregas.";
            case 1452:
                return "El pedido o el repartidor seleccionado ya no existe.\n"
                        + "Actualice los datos e intente nuevamente.";
            case 1045:
                return "Usuario o contraseña de MySQL incorrectos.\n"
                        + "Revise los datos en la clase ConexionDB.";
            case 1049:
                return "La base de datos speedfast_db no existe.\n"
                        + "Ejecute el script SQL en MySQL Workbench.";
            case 1146:
                return "Faltan tablas en la base de datos.\n"
                        + "Ejecute el script SQL en MySQL Workbench.";
            case 1406:
                return "Uno de los datos es demasiado largo para guardarse.";
            default:
                break;
        }

        String mensaje = e.getMessage() != null ? e.getMessage() : "";

        if (mensaje.contains("No suitable driver")) {
            return "Falta el conector JDBC de MySQL en el proyecto (mysql-connector-j).";
        }
        if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
            return "No se pudo conectar con MySQL.\n"
                    + "Verifique que el servidor esté iniciado.";
        }
        return "Ocurrió un error al " + accion + ":\n" + mensaje;
    }
}