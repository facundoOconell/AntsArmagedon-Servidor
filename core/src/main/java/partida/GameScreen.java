package partida;

import Fisicas.Fisica;
import Fisicas.MapaServidor;
import Gameplay.Gestores.Logicos.*;
import com.badlogic.gdx.Screen;
import com.principal.AntsArmageddon;
import com.principal.Jugador;
import entidades.personajes.Personaje;
import entidades.personajes.tiposPersonajes.HormigaExploradora;
import entidades.personajes.tiposPersonajes.HormigaGuerrera;
import entidades.personajes.tiposPersonajes.HormigaObrera;
import network.GameController;
import network.ServerThread;
import java.util.ArrayList;
import java.util.List;

public final class GameScreen implements Screen, GameController {

    private final AntsArmageddon juego;
    private ConfiguracionPartida configuracion;

    private ServerThread serverThread;
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

        serverThread = new ServerThread(this);
        serverThread.start();

        System.out.println("[Servidor] GameScreen iniciada. Esperando jugadores...");
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
        switch (tipo) {
            case "Cuadro_HO_Up":
                return new HormigaObrera(col, proy, 0, 0, idJugador);
            case "Cuadro_HG_Up":
                return new HormigaGuerrera(col, proy, 0, 0, idJugador);
            case "Cuadro_HE_Up":
                return new HormigaExploradora(col, proy, 0, 0, idJugador);
            default:
                throw new IllegalArgumentException("Tipo de hormiga desconocido: " + tipo);
        }
    }

    private List<Jugador> crearJugadores(GestorColisiones col, GestorProyectiles proy) {
        List<Jugador> jugadores = new ArrayList<>();
        jugadores.add(new Jugador(0, new ArrayList<>()));
        jugadores.add(new Jugador(1, new ArrayList<>()));
        return jugadores;
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
    public void startGame(ConfiguracionPartida configuracionPartida) {
        System.out.println("[Servidor] Partida iniciada con configuración combinada.");
        this.configuracion = configuracionPartida;

        inicializarPartida();

        inicializado = true;

        System.out.println("[Servidor] Mapa: " + configuracion.getIndiceMapa()
            + " | Tiempo por turno: " + configuracion.getTiempoTurno()
            + " | Frecuencia PU: " + configuracion.getFrecuenciaPowerUps());
    }


    @Override
    public void mover(int numPlayer, float dir) {
        gestorJuego.moverPersonaje(numPlayer, dir);
    }

    @Override
    public void saltar(int numPlayer) {
        gestorJuego.saltarPersonaje(numPlayer);
    }

    @Override
    public void apuntar(int numPlayer, int dir) {
        gestorJuego.apuntarPersonaje(numPlayer, dir);
    }

    @Override
    public void disparar(int numPlayer, float angulo, float potencia) {
        gestorJuego.dispararPersonaje(numPlayer, angulo, potencia);
    }

    @Override
    public void cambiarMovimiento(int numPlayer, int weaponIndex) {
        gestorJuego.cambiarMovimientoPersonaje(numPlayer, weaponIndex);
    }

    @Override
    public void usarMovimiento(int numPlayer) {
        gestorJuego.usarMovimientoPersonaje(numPlayer);
    }

    @Override
    public void timeOut() {
        System.out.println("[Servidor] Tiempo de turno agotado — el cambio se manejará automáticamente.");
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (serverThread != null) {
            serverThread.terminate();
            try { serverThread.join(300); } catch (InterruptedException ignored) {}
        }

        //mapa.dispose();
        gestorJuego.dispose();

        for (Jugador j : gestorJuego.getJugadores())
            j.getPersonajes().forEach(Personaje::dispose);

        System.out.println("[Servidor] GameScreen finalizada.");
    }

    private int buscarIndiceTiempo(int tiempo) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_TIEMPO_TURNO.length; i++) {
            if (ConfiguracionPartida.OPCIONES_TIEMPO_TURNO[i] == tiempo) return i;
        }
        return 0;
    }

    private int buscarIndiceFrecuencia(int freq) {
        for (int i = 0; i < ConfiguracionPartida.OPCIONES_FRECUENCIA_PU.length; i++) {
            if (ConfiguracionPartida.OPCIONES_FRECUENCIA_PU[i] == freq) return i;
        }
        return 0;
    }

}
