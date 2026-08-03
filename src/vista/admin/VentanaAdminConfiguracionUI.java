package vista.admin;

import controlador.ControladorAdminConfiguracionUI;
import modelo.ConfiguracionUI;
import vista.VentanaBase;
import vista.VentanaLogin;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.componentes.ColumnaAcciones;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.EventObject;
import java.util.List;

/** Módulo Configuración de UI del panel de administrador. */
public class VentanaAdminConfiguracionUI extends VentanaBase {

    private final ControladorAdminConfiguracionUI controlador = new ControladorAdminConfiguracionUI();
    private final String emailAdmin;
    private List<ConfiguracionUI> configuraciones;
    private Set<String> modulos = new TreeSet<>();
    private Map<String, Set<String>> seccionesPorModulo = new TreeMap<>();

    private JTable tabla;
    private JComboBox<String> selectModulo;
    private JComboBox<String> selectSeccion;

    private JTextField campoValor;
    private JLabel previewLabel;
    private JPanel panelPreview;
    private ConfiguracionUI configSeleccionada;

    public VentanaAdminConfiguracionUI(String emailAdmin) {
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

        contenido.add(construirFiltros(), BorderLayout.NORTH);

        // Split pane: tabla (40%) | preview (60%)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(construirTabla());
        split.setRightComponent(construirPanelEdicion());
        split.setDividerLocation(0.4);
        split.setResizeWeight(0.4);
        split.setDividerSize(8);
        split.setBackground(new Color(245, 248, 252));

        contenido.add(split, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        wrapper.add(construirTituloSeccion("Configuración de UI"), BorderLayout.NORTH);
        wrapper.add(contenido, BorderLayout.CENTER);
        raiz.add(wrapper, BorderLayout.CENTER);
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
        encabezado.setBackground(new Color(240, 245, 250));
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));
        bloqueTitulo.add(FabricaUI.crearLogoEducG(100));

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

        JPanel pestanas = crearPestanas();
        encabezado.add(pestanas, BorderLayout.SOUTH);
        return encabezado;
    }

    private JPanel crearPestanas() {
        JPanel pestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pestanas.setOpaque(false);
        pestanas.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] labels = {"Alumnos", "Cursos", "Estadísticas", "Configuración"};
        Runnable[] acciones = {
            () -> abrirVentana(new VentanaAdminAlumnos(emailAdmin)),
            () -> abrirVentana(new VentanaAdminCursos(emailAdmin)),
            () -> abrirVentana(new VentanaAdminEstadisticas(emailAdmin)),
            () -> {}
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel pestaña = crearPestaña(labels[i], i == 3);
            final int index = i;
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (index == 3) return;
                    if (!iniciarTransicionUnica()) return;
                    dispose();
                    acciones[index].run();
                }
            });
            pestanas.add(pestaña);
        }
        return pestanas;
    }

    private JScrollPane construirTabla() {
        tabla = new JTable();
        tabla.setFont(EstiloUI.FUENTE_ETIQUETA);
        tabla.setRowHeight(44);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setSelectionBackground(new Color(220, 235, 255));
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    private void recargar() {
        try {
            configuraciones = controlador.listar();
            construirMapaModulosSecciones();
            actualizarSelectores();
            actualizarFilas();
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "Error al cargar configuraciones: " + ex.getMessage());
        }
    }

    private void construirMapaModulosSecciones() {
        modulos.clear();
        seccionesPorModulo.clear();
        for (ConfiguracionUI c : configuraciones) {
            modulos.add(c.getModulo());
            seccionesPorModulo.computeIfAbsent(c.getModulo(), k -> new TreeSet<>())
                .add(c.getSeccion());
        }
    }

    private void actualizarSelectores() {
        selectModulo.removeAllItems();
        for (String mod : modulos) {
            selectModulo.addItem(mod);
        }
        if (!modulos.isEmpty()) {
            selectModulo.setSelectedIndex(0);
        }
    }

    private void actualizarFilas() {
        String moduloSeleccionado = (String) selectModulo.getSelectedItem();
        String seccionSeleccionada = (String) selectSeccion.getSelectedItem();

        List<ConfiguracionUI> filtradas = new ArrayList<>();
        for (ConfiguracionUI c : configuraciones) {
            if (c.getModulo().equals(moduloSeleccionado) && c.getSeccion().equals(seccionSeleccionada)) {
                filtradas.add(c);
            }
        }

        String[] columnas = {"Clave", "Valor", "Tipo", "Descripción", "Acciones"};
        Object[][] datos = new Object[filtradas.size()][columnas.length];
        for (int i = 0; i < filtradas.size(); i++) {
            ConfiguracionUI c = filtradas.get(i);
            datos[i] = new Object[]{c.getClave(), c.getValor(), c.getTipo(), c.getDescripcion(), ""};
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override public boolean isCellEditable(int fila, int columna) { return columna == 4; }
        };
        tabla.setModel(modelo);
        fijarAnchos();
        agregarBotonGuardar();
        limpiarEditor();
    }

    private List<ConfiguracionUI> obtenerFiltradasActuales() {
        String moduloSeleccionado = (String) selectModulo.getSelectedItem();
        String seccionSeleccionada = (String) selectSeccion.getSelectedItem();

        List<ConfiguracionUI> filtradas = new ArrayList<>();
        for (ConfiguracionUI c : configuraciones) {
            if (c.getModulo().equals(moduloSeleccionado) && c.getSeccion().equals(seccionSeleccionada)) {
                filtradas.add(c);
            }
        }
        return filtradas;
    }

    private ConfiguracionUI obtenerFilaFiltrada(int fila) {
        List<ConfiguracionUI> filtradas = obtenerFiltradasActuales();
        if (fila >= 0 && fila < filtradas.size()) {
            return filtradas.get(fila);
        }
        return null;
    }

    private void fijarAnchos() {
        tabla.getColumnModel().getColumn(0).setMaxWidth(150);
        tabla.getColumnModel().getColumn(1).setMaxWidth(150);
        tabla.getColumnModel().getColumn(2).setMaxWidth(100);
        tabla.getColumnModel().getColumn(4).setMaxWidth(100);
    }

    private JPanel construirFiltros() {
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filtros.setOpaque(false);

        JLabel etiqModulo = new JLabel("Módulo:");
        etiqModulo.setFont(EstiloUI.FUENTE_ETIQUETA);
        etiqModulo.setForeground(EstiloUI.TEXTO_PRIMARIO);

        selectModulo = new JComboBox<>();
        selectModulo.setPreferredSize(new Dimension(160, 32));
        selectModulo.setFont(EstiloUI.FUENTE_CUERPO);
        selectModulo.addActionListener(e -> {
            actualizarSeccionesDelModulo();
            actualizarFilas();
        });

        JLabel etiqSeccion = new JLabel("Sección:");
        etiqSeccion.setFont(EstiloUI.FUENTE_ETIQUETA);
        etiqSeccion.setForeground(EstiloUI.TEXTO_PRIMARIO);

        selectSeccion = new JComboBox<>();
        selectSeccion.setPreferredSize(new Dimension(200, 32));
        selectSeccion.setFont(EstiloUI.FUENTE_CUERPO);
        selectSeccion.addActionListener(e -> actualizarFilas());

        filtros.add(etiqModulo);
        filtros.add(selectModulo);
        filtros.add(etiqSeccion);
        filtros.add(selectSeccion);

        return filtros;
    }

    private void actualizarSeccionesDelModulo() {
        selectSeccion.removeAllItems();
        String moduloSeleccionado = (String) selectModulo.getSelectedItem();
        if (moduloSeleccionado != null) {
            Set<String> secciones = seccionesPorModulo.get(moduloSeleccionado);
            if (secciones != null) {
                for (String sec : secciones) {
                    selectSeccion.addItem(sec);
                }
            }
        }
        if (selectSeccion.getItemCount() > 0) {
            selectSeccion.setSelectedIndex(0);
        }
    }

    private void agregarBotonGuardar() {
        List<ColumnaAcciones.AccionBoton> acciones = new ArrayList<>();
        acciones.add(new ColumnaAcciones.AccionBoton() {
            @Override public String etiqueta(int fila) { return "Editar"; }
            @Override public IconoVectorial.Tipo icono(int fila) { return IconoVectorial.Tipo.EDITAR; }
            @Override public void ejecutar(int fila) {
                cargarEnEditor(obtenerFilaFiltrada(fila));
            }
        });
        ColumnaAcciones.instalar(tabla, 4, acciones);
    }

    private JPanel construirPanelEdicion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Editar Configuración");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(EstiloUI.TEXTO_PRIMARIO);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setOpaque(false);
        formulario.setBorder(new EmptyBorder(16, 0, 16, 0));

        // Clave (no editable)
        formulario.add(crearFilaFormulario("Clave:", new JLabel("---"), false));
        formulario.add(Box.createVerticalStrut(12));

        // Tipo (no editable)
        formulario.add(crearFilaFormulario("Tipo:", new JLabel("---"), false));
        formulario.add(Box.createVerticalStrut(12));

        // Valor (editable)
        campoValor = new JTextField();
        campoValor.setFont(EstiloUI.FUENTE_CUERPO);
        campoValor.setBackground(EstiloUI.FONDO_CAMPO);
        campoValor.setForeground(EstiloUI.TEXTO_PRIMARIO);
        campoValor.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        campoValor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formulario.add(crearFilaFormulario("Valor:", campoValor, true));
        formulario.add(Box.createVerticalStrut(20));

        // Preview
        JLabel etiqPreview = new JLabel("Preview:");
        etiqPreview.setFont(new Font("Segoe UI", Font.BOLD, 14));
        etiqPreview.setForeground(EstiloUI.TEXTO_PRIMARIO);
        formulario.add(etiqPreview);
        formulario.add(Box.createVerticalStrut(8));

        panelPreview = new JPanel(new BorderLayout());
        panelPreview.setOpaque(true);
        panelPreview.setBackground(Color.WHITE);
        panelPreview.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1));
        panelPreview.setPreferredSize(new Dimension(0, 120));

        previewLabel = new JLabel("Selecciona un parámetro para editar");
        previewLabel.setFont(EstiloUI.FUENTE_CUERPO);
        previewLabel.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        panelPreview.add(previewLabel, BorderLayout.CENTER);

        formulario.add(panelPreview);
        formulario.add(Box.createVerticalGlue());

        // Botón Guardar
        JButton btnGuardar = FabricaUI.crearBotonPrimario("Guardar Cambios");
        btnGuardar.addActionListener(e -> guardarDesdeEditor());
        formulario.add(btnGuardar);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(formulario, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearFilaFormulario(String etiqueta, JComponent componente, boolean editable) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(EstiloUI.FUENTE_ETIQUETA);
        lbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        lbl.setPreferredSize(new Dimension(60, 40));

        if (componente instanceof JTextField) {
            componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            componente.setPreferredSize(new Dimension(200, 40));
        } else if (componente instanceof JLabel) {
            componente.setPreferredSize(new Dimension(200, 40));
        }

        fila.add(lbl);
        fila.add(componente);

        return fila;
    }

    private void cargarEnEditor(ConfiguracionUI config) {
        if (config == null) return;
        configSeleccionada = config;

        tabla.clearSelection();
        tabla.addRowSelectionInterval(tabla.getSelectedRow(), tabla.getSelectedRow());

        // Actualizar campos
        ((JLabel) tabla.getParent()).repaint(); // sin efecto real, solo fuerza actualización

        JPanel formulario = (JPanel) ((JPanel) tabla.getParent().getParent().getComponent(0)).getComponent(1);

        // Actualizar preview
        actualizarPreview(config);
        campoValor.setText(config.getValor());
        campoValor.requestFocus();
    }

    private void actualizarPreview(ConfiguracionUI config) {
        panelPreview.removeAll();

        String tipo = config.getTipo();
        if (tipo.equals("color")) {
            JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
            contenedor.setOpaque(true);
            contenedor.setBackground(Color.WHITE);

            JLabel labelColor = new JLabel(config.getClave() + ":");
            labelColor.setFont(EstiloUI.FUENTE_ETIQUETA);

            JPanel muestraColor = new JPanel();
            try {
                muestraColor.setBackground(Color.decode(config.getValor()));
            } catch (NumberFormatException ex) {
                muestraColor.setBackground(Color.LIGHT_GRAY);
            }
            muestraColor.setPreferredSize(new Dimension(60, 60));
            muestraColor.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            contenedor.add(labelColor);
            contenedor.add(muestraColor);

            panelPreview.add(contenedor, BorderLayout.WEST);
        } else if (tipo.equals("fuente")) {
            JLabel preview = new JLabel(config.getClave() + ": " + config.getValor());
            try {
                preview.setFont(new Font(config.getValor(), Font.PLAIN, 14));
            } catch (Exception ex) {
                preview.setFont(EstiloUI.FUENTE_CUERPO);
            }
            preview.setForeground(EstiloUI.TEXTO_PRIMARIO);
            panelPreview.add(preview, BorderLayout.WEST);
        } else {
            JLabel preview = new JLabel(config.getClave() + ": " + config.getValor());
            preview.setFont(EstiloUI.FUENTE_CUERPO);
            preview.setForeground(EstiloUI.TEXTO_PRIMARIO);
            panelPreview.add(preview, BorderLayout.WEST);
        }

        panelPreview.revalidate();
        panelPreview.repaint();
    }

    private void limpiarEditor() {
        configSeleccionada = null;
        campoValor.setText("");
        panelPreview.removeAll();
        previewLabel.setText("Selecciona un parámetro para editar");
        panelPreview.add(previewLabel, BorderLayout.CENTER);
        panelPreview.revalidate();
        panelPreview.repaint();
    }

    private void guardarDesdeEditor() {
        if (configSeleccionada == null) {
            DialogoPersonalizado.mostrarError(this, "Selecciona una configuración primero.");
            return;
        }

        try {
            String nuevoValor = campoValor.getText();
            if (controlador.actualizar(configSeleccionada.getClave(), nuevoValor)) {
                DialogoPersonalizado.mostrarExito(this, "Configuración actualizada.");
                recargar();
            }
        } catch (IllegalArgumentException ex) {
            DialogoPersonalizado.mostrarError(this, ex.getMessage());
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "Error al actualizar: " + ex.getMessage());
        }
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
}
