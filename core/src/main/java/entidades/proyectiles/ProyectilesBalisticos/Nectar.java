package entidades.proyectiles.ProyectilesBalisticos;

import Gameplay.Gestores.Logicos.GestorColisiones;
import entidades.personajes.Personaje;
import entidades.proyectiles.ProyectilBalistico;

public final class Nectar extends ProyectilBalistico {

    public Nectar(float x, float y, float angulo, float velocidadBase,
                  GestorColisiones gestorColisiones, Personaje ejecutor) {
        super(x, y, angulo, velocidadBase,
            25, 400f, gestorColisiones,
            ejecutor, 20f, 20f);
    }
}
