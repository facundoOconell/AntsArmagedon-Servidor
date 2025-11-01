package Gameplay.Movimientos.Rango;

import Gameplay.Gestores.Logicos.GestorProyectiles;
import Gameplay.Movimientos.MovimientoRango;
import entidades.personajes.Personaje;
import entidades.proyectiles.Proyectil;
import entidades.proyectiles.ProyectilesBalisticos.GranadaMano;

public final class LanzaGranada extends MovimientoRango {

    public LanzaGranada(GestorProyectiles gestorProyectiles) {
        super("Lanza Granada", 550f, gestorProyectiles);
    }

    @Override
    protected Proyectil crearProyectil(float x, float y, float angulo, float velocidad, Personaje ejecutor) {
        return new GranadaMano(x, y, angulo, velocidad, gestorProyectiles.getGestorColisiones(), ejecutor);
    }
}
