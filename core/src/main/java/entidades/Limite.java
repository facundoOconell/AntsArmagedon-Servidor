package entidades;

import Fisicas.Colisionable;
import com.badlogic.gdx.math.Rectangle;

public final class Limite implements Colisionable {

    private final Rectangle hitbox;
    private boolean activo = true;

    public Limite(float x, float y, float ancho, float alto) {
        this.hitbox = new Rectangle(x, y, ancho, alto);
    }

    @Override
    public void updateHitbox() {}

    @Override
    public Rectangle getHitbox() {
        return hitbox;
    }

    @Override
    public Rectangle getHitboxPosicion(float x, float y) {
        return new Rectangle(x, y, hitbox.width, hitbox.height);
    }

    @Override
    public void desactivar() {
        activo = false;
    }

    @Override
    public float getX() {
        return hitbox.x;
    }

    @Override
    public float getY() {
        return hitbox.y;
    }

    @Override
    public boolean getActivo() {
        return activo;
    }
}
