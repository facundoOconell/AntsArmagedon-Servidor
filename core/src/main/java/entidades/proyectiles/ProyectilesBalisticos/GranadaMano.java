package entidades.proyectiles.ProyectilesBalisticos;

import Gameplay.Gestores.Logicos.GestorColisiones;
import entidades.personajes.Personaje;
import entidades.proyectiles.Granada;

public final class GranadaMano extends Granada {

    public GranadaMano(float x, float y, float angulo, float velocidadBase,
                       GestorColisiones gestorColisiones, Personaje ejecutor) {
        super(x, y, angulo, velocidadBase, 20, 500f,
            gestorColisiones, ejecutor, 100,
            150, 20f, 20f, 3f);
    }
}
