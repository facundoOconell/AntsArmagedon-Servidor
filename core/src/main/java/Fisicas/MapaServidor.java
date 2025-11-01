package Fisicas;

import com.badlogic.gdx.math.Rectangle;
import java.io.IOException;
import java.io.InputStream;


/*Version simplificada de mapa para el servidor*/

public final class MapaServidor {

    private final boolean[][] solido;
    private final int ancho;
    private final int alto;

    private static final float UMBRAL_SOLIDEZ = 0.05f;

    public MapaServidor(String ruta) {
        boolean[][] temp = null;
        int w = 0, h = 0;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream(ruta)) {
            if (stream == null) throw new IOException("No se encontró el archivo: " + ruta);

            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(stream);
            w = img.getWidth();
            h = img.getHeight();

            temp = new boolean[w][h];
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int argb = img.getRGB(x, y);
                    int alpha = (argb >> 24) & 0xFF;
                    temp[x][h - 1 - y] = (alpha / 255f) > UMBRAL_SOLIDEZ;
                }
            }

        } catch (IOException e) {
            System.err.println("[Servidor] Error cargando mapa: " + e.getMessage());
        }

        this.solido = temp != null ? temp : new boolean[1][1];
        this.ancho = w;
        this.alto = h;
    }

    public boolean esSolido(int x, int y) {
        if (x < 0 || y < 0 || x >= ancho || y >= alto) return false;
        return solido[x][y];
    }

    public boolean colisiona(Rectangle rect) {
        int inicioX = (int) Math.max(0, rect.x);
        int finX = (int) Math.min(ancho - 1, rect.x + rect.width);
        int inicioY = (int) Math.max(0, rect.y);
        int finY = (int) Math.min(alto - 1, rect.y + rect.height);

        for (int x = inicioX; x <= finX; x++) {
            for (int y = inicioY; y <= finY; y++) {
                if (esSolido(x, y)) return true;
            }
        }
        return false;
    }

    public void destruir(float xCentro, float yCentro, int radio) {
        int cx = (int) xCentro;
        int cy = (int) yCentro;

        for (int x = cx - radio; x <= cx + radio; x++) {
            for (int y = cy - radio; y <= cy + radio; y++) {
                float dx = x - cx;
                float dy = y - cy;
                if (dx * dx + dy * dy <= radio * radio) {
                    setSolido(x, y, false);
                }
            }
        }
    }

    public void setSolido(int x, int y, boolean valor) {
        if (x >= 0 && x < ancho && y >= 0 && y < alto) {
            solido[x][y] = valor;
        }
    }

    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}
