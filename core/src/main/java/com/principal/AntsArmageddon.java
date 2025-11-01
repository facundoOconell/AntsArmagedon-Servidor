package com.principal;

import com.badlogic.gdx.Game;
import partida.GameScreen;
import partida.ConfiguracionPartida;

public class AntsArmageddon extends Game {

    @Override
    public void create() {

        /*
        Hacer que de alguna forma, la configuracion partida
        sea algo elegido por los jugadores
         */

        ConfiguracionPartida config = new ConfiguracionPartida();

        setScreen(new GameScreen(this, config));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
