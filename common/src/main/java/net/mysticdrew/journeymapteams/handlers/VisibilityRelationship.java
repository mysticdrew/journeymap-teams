package net.mysticdrew.journeymapteams.handlers;

/**
 * Classifies a remote player relative to the radar receiver, for the
 * server-side visibility policy in {@link AbstractHandler#applyVisibilityPolicy}.
 * Every remote player falls into exactly one of these buckets.
 */
public enum VisibilityRelationship
{
    /** Remote player has no team. */
    REMOTE_UNTEAMED,
    /** Receiver has no team, remote player does. */
    VIEWER_UNTEAMED_REMOTE_TEAMED,
    /** Receiver and remote are on the same team. */
    SAME_TEAM,
    /** Receiver and remote are on allied teams. */
    ALLIED,
    /** Both teamed, on unrelated teams. */
    OTHER_TEAM
}
