package me.neznamy.tab.shared;

import java.util.*;

import me.neznamy.tab.shared.TabConstants.Placeholder;
import me.neznamy.tab.shared.features.layout.LayoutManager;
import me.neznamy.tab.shared.features.sorting.types.SortingType;

/**
 * Class for detecting misconfiguration in config files and fix mistakes
 * to avoid headaches when making a configuration mistake.
 */
public class MisconfigurationHelper {

    /**
     * Checks if configured refresh intervals are non-negative, non-zero and
     * divisible by {@link Placeholder#MINIMUM_REFRESH_INTERVAL}. If not,
     * value is fixed in the map and console warn is sent.
     *
     * @param   refreshIntervals
     *          Configured refresh intervals
     */
    public void fixRefreshIntervals(Map<String, Integer> refreshIntervals) {
        LinkedHashMap<String, Integer> valuesToFix = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : refreshIntervals.entrySet()) {
            int interval = entry.getValue();
            if (interval < 0) {
                startupWarn("Invalid refresh interval configured for " + entry.getKey() +
                        " (" + interval + "). Value cannot be negative.");
            } else if (interval == 0) {
                startupWarn("Invalid refresh interval configured for " + entry.getKey() +
                        " (0). Value cannot be zero.");
            } else if (interval % Placeholder.MINIMUM_REFRESH_INTERVAL != 0) {
                startupWarn("Invalid refresh interval configured for " + entry.getKey() +
                        " (" + interval + "). Value must be divisible by " + Placeholder.MINIMUM_REFRESH_INTERVAL + ".");
            } else continue;
            valuesToFix.put(entry.getKey(), Placeholder.MINIMUM_REFRESH_INTERVAL);
        }
        refreshIntervals.putAll(valuesToFix);
    }

    /**
     * Makes interval divisible by {@link Placeholder#MINIMUM_REFRESH_INTERVAL}
     * and sends error message if it was not already or was 0 or less
     *
     * @param   name
     *          name of animation used in error message
     * @param   interval
     *          configured change interval
     * @return  fixed change interval
     */
    public int fixAnimationInterval(String name, int interval) {
        if (interval == 0) {
            startupWarn(String.format("Animation \"&e%s&c\" has refresh interval of 0 milliseconds! Did you forget to configure it? &bUsing 1000.", name));
            return 1000;
        }
        if (interval < 0) {
            startupWarn(String.format("Animation \"&e%s&c\" has refresh interval of %s. Refresh cannot be negative! &bUsing 1000.", name, interval));
            return 1000;
        }
        if (interval % TabConstants.Placeholder.MINIMUM_REFRESH_INTERVAL != 0) {
            int newInterval = interval - interval % TabConstants.Placeholder.MINIMUM_REFRESH_INTERVAL;
            if (newInterval == 0) newInterval = TabConstants.Placeholder.MINIMUM_REFRESH_INTERVAL;
            startupWarn(String.format("Animation \"&e%s&c\" has refresh interval of %s, which is not divisible by " +
                    TabConstants.Placeholder.MINIMUM_REFRESH_INTERVAL + "! &bUsing %s.", name, interval, newInterval));
            return newInterval;
        }
        return interval;
    }

    /**
     *
     * Returns the list if not null, empty list and error message if null
     *
     * @param   name
     *          name of animation used in error message
     * @param   list
     *          list of configured animation frames
     * @return  the list if it's valid, singleton list with {@code "<Invalid Animation>"} otherwise
     */
    public List<String> fixAnimationFrames(String name, List<String> list) {
        if (list == null) {
            startupWarn("Animation \"&e" + name + "&c\" does not have any texts defined!");
            return Collections.singletonList("<Animation does not have any texts>");
        }
        return list;
    }

    /**
     * Checks if belowname text contains suspicious placeholders, which
     * will not work as users may expect, since the text must be
     * the same for all players.
     *
     * @param   text
     *          Configured belowname text
     */
    public void checkBelowNameText(String text) {
        if (!text.contains("%")) return;
        if (text.contains("%animation") || text.contains("%condition")) return;
        startupWarn("Belowname text is set to " + text + ", however, the feature cannot display different text on different players " +
                "due to a minecraft limitation. Placeholders will be parsed for viewing player.");
    }

    /**
     * Sends a console warn that entered skin definition does not match
     * any of the supported patterns.
     *
     * @param   definition
     *          Configured skin definition
     */
    public void invalidLayoutSkinDefinition(String definition) {
        startupWarn("Invalid skin definition: \"" + definition + "\". Supported patterns are:");
        startupWarn("#1 - \"player:<name>\" for skin of player with specified name");
        startupWarn("#2 - \"mineskin:<id>\" for UUID of chosen skin from mineskin.org");
        startupWarn("#3 - \"texture:<texture>\" for raw texture string");
    }

    /**
     * Sends a console warn about a fixed line in layout being invalid.
     *
     * @param   layout
     *          Layout name where fixed slot is defined
     * @param   line
     *          Line definition from configuration
     */
    public void invalidFixedSlotDefinition(String layout, String line) {
        startupWarn("Layout " + layout + " has invalid fixed slot defined as \"" + line + "\". Supported values are " +
                "\"SLOT|TEXT\" and \"SLOT|TEXT|SKIN\", where SLOT is a number from 1 to 80, TEXT is displayed text and SKIN is skin used for the slot");
    }

    /**
     * Sends a console warn that specified layout direction is not a valid
     * enum value.
     *
     * @param   direction
     *          Configured direction
     */
    public void invalidLayoutDirection(String direction) {
        startupWarn("\"&e" + direction + "&c\" is not a valid type of layout direction. Valid options are: &e" + Arrays.deepToString(LayoutManager.Direction.values()) + ". &bUsing COLUMNS");
    }

    public void invalidSortingTypeElement(String element, Set<String> validTypes) {
        startupWarn("\"&e" + element + "&c\" is not a valid sorting type element. Valid options are: &e" + validTypes + ".");
    }

    public void invalidSortingPlaceholder(String placeholder, SortingType type) {
        startupWarn("\"" + placeholder + "\" is not a valid placeholder for " + type.getClass().getSimpleName() + " sorting type");
    }

    public void conditionHasNoConditions(String conditionName) {
        startupWarn("Condition \"" + conditionName + "\" is missing \"conditions\" section.");
    }

    public void invalidConditionPattern(String conditionName, String line) {
        startupWarn("Line \"" + line + "\" in condition " + conditionName + " is not a valid condition pattern.");
    }

    public void invisibleAndUnlimitedNameTagsAreMutuallyExclusive() {
        startupWarn("Unlimited nametag mode is enabled as well as invisible nametags. These 2 options are mutually exclusive.");
        startupWarn("If you want nametags to be invisible, you don't need unlimited nametag mode at all.");
        startupWarn("If you want enhanced nametags without limits, making them invisible would defeat the purpose.");
    }

    public void invalidDateFormat(String format) {
        startupWarn("Format \"" + format + "\" is not a valid date/time format. Did you try to use color codes?");
    }

    @SuppressWarnings("unchecked")
    public <T> T fromMapOrElse(Map<String, Object> map, String key, T defaultValue, String warnMessage) {
        if (map.containsKey(key)) {
            return (T) map.get(key);
        } else {
            startupWarn(warnMessage);
            return defaultValue;
        }
    }

    /**
     * Sends a startup warn message into console
     *
     * @param   message
     *          message to print into console
     */
    private void startupWarn(String message) {
        TAB.getInstance().sendConsoleMessage("&c" + message, true);
    }
}
