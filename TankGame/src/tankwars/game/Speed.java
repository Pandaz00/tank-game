package tankwars.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Speed extends GameObject implements Collidable {
    float x, y;
    BufferedImage img;

    public Speed(float x, float y, BufferedImage img) {
        super(x, y, img);
    }

    @Override
    public void handleCollision(Collidable with) {

    }
}
