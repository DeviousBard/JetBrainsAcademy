package shooter;

import javax.swing.*;
import java.awt.*;

public class SharpShooter extends JFrame {
    final JLabel statusbar = new JLabel();
    final Target target = new Target(this);

    boolean gameStarted = false;

    public SharpShooter() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 820);
        setVisible(true);
        setLayout(new BorderLayout());
        setTitle("Sharp shooter");

        statusbar.setName("Statusbar");
        statusbar.setPreferredSize(new Dimension(800, 20));
        statusbar.setBackground(Color.WHITE);
        statusbar.setForeground(Color.BLACK);
        statusbar.setText("Press the SPACE bar to start the game.");

        getContentPane().add(target, BorderLayout.CENTER);
        getContentPane().add(statusbar, BorderLayout.SOUTH);
    }
}
