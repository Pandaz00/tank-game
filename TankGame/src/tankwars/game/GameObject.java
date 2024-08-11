package tankwars.game;

import com.sun.source.tree.Tree;
import tankwars.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {

    /**
     * Create new game object based on a type
     * @param type type of object to create
     * @param x pos
     * @param y pos
     * @return created subclass of GameObject
     */

    protected float x, y; // if private ,need getter and setter
    protected BufferedImage img;
    protected Rectangle hitBox;

    protected boolean hasCollided = false;
    protected boolean isActive = true;

    public GameObject(float x, float y, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
        hitBox = new Rectangle((int) x,(int) y,this.img.getWidth(),this.img.getHeight());
    }

    public static GameObject newInstance(String type, float x, float y) {
        return switch (type) {
            case "3","9" -> new Wall(x, y, ResourceManager.getSprite("uwall"));
            case "2" -> new BreakableWall(x, y, ResourceManager.getSprite("bwall"));
            case "4" -> new Speed(x, y, ResourceManager.getSprite("speed"));
            case "5" -> new Shield(x, y, ResourceManager.getSprite("shield"));
            case "6" -> new Health(x, y, ResourceManager.getSprite("health"));
            case "7" -> new Water(x, y, ResourceManager.getSprite("water"));
            case "8" -> new Trees(x, y, ResourceManager.getSprite("trees"));
            default -> throw new IllegalArgumentException("Unsupported type -> %s\n".formatted(type));
        };
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public void drawImage(Graphics2D g) {
        g.drawImage(img, (int) x, (int) y, null);
    };

    public Rectangle getHitBox() {
        return hitBox.getBounds();
    }

    public void setHasCollided(boolean hasCollided) {
        this.hasCollided = hasCollided;
    }

    public boolean getHasCollided() {
        return this.hasCollided;
    }

}
