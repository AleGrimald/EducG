package vista;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class iCuestionario extends javax.swing.JFrame {
    
    private int respuestasCorrectas = 0;
    private JPanel panelPrincipal;
    private JTextArea areaPregunta;
    private JRadioButton opcionA, opcionB, opcionC;
    private ButtonGroup grupoOpciones;
    private JButton btnSiguiente;
    private JButton btnVolver;

    private Map<String, Map<String, List<String[]>>> bancoPreguntas;
    private Iterator<String[]> iteradorPreguntas;
    private String[] preguntaActual;

    private void cargarPreguntas() {
        bancoPreguntas = new HashMap<>();

        // 🟦 C Sharp
        Map<String, List<String[]>> csharp = new HashMap<>();
        List<String[]> preguntasPOO = new ArrayList<>();
        preguntasPOO.add(new String[]{"¿Qué es POO?", "Programación orientada a objetos", "Programación estructurada", "Programación funcional", "0"});
        preguntasPOO.add(new String[]{"¿Qué es una clase?", "Una plantilla para objetos", "Una función", "Un tipo de dato", "0"});
        preguntasPOO.add(new String[]{"¿Qué es un objeto?", "Instancia de una clase", "Una variable global", "Un método estático", "0"});
        preguntasPOO.add(new String[]{"¿Qué es herencia?", "Reutilización de código entre clases", "Un tipo de bucle", "Una estructura condicional", "0"});
        preguntasPOO.add(new String[]{"¿Qué es encapsulamiento?", "Ocultar detalles internos", "Mostrar todos los atributos", "Evitar el uso de métodos", "0"});
        preguntasPOO.add(new String[]{"¿Qué es polimorfismo?", "Capacidad de una clase de comportarse de distintas formas", "Uso de múltiples clases", "Herencia múltiple", "0"});
        preguntasPOO.add(new String[]{"¿Qué palabra clave define una clase?", "class", "object", "new", "0"});
        preguntasPOO.add(new String[]{"¿Qué hace el modificador 'private'?", "Restringe acceso fuera de la clase", "Permite acceso desde cualquier clase", "Hace pública la variable", "0"});
        preguntasPOO.add(new String[]{"¿Qué es un constructor?", "Método especial para inicializar objetos", "Variable global", "Clase abstracta", "0"});
        preguntasPOO.add(new String[]{"¿Qué significa 'override'?", "Sobrescribir un método heredado", "Crear una nueva clase", "Eliminar una propiedad", "0"});
        csharp.put("POO", preguntasPOO);

        List<String[]> preguntasWinForms = new ArrayList<>();
        preguntasWinForms.add(new String[]{"¿Cómo controlar un Button en WinForms?", "Eventos", "Estilos", "Base de datos", "0"});
        preguntasWinForms.add(new String[]{"¿Qué es un Form?", "Una ventana", "Una clase de estilo", "Una base de datos", "0"});
        csharp.put("WinForms", preguntasWinForms);

        bancoPreguntas.put("C Sharp", csharp);

        // 🟨 Java
        Map<String, List<String[]>> java = new HashMap<>();
        List<String[]> preguntasJavaPOO = new ArrayList<>();
        preguntasJavaPOO.add(new String[]{"¿Qué es POO en Java?", "Paradigma orientado a objetos", "Un IDE", "Un framework", "0"});
        preguntasJavaPOO.add(new String[]{"¿Qué es una interfaz?", "Un contrato de métodos", "Una clase abstracta", "Una librería", "0"});
        java.put("POO", preguntasJavaPOO);

        List<String[]> preguntasJFrame = new ArrayList<>();
        preguntasJFrame.add(new String[]{"¿Qué es JFrame?", "Una ventana en Swing", "Un tipo de archivo", "Una clase de base de datos", "0"});
        preguntasJFrame.add(new String[]{"¿Qué hace setVisible(true)?", "Muestra la ventana", "Cierra la aplicación", "Minimiza el frame", "0"});
        java.put("JFrame", preguntasJFrame);

        bancoPreguntas.put("Java", java);

        // 🟥 SQL
        Map<String, List<String[]>> sql = new HashMap<>();
        List<String[]> preguntasConsultas = new ArrayList<>();
        preguntasConsultas.add(new String[]{"¿Qué hace SELECT?", "Consulta datos", "Inserta datos", "Elimina datos", "0"});
        preguntasConsultas.add(new String[]{"¿Qué hace WHERE?", "Filtra resultados", "Ordena resultados", "Agrupa resultados", "0"});
        sql.put("Consultas", preguntasConsultas);

        List<String[]> preguntasProcedimientos = new ArrayList<>();
        preguntasProcedimientos.add(new String[]{"¿Qué es un procedimiento almacenado?", "Bloque de código SQL reutilizable", "Una tabla temporal", "Una vista", "0"});
        preguntasProcedimientos.add(new String[]{"¿Qué comando lo crea?", "CREATE PROCEDURE", "CREATE FUNCTION", "CREATE TABLE", "0"});
        sql.put("Storage Procedures", preguntasProcedimientos);

        bancoPreguntas.put("SQL", sql);

        // 🟩 Python
        Map<String, List<String[]>> python = new HashMap<>();
        List<String[]> preguntasIA = new ArrayList<>();
        preguntasIA.add(new String[]{"¿Qué es IA?", "Inteligencia Artificial", "Interfaz de Aplicación", "Integración Automática", "0"});
        preguntasIA.add(new String[]{"¿Qué librería se usa para IA?", "TensorFlow", "NumPy", "Matplotlib", "0"});
        python.put("IA", preguntasIA);

        List<String[]> preguntasDataScience = new ArrayList<>();
        preguntasDataScience.add(new String[]{"¿Qué es Pandas?", "Librería para análisis de datos", "Librería gráfica", "Framework web", "0"});
        preguntasDataScience.add(new String[]{"¿Qué hace df.head()?", "Muestra las primeras filas", "Ordena el DataFrame", "Elimina columnas", "0"});
        python.put("Data Science", preguntasDataScience);

        bancoPreguntas.put("Python", python);
    }
    
    public iCuestionario(String seccion, String subtema) {
        initComponents();
        setTitle("Cuestionario Educativo");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        cargarPreguntas();
        configurarInterfaz(seccion, subtema);
    }

    private void configurarInterfaz(String seccion, String subtema) {
        Color fondoClaro = new Color(255, 255, 255);
        Color azulInstitucional = new Color(76,175,80);
        Color verdeBoton = new Color(75, 175, 80);
        Color textoBlanco = Color.WHITE;

        Font fuenteGeneral = new Font("SansSerif", Font.PLAIN, 14);
        Font fuenteBoton = new Font("SansSerif", Font.BOLD, 14);

        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(fondoClaro);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(azulInstitucional);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnVolver = new JButton("← Volver");
        btnVolver.setFont(fuenteGeneral);
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.addActionListener(e -> {
            new iTemas().setVisible(true);
            this.dispose();
        });

        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelIzquierdo.setBackground(azulInstitucional);
        panelIzquierdo.add(btnVolver);

        JLabel lblSeccion = new JLabel("Sección: " + seccion);
        JLabel lblSubtema = new JLabel("Subtema: " + subtema);
        lblSeccion.setForeground(textoBlanco);
        lblSubtema.setForeground(textoBlanco);
        lblSeccion.setFont(fuenteGeneral);
        lblSubtema.setFont(fuenteGeneral);

        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panelDerecho.setBackground(azulInstitucional);
        panelDerecho.add(lblSeccion);
        panelDerecho.add(lblSubtema);

        panelSuperior.add(panelIzquierdo, BorderLayout.WEST);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

        areaPregunta = new JTextArea(3, 50);
        areaPregunta.setLineWrap(true);
        areaPregunta.setWrapStyleWord(true);
        areaPregunta.setEditable(false);
        areaPregunta.setFont(fuenteGeneral);
        areaPregunta.setBackground(Color.WHITE);
        areaPregunta.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        opcionA = new JRadioButton();
        opcionB = new JRadioButton();
        opcionC = new JRadioButton();
        grupoOpciones = new ButtonGroup();
        grupoOpciones.add(opcionA);
        grupoOpciones.add(opcionB);
        grupoOpciones.add(opcionC);

        opcionA.setFont(fuenteGeneral);
        opcionB.setFont(fuenteGeneral);
        opcionC.setFont(fuenteGeneral);
        opcionA.setBackground(fondoClaro);
        opcionB.setBackground(fondoClaro);
        opcionC.setBackground(fondoClaro);

        btnSiguiente = new JButton("Siguiente");
        btnSiguiente.setBackground(verdeBoton);
        btnSiguiente.setForeground(textoBlanco);
        btnSiguiente.setFont(fuenteBoton);
        btnSiguiente.setFocusPainted(false);
        btnSiguiente.addActionListener(e -> mostrarSiguientePregunta());

        JPanel panelCentro = new JPanel(new GridLayout(5, 1, 0, 10));
        panelCentro.setBackground(fondoClaro);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panelCentro.add(areaPregunta);
        panelCentro.add(opcionA);
        panelCentro.add(opcionB);
        panelCentro.add(opcionC);
        panelCentro.add(btnSiguiente);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);
        setContentPane(panelPrincipal);

        cargarPreguntasSeleccionadas(seccion, subtema);
    }
    private void cargarPreguntasSeleccionadas(String seccion, String subtema) {
        if (seccion != null && subtema != null) {
            List<String[]> preguntas = bancoPreguntas.get(seccion).get(subtema);
            if (preguntas != null) {
                iteradorPreguntas = preguntas.iterator();
                mostrarSiguientePregunta();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron preguntas para el subtema seleccionado.");
            }
        }
    }
    private void verificarRespuesta() {
        if (preguntaActual == null) return;

        int respuestaCorrecta = Integer.parseInt(preguntaActual[4]);
        boolean esCorrecta = false;

        switch (respuestaCorrecta) {
            case 0: esCorrecta = opcionA.isSelected(); break;
            case 1: esCorrecta = opcionB.isSelected(); break;
            case 2: esCorrecta = opcionC.isSelected(); break;
        }

        if (esCorrecta) {
            respuestasCorrectas++;
        }
    }
    private void mostrarSiguientePregunta() {
        verificarRespuesta(); // Verifica la respuesta anterior

        if (iteradorPreguntas != null && iteradorPreguntas.hasNext()) {
            preguntaActual = iteradorPreguntas.next();
            areaPregunta.setText(preguntaActual[0]);
            opcionA.setText("A) " + preguntaActual[1]);
            opcionB.setText("B) " + preguntaActual[2]);
            opcionC.setText("C) " + preguntaActual[3]);
            grupoOpciones.clearSelection();
        } else {
            JOptionPane.showMessageDialog(this, "Cuestionario finalizado.\nRespuestas correctas: " + respuestasCorrectas);
            btnSiguiente.setEnabled(false);
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
