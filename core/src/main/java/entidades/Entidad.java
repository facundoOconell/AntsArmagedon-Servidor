package entidades;

import Fisicas.Colisionable;
import Gameplay.Gestores.Logicos.GestorColisiones;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entidad implements Colisionable {

    protected float x, y;
    protected Rectangle hitbox;
    protected boolean sobreAlgo;
    protected boolean activo;
    protected Vector2 velocidad;
    protected GestorColisiones gestorColisiones;

    public Entidad(float x, float y, float ancho, float alto, GestorColisiones gestorColisiones) {
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle(x, y, ancho, alto);
        this.gestorColisiones = gestorColisiones;
        this.velocidad = new Vector2(0, 0);
        this.activo = true;
    }

    @Override
    public void updateHitbox() {
        hitbox.setPosition(x, y);
    }

    @Override
    public void desactivar() { this.activo = false; }

    @Override
    public boolean getActivo() { return this.activo; }

    @Override
    public Rectangle getHitbox() { return this.hitbox; }

    @Override
    public Rectangle getHitboxPosicion(float x, float y) {
        return new Rectangle(x, y, hitbox.getWidth(), hitbox.getHeight());
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        updateHitbox();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }

    public final float getWidth() {
        return this.hitbox.getWidth();
    }

    public final float getHeight() {
        return this.hitbox.getHeight();
    }

    public Vector2 getVelocidad() { return this.velocidad; }
    public void setVelocidad(Vector2 velocidad) { this.velocidad.set(velocidad); }

    public boolean getSobreAlgo() { return this.sobreAlgo; }
    public void setSobreAlgo(boolean sobreAlgo) { this.sobreAlgo = sobreAlgo; }

    public abstract void actualizar(float delta);

    public final void dispose() { }
}
