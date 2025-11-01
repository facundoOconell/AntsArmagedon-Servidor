package entidades.PowerUps;

import Gameplay.Gestores.Logicos.GestorColisiones;
import entidades.personajes.Personaje;

public final class CajaVida extends PowerUp {

    public CajaVida(float x, float y, float ancho, float alto, GestorColisiones gestorColisiones) {
        super(x, y, ancho, alto, gestorColisiones);
    }

    @Override
    public void aplicarEfecto(Personaje personaje) {
        if (!activo) return;
        personaje.aumentarVida(25);
        desactivar();
    }
}
