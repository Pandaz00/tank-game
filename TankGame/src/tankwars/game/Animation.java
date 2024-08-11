package tankwars.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public class Animation {
    private float x, y;
    private List<BufferedImage> frames;
    private long delay = 30;
    private long timeSinceLastFrame = 0;
    private int currentFrame;
    private boolean running = false;

    public Animation(float x, float y, List<BufferedImage> frames) {
        this.x = x - frames.get(0).getWidth() / 2f;
        this.y = y - frames.get(0).getHeight() / 2f;
        this.frames = frames;
        this.running = false;
        this.currentFrame = 0;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        if (this.timeSinceLastFrame + delay < currentTime) {
//            this.currentFrame = (this.currentFrame + 1) % frames.size();
            this.currentFrame++;
            if (this.currentFrame == this.frames.size()) {
                this.running = false; // Play animation once
//                this.currentFrame = 0; // Still play animation
            }
            this.timeSinceLastFrame = currentTime;
        }
    }

    public void start() {
        this.running = true;
        this.timeSinceLastFrame = System.currentTimeMillis();
    }


    public void render(Graphics g) {
        if (this.running) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform scaler = AffineTransform.getTranslateInstance(x, y);
            scaler.scale(0.8, 0.8);
            g.drawImage(this.frames.get(currentFrame), (int) x, (int) y, null);
//            g2d.drawImage(this.frames.get(currentFrame), scaler, null);
        }
    }



}
