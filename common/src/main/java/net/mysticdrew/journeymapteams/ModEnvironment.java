package net.mysticdrew.journeymapteams;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Loader-agnostic holder for the set of mod ids present at runtime. Populated
 * once by each loader's entry point during mod construction, before JourneyMap
 * initializes plugins. The Vanilla plugin reads this to decide whether to
 * self-disable in favor of a dedicated teams mod.
 */
public final class ModEnvironment
{
    private static final Set<String> LOADED_MOD_IDS = new HashSet<>();

    private ModEnvironment() {}

    public static void setLoadedModIds(Set<String> modIds)
    {
        LOADED_MOD_IDS.clear();
        LOADED_MOD_IDS.addAll(modIds);
    }

    public static boolean isLoaded(String modId)
    {
        return LOADED_MOD_IDS.contains(modId);
    }

    public static Set<String> getLoadedModIds()
    {
        return Collections.unmodifiableSet(LOADED_MOD_IDS);
    }
}
