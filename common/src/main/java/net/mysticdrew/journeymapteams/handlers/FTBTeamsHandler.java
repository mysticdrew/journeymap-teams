package net.mysticdrew.journeymapteams.handlers;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.ClientTeam;
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
        var localTeam = FTBTeamsAPI.getManager().getPlayerTeam(localPlayer.getUUID());
        var remoteTeam = FTBTeamsAPI.getManager().getPlayerTeam(remotePlayer.getUUID());
        if (localTeam != null && remoteTeam != null)
        {
            var allied = localTeam.isAlly(remotePlayer.getUUID()) || remoteTeam.isAlly(localPlayer.getUUID());

            if ((remoteTeam.getId() == localTeam.getId() || allied) || isOp || (remoteTeam.getType().isPlayer() && !remoteTeam.getType().isParty()))
            {
                return visible;
            }
            return false;
        }
        return visible;
    }

    protected int getRemotePlayerColor(Player remotePlayer)
    {
        var localPlayer = Minecraft.getInstance().player;
        var knownRemotePlayer = FTBTeamsAPI.getClientManager().getKnownPlayer(remotePlayer.getUUID());
        var knownLocalPlayer = FTBTeamsAPI.getClientManager().getKnownPlayer(localPlayer.getUUID());
        if (knownRemotePlayer != null && knownLocalPlayer != null)
        {
            ClientTeam remoteTeam = FTBTeamsAPI.getClientManager().getTeam(knownRemotePlayer.teamId);
            ClientTeam localTeam = FTBTeamsAPI.getClientManager().getTeam(knownLocalPlayer.teamId);
            if (remoteTeam != null && localTeam != null)
            {
                var allied = localTeam.isAlly(remotePlayer.getUUID()) || remoteTeam.isAlly(localPlayer.getUUID());
                var teammates = remoteTeam.getId() == localTeam.getId();
                return getColor(teammates, allied, remoteTeam.getColor());
            }
        }
        return properties.getTeamColor();
    }
}
