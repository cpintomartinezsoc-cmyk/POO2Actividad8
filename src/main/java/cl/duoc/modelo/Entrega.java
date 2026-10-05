package cl.duoc.modelo;

import java.time.LocalDate;
import java.time.LocalTime;


public class Entrega {

    private int id;
    private int idPedido;
    private int idRepartidor;
    private LocalDate fecha;
    private LocalTime hora;

    private String direccionPedido;
    private String nombreRepartidor;

    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public Entrega(int id, int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora,
                   String direccionPedido, String nombreRepartidor) {
        this(idPedido, idRepartidor, fecha, hora);
        this.id = id;
        this.direccionPedido = direccionPedido;
        this.nombreRepartidor = nombreRepartidor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getDireccionPedido() {
        return direccionPedido;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }
}