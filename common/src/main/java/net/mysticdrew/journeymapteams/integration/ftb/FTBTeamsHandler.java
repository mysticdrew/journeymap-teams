package net.mysticdrew.journeymapteams.integration.ftb;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamRank;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.AbstractHandler;
import net.mysticdrew.journeymapteams.handlers.LocalPlayerSupplier;
import net.mysticdrew.journeymapteams.handlers.VisibilityRelationship;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;

import java.util.Optional;

public class FTBTeamsHandler extends AbstractHandler
{
    public FTBTeamsHandler(Properties properties, ServerProperties serverProperties,
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
        var localTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(receiver.getUUID());
        var remoteTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(remote.getUUID());

        if (isUnteamed(remoteTeam))
        {
            return VisibilityRelationship.REMOTE_UNTEAMED;
        }
        if (isUnteamed(localTeam))
        {
            return VisibilityRelationship.VIEWER_UNTEAMED_REMOTE_TEAMED;
        }
        if (remoteTeam.get().getTeamId().equals(localTeam.get().getTeamId()))
        {
            return VisibilityRelationship.SAME_TEAM;
        }
        boolean allied = localTeam.get().getRankForPlayer(remote.getUUID()).isAtLeast(TeamRank.ALLY)
                || remoteTeam.get().getRankForPlayer(receiver.getUUID()).isAtLeast(TeamRank.ALLY);
        return allied ? VisibilityRelationship.ALLIED : VisibilityRelationship.OTHER_TEAM;
    }

    /**
     * In FTB every logged-in player has a team, so "unteamed" means the player
     * is in their own auto-created player team rather than a party team.
     */
    private static boolean isUnteamed(Optional<Team> team)
    {
        return team.isEmpty() || (team.get().isPlayerTeam() && !team.get().isPartyTeam());
    }

    @Override
    protected int getRemotePlayerColor(Player remotePlayer)
    {
        var localPlayer = localPlayerSupplier.get();
        if (localPlayer == null)
        {
            return properties.getTeamColor();
        }
        if (!FTBTeamsAPI.api().isClientManagerLoaded())
        {
            return properties.getTeamColor();
        }
        var knownRemote = FTBTeamsAPI.api().getClientManager().getKnownPlayer(remotePlayer.getUUID());
        var knownLocal = FTBTeamsAPI.api().getClientManager().getKnownPlayer(localPlayer.getUUID());
        if (knownRemote.isPresent() && knownLocal.isPresent())
        {
            var remoteTeam = FTBTeamsAPI.api().getClientManager().getTeamByID(knownRemote.get().teamId());
            var localTeam = FTBTeamsAPI.api().getClientManager().getTeamByID(knownLocal.get().teamId());
            if (localTeam.isPresent() && remoteTeam.isPresent())
            {
                var allied = localTeam.get().getRankForPlayer(remotePlayer.getUUID()).isAtLeast(TeamRank.ALLY)
                        || remoteTeam.get().getRankForPlayer(localPlayer.getUUID()).isAtLeast(TeamRank.ALLY);
                var teammates = remoteTeam.get().getId().equals(localTeam.get().getId());
                return getColor(teammates, allied, remoteTeam.get().getProperty(TeamProperties.COLOR).rgb());
            }
        }
        return properties.getTeamColor();
    }
}
