package vista.componentes;

import util.Validador;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

/**
 * Diálogo modal para que el admin elija clave y etiqueta de un ícono de curso recién subido
 * (ver {@link SelectorIconoCurso}). A partir de esa clave el ícono queda disponible como preset
 * reutilizable para cualquier curso ({@code sp_listar_iconos_curso}) — no solo para el que se
 * está editando — por eso clave y etiqueta son obligatorias y no se generan solas como antes.
 */
public class DialogoNuevoIcono extends JDialog {

    /** Clave/etiqueta que tipeó el admin. */
    public static final class Resultado {
        public final String clave;
        public final String etiqueta;
        private Resultado(String clave, String etiqueta) { this.clave = clave; this.etiqueta = etiqueta; }
    }

    private final Set<String> clavesExistentes;
    private JTextField campoEtiqueta;
    private JTextField campoClave;
    private boolean claveEditadaManualmente = false;
    private boolean actualizandoClaveProgramaticamente = false;
    private Resultado resultado;

    private DialogoNuevoIcono(JFrame padre, byte[] datosImagen, Set<String> clavesExistentes) {
        super(padre, "Nuevo ícono de curso", true);
        this.clavesExistentes = clavesExistentes;
        construirUI(padre, datosImagen);
    }

    /**
     * Muestra el diálogo y bloquea hasta que se cierra.
     * @param clavesExistentes claves ya usadas (presets + la usada internamente por el combo para
     *                         "archivo recién subido"), para avisar el choque antes de guardar
     * @return la clave/etiqueta elegidas, o null si el admin canceló
     */
    public static Resultado mostrar(JFrame padre, byte[] datosImagen, Set<String> clavesExistentes) {
        DialogoNuevoIcono dialogo = new DialogoNuevoIcono(padre, datosImagen, clavesExistentes);
        dialogo.setVisible(true);
        return dialogo.resultado;
    }

    private void construirUI(JFrame padre, byte[] datosImagen) {
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(Color.WHITE);
        raiz.setBorder(new EmptyBorder(28, 32, 24, 32));
        setContentPane(raiz);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        int fila = 0;

        JLabel nota = new JLabel("<html><div style='width:320px'>Este ícono va a quedar disponible "
            + "para elegir en cualquier curso, no solo en este.</div></html>");
        nota.setFont(EstiloUI.FUENTE_PEQUENA);
        nota.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        agregarFila(raiz, gbc, fila++, nota, new Insets(0, 0, 14, 0));

        Image miniatura = IconoCurso.cargar(datosImagen, 48);
        if (miniatura != null) {
            agregarFila(raiz, gbc, fila++, new JLabel(new ImageIcon(miniatura)), new Insets(0, 0, 14, 0));
        }

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Etiqueta (nombre visible)"), new Insets(0, 0, 6, 0));
        campoEtiqueta = FabricaUI.crearCampo();
        campoEtiqueta.setPreferredSize(new Dimension(320, EstiloUI.ALTO_CAMPO));
        agregarFila(raiz, gbc, fila++, campoEtiqueta, new Insets(0, 0, 14, 0));

        agregarFila(raiz, gbc, fila++, FabricaUI.crearEtiqueta("Clave interna"), new Insets(0, 0, 6, 0));
        campoClave = FabricaUI.crearCampo();
        agregarFila(raiz, gbc, fila++, campoClave, new Insets(0, 0, 4, 0));
        JLabel ayudaClave = new JLabel("Minúsculas, dígitos y guion bajo. Se sugiere sola desde la etiqueta.");
        ayudaClave.setFont(EstiloUI.FUENTE_PEQUENA);
        ayudaClave.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        agregarFila(raiz, gbc, fila++, ayudaClave, new Insets(0, 0, 18, 0));

        // Autocompleta la clave desde la etiqueta mientras el admin no la haya tocado a mano.
        campoEtiqueta.getDocument().addDocumentListener(soloTexto(this::sugerirClave));
        campoClave.getDocument().addDocumentListener(soloTexto(() -> {
            if (!actualizandoClaveProgramaticamente) claveEditadaManualmente = true;
        }));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        JButton botonCancelar = FabricaUI.crearBotonSecundario("Cancelar", IconoVectorial.Tipo.CANCELAR);
        botonCancelar.addActionListener(e -> dispose());
        JButton botonAgregar = FabricaUI.crearBotonPrimario("Agregar Ícono", IconoVectorial.Tipo.GUARDAR);
        botonAgregar.addActionListener(e -> confirmar(padre));
        botones.add(botonCancelar);
        botones.add(botonAgregar);
        agregarFila(raiz, gbc, fila, botones, new Insets(10, 0, 0, 0));

        setResizable(false);
        pack();
        setLocationRelativeTo(padre);
        getRootPane().setDefaultButton(botonAgregar);
    }

    private void sugerirClave() {
        if (claveEditadaManualmente) return;
        actualizandoClaveProgramaticamente = true;
        campoClave.setText(normalizarClave(campoEtiqueta.getText()));
        actualizandoClaveProgramaticamente = false;
    }

    /** Etiqueta libre → clave segura: sin tildes, minúsculas, separada por "_", máx. 50 caracteres. */
    private static String normalizarClave(String etiqueta) {
        if (etiqueta == null) return "";
        String sinTildes = Normalizer.normalize(etiqueta, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String clave = sinTildes.toLowerCase(Locale.ROOT).trim()
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
        return clave.length() > 50 ? clave.substring(0, 50) : clave;
    }

    private void confirmar(JFrame padre) {
        String etiqueta = campoEtiqueta.getText().trim();
        String clave = campoClave.getText().trim();

        if (!Validador.esEtiquetaIconoValida(etiqueta)) {
            DialogoPersonalizado.mostrarError(padre, "La etiqueta debe tener entre 2 y 50 caracteres, sin símbolos raros.");
            return;
        }
        if (!Validador.esClaveIconoValida(clave)) {
            DialogoPersonalizado.mostrarError(padre,
                "La clave debe tener 3 a 50 caracteres: minúsculas, dígitos y guion bajo, sin espacios.");
            return;
        }
        if (clavesExistentes.contains(clave)) {
            DialogoPersonalizado.mostrarError(padre, "Ya existe un ícono con la clave '" + clave + "'. Elegí otra.");
            return;
        }

        resultado = new Resultado(clave, etiqueta);
        dispose();
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila, JComponent comp, Insets insets) {
        gbc.gridy = fila;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private static DocumentListener soloTexto(Runnable accion) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { accion.run(); }
            public void removeUpdate(DocumentEvent e) { accion.run(); }
            public void changedUpdate(DocumentEvent e) { accion.run(); }
        };
    }
}
