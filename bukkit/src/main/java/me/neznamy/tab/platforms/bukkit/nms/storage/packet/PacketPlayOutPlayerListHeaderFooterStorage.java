package me.neznamy.tab.platforms.bukkit.nms.storage.packet;

import me.neznamy.tab.api.ProtocolVersion;
import me.neznamy.tab.api.protocol.PacketPlayOutPlayerListHeaderFooter;
import me.neznamy.tab.platforms.bukkit.nms.storage.nms.NMSStorage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class PacketPlayOutPlayerListHeaderFooterStorage {

    public static Class<?> CLASS;
    public static Constructor<?> CONSTRUCTOR;
    public static Field HEADER;
    public static Field FOOTER;

    public static void load(NMSStorage nms) throws NoSuchMethodException {
        if (nms.getMinorVersion() < 8) return;
        HEADER = nms.getFields(CLASS, nms.IChatBaseComponent).get(0);
        FOOTER = nms.getFields(CLASS, nms.IChatBaseComponent).get(1);
        if (nms.getMinorVersion() >= 17) {
            CONSTRUCTOR = CLASS.getConstructor(nms.IChatBaseComponent, nms.IChatBaseComponent);
        } else {
            CONSTRUCTOR = CLASS.getConstructor();
        }
    }

    public static Object build(PacketPlayOutPlayerListHeaderFooter packet, ProtocolVersion clientVersion) throws ReflectiveOperationException {
        NMSStorage nms = NMSStorage.getInstance();
        if (nms.getMinorVersion() < 8) return null;
        if (CONSTRUCTOR.getParameterCount() == 2) {
            return CONSTRUCTOR.newInstance(nms.toNMSComponent(packet.getHeader(), clientVersion),
                    nms.toNMSComponent(packet.getFooter(), clientVersion));
        }
        Object nmsPacket = CONSTRUCTOR.newInstance();
        HEADER.set(nmsPacket, nms.toNMSComponent(packet.getHeader(), clientVersion));
        FOOTER.set(nmsPacket, nms.toNMSComponent(packet.getFooter(), clientVersion));
        return nmsPacket;
    }
}