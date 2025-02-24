package me.neznamy.tab.shared.hook;

import lombok.SneakyThrows;
import me.neznamy.tab.shared.platform.TabPlayer;

import java.lang.reflect.Method;
import java.util.UUID;

public class NeonHook {

    private static Method getInstance;
    private static Method getDisguiseManager;
    private static Method getDisguise;

    static {
        try {
            Class<?> neonClass = Class.forName("net.leonemc.neon.spigot.Neon");
            getInstance = neonClass.getDeclaredMethod("getInstance");

            Class<?> disguiseManagerClass = Class.forName("net.leonemc.neon.spigot.features.disguise.DisguiseManager");
            getDisguiseManager = neonClass.getMethod("getDisguiseManager");
            getDisguise = disguiseManagerClass.getMethod("get", UUID.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static Object getDisguise(TabPlayer p) {
        if (getInstance == null || getDisguiseManager == null || getDisguise == null) {
            return null;
        }

        Object neonInstance = getInstance.invoke(null);
        Object disguiseManager = getDisguiseManager.invoke(neonInstance);
        return getDisguise.invoke(disguiseManager, p.getUniqueId());
    }
}