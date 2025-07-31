package net.mysticdrew.journeymapteams.handlers;

import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamRank;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class FTBTeamsHandler extends AbstractHandler
{
    public FTBTeamsHandler()
    {
        super("ftbteams", "prop.category.label.ftb");
    }

    @Override
    public boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible)
    {
        var localTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(localPlayer.getUUID());
        var remoteTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(remotePlayer.getUUID());
        if (localTeam.isPresent() && remoteTeam.isPresent())
        {
            var allied = localTeam.get().getRankForPlayer(remotePlayer.getUUID()).isAtLeast(TeamRank.ALLY)
                    || remoteTeam.get().getRankForPlayer(localPlayer.getUUID()).isAtLeast(TeamRank.ALLY);

            var inPlayerTeam = (remoteTeam.get().isPlayerTeam() && !remoteTeam.get().isPartyTeam());
            var sameTeam = remoteTeam.get().getTeamId().equals(localTeam.get().getTeamId());
            if ((sameTeam || allied) || isOp || inPlayerTeam)
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
        var localPlayer = Minecraft.getInstance().player;
        var knownRemotePlayer = FTBTeamsAPI.api().getClientManager().getKnownPlayer(remotePlayer.getUUID());
        var knownLocalPlayer = FTBTeamsAPI.api().getClientManager().getKnownPlayer(localPlayer.getUUID());
        if (knownRemotePlayer.isPresent() && knownLocalPlayer.isPresent())
        {
            var remoteTeam = FTBTeamsAPI.api().getClientManager().getTeamByID(knownRemotePlayer.get().teamId());
            var localTeam = FTBTeamsAPI.api().getClientManager().getTeamByID(knownLocalPlayer.get().teamId());
            if (localTeam.isPresent() && remoteTeam.isPresent())
            {
                var allied = localTeam.get().getRankForPlayer(remotePlayer.getUUID()).isAtLeast(TeamRank.ALLY)
                        || remoteTeam.get().getRankForPlayer(localPlayer.getUUID()).isAtLeast(TeamRank.ALLY);

                var teammates = remoteTeam.get().getId() == localTeam.get().getId();
                return getColor(teammates, allied, remoteTeam.get().getProperty(TeamProperties.COLOR).rgb());
            }
        }
        return properties.getTeamColor();
    }
}
