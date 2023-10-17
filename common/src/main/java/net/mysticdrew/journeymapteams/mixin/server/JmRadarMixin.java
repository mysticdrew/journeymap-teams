package net.mysticdrew.journeymapteams.mixin.server;

import journeymap.common.Journeymap;
import journeymap.common.events.ServerEventHandler;
import journeymap.common.properties.GlobalProperties;
import net.minecraft.server.level.ServerPlayer;
import net.mysticdrew.journeymapteams.JourneyMapTeams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mixin(value = ServerEventHandler.class, remap = false)
public class JmRadarMixin
{
    @Inject(method = "sendPlayerTrackingData(Lnet/minecraft/server/level/ServerPlayer;Z)V",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/common/network/dispatch/NetworkDispatcher;sendPlayerLocationPacket(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerPlayer;Z)V",
                    shift = At.Shift.BEFORE),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void sendPlayerTrackingData(ServerPlayer entityPlayerMP,
                                       boolean receiverOp,
                                       CallbackInfo ci,
                                       List serverPlayers,
                                       GlobalProperties properties,
                                       Iterator var5,
                                       ServerPlayer radarPlayer,
                                       boolean sameDimension,
                                       boolean sneaking,
                                       boolean invisible,
                                       boolean hideOp,
                                       boolean hideSpectators,
                                       boolean seeUnderground,
                                       boolean visible,
                                       UUID playerId)
    {
        boolean display = JourneyMapTeams.getInstance().getHandler().isVisible(entityPlayerMP, radarPlayer, receiverOp, visible);
        Journeymap.getInstance().getDispatcher().sendPlayerLocationPacket(entityPlayerMP, radarPlayer, display);
        ci.cancel();
    }
}

