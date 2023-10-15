package net.mysticdrew.journeymapftbteams.mixin.server;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import journeymap.common.Journeymap;
import journeymap.common.events.ServerEventHandler;
import journeymap.common.network.dispatch.NetworkDispatcher;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static journeymap.common.Journeymap.isOp;

@Mixin(value = ServerEventHandler.class, remap = false)
public class JmRadarMixin
{

    @Redirect(method = "sendPlayerTrackingData(Lnet/minecraft/server/level/ServerPlayer;Z)V",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/common/network/dispatch/NetworkDispatcher;sendPlayerLocationPacket" +
                            "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerPlayer;Z)V"))
    public void sendPlayerTrackingData(NetworkDispatcher instance, ServerPlayer player, ServerPlayer radarPlayer, boolean visible)
    {

        var teamOne = FTBTeamsAPI.getManager().getPlayerTeam(player);
        var teamTwo = FTBTeamsAPI.getManager().getPlayerTeam(radarPlayer);

        if (teamTwo != teamOne && !isOp(player))
        {
            Journeymap.getInstance().getDispatcher().sendPlayerLocationPacket(player, radarPlayer, false);
        }
        else
        {
            Journeymap.getInstance().getDispatcher().sendPlayerLocationPacket(player, radarPlayer, visible);
        }

    }

}
