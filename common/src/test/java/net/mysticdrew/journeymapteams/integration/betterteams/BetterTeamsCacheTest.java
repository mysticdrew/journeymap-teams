package net.mysticdrew.journeymapteams.integration.betterteams;

import net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsDeltaPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BetterTeamsCacheTest
{
    private BetterTeamsCache cache;

    private static final String TEAM_RED = "red-uuid";
    private static final String TEAM_BLUE = "blue-uuid";
    private static final UUID ALICE = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID BOB = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUp()
    {
        cache = new BetterTeamsCache();
    }

    @Test
    void initially_inactive_empty()
    {
        assertFalse(cache.isActiveInstance());
        assertTrue(cache.teamFor(ALICE).isEmpty());
    }

    @Test
    void applySnapshot_setsActiveAndPopulates()
    {
        TeamRecord red = new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of());
        cache.applySnapshot(List.of(red), List.of(new MemberRecord(ALICE, TEAM_RED)));

        assertTrue(cache.isActiveInstance());
        assertEquals(Optional.of(red), cache.teamFor(ALICE));
    }

    @Test
    void applySnapshot_replacesPriorState()
    {
        cache.applySnapshot(List.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())),
                List.of(new MemberRecord(ALICE, TEAM_RED)));
        cache.applySnapshot(List.of(new TeamRecord(TEAM_BLUE, "Blue", 0x0000FF, List.of())),
                List.of(new MemberRecord(BOB, TEAM_BLUE)));

        assertTrue(cache.teamFor(ALICE).isEmpty());
        assertEquals(TEAM_BLUE, cache.teamFor(BOB).get().teamId());
    }

    @Test
    void applyDelta_teamUpsert_addsTeam()
    {
        cache.applySnapshot(List.of(), List.of());
        TeamRecord red = new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of());

        cache.applyDelta(BetterTeamsDeltaPacket.teamUpsert(red));

        assertEquals(Optional.of(red), cache.teamById(TEAM_RED));
    }

    @Test
    void applyDelta_teamUpsert_overwritesExisting()
    {
        cache.applySnapshot(List.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())), List.of());
        TeamRecord updated = new TeamRecord(TEAM_RED, "Red Renamed", 0x880000, List.of());

        cache.applyDelta(BetterTeamsDeltaPacket.teamUpsert(updated));

        assertEquals("Red Renamed", cache.teamById(TEAM_RED).get().name());
        assertEquals(0x880000, cache.teamById(TEAM_RED).get().color());
    }

    @Test
    void applyDelta_teamRemove_dropsTeamAndOrphanedMembers()
    {
        cache.applySnapshot(List.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())),
                List.of(new MemberRecord(ALICE, TEAM_RED)));

        cache.applyDelta(BetterTeamsDeltaPacket.teamRemove(TEAM_RED));

        assertTrue(cache.teamById(TEAM_RED).isEmpty());
        assertTrue(cache.teamFor(ALICE).isEmpty());
    }

    @Test
    void applyDelta_memberUpsert_setsMembership()
    {
        cache.applySnapshot(List.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())), List.of());

        cache.applyDelta(BetterTeamsDeltaPacket.memberUpsert(new MemberRecord(ALICE, TEAM_RED)));

        assertEquals(TEAM_RED, cache.teamFor(ALICE).get().teamId());
    }

    @Test
    void applyDelta_memberRemove_clearsMembership()
    {
        cache.applySnapshot(List.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())),
                List.of(new MemberRecord(ALICE, TEAM_RED)));

        cache.applyDelta(BetterTeamsDeltaPacket.memberRemove(ALICE));

        assertTrue(cache.teamFor(ALICE).isEmpty());
    }

    @Test
    void applyDelta_memberUpsert_referencingUnknownTeam_isDropped()
    {
        cache.applySnapshot(List.of(), List.of());

        cache.applyDelta(BetterTeamsDeltaPacket.memberUpsert(new MemberRecord(ALICE, TEAM_RED)));

        assertTrue(cache.teamFor(ALICE).isEmpty());
    }

    @Test
    void clear_resetsActiveAndState()
    {
        cache.applySnapshot(List.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())),
                List.of(new MemberRecord(ALICE, TEAM_RED)));

        cache.clear();

        assertFalse(cache.isActiveInstance());
        assertTrue(cache.teamFor(ALICE).isEmpty());
    }
}
