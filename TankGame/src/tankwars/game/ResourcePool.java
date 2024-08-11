package tankwars.game;

import tankwars.ResourceManager;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ResourcePool<G extends Poolable> {
    private final static int INIT_CAPACITY = 100;
    private final String resourceName;
    private final Class<G> resourceClass;
    private final ArrayList<G> resources;

    public ResourcePool(String resourceName, Class<G> resourceClass, int initialCapacity) {
        this.resourceName = resourceName;
        this.resourceClass = resourceClass;
        this.resources = new ArrayList<>(initialCapacity);
    }

    public G removeFromPool() {
        if (this.resources.isEmpty()) {
            this.refillPool();
        }
        return this.resources.removeLast();

    }

    public void addToPool(G obj) {
        this.resources.addLast(obj);
    }

    private void refillPool()  {
        this.fillPool(INIT_CAPACITY);
    }

    public ResourcePool<G> fillPool(int size) {
        BufferedImage img = ResourceManager.getSprite(this.resourceName);
        for (int i = 0; i < size; i++) {
            try {
                var g = this.resourceClass.getDeclaredConstructor(BufferedImage.class).newInstance(img);
                this.resources.add(g);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }
}
