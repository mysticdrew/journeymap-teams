package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;

public abstract class AbstractHandler implements Handler
{
    protected final Properties properties;
    protected final LocalPlayerSupplier localPlayerSupplier;

    protected AbstractHandler(Properties properties, LocalPlayerSupplier localPlayerSupplier)
    {
        this.properties = properties;
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
     * Client-side color resolution; uses {@link #localPlayerSupplier}.
     */
    protected abstract int getRemotePlayerColor(Player remotePlayer);
}
