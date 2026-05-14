package net.mysticdrew.journeymapteams;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod(Constants.MOD_ID)
public class JourneyMapTeamsForge
{
    public JourneyMapTeamsForge()
    {
        Set<String> modIds = new HashSet<>();
        ModList.getMods().forEach(m -> modIds.add(m.getModId()));
        JourneyMapTeams.init(modIds);
    }
}
