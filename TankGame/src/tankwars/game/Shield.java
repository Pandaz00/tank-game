package tankwars.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Shield extends GameObject implements Collidable {

    public Shield(float x, float y, BufferedImage img) {
        super(x, y, img);
    }


    @Override
    public void handleCollision(Collidable with) {

    }
}
