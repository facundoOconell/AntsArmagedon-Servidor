package Gameplay.Movimientos.Melee;

import Gameplay.Gestores.Logicos.GestorColisiones;
import Gameplay.Movimientos.MovimientoMelee;

public final class Aranazo extends MovimientoMelee {

    public Aranazo(GestorColisiones gestorColisiones) {
        super("Arañazo", 10f, 40f,
            10, 50f, gestorColisiones);
    }
}
