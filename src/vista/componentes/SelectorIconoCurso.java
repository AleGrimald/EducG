package vista.componentes;

import dao.ImagenDAOJdbc;
import modelo.IconoPreset;
import modelo.SeleccionIcono;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Selector de ícono de curso: combo con los íconos de tecnología cargados en la tabla
 * {@code imagenes} (las filas con {@code etiqueta} no nula, vía {@code sp_listar_iconos_preset})
 * + botón para subir un PNG propio desde el disco. El valor que se persiste es una
 * {@link SeleccionIcono} — una clave de {@code imagenes.clave} para los presets (y para el ícono
 * ya asignado al editar un curso), o los bytes crudos si se acaba de subir un archivo (todavía no
 * existen como fila en la base; el DAO los inserta recién al guardar). La lista de presets y sus
 * miniaturas se traen de la base una sola vez por sesión (mismo criterio de caché estática que
 * {@code FabricaUI.CACHE_IMAGENES}) — agregar una fila nueva con {@code etiqueta} no nula la hace
 * aparecer acá sin tocar código, pero requiere reiniciar la app para que se vea (caché de sesión).
 */
public class SelectorIconoCurso extends JPanel {

    private static final String CLAVE_SUBIDO = "__subido__";

    /** Presets cargados de la base, cacheados tras la primera consulta (estáticos durante la sesión). */
    private static List<IconoPreset> CACHE_PRESETS;

    private static synchronized List<IconoPreset> obtenerPresets() {
        if (CACHE_PRESETS == null) {
            try {
                CACHE_PRESETS = new ImagenDAOJdbc().listarPresets();
            } catch (SQLException e) {
                CACHE_PRESETS = List.of(); // degradación: el selector queda solo con "Sin ícono"
            }
        }
        return CACHE_PRESETS;
    }

    /** clave -> preset, para resolver etiqueta/miniatura de cada entrada del combo. */
    private final Map<String, IconoPreset> presetsPorClave = new LinkedHashMap<>();

    /** Window (no JFrame): quien instancia este selector desde dentro de un JDialog modal
     * (ej. {@code DialogoFormCurso}) debe pasar ESE diálogo, no la ventana de más afuera —
     * ver la nota equivalente en {@link DialogoPersonalizado}. */
    private final Window padre;
    private final DefaultComboBoxModel<String> modeloCombo = new DefaultComboBoxModel<>();
    private final JComboBox<String> combo = new JComboBox<>(modeloCombo);

    /** Bytes del archivo recién subido (clave sintética {@link #CLAVE_SUBIDO}), o null. */
    private byte[] datosSubidos;
    /** Bytes del ícono personalizado que ya tenía el curso al editar (clave real, no preset), o null. */
    private byte[] datosClaveActual;
    private String claveActual;

    public SelectorIconoCurso(Window padre) {
        this.padre = padre;
        setOpaque(false);
        setLayout(new BorderLayout(8, 0));

        modeloCombo.addElement(""); // "Sin ícono"
        for (IconoPreset preset : obtenerPresets()) {
            presetsPorClave.put(preset.getClave(), preset);
            modeloCombo.addElement(preset.getClave());
        }
        combo.setRenderer(new RenderizadorIcono());
        combo.setMaximumRowCount(presetsPorClave.size() + 3);
        combo.setBackground(EstiloUI.FONDO_CAMPO);
        combo.setPreferredSize(new Dimension(0, EstiloUI.ALTO_CAMPO));

        JButton botonSubir = FabricaUI.crearBotonSecundarioPequeno("Subir PNG…", IconoVectorial.Tipo.SUBIR);
        botonSubir.addActionListener(e -> subirArchivo());

        add(combo, BorderLayout.CENTER);
        add(botonSubir, BorderLayout.EAST);
    }

    /** Precarga el selector con el ícono que ya tiene un curso existente (para el formulario de edición). */
    public void establecerActual(String claveActual, byte[] datosActuales) {
        quitarEntradaDinamica(CLAVE_SUBIDO);
        quitarEntradaDinamica(this.claveActual);
        datosSubidos = null;

        if (claveActual == null || claveActual.isBlank()) {
            combo.setSelectedItem("");
            return;
        }
        if (esClavePreset(claveActual)) {
            combo.setSelectedItem(claveActual);
            return;
        }

        // Ícono personalizado (subido en una edición anterior): agrega una entrada dinámica con
        // la clave real, así si se guarda sin tocar el selector se re-resuelve la misma fila en
        // vez de subir una copia nueva.
        this.claveActual = claveActual;
        this.datosClaveActual = datosActuales;
        modeloCombo.insertElementAt(claveActual, 1);
        combo.setSelectedItem(claveActual);
    }

    /** Restaura una selección previa (usado al volver al paso 1 del wizard "Crear Curso"). */
    public void establecerSeleccion(SeleccionIcono seleccion) {
        quitarEntradaDinamica(CLAVE_SUBIDO);
        datosSubidos = null;

        if (seleccion == null || seleccion.esNinguno()) {
            combo.setSelectedItem("");
        } else if (seleccion.esArchivoSubido()) {
            datosSubidos = seleccion.getDatos();
            agregarEntradaSubido();
            combo.setSelectedItem(CLAVE_SUBIDO);
        } else {
            combo.setSelectedItem(seleccion.getClave());
        }
    }

    public SeleccionIcono getSeleccion() {
        Object valor = combo.getSelectedItem();
        String clave = valor == null ? "" : (String) valor;
        if (clave.isEmpty()) return SeleccionIcono.ninguno();
        if (clave.equals(CLAVE_SUBIDO)) return SeleccionIcono.deArchivoSubido(datosSubidos);
        return SeleccionIcono.deClave(clave);
    }

    private void subirArchivo() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Elegir imagen del curso");
        selector.setFileFilter(new FileNameExtensionFilter("Imagen PNG", "png"));
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File archivo = selector.getSelectedFile();
        try {
            byte[] datos = Files.readAllBytes(archivo.toPath());
            if (ImageIO.read(new ByteArrayInputStream(datos)) == null) {
                DialogoPersonalizado.mostrarError(padre, "El archivo elegido no es una imagen PNG válida.");
                return;
            }
            datosSubidos = datos;
            agregarEntradaSubido();
            combo.setSelectedItem(CLAVE_SUBIDO);
        } catch (IOException ex) {
            DialogoPersonalizado.mostrarError(padre, "No se pudo leer el archivo: " + ex.getMessage());
        }
    }

    private void agregarEntradaSubido() {
        quitarEntradaDinamica(CLAVE_SUBIDO);
        modeloCombo.insertElementAt(CLAVE_SUBIDO, 1);
    }

    private void quitarEntradaDinamica(String clave) {
        if (clave == null) return;
        int indice = modeloCombo.getIndexOf(clave);
        if (indice >= 0) modeloCombo.removeElementAt(indice);
    }

    private boolean esClavePreset(String clave) {
        return presetsPorClave.containsKey(clave);
    }

    private class RenderizadorIcono extends JLabel implements ListCellRenderer<String> {
        RenderizadorIcono() {
            setOpaque(true);
            setIconTextGap(8);
            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> lista, String valor, int indice,
                                                        boolean seleccionado, boolean conFoco) {
            byte[] datosPreview;
            String texto;
            if (CLAVE_SUBIDO.equals(valor)) {
                texto = "Imagen subida";
                datosPreview = datosSubidos;
            } else if (valor != null && valor.equals(claveActual)) {
                texto = "Imagen actual";
                datosPreview = datosClaveActual;
            } else if (valor == null || valor.isEmpty()) {
                texto = "Sin ícono";
                datosPreview = null;
            } else {
                IconoPreset preset = presetsPorClave.get(valor);
                texto = preset != null ? preset.getEtiqueta() : valor;
                datosPreview = preset != null ? preset.getDatos() : null;
            }

            setText(texto);
            Image icono = datosPreview != null ? IconoCurso.cargar(datosPreview, 18) : null;
            setIcon(icono != null ? new ImageIcon(icono) : null);
            setFont(lista.getFont());
            setBackground(seleccionado ? lista.getSelectionBackground() : lista.getBackground());
            setForeground(seleccionado ? lista.getSelectionForeground() : lista.getForeground());
            return this;
        }
    }
}
