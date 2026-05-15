package net.mysticdrew.journeymapteams.integration.betterteams;

import java.util.Optional;
import java.util.UUID;

/**
 * Strategy for retrieving team data. Server side wraps the BetterTeams API
 * directly; client side reads from {@link BetterTeamsCache}.
 */
public interface BetterTeamsDataSource
{
    /** @return team the player currently belongs to, or empty if unteamed/unknown. */
    Optional<TeamRecord> teamFor(UUID playerUuid);

    /** @return team by its BT team id, or empty if not present. */
    Optional<TeamRecord> teamById(String teamId);

    /** @return true when this data source has live data (post-handshake on client, always true on server when BT is loaded). */
    boolean isAvailable();
}
