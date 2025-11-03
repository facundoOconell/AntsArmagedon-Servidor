package com.principal;

import com.badlogic.gdx.Game;
import network.LobbyScreenServer;
import partida.GameScreen;
import partida.ConfiguracionPartida;

public class AntsArmageddon extends Game {

    @Override
    public void create() {
        ConfiguracionPartida config = new ConfiguracionPartida();
        setScreen(new LobbyScreenServer(this, config));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
