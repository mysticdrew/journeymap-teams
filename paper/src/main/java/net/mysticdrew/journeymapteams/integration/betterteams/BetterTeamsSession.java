package net.mysticdrew.journeymapteams.integration.betterteams;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which connected players have completed the BetterTeams handshake.
 * Server-bound deltas iterate this set to find recipients.
 */
public final class BetterTeamsSession
{
    private final Set<UUID> handshookPlayers = ConcurrentHashMap.newKeySet();

    public void add(UUID playerUuid)
    {
        handshookPlayers.add(playerUuid);
    }

    public void remove(UUID playerUuid)
    {
        handshookPlayers.remove(playerUuid);
    }

    public Set<UUID> snapshot()
    {
        return Set.copyOf(handshookPlayers);
    }

    public boolean isHandshook(UUID playerUuid)
    {
        return handshookPlayers.contains(playerUuid);
    }
}
