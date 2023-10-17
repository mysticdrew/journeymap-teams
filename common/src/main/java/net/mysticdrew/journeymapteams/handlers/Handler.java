package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;

public interface Handler
{
    boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible);

    int getRemotePlayerColor(Player remotePlayer);

    void createConfigs();
}
