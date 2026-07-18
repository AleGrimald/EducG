package vista;

import javax.swing.*;

/** Configuración común de las ventanas principales de la app (título, cierre, tamaño). */
public abstract class VentanaBase extends JFrame {

    protected VentanaBase(String titulo, int operacionCierre) {
        setTitle(titulo);
        setDefaultCloseOperation(operacionCierre);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
    }
}
