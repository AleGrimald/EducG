package vista.admin;

import controlador.ControladorAdminCursos;
import modelo.CursoAdmin;
import vista.VentanaBase;
import vista.VentanaLogin;
import vista.componentes.ColumnaAcciones;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Módulo Cursos del panel de administrador: alta (wizard), búsqueda por nombre, listado, modificación y bajas. */
public class VentanaAdminCursos extends VentanaBase {

    private static final int COLUMNA_ACCIONES = 4;

    private final ControladorAdminCursos controlador = new ControladorAdminCursos();
    private final String emailAdmin;
    private List<CursoAdmin> cursos = new ArrayList<>();

    private JTable tabla;
    private JTextField campoBusquedaNombre;

    public VentanaAdminCursos(String emailAdmin) {
        super("Educ G", EXIT_ON_CLOSE);
        this.emailAdmin = emailAdmin;
        construirUI();
        FabricaUI.establecerIconoVentana(this);
        recargar();
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        setTitle("Educ G – " + emailAdmin);

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
        encabezado.setOpaque(true);
        encabezado.setBackground(new Color(240, 245, 250));
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel appLbl = FabricaUI.crearLogoEducG(100);


        bloqueTitulo.add(appLbl);

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

        JButton botonAlta = FabricaUI.crearBotonPrimario("Crear Curso", IconoVectorial.Tipo.AGREGAR);
        botonAlta.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaCrearCurso(emailAdmin).setVisible(true);
        });

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBusqueda.setOpaque(false);

        campoBusquedaNombre = FabricaUI.crearCampo();
        campoBusquedaNombre.setPreferredSize(new Dimension(220, EstiloUI.ALTO_CAMPO));

        JButton botonBuscar = FabricaUI.crearBotonSecundarioPequeno("Buscar por Nombre", IconoVectorial.Tipo.BUSCAR);
        botonBuscar.addActionListener(e -> buscarPorNombre());

        JButton botonVerTodos = FabricaUI.crearBotonSecundarioPequeno("Ver Todos", IconoVectorial.Tipo.LISTA);
        botonVerTodos.addActionListener(e -> { campoBusquedaNombre.setText(""); recargar(); });

        panelBusqueda.add(campoBusquedaNombre);
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

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    private void buscarPorNombre() {
        String nombre = campoBusquedaNombre.getText().trim();
        try {
            actualizarFilas(controlador.buscarPorNombre(nombre));
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "Error al buscar: " + ex.getMessage());
        }
    }

    private void recargar() {
        try {
            actualizarFilas(controlador.listar());
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo cargar el listado de cursos: " + ex.getMessage());
        }
    }

    private void actualizarFilas(List<CursoAdmin> lista) {
        this.cursos = lista;

        String[] columnas = {"", "Título", "Duración", "Estado", "Acciones"};
        Object[][] datos = new Object[lista.size()][columnas.length];
        for (int i = 0; i < lista.size(); i++) {
            CursoAdmin c = lista.get(i);
            datos[i] = new Object[]{c.getEmoji(), c.getTitulo(), c.getDuracion(), c.isActivo() ? "Activo" : "Inactivo", ""};
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override public boolean isCellEditable(int fila, int columna) { return columna == COLUMNA_ACCIONES; }
        };
        tabla.setModel(modelo);
        ColumnaAcciones.instalar(tabla, COLUMNA_ACCIONES, construirAcciones());
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private List<ColumnaAcciones.AccionBoton> construirAcciones() {
        List<ColumnaAcciones.AccionBoton> acciones = new ArrayList<>();

        acciones.add(new ColumnaAcciones.AccionBoton() {
            @Override public String etiqueta(int fila) { return "Modificar"; }
            @Override public IconoVectorial.Tipo icono(int fila) { return IconoVectorial.Tipo.EDITAR; }
            @Override public Color color(int fila) { return EstiloUI.AZUL_CLARO; }
            @Override public void ejecutar(int fila) {
                new DialogoFormCurso(VentanaAdminCursos.this, cursos.get(fila), controlador, VentanaAdminCursos.this::recargar)
                    .setVisible(true);
            }
        });

        acciones.add(new ColumnaAcciones.AccionBoton() {
            @Override public String etiqueta(int fila) { return cursos.get(fila).isActivo() ? "Baja Lógica" : "Reactivar"; }
            @Override public IconoVectorial.Tipo icono(int fila) {
                return cursos.get(fila).isActivo() ? IconoVectorial.Tipo.DESACTIVAR : IconoVectorial.Tipo.ACTIVAR;
            }
            @Override public Color color(int fila) { return cursos.get(fila).isActivo() ? EstiloUI.ADVERTENCIA : EstiloUI.EXITO; }
            @Override public void ejecutar(int fila) {
                CursoAdmin curso = cursos.get(fila);
                if (curso.isActivo()) {
                    DialogoPersonalizado.mostrarConfirmacion(VentanaAdminCursos.this, "Dar de baja",
                        "¿Confirmar la baja lógica de \"" + curso.getTitulo() + "\"?\n"
                        + "Desaparecerá del catálogo de alumnos (incluidos los ya inscriptos).",
                        "Sí, dar de baja", () -> ejecutarCambioEstado(() -> controlador.bajaLogica(curso.getId())));
                } else {
                    ejecutarCambioEstado(() -> controlador.reactivar(curso.getId()));
                }
            }
        });

        acciones.add(new ColumnaAcciones.AccionBoton() {
            @Override public String etiqueta(int fila) { return "Eliminar"; }
            @Override public IconoVectorial.Tipo icono(int fila) { return IconoVectorial.Tipo.ELIMINAR; }
            @Override public Color color(int fila) { return EstiloUI.ERROR; }
            @Override public void ejecutar(int fila) {
                CursoAdmin curso = cursos.get(fila);
                DialogoPersonalizado.mostrarConfirmacion(VentanaAdminCursos.this, "Eliminar curso",
                    "¿Eliminar definitivamente \"" + curso.getTitulo() + "\"?\n"
                    + "Esta acción no se puede deshacer: también se eliminará su plan de estudio, preguntas, "
                    + "inscripciones y resultados de tests asociados.",
                    "Sí, eliminar", () -> ejecutarCambioEstado(() -> controlador.eliminar(curso.getId())));
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
