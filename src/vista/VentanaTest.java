package vista;

import controlador.ControladorTest;
import modelo.Curso;
import modelo.OpcionTest;
import modelo.PreguntaTest;
import modelo.ResultadoTestGuardado;
import vista.componentes.BarraProgreso;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoCurso;
import vista.componentes.IconoVectorial;
import vista.componentes.PanelCertificado;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Test final (multiple choice) de un curso: 10 preguntas, corrección y guardado del resultado. */
public class VentanaTest extends VentanaBase {

    private final ControladorTest controlador = new ControladorTest();
    private final Curso curso;
    private final String emailUsuario;
    private final String nombreUsuario;
    private final Runnable alVolver;

    private List<PreguntaTest> preguntas = new ArrayList<>();
    private String errorCarga;
    private int preguntaIndexActual = 0;
    private final Map<Integer, Integer> respuestasSeleccionadas = new HashMap<>();
    private BarraProgreso barraProgreso;
    private JLabel textoPreguntaLbl;
    private JButton botonVolverEncabezado;
    private JPanel panelBotonesEncabezado;
    private JPanel panelPreguntas;

    public VentanaTest(Curso curso, String emailUsuario, String nombreUsuario, Runnable alVolver) {
        super("Educ G", EXIT_ON_CLOSE);
        this.curso = curso;
        this.emailUsuario = emailUsuario;
        this.nombreUsuario = nombreUsuario;
        this.alVolver = alVolver;
        cargarPreguntas();
        construirUI();
        FabricaUI.establecerIconoVentana(this);
    }

    private void cargarPreguntas() {
        try {
            preguntas = controlador.obtenerPreguntas(curso.getId());
        } catch (SQLException ex) {
            preguntas = new ArrayList<>();
            errorCarga = ex.getMessage();
        }
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        panelPreguntas = new JPanel(new BorderLayout());
        raiz.add(panelPreguntas, BorderLayout.CENTER);
        reconstruirPreguntas();
    }

    private JPanel construirEncabezado() {
        // Mismo azul que el encabezado de VentanaContenidoCurso (de donde se entra al test) —
        // el texto/la barra de progreso de acá abajo están pensados para ese fondo oscuro
        // (blanco/celeste claro, track semitransparente blanco); con el fondo clarito del
        // módulo admin (240,245,250) quedaban casi invisibles.
        // setOpaque(false) + relleno a mano en paintComponent (en vez de setOpaque(true) con un
        // Color de alpha<255 como fondo): marcar un panel "opaco" con un color translúcido hace
        // que el RepaintManager lo trate como si se autocubriera por completo y se salte repintar
        // lo que hay debajo cuando solo cambian sus hijos (acá, textoPreguntaLbl al terminar el
        // test) — el resultado es el texto "fantasma" superpuesto detrás del encabezado.
        JPanel encabezado = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(36, 91, 168, 221));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(24, 32, 12, 32));
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JPanel fila1 = new JPanel(new BorderLayout());
        fila1.setOpaque(false);

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        // Ícono real del curso (mismo componente que VentanaContenidoCurso) en vez de un emoji
        // suelto en el texto: "📝" no está garantizado en todas las fuentes del sistema y caía a
        // un glifo vacío — mismo criterio que DialogoPersonalizado con sus íconos vectoriales.
        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(LEFT_ALIGNMENT);
        filaTitulo.add(IconoCurso.crearEtiqueta(curso, 36));
        JLabel tituloLbl = new JLabel("Test: " + curso.getTitulo());
        tituloLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        tituloLbl.setForeground(Color.WHITE);
        filaTitulo.add(tituloLbl);

        textoPreguntaLbl = new JLabel("Pregunta 1 de " + preguntas.size());
        textoPreguntaLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        textoPreguntaLbl.setForeground(new Color(200, 220, 255));

        bloqueTitulo.add(filaTitulo);
        bloqueTitulo.add(Box.createVerticalStrut(4));
        bloqueTitulo.add(textoPreguntaLbl);

        botonVolverEncabezado = FabricaUI.crearBotonSecundarioPequeno("Cancelar", IconoVectorial.Tipo.VOLVER);
        botonVolverEncabezado.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            alVolver.run();
        });

        panelBotonesEncabezado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotonesEncabezado.setOpaque(false);
        panelBotonesEncabezado.add(botonVolverEncabezado);

        fila1.add(bloqueTitulo, BorderLayout.WEST);
        fila1.add(panelBotonesEncabezado, BorderLayout.EAST);

        encabezado.add(fila1);
        encabezado.add(Box.createVerticalStrut(12));

        barraProgreso = new BarraProgreso();
        barraProgreso.setProgreso(1, preguntas.size());
        encabezado.add(barraProgreso);

        return encabezado;
    }

    private Component construirPreguntas() {
        JPanel contenedorCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contenedorCentral.setBackground(new Color(245, 248, 252));

        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 32, 20, 32));

        if (errorCarga != null) {
            JLabel errLbl = new JLabel("No se pudieron cargar las preguntas: " + errorCarga);
            errLbl.setForeground(EstiloUI.ERROR);
            errLbl.setAlignmentX(LEFT_ALIGNMENT);
            contenido.add(errLbl);
        } else if (!preguntas.isEmpty()) {
            contenido.add(construirTarjetaPregunta(preguntaIndexActual + 1, preguntas.get(preguntaIndexActual)));
            contenido.add(Box.createVerticalStrut(24));
            contenido.add(construirFilaBotonesNavegacion());
        }

        contenido.add(Box.createVerticalGlue());

        contenedorCentral.add(contenido);

        JScrollPane scroll = new JScrollPane(contenedorCentral);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirFilaBotonesNavegacion() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);

        int total = preguntas.size();
        boolean esUltima = preguntaIndexActual == total - 1;
        boolean esPrimera = preguntaIndexActual == 0;

        JButton botonAnterior = FabricaUI.crearBotonSecundario("Anterior", IconoVectorial.Tipo.ANTERIOR);
        botonAnterior.setEnabled(!esPrimera);
        botonAnterior.addActionListener(e -> {
            preguntaIndexActual = Math.max(0, preguntaIndexActual - 1);
            reconstruirPreguntas();
        });
        fila.add(botonAnterior);

        JButton botonSiguiente = FabricaUI.crearBotonPrimarioIconoAlFinal(esUltima ? "Finalizar Test" : "Siguiente", IconoVectorial.Tipo.SIGUIENTE);
        botonSiguiente.addActionListener(e -> {
            if (esUltima) {
                manejarFinalizar();
            } else {
                preguntaIndexActual = Math.min(total - 1, preguntaIndexActual + 1);
                reconstruirPreguntas();
            }
        });
        fila.add(botonSiguiente);

        return fila;
    }

    private void reconstruirPreguntas() {
        textoPreguntaLbl.setText("Pregunta " + (preguntaIndexActual + 1) + " de " + preguntas.size());
        barraProgreso.setProgreso(preguntaIndexActual + 1, preguntas.size());
        panelPreguntas.removeAll();
        panelPreguntas.add(construirPreguntas(), BorderLayout.CENTER);
        panelPreguntas.revalidate();
        panelPreguntas.repaint();
    }

    private JPanel construirTarjetaPregunta(int numero, PreguntaTest pregunta) {
        JPanel tarjeta = new JPanel() {
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
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(16, 20, 16, 20));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel enunciadoLbl = new JLabel("<html><body style='width: 650px'>" + numero + ". " + pregunta.getEnunciado() + "</body></html>");
        enunciadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        enunciadoLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        enunciadoLbl.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(enunciadoLbl);
        tarjeta.add(Box.createVerticalStrut(10));

        Integer opcionGuardada = respuestasSeleccionadas.get(pregunta.getId());

        for (OpcionTest opcion : pregunta.getOpciones()) {
            JRadioButton radio = new JRadioButton(opcion.getTexto());
            radio.setActionCommand(String.valueOf(opcion.getId()));
            radio.setFont(EstiloUI.FUENTE_CUERPO);
            radio.setForeground(EstiloUI.TEXTO_PRIMARIO);
            radio.setOpaque(false);
            radio.setAlignmentX(LEFT_ALIGNMENT);
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (opcionGuardada != null && opcionGuardada == opcion.getId()) {
                radio.setSelected(true);
            }
            radio.addActionListener(e -> respuestasSeleccionadas.put(pregunta.getId(), opcion.getId()));
            tarjeta.add(radio);
        }

        return tarjeta;
    }

    private void manejarFinalizar() {
        if (respuestasSeleccionadas.size() < preguntas.size()) {
            DialogoPersonalizado.mostrarError(this, "Respondé todas las preguntas antes de finalizar.");
            return;
        }

        try {
            ResultadoTestGuardado resultado = controlador.corregirYGuardar(emailUsuario, curso.getId(), preguntas, respuestasSeleccionadas);
            int puntaje = resultado.getPuntaje();
            boolean aprobado = puntaje >= ControladorTest.puntajeAprobacion();
            mostrarResultados(puntaje, aprobado);
            if (resultado.isCertificadoNuevo()) {
                enviarCertificadoPorEmailEnSegundoPlano(puntaje);
            }
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo guardar el resultado:\n" + ex.getMessage());
        }
    }

    /** Solo se llama la primera vez que se aprueba este curso (ver {@code certificadoNuevo}).
     * Best-effort: si el envío falla, el certificado sigue disponible en la app de todos modos
     * (botón "Ver Certificado"), así que no interrumpe al usuario con un diálogo de error — igual
     * que {@code VentanaContenidoCurso.guardarProgreso()} con otras llamadas no críticas. */
    private void enviarCertificadoPorEmailEnSegundoPlano(int puntaje) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                byte[] png = new PanelCertificado(curso, nombreUsuario, puntaje).renderizarComoPng();
                controlador.enviarCertificadoPorEmail(emailUsuario, nombreUsuario, curso.getTitulo(), puntaje, png);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    System.err.println("No se pudo enviar el certificado por email: "
                        + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
                }
            }
        };
        worker.execute();
    }

    /** Reemplaza el cuerpo de la ventana por el resumen del puntaje y la revisión pregunta por pregunta. */
    private void mostrarResultados(int puntaje, boolean aprobado) {
        textoPreguntaLbl.setText(aprobado
            ? "¡Aprobaste con " + puntaje + " / 100!"
            : "No aprobaste  ·  " + puntaje + " / 100");
        barraProgreso.setProgreso(preguntas.size(), preguntas.size());
        actualizarBotonesEncabezado(aprobado, puntaje);

        panelPreguntas.removeAll();
        panelPreguntas.add(construirResultados(puntaje, aprobado), BorderLayout.CENTER);
        panelPreguntas.revalidate();
        panelPreguntas.repaint();
    }

    /** Al terminar el test, el "Cancelar" del encabezado deja de tener sentido: si aprobó, se
     * reemplaza por "Ver Certificado" + "Ir a Mis Cursos"; si no, sigue habiendo un solo botón
     * para volver (no hay certificado que mostrar todavía). */
    private void actualizarBotonesEncabezado(boolean aprobado, int puntaje) {
        panelBotonesEncabezado.removeAll();
        if (aprobado) {
            JButton botonCertificado = FabricaUI.crearBotonSecundarioPequeno("Ver Certificado");
            botonCertificado.addActionListener(e ->
                new VentanaCertificado(curso, nombreUsuario, puntaje).setVisible(true));

            // A diferencia de alVolver (que vuelve a VentanaContenidoCurso, de donde se entró al
            // test), este botón va directo a la pestaña "Mis Cursos" — mismo patrón de navegación
            // que VentanaContenidoCurso.abrirTest()/VentanaMisCursos al entrar a un curso.
            JButton botonMisCursos = FabricaUI.crearBotonSecundarioPequeno("Ir a Mis Cursos", IconoVectorial.Tipo.VOLVER);
            botonMisCursos.addActionListener(e -> {
                if (!iniciarTransicionUnica()) return;
                dispose();
                new VentanaMisCursos(emailUsuario).setVisible(true);
            });

            panelBotonesEncabezado.add(botonCertificado);
            panelBotonesEncabezado.add(botonMisCursos);
        } else {
            botonVolverEncabezado.setText("Volver");
            panelBotonesEncabezado.add(botonVolverEncabezado);
        }
        panelBotonesEncabezado.revalidate();
        panelBotonesEncabezado.repaint();
    }

    private Component construirResultados(int puntaje, boolean aprobado) {
        JPanel contenedorCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contenedorCentral.setBackground(new Color(245, 248, 252));

        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 32, 20, 32));

        contenido.add(construirBannerResultado(puntaje, aprobado));
        contenido.add(Box.createVerticalStrut(20));

        for (int i = 0; i < preguntas.size(); i++) {
            contenido.add(construirTarjetaRevision(i + 1, preguntas.get(i)));
            contenido.add(Box.createVerticalStrut(12));
        }

        JButton botonVolver = FabricaUI.crearBotonPrimario("Volver al curso", IconoVectorial.Tipo.VOLVER);
        botonVolver.setAlignmentX(LEFT_ALIGNMENT);
        botonVolver.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            alVolver.run();
        });
        contenido.add(botonVolver);
        contenido.add(Box.createVerticalGlue());

        contenedorCentral.add(contenido);

        JScrollPane scroll = new JScrollPane(contenedorCentral);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirBannerResultado(int puntaje, boolean aprobado) {
        JPanel banner = new JPanel(new BorderLayout()) {
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
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(18, 20, 18, 20));
        banner.setAlignmentX(LEFT_ALIGNMENT);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel(aprobado ? "✓ ¡Aprobaste el curso!" : "No alcanzaste el puntaje mínimo");
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloLbl.setForeground(aprobado ? EstiloUI.EXITO : EstiloUI.ERROR);

        JLabel detalleLbl = new JLabel("Obtuviste " + puntaje + " / 100  ·  aprueba con "
            + ControladorTest.puntajeAprobacion() + " / 100. Revisá tus respuestas debajo.");
        detalleLbl.setFont(EstiloUI.FUENTE_CUERPO);
        detalleLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        bloque.add(tituloLbl);
        bloque.add(Box.createVerticalStrut(4));
        bloque.add(detalleLbl);

        banner.add(bloque, BorderLayout.CENTER);
        return banner;
    }

    /** Tarjeta de revisión: enunciado, la respuesta elegida y, si estuvo mal, cuál era la correcta. */
    private JPanel construirTarjetaRevision(int numero, PreguntaTest pregunta) {
        Integer opcionElegidaId = respuestasSeleccionadas.get(pregunta.getId());
        OpcionTest opcionElegida = null;
        OpcionTest opcionCorrecta = null;
        for (OpcionTest opcion : pregunta.getOpciones()) {
            if (opcionElegidaId != null && opcion.getId() == opcionElegidaId) opcionElegida = opcion;
            if (opcion.isCorrecta()) opcionCorrecta = opcion;
        }
        boolean esCorrecta = opcionElegida != null && opcionElegida.isCorrecta();

        JPanel tarjeta = new JPanel() {
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
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(16, 20, 16, 20));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel enunciadoLbl = new JLabel("<html><body style='width: 650px'>" + numero + ". " + pregunta.getEnunciado() + "</body></html>");
        enunciadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        enunciadoLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        enunciadoLbl.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(enunciadoLbl);
        tarjeta.add(Box.createVerticalStrut(8));

        JLabel tuRespuestaLbl = new JLabel((esCorrecta ? "✓  " : "✕  ") + "Tu respuesta: "
            + (opcionElegida != null ? opcionElegida.getTexto() : "(sin responder)"));
        tuRespuestaLbl.setFont(EstiloUI.FUENTE_CUERPO);
        tuRespuestaLbl.setForeground(esCorrecta ? EstiloUI.EXITO : EstiloUI.ERROR);
        tuRespuestaLbl.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(tuRespuestaLbl);

        if (!esCorrecta) {
            tarjeta.add(Box.createVerticalStrut(4));
            JLabel correctaLbl = new JLabel("Respuesta correcta: " + (opcionCorrecta != null ? opcionCorrecta.getTexto() : "—"));
            correctaLbl.setFont(EstiloUI.FUENTE_CUERPO);
            correctaLbl.setForeground(EstiloUI.EXITO);
            correctaLbl.setAlignmentX(LEFT_ALIGNMENT);
            tarjeta.add(correctaLbl);
        }

        return tarjeta;
    }
}
