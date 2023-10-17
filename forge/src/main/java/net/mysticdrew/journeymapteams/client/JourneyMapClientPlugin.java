package net.mysticdrew.journeymapteams.client;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.forge.EntityRadarUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mysticdrew.journeymapteams.Constants;
import net.mysticdrew.journeymapteams.client.integration.JourneyMapCommonPlugin;

@ClientPlugin
public class JourneyMapClientPlugin implements IClientPlugin
{
    @Override
    public void initialize(IClientAPI api)
    {
        JourneyMapCommonPlugin.registerEvents(api);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityUpdate(EntityRadarUpdateEvent event)
    {
        if (event.getType() == EntityRadarUpdateEvent.EntityType.PLAYER)
        {
            JourneyMapCommonPlugin.getInstance().setPlayerColor(event.getWrappedEntity());
        }
    }

    @Override
    public String getModId()
    {
        return Constants.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent clientEvent)
    {
        JourneyMapCommonPlugin.getInstance().handleEvents(clientEvent);
    }
}
