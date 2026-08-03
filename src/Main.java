import vista.VentanaLogin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Intentar Nimbus para una apariencia más moderna

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}
