package net.mysticdrew.journeymapteams.handlers;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.ClientTeam;
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
        var knownRemotePlayer = FTBTeamsAPI.getClientManager().getKnownPlayer(remotePlayer.getUUID());
        var knownLocalPlayer = FTBTeamsAPI.getClientManager().getKnownPlayer(localPlayer.getUUID());
        if (knownRemotePlayer != null && knownLocalPlayer != null)
        {
            ClientTeam remoteTeam = FTBTeamsAPI.getClientManager().getTeam(knownRemotePlayer.teamId);
            ClientTeam localTeam = FTBTeamsAPI.getClientManager().getTeam(knownLocalPlayer.teamId);

            var allied = localTeam.isAlly(remotePlayer.getUUID()) || remoteTeam.isAlly(localPlayer.getUUID());
            if (remoteTeam != null
                    && localTeam != null
                    && remoteTeam.getId() == localTeam.getId() || allied)
            {
                return allied && properties.doOverrideAllyColor.get() ? properties.overrideAllyColor.get().getColor() : remoteTeam.getColor();
            }
        }
        return properties.defaultTeamColor.get().getColor();
    }

    @Override
    public void createConfigs()
    {

    }
}
