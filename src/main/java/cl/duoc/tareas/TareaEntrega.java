package cl.duoc.tareas;

import cl.duoc.dao.PedidoDAO;
import cl.duoc.modelo.EstadoPedido;
import cl.duoc.modelo.Pedido;

import javax.swing.SwingUtilities;
import java.sql.SQLException;


public class TareaEntrega implements Runnable {

    private final Pedido pedido;
    private final String nombreRepartidor;
    private final PedidoDAO pedidoDAO;
    private final Runnable alActualizar;

    public TareaEntrega(Pedido pedido, String nombreRepartidor,
                        PedidoDAO pedidoDAO, Runnable alActualizar) {
        this.pedido = pedido;
        this.nombreRepartidor = nombreRepartidor;
        this.pedidoDAO = pedidoDAO;
        this.alActualizar = alActualizar;
    }

    @Override
    public void run() {
        try {
            pedido.despachar(); // estado = EN_REPARTO
            pedidoDAO.updateEstado(pedido.getId(), pedido.getEstado());
            System.out.println(nombreRepartidor + " comenzó la entrega del pedido " + pedido.getId());
            notificarInterfaz();

            Thread.sleep(3000);

            pedido.setEstado(EstadoPedido.ENTREGADO);
            pedidoDAO.updateEstado(pedido.getId(), pedido.getEstado());
            System.out.println(nombreRepartidor + " entregó el pedido " + pedido.getId());
            notificarInterfaz();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado del pedido: " + e.getMessage());
        }
    }

    private void notificarInterfaz() {
        if (alActualizar != null) {
            SwingUtilities.invokeLater(alActualizar);
        }
    }
}