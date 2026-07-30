package vista;

import controlador.ControladorCursos;
import controlador.ControladorTest;
import modelo.Curso;
import modelo.Leccion;
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
        encabezado.setBackground(new Color(240, 245, 250));
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel(curso.getEmoji() + "  " + curso.getTitulo());
        tituloLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        tituloLbl.setForeground(Color.WHITE);

        JLabel duracionLbl = new JLabel("⏱ " + curso.getDuracion() + "  ·  " + curso.getDescripcion());
        duracionLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        duracionLbl.setForeground(new Color(200, 220, 255));

        bloqueTitulo.add(tituloLbl);
        bloqueTitulo.add(Box.createVerticalStrut(4));
        bloqueTitulo.add(duracionLbl);

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

        bloque.add(construirCajaTexto(leccion.getContenido()));
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

        bloque.add(construirCajaTexto(leccion.getEjercicioPropuesto()));
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

        JPanel filaRespuesta = new JPanel(new BorderLayout(10, 0));
        filaRespuesta.setOpaque(false);
        filaRespuesta.setAlignmentX(LEFT_ALIGNMENT);
        filaRespuesta.setMaximumSize(new Dimension(600, EstiloUI.ALTO_CAMPO));

        JTextField campoRespuesta = FabricaUI.crearCampo();
        JButton botonVerificar = FabricaUI.crearBotonPrimario("Verificar", IconoVectorial.Tipo.GUARDAR);

        filaRespuesta.add(campoRespuesta, BorderLayout.CENTER);
        filaRespuesta.add(botonVerificar, BorderLayout.EAST);
        panel.add(filaRespuesta);

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
        campoRespuesta.addActionListener(e -> verificar.run());

        return panel;
    }

    private JScrollPane construirCajaTexto(String textoHtml) {
        JLabel contenidoLbl = new JLabel("<html><body style='width: 700px'>" + textoHtml + "</body></html>");
        contenidoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contenidoLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        contenidoLbl.setVerticalAlignment(SwingConstants.TOP);

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

        int alturaCaja = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4);
        Dimension tamanoCaja = new Dimension(748, alturaCaja);
        cajaTexto.setPreferredSize(tamanoCaja);
        cajaTexto.setMinimumSize(tamanoCaja);
        cajaTexto.setMaximumSize(tamanoCaja);

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
        new VentanaTest(curso, emailUsuario, () ->
            new VentanaContenidoCurso(curso, emailUsuario, nombreUsuario, alVolver).setVisible(true)
        ).setVisible(true);
    }

    private void abrirCertificado(int puntaje) {
        new VentanaCertificado(curso, nombreUsuario, puntaje).setVisible(true);
    }
}
