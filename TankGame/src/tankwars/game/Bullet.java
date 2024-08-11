package tankwars.game;

import tankwars.GameConstants;
import tankwars.ResourceManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Bullet extends GameObject implements Poolable, Updatable, Collidable {
    private float vx;
    private float vy;
    private float angle;
    private int tankId;
    private float R = 4;
    private float ROTATIONSPEED = 1.0f;
    List<Animation> anims = new ArrayList<>();

    public Bullet(BufferedImage img) { // For the resource pool
        super(0, 0, img);
        this.vx = 0;
        this.vy = 0;
        this.angle = 0;
    }

    public Bullet(float x, float y,float angle, BufferedImage img) {
        super(x, y, img);
        this.angle = angle;

    }

    public void update(GameWorld gw) {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
//        checkBorder();
        hitBox.setLocation((int) x,(int) y); // This step is very important, do not new hitbox.
        // hitBox = new Rectangle((int) x,(int) y); Do not do this, big mistake.
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
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

    public void setOwner(int id) {
        this.tankId = id;
    }

    public int getOwner(int id) {
        return this.tankId;
    }

    public void drawImage(Graphics2D g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        g.drawImage(this.img, rotation, null);

    }

    @Override
    public void initObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void initObject(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    @Override
    public void resetObject() {
        this.x = -5;
        this.y = -5;
    }

    public int getOwner() {
        return this.tankId;
    }

    @Override
    public void handleCollision(Collidable with) {
        if (with instanceof BreakableWall) {
            BreakableWall bwall = (BreakableWall) with;
            bwall.setHasCollided(true);
            this.setHasCollided(true);
            ResourceManager.getSound("bullet").play();
        } else if (with instanceof Tank) {
            Tank tank = (Tank) with;
            if (tank.getTankId() != getOwner()) {
                tank.decreaseHealth(1);
                this.setHasCollided(true);
                ResourceManager.getSound("bullet").play();
            }
        }
    }
}
