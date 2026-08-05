package vista.componentes;

import controlador.ControladorVerificacion;
import email.EmailException;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** Modal para ingresar y verificar el código de 6 caracteres. Se usa post-registro y desde login. */
public class VentanaVerificacionCodigo extends JDialog {

    private final JFrame padre;
    private final ControladorVerificacion controlador = new ControladorVerificacion();
    private final Long dniArg;
    private final String emailArg;
    private final String nombreArg;

    private JTextField campoDni;
    private JTextField campoCodigo;
    private JButton botonVerificar;
    private JButton botonReenviar;
    private JLabel etiquetaInfoEmail;

    private Runnable alVerificarExitoso;

    /** Constructor. Si dni/email/nombre son null, muestra campos para ingresar DNI (flujo "Ya tengo código").
     *  Si se conocen, el DNI queda oculto y se envía el código automáticamente (flujo post-registro).
     */
    public VentanaVerificacionCodigo(JFrame padre, Long dni, String email, String nombre) {
        super(padre, "Verificar código", true);
        this.padre = padre;
        this.dniArg = dni;
        this.emailArg = email;
        this.nombreArg = nombre;

        setUndecorated(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(padre);
        setResizable(false);

        construirUI();

        if (dniArg != null && emailArg != null) {
            // Post-registro: enviar código automáticamente
            enviarCodigoBackground(dniArg, emailArg, nombreArg);
        }
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(Color.WHITE);
        raiz.setBorder(new EmptyBorder(24, 32, 24, 32));
        setContentPane(raiz);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int fila = 0;

        // Título
        JLabel titulo = new JLabel("Verificar código");
        titulo.setFont(EstiloUI.FUENTE_ENCABEZADO);
        titulo.setForeground(EstiloUI.AZUL_OSCURO);
        gbc.gridy = fila++;
        gbc.insets = new Insets(0, 0, 20, 0);
        raiz.add(titulo, gbc);

        // Información: email (si se conoce)
        if (emailArg != null) {
            etiquetaInfoEmail = new JLabel("Te enviamos un código a " + emailArg);
            etiquetaInfoEmail.setFont(EstiloUI.FUENTE_PEQUENA);
            etiquetaInfoEmail.setForeground(EstiloUI.TEXTO_SECUNDARIO);
            gbc.gridy = fila++;
            gbc.insets = new Insets(0, 0, 15, 0);
            raiz.add(etiquetaInfoEmail, gbc);
        }

        // DNI (editable solo si es flujo "Ya tengo código")
        if (dniArg == null) {
            raiz.add(FabricaUI.crearEtiqueta("DNI"), generarGbc(gbc, fila++, new Insets(0, 0, 6, 0)));
            campoDni = FabricaUI.crearCampo();
            FiltroCaracteres.aplicarA(campoDni, "[0-9]");
            raiz.add(campoDni, generarGbc(gbc, fila++, new Insets(0, 0, 15, 0)));
        } else {
            // DNI prellenado, oculto
            campoDni = new JTextField(String.valueOf(dniArg));
            campoDni.setVisible(false);
        }

        // Código
        raiz.add(FabricaUI.crearEtiqueta("Código (6 caracteres)"), generarGbc(gbc, fila++, new Insets(0, 0, 6, 0)));
        campoCodigo = FabricaUI.crearCampo();
        campoCodigo.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                String mayus = str.toUpperCase();
                if (mayus.matches("[A-Z0-9]*") && (getLength() + mayus.length() <= 6)) {
                    super.insertString(offs, mayus, a);
                }
            }
        });
        raiz.add(campoCodigo, generarGbc(gbc, fila++, new Insets(0, 0, 20, 0)));

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);

        botonReenviar = FabricaUI.crearBotonSecundario("Reenviar");
        botonReenviar.addActionListener(e -> reenviarCodigo());
        panelBotones.add(botonReenviar);

        botonVerificar = FabricaUI.crearBotonPrimario("Verificar");
        botonVerificar.addActionListener(e -> verificarCodigo());
        panelBotones.add(botonVerificar);

        gbc.gridy = fila;
        gbc.insets = new Insets(0, 0, 0, 0);
        raiz.add(panelBotones, gbc);
    }

    private GridBagConstraints generarGbc(GridBagConstraints gbc, int fila, Insets insets) {
        gbc.gridy = fila;
        gbc.insets = insets;
        return gbc;
    }

    public void establecerListenerVerificacionExitosa(Runnable callback) {
        this.alVerificarExitoso = callback;
    }

    private void verificarCodigo() {
        String dniStr = campoDni.getText().trim();
        String codigo = campoCodigo.getText().trim();

        try {
            if (dniStr.isEmpty()) {
                DialogoPersonalizado.mostrarError(this, "Ingresá tu DNI.");
                return;
            }
            if (codigo.isEmpty()) {
                DialogoPersonalizado.mostrarError(this, "Ingresá el código que recibiste.");
                return;
            }

            long dni = Long.parseLong(dniStr);
            boolean valido = controlador.verificarCodigo(dni, codigo);

            if (valido) {
                DialogoPersonalizado.mostrarExito(this, "¡Cuenta verificada exitosamente!", () -> {
                    if (alVerificarExitoso != null) alVerificarExitoso.run();
                    dispose();
                });
            } else {
                DialogoPersonalizado.mostrarError(this, "El código no es válido. Verificá que esté correcto.");
            }
        } catch (IllegalArgumentException ex) {
            DialogoPersonalizado.mostrarError(this, ex.getMessage());
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "Error en la base de datos: " + ex.getMessage());
        }
    }

    private void reenviarCodigo() {
        String dniStr = campoDni.getText().trim();
        if (dniStr.isEmpty()) {
            DialogoPersonalizado.mostrarError(this, "Ingresá tu DNI.");
            return;
        }

        botonReenviar.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                long dni = Long.parseLong(dniStr);
                controlador.reenviarCodigoPorDni(dni);
                return null;
            }

            @Override
            protected void done() {
                botonReenviar.setEnabled(true);
                try {
                    get();
                    DialogoPersonalizado.mostrarExito(VentanaVerificacionCodigo.this, "Código reenviado a tu email.");
                    campoCodigo.setText("");
                    campoCodigo.requestFocus();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    DialogoPersonalizado.mostrarError(VentanaVerificacionCodigo.this, "Error: " + msg);
                }
            }
        };
        worker.execute();
    }

    private void enviarCodigoBackground(long dni, String email, String nombre) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controlador.generarYEnviarCodigo(dni, email, nombre);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    // Éxito silencioso, el usuario ya sabe que le enviamos un código
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    DialogoPersonalizado.mostrarError(VentanaVerificacionCodigo.this,
                        "Error al enviar el código: " + msg);
                }
            }
        };
        worker.execute();
    }
}
