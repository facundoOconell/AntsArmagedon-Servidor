package Gameplay.Movimientos.Otros;

import Gameplay.Movimientos.Movimiento;
import entidades.personajes.Personaje;

public final class PasarTurno extends Movimiento {

    public PasarTurno() {
        super("Pasar turno");
    }

    @Override
    public void ejecutar(Personaje personaje) {
        if (personaje != null) {
            personaje.terminarTurno();
        }
    }
}
