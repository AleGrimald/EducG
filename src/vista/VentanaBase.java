package vista;

import javax.swing.*;

/** Configuración común de las ventanas principales de la app (título, cierre, tamaño). */
public abstract class VentanaBase extends JFrame {

    private boolean transicionEnCurso = false;

    protected VentanaBase(String titulo, int operacionCierre) {
        setTitle(titulo);
        setDefaultCloseOperation(operacionCierre);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
    }

    /**
     * Se llama al comienzo de todo manejador que dispone esta ventana y abre la
     * siguiente. Si un mismo clic llega duplicado (doble clic, evento repetido en la
     * cola del EDT), la segunda invocación devuelve {@code false} y no debe ejecutar
     * la transición — evita que queden dos ventanas abiertas a la vez.
     */
    protected final boolean iniciarTransicionUnica() {
        if (transicionEnCurso) return false;
        transicionEnCurso = true;
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        // Reinicia el guard cuando esta misma instancia se vuelve a mostrar
        // (caso VentanaLogin, que se reutiliza en vez de recrearse tras VentanaRegistro).
        if (visible) transicionEnCurso = false;
        super.setVisible(visible);
    }
}
