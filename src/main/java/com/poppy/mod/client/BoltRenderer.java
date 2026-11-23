package com.poppy.mod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.poppy.mod.entity.PoppyBolt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BoltRenderer extends EntityRenderer<PoppyBolt> {
    public BoltRenderer(EntityRendererProvider.Context c) { super(c); }
    @Override public ResourceLocation getTextureLocation(PoppyBolt e) { return PoppyRenderer.WHITE; }
    @Override public void render(PoppyBolt e, float yaw, float pt, PoseStack ps, MultiBufferSource buf, int light) {
        ps.pushPose(); ps.translate(0, 0.1, 0);
        ps.mulPose(Axis.YP.rotationDegrees((e.tickCount + pt) * 25f)); ps.mulPose(Axis.XP.rotationDegrees((e.tickCount + pt) * 17f)); ps.scale(1 / 16f, 1 / 16f, 1 / 16f);
        PoseStack.Pose p = ps.last();
        PoppyRenderer.box(p.pose(), p.normal(), buf.getBuffer(RenderType.entityCutoutNoCull(PoppyRenderer.WHITE)), -3, -3, -3, 3, 3, 3, e.color(), 0xF000F0, OverlayTexture.NO_OVERLAY);
        ps.popPose();
    }
}
