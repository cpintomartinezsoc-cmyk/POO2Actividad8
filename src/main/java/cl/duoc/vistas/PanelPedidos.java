package cl.duoc.vistas;

import cl.duoc.dao.PedidoDAO;
import cl.duoc.modelo.EstadoPedido;
import cl.duoc.modelo.Pedido;
import cl.duoc.modelo.TipoPedido;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PanelPedidos extends JPanel {

    private static final String TODOS = "TODOS";

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final JTextField txtDireccion = new JTextField(25);
    private final JComboBox<TipoPedido> cmbTipo = new JComboBox<>(TipoPedido.values());
    private final JComboBox<EstadoPedido> cmbEstado = new JComboBox<>(EstadoPedido.values());

    private final JComboBox<Object> cmbFiltroEstado = new JComboBox<>();
    private final JComboBox<Object> cmbFiltroTipo = new JComboBox<>();

    private final DefaultTableModel modelo =
            new DefaultTableModel(new String[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
    private final JTable tabla = new JTable(modelo);

    private List<Pedido> pedidos = new ArrayList<>();

    private Runnable alCambiarDatos = () -> { };

    public PanelPedidos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Dirección:"));
        formulario.add(txtDireccion);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(cmbTipo);
        formulario.add(new JLabel("Estado:"));
        formulario.add(cmbEstado);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        botones.add(btnCrear);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        cmbFiltroEstado.addItem(TODOS);
        for (EstadoPedido estado : EstadoPedido.values()) {
            cmbFiltroEstado.addItem(estado);
        }
        cmbFiltroTipo.addItem(TODOS);
        for (TipoPedido tipo : TipoPedido.values()) {
            cmbFiltroTipo.addItem(tipo);
        }

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Filtrar por estado:"));
        filtros.add(cmbFiltroEstado);
        filtros.add(new JLabel("Filtrar por tipo:"));
        filtros.add(cmbFiltroTipo);

        JPanel norte = new JPanel(new GridLayout(3, 1));
        norte.add(formulario);
        norte.add(botones);
        norte.add(filtros);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnCrear.addActionListener(e -> crear());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        cmbFiltroEstado.addActionListener(e -> cargarTabla());
        cmbFiltroTipo.addActionListener(e -> cargarTabla());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                Pedido pedido = pedidos.get(fila);
                txtDireccion.setText(pedido.getDireccion());
                cmbTipo.setSelectedItem(pedido.getTipo());
                cmbEstado.setSelectedItem(pedido.getEstado());
            }
        });

        cargarTabla();
    }

    public void setAlCambiarDatos(Runnable alCambiarDatos) {
        this.alCambiarDatos = alCambiarDatos;
    }

    public void cargarTabla() {
        modelo.setRowCount(0);

        EstadoPedido filtroEstado = cmbFiltroEstado.getSelectedItem() instanceof EstadoPedido estado ? estado : null;
        TipoPedido filtroTipo = cmbFiltroTipo.getSelectedItem() instanceof TipoPedido tipo ? tipo : null;

        try {
            pedidos = pedidoDAO.readByFiltro(filtroEstado, filtroTipo);
            for (Pedido p : pedidos) {
                modelo.addRow(new Object[]{p.getId(), p.getDireccion(), p.getTipo(), p.getEstado()});
            }
        } catch (SQLException e) {
            pedidos = new ArrayList<>();
            Mensajes.errorBD(this, "cargar los pedidos", e);
        }
    }

    private void crear() {
        try {
            Pedido pedido = leerFormulario(0);

            if (pedidoDAO.create(pedido)) {
                Mensajes.exito(this, "Pedido registrado con ID " + pedido.getId() + ".");
                despuesDeCambio();
            }
        } catch (IllegalArgumentException e) {
            Mensajes.advertencia(this, e.getMessage());
        } catch (SQLException e) {
            Mensajes.errorBD(this, "registrar el pedido", e);
        }
    }

    private void actualizar() {
        Pedido seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            return;
        }
        try {
            Pedido editado = leerFormulario(seleccionado.getId());

            if (pedidoDAO.update(editado)) {
                Mensajes.exito(this, "Pedido actualizado correctamente.");
                despuesDeCambio();
            }
        } catch (IllegalArgumentException e) {
            Mensajes.advertencia(this, e.getMessage());
        } catch (SQLException e) {
            Mensajes.errorBD(this, "actualizar el pedido", e);
        }
    }

    private void eliminar() {
        Pedido seleccionado = obtenerSeleccionado();
        if (seleccionado == null) {
            return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar el pedido " + seleccionado + "?")) {
            return;
        }
        try {
            if (pedidoDAO.delete(seleccionado.getId())) {
                Mensajes.exito(this, "Pedido eliminado correctamente.");
                despuesDeCambio();
            }
        } catch (SQLException e) {
            Mensajes.errorBD(this, "eliminar el pedido", e);
        }
    }

    private Pedido leerFormulario(int id) {
        String direccion = Validador.direccion(txtDireccion.getText());
        TipoPedido tipo = Validador.seleccion((TipoPedido) cmbTipo.getSelectedItem(), "tipo");
        EstadoPedido estado = Validador.seleccion((EstadoPedido) cmbEstado.getSelectedItem(), "estado");
        return Pedido.crear(id, direccion, tipo, estado);
    }

    private Pedido obtenerSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.advertencia(this, "Seleccione un pedido en la tabla.");
            return null;
        }
        return pedidos.get(fila);
    }

    private void despuesDeCambio() {
        limpiar();
        cargarTabla();
        alCambiarDatos.run();
    }

    private void limpiar() {
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }
}