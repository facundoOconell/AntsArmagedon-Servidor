package Gameplay.Movimientos;

import entidades.personajes.Personaje;

public abstract class Movimiento {

    protected final String nombre;

    public Movimiento(String nombre) {
        this.nombre = nombre;
    }


    public void ejecutar(Personaje personaje) { }

    public String getNombre() { return nombre; }

    public final void dispose() { }
}
