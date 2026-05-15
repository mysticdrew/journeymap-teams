package net.mysticdrew.journeymapteams.handlers.properties;

/**
 * Server-side admin options governing radar visibility. Implemented by
 * {@link DefaultServerProperties}, which is backed by the JourneyMap
 * server-options API. A {@code null} ServerProperties means "no server-side
 * policy" - i.e. a client-side handler instance.
 */
public interface ServerProperties
{
    boolean getEnforceTeamVisibility();

    boolean getHideUnteamed();

    boolean getHideAllies();

    boolean getHideOtherTeams();

    boolean getHideTeamedFromUnteamed();

    boolean getOpsBypassHiding();
}
