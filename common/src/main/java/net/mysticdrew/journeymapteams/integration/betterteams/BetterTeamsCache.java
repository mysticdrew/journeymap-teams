package net.mysticdrew.journeymapteams.integration.betterteams;

import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsDeltaPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton-style state holder. Populated by inbound BetterTeams snapshot/delta
 * packets on the client. The {@code active} flag flips on first snapshot receipt
 * and clears on disconnect.
 *
 * Construction is package-private so tests can build isolated instances; the
 * production singleton is exposed via {@link #get()}. The {@link #isActive()}
 * static helper queries the singleton.
 */
public final class BetterTeamsCache
{
    private static final BetterTeamsCache INSTANCE = new BetterTeamsCache();

    public static BetterTeamsCache get()
    {
        return INSTANCE;
    }

    public static boolean isActive()
    {
        return INSTANCE.active;
    }

    private final Map<String, TeamRecord> teams = new HashMap<>();
    private final Map<UUID, String> members = new HashMap<>();
    private volatile boolean active;

    BetterTeamsCache()
    {
        // package-private for testing
    }

    public synchronized void applySnapshot(List<TeamRecord> incomingTeams, List<MemberRecord> incomingMembers)
    {
        teams.clear();
        members.clear();
        for (TeamRecord t : incomingTeams)
        {
            teams.put(t.teamId(), t);
        }
        for (MemberRecord m : incomingMembers)
        {
            if (teams.containsKey(m.teamId()))
            {
                members.put(m.playerUuid(), m.teamId());
            }
        }
        active = true;
    }

    public synchronized void applyDelta(BetterTeamsDeltaPacket delta)
    {
        switch (delta.kind())
        {
            case TEAM_UPSERT -> delta.team().ifPresent(t -> teams.put(t.teamId(), t));
            case TEAM_REMOVE -> delta.removedTeam().ifPresent(id ->
            {
                teams.remove(id);
                members.values().removeIf(memberTeamId -> memberTeamId.equals(id));
            });
            case MEMBER_UPSERT -> delta.member().ifPresent(m ->
            {
                if (teams.containsKey(m.teamId()))
                {
                    members.put(m.playerUuid(), m.teamId());
                }
            });
            case MEMBER_REMOVE -> delta.removedUuid().ifPresent(members::remove);
        }
    }

    public synchronized void clear()
    {
        teams.clear();
        members.clear();
        active = false;
    }

    public synchronized Optional<TeamRecord> teamById(String teamId)
    {
        return Optional.ofNullable(teams.get(teamId));
    }

    public synchronized Optional<TeamRecord> teamFor(UUID playerUuid)
    {
        String teamId = members.get(playerUuid);
        return teamId == null ? Optional.empty() : Optional.ofNullable(teams.get(teamId));
    }

    /**
     * Test-only equivalent of the static {@link #isActive()} that observes this
     * specific instance rather than the singleton.
     */
    boolean isActiveInstance()
    {
        return active;
    }
}
