package cl.duoc.vistas;

import cl.duoc.dao.EntregaDAO;
import cl.duoc.dao.PedidoDAO;
import cl.duoc.dao.RepartidorDAO;
import cl.duoc.modelo.Entrega;
import cl.duoc.modelo.EstadoPedido;
import cl.duoc.modelo.Pedido;
import cl.duoc.modelo.Repartidor;
import cl.duoc.tareas.TareaEntrega;
import cl.duoc.util.Combos;
import cl.duoc.util.Mensajes;
import cl.duoc.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class PanelEntregas extends JPanel {

    private static final String TODOS = "Todos";
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private final JComboBox<Pedido> cmbPedido = new JComboBox<>();
    private final JComboBox<Repartidor> cmbRepartidor = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(10);
    private final JTextField txtHora = new JTextField(6);

    private final JComboBox<Object> cmbFiltroPedido = new JComboBox<>();
    private final JComboBox<Object> cmbFiltroRepartidor = new JComboBox<>();

    private final DefaultTableModel modelo =
            new DefaultTableModel(new String[]{"ID", "Pedido", "Repartidor", "Fecha", "Hora"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
    private final JTable tabla = new JTable(modelo);

    private List<Entrega> entregas = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    private List<Repartidor> repartidores = new ArrayList<>();

    private boolean cargandoCombos = false;

    private Runnable alCambiarDatos = () -> { };

    public PanelEntregas() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Pedido:"));
        fila1.add(cmbPedido);
        fila1.add(new JLabel("Repartidor:"));
        fila1.add(cmbRepartidor);

        JPanel filaFecha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaFecha.add(new JLabel("Fecha (AAAA-MM-DD):"));
        filaFecha.add(txtFecha);
        filaFecha.add(new JLabel("Hora (HH:MM):"));
        filaFecha.add(txtHora);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnSimular = new JButton("Simular reparto");
        fila2.add(btnCrear);
        fila2.add(btnActualizar);
        fila2.add(btnEliminar);
        fila2.add(btnLimpiar);
        fila2.add(btnSimular);

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila3.add(new JLabel("Filtrar por pedido:"));
        fila3.add(cmbFiltroPedido);
        fila3.add(new JLabel("Filtrar por repartidor:"));
        fila3.add(cmbFiltroRepartidor);

        JPanel norte = new JPanel(new GridLayout(4, 1));
        norte.add(fila1);
        norte.add(filaFecha);
        norte.add(fila2);
        norte.add(fila3);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnCrear.addActionListener(e -> crear());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnSimular.addActionListener(e -> simularReparto());

        cmbFiltroPedido.addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTabla();
            }
        });
        cmbFiltroRepartidor.addActionListener(e -> {
            if (!cargandoCombos) {
                cargarTabla();
            }
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                Entrega entrega = entregas.get(fila);
                Combos.seleccionarPorId(cmbPedido, entrega.getIdPedido());
                Combos.seleccionarPorId(cmbRepartidor, entrega.getIdRepartidor());
                txtFecha.setText(entrega.getFecha() != null ? entrega.getFecha().toString() : "");
                txtHora.setText(entrega.getHora() != null ? entrega.getHora().format(FORMATO_HORA) : "");
            }
        });

        ponerFechaHoraActual();
        refrescar();
    }

    public void setAlCambiarDatos(Runnable alCambiarDatos) {
        this.alCambiarDatos = alCambiarDatos;
    }

    public void refrescar() {
        recargarCombos();
        cargarTabla();
    }


    public void recargarCombos() {
        Integer pedidoElegido = Combos.idSeleccionado(cmbPedido);
        Integer repartidorElegido = Combos.idSeleccionado(cmbRepartidor);
        Integer filtroPedido = Combos.idSeleccionado(cmbFiltroPedido);
        Integer filtroRepartidor = Combos.idSeleccionado(cmbFiltroRepartidor);

        try {
            pedidos = pedidoDAO.readAll();
            repartidores = repartidorDAO.readAll();
        } catch (SQLException e) {
            pedidos = new ArrayList<>();
            repartidores = new ArrayList<>();
            Mensajes.errorBD(this, "cargar pedidos y repartidores", e);
        }

        cargandoCombos = true;

        cmbPedido.removeAllItems();
        cmbFiltroPedido.removeAllItems();
        cmbFiltroPedido.addItem(TODOS);
        for (Pedido p : pedidos) {
            cmbPedido.addItem(p);
            cmbFiltroPedido.addItem(p);
        }

        cmbRepartidor.removeAllItems();
        cmbFiltroRepartidor.removeAllItems();
        cmbFiltroRepartidor.addItem(TODOS);
        for (Repartidor r : repartidores) {
            cmbRepartidor.addItem(r);
            cmbFiltroRepartidor.addItem(r);
        }

        if (pedidoElegido != null) {
            Combos.seleccionarPorId(cmbPedido, pedidoElegido);
        }
        if (repartidorElegido != null) {
            Combos.seleccionarPorId(cmbRepartidor, repartidorElegido);
        }
        if (filtroPedido != null) {
            Combos.seleccionarPorId(cmbFiltroPedido, filtroPedido);
        }
        if (filtroRepartidor != null) {
            Combos.seleccionarPorId(cmbFiltroRepartidor, filtroRepartidor);
        }

        cargandoCombos = false;
    }

    public void cargarTabla() {
        modelo.setRowCount(0);
        try {
            entregas = entregaDAO.readByFiltro(
                    Combos.idSeleccionado(cmbFiltroPedido),
                    Combos.idSeleccionado(cmbFiltroRepartidor)
            );
            for (Entrega e : entregas) {
                modelo.addRow(new Object[]{
                        e.getId(),
                        e.getIdPedido() + " - " + e.getDireccionPedido(),
                        e.getIdRepartidor() + " - " + e.getNombreRepartidor(),
                        e.getFecha(),
                        e.getHora() != null ? e.getHora().format(FORMATO_HORA) : ""
                });
            }
        } catch (SQLException e) {
            entregas = new ArrayList<>();
            Mensajes.errorBD(this, "cargar las entregas", e);
        }
    }

    private void crear() {
        try {
            Entrega entrega = leerFormulario();

            Pedido pedido = (Pedido) cmbPedido.getSelectedItem();
            if (pedido != null && pedido.getEstado() == EstadoPedido.ENTREGADO) {
                Mensajes.advertencia(this, "El pedido " + pedido + " ya fue entregado.");
                return;
            }

            if (entregaDAO.create(entrega)) {
                Mensajes.exito(this, "Entrega registrada con ID " + entrega.getId() + ".");
                despuesDeCambio();
            }
        } catch (IllegalArgumentException e) {
            Mensajes.advertencia(this, e.getMessage());
        } catch (SQLException e) {
            Mensajes.errorBD(this, "registrar la entrega", e);
        }
    }

    private void actualizar() {
        Entrega seleccionada = obtenerSeleccionada();
        if (seleccionada == null) {
            return;
        }
        try {
            Entrega editada = leerFormulario();
            editada.setId(seleccionada.getId());

            if (entregaDAO.update(editada)) {
                Mensajes.exito(this, "Entrega actualizada correctamente.");
                despuesDeCambio();
            }
        } catch (IllegalArgumentException e) {
            Mensajes.advertencia(this, e.getMessage());
        } catch (SQLException e) {
            Mensajes.errorBD(this, "actualizar la entrega", e);
        }
    }

    private void eliminar() {
        Entrega seleccionada = obtenerSeleccionada();
        if (seleccionada == null) {
            return;
        }
        if (!Mensajes.confirmar(this, "¿Desea eliminar la entrega N° " + seleccionada.getId() + "?")) {
            return;
        }
        try {
            if (entregaDAO.delete(seleccionada.getId())) {
                Mensajes.exito(this, "Entrega eliminada correctamente.");
                despuesDeCambio();
            }
        } catch (SQLException e) {
            Mensajes.errorBD(this, "eliminar la entrega", e);
        }
    }

    private void simularReparto() {
        Entrega seleccionada = obtenerSeleccionada();
        if (seleccionada == null) {
            return;
        }

        Pedido pedido = buscarPedido(seleccionada.getIdPedido());
        if (pedido == null) {
            Mensajes.advertencia(this, "El pedido de esta entrega ya no existe.");
            return;
        }
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            Mensajes.advertencia(this, "Solo se puede iniciar el reparto de pedidos PENDIENTES.\n"
                    + "Estado actual: " + pedido.getEstado());
            return;
        }

        Runnable alActualizar = () -> {
            recargarCombos();
            cargarTabla();
            alCambiarDatos.run();
        };

        new Thread(new TareaEntrega(pedido, seleccionada.getNombreRepartidor(), pedidoDAO, alActualizar)).start();

        Mensajes.exito(this, "Reparto iniciado para el pedido " + pedido
                + ".\nRevise la pestaña Pedidos para ver el cambio de estado.");
    }

    private Entrega leerFormulario() {
        Pedido pedido = Validador.seleccion((Pedido) cmbPedido.getSelectedItem(), "pedido");
        Repartidor repartidor = Validador.seleccion((Repartidor) cmbRepartidor.getSelectedItem(), "repartidor");
        LocalDate fecha = Validador.fecha(txtFecha.getText());
        LocalTime hora = Validador.hora(txtHora.getText());

        return new Entrega(pedido.getId(), repartidor.getId(), fecha, hora);
    }

    private Pedido buscarPedido(int id) {
        for (Pedido p : pedidos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private Entrega obtenerSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Mensajes.advertencia(this, "Seleccione una entrega en la tabla.");
            return null;
        }
        return entregas.get(fila);
    }

    private void despuesDeCambio() {
        limpiar();
        cargarTabla();
    }

    private void limpiar() {
        tabla.clearSelection();
        if (cmbPedido.getItemCount() > 0) {
            cmbPedido.setSelectedIndex(0);
        }
        if (cmbRepartidor.getItemCount() > 0) {
            cmbRepartidor.setSelectedIndex(0);
        }
        ponerFechaHoraActual();
    }

    private void ponerFechaHoraActual() {
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText(LocalTime.now().format(FORMATO_HORA));
    }
}