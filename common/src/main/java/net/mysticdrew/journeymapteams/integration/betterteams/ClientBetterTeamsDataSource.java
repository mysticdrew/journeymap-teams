package net.mysticdrew.journeymapteams.integration.betterteams;

import java.util.Optional;
import java.util.UUID;

/**
 * Reads team data from a {@link BetterTeamsCache} populated by inbound packets.
 */
public final class ClientBetterTeamsDataSource implements BetterTeamsDataSource
{
    private final BetterTeamsCache cache;

    public ClientBetterTeamsDataSource(BetterTeamsCache cache)
    {
        this.cache = cache;
    }

    @Override
    public Optional<TeamRecord> teamFor(UUID playerUuid)
    {
        return cache.teamFor(playerUuid);
    }

    @Override
    public Optional<TeamRecord> teamById(String teamId)
    {
        return cache.teamById(teamId);
    }

    @Override
    public boolean isAvailable()
    {
        return BetterTeamsCache.isActive();
    }
}
