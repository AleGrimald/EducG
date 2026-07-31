package vista.admin;

import controlador.ControladorCrearCurso;
import modelo.ItemPlanEstudio;
import modelo.OpcionTest;
import modelo.PreguntaTest;
import vista.VentanaBase;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.componentes.PanelDesplegable;
import vista.componentes.SelectorIconoCurso;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wizard "Crear Curso": 4 pasos — (1) datos básicos + Plan de Estudio, (2) contenido teórico
 * por tema, (3) ejercicio propuesto opcional por tema, (4) banco de preguntas multiple-choice.
 * Todo se guarda junto (transaccional) al finalizar el paso 4, vía {@link ControladorCrearCurso}.
 */
public class VentanaCrearCurso extends VentanaBase {

    private static final String[] NOMBRES_PASO = {
        "Datos del Curso y Plan de Estudio", "Contenido Teórico", "Ejercicio Propuesto", "Preguntas del Test"
    };
    private static final int MAX_OPCIONES = 6;

    private final ControladorCrearCurso controlador = new ControladorCrearCurso();
    private final String emailAdmin;

    private int pasoActual = 1;
    private JLabel indicadorPaso;
    private JPanel panelContenido;
    private JButton botonAtras;
    private JButton botonSiguiente;

    // Paso 1
    private SelectorIconoCurso campoEmoji;
    private JTextField campoTitulo;
    private JTextArea campoDescripcion;
    private JTextField campoDuracion;
    private JTextField campoNuevoTema;
    private JPanel panelListaTemas;

    // Paso 2 / 3 (áreas de texto en el mismo orden que controlador.obtenerItems())
    private List<JTextArea> areasPaso2 = new ArrayList<>();
    private List<JTextArea> areasPaso3 = new ArrayList<>();
    private List<JTextField> camposRespuestaEsperada = new ArrayList<>();

    // Paso 4
    private JTextField campoEnunciadoPregunta;
    private JPanel panelOpcionesForm;
    private final List<JTextField> camposOpcionActual = new ArrayList<>();
    private final List<JRadioButton> radiosOpcionActual = new ArrayList<>();
    private final ButtonGroup grupoCorrectaActual = new ButtonGroup();
    private JPanel panelPreguntasAgregadas;

    public VentanaCrearCurso(String emailAdmin) {
        super("Educ G", EXIT_ON_CLOSE);
        this.emailAdmin = emailAdmin;
        construirUI();
        FabricaUI.establecerIconoVentana(this);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        setTitle("Educ G – " + emailAdmin);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setOpaque(false);

        JPanel tarjeta = FabricaUI.crearTarjeta();
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBorder(new EmptyBorder(24, 28, 20, 28));
        tarjeta.add(panelContenido, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 40, 30, 40));
        wrapper.add(tarjeta, BorderLayout.CENTER);
        wrapper.add(construirBarraNavegacion(), BorderLayout.SOUTH);

        raiz.add(wrapper, BorderLayout.CENTER);

        irAPaso(1);
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

        indicadorPaso = new JLabel();
        indicadorPaso.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        indicadorPaso.setForeground(new Color(180, 210, 255));

        bloqueTitulo.add(appLbl);
        bloqueTitulo.add(indicadorPaso);

        JButton botonCancelar = FabricaUI.crearBotonSecundarioPequeno("Cancelar", IconoVectorial.Tipo.CANCELAR);
        botonCancelar.addActionListener(e -> confirmarCancelar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(botonCancelar);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botones, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel construirBarraNavegacion() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(16, 0, 0, 0));

        botonAtras = FabricaUI.crearBotonSecundario("Atrás", IconoVectorial.Tipo.ANTERIOR);
        botonAtras.addActionListener(e -> { if (pasoActual > 1) irAPaso(pasoActual - 1); });

        botonSiguiente = FabricaUI.crearBotonPrimario("Siguiente →");
        botonSiguiente.addActionListener(e -> manejarSiguiente());

        barra.add(botonAtras, BorderLayout.WEST);
        barra.add(botonSiguiente, BorderLayout.EAST);
        return barra;
    }

    private void confirmarCancelar() {
        DialogoPersonalizado.mostrarConfirmacion(this, "Cancelar creación",
            "¿Descartar este curso? Se perderá todo lo cargado en el wizard.", "Sí, descartar",
            () -> {
                if (!iniciarTransicionUnica()) return;
                dispose();
                new VentanaAdminCursos(emailAdmin).setVisible(true);
            });
    }

    // ── Navegación entre pasos ───────────────────────────────────────────────

    private void irAPaso(int nroPaso) {
        pasoActual = nroPaso;
        panelContenido.removeAll();
        JComponent contenido;
        switch (pasoActual) {
            case 1:  contenido = construirPaso1(); break;
            case 2:  contenido = construirPaso2(); break;
            case 3:  contenido = construirPaso3(); break;
            default: contenido = construirPaso4(); break;
        }
        panelContenido.add(contenido, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();

        indicadorPaso.setText("Paso " + pasoActual + " de 4: " + NOMBRES_PASO[pasoActual - 1]);
        botonAtras.setEnabled(pasoActual > 1);
        botonSiguiente.setText(pasoActual == 4 ? "Guardar Curso" : "Siguiente →");
    }

    private void manejarSiguiente() {
        try {
            switch (pasoActual) {
                case 1:
                    controlador.establecerDatosBasicos(campoEmoji.getSeleccion(), campoTitulo.getText().trim(),
                        campoDescripcion.getText().trim(), campoDuracion.getText().trim());
                    controlador.validarPaso1();
                    irAPaso(2);
                    break;
                case 2:
                    volcarAreasEnItems(areasPaso2, true);
                    controlador.validarPaso2();
                    irAPaso(3);
                    break;
                case 3:
                    volcarPaso3EnItems();
                    controlador.validarPaso3();
                    irAPaso(4);
                    break;
                default:
                    guardarCurso();
            }
        } catch (IllegalArgumentException ex) {
            DialogoPersonalizado.mostrarError(this, ex.getMessage());
        }
    }

    private void volcarAreasEnItems(List<JTextArea> areas, boolean esContenido) {
        List<ItemPlanEstudio> items = controlador.obtenerItems();
        for (int i = 0; i < items.size(); i++) {
            String texto = areas.get(i).getText().trim();
            if (esContenido) items.get(i).setContenido(texto);
            else items.get(i).setEjercicioPropuesto(texto.isBlank() ? null : texto);
        }
    }

    private void volcarPaso3EnItems() {
        List<ItemPlanEstudio> items = controlador.obtenerItems();
        for (int i = 0; i < items.size(); i++) {
            String ejercicio = areasPaso3.get(i).getText().trim();
            String respuesta = camposRespuestaEsperada.get(i).getText().trim();
            if (ejercicio.isBlank()) {
                items.get(i).setEjercicioPropuesto(null);
                items.get(i).setRespuestaEsperada(null);
            } else {
                items.get(i).setEjercicioPropuesto(ejercicio);
                items.get(i).setRespuestaEsperada(respuesta.isBlank() ? null : respuesta);
            }
        }
    }

    private void guardarCurso() {
        try {
            controlador.guardarCurso();
            DialogoPersonalizado.mostrarExito(this, "¡Curso creado correctamente!", () -> {
                if (!iniciarTransicionUnica()) return;
                dispose();
                new VentanaAdminCursos(emailAdmin).setVisible(true);
            });
        } catch (IllegalArgumentException ex) {
            DialogoPersonalizado.mostrarError(this, ex.getMessage());
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo guardar el curso: " + ex.getMessage());
        }
    }

    // ── Paso 1: datos básicos + Plan de Estudio ──────────────────────────────

    private JComponent construirPaso1() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel filaDatos = new JPanel(new GridBagLayout());
        filaDatos.setOpaque(false);
        filaDatos.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 12);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        filaDatos.add(bloqueCampo("Ícono", campoEmoji = new SelectorIconoCurso(this)), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        filaDatos.add(bloqueCampo("Título", campoTitulo = FabricaUI.crearCampo()), gbc);
        gbc.gridx = 2; gbc.weightx = 0.35; gbc.insets = new Insets(0, 0, 12, 0);
        filaDatos.add(bloqueCampo("Duración (ej. \"8 semanas\")", campoDuracion = FabricaUI.crearCampo()), gbc);
        panel.add(filaDatos);

        panel.add(Box.createVerticalStrut(4));
        JLabel etiquetaDesc = FabricaUI.crearEtiqueta("Descripción");
        etiquetaDesc.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(etiquetaDesc);
        panel.add(Box.createVerticalStrut(6));
        campoDescripcion = new JTextArea(3, 20);
        campoDescripcion.setLineWrap(true);
        campoDescripcion.setWrapStyleWord(true);
        campoDescripcion.setFont(EstiloUI.FUENTE_CUERPO);
        campoDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true), new EmptyBorder(8, 10, 8, 10)));
        JScrollPane scrollDesc = new JScrollPane(campoDescripcion);
        scrollDesc.setAlignmentX(LEFT_ALIGNMENT);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        panel.add(scrollDesc);

        campoEmoji.establecerSeleccion(controlador.getIcono());
        campoTitulo.setText(controlador.getTitulo());
        campoDescripcion.setText(controlador.getDescripcion());
        campoDuracion.setText(controlador.getDuracion());

        panel.add(Box.createVerticalStrut(22));
        panel.add(crearSeparador());
        panel.add(Box.createVerticalStrut(16));

        JLabel tituloPlan = new JLabel("Plan de Estudio");
        tituloPlan.setFont(EstiloUI.FUENTE_SECCION);
        tituloPlan.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloPlan.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(tituloPlan);
        panel.add(Box.createVerticalStrut(4));

        JLabel ayudaPlan = new JLabel("Agregá los temas/clases del curso, en el orden en que se van a dictar.");
        ayudaPlan.setFont(EstiloUI.FUENTE_PEQUENA);
        ayudaPlan.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        ayudaPlan.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(ayudaPlan);
        panel.add(Box.createVerticalStrut(10));

        JPanel filaAgregarTema = new JPanel(new BorderLayout(8, 0));
        filaAgregarTema.setOpaque(false);
        filaAgregarTema.setAlignmentX(LEFT_ALIGNMENT);
        filaAgregarTema.setMaximumSize(new Dimension(Integer.MAX_VALUE, EstiloUI.ALTO_CAMPO));
        campoNuevoTema = FabricaUI.crearCampo();
        JButton botonAgregarTema = FabricaUI.crearBotonSecundario("Agregar Tema", IconoVectorial.Tipo.AGREGAR);
        botonAgregarTema.addActionListener(e -> agregarTema());
        filaAgregarTema.add(campoNuevoTema, BorderLayout.CENTER);
        filaAgregarTema.add(botonAgregarTema, BorderLayout.EAST);
        panel.add(filaAgregarTema);

        panel.add(Box.createVerticalStrut(14));
        panelListaTemas = new JPanel();
        panelListaTemas.setOpaque(false);
        panelListaTemas.setLayout(new BoxLayout(panelListaTemas, BoxLayout.Y_AXIS));
        panelListaTemas.setAlignmentX(LEFT_ALIGNMENT);
        actualizarListaTemas();
        panel.add(panelListaTemas);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    private void agregarTema() {
        try {
            controlador.agregarItemPlan(campoNuevoTema.getText().trim());
            campoNuevoTema.setText("");
            actualizarListaTemas();
        } catch (IllegalArgumentException ex) {
            DialogoPersonalizado.mostrarError(this, ex.getMessage());
        }
    }

    private void actualizarListaTemas() {
        panelListaTemas.removeAll();
        List<ItemPlanEstudio> items = controlador.obtenerItems();
        for (int i = 0; i < items.size(); i++) {
            int indice = i;
            ItemPlanEstudio item = items.get(i);

            JPanel fila = new JPanel(new BorderLayout(8, 0));
            fila.setOpaque(false);
            fila.setAlignmentX(LEFT_ALIGNMENT);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            fila.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel etiqueta = new JLabel((i + 1) + ".  " + item.getTopico());
            etiqueta.setFont(EstiloUI.FUENTE_CUERPO);
            etiqueta.setForeground(EstiloUI.TEXTO_PRIMARIO);

            JButton botonQuitar = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.QUITAR, EstiloUI.ERROR, "Quitar tema");
            botonQuitar.addActionListener(e -> { controlador.eliminarItemPlan(indice); actualizarListaTemas(); });

            fila.add(etiqueta, BorderLayout.CENTER);
            fila.add(botonQuitar, BorderLayout.EAST);
            panelListaTemas.add(fila);
        }
        panelListaTemas.revalidate();
        panelListaTemas.repaint();
    }

    // ── Paso 2: contenido teórico ─────────────────────────────────────────────

    private JComponent construirPaso2() {
        return construirPasoAcordeon(
            "Desplegá cada tema y completá el contenido teórico de la clase (obligatorio).",
            areasPaso2 = new ArrayList<>(), ItemPlanEstudio::getContenido);
    }

    // ── Paso 3: ejercicio propuesto (opcional) ───────────────────────────────

    private JComponent construirPaso3() {
        areasPaso3 = new ArrayList<>();
        camposRespuestaEsperada = new ArrayList<>();

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel ayudaLbl = new JLabel("Desplegá cada tema y, si querés, agregá un ejercicio propuesto (no es "
            + "obligatorio). Si le agregás un ejercicio, el alumno tiene que resolverlo antes de avanzar a la "
            + "clase siguiente, así que también hace falta la respuesta esperada para poder verificarlo.");
        ayudaLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        ayudaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        ayudaLbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(ayudaLbl);
        panel.add(Box.createVerticalStrut(14));

        List<ItemPlanEstudio> items = controlador.obtenerItems();
        for (int i = 0; i < items.size(); i++) {
            ItemPlanEstudio item = items.get(i);

            JPanel contenidoAcordeon = new JPanel();
            contenidoAcordeon.setOpaque(false);
            contenidoAcordeon.setLayout(new BoxLayout(contenidoAcordeon, BoxLayout.Y_AXIS));

            JTextArea areaEjercicio = new JTextArea(4, 30);
            areaEjercicio.setLineWrap(true);
            areaEjercicio.setWrapStyleWord(true);
            areaEjercicio.setFont(EstiloUI.FUENTE_CUERPO);
            areaEjercicio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true), new EmptyBorder(8, 10, 8, 10)));
            areaEjercicio.setText(item.getEjercicioPropuesto() == null ? "" : item.getEjercicioPropuesto());
            areasPaso3.add(areaEjercicio);

            JScrollPane scrollEjercicio = new JScrollPane(areaEjercicio);
            scrollEjercicio.setPreferredSize(new Dimension(0, 110));
            scrollEjercicio.setAlignmentX(LEFT_ALIGNMENT);
            contenidoAcordeon.add(scrollEjercicio);

            contenidoAcordeon.add(Box.createVerticalStrut(10));
            JLabel etiquetaRespuesta = FabricaUI.crearEtiqueta("Respuesta esperada (para verificar automáticamente)");
            etiquetaRespuesta.setAlignmentX(LEFT_ALIGNMENT);
            contenidoAcordeon.add(etiquetaRespuesta);
            contenidoAcordeon.add(Box.createVerticalStrut(6));

            JTextField campoRespuesta = FabricaUI.crearCampo();
            campoRespuesta.setAlignmentX(LEFT_ALIGNMENT);
            campoRespuesta.setMaximumSize(new Dimension(Integer.MAX_VALUE, EstiloUI.ALTO_CAMPO));
            campoRespuesta.setText(item.getRespuestaEsperada() == null ? "" : item.getRespuestaEsperada());
            camposRespuestaEsperada.add(campoRespuesta);
            contenidoAcordeon.add(campoRespuesta);

            PanelDesplegable acordeon = new PanelDesplegable((i + 1) + ".  " + item.getTopico(), contenidoAcordeon);
            acordeon.setAlignmentX(LEFT_ALIGNMENT);
            if (i == 0) acordeon.expandir();
            panel.add(acordeon);
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    private interface ExtractorTexto { String extraer(ItemPlanEstudio item); }

    private JComponent construirPasoAcordeon(String ayuda, List<JTextArea> areasDestino, ExtractorTexto extractor) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel ayudaLbl = new JLabel(ayuda);
        ayudaLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        ayudaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        ayudaLbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(ayudaLbl);
        panel.add(Box.createVerticalStrut(14));

        List<ItemPlanEstudio> items = controlador.obtenerItems();
        for (int i = 0; i < items.size(); i++) {
            ItemPlanEstudio item = items.get(i);
            JTextArea area = new JTextArea(5, 30);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(EstiloUI.FUENTE_CUERPO);
            area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true), new EmptyBorder(8, 10, 8, 10)));
            area.setText(extractor.extraer(item));
            areasDestino.add(area);

            JScrollPane scrollArea = new JScrollPane(area);
            scrollArea.setPreferredSize(new Dimension(0, 130));

            PanelDesplegable acordeon = new PanelDesplegable((i + 1) + ".  " + item.getTopico(), scrollArea);
            acordeon.setAlignmentX(LEFT_ALIGNMENT);
            if (i == 0) acordeon.expandir();
            panel.add(acordeon);
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    // ── Paso 4: preguntas multiple choice ────────────────────────────────────

    private JComponent construirPaso4() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel tituloForm = new JLabel("Nueva pregunta");
        tituloForm.setFont(EstiloUI.FUENTE_SECCION);
        tituloForm.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloForm.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(tituloForm);
        panel.add(Box.createVerticalStrut(10));

        JLabel etiquetaEnunciado = FabricaUI.crearEtiqueta("Enunciado");
        etiquetaEnunciado.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(etiquetaEnunciado);
        panel.add(Box.createVerticalStrut(6));
        campoEnunciadoPregunta = FabricaUI.crearCampo();
        campoEnunciadoPregunta.setAlignmentX(LEFT_ALIGNMENT);
        campoEnunciadoPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, EstiloUI.ALTO_CAMPO));
        panel.add(campoEnunciadoPregunta);
        panel.add(Box.createVerticalStrut(12));

        JLabel etiquetaOpciones = FabricaUI.crearEtiqueta("Opciones (marcá la correcta)");
        etiquetaOpciones.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(etiquetaOpciones);
        panel.add(Box.createVerticalStrut(6));

        panelOpcionesForm = new JPanel();
        panelOpcionesForm.setOpaque(false);
        panelOpcionesForm.setLayout(new BoxLayout(panelOpcionesForm, BoxLayout.Y_AXIS));
        panelOpcionesForm.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(panelOpcionesForm);
        reiniciarFormularioPregunta();

        JButton botonAgregarOpcion = FabricaUI.crearBotonSecundarioPequeno("Agregar Opción", IconoVectorial.Tipo.AGREGAR);
        botonAgregarOpcion.setAlignmentX(LEFT_ALIGNMENT);
        botonAgregarOpcion.addActionListener(e -> agregarFilaOpcion());
        panel.add(Box.createVerticalStrut(8));
        panel.add(botonAgregarOpcion);

        panel.add(Box.createVerticalStrut(14));
        JButton botonAgregarPregunta = FabricaUI.crearBotonPrimario("Agregar Pregunta", IconoVectorial.Tipo.AGREGAR);
        botonAgregarPregunta.setAlignmentX(LEFT_ALIGNMENT);
        botonAgregarPregunta.addActionListener(e -> agregarPregunta());
        panel.add(botonAgregarPregunta);

        panel.add(Box.createVerticalStrut(24));
        panel.add(crearSeparador());
        panel.add(Box.createVerticalStrut(16));

        JLabel tituloLista = new JLabel("Preguntas agregadas");
        tituloLista.setFont(EstiloUI.FUENTE_SECCION);
        tituloLista.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloLista.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(tituloLista);
        panel.add(Box.createVerticalStrut(10));

        panelPreguntasAgregadas = new JPanel();
        panelPreguntasAgregadas.setOpaque(false);
        panelPreguntasAgregadas.setLayout(new BoxLayout(panelPreguntasAgregadas, BoxLayout.Y_AXIS));
        panelPreguntasAgregadas.setAlignmentX(LEFT_ALIGNMENT);
        actualizarListaPreguntas();
        panel.add(panelPreguntasAgregadas);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    private void reiniciarFormularioPregunta() {
        camposOpcionActual.clear();
        radiosOpcionActual.clear();
        for (java.util.Enumeration<AbstractButton> e = grupoCorrectaActual.getElements(); e.hasMoreElements(); )
            grupoCorrectaActual.remove(e.nextElement());
        agregarFilaOpcion();
        agregarFilaOpcion();
    }

    private void agregarFilaOpcion() {
        if (camposOpcionActual.size() >= MAX_OPCIONES) return;
        JRadioButton radio = new JRadioButton();
        radio.setOpaque(false);
        JTextField campo = FabricaUI.crearCampo();
        camposOpcionActual.add(campo);
        radiosOpcionActual.add(radio);
        grupoCorrectaActual.add(radio);
        actualizarPanelOpcionesForm();
    }

    private void quitarFilaOpcion(int indice) {
        if (camposOpcionActual.size() <= 2) return;
        grupoCorrectaActual.remove(radiosOpcionActual.get(indice));
        camposOpcionActual.remove(indice);
        radiosOpcionActual.remove(indice);
        actualizarPanelOpcionesForm();
    }

    private void actualizarPanelOpcionesForm() {
        panelOpcionesForm.removeAll();
        for (int i = 0; i < camposOpcionActual.size(); i++) {
            int indice = i;
            JPanel fila = new JPanel(new BorderLayout(8, 0));
            fila.setOpaque(false);
            fila.setAlignmentX(LEFT_ALIGNMENT);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, EstiloUI.ALTO_CAMPO));
            fila.setBorder(new EmptyBorder(0, 0, 6, 0));

            JPanel oeste = new JPanel(new BorderLayout(6, 0));
            oeste.setOpaque(false);
            oeste.add(radiosOpcionActual.get(i), BorderLayout.WEST);
            oeste.add(camposOpcionActual.get(i), BorderLayout.CENTER);
            fila.add(oeste, BorderLayout.CENTER);

            if (camposOpcionActual.size() > 2) {
                JButton botonQuitar = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.QUITAR, EstiloUI.ERROR, "Quitar opción");
                botonQuitar.addActionListener(e -> quitarFilaOpcion(indice));
                fila.add(botonQuitar, BorderLayout.EAST);
            }
            panelOpcionesForm.add(fila);
        }
        panelOpcionesForm.revalidate();
        panelOpcionesForm.repaint();
    }

    private void agregarPregunta() {
        List<String> textos = new ArrayList<>();
        for (JTextField campo : camposOpcionActual) textos.add(campo.getText());
        int indiceCorrecta = -1;
        for (int i = 0; i < radiosOpcionActual.size(); i++) if (radiosOpcionActual.get(i).isSelected()) indiceCorrecta = i;

        try {
            controlador.agregarPregunta(campoEnunciadoPregunta.getText().trim(), textos, indiceCorrecta);
            campoEnunciadoPregunta.setText("");
            reiniciarFormularioPregunta();
            actualizarListaPreguntas();
        } catch (IllegalArgumentException ex) {
            DialogoPersonalizado.mostrarError(this, ex.getMessage());
        }
    }

    private void actualizarListaPreguntas() {
        panelPreguntasAgregadas.removeAll();
        List<PreguntaTest> preguntas = controlador.obtenerPreguntas();
        if (preguntas.isEmpty()) {
            JLabel vacioLbl = new JLabel("Todavía no agregaste ninguna pregunta.");
            vacioLbl.setFont(EstiloUI.FUENTE_PEQUENA);
            vacioLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
            vacioLbl.setAlignmentX(LEFT_ALIGNMENT);
            panelPreguntasAgregadas.add(vacioLbl);
        }
        for (int i = 0; i < preguntas.size(); i++) {
            int indice = i;
            PreguntaTest pregunta = preguntas.get(i);

            JPanel tarjeta = new JPanel();
            tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
            tarjeta.setOpaque(true);
            tarjeta.setBackground(EstiloUI.FONDO_GRIS_CLARO);
            tarjeta.setAlignmentX(LEFT_ALIGNMENT);
            tarjeta.setBorder(new EmptyBorder(10, 14, 10, 14));
            tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            JPanel filaSuperior = new JPanel(new BorderLayout());
            filaSuperior.setOpaque(false);
            filaSuperior.setAlignmentX(LEFT_ALIGNMENT);
            JLabel enunciadoLbl = new JLabel((i + 1) + ". " + pregunta.getEnunciado());
            enunciadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            enunciadoLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
            JButton botonQuitar = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.QUITAR, EstiloUI.ERROR, "Quitar pregunta");
            botonQuitar.addActionListener(e -> { controlador.eliminarPregunta(indice); actualizarListaPreguntas(); });
            filaSuperior.add(enunciadoLbl, BorderLayout.CENTER);
            filaSuperior.add(botonQuitar, BorderLayout.EAST);
            tarjeta.add(filaSuperior);
            tarjeta.add(Box.createVerticalStrut(4));

            for (OpcionTest opcion : pregunta.getOpciones()) {
                JLabel opcionLbl = new JLabel((opcion.isCorrecta() ? "✓  " : "•  ") + opcion.getTexto());
                opcionLbl.setFont(EstiloUI.FUENTE_PEQUENA);
                opcionLbl.setForeground(opcion.isCorrecta() ? EstiloUI.EXITO : EstiloUI.TEXTO_SECUNDARIO);
                opcionLbl.setAlignmentX(LEFT_ALIGNMENT);
                tarjeta.add(opcionLbl);
            }

            panelPreguntasAgregadas.add(tarjeta);
            panelPreguntasAgregadas.add(Box.createVerticalStrut(8));
        }
        panelPreguntasAgregadas.revalidate();
        panelPreguntasAgregadas.repaint();
    }

    // ── Auxiliares ────────────────────────────────────────────────────────────

    private JPanel bloqueCampo(String textoEtiqueta, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = FabricaUI.crearEtiqueta(textoEtiqueta);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        campo.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(campo);
        return p;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(EstiloUI.BORDE);
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }
}
