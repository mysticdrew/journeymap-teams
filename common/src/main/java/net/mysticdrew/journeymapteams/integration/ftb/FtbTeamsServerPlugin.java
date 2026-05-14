package net.mysticdrew.journeymapteams.integration.ftb;

import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ServerEventRegistry;
import journeymap.api.v2.server.IServerAPI;
import journeymap.api.v2.server.IServerPlugin;
import journeymap.api.v2.server.event.PlayerRadarUpdateEvent;
import net.minecraft.server.level.ServerPlayer;
import net.mysticdrew.journeymapteams.Constants;

@JourneyMapPlugin(apiVersion = "2.0.0", dependencies = {"ftbteams"})
public class FtbTeamsServerPlugin implements IServerPlugin
{
    private final FTBTeamsHandler handler = new FTBTeamsHandler(null, () -> null);

    @Override
    public void initialize(IServerAPI jmServerApi)
    {
        ServerEventRegistry.PLAYER_RADAR_UPDATE_EVENT.subscribe(Constants.MOD_ID, this::onPlayerRadarUpdate);
    }

    private void onPlayerRadarUpdate(PlayerRadarUpdateEvent event)
    {
        if (event.getAction() != PlayerRadarUpdateEvent.Action.UPDATE)
        {
            return;
        }
        ServerPlayer remote = event.getRemote();
        if (remote == null)
        {
            return;
        }
        event.setVisible(handler.isVisible(event.getReceiver(), remote,
                event.isReceiverOp(), event.isVisible()));
    }

    @Override
    public String getModId()
    {
        return Constants.MOD_ID + "_ftb_teams_server";
    }
}
