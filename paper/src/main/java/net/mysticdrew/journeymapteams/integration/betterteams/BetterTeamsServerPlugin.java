package net.mysticdrew.journeymapteams.integration.betterteams;

import com.booksaw.betterTeams.Team;
import commonnetwork.api.Network;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ServerEventRegistry;
import journeymap.api.v2.server.IServerAPI;
import journeymap.api.v2.server.IServerPlugin;
import journeymap.api.v2.server.event.PlayerRadarUpdateEvent;
import net.minecraft.server.level.ServerPlayer;
import net.mysticdrew.journeymapteams.Constants;
import net.mysticdrew.journeymapteams.handlers.properties.DefaultServerProperties;
import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsDeltaPacket;
import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsHandshakePacket;
import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsSnapshotPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Server-side JourneyMap plugin for BetterTeams integration.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Register the three BetterTeams network packets server-side.</li>
 *   <li>Wire {@link BetterTeamsHandshakePacket.ServerHook} so handshakes from clients
 *       trigger a full snapshot send and session tracking.</li>
 *   <li>Subscribe to JourneyMap's PlayerRadarUpdateEvent to filter teammate visibility.</li>
 *   <li>Register {@link BetterTeamsBukkitListener} to forward BT events as delta packets.</li>
 * </ul>
 * </p>
 *
 * <p>The {@code dependencies} value "BetterTeams" matches the Bukkit plugin name
 * declared in paper-plugin.yml. If BetterTeams is absent the defensive guard in
 * {@link #initialize} short-circuits before any BT API is touched.</p>
 */
@JourneyMapPlugin(apiVersion = "2.0.0", dependencies = {"BetterTeams"})
public class BetterTeamsServerPlugin implements IServerPlugin
{
    private final BetterTeamsSession session = new BetterTeamsSession();
    private final ServerBetterTeamsDataSource dataSource = new ServerBetterTeamsDataSource();
    private BetterTeamsHandler handler;

    @Override
    public void initialize(IServerAPI jmServerApi)
    {
        // Defensive guard: if BetterTeams is not loaded, do nothing.
        if (Bukkit.getPluginManager().getPlugin("BetterTeams") == null)
        {
            return;
        }

        // Register the three BetterTeams packets server-side.
        // The client plugin registers these on the client; both sides must register
        // before any packet can be exchanged.
        Network.registerPacket(
                BetterTeamsHandshakePacket.type(),
                BetterTeamsHandshakePacket.class,
                BetterTeamsHandshakePacket.STREAM_CODEC,
                BetterTeamsHandshakePacket::handle)
            .registerPacket(
                BetterTeamsSnapshotPacket.type(),
                BetterTeamsSnapshotPacket.class,
                BetterTeamsSnapshotPacket.STREAM_CODEC,
                BetterTeamsSnapshotPacket::handle)
            .registerPacket(
                BetterTeamsDeltaPacket.staticType(),
                BetterTeamsDeltaPacket.class,
                BetterTeamsDeltaPacket.STREAM_CODEC,
                BetterTeamsDeltaPacket::handle);

        // Wire the handshake hook so that when a client sends the handshake packet
        // the common-module packet handler delegates back to us here.
        BetterTeamsHandshakePacket.ServerHook.register(this::onHandshakeReceived);

        ServerEventRegistry.OPTIONS_REGISTRY_EVENT.subscribe(Constants.MOD_ID, event ->
        {
            DefaultServerProperties serverProperties =
                    new DefaultServerProperties("betterteams", "prop.category.label.betterteams.server");
            this.handler = new BetterTeamsHandler(null, serverProperties, () -> null, dataSource);
        });

        ServerEventRegistry.PLAYER_RADAR_UPDATE_EVENT.subscribe(Constants.MOD_ID, this::onPlayerRadarUpdate);

        // Register the Bukkit listener that forwards BT team/member events as delta packets.
        JavaPlugin host = JavaPlugin.getProvidingPlugin(net.mysticdrew.journeymapteams.JourneyMapTeamsPaper.class);
        if (host != null)
        {
            Bukkit.getPluginManager().registerEvents(new BetterTeamsBukkitListener(session), host);
        }
    }

    private void onPlayerRadarUpdate(PlayerRadarUpdateEvent event)
    {
        if (handler == null || event.getAction() != PlayerRadarUpdateEvent.Action.UPDATE)
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

    /**
     * Called when a client completes the BetterTeams handshake. We track the player
     * in the session and immediately send a full snapshot of all current teams.
     */
    private void onHandshakeReceived(UUID playerUuid, String addonVersion)
    {
        session.add(playerUuid);
        Player bukkitPlayer = Bukkit.getPlayer(playerUuid);
        if (bukkitPlayer == null)
        {
            return;
        }
        ServerPlayer mc = unwrap(bukkitPlayer);
        if (mc == null)
        {
            return;
        }
        Network.getNetworkHandler().sendToClient(buildSnapshot(), mc);
    }

    /**
     * Builds a full snapshot of all teams currently known to BetterTeams.
     *
     * <p>BT 5.1.2 API: {@code Team.getTeamManager().getLoadedTeamListClone()} returns
     * {@code Map<UUID, Team>} of all teams currently loaded in memory. This is the
     * authoritative accessor; no static "getLoadedTeams()" exists on Team directly.</p>
     */
    private BetterTeamsSnapshotPacket buildSnapshot()
    {
        List<TeamRecord> teams = new ArrayList<>();
        List<MemberRecord> members = new ArrayList<>();
        try
        {
            for (Team team : Team.getTeamManager().getLoadedTeamListClone().values())
            {
                TeamRecord record = ServerBetterTeamsDataSource.toRecord(team);
                teams.add(record);
                members.addAll(ServerBetterTeamsDataSource.toMemberRecords(team));
            }
        }
        catch (Throwable t)
        {
            // Snapshot failure is non-fatal: the client renders without team coloring
            // until deltas arrive and fill in the picture.
        }
        return new BetterTeamsSnapshotPacket(teams, members);
    }

    /**
     * Unwraps a Bukkit Player to its NMS ServerPlayer via CraftEntity.getHandle().
     */
    private static ServerPlayer unwrap(Player p)
    {
        try
        {
            java.lang.reflect.Method getHandle = p.getClass().getMethod("getHandle");
            return (ServerPlayer) getHandle.invoke(p);
        }
        catch (Throwable t)
        {
            return null;
        }
    }

    @Override
    public String getModId()
    {
        return Constants.MOD_ID + "_betterteams_server";
    }
}
