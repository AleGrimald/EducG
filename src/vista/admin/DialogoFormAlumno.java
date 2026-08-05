package vista.admin;

import controlador.ControladorAdminAlumnos;
import modelo.AlumnoAdmin;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.FiltroCaracteres;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** Formulario de alta/edición de un alumno (JDialog modal). Sin campo de contraseña al editar. */
public class DialogoFormAlumno extends JDialog {

    /** Letras (con acentos/ñ) y espacios — usado en Nombre/Apellido. */
    private static final String PATRON_LETRAS = "[a-zA-ZÁÉÍÓÚÑÜáéíóúñü ]";
    private static final int ANCHO_CAMPO = 380;

    private final ControladorAdminAlumnos controlador;
    private final AlumnoAdmin alumnoExistente;
    private final Runnable alGuardar;

    private JTextField campoNombre;
    private JTextField campoApellido;
    private JTextField campoDni;
    private JTextField campoTelefono;
    private JTextField campoEmail;
    private JPasswordField campoPassword;

    /** {@code alumnoExistente} null = alta, no-null = edición. */
    public DialogoFormAlumno(JFrame padre, AlumnoAdmin alumnoExistente, ControladorAdminAlumnos controlador, Runnable alGuardar) {
        super(padre, alumnoExistente == null ? "Nuevo Alumno" : "Modificar Alumno", true);
        this.alumnoExistente = alumnoExistente;
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

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Nombre"), new Insets(0, 0, 6, 0));
        campoNombre = FabricaUI.crearCampo();
        campoNombre.setPreferredSize(new Dimension(ANCHO_CAMPO, EstiloUI.ALTO_CAMPO));
        FiltroCaracteres.aplicarA(campoNombre, PATRON_LETRAS);
        agregarFila(raiz, gbc, fila++, campoNombre, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Apellido"), new Insets(0, 0, 6, 0));
        campoApellido = FabricaUI.crearCampo();
        campoApellido.setPreferredSize(new Dimension(ANCHO_CAMPO, EstiloUI.ALTO_CAMPO));
        FiltroCaracteres.aplicarA(campoApellido, PATRON_LETRAS);
        agregarFila(raiz, gbc, fila++, campoApellido, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("DNI"), new Insets(0, 0, 6, 0));
        campoDni = FabricaUI.crearCampo();
        campoDni.setPreferredSize(new Dimension(ANCHO_CAMPO, EstiloUI.ALTO_CAMPO));
        FiltroCaracteres.aplicarA(campoDni, "[0-9]");
        agregarFila(raiz, gbc, fila++, campoDni, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Teléfono"), new Insets(0, 0, 6, 0));
        campoTelefono = FabricaUI.crearCampo();
        campoTelefono.setPreferredSize(new Dimension(ANCHO_CAMPO, EstiloUI.ALTO_CAMPO));
        FiltroCaracteres.aplicarA(campoTelefono, "[0-9+\\-() ]");
        agregarFila(raiz, gbc, fila++, campoTelefono, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Correo Electrónico"), new Insets(0, 0, 6, 0));
        campoEmail = FabricaUI.crearCampo();
        campoEmail.setPreferredSize(new Dimension(ANCHO_CAMPO, EstiloUI.ALTO_CAMPO));
        agregarFila(raiz, gbc, fila++, campoEmail, new Insets(0, 0, 14, 0));

        if (alumnoExistente == null) {
            agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Contraseña (6–20 caracteres alfanuméricos)"), new Insets(0, 0, 6, 0));
            campoPassword = FabricaUI.crearCampoPassword();
            campoPassword.setPreferredSize(new Dimension(ANCHO_CAMPO, EstiloUI.ALTO_CAMPO));
            FiltroCaracteres.aplicarA(campoPassword, "[a-zA-Z0-9]");
            agregarFila(raiz, gbc, fila++, campoPassword, new Insets(0, 0, 18, 0));
        }

        if (alumnoExistente != null) {
            campoNombre.setText(alumnoExistente.getNombre());
            campoApellido.setText(alumnoExistente.getApellido());
            campoDni.setText(String.valueOf(alumnoExistente.getDni()));
            campoTelefono.setText(alumnoExistente.getTelefono());
            campoEmail.setText(alumnoExistente.getEmail());
        }

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton botonCancelar = FabricaUI.crearBotonSecundario("Cancelar", IconoVectorial.Tipo.CANCELAR);
        botonCancelar.addActionListener(e -> dispose());
        JButton botonGuardar = FabricaUI.crearBotonPrimario(
            alumnoExistente == null ? "Crear Alumno" : "Guardar Cambios", IconoVectorial.Tipo.GUARDAR);
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
        String nombre = campoNombre.getText().trim();
        String apellido = campoApellido.getText().trim();
        String dni = campoDni.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String email = campoEmail.getText().trim();

        try {
            if (alumnoExistente == null) {
                String password = new String(campoPassword.getPassword());
                if (!controlador.alta(nombre, apellido, dni, telefono, email, password)) {
                    mostrarError("El correo electrónico ya está registrado.");
                    return;
                }
            } else {
                controlador.modificar(alumnoExistente.getId(), nombre, apellido, dni, telefono, email);
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
