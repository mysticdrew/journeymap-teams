package net.mysticdrew.journeymapteams.integration.ftb;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamRank;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.AbstractHandler;
import net.mysticdrew.journeymapteams.handlers.LocalPlayerSupplier;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;

public class FTBTeamsHandler extends AbstractHandler
{
    public FTBTeamsHandler(Properties properties, LocalPlayerSupplier localPlayerSupplier)
    {
        super(properties, localPlayerSupplier);
    }

    @Override
    public boolean isVisible(Player receiver, Player remote, boolean isOp, boolean visible)
    {
        var localTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(receiver.getUUID());
        var remoteTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(remote.getUUID());
        if (localTeam.isPresent() && remoteTeam.isPresent())
        {
            var allied = localTeam.get().getRankForPlayer(remote.getUUID()).isAtLeast(TeamRank.ALLY)
                    || remoteTeam.get().getRankForPlayer(receiver.getUUID()).isAtLeast(TeamRank.ALLY);

            var inPlayerTeam = remoteTeam.get().isPlayerTeam() && !remoteTeam.get().isPartyTeam();
            var sameTeam = remoteTeam.get().getTeamId().equals(localTeam.get().getTeamId());
            if (sameTeam || allied || isOp || inPlayerTeam)
            {
                return visible;
            }
            return false;
        }
        else if (localTeam.isEmpty() && remoteTeam.isPresent() && !isOp)
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
