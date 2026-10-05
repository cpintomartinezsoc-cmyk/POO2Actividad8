package org.example;

import cl.duoc.conexion.ConexionDB;
import cl.duoc.util.Mensajes;
import cl.duoc.vistas.VentanaPrincipal;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try (Connection conexion = ConexionDB.getConnection()) {
                System.out.println("Conexión exitosa a speedfast_db");
            } catch (SQLException e) {
                Mensajes.errorBD(null, "conectar con la base de datos", e);
                return;
            }

            new VentanaPrincipal().setVisible(true);
        });
    }
}