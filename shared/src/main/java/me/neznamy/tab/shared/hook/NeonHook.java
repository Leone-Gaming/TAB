package me.neznamy.tab.shared.hook;

import lombok.SneakyThrows;
import me.neznamy.tab.shared.platform.TabPlayer;

import java.util.UUID;

public class NeonHook {

    @SneakyThrows
    public static Object getDisguise(TabPlayer p) {
        final Object instance = Class.forName("net.leonemc.neon.spigot.Neon").getDeclaredMethod("getInstance").invoke(null);
        return instance.getClass().getMethod("getDisguiseManager").invoke(instance).getClass().getMethod("get", UUID.class).invoke(instance, p.getUniqueId());
    }

}
