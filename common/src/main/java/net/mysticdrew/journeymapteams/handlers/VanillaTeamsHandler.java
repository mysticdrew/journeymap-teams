package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.DefaultHandlerProperties;

public class VanillaTeamsHandler extends AbstractHandler
{
    public VanillaTeamsHandler()
    {
        super("vanilla", "prop.category.label.vanilla.tooltip");
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

    protected int getRemotePlayerColor(Player remotePlayer)
    {
        var localPlayer = Minecraft.getInstance().player;
        var localTeam = localPlayer.getTeam();
        var remoteTeam = remotePlayer.getTeam();

        if (localTeam != null && remoteTeam != null)
        {
            var allied = localTeam.isAlliedTo(remoteTeam) || remoteTeam.isAlliedTo(localTeam);
            var color = remoteTeam.getColor().getColor() != null ? remoteTeam.getColor().getColor() : properties.getTeamColor();
            return getColor(remoteTeam == localTeam, allied, color);

        }
        return properties.getTeamColor();
    }

}
