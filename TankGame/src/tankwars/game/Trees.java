package tankwars.game;

import java.awt.image.BufferedImage;

public class Trees extends GameObject implements Collidable {

    public Trees(float x, float y, BufferedImage img) {
        super(x, y, img);
    }

    @Override
    public void handleCollision(Collidable with) {

    }
}
