package net.mysticdrew.journeymapteams.integration.betterteams;

import java.util.List;
import java.util.Objects;

/**
 * Wire-stable representation of a BetterTeams team relevant to map display.
 * BT-internal state (balance, levels, ranks, tags) is intentionally omitted.
 */
public record TeamRecord(String teamId, String name, int color, List<String> allies)
{
    public TeamRecord
    {
        Objects.requireNonNull(teamId, "teamId");
        Objects.requireNonNull(name, "name");
        allies = List.copyOf(allies == null ? List.of() : allies);
    }
}
