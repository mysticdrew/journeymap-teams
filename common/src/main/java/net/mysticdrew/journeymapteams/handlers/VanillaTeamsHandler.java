package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class VanillaTeamsHandler implements Handler
{

    @Override
    public boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible)
    {
        return false;
    }

    @Override
    public int getRemotePlayerColor(Player remotePlayer)
    {
        var localPlayer = Minecraft.getInstance().player;
        return 0;
    }
}
