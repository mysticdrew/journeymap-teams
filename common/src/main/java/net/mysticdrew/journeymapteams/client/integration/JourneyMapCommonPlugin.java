package net.mysticdrew.journeymapteams.client.integration;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.RegistryEvent;
import journeymap.client.api.model.WrappedEntity;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.Handler;

import java.util.EnumSet;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

public class JourneyMapCommonPlugin
{
    private static JourneyMapCommonPlugin instance;
    private final Handler handler;

    public Properties properties;

    public JourneyMapCommonPlugin(Handler handler)
    {
        this.handler = handler;
    }

    public static void init(Handler handler)
    {
        instance = new JourneyMapCommonPlugin(handler);
    }

    public static JourneyMapCommonPlugin getInstance()
    {
        if (instance == null)
        {
            throw new UnsupportedOperationException("Common Plugin is not initialized");
        }
        return instance;
    }

    public void handleEvents(ClientEvent clientEvent)
    {
        if (ClientEvent.Type.REGISTRY.equals(clientEvent.type) && ((RegistryEvent) clientEvent).getRegistryType().equals(RegistryEvent.RegistryType.OPTIONS))
        {
            this.properties = new Properties();
        }
    }

    public static void registerEvents(IClientAPI api)
    {
        api.subscribe(MOD_ID, EnumSet.of(ClientEvent.Type.REGISTRY));
    }

    public void setPlayerColor(WrappedEntity entity)
    {
        entity.setColor(handler.getRemotePlayerColor((Player) entity.getEntityLivingRef().get()));
    }

}
