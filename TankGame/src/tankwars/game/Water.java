package tankwars.game;

import java.awt.image.BufferedImage;

public class Water extends GameObject implements Collidable {

    public Water(float x, float y, BufferedImage img) {
        super(x, y, img);
    }

    @Override
    public void handleCollision(Collidable with) {

    }
}
