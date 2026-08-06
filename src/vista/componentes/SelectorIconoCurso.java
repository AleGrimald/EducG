package vista.componentes;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Selector de ícono de curso: combo con los PNG de tecnología conocidos + botón para subir un
 * PNG propio desde el disco. El valor que se persiste es una {@link SeleccionIcono} — una clave
 * de {@code imagenes.clave} para los presets (y para el ícono ya asignado al editar un curso),
 * o los bytes crudos si se acaba de subir un archivo (todavía no existen como fila en la base;
 * el DAO los inserta recién al guardar). La miniatura del combo y la vista previa se leen de
 * {@code assets/} o del archivo recién elegido — nunca de la base, eso es solo UI del propio picker.
 */
public class SelectorIconoCurso extends JPanel {

    private static final String CARPETA_ASSETS = "assets/";
    private static final String CLAVE_SUBIDO = "__subido__";

    /** {clave en imagenes.clave, etiqueta legible, archivo de assets/ para la miniatura}. "" = sin ícono. */
    private static final String[][] PRESETS = {
        {"", "Sin ícono", null},
        {"icono_python", "Python", "python.png"},
        {"icono_java", "Java", "java.png"},
        {"icono_github", "GitHub", "github.png"},
        {"icono_react", "React", "react.png"},
        {"icono_sql", "SQL", "sql.png"},
        {"icono_algoritmo", "Algoritmos", "algoritmo.png"},
    };

    private static final Map<String, byte[]> CACHE_ASSETS = new HashMap<>();

    private final JFrame padre;
    private final DefaultComboBoxModel<String> modeloCombo = new DefaultComboBoxModel<>();
    private final JComboBox<String> combo = new JComboBox<>(modeloCombo);

    /** Bytes del archivo recién subido (clave sintética {@link #CLAVE_SUBIDO}), o null. */
    private byte[] datosSubidos;
    /** Clave/etiqueta que el admin eligió para el archivo subido en {@link DialogoNuevoIcono}, o null. */
    private String claveSubida;
    private String etiquetaSubida;
    /** Bytes del ícono personalizado que ya tenía el curso al editar (clave real, no preset), o null. */
    private byte[] datosClaveActual;
    private String claveActual;

    public SelectorIconoCurso(JFrame padre) {
        this.padre = padre;
        setOpaque(false);
        setLayout(new BorderLayout(8, 0));

        for (String[] par : PRESETS) modeloCombo.addElement(par[0]);
        combo.setRenderer(new RenderizadorIcono());
        combo.setMaximumRowCount(PRESETS.length + 2);
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
        claveSubida = null;
        etiquetaSubida = null;

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
            claveSubida = seleccion.getClave();
            etiquetaSubida = seleccion.getEtiqueta();
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
        if (clave.equals(CLAVE_SUBIDO)) return SeleccionIcono.deArchivoSubido(datosSubidos, claveSubida, etiquetaSubida);
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

            DialogoNuevoIcono.Resultado resultado = DialogoNuevoIcono.mostrar(padre, datos, clavesReservadas());
            if (resultado == null) return; // el admin canceló el diálogo de clave/etiqueta

            datosSubidos = datos;
            claveSubida = resultado.clave;
            etiquetaSubida = resultado.etiqueta;
            agregarEntradaSubido();
            combo.setSelectedItem(CLAVE_SUBIDO);
        } catch (IOException ex) {
            DialogoPersonalizado.mostrarError(padre, "No se pudo leer el archivo: " + ex.getMessage());
        }
    }

    /** Claves a evitar al elegir una nueva en {@link DialogoNuevoIcono}: presets + la ya asignada al editar. */
    private Set<String> clavesReservadas() {
        Set<String> claves = new HashSet<>();
        for (String[] par : PRESETS) if (!par[0].isEmpty()) claves.add(par[0]);
        if (claveActual != null) claves.add(claveActual);
        return claves;
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

    private static boolean esClavePreset(String clave) {
        for (String[] par : PRESETS) if (par[0].equals(clave)) return true;
        return false;
    }

    private static String etiquetaPreset(String clave) {
        for (String[] par : PRESETS) if (par[0].equals(clave)) return par[1];
        return clave;
    }

    private static byte[] bytesDeAsset(String archivo) {
        return CACHE_ASSETS.computeIfAbsent(archivo, a -> {
            try {
                return Files.readAllBytes(new File(CARPETA_ASSETS + a).toPath());
            } catch (IOException e) {
                return null;
            }
        });
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
            } else {
                texto = etiquetaPreset(valor);
                String archivo = null;
                for (String[] par : PRESETS) if (par[0].equals(valor)) archivo = par[2];
                datosPreview = archivo != null ? bytesDeAsset(archivo) : null;
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
