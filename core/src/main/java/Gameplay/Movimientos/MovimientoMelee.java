package Gameplay.Movimientos;

import Fisicas.Colisionable;
import Gameplay.Gestores.Logicos.GestorColisiones;
import com.badlogic.gdx.math.Rectangle;
import entidades.personajes.Personaje;
import java.util.List;

public abstract class MovimientoMelee extends Movimiento {

    protected final float anchoGolpe;
    protected final float altoGolpe;
    protected final int danio;
    protected final float distanciaGolpe;
    protected final GestorColisiones gestorColisiones;

    public MovimientoMelee(String nombre,
                           float anchoGolpe,
                           float altoGolpe,
                           int danio,
                           float distanciaGolpe,
                           GestorColisiones gestorColisiones) {
        super(nombre);
        this.anchoGolpe = anchoGolpe;
        this.altoGolpe = altoGolpe;
        this.danio = danio;
        this.distanciaGolpe = distanciaGolpe;
        this.gestorColisiones = gestorColisiones;
    }

    @Override
    public void ejecutar(Personaje atacante) {
        if (atacante == null || !atacante.getActivo()) return;

        float angulo = atacante.getMirilla().getAnguloRad();
        float dir = atacante.getDireccionMultiplicador();

        float origenX = atacante.getX();
        float origenY = atacante.getY();

        float golpeX = (float) (origenX + Math.cos(angulo) * distanciaGolpe * dir - anchoGolpe / 2f);
        float golpeY = (float) (origenY + Math.sin(angulo) * distanciaGolpe - altoGolpe / 2f);

        Rectangle area = new Rectangle(golpeX, golpeY, anchoGolpe, altoGolpe);
        aplicarGolpe(atacante, area);
    }

    protected void aplicarGolpe(Personaje atacante, Rectangle area) {
        List<Colisionable> colisionados = gestorColisiones.getColisionablesEnRect(area, atacante);
        if (colisionados == null || colisionados.isEmpty()) return;

        for (Colisionable c : colisionados) {
            if (c instanceof Personaje enemigo && enemigo != atacante && enemigo.getActivo()) {
                enemigo.recibirDanio(danio, 1f, 1f);
            }
        }
    }

    public float getDistanciaGolpe() { return distanciaGolpe; }

}
