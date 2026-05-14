package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;

public interface Handler
{
    /**
     * Server-side: decide radar visibility of {@code remote} for {@code receiver}.
     */
    boolean isVisible(Player receiver, Player remote, boolean isOp, boolean visible);

    /**
     * Client-side: team-name label / fallback color for a tracked player.
     */
    int getRemotePlayerNameColor(Player remotePlayer, int currentColor);

    /**
     * Client-side: icon/dot color for a tracked player.
     */
    int getRemotePlayerIconColor(Player remotePlayer, int currentColor);
}
