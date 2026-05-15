package net.mysticdrew.journeymapteams.integration.betterteams;

import java.util.Objects;
import java.util.UUID;

/**
 * Wire-stable pairing of a player UUID to its current team id.
 */
public record MemberRecord(UUID playerUuid, String teamId)
{
    public MemberRecord
    {
        Objects.requireNonNull(playerUuid, "playerUuid");
        Objects.requireNonNull(teamId, "teamId");
    }
}
