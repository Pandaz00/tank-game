package tankwars.game;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Health extends GameObject implements Collidable {

    public Health(float x, float y, BufferedImage img) {
        super(x, y, img);
    }


    @Override
    public void handleCollision(Collidable with) {

    }
}
