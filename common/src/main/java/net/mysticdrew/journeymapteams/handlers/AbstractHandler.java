package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.DefaultHandlerProperties;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;

public abstract class AbstractHandler implements Handler
{
    protected final Properties properties;

    public AbstractHandler(String categoryToken, String categoryKey)
    {
        this.properties = new DefaultHandlerProperties(categoryToken, categoryKey);
    }

    public AbstractHandler(Properties properties)
    {
        this.properties = properties;
    }

    @Override
    public int getRemotePlayerNameColor(Player remotePlayer, int currentColor)
    {
        if (this.properties.getShowNameColor())
        {
            return getRemotePlayerColor(remotePlayer);
        }
        return currentColor;
    }

    @Override
    public int getRemotePlayerIconColor(Player remotePlayer, int currentColor)
    {
        if (this.properties.getShowIconColor())
        {
            return getRemotePlayerColor(remotePlayer);
        }
        return currentColor;
    }

    public int getColor(boolean teammates, boolean allied, int teamColor)
    {
        if (allied)
        {
            return properties.getForceAllyColor() ? properties.getAllyColor() : teamColor;
        }

        if (teammates)
        {
            return properties.getForceTeamColor() ? properties.getTeamColor() : teamColor;
        }
        return properties.getTeamColor();
    }

    protected abstract int getRemotePlayerColor(Player remotePlayer);
}
