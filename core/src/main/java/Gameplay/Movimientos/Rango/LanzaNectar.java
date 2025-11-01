package Gameplay.Movimientos.Rango;

import Gameplay.Gestores.Logicos.GestorProyectiles;
import Gameplay.Movimientos.MovimientoRango;
import entidades.personajes.Personaje;
import entidades.proyectiles.Proyectil;
import entidades.proyectiles.ProyectilesBalisticos.Nectar;

public final class LanzaNectar extends MovimientoRango {

    public LanzaNectar(GestorProyectiles gestorProyectiles) {
        super("Lanza Néctar", 550f, gestorProyectiles);
    }

    @Override
    protected Proyectil crearProyectil(float x, float y, float angulo, float velocidad, Personaje ejecutor) {
        return new Nectar(x, y, angulo, velocidad, gestorProyectiles.getGestorColisiones(), ejecutor);
    }
}
