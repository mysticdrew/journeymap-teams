package net.mysticdrew.journeymapteams;

import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod(Constants.MOD_ID)
public class JourneyMapTeamsNeoForge
{
    public JourneyMapTeamsNeoForge()
    {
        Set<String> modIds = new HashSet<>();
        ModList.get().getMods().forEach(m -> modIds.add(m.getModId()));
        JourneyMapTeams.init(modIds);
    }
}
