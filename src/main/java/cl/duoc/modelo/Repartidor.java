package cl.duoc.modelo;

import cl.duoc.interfaz.Identificable;

public class Repartidor implements Identificable {

    private int id;
    private String nombre;

    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}