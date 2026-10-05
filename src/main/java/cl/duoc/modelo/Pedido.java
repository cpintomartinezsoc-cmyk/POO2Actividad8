package cl.duoc.modelo;

import cl.duoc.interfaz.Cancelable;
import cl.duoc.interfaz.Despachable;
import cl.duoc.interfaz.Identificable;
import cl.duoc.interfaz.Rastreable;


public abstract class Pedido implements Despachable, Cancelable, Rastreable, Identificable {

    protected int id;
    protected String direccion;
    protected EstadoPedido estado;

    public Pedido(int id, String direccion, EstadoPedido estado) {
        this.id = id;
        this.direccion = direccion;
        this.estado = estado;
    }


    public static Pedido crear(int id, String direccion, TipoPedido tipo, EstadoPedido estado) {
        return switch (tipo) {
            case COMIDA -> new PedidoComida(id, direccion, estado);
            case ENCOMIENDA -> new PedidoEncomienda(id, direccion, estado);
            case EXPRESS -> new PedidoExpress(id, direccion, estado);
        };
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public abstract TipoPedido getTipo();

    public abstract void mostrarResumen();

    @Override
    public void despachar() {
        estado = EstadoPedido.EN_REPARTO;
    }

    @Override
    public void cancelar() {
        estado = EstadoPedido.PENDIENTE;
    }

    @Override
    public void verHistorial() {
        System.out.println("Pedido " + id + " | Tipo: " + getTipo()
                + " | Dirección: " + direccion + " | Estado: " + estado);
    }

    @Override
    public String toString() {
        return id + " - " + direccion;
    }
}