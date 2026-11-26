package com.poppy.mod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.poppy.mod.creature.CreatureDef;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Renders a CreatureDef's box tree with Anim poses. */
public class PoppyRenderer extends EntityRenderer<PoppyMob> {
    static final ResourceLocation WHITE = new ResourceLocation("poppyplaytime", "textures/entity/white.png");
    static final ResourceLocation[] MATS = {WHITE, new ResourceLocation("poppyplaytime", "textures/entity/mat_fur.png"), new ResourceLocation("poppyplaytime", "textures/entity/mat_cloth.png"),
        new ResourceLocation("poppyplaytime", "textures/entity/mat_plastic.png"), new ResourceLocation("poppyplaytime", "textures/entity/mat_metal.png"),
        new ResourceLocation("poppyplaytime", "textures/entity/mat_fur2.png"), new ResourceLocation("poppyplaytime", "textures/entity/mat_yarn.png")};
    private boolean far;
    static final Map<String, ResourceLocation> DECALS = new HashMap<>();
    private final Map<CreatureDef, Map<String, List<CreatureDef.Part>>> kids = new HashMap<>();

    public PoppyRenderer(EntityRendererProvider.Context c) { super(c); shadowRadius = 0.6f; }
    @Override public ResourceLocation getTextureLocation(PoppyMob e) { return WHITE; }

    @Override public void render(PoppyMob e, float yaw, float pt, PoseStack ps, MultiBufferSource buf, int light) {
        CreatureDef d = e.def;
        ps.pushPose();
        shadowRadius = d.w * 0.6f;
        ps.mulPose(Axis.YP.rotationDegrees(180f - Mth.rotLerp(pt, e.yBodyRotO, e.yBodyRot)));
        if (e.deathTime > 0) { float f = Math.min(Mth.sqrt((e.deathTime + pt - 1f) / 20f * 1.6f), 1f); ps.translate(0, d.h * 0.1f * f, 0); ps.mulPose(Axis.ZP.rotationDegrees(f * 90f)); }
        ps.scale(d.scale / 16f, d.scale / 16f, d.scale / 16f);
        float ls = e.walkAnimation.position(pt), lsa = e.walkAnimation.speed(pt);
        float age = e.tickCount + pt;
        float hy = Mth.rotLerp(pt, e.yHeadRotO, e.yHeadRot) - Mth.rotLerp(pt, e.yBodyRotO, e.yBodyRot);
        Pose pose = Anim.compute(e, pt, ls, lsa, age, Mth.clamp(hy, -75, 75), Mth.lerp(pt, e.xRotO, e.getXRot()));
        int ov = LivingEntityRenderer.getOverlayCoords(e, 0f);
        Map<String, List<CreatureDef.Part>> tree = kids.computeIfAbsent(d, x -> { Map<String, List<CreatureDef.Part>> m = new HashMap<>(); for (var p : x.parts) m.computeIfAbsent(p.parent == null ? "" : p.parent, k -> new java.util.ArrayList<>()).add(p); return m; });
        far = e.distanceToSqr(net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()) > 22 * 22;
        draw("", tree, pose, ps, buf, light, ov);
        ps.popPose();
        super.render(e, yaw, pt, ps, buf, light);
    }

    private void draw(String parent, Map<String, List<CreatureDef.Part>> tree, Pose pose, PoseStack ps, MultiBufferSource buf, int light, int ov) {
        for (CreatureDef.Part p : tree.getOrDefault(parent, List.of())) {
            if (far && p.detail) continue;
            float[] a = pose.m.getOrDefault(p.name, new float[]{0, 0, 0, 0, 0, 0, 1, 1, 1});
            ps.pushPose();
            ps.translate(p.px + a[3], p.py + a[4], p.pz + a[5]);
            ps.mulPose(new Quaternionf().rotationZYX(a[2], a[1], a[0]));
            ps.scale(a[6], a[7], a[8]);
            PoseStack.Pose pp = ps.last();
            if (p.decal != null) {
                VertexConsumer dv = buf.getBuffer(RenderType.entityTranslucent(DECALS.computeIfAbsent(p.decal, k -> new ResourceLocation("poppyplaytime", "textures/entity/decal/" + k + ".png"))));
                float x0 = p.cx - p.w / 2, x1 = p.cx + p.w / 2, y0 = p.cy - p.h / 2, y1 = p.cy + p.h / 2, z = p.cz - p.d / 2;
                float[][] v = {{x1, y0, 0, 1}, {x0, y0, 1, 1}, {x0, y1, 1, 0}, {x1, y1, 0, 0}};
                for (float[] q : v) dv.vertex(pp.pose(), q[0], q[1], z).color(1f, 1f, 1f, 1f).uv(q[2], q[3]).overlayCoords(ov).uv2(p.glow ? 0xF000F0 : light).normal(pp.normal(), 0, 0, -1).endVertex();
                draw(p.name, tree, pose, ps, buf, light, ov);
                ps.popPose();
                continue;
            }
            VertexConsumer vc = buf.getBuffer(RenderType.entityCutoutNoCull(MATS[Mth.clamp(p.mat, 0, MATS.length - 1)]));
            box(pp.pose(), pp.normal(), vc, p.cx - p.w / 2, p.cy - p.h / 2, p.cz - p.d / 2, p.cx + p.w / 2, p.cy + p.h / 2, p.cz + p.d / 2, p.color, p.glow ? 0xF000F0 : light, ov);
            draw(p.name, tree, pose, ps, buf, light, ov);
            ps.popPose();
        }
    }

    /** Tile size in model pixels: one texture repeat covers T px, offset per box so neighbours don't line up. */
    static final float T = 16f;
    static void box(Matrix4f m, Matrix3f n, VertexConsumer vc, float x0, float y0, float z0, float x1, float y1, float z1, int c, int l, int ov) {
        float w = (x1 - x0) / T, h = (y1 - y0) / T, d = (z1 - z0) / T, o = ((x0 * 7.3f + y0 * 3.1f + z0 * 5.7f) % 1f + 1f) % 1f;
        float r = (c >> 16 & 255) / 255f, g = (c >> 8 & 255) / 255f, b = (c & 255) / 255f;
        // -z (front), +z, -x, +x, -y, +y
        q(m, n, vc, r, g, b, l, ov, w, h, o, 0, 0, -1, x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0);
        q(m, n, vc, r, g, b, l, ov, w, h, o, 0, 0, 1, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);
        q(m, n, vc, r, g, b, l, ov, d, h, o, -1, 0, 0, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
        q(m, n, vc, r, g, b, l, ov, d, h, o, 1, 0, 0, x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1);
        q(m, n, vc, r * 0.8f, g * 0.8f, b * 0.8f, l, ov, w, d, o, 0, -1, 0, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1);
        q(m, n, vc, r, g, b, l, ov, w, d, o, 0, 1, 0, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0);
    }
    private static void q(Matrix4f m, Matrix3f n, VertexConsumer vc, float r, float g, float b, int l, int ov, float ua, float va, float o, float nx, float ny, float nz, float... v) {
        for (int i = 0; i < 4; i++)
            vc.vertex(m, v[i * 3], v[i * 3 + 1], v[i * 3 + 2]).color(r, g, b, 1f).uv(o + (i == 1 || i == 2 ? ua : 0), o + (i >= 2 ? va : 0)).overlayCoords(ov).uv2(l).normal(n, nx, ny, nz).endVertex();
    }
}
