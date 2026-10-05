package cl.duoc.modelo;

public class PedidoExpress extends Pedido {

    public PedidoExpress(int id, String direccion, EstadoPedido estado) {
        super(id, direccion, estado);
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.EXPRESS;
    }

    @Override
    public void mostrarResumen() {
        System.out.println("Pedido express " + id
                + " | Dirección: " + direccion
                + " | Estado: " + estado);
    }
}