package me.neznamy.tab.shared.hook;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.platform.TabPlayer;
import me.neznamy.tab.shared.util.ReflectionUtils;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Class that hooks into LuckPerms if installed.
 */
@Getter
public class LuckPermsHook {

    /** Instance of the class */
    @Getter private static final LuckPermsHook instance = new LuckPermsHook();

    /** Flag tracking if LuckPerms is installed or not */
    private final boolean installed = ReflectionUtils.classExists("net.luckperms.api.LuckPerms");

    /** Function retrieving group of player from LuckPerms */
    private final Function<TabPlayer, String> groupFunction = p -> {
        User user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());

        if (user == null) {
            return "default";
        }

        final String fakeGroup = getFakeGroup(p);
        if (fakeGroup != null) {
            return fakeGroup;
        }

        final String group = user.getPrimaryGroup();
        if (group.contains("media")) {
            return "media";
        }

        return group;
    };

    /**
     * Returns a fake group if the player is disguised
     * @param p - player to get group of
     */
    @SneakyThrows
    public @Nullable String getFakeGroup(TabPlayer p) {
        final Object disguiseObject = NeonHook.getDisguise(p);

        if (disguiseObject != null) {
            final Object rankObject = disguiseObject.getClass().getDeclaredMethod("getRank").invoke(disguiseObject);
            return (String) rankObject.getClass().getDeclaredMethod("getFakeRank").invoke(rankObject);
        }

        return null;
    }

    /**
     * Returns player's prefix configured in LuckPerms
     *
     * @param   p
     *          Player to get prefix of
     * @return  Player's prefix
     */
    public String getPrefix(@NonNull TabPlayer p) {
        return getValue(p, true);
    }

    /**
     * Returns player's suffix configured in LuckPerms
     *
     * @param   p
     *          Player to get suffix of
     * @return  Player's suffix
     */
    public String getSuffix(@NonNull TabPlayer p) {
        return getValue(p, false);
    }

    /**
     * Returns player's metadata value based on entered boolean flag,
     * {@code true} for prefix, {@code false} for suffix.
     *
     * @param   p
     *          Player to get metadata value of
     * @param   prefix
     *          {@code true} if prefix should be returned, {@code false} if suffix
     * @return  Player's metadata value
     */
    private String getValue(@NonNull TabPlayer p, boolean prefix) {
        User user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());
        if (user == null) return "";
        Optional<QueryOptions> options = LuckPermsProvider.get().getContextManager().getQueryOptions(user);
        if (!options.isPresent()) return "";
        CachedMetaData data = user.getCachedData().getMetaData(options.get());
        String value;
        if (prefix) {
            value = data.getPrefix();
        } else {
            value = data.getSuffix();
        }
        return value == null ? "" : value;
    }
}
