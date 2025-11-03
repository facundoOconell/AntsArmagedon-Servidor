package network;

import partida.ConfiguracionPartida;
import Gameplay.Gestores.Logicos.GestorJuego;

public final class GameControllerServer implements GameController {

    private final ServerThread serverThread;
    private GestorJuego gestorJuego;

    public GameControllerServer(ServerThread serverThread) {
        this.serverThread = serverThread;
    }

    @Override
    public void startGame(ConfiguracionPartida config) {
        System.out.println("[SERVIDOR] Iniciando partida...");
    }

    @Override
    public void mover(int numPlayer, float dir) {
        if (gestorJuego == null) return;
        gestorJuego.moverPersonaje(numPlayer, dir);
    }

    @Override
    public void saltar(int numPlayer) {
        if (gestorJuego == null) return;
        gestorJuego.saltarPersonaje(numPlayer);
    }

    @Override
    public void apuntar(int numPlayer, int dir) {
        if (gestorJuego == null) return;
        gestorJuego.apuntarPersonaje(numPlayer, dir);
    }

    @Override
    public void disparar(int numPlayer, float angulo, float potencia) {
        gestorJuego.dispararPersonaje(numPlayer, angulo, potencia);

        String mensaje = "Disparo:" + numPlayer + ":" + angulo + ":" + potencia;
        serverThread.sendMessageToAll(mensaje);

        System.out.println("[SERVIDOR] Disparo reenviado a clientes -> " + mensaje);
    }


    @Override
    public void cambiarMovimiento(int numPlayer, int indiceMovimiento) {
        if (gestorJuego == null) return;
        gestorJuego.cambiarMovimientoPersonaje(numPlayer, indiceMovimiento);
    }

    @Override
    public void usarMovimiento(int numPlayer) {
        if (gestorJuego == null) return;
        gestorJuego.usarMovimientoPersonaje(numPlayer);
    }

    @Override
    public void timeOut() {
        gestorJuego.cambiarTurno();

        int turnoActual = gestorJuego.getTurnoActual();
        float tiempoRestante = gestorJuego.getGestorTurno().getTiempoActual();

        String mensaje = "UpdateTurno:" + turnoActual + ":" + tiempoRestante;
        serverThread.sendMessageToAll(mensaje);

        System.out.println("[SERVIDOR] Cambio de turno -> Jugador " + (turnoActual + 1));
    }


    @Override
    public void sendToAll(String message) {
        String msg = GameMessage.of("ServerMsg", message).toPacketString();
        serverThread.sendMessageToAll(msg);
    }


    @Override
    public void sendToPlayer(int numJugador, String message) {
        System.out.println("[SERVIDOR → Jugador" + numJugador + "] " + message);
    }

    public void setGestorJuego(GestorJuego gestorJuego) {
        this.gestorJuego = gestorJuego;
    }

    public GestorJuego getGestorJuego() {
        return gestorJuego;
    }
}
