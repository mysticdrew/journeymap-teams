package net.mysticdrew.journeymapteams;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.mysticdrew.journeymapteams.integration.betterteams.BetterTeamsClientLifecycle;

public class JourneyMapTeamsFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> BetterTeamsClientLifecycle.onDisconnect());
    }
}
