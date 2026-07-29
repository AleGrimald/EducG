package vista.admin;

import controlador.ControladorAdminAlumnos;
import modelo.AlumnoAdmin;
import vista.VentanaBase;
import vista.VentanaLogin;
import vista.componentes.ColumnaAcciones;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.FiltroCaracteres;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Módulo Alumnos del panel de administrador: alta, búsqueda por DNI, listado, modificación y bajas. */
public class VentanaAdminAlumnos extends VentanaBase {

    private static final int COLUMNA_ACCIONES = 7;

    private final ControladorAdminAlumnos controlador = new ControladorAdminAlumnos();
    private final String emailAdmin;
    private List<AlumnoAdmin> alumnos = new ArrayList<>();

    private JTable tabla;
    private JTextField campoBusquedaDni;

    public VentanaAdminAlumnos(String emailAdmin) {
        super("Educ G – Administrar Alumnos", EXIT_ON_CLOSE);
        this.emailAdmin = emailAdmin;
        construirUI();
        recargar();
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setBorder(new EmptyBorder(16, 24, 24, 24));

        contenido.add(construirBarraSuperior(), BorderLayout.NORTH);
        contenido.add(construirTabla(), BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        wrapper.add(contenido, BorderLayout.CENTER);
        raiz.add(wrapper, BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel appLbl = new JLabel("Educ G");
        appLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        appLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Panel de Administrador – Alumnos");
        subLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        subLbl.setForeground(new Color(180, 210, 255));

        bloqueTitulo.add(appLbl);
        bloqueTitulo.add(Box.createVerticalStrut(2));
        bloqueTitulo.add(subLbl);

        JButton botonVolver = FabricaUI.crearBotonSecundarioPequeno("Volver al Panel", IconoVectorial.Tipo.INICIO);
        botonVolver.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaAdmin(emailAdmin).setVisible(true);
        });

        JButton botonCerrarSesion = FabricaUI.crearBotonSecundarioPequeno("Cerrar Sesión", IconoVectorial.Tipo.SALIR);
        botonCerrarSesion.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaLogin().setVisible(true);
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(botonVolver);
        botones.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botones, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel construirBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(4, 0, 16, 0));

        JButton botonAlta = FabricaUI.crearBotonPrimario("Alta de Alumno", IconoVectorial.Tipo.AGREGAR);
        botonAlta.addActionListener(e ->
            new DialogoFormAlumno(this, null, controlador, this::recargar).setVisible(true));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBusqueda.setOpaque(false);

        campoBusquedaDni = FabricaUI.crearCampo();
        campoBusquedaDni.setPreferredSize(new Dimension(180, EstiloUI.ALTO_CAMPO));
        FiltroCaracteres.aplicarA(campoBusquedaDni, "[0-9]");

        JButton botonBuscar = FabricaUI.crearBotonSecundarioPequeno("Buscar por DNI", IconoVectorial.Tipo.BUSCAR);
        botonBuscar.addActionListener(e -> buscarPorDni());

        JButton botonVerTodos = FabricaUI.crearBotonSecundarioPequeno("Ver Todos", IconoVectorial.Tipo.LISTA);
        botonVerTodos.addActionListener(e -> { campoBusquedaDni.setText(""); recargar(); });

        panelBusqueda.add(campoBusquedaDni);
        panelBusqueda.add(botonBuscar);
        panelBusqueda.add(botonVerTodos);

        barra.add(botonAlta, BorderLayout.WEST);
        barra.add(panelBusqueda, BorderLayout.EAST);
        return barra;
    }

    private JScrollPane construirTabla() {
        tabla = new JTable();
        tabla.setFont(EstiloUI.FUENTE_ETIQUETA);
        tabla.setRowHeight(44);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setSelectionBackground(new Color(220, 235, 255));
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        // Ancho fijo por columna (ver fijarAnchoColumnas): que el ancho de la ventana
        // no achique/estire las columnas de datos, a diferencia del auto-resize por defecto.
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getHorizontalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    /**
     * Ancho fijo por columna, calculado a partir de la cantidad máxima de caracteres que puede
     * traer cada campo: DNI (9 dígitos, {@code Validador.esDniValido}), teléfono (20 caracteres,
     * {@code Validador.esTelefonoValido}) y fecha (10 = "aaaa-mm-dd") son exactos. Nombre/apellido/
     * email no tienen un tope realista chico (hasta 100/255 en la base) — se usa un ancho generoso
     * pero acotado en vez del máximo literal, que haría la tabla inusable.
     */
    private void fijarAnchoColumnas() {
        FontMetrics fm = tabla.getFontMetrics(tabla.getFont());
        fijarAncho(0, anchoPara(fm, 19));  // Nombre
        fijarAncho(1, anchoPara(fm, 19));  // Apellido
        fijarAncho(2, anchoPara(fm, 13));   // DNI
        fijarAncho(3, anchoPara(fm, 35));  // Email
        fijarAncho(4, anchoPara(fm, 22));  // Teléfono
        fijarAncho(5, anchoPara(fm, 15));  // Registrado
        fijarAncho(6, anchoPara(fm, 10));   // Estado

        centrar(2); // DNI
        centrar(4); // Teléfono
        centrar(5); // Registrado
        centrar(6); // Estado
    }

    private int anchoPara(FontMetrics fm, int caracteres) {
        return fm.charWidth('0') * caracteres + 28;
    }

    private void fijarAncho(int indice, int ancho) {
        TableColumn columna = tabla.getColumnModel().getColumn(indice);
        columna.setMinWidth(ancho);
        columna.setMaxWidth(ancho);
        columna.setPreferredWidth(ancho);
    }

    private void centrar(int indice) {
        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer();
        renderizador.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(indice).setCellRenderer(renderizador);
    }

    private void buscarPorDni() {
        String dni = campoBusquedaDni.getText().trim();
        try {
            AlumnoAdmin resultado = controlador.buscarPorDni(dni);
            if (resultado == null) {
                actualizarFilas(new ArrayList<>());
                DialogoPersonalizado.mostrarInfo(this, "No se encontró ningún alumno con DNI " + dni + ".");
            } else {
                List<AlumnoAdmin> uno = new ArrayList<>();
                uno.add(resultado);
                actualizarFilas(uno);
            }
        } catch (IllegalArgumentException ex) {
            DialogoPersonalizado.mostrarError(this, ex.getMessage());
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "Error al buscar: " + ex.getMessage());
        }
    }

    private void recargar() {
        try {
            actualizarFilas(controlador.listar());
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo cargar el listado de alumnos: " + ex.getMessage());
        }
    }

    private void actualizarFilas(List<AlumnoAdmin> lista) {
        this.alumnos = lista;

        String[] columnas = {"Nombre", "Apellido", "DNI", "Email", "Teléfono", "Registrado", "Estado", "Acciones"};
        Object[][] datos = new Object[lista.size()][columnas.length];
        for (int i = 0; i < lista.size(); i++) {
            AlumnoAdmin a = lista.get(i);
            String fecha = a.getFechaRegistro() != null && a.getFechaRegistro().length() >= 10
                ? a.getFechaRegistro().substring(0, 10) : String.valueOf(a.getFechaRegistro());
            datos[i] = new Object[]{
                a.getNombre(), a.getApellido(), String.valueOf(a.getDni()), a.getEmail(), a.getTelefono(),
                fecha, a.isActivo() ? "Activo" : "Inactivo", ""
            };
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override public boolean isCellEditable(int fila, int columna) { return columna == COLUMNA_ACCIONES; }
        };
        tabla.setModel(modelo);
        ColumnaAcciones.instalar(tabla, COLUMNA_ACCIONES, construirAcciones());
        fijarAnchoColumnas();
    }

    private List<ColumnaAcciones.AccionBoton> construirAcciones() {
        List<ColumnaAcciones.AccionBoton> acciones = new ArrayList<>();

        acciones.add(new ColumnaAcciones.AccionBoton() {
            @Override public String etiqueta(int fila) { return "Modificar"; }
            @Override public IconoVectorial.Tipo icono(int fila) { return IconoVectorial.Tipo.EDITAR; }
            @Override public Color color(int fila) { return EstiloUI.AZUL_CLARO; }
            @Override public void ejecutar(int fila) {
                new DialogoFormAlumno(VentanaAdminAlumnos.this, alumnos.get(fila), controlador, VentanaAdminAlumnos.this::recargar)
                    .setVisible(true);
            }
        });

        acciones.add(new ColumnaAcciones.AccionBoton() {
            @Override public String etiqueta(int fila) { return alumnos.get(fila).isActivo() ? "Baja Lógica" : "Reactivar"; }
            @Override public IconoVectorial.Tipo icono(int fila) {
                return alumnos.get(fila).isActivo() ? IconoVectorial.Tipo.DESACTIVAR : IconoVectorial.Tipo.ACTIVAR;
            }
            @Override public Color color(int fila) { return alumnos.get(fila).isActivo() ? EstiloUI.ADVERTENCIA : EstiloUI.EXITO; }
            @Override public void ejecutar(int fila) {
                AlumnoAdmin alumno = alumnos.get(fila);
                if (alumno.isActivo()) {
                    DialogoPersonalizado.mostrarConfirmacion(VentanaAdminAlumnos.this, "Dar de baja",
                        "¿Confirmar la baja lógica de \"" + alumno.getNombre() + " " + alumno.getApellido() + "\"?\n"
                        + "El alumno no podrá iniciar sesión hasta ser reactivado.",
                        "Sí, dar de baja", () -> ejecutarCambioEstado(() -> controlador.bajaLogica(alumno.getId())));
                } else {
                    ejecutarCambioEstado(() -> controlador.reactivar(alumno.getId()));
                }
            }
        });

        acciones.add(new ColumnaAcciones.AccionBoton() {
            @Override public String etiqueta(int fila) { return "Eliminar"; }
            @Override public IconoVectorial.Tipo icono(int fila) { return IconoVectorial.Tipo.ELIMINAR; }
            @Override public Color color(int fila) { return EstiloUI.ERROR; }
            @Override public void ejecutar(int fila) {
                AlumnoAdmin alumno = alumnos.get(fila);
                DialogoPersonalizado.mostrarConfirmacion(VentanaAdminAlumnos.this, "Eliminar alumno",
                    "¿Eliminar definitivamente a \"" + alumno.getNombre() + " " + alumno.getApellido() + "\"?\n"
                    + "Esta acción no se puede deshacer: también se eliminarán sus inscripciones y resultados de tests.",
                    "Sí, eliminar", () -> ejecutarCambioEstado(() -> controlador.eliminar(alumno.getId())));
            }
        });

        return acciones;
    }

    private interface OperacionEstado { boolean ejecutar() throws SQLException; }

    private void ejecutarCambioEstado(OperacionEstado operacion) {
        try {
            operacion.ejecutar();
            recargar();
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "Error al procesar la operación: " + ex.getMessage());
        }
    }
}
