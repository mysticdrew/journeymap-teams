package net.mysticdrew.journeymapteams;

import java.util.Set;

/**
 * Common mod bootstrap. Each loader entry point calls {@link #init(Set)} with
 * the set of loaded mod ids during mod construction.
 */
public final class JourneyMapTeams
{
    private JourneyMapTeams() {}

    public static void init(Set<String> loadedModIds)
    {
        ModEnvironment.setLoadedModIds(loadedModIds);
    }
}
