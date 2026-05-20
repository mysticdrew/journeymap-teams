package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.config.ServerConfig;

public class VanillaTeamsHandler extends AbstractHandler
{
    public VanillaTeamsHandler()
    {
        super("vanilla", "prop.category.label.vanilla.tooltip");
    }

    @Override
    public boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible)
    {
        // The receiver being op overrides team filtering (still respects JM's own visibility
        // flag), unless an admin disabled the bypass in the server config.
        if (isOp && ServerConfig.getInstance().opsBypassTeamVisibility())
        {
            return visible;
        }

        var localTeam = localPlayer.getTeam();
        var remoteTeam = remotePlayer.getTeam();

        // Neither side is on a vanilla team: nothing to filter on, defer to JM's decision.
        if (localTeam == null && remoteTeam == null)
        {
            return visible;
        }

        // Exactly one side is teamed: hide. Symmetric with the unteamed-local case
        // that already hid teamed remotes; the previous code leaked the reverse.
        if (localTeam == null || remoteTeam == null)
        {
            return false;
        }

        // Same team (by registered name; same Team instance also matches),
        // or a mod-defined alliance via either side.
        if (localTeam.getName().equals(remoteTeam.getName())
                || localTeam.isAlliedTo(remoteTeam)
                || remoteTeam.isAlliedTo(localTeam))
        {
            return visible;
        }

        return false;
    }

    @Override
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
