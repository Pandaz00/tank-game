package tankwars.game;

import tankwars.GameConstants;
import tankwars.ResourceManager;
import tankwars.ResourcePools;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class Tank extends GameObject implements Updatable, Collidable {
    GameWorld gw;
    private int tankId;
    private float screenX;
    private float screenY;

    private float prevX;
    private float prevY;
    private float vx;
    private float vy;
    private float angle;
    private int health = 10;
    private int lives = 3;
    private Point2D.Float respawnPoint;

    private float R = 6;
    private float ROTATIONSPEED = 3.0f;

    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean ShootPressed;
    private boolean isVisible = true;
    private boolean isShielded = false;
    List<Animation> anims = new ArrayList<>();
    private Timer speedTimer;
    private Timer shieldTimer;

    private long coolDown = 500;
    private long timeSinceLastShot = 0;
    private final BufferedImage originalImage;
    private final BufferedImage invisibleImage;

    Tank(GameWorld gw, float x, float y, float vx, float vy, float angle,
         BufferedImage originalImage, BufferedImage invisibleImage) {
        super(x, y, originalImage);
        this.gw = gw;
        this.tankId = new Random().nextInt(300);
        this.screenX = x;
        this.screenY = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.originalImage = originalImage;
        this.invisibleImage = invisibleImage;
        this.img = originalImage;
        this.health = health;
        this.lives = lives;
        this.respawnPoint = new Point2D.Float(x, y);
        this.speedTimer = new Timer();
        this.shieldTimer = new Timer();
    }

    private int safeShootX() {
        return (int) (x + Math.cos(Math.toRadians(angle)) * img.getWidth() / 2);
    }

    private int safeShootY() {
        return (int) (y + Math.sin(Math.toRadians(angle)) * img.getHeight() / 2);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    void setX(float x) {
        this.x = x;
    }

    void setY(float y) {
        this.y = y;
    }

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void toggleShootPressed() {
        this.ShootPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleShootPressed() {
        this.ShootPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    public float getScreenX() {
        return screenX;
    }

    public float getScreenY() {
        return screenY;
    }

    public int getTankId() {
        return tankId;
    }

    public void update(GameWorld gw) {
        prevX = x;
        prevY = y;

        boolean inWater = gw.isWaterArea(this.x, this.y);
        if (inWater) {
            this.R = 2;
        } else {
            this.R = 6;
        }

        boolean inTree = gw.isInTreesArea(this.x, this.y);
        if (!inTree) {
            setVisible(true);
        } else {
            setVisible(false);
        }

        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }

        long currentTime = System.currentTimeMillis();
        if (this.ShootPressed && currentTime > this.timeSinceLastShot + this.coolDown) {
            this.timeSinceLastShot = currentTime;
            var p = ResourcePools.getPoolInstance("bullet");
            p.initObject(safeShootX(), safeShootY(), angle);
            Bullet b = (Bullet) p;
            b.setOwner(this.tankId);
            gw.addGameObject(b);
            ResourceManager.getSound("shooting").play(); // Start shooting sound
            Animation shootAnimation = new Animation(safeShootX(), safeShootY(), ResourceManager.getAnim("bulletshoot"));
            shootAnimation.start();  // Start shooting animation
            gw.anims.add(shootAnimation);
        }

        centerScreen();
        checkBorder();
        hitBox.setLocation((int) x, (int) y); // This step is very important, do not new hitbox.
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (visible) {
            this.img = originalImage;
        } else {
            this.img = invisibleImage;
        }
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        prevX = x;
        prevY = y;
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
        centerScreen();
    }

    private void moveForwards() {
        prevX = x;
        prevY = y;
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
        centerScreen();
    }

    private void centerScreen() {
        this.screenX = this.x - GameConstants.GAME_SCREEN_WIDTH / 4f;
        this.screenY = this.y - GameConstants.GAME_SCREEN_HEIGHT / 4f;

        if (this.screenX < 0) screenX = 0;
        if (this.screenY < 0) screenY = 0;

        if (this.screenX > GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 2f) {
            this.screenX = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 2f;
        }

        if (this.screenY > GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT) {
            this.screenY = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT;
        }
    }

    private void checkBorder() {
        if (x < 30) x = 30;
        if (y < 40) y = 40;
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 115) {
            y = GameConstants.GAME_WORLD_HEIGHT - 115;
        }
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }


    public void drawImage(Graphics2D g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g.drawImage(this.img, rotation, null);
        drawHealthBar(g);
    }

    private void drawHealthBar(Graphics2D g) {
        // Health bar settings
        int healthBarWidth = 50;
        int healthBarHeight = 5;
        int healthBarX = (int) x;
        int healthBarY = (int) y - 10;  // Adjust to position above the tank

        // Draw the background of the health bar
        g.setColor(Color.gray);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

        // Draw the current health
        g.setColor(Color.green);
        int healthWidth = (int) ((health / 10.0) * healthBarWidth);  // Assuming health is out of 100
        g.fillRect(healthBarX, healthBarY, healthWidth, healthBarHeight);

        // Draw lives count next to the health bar
        g.setColor(Color.white);
        g.drawString("Lives: " + lives, healthBarX + healthBarWidth + 5, healthBarY + healthBarHeight);
    }


    @Override
    public void handleCollision(Collidable with) {
        if (with instanceof BreakableWall) {
            BreakableWall bwall = (BreakableWall) with;
            revertPosition(bwall);
        } else if (with instanceof Wall) {
            Wall uwall = (Wall) with;
            revertPosition(uwall);
        } else if (with instanceof Tank) {
            Tank tank = (Tank) with;
            revertPosition(tank);
        } else if (with instanceof Health) {
            Health health = (Health) with;
            health.setHasCollided(true);
            ResourceManager.getSound("pickup").play();
            this.health += 10;
        } else if (with instanceof Speed) {
            Speed speed = (Speed) with;
            speed.setHasCollided(true);
            ResourceManager.getSound("pickup").play();
            this.R += 10;
            speedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    R = 6;
                }
            }, 5000);
        } else if (with instanceof Shield) {
            Shield shield = (Shield) with;
            shield.setHasCollided(true);
            activateShield();
            ResourceManager.getSound("pickup").play();
        }
    }

    private void activateShield() {
        this.isShielded = true;
        shieldTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                isShielded = false;
            }
        }, 8000);
    }

    // Back tank position
    private void revertPosition(Collidable with) {
        this.x = prevX;
        this.y = prevY;
    }

    public void decreaseHealth(int damage) {
        this.health -= damage; // Decrease health
        if (this.health <= 0) {
            this.lives -= 1; // Decrease lives
            if (this.lives > 0) {
                respawn();
            } else {
                die();
            }
        }
    }

    private void die() {
        this.setActive(false); // Make tank inactive
        if (gw != null) {
            gw.endGame(this); // Assume this method is designed to handle game over logic
        }
    }

    private void respawn() {
        this.health = 10;
        this.x = respawnPoint.x;
        this.y = respawnPoint.y;
//        System.out.println("Respawned with full health.");
    }

}
