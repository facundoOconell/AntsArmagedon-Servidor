package com.principal;

import entidades.personajes.Personaje;
import java.util.ArrayList;
import java.util.List;

public final class Jugador {

    private final List<Personaje> personajes;
    private int indiceActivo;
    private boolean jugadorVivo;
    private String colorJugador = "white"; // Representación simple del color
    private int idJugador;

    public Jugador(int idJugador, List<Personaje> personajes) {
        this.personajes = new ArrayList<>(personajes);
        this.indiceActivo = 0;
        this.jugadorVivo = !personajes.isEmpty();
        this.idJugador = idJugador;
    }

    public void removerPersonaje(Personaje personaje) {
        int index = personajes.indexOf(personaje);
        if (index == -1) return;

        personajes.remove(personaje);

        if (index < indiceActivo) indiceActivo--;
        if (indiceActivo >= personajes.size()) indiceActivo = 0;
        if (personajes.isEmpty()) jugadorVivo = false;
    }

    public void avanzarPersonaje() {
        if (personajes.isEmpty()) return;
        indiceActivo = (indiceActivo + 1) % personajes.size();
    }

    public void agregarPersonaje(Personaje nuevoPersonaje) {
        personajes.add(nuevoPersonaje);
        jugadorVivo = true;
    }

    public Personaje getPersonajeActivo() {
        if (personajes.isEmpty()) return null;
        if (indiceActivo >= personajes.size()) indiceActivo = 0;
        return personajes.get(indiceActivo);
    }

   public List<Personaje> getPersonajes() { return this.personajes; }
    public boolean estaVivo() { return this.jugadorVivo; }

    public void setColorJugador(String color) { this.colorJugador = color; }
    public String getColorJugador() { return this.colorJugador; }

    public int getIdJugador() { return this.idJugador; }
}
