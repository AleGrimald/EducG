package vista.admin;

import controlador.ControladorAdminCursos;
import modelo.CursoAdmin;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.componentes.SelectorIconoCurso;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** Edición de los datos básicos de un curso ya creado (emoji/título/descripción/duración). No toca el plan de estudio. */
public class DialogoFormCurso extends JDialog {

    private final CursoAdmin cursoExistente;
    private final ControladorAdminCursos controlador;
    private final Runnable alGuardar;

    private SelectorIconoCurso campoEmoji;
    private JTextField campoTitulo;
    private JTextArea campoDescripcion;
    private JTextField campoDuracion;

    public DialogoFormCurso(JFrame padre, CursoAdmin cursoExistente, ControladorAdminCursos controlador, Runnable alGuardar) {
        super(padre, "Modificar Curso", true);
        this.cursoExistente = cursoExistente;
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

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Ícono"), new Insets(0, 0, 6, 0));
        campoEmoji = new SelectorIconoCurso(padre);
        agregarFila(raiz, gbc, fila++, campoEmoji, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Título"), new Insets(0, 0, 6, 0));
        campoTitulo = FabricaUI.crearCampo();
        agregarFila(raiz, gbc, fila++, campoTitulo, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Descripción"), new Insets(0, 0, 6, 0));
        campoDescripcion = new JTextArea(8, 20);
        campoDescripcion.setLineWrap(true);
        campoDescripcion.setWrapStyleWord(true);
        campoDescripcion.setFont(FabricaUI.crearCampo().getFont());
        campoDescripcion.setBorder(BorderFactory.createLineBorder(vista.estilo.EstiloUI.BORDE, 1, true));
        agregarFila(raiz, gbc, fila++, new JScrollPane(campoDescripcion), new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Duración (ej. \"8 semanas\")"), new Insets(0, 0, 6, 0));
        campoDuracion = FabricaUI.crearCampo();
        agregarFila(raiz, gbc, fila++, campoDuracion, new Insets(0, 0, 18, 0));

        campoEmoji.establecerActual(cursoExistente.getEmojiClave(), cursoExistente.getEmoji());
        campoTitulo.setText(cursoExistente.getTitulo());
        campoDescripcion.setText(cursoExistente.getDescripcion());
        campoDuracion.setText(cursoExistente.getDuracion());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton botonCancelar = FabricaUI.crearBotonSecundario("Cancelar", IconoVectorial.Tipo.CANCELAR);
        botonCancelar.addActionListener(e -> dispose());
        JButton botonGuardar = FabricaUI.crearBotonPrimario("Guardar Cambios", IconoVectorial.Tipo.GUARDAR);
        botonGuardar.addActionListener(e -> guardar());
        botones.add(botonCancelar);
        botones.add(botonGuardar);
        agregarFila(raiz, gbc, fila, botones, new Insets(10, 0, 0, 0));

        setSize(500, 700);
        setResizable(false);
        setLocationRelativeTo(padre);
        getRootPane().setDefaultButton(botonGuardar);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, JComponent comp, Insets insets) {
        gbc.gridy = fila;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private void guardar() {
        try {
            controlador.modificar(cursoExistente.getId(), campoEmoji.getSeleccion(), campoTitulo.getText().trim(),
                campoDescripcion.getText().trim(), campoDuracion.getText().trim());
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
