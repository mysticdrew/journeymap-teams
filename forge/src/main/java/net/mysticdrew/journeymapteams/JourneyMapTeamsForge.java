package net.mysticdrew.journeymapteams;

import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.mysticdrew.journeymapteams.config.ServerConfig;

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

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event)
    {
        ServerConfig.load(FMLPaths.CONFIGDIR.get());
    }

}
