package tankwars;

import tankwars.game.Sound;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<>();
    private final static Map<String, Sound> sounds = new HashMap<>();
    private final static Map<String, List<BufferedImage>> animations = new HashMap<>();
    private final static Map<String, Integer> animInfo = new HashMap<>(){{
        put("bullethit", 24);
        put("bulletshoot", 24);
    }};

    private static BufferedImage loadSprite(String path) throws IOException {
        return ImageIO.read(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResource(path),
                        "Resource %s was not found".formatted((path)))
        );
    }

    private static Sound loadSound(String path) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(
                        ResourceManager.class.getClassLoader().getResource(path),
                        "Resource %s was not found".formatted(path))
        );
        Clip c = AudioSystem.getClip();
        c.open(ais);
        Sound s = new Sound(c);
        return s;
    }

    private static void loadAnims() {
        String baseFormat = "animations/%s/%s_%04d.png";
        ResourceManager.animInfo.forEach((animationName, frameCount) -> {
            List<BufferedImage> f = new ArrayList<>(frameCount);
            try {
                for (int i = 0; i < frameCount; i++) {
                    String spritePath = String.format(baseFormat, animationName, animationName, i);
                    f.add(loadSprite(spritePath));
                }
                ResourceManager.animations.put(animationName, f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void initSprites() throws IOException {
        ResourceManager.sprites.put("menu",      loadSprite("menu/title.png"));
        ResourceManager.sprites.put("t1",        loadSprite("tank/tank1.png"));
        ResourceManager.sprites.put("t2",        loadSprite("tank/tank2.png"));
        ResourceManager.sprites.put("invisible", loadSprite("tank/invisible.png"));
        ResourceManager.sprites.put("bullet",    loadSprite("bullet/bullet.png"));
        ResourceManager.sprites.put("rocket1",   loadSprite("bullet/rocket1.png"));
        ResourceManager.sprites.put("rocket2",   loadSprite("bullet/rocket2.png"));
        ResourceManager.sprites.put("health",    loadSprite("powerups/health.png"));
        ResourceManager.sprites.put("speed",     loadSprite("powerups/speed.png"));
        ResourceManager.sprites.put("shield",    loadSprite("powerups/shield.png"));
        ResourceManager.sprites.put("uwall",     loadSprite("wall/uwall.png"));
        ResourceManager.sprites.put("bwall",     loadSprite("wall/bwall.png"));
        ResourceManager.sprites.put("water",     loadSprite("wall/water.png"));
        ResourceManager.sprites.put("trees",      loadSprite("wall/trees.png"));
        ResourceManager.sprites.put("floor",     loadSprite("floor/floor.bmp"));
    }

    public static void loadSounds() {
        try {
            ResourceManager.sounds.put("bg", loadSound("sounds/bg1.wav"));
            ResourceManager.sounds.put("bullet", loadSound("sounds/bullet.wav"));
            ResourceManager.sounds.put("shooting", loadSound("sounds/shooting.wav"));
            ResourceManager.sounds.put("pickup", loadSound("sounds/pickup.wav"));
//            ResourceManager.sounds.put("pickup", loadSound("sounds/pickup.wav"));
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadAssets() {
        try {
            initSprites();
            loadSounds();
            loadAnims();
//            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException("Loading assets failed", e);
        }
    }

    public static BufferedImage getSprite(String key) {
        if (!ResourceManager.sprites.containsKey(key)) {
            throw new IllegalArgumentException(
                    "Resource %s is not in map".formatted(key)
            );
        }
        return ResourceManager.sprites.get(key);
    }

    public static Sound getSound(String key) {
        if (!ResourceManager.sounds.containsKey(key)) {
            throw new IllegalArgumentException(
                    "Resource %s is not in map".formatted(key)
            );
        }
        return ResourceManager.sounds.get(key);
    }

    public static List<BufferedImage> getAnim(String key) {
        if (!ResourceManager.animations.containsKey(key)) {
            throw new IllegalArgumentException(
                    "Resource %s is not in map".formatted(key)
            );
        }
        return ResourceManager.animations.get(key);
    }

//    public static void main(String[] args) {
//        ResourceManager.loadAssets();
//        System.out.println();
//        loadAnims();
//        System.out.println();
//    }
}
