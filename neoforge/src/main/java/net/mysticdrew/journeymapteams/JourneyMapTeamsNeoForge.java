package net.mysticdrew.journeymapteams;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.mysticdrew.journeymapteams.integration.betterteams.BetterTeamsClientLifecycle;

import java.util.HashSet;
import java.util.Set;

@Mod(Constants.MOD_ID)
public class JourneyMapTeamsNeoForge
{
    public JourneyMapTeamsNeoForge(IEventBus eventBus)
    {
        Set<String> modIds = new HashSet<>();
        ModList.get().getMods().forEach(m -> modIds.add(m.getModId()));
        JourneyMapTeams.init(modIds);

        if (FMLEnvironment.getDist().isClient())
        {
            NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) ->
                    BetterTeamsClientLifecycle.onDisconnect());
        }
    }
}
