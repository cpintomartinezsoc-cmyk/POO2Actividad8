package cl.duoc.vistas;

import cl.duoc.dao.RepartidorDAO;
import cl.duoc.modelo.Repartidor;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PanelRepartidores extends JPanel {

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private final JTextField txtNombre = new JTextField(20);

    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    private List<Repartidor> repartidores = new ArrayList<>();

    private Runnable alCambiarDatos = () -> { };

    public PanelRepartidores() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        formulario.add(btnCrear);
        formulario.add(btnActualizar);
        formulario.add(btnEliminar);
        formulario.add(btnLimpiar);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnCrear.addActionListener(e -> crear());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                txtNombre.setText(repartidores.get(fila).getNombre());
            }
        });

        cargarTabla();
    }

    public void setAlCambiarDatos(Runnable alCambiarDatos) {
        this.alCambiarDatos = alCambiarDatos;
    }

    public void cargarTabla() {
        modelo.setRowCount(0);
        try {
            repartidores = repartidorDAO.readAll();
            for (Repartidor r : repartidores) {
                modelo.addRow(new Object[]{r.getId(), r.getNombre()});
            }
        } catch (SQLException e) {
            repartidores = new ArrayList<>();
            Mensajes.errorBD(this, "cargar los repartidores", e);
        }
    }

    private void crear() {
        try {
            String nombre = Validador.nombre(txtNombre.getText(), "Nombre", 100);
            Repartidor repartidor = new Repartidor(nombre);

            if (repartidorDAO.create(repartidor)) {
                Mensajes.exito(this, "Repartidor registrado con ID " + repartidor.getId() + ".");
                despuesDeCambio();
            }
        } catch (IllegalArgumentException e) {
            Mensajes.advertencia(this, e.getMessage());
        } catch (SQLException e) {
            Mensajes.errorBD(this, "registrar el repartidor", e);
        }
    }

    private void actualizar() {
        Repartidor seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            String nombre = Validador.nombre(txtNombre.getText(), "Nombre", 100);
            Repartidor editado = new Repartidor(seleccionado.getId(), nombre);

            if (repartidorDAO.update(editado)) {
                Mensajes.exito(this, "Repartidor actualizado correctamente.");
                despuesDeCambio();
            }
        } catch (IllegalArgumentException e) {
            Mensajes.advertencia(this, e.getMessage());
        } catch (SQLException e) {
            Mensajes.errorBD(this, "actualizar el repartidor", e);
        }
    }

    private void eliminar() {
        Repartidor seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar al repartidor \"" + seleccionado.getNombre() + "\"?")) {
            return;
        }
        try {
            if (repartidorDAO.delete(seleccionado.getId())) {
                Mensajes.exito(this, "Repartidor eliminado correctamente.");
                despuesDeCambio();
            }
        } catch (SQLException e) {
            Mensajes.errorBD(this, "eliminar el repartidor", e);
        }
    }

    private Repartidor obtenerSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.advertencia(this, "Seleccione un repartidor en la tabla.");
            return null;
        }
        return repartidores.get(fila);
    }

    private void despuesDeCambio() {
        limpiar();
        cargarTabla();
        alCambiarDatos.run();
    }

    private void limpiar() {
        txtNombre.setText("");
        tabla.clearSelection();
    }
}