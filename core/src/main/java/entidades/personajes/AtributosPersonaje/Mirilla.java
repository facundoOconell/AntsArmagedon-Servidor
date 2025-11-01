package entidades.personajes.AtributosPersonaje;

import entidades.personajes.Personaje;

public final class Mirilla {

    private static final float RADIO_MIRA = 50f;
    private static final float VELOCIDAD_MIRA = 2f;
    private static final float ANGULO_MIN = 90f;
    private static final float ANGULO_MAX = 270f;

    private float x, y;
    private float angulo;
    private float anguloRad;
    private final Personaje personaje;
    private boolean visible = false;

    public Mirilla(Personaje personaje) {
        this.personaje = personaje;
        this.angulo = personaje.getDireccion() ? 0 : 180;
        actualizarPosicion();
    }

    public void actualizarPosicion() {
        float poscX = personaje.getX();
        float poscY = personaje.getY();

        anguloRad = (float) Math.toRadians(angulo);

        float distanciaX = (float) Math.cos(anguloRad) * RADIO_MIRA * personaje.getDireccionMultiplicador();
        float distanciaY = (float) Math.sin(anguloRad) * RADIO_MIRA;

        this.x = poscX + distanciaX;
        this.y = poscY + distanciaY;
    }

    public void cambiarAngulo(int direccion) {
        angulo += direccion * VELOCIDAD_MIRA;
        if (angulo > ANGULO_MAX) angulo = ANGULO_MAX;
        else if (angulo < ANGULO_MIN) angulo = ANGULO_MIN;
        actualizarPosicion();
    }

    public void update(float delta) {
        if (!visible) return;
        actualizarPosicion();
    }

    public void mostrarMirilla() { this.visible = true; }
    public void ocultarMirilla() { this.visible = false; }

    public float getAnguloRad() { return this.anguloRad; }
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public boolean isVisible() { return this.visible; }
}
