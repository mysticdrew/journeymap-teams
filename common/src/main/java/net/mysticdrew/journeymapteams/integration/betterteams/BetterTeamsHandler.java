package net.mysticdrew.journeymapteams.integration.betterteams;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.AbstractHandler;
import net.mysticdrew.journeymapteams.handlers.LocalPlayerSupplier;
import net.mysticdrew.journeymapteams.handlers.VisibilityRelationship;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;

import java.util.Optional;

/**
 * BetterTeams handler. The visibility-classification half (server side) and
 * the coloring half (client side) share this class; differences live in the
 * {@link BetterTeamsDataSource} they wrap.
 */
public final class BetterTeamsHandler extends AbstractHandler
{
    private final BetterTeamsDataSource dataSource;

    public BetterTeamsHandler(Properties properties, ServerProperties serverProperties,
                              LocalPlayerSupplier localPlayerSupplier, BetterTeamsDataSource dataSource)
    {
        super(properties, serverProperties, localPlayerSupplier);
        this.dataSource = dataSource;
    }

    @Override
    public boolean isVisible(Player receiver, Player remote, boolean isOp, boolean visible)
    {
        return applyVisibilityPolicy(relationship(receiver, remote), isOp, visible);
    }

    VisibilityRelationship relationship(Player receiver, Player remote)
    {
        Optional<TeamRecord> remoteTeam = dataSource.teamFor(remote.getUUID());
        if (remoteTeam.isEmpty())
        {
            return VisibilityRelationship.REMOTE_UNTEAMED;
        }
        Optional<TeamRecord> receiverTeam = dataSource.teamFor(receiver.getUUID());
        if (receiverTeam.isEmpty())
        {
            return VisibilityRelationship.VIEWER_UNTEAMED_REMOTE_TEAMED;
        }
        if (remoteTeam.get().teamId().equals(receiverTeam.get().teamId()))
        {
            return VisibilityRelationship.SAME_TEAM;
        }
        boolean allied = receiverTeam.get().allies().contains(remoteTeam.get().teamId())
                || remoteTeam.get().allies().contains(receiverTeam.get().teamId());
        return allied ? VisibilityRelationship.ALLIED : VisibilityRelationship.OTHER_TEAM;
    }

    @Override
    protected int getRemotePlayerColor(Player remotePlayer)
    {
        Player localPlayer = localPlayerSupplier == null ? null : localPlayerSupplier.get();
        Optional<TeamRecord> remoteTeam = dataSource.teamFor(remotePlayer.getUUID());
        if (remoteTeam.isEmpty())
        {
            return properties.getTeamColor();
        }
        Optional<TeamRecord> localTeam = localPlayer == null
                ? Optional.empty()
                : dataSource.teamFor(localPlayer.getUUID());
        boolean teammates = localTeam.isPresent() && localTeam.get().teamId().equals(remoteTeam.get().teamId());
        boolean allied = !teammates
                && localTeam.isPresent()
                && (localTeam.get().allies().contains(remoteTeam.get().teamId())
                        || remoteTeam.get().allies().contains(localTeam.get().teamId()));
        return getColor(teammates, allied, remoteTeam.get().color());
    }
}
