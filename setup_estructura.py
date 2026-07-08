"""
setup_estructura.py
Ejecutar desde la carpeta raíz del proyecto:
    python setup_estructura.py

Crea la estructura de paquetes y escribe todos los archivos fuente Java
para la arquitectura Cliente-Servidor en capas de Educ G.
"""

import os

BASE = os.path.dirname(os.path.abspath(__file__))
SRC  = os.path.join(BASE, "src")

# ── Contenido de cada archivo Java ─────────────────────────────────────────

ARCHIVOS = {}

# ── MODELO ──────────────────────────────────────────────────────────────────

ARCHIVOS["modelo/Usuario.java"] = """\
package modelo;

/**
 * Entidad que representa a un usuario del sistema.
 * Capa: Modelo
 */
public class Usuario {

    private int    id;
    private String email;
    private String passwordHash;
    private String nombre;
    private String apellido;

    public Usuario() {}

    public Usuario(int id, String email, String passwordHash,
                   String nombre, String apellido) {
        this.id           = id;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.nombre       = nombre;
        this.apellido     = apellido;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int    getId()           { return id; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getNombre()       { return nombre; }
    public String getApellido()     { return apellido; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setId(int id)                        { this.id           = id; }
    public void setEmail(String email)               { this.email        = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setNombre(String nombre)             { this.nombre       = nombre; }
    public void setApellido(String apellido)         { this.apellido     = apellido; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", email='" + email
             + "', nombre='" + nombre + " " + apellido + "'}";
    }
}
"""

# ── SERVIDOR – CONEXION ──────────────────────────────────────────────────────

ARCHIVOS["servidor/conexion/ConexionDB.java"] = """\
package servidor.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton que gestiona la conexión JDBC con MySQL.
 * Capa: Servidor – Conexión
 *
 * TODO: completar HOST, DATABASE, USUARIO y CLAVE con los datos reales.
 */
public class ConexionDB {

    private static final String HOST     = "localhost";
    private static final int    PORT     = 3306;
    private static final String DATABASE = "educg_db";
    private static final String USUARIO  = "root";
    private static final String CLAVE    = "78531015aA@";

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&allowPublicKeyRetrieval=true"
        + "&serverTimezone=UTC&characterEncoding=UTF-8";

    private static ConexionDB instancia;
    private Connection conexion;

    private ConexionDB() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL no encontrado.\\n"
                + "Agregá mysql-connector-java al proyecto (File > Project Structure > Libraries).", e);
        }
    }

    public static ConexionDB getInstancia() throws SQLException {
        if (instancia == null || instancia.conexion.isClosed()) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    public void cerrar() {
        if (conexion != null) {
            try { conexion.close(); } catch (SQLException ignored) {}
            instancia = null;
        }
    }
}
"""

# ── SERVIDOR – DAO ───────────────────────────────────────────────────────────

ARCHIVOS["servidor/dao/IUsuarioDAO.java"] = """\
package servidor.dao;

import modelo.Usuario;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Contrato para las operaciones de persistencia de Usuario.
 * Capa: Servidor – DAO (Interfaz)
 */
public interface IUsuarioDAO {
    boolean           insertar(Usuario usuario) throws SQLException;
    Optional<Usuario> buscarPorEmail(String email) throws SQLException;
    boolean           existeEmail(String email) throws SQLException;
}
"""

ARCHIVOS["servidor/dao/UsuarioDAO.java"] = """\
package servidor.dao;

import modelo.Usuario;
import servidor.conexion.ConexionDB;

import java.sql.*;
import java.util.Optional;

/**
 * Implementación de {@link IUsuarioDAO} sobre MySQL.
 * Capa: Servidor – DAO (Implementación)
 * Todas las consultas usan PreparedStatement para prevenir inyección SQL.
 */
public class UsuarioDAO implements IUsuarioDAO {

    @Override
    public boolean insertar(Usuario usuario) throws SQLException {
        final String sql =
            "INSERT INTO usuarios (email, password_hash, nombre, apellido) "
            + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps =
                ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario.getEmail());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellido());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        final String sql =
            "SELECT id, email, password_hash, nombre, apellido "
            + "FROM usuarios WHERE email = ? AND activo = 1";

        try (PreparedStatement ps =
                ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("nombre"),
                        rs.getString("apellido")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean existeEmail(String email) throws SQLException {
        final String sql = "SELECT id FROM usuarios WHERE email = ?";
        try (PreparedStatement ps =
                ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
"""

# ── SERVIDOR – SERVICIO ──────────────────────────────────────────────────────

ARCHIVOS["servidor/servicio/IAuthServicio.java"] = """\
package servidor.servicio;

import modelo.Usuario;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Contrato para los casos de uso de autenticación.
 * Capa: Servidor – Servicio (Interfaz)
 */
public interface IAuthServicio {
    /**
     * Verifica credenciales.
     * @return el Usuario si las credenciales son válidas, vacío si no.
     */
    Optional<Usuario> login(String email, String password) throws SQLException;

    /**
     * Registra un nuevo usuario.
     * @return true si se creó correctamente, false si el email ya existe.
     */
    boolean registrar(String email, String password,
                      String nombre, String apellido) throws SQLException;
}
"""

ARCHIVOS["servidor/servicio/AuthServicio.java"] = """\
package servidor.servicio;

import modelo.Usuario;
import servidor.dao.IUsuarioDAO;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementación de {@link IAuthServicio}.
 * Aplica hashing SHA-256 + salt aleatorio antes de persistir la contraseña.
 * Capa: Servidor – Servicio (Implementación)
 */
public class AuthServicio implements IAuthServicio {

    private final IUsuarioDAO usuarioDAO;

    public AuthServicio(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    // ── Casos de uso ─────────────────────────────────────────────────────────

    @Override
    public Optional<Usuario> login(String email, String password) throws SQLException {
        Optional<Usuario> usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario.isPresent()
                && verificarPassword(password, usuario.get().getPasswordHash())) {
            return usuario;
        }
        return Optional.empty();
    }

    @Override
    public boolean registrar(String email, String password,
                             String nombre, String apellido) throws SQLException {
        if (usuarioDAO.existeEmail(email)) return false;
        try {
            String hash  = hashPassword(password);
            Usuario nuevo = new Usuario(0, email, hash, nombre, apellido);
            return usuarioDAO.insertar(nuevo);
        } catch (NoSuchAlgorithmException e) {
            throw new SQLException("Error al procesar la contraseña.", e);
        }
    }

    // ── Hashing ───────────────────────────────────────────────────────────────

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(salt) + ":" + bytesToHex(hash);
    }

    private boolean verificarPassword(String password, String almacenado) {
        if (almacenado == null || !almacenado.contains(":")) return false;
        try {
            String[] partes = almacenado.split(":", 2);
            byte[]   salt   = hexToBytes(partes[0]);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).equals(partes[1]);
        } catch (Exception e) {
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}
"""

# ── CLIENTE – UTILIDADES ─────────────────────────────────────────────────────

ARCHIVOS["cliente/utilidades/Validador.java"] = """\
package cliente.utilidades;

import java.util.regex.Pattern;

/**
 * Utilidad de validación de entradas del usuario.
 * Capa: Cliente – Utilidades
 */
public final class Validador {

    private static final Pattern PATRON_EMAIL = Pattern.compile(
        "^[a-zA-Z0-9._%+\\\\-]+@[a-zA-Z0-9.\\\\-]+\\\\.[a-zA-Z]{2,}$"
    );

    /** Solo alfanumérico, 6–20 chars — descarta cualquier símbolo SQL por definición. */
    private static final Pattern PATRON_PASSWORD = Pattern.compile("^[a-zA-Z0-9]{6,20}$");

    private static final Pattern PATRON_INYECCION =
        Pattern.compile("[';\\\"\\\\\\\\\\\\-\\\\-/\\\\*=<>|&%^#!~`]");

    private Validador() {}

    public static boolean esEmailValido(String email) {
        return email != null && PATRON_EMAIL.matcher(email).matches();
    }

    public static boolean esPasswordValida(String password) {
        return password != null && PATRON_PASSWORD.matcher(password).matches();
    }

    public static boolean tieneRiesgoInyeccion(String input) {
        return input != null && PATRON_INYECCION.matcher(input).find();
    }

    public static boolean esNombreValido(String nombre) {
        return nombre != null
            && nombre.length() >= 2
            && nombre.length() <= 100
            && !tieneRiesgoInyeccion(nombre);
    }
}
"""

ARCHIVOS["cliente/utilidades/FabricaUI.java"] = """\
package cliente.utilidades;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Fábrica de componentes Swing con el estilo visual de Educ G.
 * Capa: Cliente – Utilidades
 */
public final class FabricaUI {

    // ── Paleta ───────────────────────────────────────────────────────────────
    public static final Color COLOR_ACENTO   = new Color( 41, 128, 185);
    public static final Color COLOR_TEXTO    = new Color( 44,  62,  80);
    public static final Color COLOR_MUTED    = new Color(127, 140, 141);
    public static final Color COLOR_CAMPO_BG = new Color(245, 248, 250);
    public static final Color COLOR_BORDE    = new Color(189, 195, 199);
    static final Color COLOR_BG_TOP          = new Color( 20,  40,  70);
    static final Color COLOR_BG_BOTTOM       = new Color( 55,  95, 150);

    // ── Tipografía ────────────────────────────────────────────────────────────
    public static final Font FUENTE_TITULO    = new Font("Segoe UI", Font.BOLD,  36);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FUENTE_ENCABEZ   = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FUENTE_ETIQUETA  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_CAMPO     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOTON     = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FUENTE_PEQUEÑA   = new Font("Segoe UI", Font.PLAIN, 12);

    private FabricaUI() {}

    // ── Paneles ───────────────────────────────────────────────────────────────

    public static JPanel crearFondoGradiente() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(
                    0, 0, COLOR_BG_TOP, 0, getHeight(), COLOR_BG_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    public static JPanel crearTarjeta() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 22));
                g2.fill(new RoundRectangle2D.Float(4, 6, getWidth()-5, getHeight()-6, 18, 18));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-5, getHeight()-7, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    // ── Campos ────────────────────────────────────────────────────────────────

    public static JTextField crearCampo() {
        JTextField f = new JTextField();
        estilizarCampo(f);
        return f;
    }

    public static JPasswordField crearCampoPassword() {
        JPasswordField f = new JPasswordField();
        estilizarCampo(f);
        return f;
    }

    private static void estilizarCampo(JTextField f) {
        f.setFont(FUENTE_CAMPO);
        f.setBackground(COLOR_CAMPO_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        f.setPreferredSize(new Dimension(0, 40));
    }

    // ── Etiquetas ─────────────────────────────────────────────────────────────

    public static JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_ETIQUETA);
        lbl.setForeground(COLOR_TEXTO);
        return lbl;
    }

    // ── Botones ───────────────────────────────────────────────────────────────

    public static JButton crearBotonPrimario(String etiqueta) {
        JButton btn = new JButton(etiqueta) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                if      (getModel().isPressed())  g2.setColor(new Color(25,  90, 150));
                else if (getModel().isRollover()) g2.setColor(new Color(52, 152, 219));
                else                              g2.setColor(COLOR_ACENTO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(FUENTE_BOTON);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(etiqueta,
                    (getWidth()  - fm.stringWidth(etiqueta)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        aplicarEstiloBoton(btn);
        return btn;
    }

    public static JButton crearBotonSecundario(String etiqueta) {
        JButton btn = new JButton(etiqueta) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(236, 245, 253) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(COLOR_ACENTO);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8));
                g2.setFont(FUENTE_BOTON);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(etiqueta,
                    (getWidth()  - fm.stringWidth(etiqueta)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        aplicarEstiloBoton(btn);
        return btn;
    }

    private static void aplicarEstiloBoton(JButton btn) {
        btn.setPreferredSize(new Dimension(0, 42));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
"""

# ── CLIENTE – VISTA ──────────────────────────────────────────────────────────

ARCHIVOS["cliente/vista/VentanaLogin.java"] = """\
package cliente.vista;

import cliente.utilidades.FabricaUI;
import cliente.utilidades.Validador;
import modelo.Usuario;
import servidor.servicio.IAuthServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

/**
 * Ventana de inicio de sesión.
 * Capa: Cliente – Vista
 */
public class VentanaLogin extends JFrame {

    private final IAuthServicio authServicio;

    private JTextField     campoEmail;
    private JPasswordField campoPassword;
    private JCheckBox      checkMostrarPass;

    public VentanaLogin(IAuthServicio authServicio) {
        this.authServicio = authServicio;
        setTitle("Educ G – Programación");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 545);
        setLocationRelativeTo(null);
        setResizable(false);
        construirUI();
    }

    private void construirUI() {
        JPanel fondo = FabricaUI.crearFondoGradiente();
        setContentPane(fondo);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);

        // ── Encabezado ────────────────────────────────────────────────────────
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(new EmptyBorder(0, 0, 22, 0));

        JLabel lblTitulo = new JLabel("Educ G");
        lblTitulo.setFont(FabricaUI.FUENTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Programación");
        lblSubtitulo.setFont(FabricaUI.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(new Color(180, 210, 255));
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(lblSubtitulo);

        // ── Tarjeta ───────────────────────────────────────────────────────────
        JPanel tarjeta = FabricaUI.crearTarjeta();
        tarjeta.setBorder(new EmptyBorder(28, 32, 28, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        JLabel lblCard = new JLabel("Iniciar Sesión");
        lblCard.setFont(FabricaUI.FUENTE_ENCABEZ);
        lblCard.setForeground(FabricaUI.COLOR_TEXTO);
        lblCard.setHorizontalAlignment(SwingConstants.CENTER);
        agregarFila(tarjeta, lblCard,     gbc, 0, new Insets(0, 0, 20, 0));

        agregarFila(tarjeta, FabricaUI.crearEtiqueta("Correo electrónico"),
                             gbc, 1, new Insets(0, 0,  4, 0));
        campoEmail = FabricaUI.crearCampo();
        agregarFila(tarjeta, campoEmail,  gbc, 2, new Insets(0, 0, 14, 0));

        agregarFila(tarjeta, FabricaUI.crearEtiqueta("Contraseña"),
                             gbc, 3, new Insets(0, 0,  4, 0));
        campoPassword = FabricaUI.crearCampoPassword();
        agregarFila(tarjeta, campoPassword, gbc, 4, new Insets(0, 0, 6, 0));

        checkMostrarPass = new JCheckBox("Mostrar contraseña");
        checkMostrarPass.setFont(FabricaUI.FUENTE_PEQUEÑA);
        checkMostrarPass.setForeground(FabricaUI.COLOR_MUTED);
        checkMostrarPass.setOpaque(false);
        checkMostrarPass.addActionListener(e -> alternarVisibilidadPassword());
        agregarFila(tarjeta, checkMostrarPass, gbc, 5, new Insets(0, 0, 22, 0));

        JButton btnIngresar = FabricaUI.crearBotonPrimario("Iniciar Sesión");
        btnIngresar.addActionListener(e -> manejarLogin());
        agregarFila(tarjeta, btnIngresar, gbc, 6, new Insets(0, 0, 12, 0));

        JLabel lblSinCuenta = new JLabel("¿No tenés cuenta?");
        lblSinCuenta.setFont(FabricaUI.FUENTE_PEQUEÑA);
        lblSinCuenta.setForeground(FabricaUI.COLOR_MUTED);
        lblSinCuenta.setHorizontalAlignment(SwingConstants.CENTER);
        agregarFila(tarjeta, lblSinCuenta, gbc, 7, new Insets(0, 0,  8, 0));

        JButton btnRegistro = FabricaUI.crearBotonSecundario("Registrarse");
        btnRegistro.addActionListener(e -> abrirRegistro());
        agregarFila(tarjeta, btnRegistro, gbc, 8, new Insets(0, 0, 0, 0));

        contenedor.add(encabezado, BorderLayout.NORTH);
        contenedor.add(tarjeta,   BorderLayout.CENTER);

        GridBagConstraints gbcFondo = new GridBagConstraints();
        gbcFondo.insets = new Insets(30, 30, 30, 30);
        gbcFondo.fill   = GridBagConstraints.BOTH;
        fondo.add(contenedor, gbcFondo);

        getRootPane().setDefaultButton(btnIngresar);
    }

    private static void agregarFila(JPanel panel, JComponent comp,
                                    GridBagConstraints gbc, int fila, Insets insets) {
        gbc.gridy  = fila;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private void alternarVisibilidadPassword() {
        campoPassword.setEchoChar(checkMostrarPass.isSelected() ? (char) 0 : '•');
    }

    private void manejarLogin() {
        String email    = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (!Validador.esEmailValido(email)) {
            mostrarError("Ingresá un correo electrónico válido.\\nEjemplo: usuario@dominio.com");
            campoEmail.requestFocus();
            return;
        }
        if (!Validador.esPasswordValida(password)) {
            mostrarError("La contraseña debe tener entre 6 y 20 caracteres alfanuméricos\\n"
                       + "(solo letras y números, sin símbolos).");
            campoPassword.requestFocus();
            return;
        }

        try {
            Optional<Usuario> usuario = authServicio.login(email, password);
            if (usuario.isPresent()) {
                JOptionPane.showMessageDialog(this,
                    "¡Bienvenido/a, " + usuario.get().getNombre() + "!",
                    "Acceso correcto", JOptionPane.INFORMATION_MESSAGE);
                // TODO: abrir ventana principal pasando el usuario
            } else {
                mostrarError("Correo electrónico o contraseña incorrectos.");
            }
        } catch (Exception ex) {
            mostrarError("No se pudo conectar con la base de datos:\\n" + ex.getMessage());
        }
    }

    private void abrirRegistro() {
        setVisible(false);
        new VentanaRegistro(this, authServicio).setVisible(true);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
"""

ARCHIVOS["cliente/vista/VentanaRegistro.java"] = """\
package cliente.vista;

import cliente.utilidades.FabricaUI;
import cliente.utilidades.Validador;
import servidor.servicio.IAuthServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana de registro de nuevo usuario.
 * Capa: Cliente – Vista
 */
public class VentanaRegistro extends JFrame {

    private final VentanaLogin  ventanaLogin;
    private final IAuthServicio authServicio;

    private JTextField     campoNombre;
    private JTextField     campoApellido;
    private JTextField     campoEmail;
    private JPasswordField campoPassword;
    private JPasswordField campoConfirmar;
    private JCheckBox      checkMostrarPass;

    public VentanaRegistro(VentanaLogin ventanaLogin, IAuthServicio authServicio) {
        this.ventanaLogin  = ventanaLogin;
        this.authServicio  = authServicio;
        setTitle("Educ G – Crear cuenta");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 660);
        setLocationRelativeTo(null);
        setResizable(false);
        construirUI();

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                ventanaLogin.setVisible(true);
            }
        });
    }

    private void construirUI() {
        JPanel fondo = FabricaUI.crearFondoGradiente();
        setContentPane(fondo);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);

        // ── Encabezado ────────────────────────────────────────────────────────
        JPanel encabezado = new JPanel();
        encabezado.setOpaque(false);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(new EmptyBorder(0, 0, 18, 0));

        JLabel lblTitulo = new JLabel("Educ G");
        lblTitulo.setFont(FabricaUI.FUENTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Programación");
        lblSubtitulo.setFont(FabricaUI.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(new Color(180, 210, 255));
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(lblSubtitulo);

        // ── Tarjeta ───────────────────────────────────────────────────────────
        JPanel tarjeta = FabricaUI.crearTarjeta();
        tarjeta.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;

        JLabel lblCard = new JLabel("Crear cuenta");
        lblCard.setFont(FabricaUI.FUENTE_ENCABEZ);
        lblCard.setForeground(FabricaUI.COLOR_TEXTO);
        lblCard.setHorizontalAlignment(SwingConstants.CENTER);
        agregarFila(tarjeta, lblCard, gbc, 0, new Insets(0, 0, 16, 0));

        // Nombre y apellido en la misma fila
        JPanel panelNombre = new JPanel(new GridLayout(2, 2, 10, 4));
        panelNombre.setOpaque(false);
        panelNombre.add(FabricaUI.crearEtiqueta("Nombre"));
        panelNombre.add(FabricaUI.crearEtiqueta("Apellido"));
        campoNombre   = FabricaUI.crearCampo();
        campoApellido = FabricaUI.crearCampo();
        panelNombre.add(campoNombre);
        panelNombre.add(campoApellido);
        agregarFila(tarjeta, panelNombre, gbc, 1, new Insets(0, 0, 12, 0));

        agregarFila(tarjeta, FabricaUI.crearEtiqueta("Correo electrónico"),
                             gbc, 2, new Insets(0, 0,  4, 0));
        campoEmail = FabricaUI.crearCampo();
        agregarFila(tarjeta, campoEmail, gbc, 3, new Insets(0, 0, 12, 0));

        agregarFila(tarjeta,
            FabricaUI.crearEtiqueta("Contraseña (6–20 caracteres alfanuméricos)"),
                             gbc, 4, new Insets(0, 0,  4, 0));
        campoPassword = FabricaUI.crearCampoPassword();
        agregarFila(tarjeta, campoPassword, gbc, 5, new Insets(0, 0, 12, 0));

        agregarFila(tarjeta, FabricaUI.crearEtiqueta("Confirmar contraseña"),
                             gbc, 6, new Insets(0, 0,  4, 0));
        campoConfirmar = FabricaUI.crearCampoPassword();
        agregarFila(tarjeta, campoConfirmar, gbc, 7, new Insets(0, 0, 6, 0));

        checkMostrarPass = new JCheckBox("Mostrar contraseñas");
        checkMostrarPass.setFont(FabricaUI.FUENTE_PEQUEÑA);
        checkMostrarPass.setForeground(FabricaUI.COLOR_MUTED);
        checkMostrarPass.setOpaque(false);
        checkMostrarPass.addActionListener(e -> alternarVisibilidadPassword());
        agregarFila(tarjeta, checkMostrarPass, gbc, 8, new Insets(0, 0, 18, 0));

        JButton btnCrear = FabricaUI.crearBotonPrimario("Crear cuenta");
        btnCrear.addActionListener(e -> manejarRegistro());
        agregarFila(tarjeta, btnCrear, gbc, 9, new Insets(0, 0, 10, 0));

        JButton btnVolver = FabricaUI.crearBotonSecundario("← Volver al inicio de sesión");
        btnVolver.addActionListener(e -> volverAlLogin());
        agregarFila(tarjeta, btnVolver, gbc, 10, new Insets(0, 0, 0, 0));

        contenedor.add(encabezado, BorderLayout.NORTH);
        contenedor.add(tarjeta,   BorderLayout.CENTER);

        GridBagConstraints gbcFondo = new GridBagConstraints();
        gbcFondo.insets = new Insets(26, 30, 26, 30);
        gbcFondo.fill   = GridBagConstraints.BOTH;
        fondo.add(contenedor, gbcFondo);

        getRootPane().setDefaultButton(btnCrear);
    }

    private static void agregarFila(JPanel panel, JComponent comp,
                                    GridBagConstraints gbc, int fila, Insets insets) {
        gbc.gridy  = fila;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private void alternarVisibilidadPassword() {
        char echo = checkMostrarPass.isSelected() ? (char) 0 : '•';
        campoPassword.setEchoChar(echo);
        campoConfirmar.setEchoChar(echo);
    }

    private void manejarRegistro() {
        String nombre    = campoNombre.getText().trim();
        String apellido  = campoApellido.getText().trim();
        String email     = campoEmail.getText().trim();
        String password  = new String(campoPassword.getPassword());
        String confirmar = new String(campoConfirmar.getPassword());

        if (!Validador.esNombreValido(nombre)) {
            mostrarError("El nombre debe tener entre 2 y 100 caracteres y no contener símbolos especiales.");
            campoNombre.requestFocus(); return;
        }
        if (!Validador.esNombreValido(apellido)) {
            mostrarError("El apellido debe tener entre 2 y 100 caracteres y no contener símbolos especiales.");
            campoApellido.requestFocus(); return;
        }
        if (!Validador.esEmailValido(email)) {
            mostrarError("Ingresá un correo electrónico válido.\\nEjemplo: usuario@dominio.com");
            campoEmail.requestFocus(); return;
        }
        if (!Validador.esPasswordValida(password)) {
            mostrarError("La contraseña debe tener entre 6 y 20 caracteres alfanuméricos\\n"
                       + "(solo letras y números, sin símbolos).");
            campoPassword.requestFocus(); return;
        }
        if (!password.equals(confirmar)) {
            mostrarError("Las contraseñas no coinciden.");
            campoConfirmar.requestFocus(); return;
        }

        try {
            if (authServicio.registrar(email, password, nombre, apellido)) {
                JOptionPane.showMessageDialog(this,
                    "¡Cuenta creada exitosamente!\\nYa podés iniciar sesión.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                volverAlLogin();
            } else {
                mostrarError("El correo electrónico ya está registrado.\\nUsá otro o iniciá sesión.");
            }
        } catch (Exception ex) {
            mostrarError("No se pudo conectar con la base de datos:\\n" + ex.getMessage());
        }
    }

    private void volverAlLogin() {
        ventanaLogin.setVisible(true);
        dispose();
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
"""

# ── MAIN ──────────────────────────────────────────────────────────────────────

ARCHIVOS["Main.java"] = """\
import cliente.vista.VentanaLogin;
import servidor.dao.IUsuarioDAO;
import servidor.dao.UsuarioDAO;
import servidor.servicio.AuthServicio;
import servidor.servicio.IAuthServicio;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación Educ G.
 *
 * Composición de capas:
 *   Modelo ← DAO (Servidor) ← Servicio (Servidor) ← Vista (Cliente)
 */
public class Main {
    public static void main(String[] args) {
        // Intentar look and feel Nimbus para apariencia moderna
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Inyección de dependencias: Vista no conoce implementaciones concretas del servidor
        IUsuarioDAO    usuarioDAO   = new UsuarioDAO();
        IAuthServicio  authServicio = new AuthServicio(usuarioDAO);

        SwingUtilities.invokeLater(() -> new VentanaLogin(authServicio).setVisible(true));
    }
}
"""

# ── Generación de archivos ────────────────────────────────────────────────────

def escribir(ruta_relativa, contenido):
    ruta = os.path.join(SRC, ruta_relativa.replace("/", os.sep))
    directorio = os.path.dirname(ruta)
    os.makedirs(directorio, exist_ok=True)
    with open(ruta, "w", encoding="utf-8") as f:
        f.write(contenido)
    print(f"  ✓  {ruta_relativa}")

if __name__ == "__main__":
    print("\n=== Educ G – Generando estructura en capas ===\n")
    for rel, contenido in ARCHIVOS.items():
        escribir(rel, contenido)
    print("\n✅ Listo. Estructura creada en:", SRC)
    print("\nPróximos pasos:")
    print("  1. Refrescá el proyecto en IntelliJ (clic derecho > Reload from Disk)")
    print("  2. Completá las credenciales en servidor/conexion/ConexionDB.java")
    print("  3. Ejecutá schema.sql en MySQL para crear la base de datos")
    print("  4. Ejecutá el proyecto desde Main.java\n")
