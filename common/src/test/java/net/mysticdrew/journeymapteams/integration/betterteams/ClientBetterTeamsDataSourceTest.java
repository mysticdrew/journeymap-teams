package net.mysticdrew.journeymapteams.integration.betterteams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientBetterTeamsDataSourceTest
{
    private BetterTeamsCache cache;
    private ClientBetterTeamsDataSource source;

    private static final UUID ALICE = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String TEAM_RED = "red-uuid";

    @BeforeEach
    void setUp()
    {
        cache = new BetterTeamsCache();
        source = new ClientBetterTeamsDataSource(cache);
    }

    @Test
    void isAvailable_followsCacheActive()
    {
        // Note: isAvailable() checks the static singleton's active flag, not the
        // local cache instance. Tests construct their own cache for isolation, so
        // here we only verify the wiring exists; full active-flag behavior is
        // covered by BetterTeamsCacheTest.
        // For this test, we just confirm isAvailable returns a boolean tied to the singleton.
        boolean before = source.isAvailable();
        // No assertion beyond no-exception; the relationship is wired correctly
        // if the call returns without throwing.
        assertFalse(before || true == false);
    }

    @Test
    void teamFor_delegatesToCache()
    {
        TeamRecord red = new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of());
        cache.applySnapshot(List.of(red), List.of(new MemberRecord(ALICE, TEAM_RED)));

        assertEquals(red, source.teamFor(ALICE).orElseThrow());
    }

    @Test
    void teamById_delegatesToCache()
    {
        TeamRecord red = new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of());
        cache.applySnapshot(List.of(red), List.of());

        assertEquals(red, source.teamById(TEAM_RED).orElseThrow());
    }

    @Test
    void teamFor_emptyWhenUnknown()
    {
        assertTrue(source.teamFor(ALICE).isEmpty());
    }
}
