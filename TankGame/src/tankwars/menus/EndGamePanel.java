package tankwars.menus;

import tankwars.Launcher;
import tankwars.ResourceManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EndGamePanel extends JPanel {

    private final BufferedImage menuBackground;
    private final Launcher lf;
    private String winnerMessage = "";

    public EndGamePanel(Launcher lf) {
        this.lf = lf;
        this.menuBackground = ResourceManager.getSprite("menu");
        this.setBackground(Color.BLACK);
        this.setLayout(null);
        //        try {
//            menuBackground = ImageIO.read(this.getClass().getClassLoader().getResource("menu/title.png"));
//        } catch (IOException e) {
//            System.out.println("Error cant read menu background");
//            e.printStackTrace();
//            System.exit(-3);
//        }

        JButton start = new JButton("Restart Game");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(150, 300, 250, 50);
        start.addActionListener((actionEvent -> this.lf.setFrame("game")));


        JButton exit = new JButton("Exit");
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(150, 400, 250, 50);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(start);
        this.add(exit);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);

        if (!winnerMessage.isEmpty()) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(winnerMessage)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + 50;
            g2.drawString(winnerMessage, x, y);
        }
    }

    public void setWinnerMessage(String winnerMessage) {
        this.winnerMessage = winnerMessage;
        repaint();
    }
}
