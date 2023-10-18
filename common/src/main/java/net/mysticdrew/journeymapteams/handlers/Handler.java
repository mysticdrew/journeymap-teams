package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;

public interface Handler
{
    boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible);

    int getRemotePlayerNameColor(Player remotePlayer, int currentColor);

    int getRemotePlayerIconColor(Player remotePlayer, int currentColo);
}
