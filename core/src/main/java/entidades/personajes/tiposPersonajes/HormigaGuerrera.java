package entidades.personajes.tiposPersonajes;

import Gameplay.Gestores.Logicos.GestorColisiones;
import Gameplay.Gestores.Logicos.GestorProyectiles;
import Gameplay.Movimientos.Otros.PasarTurno;
import Gameplay.Movimientos.Rango.LanzaGranada;
import Gameplay.Movimientos.Rango.LanzaNectar;
import Gameplay.Movimientos.Rango.LanzaRoca;
import entidades.personajes.Personaje;

public final class HormigaGuerrera extends Personaje {

    public HormigaGuerrera(GestorColisiones gestorColisiones, GestorProyectiles gestorProyectiles,
                           float x, float y, int idJugador) {
        super(gestorColisiones, gestorProyectiles,
            x, y, 120, 150, 400,
            10f, idJugador);
    }

    @Override
    protected void inicializarMovimientos() {
        movimientos.add(new LanzaRoca(gestorProyectiles));
        movimientos.add(new LanzaNectar(gestorProyectiles));
        movimientos.add(new LanzaGranada(gestorProyectiles));
        movimientos.add(new PasarTurno());
    }

}
