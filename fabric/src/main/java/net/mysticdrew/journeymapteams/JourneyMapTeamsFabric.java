package net.mysticdrew.journeymapteams;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashSet;
import java.util.Set;

public class JourneyMapTeamsFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        Set<String> modIds = new HashSet<>();
        FabricLoader.getInstance().getAllMods()
                .forEach(m -> modIds.add(m.getMetadata().getId()));
        JourneyMapTeams.init(modIds);
    }
}
