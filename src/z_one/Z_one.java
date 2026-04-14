package z_one;

import javax.swing.SwingUtilities;
import view.LoginFrame;

public class Z_one {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
