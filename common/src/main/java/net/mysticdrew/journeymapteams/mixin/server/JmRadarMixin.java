package net.mysticdrew.journeymapteams.mixin.server;

import journeymap.common.Journeymap;
import journeymap.common.events.ServerEventHandler;
import journeymap.common.network.dispatch.NetworkDispatcher;
import net.minecraft.server.level.ServerPlayer;
import net.mysticdrew.journeymapteams.JourneyMapTeams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Hands the per-player {@code visible} flag through our handler so team-based
 * hiding actually applies on the server. JM 5.10 has no server event, so we
 * redirect its own per-player {@code sendPlayerLocationPacket} call to swap
 * the boolean argument before JM dispatches it.
 *
 * <p>This used to be an {@code @Inject(cancellable = true) + ci.cancel()} that
 * captured locals. Two bugs in that approach:
 * <ul>
 *   <li>{@code ci.cancel()} returns from {@code sendPlayerTrackingData}, not
 *       from the for-loop iteration, so on JM 5.10 (which sends a packet per
 *       player inside the loop) every receiver only ever processed the first
 *       non-self radar player and silently skipped everyone else.</li>
 *   <li>The extra "enclosing method args" added to the redirect handler in a
 *       previous attempt are not legal for SpongePowered {@code @Redirect};
 *       the handler signature must match the redirected call exactly, so any
 *       extra parameters cause the redirect to silently fail to apply when
 *       {@code require = 0}.</li>
 * </ul>
 * Mirrors what {@code PlayerRadarUpdateEvent} does in journeymap-6: mutate the
 * visibility decision and let JM's own dispatch path proceed.
 */
@Mixin(value = ServerEventHandler.class, remap = false)
public class JmRadarMixin
{
    @Redirect(
            method = "sendPlayerTrackingData(Lnet/minecraft/server/level/ServerPlayer;Z)V",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/common/network/dispatch/NetworkDispatcher;sendPlayerLocationPacket(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerPlayer;Z)V"),
            require = 0
    )
    private void journeymapTeams$filterVisibility(NetworkDispatcher dispatcher,
                                                  ServerPlayer receiver,
                                                  ServerPlayer radarPlayer,
                                                  boolean visible)
    {
        boolean receiverOp = Journeymap.isOp(receiver);
        boolean display = JourneyMapTeams.getInstance()
                .getHandler()
                .isVisible(receiver, radarPlayer, receiverOp, visible);
        dispatcher.sendPlayerLocationPacket(receiver, radarPlayer, display);
    }

    @Redirect(
            method = "sendPlayerTrackingData(Lnet/minecraft/class_3222;Z)V",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/common/network/dispatch/NetworkDispatcher;sendPlayerLocationPacket(Lnet/minecraft/class_3222;Lnet/minecraft/class_3222;Z)V"),
            require = 0
    )
    private void journeymapTeams$filterVisibilityIntermediary(NetworkDispatcher dispatcher,
                                                              ServerPlayer receiver,
                                                              ServerPlayer radarPlayer,
                                                              boolean visible)
    {
        boolean receiverOp = Journeymap.isOp(receiver);
        boolean display = JourneyMapTeams.getInstance()
                .getHandler()
                .isVisible(receiver, radarPlayer, receiverOp, visible);
        dispatcher.sendPlayerLocationPacket(receiver, radarPlayer, display);
    }
}
