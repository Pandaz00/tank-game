package tankwars.menus;

import javax.swing.*;
import java.awt.*;

public class HelpDialog extends JDialog {
    public HelpDialog(Frame parent) {
        super(parent, "Help", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("SansSerif", Font.BOLD, 14));
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);

        // Adding content to the text area
        textArea.append("Game Manual: Tank Game\n\n");
        textArea.append("Overview\n");
        textArea.append("Tank Game is a dynamic, action-packed two-player game. " +
                "Each player controls a tank, battling to outmaneuver and outgun their opponent. " +
                "The game features destructible and indestructible obstacles, " +
                "various power-ups, and different terrains that affect tank movement and strategy.\n\n");

        textArea.append("Controls\n");
        textArea.append(" * Left Player (Red Tank):\n");
        textArea.append("   - W, A, S, D: Move forward, left, backward, and right, respectively.\n");
        textArea.append("   - Space Bar: Fire bullets.\n");
        textArea.append(" * Right Player (Blue Tank):\n");
        textArea.append("   - Arrow Keys (Up, Down, Left, Right): Move the tank in the respective directions.\n");
        textArea.append("   - Enter Key: Fire bullets.\n");
        textArea.append("   - Diagonal Movement: Press two directional keys simultaneously to move diagonally.\n\n");

        textArea.append("Game Environment\n");
        textArea.append(" * Red Walls (Brick Walls):\n");
        textArea.append("   - These can block tank movements and can be destroyed by firing bullets at them.\n");
        textArea.append(" * White Walls (Bulletproof Walls):\n");
        textArea.append("   - Impenetrable by both tanks and bullets, effectively blocking passage.\n");
        textArea.append(" * Water Terrains:\n");
        textArea.append("   - Tanks slow down when entering water and return to normal speed once they exit.\n");
        textArea.append(" * Jungles:\n");
        textArea.append("   - Tanks become invisible to the opponent when inside the jungle, " +
                "becoming visible again once they leave.\n");
        textArea.append(" * Power-Ups:\n");
        textArea.append("   - Heart-Shaped Supply: Restores health.\n");
        textArea.append("   - Shield Supply: Grants invulnerability to damage for 8 seconds.\n");
        textArea.append("   - Lightning Bolt Supply: Temporarily boosts speed for 5 seconds.\n\n");
        textArea.append("Thank you for playing Tank Game. Enjoy! : )\n\n");

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.setPreferredSize(new Dimension(400, 600));

        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
