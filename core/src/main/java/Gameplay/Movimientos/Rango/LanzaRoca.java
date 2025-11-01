package Gameplay.Movimientos.Rango;

import Gameplay.Gestores.Logicos.GestorProyectiles;
import Gameplay.Movimientos.MovimientoRango;
import entidades.personajes.Personaje;
import entidades.proyectiles.Proyectil;
import entidades.proyectiles.ProyectilesBalisticos.Roca;

public final class LanzaRoca extends MovimientoRango {

    public LanzaRoca(GestorProyectiles gestorProyectiles) {
        super("Lanza Roca", 550f, gestorProyectiles);
    }

    @Override
    protected Proyectil crearProyectil(float x, float y, float angulo, float velocidad, Personaje ejecutor) {
        return new Roca(x, y, angulo, velocidad, gestorProyectiles.getGestorColisiones(), ejecutor);
    }
}
