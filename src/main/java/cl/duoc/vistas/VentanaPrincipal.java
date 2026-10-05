package cl.duoc.vistas;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        super("SpeedFast - Gestión de Pedidos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null);

        PanelRepartidores panelRepartidores = new PanelRepartidores();
        PanelPedidos panelPedidos = new PanelPedidos();
        PanelEntregas panelEntregas = new PanelEntregas();

        panelRepartidores.setAlCambiarDatos(panelEntregas::refrescar);
        panelPedidos.setAlCambiarDatos(panelEntregas::refrescar);

        panelEntregas.setAlCambiarDatos(panelPedidos::cargarTabla);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Repartidores", panelRepartidores);
        pestanas.addTab("Pedidos", panelPedidos);
        pestanas.addTab("Entregas", panelEntregas);

        pestanas.addChangeListener(e -> {
            Component actual = pestanas.getSelectedComponent();
            if (actual == panelRepartidores) {
                panelRepartidores.cargarTabla();
            } else if (actual == panelPedidos) {
                panelPedidos.cargarTabla();
            } else if (actual == panelEntregas) {
                panelEntregas.refrescar();
            }
        });

        add(pestanas);
    }
}