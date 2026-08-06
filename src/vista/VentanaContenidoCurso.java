package vista;

import controlador.ControladorCursos;
import controlador.ControladorTest;
import modelo.Curso;
import modelo.Leccion;
import vista.componentes.IconoCurso;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Muestra el contenido de un curso al que el usuario ya está inscripto: alterna clases y,
 * cuando una clase tiene ejercicio propuesto, un paso de ejercicio antes de poder avanzar a la
 * siguiente clase (el botón "Siguiente" queda deshabilitado hasta resolverlo correctamente).
 */
public class VentanaContenidoCurso extends VentanaBase {

    private final ControladorCursos controladorCursos = new ControladorCursos();
    private final ControladorTest controladorTest = new ControladorTest();
    private final Curso curso;
    private final String emailUsuario;
    private final String nombreUsuario;
    private final Runnable alVolver;

    private final List<Paso> pasos;
    private final Set<Integer> ejerciciosResueltos = new HashSet<>();
    private int pasoActual = 0;
    private JPanel panelContenido;

    public VentanaContenidoCurso(Curso curso, String emailUsuario, String nombreUsuario, Runnable alVolver) {
        super("Educ G", EXIT_ON_CLOSE);
        this.curso = curso;
        this.emailUsuario = emailUsuario;
        this.nombreUsuario = nombreUsuario;
        this.alVolver = alVolver;
        this.pasos = construirPasos();

        if (pasos.isEmpty()) {
            construirUIVacio();
            return;
        }

        cargarProgreso();
        construirUI();
        FabricaUI.establecerIconoVentana(this);
        activarBurbujaChatbot(emailUsuario, curso.getTitulo());
    }

    /** Un paso del circuito: la clase en sí, o (si la clase tiene ejercicio propuesto) su ejercicio, justo después. */
    private static class Paso {
        final int leccionIndex;
        final boolean esEjercicio;
        Paso(int leccionIndex, boolean esEjercicio) {
            this.leccionIndex = leccionIndex;
            this.esEjercicio = esEjercicio;
        }
    }

    private List<Paso> construirPasos() {
        List<Paso> resultado = new ArrayList<>();
        List<Leccion> lecciones = curso.getLecciones();
        for (int i = 0; i < lecciones.size(); i++) {
            resultado.add(new Paso(i, false));
            if (lecciones.get(i).tieneEjercicio()) resultado.add(new Paso(i, true));
        }
        return resultado;
    }

    private void cargarProgreso() {
        int leccionGuardada;
        try {
            leccionGuardada = controladorCursos.obtenerProgreso(emailUsuario, curso.getId());
        } catch (Exception ignored) {
            leccionGuardada = 0;
        }
        leccionGuardada = Math.max(0, Math.min(leccionGuardada, curso.getLecciones().size() - 1));

        // Reanuda siempre en la clase (nunca a mitad de un ejercicio de una sesión anterior).
        pasoActual = 0;
        for (int i = 0; i < pasos.size(); i++) {
            if (!pasos.get(i).esEjercicio && pasos.get(i).leccionIndex == leccionGuardada) {
                pasoActual = i;
                break;
            }
        }
    }

    private void construirUIVacio() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        JPanel contenedor = new JPanel();
        contenedor.setOpaque(false);
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setAlignmentX(CENTER_ALIGNMENT);
        contenedor.setAlignmentY(CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Este curso no tiene contenido disponible");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(EstiloUI.TEXTO_PRIMARIO);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel detalle = new JLabel("Por favor, volvé a la lista de cursos");
        detalle.setFont(EstiloUI.FUENTE_PEQUENA);
        detalle.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        detalle.setAlignmentX(CENTER_ALIGNMENT);

        JButton botonVolver = FabricaUI.crearBotonPrimario("Volver");
        botonVolver.setMaximumSize(new Dimension(200, EstiloUI.ALTO_BOTON));
        botonVolver.setAlignmentX(CENTER_ALIGNMENT);
        botonVolver.addActionListener(e -> {
            dispose();
            alVolver.run();
        });

        contenedor.add(Box.createVerticalGlue());
        contenedor.add(titulo);
        contenedor.add(Box.createVerticalStrut(12));
        contenedor.add(detalle);
        contenedor.add(Box.createVerticalStrut(24));
        contenedor.add(botonVolver);
        contenedor.add(Box.createVerticalGlue());

        raiz.add(contenedor, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        panelContenido = new JPanel(new BorderLayout());
        raiz.add(panelContenido, BorderLayout.CENTER);
        reconstruirContenido();
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(true);
        encabezado.setBackground(new Color(36, 91, 168, 221));
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(LEFT_ALIGNMENT);
        filaTitulo.add(IconoCurso.crearEtiqueta(curso, 36));
        JLabel tituloLbl = new JLabel(curso.getTitulo());
        tituloLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        tituloLbl.setForeground(Color.WHITE);
        filaTitulo.add(tituloLbl);

        //JLabel duracionLbl = new JLabel("⏱ " + curso.getDuracion() + "  ·  " + curso.getDescripcion());
        //duracionLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        //duracionLbl.setForeground(new Color(200, 220, 255));

        bloqueTitulo.add(filaTitulo);
        bloqueTitulo.add(Box.createVerticalStrut(4));
        //bloqueTitulo.add(duracionLbl);

        JButton botonVolver = FabricaUI.crearBotonSecundarioPequeno("Volver", IconoVectorial.Tipo.VOLVER);
        botonVolver.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            alVolver.run();
        });

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botonVolver, BorderLayout.EAST);
        return encabezado;
    }

    private Component construirContenido() {
        JPanel contenedorCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contenedorCentral.setBackground(new Color(245, 248, 252));

        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        contenido.add(construirBarraEvaluacion());
        contenido.add(Box.createVerticalStrut(24));

        Paso paso = pasos.get(pasoActual);
        Leccion leccion = curso.getLecciones().get(paso.leccionIndex);

        if (paso.esEjercicio) {
            contenido.add(construirCuerpoEjercicio(paso, leccion));
        } else {
            contenido.add(construirCuerpoLeccion(paso, leccion));
        }
        contenido.add(Box.createVerticalStrut(24));

        contenido.add(construirFilaBotones());

        contenedorCentral.add(contenido);

        JScrollPane scroll = new JScrollPane(contenedorCentral);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirCuerpoLeccion(Paso paso, Leccion leccion) {
        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));

        int total = curso.getLecciones().size();
        JLabel numeroLbl = new JLabel("Lección " + (paso.leccionIndex + 1) + " de " + total);
        numeroLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        numeroLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        numeroLbl.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(numeroLbl);
        bloque.add(Box.createVerticalStrut(8));

        JLabel tituloLeccion = new JLabel(leccion.getTitulo());
        tituloLeccion.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tituloLeccion.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloLeccion.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(tituloLeccion);
        bloque.add(Box.createVerticalStrut(14));

        bloque.add(construirCajaTexto(leccion.getContenido(), false));
        return bloque;
    }

    private JPanel construirCuerpoEjercicio(Paso paso, Leccion leccion) {
        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));

        int total = curso.getLecciones().size();
        JLabel numeroLbl = new JLabel("Ejercicio propuesto — Clase " + (paso.leccionIndex + 1) + " de " + total);
        numeroLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        numeroLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        numeroLbl.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(numeroLbl);
        bloque.add(Box.createVerticalStrut(8));

        JLabel tituloEjercicio = new JLabel("✏️  Ejercicio: " + leccion.getTitulo());
        tituloEjercicio.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tituloEjercicio.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloEjercicio.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(tituloEjercicio);
        bloque.add(Box.createVerticalStrut(14));

        bloque.add(construirCajaTexto(leccion.getEjercicioPropuesto(), true));
        bloque.add(Box.createVerticalStrut(20));

        boolean resuelto = ejerciciosResueltos.contains(paso.leccionIndex);
        if (resuelto) {
            JLabel resueltoLbl = new JLabel("✓  ¡Resuelto correctamente! Ya podés avanzar a la siguiente clase.");
            resueltoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            resueltoLbl.setForeground(EstiloUI.EXITO);
            resueltoLbl.setAlignmentX(LEFT_ALIGNMENT);
            bloque.add(resueltoLbl);
        } else {
            bloque.add(construirFormularioRespuesta(paso, leccion));
        }

        return bloque;
    }

    private JPanel construirFormularioRespuesta(Paso paso, Leccion leccion) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel etiqueta = FabricaUI.crearEtiqueta("Tu respuesta");
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(6));

        JTextArea campoRespuesta = new JTextArea(6, 40);
        campoRespuesta.setFont(EstiloUI.FUENTE_CUERPO);
        campoRespuesta.setLineWrap(true);
        campoRespuesta.setWrapStyleWord(true);
        campoRespuesta.setBackground(EstiloUI.FONDO_CAMPO);
        campoRespuesta.setForeground(EstiloUI.TEXTO_PRIMARIO);
        campoRespuesta.setBorder(new javax.swing.border.EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollRespuesta = new JScrollPane(campoRespuesta);
        scrollRespuesta.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollRespuesta.setOpaque(false);
        scrollRespuesta.setAlignmentX(LEFT_ALIGNMENT);
        scrollRespuesta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JButton botonVerificar = FabricaUI.crearBotonPrimario("Verificar", IconoVectorial.Tipo.GUARDAR);
        botonVerificar.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(scrollRespuesta);
        panel.add(Box.createVerticalStrut(8));

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaBoton.setOpaque(false);
        filaBoton.setAlignmentX(LEFT_ALIGNMENT);
        filaBoton.add(botonVerificar);
        panel.add(filaBoton);

        panel.add(Box.createVerticalStrut(8));
        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        feedbackLbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(feedbackLbl);

        Runnable verificar = () -> {
            boolean correcta = controladorCursos.verificarRespuestaEjercicio(
                campoRespuesta.getText(), leccion.getRespuestaEsperada());
            if (correcta) {
                ejerciciosResueltos.add(paso.leccionIndex);
                reconstruirContenido();
            } else {
                feedbackLbl.setText("✕  Respuesta incorrecta. Volvé a intentarlo.");
                feedbackLbl.setForeground(EstiloUI.ERROR);
            }
        };
        botonVerificar.addActionListener(e -> verificar.run());

        return panel;
    }

    private JScrollPane construirCajaTexto(String html, boolean esEjercicio) {
        // El motor HTML de Swing (HTMLEditorKit) no hace bien la cascada de font-size desde el
        // body hacia los hijos (sobre todo listas, tablas y bloques de código) — sin una regla
        // explícita por elemento, esos caen a un tamaño por defecto bastante más chico que el
        // resto del texto. Por eso cada selector de acá abajo tiene su propio font-size, en vez
        // de confiar en la herencia.
        JEditorPane contenidoLbl = new JEditorPane("text/html", "<html><head><meta charset=\"UTF-8\"><style>" +
            "body { font-family: Segoe UI, Arial; font-size: 15px; color: #7F8C8D; line-height: 1.6; margin: 0; word-wrap: break-word; }" +
            "p { font-size: 15px; margin: 10px 0; }" +
            "h1 { font-size: 24px; color: #1E0550; margin: 15px 0 10px 0; }" +
            "h2 { font-size: 20px; color: #1E0550; margin: 15px 0 10px 0; }" +
            "h3 { font-size: 17px; color: #1E0550; margin: 15px 0 10px 0; }" +
            "strong, b { font-size: inherit; }" +
            "code { background-color: #f4f4f4; padding: 2px 6px; border-radius: 3px; font-family: 'Courier New'; font-size: 14px; }" +
            "pre { background-color: #f4f4f4; padding: 10px; border-radius: 5px; overflow-x: auto; border-left: 3px solid #1E0550; }" +
            "pre code { background-color: transparent; padding: 0; font-size: 14px; }" +
            "ul, ol { margin: 10px 0; padding-left: 20px; font-size: 15px; }" +
            "li { font-size: 15px; margin: 4px 0; }" +
            "table { border-collapse: collapse; width: 100%; margin: 10px 0; font-size: 14px; }" +
            "table th, table td { border: 1px solid #ddd; padding: 8px; text-align: left; font-size: 14px; }" +
            "table th { background-color: #f4f4f4; font-weight: bold; }" +
            "a { color: #2980B9; text-decoration: none; font-size: inherit; }" +
            "a:hover { text-decoration: underline; }" +
            "</style></head><body style='padding: 10px;'>" + html + "</body></html>");
        contenidoLbl.setEditable(false);
        contenidoLbl.setOpaque(false);
        contenidoLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        contenidoLbl.addHyperlinkListener(e -> {
            if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel envoltorioTexto = new JPanel(new BorderLayout());
        envoltorioTexto.setOpaque(false);
        envoltorioTexto.add(contenidoLbl, BorderLayout.NORTH);

        // Caja de tamaño fijo (60% del alto de pantalla): el texto scrollea adentro
        // en vez de cambiar el alto de la caja, así los botones no se mueven según la lección.
        JScrollPane cajaTexto = new JScrollPane(
                envoltorioTexto,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        cajaTexto.setBorder(BorderFactory.createEmptyBorder());
        cajaTexto.setViewportBorder(BorderFactory.createEmptyBorder());
        cajaTexto.getVerticalScrollBar().setUnitIncrement(16);
        cajaTexto.setOpaque(false);
        cajaTexto.getViewport().setOpaque(false);
        cajaTexto.setAlignmentX(LEFT_ALIGNMENT);

        int anchoCaja = 980;
        if (esEjercicio) {
            // El enunciado de un ejercicio suele ser una o dos líneas: a diferencia del contenido
            // teórico, acá NO conviene reservar el mismo bloque fijo (55% de la pantalla) — dejaba
            // un hueco enorme entre el enunciado y "Tu respuesta". La caja se ajusta a su contenido
            // real, con un tope razonable por si el enunciado es largo (sigue scrolleando adentro).
            contenidoLbl.setSize(anchoCaja, Short.MAX_VALUE);
            int alturaContenido = contenidoLbl.getPreferredSize().height + 20; // padding del body
            int alturaMaxima = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.3);
            Dimension tamanoCaja = new Dimension(anchoCaja, Math.min(alturaContenido, alturaMaxima));
            cajaTexto.setPreferredSize(tamanoCaja);
            cajaTexto.setMaximumSize(new Dimension(anchoCaja, alturaMaxima));
        } else {
            // Caja de tamaño fijo (55% del alto de pantalla): el texto scrollea adentro
            // en vez de cambiar el alto de la caja, así los botones no se mueven según la lección.
            int alturaCaja = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.55);
            Dimension tamanoCaja = new Dimension(anchoCaja, alturaCaja);
            cajaTexto.setPreferredSize(tamanoCaja);
            cajaTexto.setMinimumSize(tamanoCaja);
            cajaTexto.setMaximumSize(tamanoCaja);
        }

        return cajaTexto;
    }

    private JPanel construirFilaBotones() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);

        Paso paso = pasos.get(pasoActual);
        boolean esUltimoPaso = pasoActual == pasos.size() - 1;
        boolean esPrimero = pasoActual == 0;
        boolean bloqueadoPorEjercicio = paso.esEjercicio && !ejerciciosResueltos.contains(paso.leccionIndex);

        JButton botonAnterior = FabricaUI.crearBotonSecundario("Anterior", IconoVectorial.Tipo.ANTERIOR);
        botonAnterior.setEnabled(!esPrimero);
        botonAnterior.addActionListener(e -> {
            pasoActual = Math.max(0, pasoActual - 1);
            guardarProgreso();
            reconstruirContenido();
        });
        fila.add(botonAnterior);

        JButton botonSiguiente = FabricaUI.crearBotonPrimarioIconoAlFinal(esUltimoPaso ? "Hacer Test" : "Siguiente", IconoVectorial.Tipo.SIGUIENTE);
        botonSiguiente.setEnabled(!bloqueadoPorEjercicio);
        botonSiguiente.addActionListener(e -> {
            if (esUltimoPaso) {
                abrirTest();
            } else {
                pasoActual = Math.min(pasos.size() - 1, pasoActual + 1);
                guardarProgreso();
                reconstruirContenido();
            }
        });
        fila.add(botonSiguiente);

        return fila;
    }

    private void guardarProgreso() {
        try {
            controladorCursos.actualizarProgreso(emailUsuario, curso.getId(), pasos.get(pasoActual).leccionIndex);
        } catch (Exception ignored) {}
    }

    private void reconstruirContenido() {
        panelContenido.removeAll();
        panelContenido.add(construirContenido(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /** Tarjeta con el estado del test final (aprobado/no rendido) y los botones Hacer Test / Ver Certificado. */
    private JPanel construirBarraEvaluacion() {
        int puntajeObtenido = -1;
        try {
            puntajeObtenido = controladorTest.obtenerMejorPuntaje(emailUsuario, curso.getId());
        } catch (Exception ignored) {}
        final int mejorPuntaje = puntajeObtenido;
        boolean aprobado = mejorPuntaje >= ControladorTest.puntajeAprobacion();

        JPanel tarjeta = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 4, 12, 12));
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(16, 20, 16, 20));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel bloqueEstado = new JPanel();
        bloqueEstado.setOpaque(false);
        bloqueEstado.setLayout(new BoxLayout(bloqueEstado, BoxLayout.Y_AXIS));

        JLabel estadoLbl = new JLabel(textoEstado(mejorPuntaje, aprobado));
        estadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        estadoLbl.setForeground(aprobado ? EstiloUI.EXITO : EstiloUI.TEXTO_PRIMARIO);

        JLabel detalleLbl = new JLabel(mejorPuntaje >= 0
            ? "Mejor puntaje: " + mejorPuntaje + " / 100  (aprueba con " + ControladorTest.puntajeAprobacion() + ")"
            : "Rendí el test para evaluar lo que aprendiste en este curso.");
        detalleLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        detalleLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        bloqueEstado.add(estadoLbl);
        bloqueEstado.add(Box.createVerticalStrut(3));
        bloqueEstado.add(detalleLbl);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);

        JButton botonTest = FabricaUI.crearBotonPrimario(mejorPuntaje >= 0 ? "Volver a hacer el test" : "Hacer Test");
        botonTest.addActionListener(e -> abrirTest());
        botones.add(botonTest);

        if (aprobado) {
            JButton botonCertificado = FabricaUI.crearBotonSecundario("Ver Certificado");
            botonCertificado.addActionListener(e -> abrirCertificado(mejorPuntaje));
            botones.add(botonCertificado);
        }

        tarjeta.add(bloqueEstado, BorderLayout.CENTER);
        tarjeta.add(botones, BorderLayout.EAST);
        return tarjeta;
    }

    private String textoEstado(int mejorPuntaje, boolean aprobado) {
        if (aprobado) return "✓ Curso aprobado";
        if (mejorPuntaje >= 0) return "Curso no aprobado todavía";
        return "Todavía no rendiste el test";
    }

    private void abrirTest() {
        if (!iniciarTransicionUnica()) return;
        dispose();
        new VentanaTest(curso, emailUsuario, nombreUsuario, () ->
            new VentanaContenidoCurso(curso, emailUsuario, nombreUsuario, alVolver).setVisible(true)
        ).setVisible(true);
    }

    private void abrirCertificado(int puntaje) {
        new VentanaCertificado(curso, nombreUsuario, puntaje).setVisible(true);
    }
}
