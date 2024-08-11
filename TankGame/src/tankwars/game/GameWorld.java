package tankwars.game;


import com.sun.source.tree.Tree;
import tankwars.GameConstants;
import tankwars.Launcher;
import tankwars.ResourceManager;
import tankwars.menus.EndGamePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {
    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    private boolean isGameOver = false;
    private String winner = "";
    ArrayList<GameObject> gameObjects = new ArrayList<>(1000);
    List<Animation> anims = new ArrayList<>();

    private long tick = 0;

    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        this.resetGame();
        Sound bg = ResourceManager.getSound("bg");
//        bg.loopContinuously();
//        bg.play();
        this.anims.add(new Animation(100, 100, ResourceManager.getAnim("bulletshoot")));
        this.anims.add(new Animation(100, 100, ResourceManager.getAnim("bullethit")));
        try {
            while (true) {
                this.tick++;
                for (int i = this.gameObjects.size() - 1; i >= 0; i-- ) {
                    if (this.gameObjects.get(i) instanceof Updatable u) {
                        u.update(this);
                    } else {
                        break;
                    }
                }
                for (int i = 0; i < anims.size(); i++) {
                    this.anims.get(i).update();
                }

                this.checkCollisions();
                this.gameObjects.removeIf(GameObject::getHasCollided);
                this.renderFrame();
                this.repaint();   // Redraw game


                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our 
                 * loop run at a fixed rate per/sec. 
                */

                Thread.sleep(20); // There was 7 before but mini map was sparking
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    // Check water area
    public boolean isWaterArea(float x, float y) {
        for (GameObject obj : gameObjects) {
            if (obj instanceof Water) {
                Rectangle waterArea = obj.getHitBox();
                if (waterArea.contains(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check trees area
    public boolean isInTreesArea(float x, float y) {
        for (GameObject obj : gameObjects) {
            if (obj instanceof Trees) {
                Rectangle treesArea = obj.getHitBox();
                if (treesArea.contains(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check collision
    private void checkCollisions() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject obj1 = this.gameObjects.get(i);
            if (!(obj1 instanceof Updatable)) continue;

            for (int j = 0; j < this.gameObjects.size(); j++) {
                if (i == j) continue;
                GameObject obj2 = this.gameObjects.get(j);
                if (!(obj2 instanceof Collidable)) continue;

                if (obj1.getHitBox().intersects(obj2.getHitBox())) {
                    ((Collidable) obj1).handleCollision((Collidable) obj2);
                    ((Collidable) obj2).handleCollision((Collidable) obj1);
                }
            }
        }
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        this.t1.setX(50);
        this.t1.setY(1000);
        this.t2.setX(3500);
        this.t2.setY(1000);
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        InputStreamReader isr = new InputStreamReader(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResourceAsStream("maps/map1.csv")
                )
        );

        try (BufferedReader mapReader = new BufferedReader(isr)) {
            int row = 0;
            while (mapReader.ready()) {
                String line = mapReader.readLine();
                String[] objs = line.split(",");
//                System.out.println(Arrays.toString(objs));
                for (int col = 0; col < objs.length; col++) {
                    String gameItem = objs[col];
                    if ("0".equals(gameItem)) continue;
                    this.gameObjects.add(GameObject.newInstance(gameItem, col * 32, row * 32));
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage t1Image = ResourceManager.getSprite("t1");
        BufferedImage t1Invisible = ResourceManager.getSprite("invisible");
        BufferedImage t2Image = ResourceManager.getSprite("t2");
        BufferedImage t2Invisible = ResourceManager.getSprite("invisible");

        t1 = new Tank(this, 50, 1000, 0, 0, (short) 0, t1Image, t1Invisible);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        this.lf.getJf().addKeyListener(tc1);

        t2 = new Tank(this, 3500, 1000, 0, 0, (short) 180, t2Image, t2Invisible);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        this.lf.getJf().addKeyListener(tc2);
        this.gameObjects.add(t1);
        this.gameObjects.add(t2);
    }

    private void renderFloor(Graphics buffer) {
        BufferedImage floor = ResourceManager.getSprite("floor");
        for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i+=320) {
            for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j+=240) {
                buffer.drawImage(floor, i, j, null);
            }
        }
    }

    private void displaySplitScreen(Graphics2D onScreenPanel) {
        BufferedImage lh = this.world.getSubimage((int)this.t1.getScreenX(), (int)this.t1.getScreenY(), GameConstants.GAME_SCREEN_WIDTH/2, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage rh = this.world.getSubimage((int)this.t2.getScreenX(), (int)this.t2.getScreenY(), GameConstants.GAME_SCREEN_WIDTH/2, GameConstants.GAME_SCREEN_HEIGHT);
        onScreenPanel.drawImage(lh, 0, 0, null);
        onScreenPanel.drawImage(rh, GameConstants.GAME_SCREEN_WIDTH / 2, 0, null);

        // Draw a vertical split line
        onScreenPanel.setColor(Color.BLACK);  // Set color
        int lineX = GameConstants.GAME_SCREEN_WIDTH / 2;  // Location
        onScreenPanel.setStroke(new BasicStroke(2)); // Width of the line
        onScreenPanel.drawLine(lineX, 0, lineX, GameConstants.GAME_SCREEN_HEIGHT);
    }

    static double scaleFactor = .15; //.2
    private void displayMiniMap(Graphics2D onScreenPanel) {
        BufferedImage mm = this.world.getSubimage(0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        double mmx = GameConstants.GAME_SCREEN_WIDTH/2. - (GameConstants.GAME_WORLD_WIDTH * scaleFactor) / 2;
        double mmy = GameConstants.GAME_SCREEN_HEIGHT - (GameConstants.GAME_WORLD_HEIGHT * scaleFactor);
        AffineTransform scaler = AffineTransform.getTranslateInstance(mmx, mmy);
        scaler.scale(scaleFactor, scaleFactor);
        onScreenPanel.drawImage(mm, scaler, null);
    }

    private void renderFrame() {
        Graphics2D buffer = world.createGraphics();
        this.renderFloor(buffer);
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).drawImage(buffer);
        }
        for (int i = 0; i < anims.size(); i++) {
            this.anims.get(i).render(buffer);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // Display the split screen
        this.displaySplitScreen(g2);
        // Display the minimap
        this.displayMiniMap(g2);
    }

    public void addGameObject(GameObject g) {
        this.gameObjects.add(g);
    }

    public void endGame(Tank loser) {
        // Assuming you have a reference to Launcher or a method to access it
        Launcher launcher = this.getLf();
        String winnerMessage;
        if (loser == t1) {
            winnerMessage = "Blue tank (Right side) wins !!! ";
        } else if (loser == t2) {
            winnerMessage = "Red Tank (Left side) wins !!! ";
        } else {
            winnerMessage = "It is a draw ... ";
        }

        // Update end game panel with the winner message
        ((EndGamePanel) launcher.getMainPanel().getComponent(2)).setWinnerMessage(winnerMessage);
        launcher.setFrame("end"); // Switch to end game panel
    }

    public Launcher getLf() {
        return lf;
    }
}


