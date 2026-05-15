package net.mysticdrew.journeymapteams.integration.betterteams;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Wraps the BetterTeams server-side API as a {@link BetterTeamsDataSource}.
 *
 * <p>All BT calls are wrapped in try/catch so that an API change in a future
 * BT version degrades gracefully to "no team data" rather than crashing.</p>
 */
public final class ServerBetterTeamsDataSource implements BetterTeamsDataSource
{
    @Override
    public Optional<TeamRecord> teamFor(UUID playerUuid)
    {
        Team team = safeGetTeam(playerUuid);
        return team == null ? Optional.empty() : Optional.of(toRecord(team));
    }

    @Override
    public Optional<TeamRecord> teamById(String teamId)
    {
        try
        {
            UUID id = UUID.fromString(teamId);
            Team team = Team.getTeamManager().getTeam(id);
            return team == null ? Optional.empty() : Optional.of(toRecord(team));
        }
        catch (IllegalArgumentException ex)
        {
            return Optional.empty();
        }
        catch (Throwable t)
        {
            return Optional.empty();
        }
    }

    @Override
    public boolean isAvailable()
    {
        return true;
    }

    private static Team safeGetTeam(UUID playerUuid)
    {
        try
        {
            return Team.getTeam(playerUuid);
        }
        catch (Throwable t)
        {
            return null;
        }
    }

    /**
     * Package-private so BetterTeamsBukkitListener and BetterTeamsServerPlugin can build records too.
     *
     * <p>Verified BT 5.1.2 API:
     * <ul>
     *   <li>{@code team.getID()} - UUID</li>
     *   <li>{@code team.getName()} - String</li>
     *   <li>{@code team.getColor()} - org.bukkit.ChatColor</li>
     *   <li>{@code team.getAllies()} - AllySetComponent extends UuidSetComponent; {@code getClone()} returns Set<UUID></li>
     *   <li>{@code team.getMembers()} - MemberSetComponent extends TeamPlayerSetComponent; {@code getClone()} returns Set<TeamPlayer></li>
     *   <li>{@code TeamPlayer.getPlayerUUID()} - UUID</li>
     * </ul>
     * </p>
     */
    static TeamRecord toRecord(Team team)
    {
        String teamId = team.getID().toString();
        String name = team.getName();
        int color = packColor(team);
        List<String> allies = buildAllyList(team);
        return new TeamRecord(teamId, name, color, allies);
    }

    /**
     * Enumerates all members of a team. Returns an empty list on any failure.
     */
    static List<MemberRecord> toMemberRecords(Team team)
    {
        List<MemberRecord> out = new ArrayList<>();
        try
        {
            String teamId = team.getID().toString();
            for (TeamPlayer tp : team.getMembers().getClone())
            {
                out.add(new MemberRecord(tp.getPlayerUUID(), teamId));
            }
        }
        catch (Throwable t)
        {
            // Non-fatal: return what we have so far.
        }
        return out;
    }

    /**
     * BT 5.1.2: {@code team.getColor()} returns {@code org.bukkit.ChatColor}.
     * ChatColor.asBungee() gives a net.md_5.bungee.api.ChatColor which has getColor()
     * returning java.awt.Color.
     */
    private static int packColor(Team team)
    {
        try
        {
            return team.getColor().asBungee().getColor().getRGB() & 0xFFFFFF;
        }
        catch (Throwable t)
        {
            return 0xFFFFFF;
        }
    }

    /**
     * BT 5.1.2: {@code team.getAllies()} returns AllySetComponent whose {@code getClone()}
     * returns {@code Set<UUID>} - the UUIDs of allied *teams*.
     */
    private static List<String> buildAllyList(Team team)
    {
        try
        {
            List<String> allies = new ArrayList<>();
            for (UUID allyId : team.getAllies().getClone())
            {
                allies.add(allyId.toString());
            }
            return allies;
        }
        catch (Throwable t)
        {
            return List.of();
        }
    }
}
