package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.VanillaHandlerProperties;

public class VanillaTeamsHandler implements Handler
{
    private VanillaHandlerProperties properties;

    public VanillaTeamsHandler()
    {
        properties = new VanillaHandlerProperties();
    }

    @Override
    public boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible)
    {
        var localTeam = localPlayer.getTeam();
        var remoteTeam = remotePlayer.getTeam();

        if (localTeam != null && remoteTeam != null)
        {
            var allied = localTeam.isAlliedTo(remoteTeam) || remoteTeam.isAlliedTo(localTeam);

            if ((remoteTeam == localTeam || allied) || isOp)
            {
                return visible;
            }
            return false;
        }
        return visible;
    }

    @Override
    public int getRemotePlayerColor(Player remotePlayer)
    {
        var localPlayer = Minecraft.getInstance().player;
        var localTeam = localPlayer.getTeam();
        var remoteTeam = remotePlayer.getTeam();

        if (localTeam != null && remoteTeam != null)
        {
            var allied = localTeam.isAlliedTo(remoteTeam) || remoteTeam.isAlliedTo(localTeam);

            if ((remoteTeam == localTeam || allied))
            {
                var color = remoteTeam.getColor().getColor() != null ? remoteTeam.getColor().getColor() : properties.defaultTeamColor.get().getColor();
                return allied && properties.doOverrideAllyColor.get() ? properties.overrideAllyColor.get().getColor() : color;
            }
        }
        return properties.defaultTeamColor.get().getColor();
    }

}
