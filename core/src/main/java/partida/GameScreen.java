package partida;

import Fisicas.Fisica;
import Fisicas.MapaServidor;
import Gameplay.Gestores.Logicos.*;
import com.badlogic.gdx.Screen;
import com.principal.AntsArmageddon;
import com.principal.Jugador;
import entidades.personajes.Personaje;
import entidades.personajes.tiposPersonajes.*;
import java.util.ArrayList;
import java.util.List;

public final class GameScreen implements Screen {

    private final AntsArmageddon juego;
    private ConfiguracionPartida configuracion;

    private GestorJuego gestorJuego;
    private MapaServidor mapa;

    private boolean inicializado = false;
    private int turnoAnterior = -1;

    public GameScreen(AntsArmageddon juego, ConfiguracionPartida configuracion) {
        this.juego = juego;
        this.configuracion = configuracion;
    }

    @Override
    public void show() {
        if (!inicializado) {
            inicializarPartida();
            inicializado = true;
        }
        System.out.println("[Servidor] GameScreen inicializada (modo local, sin red).");
    }

    private void inicializarPartida() {
        configuracion.normalizarEquipos();

        String rutaMapa = "mapas/pruebaMapa1.png";
        mapa = new MapaServidor(rutaMapa);

        GestorColisiones gestorColisiones = new GestorColisiones(mapa);
        Fisica fisica = new Fisica();
        GestorFisica gestorFisica = new GestorFisica(fisica, gestorColisiones);
        GestorProyectiles gestorProyectiles = new GestorProyectiles(gestorColisiones, gestorFisica);
        GestorSpawnServidor gestorSpawn = new GestorSpawnServidor(mapa);

        List<Jugador> jugadores = new ArrayList<>();

        Jugador j1 = new Jugador(0, new ArrayList<>());
        Jugador j2 = new Jugador(1, new ArrayList<>());

        for (String tipo : configuracion.getEquipoJugador1()) {
            j1.agregarPersonaje(crearPersonajeDesdeTipo(tipo, gestorColisiones, gestorProyectiles, j1.getIdJugador()));
        }

        for (String tipo : configuracion.getEquipoJugador2()) {
            j2.agregarPersonaje(crearPersonajeDesdeTipo(tipo, gestorColisiones, gestorProyectiles, j2.getIdJugador()));
        }

        j1.getPersonajes().removeIf(p -> p == null);
        j2.getPersonajes().removeIf(p -> p == null);

        jugadores.add(j1);
        jugadores.add(j2);

        gestorJuego = new GestorJuego(
            jugadores,
            gestorColisiones,
            gestorProyectiles,
            gestorSpawn,
            fisica,
            configuracion.getTiempoTurno(),
            configuracion.getFrecuenciaPowerUps()
        );

        gestorJuego.getGestorTurno().iniciarTurnoActual();

        System.out.println("[Servidor] Partida inicializada correctamente con "
            + j1.getPersonajes().size() + " y " + j2.getPersonajes().size() + " personajes.");
    }

    private Personaje crearPersonajeDesdeTipo(String tipo, GestorColisiones col, GestorProyectiles proy, int idJugador) {
        return switch (tipo) {
            case "Cuadro_HO_Up" -> new HormigaObrera(col, proy, 0, 0, idJugador);
            case "Cuadro_HG_Up" -> new HormigaGuerrera(col, proy, 0, 0, idJugador);
            case "Cuadro_HE_Up" -> new HormigaExploradora(col, proy, 0, 0, idJugador);
            default -> throw new IllegalArgumentException("Tipo de hormiga desconocido: " + tipo);
        };
    }

    @Override
    public void render(float delta) {
        if (!inicializado) return;

        gestorJuego.actualizar(delta, mapa);

        int turnoActual = gestorJuego.getTurnoActual();
        if (turnoActual != turnoAnterior) {
            turnoAnterior = turnoActual;
            System.out.println("[Servidor] Turno cambiado: Jugador " + (turnoActual + 1));
        }
    }

    @Override
    public void dispose() {
        gestorJuego.dispose();
        for (Jugador j : gestorJuego.getJugadores())
            j.getPersonajes().forEach(Personaje::dispose);

        System.out.println("[Servidor] GameScreen finalizada.");
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
