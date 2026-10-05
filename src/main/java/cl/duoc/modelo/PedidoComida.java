package cl.duoc.modelo;

public class PedidoComida extends Pedido {

    public PedidoComida(int id, String direccion, EstadoPedido estado) {
        super(id, direccion, estado);
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.COMIDA;
    }

    @Override
    public void mostrarResumen() {
        System.out.println("Pedido de comida " + id
                + " | Dirección: " + direccion
                + " | Estado: " + estado);
    }
}