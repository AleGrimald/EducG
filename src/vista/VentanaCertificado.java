package vista;

import modelo.Curso;
import vista.componentes.IconoVectorial;
import vista.componentes.PanelCertificado;
import vista.estilo.FabricaUI;

import javax.swing.*;
import java.awt.*;

/** Certificado de finalización de un curso aprobado. */
public class VentanaCertificado extends VentanaBase {

    private final Curso curso;
    private final String nombreUsuario;
    private final int puntaje;

    public VentanaCertificado(Curso curso, String nombreUsuario, int puntaje) {
        super("Educ G – Certificado", DISPOSE_ON_CLOSE);
        this.curso = curso;
        this.nombreUsuario = nombreUsuario;
        this.puntaje = puntaje;
        construirUI();
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new GridBagLayout());
        setContentPane(raiz);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(new PanelCertificado(curso, nombreUsuario, puntaje), BorderLayout.CENTER);

        JButton botonCerrar = FabricaUI.crearBotonSecundario("Cerrar", IconoVectorial.Tipo.CANCELAR);
        botonCerrar.addActionListener(e -> dispose());
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(botonCerrar);
        envoltorio.add(panelBoton, BorderLayout.SOUTH);

        raiz.add(envoltorio);
    }
}
