package Gameplay.Gestores.Logicos;

import Fisicas.Fisica;
import Fisicas.MapaServidor;
import Gameplay.Gestores.GestorTurno;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.principal.Jugador;
import entidades.Entidad;
import entidades.PowerUps.CajaVida;
import entidades.PowerUps.PowerUp;
import entidades.personajes.Personaje;
import java.util.ArrayList;
import java.util.List;

public final class GestorJuego {

    private final List<Jugador> jugadores = new ArrayList<>();
    private final GestorColisiones gestorColisiones;
    private final GestorProyectiles gestorProyectiles;
    private final GestorEntidades gestorEntidades;
    private final GestorFisica gestorFisica;
    private final GestorTurno gestorTurno;
    private final GestorSpawnServidor gestorSpawn;

    private int turnosCompletados;
    private final int frecuenciaPowerUps;

    public GestorJuego(List<Jugador> jugadores,
                       GestorColisiones gestorColisiones,
                       GestorProyectiles gestorProyectiles,
                       GestorSpawnServidor gestorSpawn,
                       Fisica fisica,
                       int tiempoPorTurno,
                       int frecuenciaPowerUps) {

        this.jugadores.addAll(jugadores);
        this.gestorColisiones = gestorColisiones;
        this.gestorProyectiles = gestorProyectiles;
        this.gestorSpawn = gestorSpawn;
        this.frecuenciaPowerUps = frecuenciaPowerUps;

        this.gestorTurno = new GestorTurno(new ArrayList<>(jugadores), tiempoPorTurno);
        this.gestorFisica = new GestorFisica(fisica, gestorColisiones);
        this.gestorEntidades = new GestorEntidades(gestorFisica, gestorColisiones);

        for (Jugador jugador : this.jugadores) {
            for (Personaje personaje : jugador.getPersonajes()) {
                this.gestorEntidades.agregarEntidad(personaje);
            }
        }
    }

    public void actualizar(float delta, MapaServidor mapa) {
        int turnoAntes = gestorTurno.getTurnoActual();
        gestorTurno.correrContador(delta);
        revisarPersonajesMuertos();
        gestorEntidades.actualizar(delta);
        gestorProyectiles.actualizar(delta);

        int turnoActual = gestorTurno.getTurnoActual();
        if (turnoActual != turnoAntes) {
            turnosCompletados++;
            cambiarTurno(turnoAntes, turnoActual);
        }
    }

    private void cambiarTurno(int anterior, int actual) {
        Jugador jugadorAnterior = jugadores.get(anterior);
        jugadorAnterior.getPersonajeActivo().setEnTurno(false);

        Jugador jugadorActual = jugadores.get(actual);
        jugadorActual.getPersonajeActivo().setEnTurno(true);

        if (turnosCompletados > 0 && turnosCompletados % frecuenciaPowerUps == 0) {
            generarPowerUp();
        }
    }

    private void revisarPersonajesMuertos() {
        List<Jugador> eliminados = new ArrayList<>();

        for (Jugador jugador : jugadores) {
            jugador.getPersonajes().removeIf(p -> !p.getActivo());
            if (!jugador.estaVivo()) eliminados.add(jugador);
        }

        jugadores.removeAll(eliminados);

        if (jugadores.size() <= 1) indicarGanador();
    }

    private void indicarGanador() {
        if (jugadores.isEmpty()) {
            System.out.println("Empate");
        } else {
            Jugador ganador = jugadores.get(0);
            System.out.println("Jugador " + (ganador.getIdJugador() + 1) + " gana!");
        }
    }

    private void generarPowerUp() {
        if (gestorSpawn == null) return;

        Vector2 spawn = gestorSpawn.generarSpawnPowerUp(8f);
        if (spawn != null) {
            float ancho = 16f;
            float alto = 16f;

            PowerUp nuevo = new CajaVida(spawn.x, spawn.y, ancho, alto, gestorColisiones);
            agregarEntidad(nuevo);
        }
    }

    public void moverPersonaje(int idJugador, float dir) {
        Jugador jugador = jugadores.get(idJugador - 1);
        Personaje p = jugador.getPersonajeActivo();
        if (p != null && p.getActivo()) {
            p.mover(dir, Gdx.graphics.getDeltaTime());
        }
    }

    public void saltarPersonaje(int idJugador) {
        Jugador jugador = jugadores.get(idJugador - 1);
        Personaje p = jugador.getPersonajeActivo();
        if (p != null && p.getActivo()) {
            p.saltar();
        }
    }

    public void apuntarPersonaje(int idJugador, int dir) {
        Jugador jugador = jugadores.get(idJugador - 1);
        Personaje p = jugador.getPersonajeActivo();
        if (p != null && p.getActivo()) {
            p.apuntar(dir);
        }
    }

    public void dispararPersonaje(int idJugador, float angulo, float potencia) {
        Jugador jugador = jugadores.get(idJugador - 1);
        Personaje p = jugador.getPersonajeActivo();
        if (p != null && p.getActivo()) {
            p.usarMovimiento();
        }
    }

    public void cambiarMovimientoPersonaje(int idJugador, int weaponIndex) {
        Jugador jugador = jugadores.get(idJugador - 1);
        Personaje p = jugador.getPersonajeActivo();
        if (p != null && p.getActivo()) {
            p.setMovimientoSeleccionado(weaponIndex);
        }
    }

    public void usarMovimientoPersonaje(int idJugador) {
        Jugador jugador = jugadores.get(idJugador - 1);
        Personaje p = jugador.getPersonajeActivo();
        if (p != null && p.getActivo()) {
            p.usarMovimiento();
        }
    }

    public void cambiarTurno() {
        gestorTurno.avanzarTurno();
    }

    public void agregarEntidad(Entidad entidad) {
        gestorEntidades.agregarEntidad(entidad);
    }

    public void dispose() {
        gestorProyectiles.dispose();
        gestorEntidades.dispose();
    }

    public Personaje getPersonajeActivo() {
        Jugador jugador = getJugadorActivo();
        return (jugador != null) ? jugador.getPersonajeActivo() : null;
    }

    public Jugador getJugadorActivo() { return gestorTurno.getJugadorActivo(); }
    public int getTurnoActual() { return gestorTurno.getTurnoActual(); }
    public float getTiempoActual() { return gestorTurno.getTiempoActual(); }
    public List<Jugador> getJugadores() { return jugadores; }
    public GestorColisiones getGestorColisiones() { return gestorColisiones; }
    public GestorProyectiles getGestorProyectiles() { return gestorProyectiles; }
    public GestorTurno getGestorTurno() { return gestorTurno; }
}
