package vista.admin;

import controlador.ControladorAdminCursos;
import modelo.ItemPlanEstudio;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Alta/edición de un ítem del Plan de Estudio de un curso. En alta expone tópico, contenido,
 * ejercicio propuesto (opcional) y respuesta esperada — igual que los pasos 2/3 del wizard
 * "Crear Curso". En edición solo tópico/contenido: {@code sp_modificar_leccion} todavía no
 * persiste el ejercicio propuesto.
 */
public class DialogoFormItemPlanEstudio extends JDialog {

    private final int cursoId;
    private final int orden;
    private final ItemPlanEstudio itemExistente;
    private final ControladorAdminCursos controlador;
    private final Runnable alGuardar;

    private JTextField campoTopico;
    private JTextArea campoContenido;
    private JTextArea campoEjercicio;
    private JTextField campoRespuesta;

    /** {@code itemExistente} null = alta, no-null = edición. */
    public DialogoFormItemPlanEstudio(JFrame padre, int cursoId, int orden, ItemPlanEstudio itemExistente,
                                       ControladorAdminCursos controlador, Runnable alGuardar) {
        super(padre, itemExistente == null ? "Agregar Ítem al Plan de Estudio" : "Modificar Ítem del Plan de Estudio", true);
        this.cursoId = cursoId;
        this.orden = orden;
        this.itemExistente = itemExistente;
        this.controlador = controlador;
        this.alGuardar = alGuardar;
        construirUI(padre);
    }

    private void construirUI(JFrame padre) {
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(EstiloUI.FONDO_SUAVE);
        raiz.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(raiz);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int fila = 0;

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Tópico"), new Insets(0, 0, 6, 0));
        campoTopico = FabricaUI.crearCampo();
        campoTopico.setPreferredSize(new Dimension(620, EstiloUI.ALTO_CAMPO));
        agregarFila(raiz, gbc, fila++, campoTopico, new Insets(0, 0, 14, 0));

        // Contenido teórico es el campo que más se edita acá (puede ser una clase entera) — se
        // le da la mayor parte del espacio vertical (weighty alto) para que agrandar la ventana
        // realmente sirva para ver/editar más texto de una, no solo deje espacio vacío abajo.
        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Contenido teórico"), new Insets(0, 0, 6, 0));
        campoContenido = new JTextArea(20, 60);
        campoContenido.setLineWrap(true);
        campoContenido.setWrapStyleWord(true);
        campoContenido.setFont(FabricaUI.crearCampo().getFont());
        campoContenido.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scrollContenido = new JScrollPane(campoContenido);
        scrollContenido.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true));
        agregarFilaExpandible(raiz, gbc, fila++, scrollContenido, new Insets(0, 0, 14, 0), itemExistente == null ? 0.6 : 1.0);

        if (itemExistente == null) {
            agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Ejercicio propuesto (opcional)"), new Insets(0, 0, 6, 0));
            campoEjercicio = new JTextArea(8, 60);
            campoEjercicio.setLineWrap(true);
            campoEjercicio.setWrapStyleWord(true);
            campoEjercicio.setFont(FabricaUI.crearCampo().getFont());
            campoEjercicio.setBorder(new EmptyBorder(8, 10, 8, 10));
            JScrollPane scrollEjercicio = new JScrollPane(campoEjercicio);
            scrollEjercicio.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true));
            agregarFilaExpandible(raiz, gbc, fila++, scrollEjercicio, new Insets(0, 0, 14, 0), 0.4);

            agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Respuesta esperada"), new Insets(0, 0, 6, 0));
            campoRespuesta = FabricaUI.crearCampo();
            agregarFila(raiz, gbc, fila++, campoRespuesta, new Insets(0, 0, 18, 0));
        } else {
            campoTopico.setText(itemExistente.getTopico());
            campoContenido.setText(itemExistente.getContenido());
            campoContenido.setCaretPosition(0);

            if (itemExistente.getEjercicioPropuesto() != null && !itemExistente.getEjercicioPropuesto().isBlank()) {
                JLabel nota = new JLabel("<html><div style='width:560px'>Esta clase tiene un ejercicio propuesto "
                    + "asociado; por ahora no se puede editar desde acá.</div></html>");
                nota.setFont(EstiloUI.FUENTE_PEQUENA);
                nota.setForeground(EstiloUI.TEXTO_SECUNDARIO);
                agregarFila(raiz, gbc, fila++, nota, new Insets(0, 0, 18, 0));
            }
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton botonCancelar = FabricaUI.crearBotonSecundario("Cancelar", IconoVectorial.Tipo.CANCELAR);
        botonCancelar.addActionListener(e -> dispose());
        JButton botonGuardar = FabricaUI.crearBotonPrimario(
            itemExistente == null ? "Agregar Ítem" : "Guardar Cambios", IconoVectorial.Tipo.GUARDAR);
        botonGuardar.addActionListener(e -> guardar());
        botones.add(botonCancelar);
        botones.add(botonGuardar);
        GridBagConstraints gbcBotones = (GridBagConstraints) gbc.clone();
        gbcBotones.weighty = 0;
        agregarFila(raiz, gbcBotones, fila, botones, new Insets(10, 0, 0, 0));

        // Redimensionable: en modo "agregar" (contenido + ejercicio + respuesta) el diálogo ya
        // arranca grande de por sí; en "editar" arranca más chico (menos campos) pero se puede
        // estirar libremente — antes eran de tamaño fijo (pack() + setResizable(false)) con áreas
        // de texto de 6x24/3x24, incómodas para editar una clase entera.
        setResizable(true);
        setMinimumSize(new Dimension(560, 420));
        pack();
        if (itemExistente == null) setSize(getWidth(), Math.min(getHeight(), 820));
        setLocationRelativeTo(padre);
        getRootPane().setDefaultButton(botonGuardar);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, JComponent comp, Insets insets) {
        gbc.gridy = fila;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    /** Como {@link #agregarFila}, pero la fila crece verticalmente al agrandar la ventana. */
    private void agregarFilaExpandible(JPanel panel, GridBagConstraints gbc, int fila, JComponent comp, Insets insets, double weighty) {
        gbc.gridy = fila;
        gbc.weighty = weighty;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private void guardar() {
        String topico = campoTopico.getText().trim();
        String contenido = campoContenido.getText().trim();
        try {
            if (itemExistente == null) {
                String ejercicio = campoEjercicio.getText().trim();
                String respuesta = campoRespuesta.getText().trim();
                controlador.agregarItemPlan(cursoId, orden, topico, contenido, ejercicio, respuesta);
            } else {
                controlador.modificarItemPlan(itemExistente.getId(), topico, contenido);
            }
            dispose();
            alGuardar.run();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    private void mostrarError(String msg) {
        DialogoPersonalizado.mostrarError(this, msg);
    }
}
