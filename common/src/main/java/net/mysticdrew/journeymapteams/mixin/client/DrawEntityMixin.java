package net.mysticdrew.journeymapteams.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import journeymap.client.cartography.color.RGB;
import journeymap.client.render.draw.DrawEntityStep;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.render.map.GridRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.JourneyMapTeams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.geom.Point2D;

@Mixin(value = DrawEntityStep.class)
public class DrawEntityMixin
{
    @Shadow
    Component playerTeamName;

    @Inject(method = "drawPlayer",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/client/render/draw/DrawUtil;drawBatchLabel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/renderer/MultiBufferSource;DDLjourneymap/client/render/draw/DrawUtil$HAlign;Ljourneymap/client/render/draw/DrawUtil$VAlign;Ljava/lang/Integer;FIFDZD)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false,
            require = 0)
    public void drawVanillaTeamPlayer(PoseStack poseStack, DrawStep.Pass pass, MultiBufferSource buffers, double drawX, double drawY, GridRenderer gridRenderer, float alpha, double heading, double fontScale, double rotation, CallbackInfo ci, LivingEntity living, int labelOffset, Point2D labelPoint)
    {
        int color = JourneyMapTeams.getInstance().getHandler().getRemotePlayerNameColor((Player) living, 16777215);
        DrawUtil.drawBatchLabel(poseStack, this.playerTeamName, buffers, labelPoint.getX(), labelPoint.getY(), DrawUtil.HAlign.Center, DrawUtil.VAlign.Below, RGB.BLACK_RGB, .8f, color, 1f, fontScale, false, rotation);
        ci.cancel();
    }

    @Surrogate
    @Inject(method = "drawPlayer",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/client/render/draw/DrawUtil;drawBatchLabel(Lnet/minecraft/class_4587;Lnet/minecraft/class_2561;Lnet/minecraft/class_4597;DDLjourneymap/client/render/draw/DrawUtil$HAlign;Ljourneymap/client/render/draw/DrawUtil$VAlign;Ljava/lang/Integer;FIFDZD)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 0),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false,
            require = 0)
    public void drawVanillaTeamPlayerSurrogate(PoseStack poseStack, DrawStep.Pass pass, MultiBufferSource buffers, double drawX, double drawY, GridRenderer gridRenderer, float alpha, double heading, double fontScale, double rotation, CallbackInfo ci, LivingEntity living, int labelOffset, Point2D labelPoint)
    {
        int color = JourneyMapTeams.getInstance().getHandler().getRemotePlayerNameColor((Player) living, 16777215);
        DrawUtil.drawBatchLabel(poseStack, this.playerTeamName, buffers, labelPoint.getX(), labelPoint.getY(), DrawUtil.HAlign.Center, DrawUtil.VAlign.Below, RGB.BLACK_RGB, .8f, color, 1f, fontScale, false, rotation);
        ci.cancel();
    }

    @Inject(method = "drawPlayer",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/client/render/draw/DrawUtil;drawBatchLabel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/renderer/MultiBufferSource;DDLjourneymap/client/render/draw/DrawUtil$HAlign;Ljourneymap/client/render/draw/DrawUtil$VAlign;Ljava/lang/Integer;FIFDZD)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 1),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false,
            require = 0)
    public void drawTeamPlayer(PoseStack poseStack, DrawStep.Pass pass, MultiBufferSource buffers, double drawX, double drawY, GridRenderer gridRenderer, float alpha, double heading, double fontScale, double rotation, CallbackInfo ci, LivingEntity living, int labelOffset, Point2D labelPoint)
    {
        int color = JourneyMapTeams.getInstance().getHandler().getRemotePlayerNameColor((Player) living, 65280);
        DrawUtil.drawBatchLabel(poseStack, living.getName(), buffers, labelPoint.getX(), labelPoint.getY(), DrawUtil.HAlign.Center, DrawUtil.VAlign.Below, RGB.BLACK_RGB, .8f, color, 1f, fontScale, false, rotation);
        ci.cancel();
    }

    @Surrogate
    @Inject(method = "drawPlayer",
            at = @At(value = "INVOKE",
                    target = "Ljourneymap/client/render/draw/DrawUtil;drawBatchLabel(Lnet/minecraft/class_4587;Lnet/minecraft/class_2561;Lnet/minecraft/class_4597;DDLjourneymap/client/render/draw/DrawUtil$HAlign;Ljourneymap/client/render/draw/DrawUtil$VAlign;Ljava/lang/Integer;FIFDZD)V",
                    shift = At.Shift.BEFORE,
                    ordinal = 1),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false,
            require = 0)
    public void drawTeamPlayerSurrogate(PoseStack poseStack, DrawStep.Pass pass, MultiBufferSource buffers, double drawX, double drawY, GridRenderer gridRenderer, float alpha, double heading, double fontScale, double rotation, CallbackInfo ci, LivingEntity living, int labelOffset, Point2D labelPoint)
    {
        int color = JourneyMapTeams.getInstance().getHandler().getRemotePlayerNameColor((Player) living, 65280);
        DrawUtil.drawBatchLabel(poseStack, living.getName(), buffers, labelPoint.getX(), labelPoint.getY(), DrawUtil.HAlign.Center, DrawUtil.VAlign.Below, RGB.BLACK_RGB, .8f, color, 1f, fontScale, false, rotation);
        ci.cancel();
    }

}
