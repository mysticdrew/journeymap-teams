package net.mysticdrew.journeymapteams.integration.betterteams;

import commonnetwork.api.Network;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ClientEventRegistry;
import net.minecraft.client.Minecraft;
import net.mysticdrew.journeymapteams.Constants;
import net.mysticdrew.journeymapteams.handlers.properties.DefaultHandlerProperties;
import net.mysticdrew.journeymapteams.integration.RadarColorApplier;
import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsDeltaPacket;
import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsHandshakePacket;
import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsSnapshotPacket;

/**
 * Client-side JourneyMap plugin for BetterTeams integration.
 *
 * <p>No {@code dependencies} on BetterTeams: BT does not ship a Minecraft mod jar,
 * so there is no mod-id to gate on. Data arrives purely via packets once the
 * server-side Paper plugin sends a snapshot.</p>
 */
@JourneyMapPlugin(apiVersion = "2.0.0")
public class BetterTeamsClientPlugin implements IClientPlugin
{
    private RadarColorApplier colorApplier;

    @Override
    public void initialize(IClientAPI jmClientApi)
    {
        // Register all three BetterTeams packets. No PacketBounds argument exists
        // in the Network.registerPacket signature - direction is inferred from
        // which side's handle() fires (server-side for handshake, client-side
        // for snapshot and delta).
        Network.registerPacket(
                BetterTeamsHandshakePacket.TYPE,
                BetterTeamsHandshakePacket.STREAM_CODEC,
                BetterTeamsHandshakePacket::handle)
            .registerPacket(
                BetterTeamsSnapshotPacket.TYPE,
                BetterTeamsSnapshotPacket.STREAM_CODEC,
                BetterTeamsSnapshotPacket::handle)
            .registerPacket(
                BetterTeamsDeltaPacket.TYPE,
                BetterTeamsDeltaPacket.STREAM_CODEC,
                BetterTeamsDeltaPacket::handle);

        DefaultHandlerProperties properties =
                new DefaultHandlerProperties("vanilla", "prop.category.label.vanilla");
        BetterTeamsHandler handler = new BetterTeamsHandler(
                properties,
                null,
                () -> Minecraft.getInstance().player,
                new ClientBetterTeamsDataSource(BetterTeamsCache.get()));
        this.colorApplier = new RadarColorApplier(handler, RadarColorApplier.NameMode.CUSTOM_NAME);

        ClientEventRegistry.ENTITY_RADAR_UPDATE_EVENT.subscribe(Constants.MOD_ID,
                event ->
                {
                    if (!BetterTeamsCache.isActive())
                    {
                        return;
                    }
                    colorApplier.onEntityRadarUpdate(event);
                });

        // Send the handshake to the server when mapping starts. MAPPING_EVENT fires
        // once JourneyMap has loaded the world and is actively mapping - the earliest
        // point where the custom channel is open and a reply snapshot is useful.
        ClientEventRegistry.MAPPING_EVENT.subscribe(Constants.MOD_ID,
                event ->
                {
                    if (event.getStage() == journeymap.api.v2.client.event.MappingEvent.Stage.MAPPING_STARTED)
                    {
                        Network.getNetworkHandler().sendToServer(
                                new BetterTeamsHandshakePacket(Constants.MOD_VERSION), true);
                    }
                });
    }

    @Override
    public String getModId()
    {
        return Constants.MOD_ID + "_betterteams_client";
    }
}
