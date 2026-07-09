package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;

public class VanillaTeamsHandler extends AbstractHandler
{
    public VanillaTeamsHandler(Properties properties, ServerProperties serverProperties,
                               LocalPlayerSupplier localPlayerSupplier)
    {
        super(properties, serverProperties, localPlayerSupplier);
    }

    @Override
    public boolean isVisible(Player receiver, Player remote, boolean isOp, boolean visible)
    {
        return applyVisibilityPolicy(relationship(receiver, remote), isOp, visible);
    }

    private VisibilityRelationship relationship(Player receiver, Player remote)
    {
        var localTeam = receiver.getTeam();
        var remoteTeam = remote.getTeam();

        if (remoteTeam == null)
        {
            return VisibilityRelationship.REMOTE_UNTEAMED;
        }
        if (localTeam == null)
        {
            return VisibilityRelationship.VIEWER_UNTEAMED_REMOTE_TEAMED;
        }
        if (remoteTeam.getName().equals(localTeam.getName()))
        {
            return VisibilityRelationship.SAME_TEAM;
        }
        if (localTeam.isAlliedTo(remoteTeam) || remoteTeam.isAlliedTo(localTeam))
        {
            return VisibilityRelationship.ALLIED;
        }
        return VisibilityRelationship.OTHER_TEAM;
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
            Integer teamColorValue = remoteTeam.getColor().get().rgb();
            int color = teamColorValue != null ? teamColorValue : properties.getTeamColor();
            return getColor(localTeam == remoteTeam, allied, color);
        }
        return properties.getTeamColor();
    }
}
