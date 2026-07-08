import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/** Ventana principal que muestra el catálogo de cursos disponibles. */
public class CoursesFrame extends JFrame {

    private final String userEmail;

    public static final List<Course> COURSES = Arrays.asList(
        new Course("☕", "Java desde Cero",
            "Aprendé programación orientada a objetos con el lenguaje más usado en la industria.",
            "8 semanas",
            "Variables y tipos de datos", "Control de flujo", "POO: clases y objetos",
            "Colecciones y generics", "Excepciones y archivos"),

        new Course("🐍", "Python para Principiantes",
            "El lenguaje más amigable para comenzar. Ideal para automatización, datos y web.",
            "6 semanas",
            "Sintaxis y estructuras básicas", "Funciones y módulos",
            "Listas, dicts y sets", "Archivos y excepciones", "Introducción a pip"),

        new Course("🌐", "Desarrollo Web Full Stack",
            "Construí sitios modernos con HTML, CSS, JavaScript y una intro a frameworks.",
            "10 semanas",
            "HTML5 semántico", "CSS3 y Flexbox/Grid",
            "JavaScript ES6+", "DOM y eventos", "Intro a React"),

        new Course("🗄️", "SQL y Bases de Datos",
            "Diseñá y consultá bases de datos relacionales. Fundamento de toda aplicación.",
            "5 semanas",
            "Modelo relacional y DDL", "SELECT, WHERE, JOIN",
            "Subconsultas y funciones", "Índices y optimización", "MySQL en la práctica"),

        new Course("🔧", "Git y GitHub",
            "Control de versiones profesional. Trabajá en equipo sin perder ningún cambio.",
            "3 semanas",
            "Repositorios y commits", "Branches y merges",
            "Resolución de conflictos", "Pull requests y code review", "GitHub Actions básico"),

        new Course("📊", "Algoritmos y Estructuras de Datos",
            "El corazón de la programación eficiente. Preparate para entrevistas técnicas.",
            "7 semanas",
            "Complejidad algorítmica", "Arrays, listas y pilas",
            "Árboles y grafos", "Búsqueda y ordenamiento", "Algoritmos greedy y DP")
    );

    public CoursesFrame(String userEmail) {
        this.userEmail = userEmail;
        setTitle("Educ G – Cursos disponibles");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        buildUI(userEmail);
    }

    private void buildUI(String userEmail) {
        // ── Fondo ──────────────────────────────────────────────────────────────
        JPanel root = UIFactory.createDefaultBackground();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        // ── Header ─────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 32, 12, 32));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel appLbl = new JLabel("Educ G");
        appLbl.setFont(UIFactory.FONT_TITLE);
        appLbl.setForeground(Color.WHITE);

        JLabel welcomeLbl = new JLabel("Bienvenido/a, " + userEmail);
        welcomeLbl.setFont(UIFactory.FONT_SUBTITLE);
        welcomeLbl.setForeground(new Color(180, 210, 255));

        titleBlock.add(appLbl);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(welcomeLbl);

        JButton panelBtn = UIFactory.createSecondaryButton("Mi Panel");
        panelBtn.setPreferredSize(new Dimension(120, 38));
        panelBtn.addActionListener(e -> {
            dispose();
            new UserPanelFrame(userEmail).setVisible(true);
        });

        JButton logoutBtn = UIFactory.createSecondaryButton("Cerrar Sesión");
        logoutBtn.setPreferredSize(new Dimension(140, 38));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel headerBtns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        headerBtns.setOpaque(false);
        headerBtns.add(panelBtn);
        headerBtns.add(logoutBtn);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(headerBtns,  BorderLayout.EAST);

        // ── Subtítulo ──────────────────────────────────────────────────────────
        JLabel subtitle = new JLabel("Explorá nuestros cursos de programación");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(200, 220, 255));
        subtitle.setBorder(new EmptyBorder(0, 32, 16, 32));
        subtitle.setOpaque(false);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(header,   BorderLayout.NORTH);
        topSection.add(subtitle, BorderLayout.SOUTH);

        // ── Grid de tarjetas ───────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(0, 3, 8, 8));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(4, 26, 26, 26));

        for (Course course : COURSES) {
            grid.add(new CourseCard(course, () -> handleEnroll(course)));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(topSection, BorderLayout.NORTH);
        root.add(scroll,     BorderLayout.CENTER);
    }

    private void handleEnroll(Course course) {
        try {
            boolean enrolled = AuthService.enrollCourse(userEmail, course.getTitle());
            if (enrolled) {
                JOptionPane.showMessageDialog(this,
                    "¡Te inscribiste en \"" + course.getTitle() + "\"!\n"
                    + "Podés acceder al curso desde \"Mi Panel\".",
                    "Inscripción exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Ya estás inscripto en \"" + course.getTitle() + "\".\n"
                    + "Accedé al curso desde \"Mi Panel\".",
                    "Ya inscripto",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo procesar la inscripción:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
