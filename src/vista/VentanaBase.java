package vista;

import vista.componentes.BurbujaRobotito;
import vista.componentes.VentanaChatFlotante;
import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** Configuración común de las ventanas principales de la app (título, cierre, tamaño). */
public abstract class VentanaBase extends JFrame {

    private boolean transicionEnCurso = false;

    protected VentanaBase(String titulo, int operacionCierre) {
        setTitle(titulo);
        setDefaultCloseOperation(operacionCierre);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
    }

    /** Agrega la burbuja flotante de chatbot ("Robotito") a esta ventana. Opt-in: cada ventana que la
     *  quiera la llama explícitamente al final de su constructor. */
    protected final void activarBurbujaChatbot(String emailUsuario) {
        activarBurbujaChatbot(emailUsuario, null);
    }

    protected final void activarBurbujaChatbot(String emailUsuario, String cursoTituloActual) {
        BurbujaRobotito burbuja = new BurbujaRobotito();
        burbuja.setSize(EstiloUI.TAMANO_BURBUJA_CHATBOT, EstiloUI.TAMANO_BURBUJA_CHATBOT);
        getLayeredPane().add(burbuja, JLayeredPane.PALETTE_LAYER);

        Runnable reposicionar = () -> {
            int x = getContentPane().getWidth()  - EstiloUI.TAMANO_BURBUJA_CHATBOT - EstiloUI.MARGEN_BURBUJA_CHATBOT;
            int y = getContentPane().getHeight() - EstiloUI.TAMANO_BURBUJA_CHATBOT - EstiloUI.MARGEN_BURBUJA_CHATBOT_INFERIOR;
            burbuja.setLocation(Math.max(0, x), Math.max(0, y));
        };
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { reposicionar.run(); }
        });
        SwingUtilities.invokeLater(reposicionar);

        final VentanaChatFlotante[] ventanaChatRef = new VentanaChatFlotante[1];
        burbuja.alHacerClic(() -> {
            if (ventanaChatRef[0] != null && ventanaChatRef[0].isDisplayable()) {
                ventanaChatRef[0].toFront();
                return;
            }
            ventanaChatRef[0] = new VentanaChatFlotante(this, emailUsuario, cursoTituloActual);
            ventanaChatRef[0].setVisible(true);
        });
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
