package entidades.proyectiles;

import Fisicas.Colisionable;
import Fisicas.Fisica;
import Gameplay.Gestores.Logicos.GestorColisiones;
import Gameplay.Gestores.Logicos.GestorFisica;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import entidades.personajes.Personaje;
import utils.Constantes;

public abstract class Proyectil implements Colisionable {

    protected float x, y;
    protected Rectangle hitbox;

    protected GestorColisiones gestorColisiones;
    protected Personaje ejecutor;

    protected boolean activo;
    protected int danio;
    protected Vector2 posAnterior = new Vector2();
    protected Vector2 velocidadVector = new Vector2();
    protected float fuerzaKnockback;

    protected float tiempoTranscurrido = 0f;
    protected boolean impacto = false;

    public Proyectil(float x, float y, float angulo, float velocidad, int danio, float fuerzaKnockback,
                     GestorColisiones gestorColisiones, Personaje ejecutor,
                     float ancho, float alto) {

        this.x = x;
        this.y = y;
        this.danio = Math.min(Math.max(danio, 0), Constantes.DANO_MAXIMO);
        this.fuerzaKnockback = Math.min(Math.max(fuerzaKnockback, 0), Constantes.KNOCKBACK_MAXIMO);
        this.gestorColisiones = gestorColisiones;
        this.ejecutor = ejecutor;

        this.hitbox = new Rectangle(x, y, ancho, alto);
        this.activo = true;

        float vel = Math.min(Math.max(velocidad, 0), Constantes.VEL_MAX_HORIZONTAL);
        this.velocidadVector.x = (float) Math.cos(angulo) * vel * (ejecutor != null ? ejecutor.getDireccionMultiplicador() : 1);
        this.velocidadVector.y = (float) Math.sin(angulo) * vel;
    }

    public void mover(float delta, GestorFisica gestorFisica) {
        if (!activo) return;

        tiempoTranscurrido += delta;
        posAnterior.set(x, y);

        if (tiempoTranscurrido > Constantes.TIEMPO_VIDA_MAX) {
            desactivar();
            return;
        }

        Personaje ignorar = (ejecutor != null && tiempoTranscurrido < Constantes.TIEMPO_GRACIA)
            ? ejecutor : null;

        Vector2 nuevaPos = gestorFisica.moverProyectilConRaycast(this, delta, ignorar);
        setPosicion(nuevaPos.x, nuevaPos.y);
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        updateHitbox();
    }

    public void aplicarFisica(float delta, Fisica fisica) {
        fisica.aplicarGravedad(velocidadVector, delta);

        if (velocidadVector.x > Constantes.VEL_MAX_HORIZONTAL)
            velocidadVector.x = Constantes.VEL_MAX_HORIZONTAL;
        else if (velocidadVector.x < -Constantes.VEL_MAX_HORIZONTAL)
            velocidadVector.x = -Constantes.VEL_MAX_HORIZONTAL;

        if (velocidadVector.y > Constantes.VEL_MAX_VERTICAL)
            velocidadVector.y = Constantes.VEL_MAX_VERTICAL;
        else if (velocidadVector.y < -Constantes.VEL_MAX_VERTICAL)
            velocidadVector.y = -Constantes.VEL_MAX_VERTICAL;
    }

    @Override
    public void updateHitbox() {
        hitbox.setPosition(x, y);
    }

    public abstract void impactar(float centroX, float centroY);

    @Override public Rectangle getHitbox() { return hitbox; }
    @Override public Rectangle getHitboxPosicion(float x, float y) { return new Rectangle(x, y, hitbox.getWidth(), hitbox.getHeight()); }
    @Override public void desactivar() { activo = false; }
    @Override public boolean getActivo() { return activo; }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float nuevaX) { this.x = nuevaX; }
    public void setY(float nuevaY) { this.y = nuevaY; }
    public Vector2 getVelocidadVector() { return velocidadVector; }
    public boolean getImpacto() { return impacto; }
    public void setImpacto(boolean v) { impacto = v; }
    public int getDanio() { return danio; }
    public float getFuerzaKnockback() { return fuerzaKnockback; }
    public float getTiempoTranscurrido() { return tiempoTranscurrido; }

    public final void dispose() { }
}
