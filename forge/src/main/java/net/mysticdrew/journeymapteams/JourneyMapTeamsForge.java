package net.mysticdrew.journeymapteams;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.mysticdrew.journeymapteams.integration.betterteams.BetterTeamsClientLifecycle;

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

        if (FMLEnvironment.dist.isClient())
        {
            ClientPlayerNetworkEvent.LoggingOut.BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) ->
                    BetterTeamsClientLifecycle.onDisconnect());
        }
    }
}
