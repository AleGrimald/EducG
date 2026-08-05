package vista.admin;

import controlador.ControladorAdminCursos;
import modelo.CursoAdmin;
import modelo.ItemPlanEstudio;
import vista.VentanaBase;
import vista.VentanaLogin;
import vista.componentes.ColumnaAcciones;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoCurso;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Módulo Cursos del panel de administrador: alta (wizard), búsqueda por nombre, listado,
 * modificación y bajas, más gestión del Plan de Estudio de cada curso (agregar/ver/modificar/
 * eliminar ítems) en el panel de la derecha, que se completa al seleccionar un curso de la tabla.
 */
public class VentanaAdminCursos extends VentanaBase {

    private static final int COLUMNA_ACCIONES = 3;
    /** Anchos fijos (px): no cambian pase lo que pase con el contenido ni con la ventana. */
    private static final int ANCHO_TABLA = 760;
    private static final int ANCHO_LISTA = 560;
    private static final int ESPACIO_ENTRE_PANELES = 14;
    /** Ancho disponible (px) para el título de un ítem del plan, dejando lugar a la manija y los botones. */
    private static final int ANCHO_MAX_ETIQUETA_ITEM = 380;

    private final ControladorAdminCursos controlador = new ControladorAdminCursos();
    private final String emailAdmin;
    private List<CursoAdmin> cursos = new ArrayList<>();
    private List<ItemPlanEstudio> itemsPlan = new ArrayList<>();
    private CursoAdmin cursoSeleccionado;

    private JPanel listaPlan;
    private int arrastreOrigen = -1;
    private JPanel arrastreFilaResaltada;
    private boolean ordenModificado = false;

    private JTable tabla;
    private JTextField campoBusquedaNombre;
    private JPanel panelPlanEstudio;

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

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(EstiloUI.FONDO_SUAVE);
        panelTabla.setBorder(new EmptyBorder(16, 24, 24, 24));
        panelTabla.add(construirBarraSuperior(), BorderLayout.NORTH);
        panelTabla.add(construirTabla(), BorderLayout.CENTER);
        fijarAnchoPanel(panelTabla, ANCHO_TABLA);

        panelPlanEstudio = new JPanel(new BorderLayout());
        panelPlanEstudio.setBackground(EstiloUI.FONDO_SUAVE);
        panelPlanEstudio.setBorder(new EmptyBorder(16, 24, 24, 24));
        fijarAnchoPanel(panelPlanEstudio, ANCHO_LISTA);
        mostrarPlaceholderPlanEstudio();

        // Grupo de ancho fijo: tabla + espacio fijo + lista. Ninguno de los dos paneles cambia
        // de tamaño por más que cambie su contenido (fijarAnchoPanel deja min=preferred=max).
        JPanel grupoFijo = new JPanel();
        grupoFijo.setOpaque(false);
        grupoFijo.setLayout(new BoxLayout(grupoFijo, BoxLayout.X_AXIS));
        grupoFijo.add(panelTabla);
        grupoFijo.add(Box.createHorizontalStrut(ESPACIO_ENTRE_PANELES));
        grupoFijo.add(panelPlanEstudio);

        // El grupo fijo va centrado: los espacios a izquierda/derecha son los únicos elásticos.
        JPanel panelDividido = new JPanel(new GridBagLayout());
        panelDividido.setOpaque(false);

        GridBagConstraints gbcEspacioIzq = new GridBagConstraints();
        gbcEspacioIzq.gridx = 0;
        gbcEspacioIzq.weightx = 1;
        gbcEspacioIzq.weighty = 1;
        gbcEspacioIzq.fill = GridBagConstraints.BOTH;
        panelDividido.add(crearEspaciador(), gbcEspacioIzq);

        GridBagConstraints gbcGrupo = new GridBagConstraints();
        gbcGrupo.gridx = 1;
        gbcGrupo.weightx = 0;
        gbcGrupo.weighty = 1;
        gbcGrupo.fill = GridBagConstraints.VERTICAL;
        panelDividido.add(grupoFijo, gbcGrupo);

        GridBagConstraints gbcEspacioDer = new GridBagConstraints();
        gbcEspacioDer.gridx = 2;
        gbcEspacioDer.weightx = 1;
        gbcEspacioDer.weighty = 1;
        gbcEspacioDer.fill = GridBagConstraints.BOTH;
        panelDividido.add(crearEspaciador(), gbcEspacioDer);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        wrapper.add(construirTituloSeccion("Cursos"), BorderLayout.NORTH);
        wrapper.add(panelDividido, BorderLayout.CENTER);
        raiz.add(wrapper, BorderLayout.CENTER);
    }

    /** min = preferred = max: el panel nunca cambia de ancho, pase lo que pase con su contenido. */
    private void fijarAnchoPanel(JPanel panel, int ancho) {
        panel.setMinimumSize(new Dimension(ancho, 0));
        panel.setPreferredSize(new Dimension(ancho, 0));
        panel.setMaximumSize(new Dimension(ancho, Integer.MAX_VALUE));
    }

    private JPanel crearEspaciador() {
        JPanel espaciador = new JPanel();
        espaciador.setOpaque(false);
        return espaciador;
    }

    private JLabel construirTituloSeccion(String texto) {
        JLabel tituloSeccion = new JLabel(texto);
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloSeccion.setForeground(Color.WHITE);
        tituloSeccion.setBorder(new EmptyBorder(20, 24, 12, 24));
        tituloSeccion.setOpaque(false);
        return tituloSeccion;
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(true);
        encabezado.setBackground(EstiloUI.FONDO_SUAVE);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel appLbl = FabricaUI.crearLogoEducG(100);


        bloqueTitulo.add(appLbl);

        JButton botonCerrarSesion = FabricaUI.crearBotonSecundarioPequeno("Cerrar Sesión", IconoVectorial.Tipo.SALIR);
        botonCerrarSesion.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaLogin().setVisible(true);
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botones, BorderLayout.EAST);

        // ── Pestañas de navegación ─────────────────────────────────────────
        JPanel pestanas = crearPestanas();
        encabezado.add(pestanas, BorderLayout.SOUTH);
        return encabezado;
    }

    private JPanel crearPestanas() {
        JPanel pestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pestanas.setOpaque(false);
        pestanas.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] labels = {"Alumnos", "Cursos", "Estadísticas"};
        Runnable[] acciones = {
            () -> abrirVentana(new VentanaAdminAlumnos(emailAdmin)),
            () -> {},
            () -> abrirVentana(new VentanaAdminEstadisticas(emailAdmin))
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel pestaña = crearPestaña(labels[i], i == 1);
            final int index = i;
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (index == 1) return;
                    if (!iniciarTransicionUnica()) return;
                    dispose();
                    acciones[index].run();
                }
            });
            pestanas.add(pestaña);
        }

        return pestanas;
    }

    private JLabel crearPestaña(String texto, boolean activa) {
        JLabel pestaña = new JLabel(texto);
        pestaña.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pestaña.setForeground(activa ? new Color(37, 99, 235) : new Color(80, 100, 130));
        pestaña.setBorder(new EmptyBorder(6, 14, 6, 14));
        pestaña.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pestaña.setOpaque(false);

        if (!activa) {
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    pestaña.setForeground(new Color(37, 99, 235));
                }
                @Override public void mouseExited(MouseEvent e) {
                    pestaña.setForeground(new Color(80, 100, 130));
                }
            });
        }

        return pestaña;
    }

    private void abrirVentana(VentanaBase ventana) {
        ventana.setVisible(true);
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

        campoBusquedaNombre = FabricaUI.crearCampoConPlaceholder("Buscar por Nombre");
        campoBusquedaNombre.setPreferredSize(new Dimension(220, EstiloUI.ALTO_CAMPO));
        campoBusquedaNombre.addActionListener(e -> buscarPorNombre());

        JButton botonBuscar = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.BUSCAR, EstiloUI.AZUL_CLARO, "Buscar por Nombre");
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
        // Auto-resize por defecto (AUTO_RESIZE_ALL_COLUMNS): a diferencia de VentanaAdminAlumnos
        // (8 columnas, necesita scroll horizontal), acá con 5 columnas conviene que "Título" —la
        // única sin maxWidth fijado, ver fijarAnchoColumnas— absorba el espacio sobrante en vez de
        // dejar una franja gris del viewport sin cubrir a la derecha de "Acciones".
        // Sin esto, el viewport pinta gris por debajo de las filas cuando hay pocos cursos.
        tabla.setFillsViewportHeight(true);

        tabla.getSelectionModel().addListSelectionListener(this::alSeleccionarFila);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    private void alSeleccionarFila(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        cargarPlanEstudio(cursos.get(tabla.convertRowIndexToModel(fila)));
    }

    /**
     * "Título" es la única columna flexible (sin {@code maxWidth}): con auto-resize por defecto
     * absorbe todo el ancho sobrante del panel, así la tabla siempre cubre el 100% del espacio
     * disponible en vez de dejar una franja de viewport sin columnas. Las demás quedan fijas
     * (min = max = preferred), igual criterio que {@code VentanaAdminAlumnos.fijarAnchoColumnas}.
     */
    private void fijarAnchoColumnas() {
        FontMetrics fm = tabla.getFontMetrics(tabla.getFont());
        fijarAnchoFijo(0, 50);                  // Emoji
        fijarAnchoMinimo(1, anchoPara(fm, 18)); // Título: flexible
        fijarAnchoFijo(2, anchoPara(fm, 10));   // Estado

        aplicarRenderizadorIcono(0); // Emoji: pinta el PNG (o el placeholder) en vez del nombre de archivo
        centrar(2); // Estado
    }

    /** Pinta el ícono del curso (PNG o placeholder con la inicial) en vez del nombre de archivo en crudo. */
    private void aplicarRenderizadorIcono(int indice) {
        tabla.getColumnModel().getColumn(indice).setCellRenderer((jtable, valor, seleccionada, foco, fila, columna) -> {
            int filaModelo = tabla.convertRowIndexToModel(fila);
            byte[] datosPng = (byte[]) valor;
            String titulo = filaModelo < cursos.size() ? cursos.get(filaModelo).getTitulo() : "";
            return IconoCurso.crearEtiqueta(datosPng, titulo, 28);
        });
    }

    private int anchoPara(FontMetrics fm, int caracteres) {
        return fm.charWidth('0') * caracteres + 28;
    }

    private void fijarAnchoFijo(int indice, int ancho) {
        TableColumn columna = tabla.getColumnModel().getColumn(indice);
        columna.setMinWidth(ancho);
        columna.setMaxWidth(ancho);
        columna.setPreferredWidth(ancho);
    }

    /** Solo min/preferred: el maxWidth queda en su valor por defecto, así la columna puede crecer. */
    private void fijarAnchoMinimo(int indice, int ancho) {
        TableColumn columna = tabla.getColumnModel().getColumn(indice);
        columna.setMinWidth(ancho);
        columna.setPreferredWidth(ancho);
    }

    private void centrar(int indice) {
        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer();
        renderizador.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(indice).setCellRenderer(renderizador);
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

        String[] columnas = {"", "Título", "Estado", "Acciones"};
        Object[][] datos = new Object[lista.size()][columnas.length];
        for (int i = 0; i < lista.size(); i++) {
            CursoAdmin c = lista.get(i);
            datos[i] = new Object[]{c.getEmoji(), c.getTitulo(), c.isActivo() ? "Activo" : "Inactivo", ""};
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override public boolean isCellEditable(int fila, int columna) { return columna == COLUMNA_ACCIONES; }
        };
        tabla.setModel(modelo);
        ColumnaAcciones.instalar(tabla, COLUMNA_ACCIONES, construirAcciones());
        fijarAnchoColumnas();

        // Los índices de fila cambiaron: si había un curso seleccionado, se pierde la selección
        // y el panel del Plan de Estudio vuelve al estado vacío en vez de mostrar datos obsoletos.
        cursoSeleccionado = null;
        mostrarPlaceholderPlanEstudio();
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

    // ── Panel de Plan de Estudio (columna derecha) ──────────────────────────

    private void mostrarPlaceholderPlanEstudio() {
        panelPlanEstudio.removeAll();
        JLabel vacio = new JLabel("<html><div style='text-align:center'>Seleccioná un curso de la tabla<br>"
            + "para ver su plan de estudio.</div></html>");
        vacio.setFont(EstiloUI.FUENTE_PEQUENA);
        vacio.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        vacio.setHorizontalAlignment(SwingConstants.CENTER);
        panelPlanEstudio.add(vacio, BorderLayout.CENTER);
        panelPlanEstudio.revalidate();
        panelPlanEstudio.repaint();
    }

    private void cargarPlanEstudio(CursoAdmin curso) {
        cursoSeleccionado = curso;
        ordenModificado = false;
        try {
            itemsPlan = controlador.listarPlanEstudio(curso.getId());
            mostrarPlanEstudio();
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo cargar el plan de estudio: " + ex.getMessage());
        }
    }

    private void mostrarPlanEstudio() {
        panelPlanEstudio.removeAll();

        JPanel encabezadoPlan = new JPanel(new BorderLayout());
        encabezadoPlan.setOpaque(false);
        encabezadoPlan.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel filaTituloCurso = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaTituloCurso.setOpaque(false);
        filaTituloCurso.setBorder(new EmptyBorder(0, 0, 8, 0));
        filaTituloCurso.add(IconoCurso.crearEtiqueta(cursoSeleccionado.getEmoji(), cursoSeleccionado.getTitulo(), 30));
        JLabel tituloCurso = new JLabel(cursoSeleccionado.getTitulo());
        tituloCurso.setFont(EstiloUI.FUENTE_SECCION);
        tituloCurso.setForeground(EstiloUI.TEXTO_PRIMARIO);
        filaTituloCurso.add(tituloCurso);

        JButton botonAgregarItem = FabricaUI.crearBotonPrimarioPequeno("Agregar Ítem", IconoVectorial.Tipo.AGREGAR);
        botonAgregarItem.addActionListener(e ->
            new DialogoFormItemPlanEstudio(this, cursoSeleccionado.getId(), itemsPlan.size() + 1, null,
                controlador, () -> cargarPlanEstudio(cursoSeleccionado)).setVisible(true));

        JPanel botonesEncabezado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesEncabezado.setOpaque(false);
        if (ordenModificado) {
            JButton botonGuardarOrden = FabricaUI.crearBotonPrimarioPequeno("Guardar Orden", IconoVectorial.Tipo.GUARDAR);
            botonGuardarOrden.addActionListener(e -> guardarOrden());
            botonesEncabezado.add(botonGuardarOrden);
        }
        botonesEncabezado.add(botonAgregarItem);

        // Título y botones en filas separadas (cada una ocupa el ancho completo): con un título
        // de curso largo, compartir una sola fila los hacía competir por el espacio y se
        // encimaban con los botones.
        encabezadoPlan.add(filaTituloCurso, BorderLayout.NORTH);
        encabezadoPlan.add(botonesEncabezado, BorderLayout.SOUTH);
        panelPlanEstudio.add(encabezadoPlan, BorderLayout.NORTH);

        listaPlan = new JPanel();
        listaPlan.setOpaque(false);
        listaPlan.setLayout(new BoxLayout(listaPlan, BoxLayout.Y_AXIS));

        if (itemsPlan.isEmpty()) {
            JLabel vacio = new JLabel("Este curso todavía no tiene ítems en su plan de estudio.");
            vacio.setFont(EstiloUI.FUENTE_PEQUENA);
            vacio.setForeground(EstiloUI.TEXTO_SECUNDARIO);
            vacio.setAlignmentX(LEFT_ALIGNMENT);
            listaPlan.add(vacio);
        } else {
            for (ItemPlanEstudio item : itemsPlan) {
                listaPlan.add(construirFilaItem(item));
                listaPlan.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scroll = new JScrollPane(listaPlan);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        panelPlanEstudio.add(scroll, BorderLayout.CENTER);

        panelPlanEstudio.revalidate();
        panelPlanEstudio.repaint();
    }

    private static final Border BORDE_FILA_ITEM = new EmptyBorder(10, 14, 10, 10);

    private JPanel construirFilaItem(ItemPlanEstudio item) {
        // Fondo plano, sin la sombra de FabricaUI.crearTarjeta(): con las filas tan
        // juntas (8px de separación) esa sombra se encimaba entre una fila y la siguiente y
        // se veía como un borde continuo envolviendo toda la lista.
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EstiloUI.FONDO_SUAVE);
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
        };
        fila.setOpaque(false);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(BORDE_FILA_ITEM);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        fila.setAlignmentX(LEFT_ALIGNMENT);

        String textoCompleto = item.getOrden() + ". " + item.getTopico();
        JLabel etiqueta = new JLabel(acortarTexto(textoCompleto, ANCHO_MAX_ETIQUETA_ITEM));
        etiqueta.setFont(EstiloUI.FUENTE_ETIQUETA);
        etiqueta.setForeground(EstiloUI.TEXTO_PRIMARIO);
        etiqueta.setToolTipText(textoCompleto);

        JLabel manija = crearManijaArrastre();
        manija.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                arrastreOrigen = itemsPlan.indexOf(item);
            }
            @Override public void mouseReleased(MouseEvent e) {
                limpiarResaltadoArrastre();
                if (arrastreOrigen < 0) return;
                Point pEnLista = SwingUtilities.convertPoint(manija, e.getPoint(), listaPlan);
                int destino = calcularIndiceDestino(pEnLista);
                moverItem(arrastreOrigen, destino);
                arrastreOrigen = -1;
            }
        });
        manija.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (arrastreOrigen < 0) return;
                Point pEnLista = SwingUtilities.convertPoint(manija, e.getPoint(), listaPlan);
                resaltarFilaBajoCursor(pEnLista);
            }
        });

        JPanel izquierda = new JPanel(new BorderLayout(8, 0));
        izquierda.setOpaque(false);
        izquierda.add(manija, BorderLayout.WEST);
        izquierda.add(etiqueta, BorderLayout.CENTER);

        JButton botonVer = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.VER, EstiloUI.AZUL_CLARO, "Ver");
        botonVer.addActionListener(e -> new DialogoVerItemPlanEstudio(this, item).setVisible(true));

        JButton botonEditar = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.EDITAR, EstiloUI.AZUL_CLARO, "Modificar");
        botonEditar.addActionListener(e -> new DialogoFormItemPlanEstudio(this, cursoSeleccionado.getId(), item.getOrden(),
            item, controlador, () -> cargarPlanEstudio(cursoSeleccionado)).setVisible(true));

        JButton botonEliminar = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.ELIMINAR, EstiloUI.ERROR, "Eliminar");
        botonEliminar.addActionListener(e -> DialogoPersonalizado.mostrarConfirmacion(this, "Eliminar ítem",
            "¿Eliminar \"" + item.getTopico() + "\" del plan de estudio?\n"
            + "Los alumnos que ya cursaron esa clase no perderán su progreso, pero dejará de estar disponible.",
            "Sí, eliminar", () -> eliminarItem(item)));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        botones.setOpaque(false);
        botones.add(botonVer);
        botones.add(botonEditar);
        botones.add(botonEliminar);

        fila.add(izquierda, BorderLayout.CENTER);
        fila.add(botones, BorderLayout.EAST);
        return fila;
    }

    /** Corta el texto y agrega "…" si no entra en {@code anchoMaximo} px, para no encimarse con los botones. */
    private String acortarTexto(String texto, int anchoMaximo) {
        FontMetrics fm = getFontMetrics(EstiloUI.FUENTE_ETIQUETA);
        if (fm.stringWidth(texto) <= anchoMaximo) return texto;
        String elipsis = "…";
        int anchoElipsis = fm.stringWidth(elipsis);
        int lo = 0, hi = texto.length();
        while (lo < hi) {
            int mid = (lo + hi + 1) / 2;
            if (fm.stringWidth(texto.substring(0, mid)) + anchoElipsis <= anchoMaximo) lo = mid;
            else hi = mid - 1;
        }
        return texto.substring(0, lo).stripTrailing() + elipsis;
    }

    private JLabel crearManijaArrastre() {
        JLabel manija = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                IconoVectorial.dibujar(g2, IconoVectorial.Tipo.ARRASTRAR, 0, 0, 20, EstiloUI.TEXTO_SECUNDARIO);
                g2.dispose();
            }
        };
        manija.setPreferredSize(new Dimension(20, 20));
        manija.setToolTipText("Arrastrar para reordenar");
        manija.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        return manija;
    }

    /** @return el índice (0-based, sobre la lista actual sin remover nada todavía) donde soltar el ítem arrastrado. */
    private int calcularIndiceDestino(Point pEnLista) {
        int indice = 0;
        for (Component comp : listaPlan.getComponents()) {
            if (!(comp instanceof JPanel)) continue; // salta los Box.createVerticalStrut()
            Rectangle b = comp.getBounds();
            if (pEnLista.y < b.y + b.height / 2.0) return indice;
            indice++;
        }
        return indice;
    }

    private void resaltarFilaBajoCursor(Point pEnLista) {
        JPanel filaBajoCursor = null;
        for (Component comp : listaPlan.getComponents()) {
            if (comp instanceof JPanel && comp.getBounds().contains(pEnLista)) {
                filaBajoCursor = (JPanel) comp;
                break;
            }
        }
        if (filaBajoCursor == arrastreFilaResaltada) return;
        limpiarResaltadoArrastre();
        if (filaBajoCursor != null) {
            filaBajoCursor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, EstiloUI.AZUL_CLARO),
                new EmptyBorder(8, 14, 10, 10)));
            arrastreFilaResaltada = filaBajoCursor;
        }
    }

    private void limpiarResaltadoArrastre() {
        if (arrastreFilaResaltada != null) {
            arrastreFilaResaltada.setBorder(BORDE_FILA_ITEM);
            arrastreFilaResaltada = null;
        }
    }

    /**
     * Mueve el ítem de {@code origen} a la posición {@code destinoCrudo} (calculada sobre la
     * lista sin remover nada aún). Solo reordena en memoria — no toca la base hasta que se
     * aprieta "Guardar Orden", así varios arrastres seguidos no generan un viaje a la base cada uno.
     */
    private void moverItem(int origen, int destinoCrudo) {
        int destino = destinoCrudo > origen ? destinoCrudo - 1 : destinoCrudo;
        if (destino == origen) return;

        ItemPlanEstudio movido = itemsPlan.remove(origen);
        itemsPlan.add(destino, movido);
        for (int i = 0; i < itemsPlan.size(); i++) itemsPlan.get(i).setOrden(i + 1);

        ordenModificado = true;
        mostrarPlanEstudio();
    }

    private void guardarOrden() {
        try {
            controlador.reordenarPlan(itemsPlan);
            ordenModificado = false;
            mostrarPlanEstudio();
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo guardar el nuevo orden: " + ex.getMessage());
        }
    }

    private void eliminarItem(ItemPlanEstudio item) {
        try {
            controlador.eliminarItemPlan(item.getId());
            cargarPlanEstudio(cursoSeleccionado);
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "Error al eliminar: " + ex.getMessage());
        }
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
