package net.mysticdrew.journeymapteams;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.List;

public class JourneyMapTeamsFabric implements ModInitializer
{
    public JourneyMapTeamsFabric()
    {
        new JourneyMapTeams(getMods());
    }


    public List<String> getMods()
    {
        List<String> list = new ArrayList<String>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods())
        {
            ModMetadata meta = mod.getMetadata();
            if (isModLoaded(meta.getId()))
            {
                list.add(meta.getId());
            }
        }
        return list;
    }

    public boolean isModLoaded(String modId)
    {
        return FabricLoader.getInstance().isModLoaded(modId) || FabricLoader.getInstance().isModLoaded(modId.toLowerCase());
    }

    @Override
    public void onInitialize()
    {

    }
}
