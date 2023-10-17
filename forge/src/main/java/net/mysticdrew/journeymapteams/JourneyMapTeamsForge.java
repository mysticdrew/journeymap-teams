package net.mysticdrew.journeymapteams;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.List;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class JourneyMapTeamsForge
{
    public JourneyMapTeamsForge()
    {
        new JourneyMapTeams(getMods());
    }

    public List<String> getMods()
    {
        List<String> list = new ArrayList<String>();
        for (IModInfo mod : ModList.get().getMods())
        {
            if (ModList.get().isLoaded(mod.getModId()))
            {
                list.add(mod.getModId());
            }
        }
        return list;
    }

}
