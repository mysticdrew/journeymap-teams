package net.mysticdrew.journeymapteams.handlers;

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
        // The receiver being op overrides team filtering (still respects JM's own visibility flag).
        if (isOp)
        {
            return visible;
        }

        var localTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(localPlayer.getUUID());
        var remoteTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(remotePlayer.getUUID());

        // No FTB team data on either side: hide. Every online player normally has a player team,
        // so empty here means we cannot make a relationship decision.
        if (localTeam.isEmpty() || remoteTeam.isEmpty())
        {
            return false;
        }

        var local = localTeam.get();
        var remote = remoteTeam.get();

        // Same FTB team (party members share a team id; personal player teams never collide).
        if (remote.getTeamId().equals(local.getTeamId()))
        {
            return visible;
        }

        // Either side has marked the other as ALLY or higher on their team.
        boolean allied = local.getRankForPlayer(remotePlayer.getUUID()).isAtLeast(TeamRank.ALLY)
                || remote.getRankForPlayer(localPlayer.getUUID()).isAtLeast(TeamRank.ALLY);
        if (allied)
        {
            return visible;
        }

        return false;
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
