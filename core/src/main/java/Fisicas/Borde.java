package Fisicas;

import Gameplay.Gestores.Logicos.GestorColisiones;
import entidades.Limite;
import utils.Constantes;

public final class Borde {

    public static final int GROSOR_BORDE = 10;

    private final Limite limiteSuperior;
    private final Limite limiteInferior;
    private final Limite limiteIzquierdo;
    private final Limite limiteDerecho;

    public Borde(GestorColisiones gestor) {
        final int ancho = Constantes.RESOLUCION_ANCHO_MAPA;
        final int alto  = Constantes.RESOLUCION_ALTO_MAPA;

        limiteSuperior  = new Limite(0, alto, ancho, GROSOR_BORDE);
        limiteInferior  = new Limite(0, -GROSOR_BORDE, ancho, GROSOR_BORDE);
        limiteIzquierdo = new Limite(-GROSOR_BORDE, 0, GROSOR_BORDE, alto);
        limiteDerecho   = new Limite(ancho, 0, GROSOR_BORDE, alto);

        gestor.agregarObjeto(limiteSuperior);
        gestor.agregarObjeto(limiteInferior);
        gestor.agregarObjeto(limiteIzquierdo);
        gestor.agregarObjeto(limiteDerecho);
    }
}
