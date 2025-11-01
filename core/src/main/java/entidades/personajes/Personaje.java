package entidades.personajes;

import Gameplay.Gestores.Logicos.GestorColisiones;
import Gameplay.Gestores.Logicos.GestorProyectiles;
import Gameplay.Movimientos.Movimiento;
import Gameplay.Movimientos.MovimientoRango;
import entidades.Entidad;
import entidades.personajes.AtributosPersonaje.BarraCarga;
import entidades.personajes.AtributosPersonaje.FisicaPersonaje;
import entidades.personajes.AtributosPersonaje.Mirilla;
import utils.Constantes;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class Personaje extends Entidad {

    public enum Estado {
        IDLE, WALK, JUMP, HIT, MUERTE;
    }

    protected int idJugador;
    protected Estado estadoActual = Estado.IDLE;

    protected GestorProyectiles gestorProyectiles;
    protected Mirilla mirilla;
    protected List<Movimiento> movimientos;
    protected boolean direccion;
    protected int vida;
    protected float velocidadX;
    protected BarraCarga barraCarga;
    protected FisicaPersonaje fisicas;

    protected int movimientoSeleccionado = 0;
    protected boolean estaDisparando = false;

    protected boolean enTurno = false;
    protected boolean turnoTerminado = false;

    protected float fuerzaSalto;
    protected float peso;
    protected float lastX = 0f;

    public Personaje(GestorColisiones gestorColisiones, GestorProyectiles gestorProyectiles,
                     float x, float y, int vida, float velocidadMovimiento,
                     float fuerzaSalto, float peso, int idJugador) {

        /*Dejamos estos valores hardcodeados para rellenar algo en la entidad, preguntarle al profe que hacer
        * Con esto, si dejarlo asi o modificar las clases para que no pidan estos valores vacios.*/

        super(x, y, 50, 50, gestorColisiones);

        this.gestorProyectiles = gestorProyectiles;
        this.vida = Math.min(vida, Constantes.VIDA_MAXIMA);
        this.velocidadX = MathUtils.clamp(velocidadMovimiento, 0, Constantes.VEL_MAX_HORIZONTAL);
        this.fuerzaSalto = MathUtils.clamp(fuerzaSalto, 0, Constantes.VEL_MAX_VERTICAL);
        this.peso = peso;
        this.idJugador = idJugador;
        this.activo = true;

        this.barraCarga = new BarraCarga();
        this.mirilla = new Mirilla(this);
        this.movimientos = new ArrayList<>();
        this.fisicas = new FisicaPersonaje(this, gestorColisiones);

        this.direccion = MathUtils.randomBoolean();
        inicializarMovimientos();

        this.lastX = x;
    }

    protected abstract void inicializarMovimientos();

    @Override
    public final void actualizar(float delta) {
        if (!activo) return;

        fisicas.actualizar(delta);
        mirilla.update(delta);
        mirilla.actualizarPosicion();

        if (estadoActual == Estado.MUERTE) {
            desactivar();
            return;
        }

        if (estadoActual == Estado.HIT && fisicas.estaEnKnockback()) {
            return;
        }

        float dx = Math.abs(getX() - lastX);
        boolean caminando = dx > 0.1f && getSobreAlgo();

        if (vida <= 0) cambiarEstado(Estado.MUERTE);
        else if (!getSobreAlgo()) cambiarEstado(Estado.JUMP);
        else if (caminando) cambiarEstado(Estado.WALK);
        else cambiarEstado(Estado.IDLE);

        if (!enTurno || estadoActual == Estado.MUERTE || estadoActual == Estado.HIT) {
            ocultarMirilla();
        } else if (estaDisparando) {
            mostrarMirilla();
        } else if (!caminando && getSobreAlgo()) {
            mostrarMirilla();
        } else {
            ocultarMirilla();
        }

        lastX = getX();
    }

    protected final void cambiarEstado(Estado nuevoEstado) {
        if (estadoActual != nuevoEstado) {
            estadoActual = nuevoEstado;
        }
    }

    public final void mover(float deltaX, float deltaTiempo) {
        if (!puedeActuar()) return;
        fisicas.moverHorizontal(deltaX, deltaTiempo);
    }

    public final void saltar() {
        if (!puedeActuar()) return;
        fisicas.saltar(fuerzaSalto);
    }

    public final void apuntar(int direccion) {
        if (!getSobreAlgo()) return;
        mirilla.mostrarMirilla();
        mirilla.cambiarAngulo(direccion);
    }

    public final void usarMovimiento() {
        if (!getSobreAlgo() || !activo) return;

        Movimiento movimiento = getMovimientoSeleccionado();
        if (movimiento == null) return;

        if (movimiento instanceof MovimientoRango movimientoRango) {
            if (estaDisparando) {
                float potencia = barraCarga.getCargaNormalizada();
                if (potencia > 0f) movimientoRango.ejecutar(this, potencia);

                barraCarga.reset();
                estaDisparando = false;
                terminarTurno();
                return;
            }

            barraCarga.start();
            estaDisparando = true;
            return;
        }

        movimiento.ejecutar(this);
        terminarTurno();
    }

    public final void actualizarDisparo(float delta) {
        if (estaDisparando) barraCarga.update(delta);
    }

    public final void recibirDanio(int danio, float fuerzaX, float fuerzaY) {
        int danoFinal = MathUtils.clamp(danio, 0, Constantes.DANO_MAXIMO);
        this.vida -= danoFinal;

        if (this.vida <= 0) {
            this.vida = 0;
            cambiarEstado(Estado.MUERTE);
            terminarTurno();
            return;
        }

        fisicas.aplicarKnockback(fuerzaX, fuerzaY);
        cambiarEstado(Estado.HIT);
    }

    public final void aumentarVida(int vidaRecogida) {
        if (vidaRecogida <= 0) return;
        this.vida = Math.min(this.vida + vidaRecogida, Constantes.VIDA_MAXIMA);
    }

    public final boolean puedeActuar() {
        return this.activo && !estaDisparando;
    }

    public final void setEnTurno(boolean enTurno) {
        this.enTurno = enTurno;
        if (!enTurno) ocultarMirilla();
    }

    public final void terminarTurno() {
        this.turnoTerminado = true;
        ocultarMirilla();
    }

    public final void reiniciarTurno() {
        this.turnoTerminado = false;
    }

    public final boolean isTurnoTerminado() { return turnoTerminado; }
    public final boolean isEnTurno() { return enTurno; }

    public final float distanciaAlCentro(float x, float y) {
        float centroX = getX() + getWidth() / 2f;
        float centroY = getY() + getHeight() / 2f;
        float dx = centroX - x;
        float dy = centroY - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public final void mostrarMirilla() { this.mirilla.mostrarMirilla(); }
    public final void ocultarMirilla() { this.mirilla.ocultarMirilla(); }

    public final Movimiento getMovimientoSeleccionado() {
        if (movimientoSeleccionado < 0 || movimientoSeleccionado >= movimientos.size()) return null;
        return movimientos.get(movimientoSeleccionado);
    }

    public final void setMovimientoSeleccionado(int indice) {
        if (indice >= 0 && indice < movimientos.size()) this.movimientoSeleccionado = indice;
    }

    public final Mirilla getMirilla() { return this.mirilla; }
    public final int getDireccionMultiplicador() { return this.direccion ? -1 : 1; }
    public final int getVida() { return this.vida; }
    public final boolean getDireccion() { return this.direccion; }
    public final void setDireccion(boolean direccion) { this.direccion = direccion; }
    public final float getVelocidadX() { return this.velocidadX; }
    public final FisicaPersonaje getFisicas() { return this.fisicas; }
    public final boolean isDisparando() { return this.estaDisparando; }
    public final void setDisparando(boolean disparando) { this.estaDisparando = disparando; }
    public final int getIdJugador() { return this.idJugador; }
    public final float getFuerzaSalto() { return this.fuerzaSalto; }
    public final float getPeso() { return this.peso; }

}
