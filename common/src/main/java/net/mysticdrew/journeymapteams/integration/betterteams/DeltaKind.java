package net.mysticdrew.journeymapteams.integration.betterteams;

/**
 * Discriminator for {@link net.mysticdrew.journeymapteams.integration.betterteams.network.BetterTeamsDeltaPacket}.
 * Exactly one of the packet's optional payload fields is populated based on this value.
 */
public enum DeltaKind
{
    /** Create or update a team record (color/name/allies change). */
    TEAM_UPSERT,
    /** Disband a team; member entries pointing at it are dropped client-side. */
    TEAM_REMOVE,
    /** Player joined or moved to a team. */
    MEMBER_UPSERT,
    /** Player left their team (no team or remained unteamed). */
    MEMBER_REMOVE
}
