package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;

public abstract class AbstractHandler implements Handler
{
    protected final Properties properties;
    protected final ServerProperties serverProperties;
    protected final LocalPlayerSupplier localPlayerSupplier;

    protected AbstractHandler(Properties properties, ServerProperties serverProperties,
                              LocalPlayerSupplier localPlayerSupplier)
    {
        this.properties = properties;
        this.serverProperties = serverProperties;
        this.localPlayerSupplier = localPlayerSupplier;
    }

    @Override
    public int getRemotePlayerNameColor(Player remotePlayer, int currentColor)
    {
        if (this.properties != null && this.properties.getShowNameColor())
        {
            return getRemotePlayerColor(remotePlayer);
        }
        return currentColor;
    }

    @Override
    public int getRemotePlayerIconColor(Player remotePlayer, int currentColor)
    {
        if (this.properties != null && this.properties.getShowIconColor())
        {
            return getRemotePlayerColor(remotePlayer);
        }
        return currentColor;
    }

    public int getColor(boolean teammates, boolean allied, int teamColor)
    {
        if (teammates)
        {
            return properties.getForceTeamColor() ? properties.getTeamColor() : teamColor;
        }
        if (allied)
        {
            return properties.getForceAllyColor() ? properties.getAllyColor() : teamColor;
        }
        return teamColor;
    }

    /**
     * Server-side: apply the admin visibility options to a classified remote
     * player. A {@code null} {@link #serverProperties} or a disabled master
     * toggle means "do not change visibility": the incoming {@code visible}
     * value is returned unchanged.
     *
     * @param relationship how the remote player relates to the receiver.
     * @param isOp         whether the receiver is a server operator.
     * @param visible      the visibility JourneyMap proposes for this pair.
     * @return the visibility verdict after applying the server options.
     */
    protected boolean applyVisibilityPolicy(VisibilityRelationship relationship, boolean isOp, boolean visible)
    {
        if (serverProperties == null || !serverProperties.getEnforceTeamVisibility())
        {
            return visible;
        }
        if (serverProperties.getOpsBypassHiding() && isOp)
        {
            return visible;
        }
        return switch (relationship)
        {
            case REMOTE_UNTEAMED -> serverProperties.getHideUnteamed() ? false : visible;
            case VIEWER_UNTEAMED_REMOTE_TEAMED -> serverProperties.getHideTeamedFromUnteamed() ? false : visible;
            case SAME_TEAM -> visible;
            case ALLIED -> serverProperties.getHideAllies() ? false : visible;
            case OTHER_TEAM -> serverProperties.getHideOtherTeams() ? false : visible;
        };
    }

    /**
     * Client-side color resolution; uses {@link #localPlayerSupplier}.
     */
    protected abstract int getRemotePlayerColor(Player remotePlayer);
}
