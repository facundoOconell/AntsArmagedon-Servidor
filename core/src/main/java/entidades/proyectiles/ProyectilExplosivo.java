package entidades.proyectiles;

import Fisicas.Colisionable;
import Gameplay.Gestores.Logicos.GestorColisiones;
import Gameplay.Gestores.Logicos.GestorFisica;
import com.badlogic.gdx.math.Vector2;
import entidades.personajes.Personaje;

public abstract class ProyectilExplosivo extends Proyectil {

    protected int radioDestruccion;
    protected int radioExpansion;

    public ProyectilExplosivo(float x, float y, float angulo, float velocidad, int danio,
                              float fuerzaKnockback, GestorColisiones gestorColisiones, Personaje ejecutor,
                              int radioDestruccion, int radioExpansion,
                              float ancho, float alto) {
        super(x, y, angulo, velocidad, danio, fuerzaKnockback, gestorColisiones, ejecutor, ancho, alto);
        this.radioDestruccion = radioDestruccion;
        this.radioExpansion = radioExpansion;
    }

    @Override
    public final void mover(float delta, GestorFisica gestorFisica) {
        if (!activo) return;

        super.mover(delta, gestorFisica);

        if (getImpacto() && activo) {
            impactar(hitbox.x + hitbox.width / 2f, hitbox.y + hitbox.height / 2f);
            setImpacto(false);
        }
    }

    @Override
    public final void impactar(float centroX, float centroY) {

        gestorColisiones.getMapa().destruir(centroX, centroY, radioDestruccion);

        for (Colisionable c : gestorColisiones.getColisionablesRadio(centroX, centroY, radioExpansion)) {
            if (c instanceof Personaje personaje && personaje.getActivo()) {
                float distancia = personaje.distanciaAlCentro(centroX, centroY);
                float factor = (distancia <= radioDestruccion) ? 1f : factorDeDanio(distancia);
                int danioFinal = (int) (danio * factor);

                if (danioFinal > 0) {
                    Vector2 dir = new Vector2(
                        personaje.getX() + personaje.getWidth() / 2f - centroX,
                        personaje.getY() + personaje.getHeight() / 2f - centroY
                    ).nor();

                    float fuerzaX = dir.x * fuerzaKnockback * factor;
                    float fuerzaY = dir.y * fuerzaKnockback * factor * 0.8f;

                    personaje.recibirDanio(danioFinal, fuerzaX, fuerzaY);
                }
            } else if (!(c instanceof Personaje)) {
                c.desactivar();
            }
        }

        desactivar();
    }

    protected float factorDeDanio(float distancia) {
        if (distancia <= radioDestruccion) return 1f;
        if (distancia >= radioExpansion) return 0f;
        return 1f - (distancia - radioDestruccion) / (radioExpansion - radioDestruccion);
    }

}
