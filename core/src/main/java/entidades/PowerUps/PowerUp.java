package entidades.PowerUps;

import Gameplay.Gestores.Logicos.GestorColisiones;
import com.badlogic.gdx.math.Rectangle;
import entidades.Entidad;
import entidades.personajes.Personaje;

public abstract class PowerUp extends Entidad {

    protected Rectangle areaRecoleccion;
    protected float extraArea = 5f;

    public PowerUp(float x, float y, float ancho, float alto, GestorColisiones gestorColisiones) {
        super(x, y, ancho, alto, gestorColisiones);
        this.areaRecoleccion = new Rectangle();
        actualizarAreaRecoleccion();
    }

    @Override
    public void actualizar(float delta) {
        if (!activo) return;

        updateHitbox();
        actualizarAreaRecoleccion();

        Personaje personaje = gestorColisiones.buscarPersonajeEnArea(areaRecoleccion);
        if (personaje != null) {
            aplicarEfecto(personaje);
            desactivar();
        }
    }

    protected final void actualizarAreaRecoleccion() {
        areaRecoleccion.set(
            x - extraArea, y - extraArea,
            hitbox.width + 2 * extraArea,
            hitbox.height + 2 * extraArea
        );
    }

    public abstract void aplicarEfecto(Personaje personaje);

}
