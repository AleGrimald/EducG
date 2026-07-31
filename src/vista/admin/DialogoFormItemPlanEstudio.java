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
        raiz.setBackground(Color.WHITE);
        raiz.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(raiz);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int fila = 0;

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Tópico"), new Insets(0, 0, 6, 0));
        campoTopico = FabricaUI.crearCampo();
        campoTopico.setPreferredSize(new Dimension(420, EstiloUI.ALTO_CAMPO));
        agregarFila(raiz, gbc, fila++, campoTopico, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Contenido teórico"), new Insets(0, 0, 6, 0));
        campoContenido = new JTextArea(6, 24);
        campoContenido.setLineWrap(true);
        campoContenido.setWrapStyleWord(true);
        campoContenido.setFont(FabricaUI.crearCampo().getFont());
        campoContenido.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true));
        agregarFila(raiz, gbc, fila++, new JScrollPane(campoContenido), new Insets(0, 0, 14, 0));

        if (itemExistente == null) {
            agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Ejercicio propuesto (opcional)"), new Insets(0, 0, 6, 0));
            campoEjercicio = new JTextArea(3, 24);
            campoEjercicio.setLineWrap(true);
            campoEjercicio.setWrapStyleWord(true);
            campoEjercicio.setFont(FabricaUI.crearCampo().getFont());
            campoEjercicio.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true));
            agregarFila(raiz, gbc, fila++, new JScrollPane(campoEjercicio), new Insets(0, 0, 14, 0));

            agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Respuesta esperada"), new Insets(0, 0, 6, 0));
            campoRespuesta = FabricaUI.crearCampo();
            agregarFila(raiz, gbc, fila++, campoRespuesta, new Insets(0, 0, 18, 0));
        } else {
            campoTopico.setText(itemExistente.getTopico());
            campoContenido.setText(itemExistente.getContenido());

            if (itemExistente.getEjercicioPropuesto() != null && !itemExistente.getEjercicioPropuesto().isBlank()) {
                JLabel nota = new JLabel("<html><div style='width:380px'>Esta clase tiene un ejercicio propuesto "
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
        agregarFila(raiz, gbc, fila, botones, new Insets(10, 0, 0, 0));

        setResizable(false);
        pack();
        setLocationRelativeTo(padre);
        getRootPane().setDefaultButton(botonGuardar);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, JComponent comp, Insets insets) {
        gbc.gridy = fila;
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
        DialogoPersonalizado.mostrarError((JFrame) getParent(), msg);
    }
}
