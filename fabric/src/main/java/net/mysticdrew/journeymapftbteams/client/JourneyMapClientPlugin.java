package net.mysticdrew.journeymapftbteams.client;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.EntityRadarUpdateEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import net.minecraft.client.Minecraft;
import net.mysticdrew.journeymapftbteams.Constants;

@ClientPlugin
public class JourneyMapClientPlugin implements IClientPlugin
{
    @Override
    public void initialize(IClientAPI iClientAPI)
    {
        FabricEvents.ENTITY_RADAR_UPDATE_EVENT.register(this::onEntityUpdate);
    }

    private void onEntityUpdate(EntityRadarUpdateEvent event)
    {
        if (event.getType() == EntityRadarUpdateEvent.EntityType.PLAYER)
        {
            ClientTeam remotePlayer = FTBTeamsAPI.getClientManager().getTeam(event.getWrappedEntity().getEntityLivingRef().get().getUUID());
            ClientTeam localPlayer = FTBTeamsAPI.getClientManager().getTeam(Minecraft.getInstance().player.getUUID());

            if (remotePlayer != null
                    && localPlayer != null
                    && remotePlayer.manager.getManagerId() == localPlayer.manager.getManagerId())
            {
                event.getWrappedEntity().setColor(localPlayer.getColor());
            }
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

    }
}
