package net.mysticdrew.journeymapteams.handlers;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.client.integration.JourneyMapCommonPlugin;

public class FTBTeamsHandler implements Handler
{
    @Override
    public boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible)
    {
        var localTeam = FTBTeamsAPI.getManager().getPlayerTeam(localPlayer.getUUID());
        var remoteTeam = FTBTeamsAPI.getManager().getPlayerTeam(remotePlayer.getUUID());

        var allied = localTeam.isAlly(remotePlayer.getUUID()) || remoteTeam.isAlly(localPlayer.getUUID());

        if ((remoteTeam.getId() == localTeam.getId() || allied) || isOp)
        {
            return visible;
        }
        return false;
    }

    @Override
    public int getRemotePlayerColor(Player remotePlayer)
    {
        var localPlayer = Minecraft.getInstance().player;
        ClientTeam remoteTeam = FTBTeamsAPI.getClientManager().getTeam(remotePlayer.getUUID());
        ClientTeam localTeam = FTBTeamsAPI.getClientManager().getTeam(localPlayer.getUUID());

        if (remoteTeam != null
                && localTeam != null
                && remoteTeam.manager.getManagerId() == localTeam.manager.getManagerId())
        {
            return remoteTeam.getColor();
        }
        return JourneyMapCommonPlugin.getInstance().properties.defaultTeamColor.get().getColor();
    }
}
