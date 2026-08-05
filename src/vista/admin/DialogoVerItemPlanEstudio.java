package vista.admin;

import modelo.ItemPlanEstudio;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Lectura de solo consulta del contenido completo de un ítem del Plan de Estudio. */
public class DialogoVerItemPlanEstudio extends JDialog {

    public DialogoVerItemPlanEstudio(JFrame padre, ItemPlanEstudio item) {
        super(padre, item.getTopico(), true);
        construirUI(padre, item);
    }

    private void construirUI(JFrame padre, ItemPlanEstudio item) {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Color.WHITE);
        raiz.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(raiz);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(item.getOrden() + ". " + item.getTopico());
        titulo.setFont(EstiloUI.FUENTE_SECCION);
        titulo.setForeground(EstiloUI.TEXTO_PRIMARIO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(14));

        contenido.add(seccion("Contenido teórico", item.getContenido()));

        if (item.getEjercicioPropuesto() != null && !item.getEjercicioPropuesto().isBlank()) {
            contenido.add(Box.createVerticalStrut(16));
            contenido.add(seccion("Ejercicio propuesto", item.getEjercicioPropuesto()));
            contenido.add(Box.createVerticalStrut(16));
            contenido.add(seccion("Respuesta esperada", item.getRespuestaEsperada()));
        }

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setPreferredSize(new Dimension(700, 620));
        raiz.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.setBorder(new EmptyBorder(16, 0, 0, 0));
        JButton botonCerrar = FabricaUI.crearBotonPrimario("Cerrar");
        botonCerrar.addActionListener(e -> dispose());
        botones.add(botonCerrar);
        raiz.add(botones, BorderLayout.SOUTH);

        setResizable(true);
        setMinimumSize(new Dimension(480, 360));
        pack();
        setLocationRelativeTo(padre);
        getRootPane().setDefaultButton(botonCerrar);
    }

    private JPanel seccion(String etiqueta, String texto) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = FabricaUI.crearEtiqueta(etiqueta);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setFont(EstiloUI.FUENTE_CUERPO);
        area.setForeground(EstiloUI.TEXTO_PRIMARIO);
        area.setBorder(null);
        area.setAlignmentX(LEFT_ALIGNMENT);
        area.setMaximumSize(new Dimension(640, Integer.MAX_VALUE));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(area);
        return panel;
    }
}
