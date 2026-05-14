package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;

public class VanillaTeamsHandler extends AbstractHandler
{
    public VanillaTeamsHandler(Properties properties, LocalPlayerSupplier localPlayerSupplier)
    {
        super(properties, localPlayerSupplier);
    }

    @Override
    public boolean isVisible(Player receiver, Player remote, boolean isOp, boolean visible)
    {
        var localTeam = receiver.getTeam();
        var remoteTeam = remote.getTeam();

        if (localTeam != null && remoteTeam != null)
        {
            var allied = localTeam.isAlliedTo(remoteTeam) || remoteTeam.isAlliedTo(localTeam);
            if (remoteTeam.getName().equals(localTeam.getName()) || allied || isOp)
            {
                return visible;
            }
            return false;
        }
        else if (localTeam == null && remoteTeam != null && !isOp)
        {
            return false;
        }
        return visible;
    }

    @Override
    protected int getRemotePlayerColor(Player remotePlayer)
    {
        var localPlayer = localPlayerSupplier.get();
        if (localPlayer == null)
        {
            return properties.getTeamColor();
        }
        var localTeam = localPlayer.getTeam();
        var remoteTeam = remotePlayer.getTeam();

        if (localTeam != null && remoteTeam != null)
        {
            var allied = localTeam.isAlliedTo(remoteTeam) || remoteTeam.isAlliedTo(localTeam);
            Integer teamColorValue = remoteTeam.getColor().getColor();
            int color = teamColorValue != null ? teamColorValue : properties.getTeamColor();
            return getColor(localTeam == remoteTeam, allied, color);
        }
        return properties.getTeamColor();
    }
}
