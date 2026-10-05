package cl.duoc.modelo;

public class PedidoEncomienda extends Pedido {

    public PedidoEncomienda(int id, String direccion, EstadoPedido estado) {
        super(id, direccion, estado);
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.ENCOMIENDA;
    }

    @Override
    public void mostrarResumen() {
        System.out.println("Pedido de encomienda " + id
                + " | Dirección: " + direccion
                + " | Estado: " + estado);
    }
}