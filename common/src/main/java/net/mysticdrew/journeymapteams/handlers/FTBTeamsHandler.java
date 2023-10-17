package net.mysticdrew.journeymapteams.handlers;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.TeamRank;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import dev.ftb.mods.ftbteams.api.property.TeamPropertyValue;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.FTBTeamsHandlerProperties;

public class FTBTeamsHandler implements Handler
{
    private FTBTeamsHandlerProperties properties;

    public FTBTeamsHandler()
    {
        properties = new FTBTeamsHandlerProperties();
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

            if ((remoteTeam.get().getId() == localTeam.get().getId() || allied) || isOp)
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

                if (remoteTeam.get().getId() == localTeam.get().getId() || allied)
                {
                    return allied && properties.doOverrideAllyColor.get()
                            ? properties.overrideAllyColor.get().getColor()
                            : remoteTeam.get().getProperty(TeamProperties.COLOR).rgb();
                }


            }
        }
        return properties.defaultTeamColor.get().getColor();
    }
}
