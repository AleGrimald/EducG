package vista.componentes;

import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

/**
 * Instala una columna de botones de acción (Modificar/Baja Lógica/Eliminar, etc.) en un
 * {@link JTable}, reutilizable entre las tablas de Alumnos y Cursos del panel de administrador.
 * Cada botón se resuelve con el patrón renderer+editor estándar de Swing: el renderer solo
 * dibuja los botones, el editor los hace clickeables (se activa al primer click en la celda).
 * Los botones son solo-ícono ({@link BotonAccionIcono}), con la etiqueta como tooltip.
 */
public final class ColumnaAcciones {

    private ColumnaAcciones() {}

    public static void instalar(JTable tabla, int indiceColumna, List<AccionBoton> acciones) {
        TableColumn columna = tabla.getColumnModel().getColumn(indiceColumna);
        columna.setCellRenderer(new RenderizadorAcciones(acciones));
        columna.setCellEditor(new EditorAcciones(acciones));
        int ancho = acciones.size() * 44;
        columna.setMinWidth(ancho);
        columna.setMaxWidth(ancho);
        columna.setPreferredWidth(ancho);
    }

    /** Una acción disponible por fila: etiqueta/ícono/color pueden variar según el estado de esa fila (ej. Baja Lógica/Reactivar). */
    public interface AccionBoton {
        String etiqueta(int filaModelo);
        void ejecutar(int filaModelo);

        /** Ícono a mostrar (el botón es solo-ícono; la etiqueta queda como tooltip). */
        IconoVectorial.Tipo icono(int filaModelo);

        /** Color del ícono/hover. Por defecto, azul primario. */
        default Color color(int filaModelo) { return EstiloUI.AZUL_CLARO; }
    }

    private static JPanel construirPanelBotones(JTable tabla, int filaVista, List<AccionBoton> acciones, Runnable alTerminarEdicion) {
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        panel.setOpaque(true);
        for (AccionBoton accion : acciones) {
            JButton boton = FabricaUI.crearBotonAccionIcono(accion.icono(filaModelo), accion.color(filaModelo), accion.etiqueta(filaModelo));
            boton.addActionListener(e -> {
                if (alTerminarEdicion != null) alTerminarEdicion.run();
                accion.ejecutar(filaModelo);
            });
            panel.add(boton);
        }
        return panel;
    }

    private static class RenderizadorAcciones implements TableCellRenderer {
        private final List<AccionBoton> acciones;

        RenderizadorAcciones(List<AccionBoton> acciones) { this.acciones = acciones; }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                         boolean foco, int fila, int columna) {
            JPanel panel = construirPanelBotones(tabla, fila, acciones, null);
            panel.setBackground(seleccionada ? tabla.getSelectionBackground() : tabla.getBackground());
            return panel;
        }
    }

    private static class EditorAcciones extends AbstractCellEditor implements TableCellEditor {
        private final List<AccionBoton> acciones;

        EditorAcciones(List<AccionBoton> acciones) { this.acciones = acciones; }

        @Override
        public Component getTableCellEditorComponent(JTable tabla, Object valor, boolean seleccionada, int fila, int columna) {
            return construirPanelBotones(tabla, fila, acciones, this::stopCellEditing);
        }

        @Override
        public Object getCellEditorValue() { return null; }
    }
}
