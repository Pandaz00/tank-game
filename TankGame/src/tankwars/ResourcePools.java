package tankwars;

import tankwars.game.Poolable;
import tankwars.game.ResourcePool;

import java.util.HashMap;
import java.util.Map;

public class ResourcePools {

    private static final Map<String, ResourcePool<? extends Poolable>> pools = new HashMap<>();

    public static void addPool(String key, ResourcePool<? extends Poolable> pool) {
        ResourcePools.pools.put(key, pool);
    }

    public static Poolable getPoolInstance(String key) {
        return ResourcePools.pools.get(key).removeFromPool();
    }

//    public static void returnPoolInstance(String key, Poolable pool) {
//        return ResourcePools.pools.get(key).addToPool(pool);
//    }
}
