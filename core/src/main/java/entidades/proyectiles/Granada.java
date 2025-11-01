package entidades.proyectiles;

import Fisicas.Colisionable;
import Gameplay.Gestores.Logicos.GestorColisiones;
import Gameplay.Gestores.Logicos.GestorFisica;
import com.badlogic.gdx.math.Vector2;
import entidades.personajes.Personaje;
import utils.Constantes;

public class Granada extends Proyectil {

    private float tiempoVida;
    private float tiempoTranscurridoExplosion = 0f;
    private float coeficienteRebote = 0.6f;
    private static final float VELOCIDAD_MINIMA = 5f;

    private int radioDestruccion;
    private int radioExpansion;

    public Granada(float x, float y, float angulo, float velocidad, int danio,
                   float fuerzaKnockback, GestorColisiones gestorColisiones, Personaje ejecutor,
                   int radioDestruccion, int radioExpansion, float ancho, float alto, float tiempoVida) {
        super(x, y, angulo, velocidad, danio, fuerzaKnockback, gestorColisiones, ejecutor, ancho, alto);

        this.radioDestruccion = radioDestruccion;
        this.radioExpansion = radioExpansion;
        this.tiempoVida = tiempoVida;
    }

    @Override
    public final void mover(float delta, GestorFisica gestorFisica) {
        if (!activo) return;

        tiempoTranscurrido += delta;
        tiempoTranscurridoExplosion += delta;

        if (tiempoTranscurridoExplosion >= tiempoVida) {
            explotar();
            return;
        }

        Personaje ignorar = (ejecutor != null && tiempoTranscurrido < Constantes.TIEMPO_GRACIA) ? ejecutor : null;

        gestorFisica.moverGranadaConRaycast(this, delta, ignorar);

        if (velocidadVector.len2() < VELOCIDAD_MINIMA)
            velocidadVector.setZero();

        updateHitbox();
    }

    private void explotar() {

        float centroX = hitbox.x + hitbox.width / 2f;
        float centroY = hitbox.y + hitbox.height / 2f;

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
                    float fuerzaY = dir.y * fuerzaKnockback * factor;

                    personaje.recibirDanio(danioFinal, fuerzaX, fuerzaY);
                }
            } else if (!(c instanceof Personaje)) {
                c.desactivar();
            }
        }

        desactivar();
    }

    private float factorDeDanio(float distancia) {
        if (distancia <= radioDestruccion) return 1f;
        if (distancia >= radioExpansion) return 0f;
        return 1f - (distancia - radioDestruccion) / (radioExpansion - radioDestruccion);
    }

    @Override
    public final void impactar(float centroX, float centroY) { }

    public float getCoeficienteRebote() {
        return this.coeficienteRebote;
    }

}
