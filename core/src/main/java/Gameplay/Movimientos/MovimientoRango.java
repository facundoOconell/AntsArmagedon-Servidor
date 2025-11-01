package Gameplay.Movimientos;

import Gameplay.Gestores.Logicos.GestorProyectiles;
import entidades.personajes.Personaje;
import entidades.proyectiles.Proyectil;

public abstract class MovimientoRango extends Movimiento {

    protected final float velocidadBase;
    protected final GestorProyectiles gestorProyectiles;

    private static final float OFFSET_DISPARO = 13f;
    private static final float FACTOR_MIN_VEL = 0.5f;
    private static final float FACTOR_MAX_VEL = 2.0f;

    public MovimientoRango(String nombre,
                           float velocidadBase,
                           GestorProyectiles gestorProyectiles) {
        super(nombre);
        this.velocidadBase = velocidadBase;
        this.gestorProyectiles = gestorProyectiles;
    }

    public void ejecutar(Personaje personaje, float potencia) {
        if (personaje == null || !personaje.getActivo()) return;

        float angulo = personaje.getMirilla().getAnguloRad();
        float dir = personaje.getDireccionMultiplicador();

        float posX = personaje.getX();
        float posY = personaje.getY();

        float x = (float) (posX + Math.cos(angulo) * OFFSET_DISPARO * dir);
        float y = (float) (posY + Math.sin(angulo) * OFFSET_DISPARO);

        float factorVelocidad = FACTOR_MIN_VEL + (FACTOR_MAX_VEL - FACTOR_MIN_VEL) * (potencia * potencia);
        float velocidadFinal = velocidadBase * factorVelocidad;

        Proyectil proyectil = crearProyectil(x, y, angulo, velocidadFinal, personaje);
        if (proyectil != null) {
            gestorProyectiles.agregar(proyectil);
        }
    }

    protected abstract Proyectil crearProyectil(float x, float y, float angulo, float velocidad, Personaje ejecutor);

    public float getVelocidadBase() { return velocidadBase; }
}
