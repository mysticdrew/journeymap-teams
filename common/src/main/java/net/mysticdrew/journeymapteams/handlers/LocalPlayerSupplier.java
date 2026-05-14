package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;

/**
 * Supplies the local (client) player. Only client plugins provide a real
 * implementation; server plugins never construct a color-capable handler, so
 * client-only classes are never classloaded on a dedicated server.
 */
@FunctionalInterface
public interface LocalPlayerSupplier
{
    Player get();
}
