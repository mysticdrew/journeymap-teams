package net.mysticdrew.journeymapteams;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Bukkit entry point only. This class is the {@code main} declared in
 * paper-plugin.yml so Paper loads the jar; it is NOT a JourneyMap plugin.
 *
 * The JourneyMap-side plugins ({@link net.mysticdrew.journeymapteams.integration.VanillaTeamsServerPlugin}
 * and {@link net.mysticdrew.journeymapteams.integration.ftb.FtbTeamsServerPlugin})
 * are plain classes carrying {@code @JourneyMapPlugin}; JM's Paper scanner finds them
 * by classpath annotation scan.
 *
 * The only job here is to populate {@link ModEnvironment} from the Bukkit plugin
 * list so {@code FtbTeamsServerPlugin} can self-disable when FTB Teams is not present.
 */
public final class JourneyMapTeamsPaper extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        Set<String> loadedIds = new HashSet<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
        {
            loadedIds.add(plugin.getName().toLowerCase());
        }
        JourneyMapTeams.init(loadedIds);
    }
}
