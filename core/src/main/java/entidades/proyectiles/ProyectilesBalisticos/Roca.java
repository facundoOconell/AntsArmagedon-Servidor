package entidades.proyectiles.ProyectilesBalisticos;

import Gameplay.Gestores.Logicos.GestorColisiones;
import entidades.personajes.Personaje;
import entidades.proyectiles.ProyectilExplosivo;

public final class Roca extends ProyectilExplosivo {

    public Roca(float x, float y, float angulo, float velocidadBase,
                GestorColisiones gestorColisiones, Personaje ejecutor) {
        super(x, y, angulo, velocidadBase,
            15, 300f, gestorColisiones,
            ejecutor, 80, 120,
            20f, 20f);
    }
}
