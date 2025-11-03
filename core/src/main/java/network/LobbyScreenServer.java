package network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.principal.AntsArmageddon;
import partida.ConfiguracionPartida;
import partida.GameScreen;

public class LobbyScreenServer extends ScreenAdapter {

    private final AntsArmageddon juego;
    private final ConfiguracionPartida config;
    private final Stage stage;
    private final Label label;
    private ServerThread serverThread;
    private boolean partidaIniciada = false;

    public LobbyScreenServer(AntsArmageddon juego, ConfiguracionPartida config) {
        this.juego = juego;
        this.config = config;
        this.stage = new Stage(new ScreenViewport());

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = new BitmapFont();
        estilo.fontColor = Color.WHITE;

        label = new Label("Esperando jugadores para iniciar la partida...", estilo);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(label).center();

        stage.addActor(table);
    }

    @Override
    public void show() {
        System.out.println("[Servidor] Esperando jugadores...");
        serverThread = new ServerThread(new ServerGameController());
        serverThread.start();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    public void actualizarTexto(String texto) {
        label.setText(texto);
    }

    @Override
    public void dispose() {
        if (serverThread != null) {
            serverThread.terminate();
        }
        stage.dispose();
    }

    private class ServerGameController implements GameController {

        @Override
        public void startGame(ConfiguracionPartida finalConfig) {
            if (!partidaIniciada) {
                partidaIniciada = true;
                Gdx.app.postRunnable(() -> {
                    System.out.println("[Servidor] Ambos jugadores listos. Iniciando partida...");
                    juego.setScreen(new GameScreen(juego, finalConfig));
                });
            }
        }

        @Override public void mover(int n, float d) {}
        @Override public void saltar(int n) {}
        @Override public void apuntar(int n, int d) {}
        @Override public void disparar(int n, float a, float p) {}
        @Override public void cambiarMovimiento(int n, int w) {}
        @Override public void usarMovimiento(int n) {}
        @Override public void timeOut() {}

        @Override
        public void sendToAll(String message) {

        }

        @Override
        public void sendToPlayer(int numJugador, String message) {

        }
    }
}
