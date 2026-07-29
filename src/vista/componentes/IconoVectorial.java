package vista.componentes;

import java.awt.*;
import java.awt.geom.*;

/**
 * Íconos de la app: usa el PNG correspondiente en {@code assets/} cuando existe (recoloreado
 * vía {@link IconoPng}), y si no, cae a un glifo dibujado a mano con {@link Graphics2D} —
 * mismo criterio que {@code DialogoPersonalizado.IconoCirculo}: nunca depender de que un
 * archivo esté presente para poder dibujar algo. Se dibujan dentro de un cuadrado
 * {@code size x size} con origen en (x, y).
 */
public final class IconoVectorial {

    public enum Tipo {
        EDITAR("editar.png"), ELIMINAR("basura.png"), DESACTIVAR(null), ACTIVAR(null),
        AGREGAR("agregar.png"), BUSCAR("busqueda.png"), LISTA("menu-hamburguesa.png"),
        VOLVER("flecha-pequena-izquierda.png"), ANTERIOR("angulo-izquierdo.png"),
        SIGUIENTE("angulo-derecho.png"), INICIO("hogar.png"), USUARIO("usuario.png"),
        SALIR(null), GUARDAR(null), CANCELAR(null), QUITAR(null);

        private final String archivoPng;
        Tipo(String archivoPng) { this.archivoPng = archivoPng; }
    }

    private IconoVectorial() {}

    public static void dibujar(Graphics2D destino, Tipo tipo, int x, int y, int size, Color color) {
        if (tipo.archivoPng != null) {
            Image icono = IconoPng.obtener(tipo.archivoPng, color, size);
            if (icono != null) {
                destino.drawImage(icono, x, y, null);
                return;
            }
        }

        Graphics2D g2 = (Graphics2D) destino.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.3f, size * 0.09f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (tipo) {
            case EDITAR:     dibujarEditar(g2, x, y, size); break;
            case ELIMINAR:   dibujarEliminar(g2, x, y, size); break;
            case DESACTIVAR: dibujarCirculo(g2, x, y, size, true); break;
            case ACTIVAR:    dibujarCirculo(g2, x, y, size, false); break;
            case AGREGAR:    dibujarAgregar(g2, x, y, size); break;
            case BUSCAR:     dibujarBuscar(g2, x, y, size); break;
            case LISTA:      dibujarLista(g2, x, y, size); break;
            case VOLVER:
            case ANTERIOR:   dibujarFlecha(g2, x, y, size, true); break;
            case SIGUIENTE:  dibujarFlecha(g2, x, y, size, false); break;
            case INICIO:     dibujarInicio(g2, x, y, size); break;
            case USUARIO:    dibujarUsuario(g2, x, y, size); break;
            case SALIR:      dibujarSalir(g2, x, y, size); break;
            case GUARDAR:    dibujarCheck(g2, x, y, size); break;
            case CANCELAR:   dibujarX(g2, x, y, size); break;
            case QUITAR:     dibujarQuitar(g2, x, y, size); break;
        }
        g2.dispose();
    }

    private static void dibujarInicio(Graphics2D g2, int x, int y, int s) {
        Path2D techo = new Path2D.Float();
        techo.moveTo(x + s * 0.15f, y + s * 0.48f);
        techo.lineTo(x + s * 0.5f, y + s * 0.18f);
        techo.lineTo(x + s * 0.85f, y + s * 0.48f);
        g2.draw(techo);
        Path2D casa = new Path2D.Float();
        casa.moveTo(x + s * 0.24f, y + s * 0.42f);
        casa.lineTo(x + s * 0.24f, y + s * 0.82f);
        casa.lineTo(x + s * 0.76f, y + s * 0.82f);
        casa.lineTo(x + s * 0.76f, y + s * 0.42f);
        g2.draw(casa);
        g2.draw(new RoundRectangle2D.Float(x + s * 0.42f, y + s * 0.58f, s * 0.16f, s * 0.24f, 4, 4));
    }

    private static void dibujarUsuario(Graphics2D g2, int x, int y, int s) {
        float d = s * 0.30f;
        g2.draw(new Ellipse2D.Float(x + (s - d) / 2f, y + s * 0.16f, d, d));
        Path2D hombros = new Path2D.Float();
        hombros.moveTo(x + s * 0.22f, y + s * 0.84f);
        hombros.curveTo(x + s * 0.22f, y + s * 0.58f, x + s * 0.78f, y + s * 0.58f, x + s * 0.78f, y + s * 0.84f);
        g2.draw(hombros);
    }

    /**
     * Lápiz sólido (relleno, no trazo): cuerpo + punta + borrador, rotado 45° sobre su propio
     * centro. A tamaños chicos (16-18px) un lápiz de líneas finas no se distingue de un garabato;
     * relleno se lee mucho más claro, igual que un ícono "solid" de cualquier set de íconos.
     */
    private static void dibujarEditar(Graphics2D g2, int x, int y, int s) {
        Graphics2D g2r = (Graphics2D) g2.create();
        g2r.translate(x + s * 0.5f, y + s * 0.5f);
        g2r.rotate(Math.toRadians(-45));

        float largoCuerpo = s * 0.56f;
        float grosor = s * 0.20f;
        float largoPunta = s * 0.22f;
        float anchoBorrador = s * 0.12f;

        float xCuerpoIni = -(largoCuerpo + largoPunta) / 2f;

        RoundRectangle2D.Float borrador = new RoundRectangle2D.Float(
            xCuerpoIni - anchoBorrador, -grosor / 2f - s * 0.02f, anchoBorrador, grosor + s * 0.04f, 2, 2);
        g2r.fill(borrador);

        RoundRectangle2D.Float cuerpo = new RoundRectangle2D.Float(
            xCuerpoIni, -grosor / 2f, largoCuerpo, grosor, 2, 2);
        g2r.fill(cuerpo);

        float xPuntaIni = xCuerpoIni + largoCuerpo;
        Path2D.Float punta = new Path2D.Float();
        punta.moveTo(xPuntaIni, -grosor / 2f);
        punta.lineTo(xPuntaIni + largoPunta, 0);
        punta.lineTo(xPuntaIni, grosor / 2f);
        punta.closePath();
        g2r.fill(punta);

        g2r.dispose();
    }

    private static void dibujarEliminar(Graphics2D g2, int x, int y, int s) {
        g2.draw(new Line2D.Float(x + s * 0.20f, y + s * 0.34f, x + s * 0.80f, y + s * 0.34f));
        Path2D cuerpo = new Path2D.Float();
        cuerpo.moveTo(x + s * 0.28f, y + s * 0.34f);
        cuerpo.lineTo(x + s * 0.33f, y + s * 0.84f);
        cuerpo.lineTo(x + s * 0.67f, y + s * 0.84f);
        cuerpo.lineTo(x + s * 0.72f, y + s * 0.34f);
        g2.draw(cuerpo);
        Path2D tapa = new Path2D.Float();
        tapa.moveTo(x + s * 0.40f, y + s * 0.34f);
        tapa.lineTo(x + s * 0.40f, y + s * 0.20f);
        tapa.lineTo(x + s * 0.60f, y + s * 0.20f);
        tapa.lineTo(x + s * 0.60f, y + s * 0.34f);
        g2.draw(tapa);
        g2.draw(new Line2D.Float(x + s * 0.42f, y + s * 0.44f, x + s * 0.44f, y + s * 0.74f));
        g2.draw(new Line2D.Float(x + s * 0.58f, y + s * 0.44f, x + s * 0.56f, y + s * 0.74f));
    }

    /** Círculo con diagonal (desactivar/prohibido) o con check adentro (activar). */
    private static void dibujarCirculo(Graphics2D g2, int x, int y, int s, boolean prohibido) {
        float d = s * 0.62f;
        g2.draw(new Ellipse2D.Float(x + (s - d) / 2f, y + (s - d) / 2f, d, d));
        if (prohibido) {
            g2.draw(new Line2D.Float(x + s * 0.31f, y + s * 0.69f, x + s * 0.69f, y + s * 0.31f));
        } else {
            Path2D check = new Path2D.Float();
            check.moveTo(x + s * 0.35f, y + s * 0.52f);
            check.lineTo(x + s * 0.46f, y + s * 0.63f);
            check.lineTo(x + s * 0.67f, y + s * 0.39f);
            g2.draw(check);
        }
    }

    private static void dibujarAgregar(Graphics2D g2, int x, int y, int s) {
        float d = s * 0.72f;
        g2.draw(new Ellipse2D.Float(x + (s - d) / 2f, y + (s - d) / 2f, d, d));
        g2.draw(new Line2D.Float(x + s * 0.5f, y + s * 0.32f, x + s * 0.5f, y + s * 0.68f));
        g2.draw(new Line2D.Float(x + s * 0.32f, y + s * 0.5f, x + s * 0.68f, y + s * 0.5f));
    }

    private static void dibujarBuscar(Graphics2D g2, int x, int y, int s) {
        float d = s * 0.5f;
        g2.draw(new Ellipse2D.Float(x + s * 0.14f, y + s * 0.14f, d, d));
        float bordeLente = x + s * 0.14f + d;
        g2.draw(new Line2D.Float(bordeLente - d * 0.18f, y + s * 0.14f + d - d * 0.18f, x + s * 0.86f, y + s * 0.86f));
    }

    private static void dibujarLista(Graphics2D g2, int x, int y, int s) {
        g2.draw(new Line2D.Float(x + s * 0.2f, y + s * 0.28f, x + s * 0.8f, y + s * 0.28f));
        g2.draw(new Line2D.Float(x + s * 0.2f, y + s * 0.5f, x + s * 0.8f, y + s * 0.5f));
        g2.draw(new Line2D.Float(x + s * 0.2f, y + s * 0.72f, x + s * 0.8f, y + s * 0.72f));
    }

    private static void dibujarFlecha(Graphics2D g2, int x, int y, int s, boolean izquierda) {
        float xIni = izquierda ? s * 0.72f : s * 0.28f;
        float xFin = izquierda ? s * 0.28f : s * 0.72f;
        g2.draw(new Line2D.Float(x + xIni, y + s * 0.5f, x + xFin, y + s * 0.5f));
        float dir = izquierda ? 1f : -1f;
        Path2D punta = new Path2D.Float();
        punta.moveTo(x + xFin + dir * s * 0.20f, y + s * 0.30f);
        punta.lineTo(x + xFin, y + s * 0.5f);
        punta.lineTo(x + xFin + dir * s * 0.20f, y + s * 0.70f);
        g2.draw(punta);
    }

    private static void dibujarSalir(Graphics2D g2, int x, int y, int s) {
        Path2D marco = new Path2D.Float();
        marco.moveTo(x + s * 0.55f, y + s * 0.18f);
        marco.lineTo(x + s * 0.24f, y + s * 0.18f);
        marco.lineTo(x + s * 0.24f, y + s * 0.82f);
        marco.lineTo(x + s * 0.55f, y + s * 0.82f);
        g2.draw(marco);
        g2.draw(new Line2D.Float(x + s * 0.38f, y + s * 0.5f, x + s * 0.82f, y + s * 0.5f));
        Path2D punta = new Path2D.Float();
        punta.moveTo(x + s * 0.64f, y + s * 0.32f);
        punta.lineTo(x + s * 0.82f, y + s * 0.5f);
        punta.lineTo(x + s * 0.64f, y + s * 0.68f);
        g2.draw(punta);
    }

    private static void dibujarCheck(Graphics2D g2, int x, int y, int s) {
        Path2D check = new Path2D.Float();
        check.moveTo(x + s * 0.20f, y + s * 0.52f);
        check.lineTo(x + s * 0.42f, y + s * 0.74f);
        check.lineTo(x + s * 0.82f, y + s * 0.26f);
        g2.draw(check);
    }

    private static void dibujarX(Graphics2D g2, int x, int y, int s) {
        g2.draw(new Line2D.Float(x + s * 0.26f, y + s * 0.26f, x + s * 0.74f, y + s * 0.74f));
        g2.draw(new Line2D.Float(x + s * 0.74f, y + s * 0.26f, x + s * 0.26f, y + s * 0.74f));
    }

    private static void dibujarQuitar(Graphics2D g2, int x, int y, int s) {
        float d = s * 0.62f;
        g2.draw(new Ellipse2D.Float(x + (s - d) / 2f, y + (s - d) / 2f, d, d));
        g2.draw(new Line2D.Float(x + s * 0.34f, y + s * 0.5f, x + s * 0.66f, y + s * 0.5f));
    }
}
