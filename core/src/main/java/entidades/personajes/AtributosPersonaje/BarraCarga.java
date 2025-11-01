package entidades.personajes.AtributosPersonaje;

public final class BarraCarga {

    private float cargaActual = 0f;
    private final float CARGA_MAXIMA = 1f;
    private final float TIEMPO_CARGA = 1.5f;
    private final float VELOCIDAD_CARGA = CARGA_MAXIMA / TIEMPO_CARGA;
    private boolean cargando = false;

    public BarraCarga() {}

    public void update(float delta) {
        if (cargando) {
            cargaActual += VELOCIDAD_CARGA * delta;
            if (cargaActual > CARGA_MAXIMA) cargaActual = CARGA_MAXIMA;
        }
    }

    public void reset() {
        cargaActual = 0f;
        cargando = false;
    }

    public float getCargaNormalizada() { return this.cargaActual / CARGA_MAXIMA; }
    public void start() { this.cargando = true; }
    public void stop() { this.cargando = false; }
    public float getCargaActual() { return this.cargaActual; }
    public boolean isCargando() { return this.cargando; }
}
