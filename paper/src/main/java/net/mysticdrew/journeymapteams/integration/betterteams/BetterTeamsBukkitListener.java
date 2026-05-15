package net.mysticdrew.journeymapteams.integration.betterteams;

import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.customEvents.CreateTeamEvent;
import com.booksaw.betterTeams.customEvents.DisbandTeamEvent;
import com.booksaw.betterTeams.customEvents.PlayerJoinTeamEvent;
import com.booksaw.betterTeams.customEvents.PlayerLeaveTeamEvent;
import com.booksaw.betterTeams.customEvents.RelationChangeTeamEvent;
import com.booksaw.betterTeams.customEvents.TeamColorChangeEvent;
import com.booksaw.betterTeams.customEvents.TeamNameChangeEvent;
import commonnetwork.api.Network;
import net.minecraft.server.level.ServerPlayer;
import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsDeltaPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Translates BetterTeams Bukkit events into {@link BetterTeamsDeltaPacket}s
 * pushed to all handshook clients.
 *
 * <p>MONITOR + ignoreCancelled = true: we observe the final, post-resolution
 * state without interfering with BT's own processing. PlayerQuitEvent uses
 * default priority since we just need to clean up session state.</p>
 *
 * <p>Verified BT 5.1.2 event API:
 * <ul>
 *   <li>All team events extend TeamEvent which exposes {@code getTeam()}</li>
 *   <li>TeamPlayerEvent (PlayerJoin/Leave) exposes {@code getTeamPlayer()} returning TeamPlayer</li>
 *   <li>TeamPlayer.getPlayerUUID() returns UUID</li>
 *   <li>RelationChangeTeamEvent exposes {@code getOtherTeam()} - both teams need upsert</li>
 *   <li>TeamColorChangeEvent exposes {@code getNewTeamColor()} but at MONITOR priority
 *       team.getColor() already reflects the new value</li>
 * </ul>
 * </p>
 */
public final class BetterTeamsBukkitListener implements Listener
{
    private final BetterTeamsSession session;

    public BetterTeamsBukkitListener(BetterTeamsSession session)
    {
        this.session = session;
    }

    // -----------------------------------------------------------------------
    // Team lifecycle
    // -----------------------------------------------------------------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeamCreate(CreateTeamEvent event)
    {
        broadcast(BetterTeamsDeltaPacket.teamUpsert(ServerBetterTeamsDataSource.toRecord(event.getTeam())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeamDisband(DisbandTeamEvent event)
    {
        broadcast(BetterTeamsDeltaPacket.teamRemove(event.getTeam().getID().toString()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeamColorChange(TeamColorChangeEvent event)
    {
        // At MONITOR priority the team color has already been updated on the team object.
        broadcast(BetterTeamsDeltaPacket.teamUpsert(ServerBetterTeamsDataSource.toRecord(event.getTeam())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeamNameChange(TeamNameChangeEvent event)
    {
        // At MONITOR priority the team name has already been updated on the team object.
        broadcast(BetterTeamsDeltaPacket.teamUpsert(ServerBetterTeamsDataSource.toRecord(event.getTeam())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRelationChange(RelationChangeTeamEvent event)
    {
        // Both teams' ally lists have changed; upsert both so clients stay consistent.
        broadcast(BetterTeamsDeltaPacket.teamUpsert(ServerBetterTeamsDataSource.toRecord(event.getTeam())));
        if (event.getOtherTeam() != null)
        {
            broadcast(BetterTeamsDeltaPacket.teamUpsert(ServerBetterTeamsDataSource.toRecord(event.getOtherTeam())));
        }
    }

    // -----------------------------------------------------------------------
    // Member lifecycle
    // -----------------------------------------------------------------------

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinTeam(PlayerJoinTeamEvent event)
    {
        TeamPlayer tp = event.getTeamPlayer();
        MemberRecord record = new MemberRecord(tp.getPlayerUUID(), event.getTeam().getID().toString());
        broadcast(BetterTeamsDeltaPacket.memberUpsert(record));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeaveTeam(PlayerLeaveTeamEvent event)
    {
        broadcast(BetterTeamsDeltaPacket.memberRemove(event.getTeamPlayer().getPlayerUUID()));
    }

    // -----------------------------------------------------------------------
    // Player disconnect
    // -----------------------------------------------------------------------

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        session.remove(event.getPlayer().getUniqueId());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void broadcast(BetterTeamsDeltaPacket packet)
    {
        for (UUID uuid : session.snapshot())
        {
            Player bukkitPlayer = Bukkit.getPlayer(uuid);
            if (bukkitPlayer == null)
            {
                continue;
            }
            ServerPlayer mc = unwrap(bukkitPlayer);
            if (mc == null)
            {
                continue;
            }
            Network.getNetworkHandler().sendToClient(packet, mc);
        }
    }

    /**
     * Unwraps a Bukkit Player to its NMS ServerPlayer via CraftEntity.getHandle().
     * Reflection avoids a hard compile-time dependency on CraftBukkit's internal
     * package which changes per MC version.
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
}
