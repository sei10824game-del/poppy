package com.poppy.mod.creature;

import com.poppy.mod.client.Pose;
import com.poppy.mod.entity.Atk;
import com.poppy.mod.entity.Fx;
import com.poppy.mod.entity.PoppyBolt;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Batch D – original "nightmare plush" creatures (glitchy, red-stitched horror toys). See DEV_GUIDE.md. */
public final class BatchD {
    static final float P = (float) Math.PI;
    static final int BLACK = 0x0d0608, BONE = 0xe8e0cc, BLOOD = 0xb0101c, GLOW_RED = 0xff2a2a, THREAD = 0xd8d0c0, IRON = 0x7a7f88;

    public static List<CreatureDef> all() {
        List<CreatureDef> l = new ArrayList<>();
        l.add(duskProwler()); l.add(ragHound()); l.add(tuskLump()); l.add(snarlHare());
        l.add(spurclaw()); l.add(glimmerhorn()); l.add(troughHog()); l.add(gloomCub());
        return l;
    }

    // ---------- model helpers ----------
    /** Glowing eye pair + slit pupils on parent at (±x, y, z). */
    static CreatureDef eyes(CreatureDef d, String par, float x, float y, float z, float s, int eye, int pupil) {
        return d.part("eyeL", par, -x, y, z, s, s, 1, 0, 0, 0, eye).glow().part("eyeR", par, x, y, z, s, s, 1, 0, 0, 0, eye).glow()
                .part("pupilL", par, -x, y, z - 0.4f, s * 0.35f, s * 0.8f, 0.5f, 0, 0, 0, pupil).part("pupilR", par, x, y, z - 0.4f, s * 0.35f, s * 0.8f, 0.5f, 0, 0, 0, pupil);
    }
    /** Row of n small boxes (teeth, stitches, claws...). */
    static CreatureDef row(CreatureDef d, String par, String pre, int n, float x0, float y, float z, float dx, float w, float h, float dp, int c) {
        for (int i = 0; i < n; i++) d.part(pre + i, par, x0 + i * dx, y, z, w, h, dp, 0, 0, 0, c);
        return d;
    }
    /** Vertical ladder of n horizontal stitch bars starting at y0, step dy. */
    static CreatureDef col(CreatureDef d, String par, String pre, int n, float y0, float z, float dy, float w, float h, int c) {
        for (int i = 0; i < n; i++) d.part(pre + i, par, 0, y0 + i * dy, z, w, h, 0.3f, 0, 0, 0, c);
        return d;
    }
    /** X-shaped stitch (two crossed bars approximated by an offset pair). */
    static CreatureDef xStitch(CreatureDef d, String par, String pre, float x, float y, float z) {
        return d.part(pre + "a", par, x, y, z, 3, 0.8f, 0.5f, 0, 0, 0, THREAD).part(pre + "b", par, x, y, z, 0.8f, 3, 0.5f, 0, 0, 0, THREAD);
    }

    // ---------- server helpers ----------
    /** At tick `at`, hit every foe in the front arc; extra runs per victim. */
    static void strike(PoppyMob m, AttackSpec sp, int t, int at, double dot, float dmgMul, float kb, Consumer<LivingEntity> extra) {
        if (t != at) return;
        Vec3 f = Fx.flat(m);
        m.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, 0.6f + m.getRandom().nextFloat() * 0.3f);
        for (LivingEntity e : Fx.arc(m, sp.reach(), dot)) { Fx.hit(m, e, sp.dmg() * dmgMul, kb, f); if (extra != null) extra.accept(e); }
    }
    static void puff(PoppyMob m, Vec3 at, int n) {
        if (m.level() instanceof ServerLevel lv) { lv.sendParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 1, at.z, n, .4, .6, .4, .02); lv.sendParticles(ParticleTypes.CRIMSON_SPORE, at.x, at.y + 1, at.z, n, .5, .8, .5, .05); }
    }
    static void launch(LivingEntity e, double up) { e.setDeltaMovement(e.getDeltaMovement().add(0, up, 0)); e.hurtMarked = true; }
    /** Windup envelope (0→1 in windup, 1→0 in recovery). */
    static float env(float w, float r) { return r > 0 ? 1 - Pose.ease(r) : Pose.ease(w); }
    /** Glitch jitter used by all nightmare anims during the strike. */
    static void glitch(Pose p, float age, float s, float r) {
        if (s > 0 && r <= 0) { p.add("head", 0, Mth.sin(age * 7.3f) * 0.12f, Mth.sin(age * 5.1f) * 0.08f); p.g("body")[3] += Mth.sin(age * 9f) * 0.4f; }
    }

    // =====================================================================
    /** Dusk Prowler – lanky smoke-violet cat plush, split stitched grin, red slit eyes. */
    static CreatureDef duskProwler() {
        int fur = 0x3a2d4a, dark = 0x241a30, muz = 0x5b4d6a;
        CreatureDef d = CreatureDef.of("dusk_prowler", "Dusk Prowler", "ダスクプラウラー").size(0.9f, 3.3f).hp(160).dmg(9).speed(0.36).kbRes(0.4).egg(fur, GLOW_RED).spawn(1)
                .part("legL", null, -4, 22, 1, 5, 22, 5, 0, -11, 0, dark).walk(0.9f, 0)
                .part("footL", "legL", 0, -21, -1, 6, 2, 8, 0, 0, -1, fur)
                .part("legR", null, 4, 22, 1, 5, 22, 5, 0, -11, 0, dark).walk(0.9f, P)
                .part("footR", "legR", 0, -21, -1, 6, 2, 8, 0, 0, -1, fur)
                .part("body", null, 0, 22, 0, 13, 18, 9, 0, 9, 0, fur).sway(0.03f, 0.08f, 0, 2)
                .part("rip", "body", 0, 9, -4.7f, 6, 9, 1, 0, 0, 0, BLOOD)
                .part("spike0", "body", 0, 16, 5, 2, 4, 2, 0, 0, 0, BLOOD).part("spike1", "body", 0, 11, 5, 2, 3, 2, 0, 0, 0, BLOOD).part("spike2", "body", 0, 6, 5, 2, 3, 2, 0, 0, 0, BLOOD)
                .part("head", "body", 0, 18, 0, 16, 13, 12, 0, 6.5f, 0, fur).sway(0.05f, 1.9f, 0, 1)
                .part("earL", "head", -6, 13, 0, 4, 6, 3, 0, 3, 0, fur).part("earLi", "earL", 0, 0, -1.6f, 2, 4, 0.5f, 0, 3, 0, BLOOD)
                .part("earR", "head", 6, 13, 0, 4, 5, 3, 0, 2.5f, 0, fur).part("earRi", "earR", 0, 0, -1.6f, 2, 3, 0.5f, 0, 2.5f, 0, BLOOD)
                .part("muzzle", "head", 0, 4, -6.5f, 8, 4, 2, 0, 0, 0, muz)
                .part("nose", "head", 0, 6, -7.6f, 3, 2, 1, 0, 0, 0, BLACK)
                .part("whiskL", "head", -7, 4, -7, 6, 0.5f, 0.5f, 0, 0, 0, THREAD).part("whiskR", "head", 7, 4, -7, 6, 0.5f, 0.5f, 0, 0, 0, THREAD)
                .part("jaw", "head", 0, 2, -4, 12, 3, 8, 0, -1.5f, -2.5f, 0x140409)
                .part("armL", "body", -8.5f, 16, 0, 4, 24, 4, 0, -12, 0, fur).sway(0.05f, 0.09f, 0, 2)
                .part("armR", "body", 8.5f, 16, 0, 4, 24, 4, 0, -12, 0, fur).sway(0.05f, 0.09f, P, 2)
                .part("tail", "body", 0, 3, 4.5f, 3, 3, 20, 0, 0, 10, dark).sway(0.35f, 0.12f, 0, 1)
                .part("tailTip", "tail", 0, 0, 20, 4, 4, 4, 0, 0, 1, BLOOD);
        eyes(d, "head", 4, 8, -6.2f, 3.5f, GLOW_RED, BLACK);
        row(d, "head", "fang", 5, -4, 2, -6.8f, 2, 1, 2, 1, BONE);
        row(d, "jaw", "lfang", 4, -3, -0.3f, -6.4f, 2, 1, 1.6f, 1, BONE);
        row(d, "armL", "clawL", 3, -1.2f, -25, -1.5f, 1.2f, 0.8f, 3, 0.8f, BONE);
        row(d, "armR", "clawR", 3, -1.2f, -25, -1.5f, 1.2f, 0.8f, 3, 0.8f, BONE);
        col(d, "rip", "stitch", 4, -3, -0.6f, 2, 7, 0.6f, THREAD);
        return d
                // Double rake: right claw then left claw, hips twisting.
                .custom(5, 3.5f, 0, 26, 9, 10, 9, (m, sp, t) -> { strike(m, sp, t, 10, 0.1, 1f, 0.4f, null); strike(m, sp, t, 15, 0.1, 1.1f, 0.9f, e -> e.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0))); },
                        (p, e, w, s, r, age) -> {
                            float a = s < 0.5f ? Pose.ease(s * 2) : 1, b = s < 0.5f ? 0 : Pose.ease((s - 0.5f) * 2), f = env(w, r);
                            p.add("armR", f * (2.4f - a * 1.2f), 0, f * (0.6f - a * 1.4f));
                            p.add("armL", f * (0.3f + b * 1.9f), 0, f * (-0.3f + b * 1.5f) * -1);
                            p.add("body", f * (-0.25f), f * (-0.5f + a * 1.0f - b * 1.0f), 0);
                            p.add("head", 0, f * (0.4f - a * 0.8f + b * 0.8f), 0);
                            p.g("body")[5] += f * (2 - 6 * (a + b) * 0.5f);
                            p.add("jaw", -0.6f * f, 0, 0); glitch(p, age, s, r);
                        })
                // Shadow blink: fades out, re-forms behind the target and rakes.
                .custom(8, 16f, 4f, 180, 14, 6, 12, (m, sp, t) -> {
                    LivingEntity tg = m.getTarget();
                    if (t == 14 && tg != null) { puff(m, m.position(), 20); Vec3 b = tg.position().subtract(Fx.flat(m).scale(1.6)); Vec3 back = tg.position().add(tg.getLookAngle().multiply(1, 0, 1).normalize().scale(-1.6));
                        Vec3 to = m.level().noCollision(m, m.getBoundingBox().move(back.subtract(m.position()))) ? back : b; m.teleportTo(to.x, tg.getY(), to.z); m.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, tg.getEyePosition());
                        m.setYRot(m.getYHeadRot()); m.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.5f, 0.5f); puff(m, m.position(), 20); }
                    strike(m, sp, t, 17, -0.2, 1f, 1f, e -> e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0)));
                }, (p, e, w, s, r, age) -> {
                    float fade = w < 1 ? 1 - Pose.ease(w) * 0.9f : (r <= 0 ? 0.1f + Pose.ease(s) * 0.9f : 1);
                    for (String n : new String[]{"body", "legL", "legR"}) { float[] g = p.g(n); g[6] *= fade; g[7] *= 0.4f + 0.6f * fade; g[8] *= fade; }
                    p.rot("armR", 0.5f, 0, 0.4f, 2.6f, 0, -0.6f, w, s, r); p.rot("armL", 0.5f, 0, -0.4f, 2.6f, 0, 0.6f, w, s, r);
                    p.rot("body", 0.3f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -0.9f, 0, 0, w, s, r);
                    if (w < 1) p.g("body")[3] += Mth.sin(age * 11) * 2 * w; glitch(p, age, s, r);
                })
                .atk(Atk.POUNCE, 8, 9f, 3f, 110).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -9, 3, 0, 3, -10, w, s, r); p.rot("body", -0.7f, 0, 0, -1.3f, 0, 0, w, s, r);
                    p.rot("legL", 1.3f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("legR", 1.3f, 0, 0, -0.6f, 0, 0, w, s, r);
                    p.rot("armL", -0.4f, 0, -0.3f, 2.9f, 0, -0.2f, w, s, r); p.rot("armR", -0.4f, 0, 0.3f, 2.9f, 0, 0.2f, w, s, r);
                    p.rot("head", 0.7f, 0, 0, 1.0f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -1.0f, 0, 0, w, s, r);
                    p.rot("tail", 0.6f, 0, 0, -0.4f, 0, 0, w, s, r); p.add("tail", 0, Mth.sin(age * 1.5f) * 0.3f * env(w, r) * (1 - s), 0);
                })
                .atk(Atk.GAS, 3, 6f, 0, 240, 1, "").anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.35f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("head", 0.6f, 0, 0, -0.2f, 0, 0, w, s, r);
                    p.rot("jaw", -0.2f, 0, 0, -1.3f, 0, 0, w, s, r); p.scl("body", 1.1f, 1.05f, 1.15f, 0.92f, 1, 0.9f, w, s, r);
                    p.rot("armL", 0.3f, 0, -1.0f, 0.6f, 0, -0.4f, w, s, r); p.rot("armR", 0.3f, 0, 1.0f, 0.6f, 0, 0.4f, w, s, r);
                    p.rot("tail", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r); glitch(p, age, s, r);
                })
                .atk(Atk.WHIP, 7, 6.5f, 2f, 90).anim((p, e, w, s, r, age) -> {
                    // turns its back and lashes the tail around
                    p.rot("body", 0.1f, 1.6f, 0, -0.2f, -1.2f, 0, w, s, r); p.rot("tail", -0.3f, 0.6f, 0, 0.2f, -1.6f, 0, w, s, r);
                    p.scl("tail", 1, 1, 1, 1, 1, 1.8f, w, s, r); p.rot("head", 0, -1.2f, 0, 0, 1.0f, 0, w, s, r);
                    p.rot("armL", 0.4f, 0, -0.8f, 0.2f, 0, -0.4f, w, s, r); p.rot("armR", 0.4f, 0, 0.8f, 0.2f, 0, 0.4f, w, s, r);
                });
    }

    // =====================================================================
    /** Rag Hound – hunched patchwork hound, spiked collar & chain, one button eye. */
    static CreatureDef ragHound() {
        int tan = 0x8a5a2b, dark = 0x4a2e16, snout = 0xb98a50, mouth = 0x2a0508;
        CreatureDef d = CreatureDef.of("rag_hound", "Rag Hound", "ラグハウンド").size(1.0f, 2.8f).hp(130).dmg(8).speed(0.38).kbRes(0.4).egg(tan, BLOOD).spawn(1)
                .part("legL", null, -4, 18, 1, 6, 18, 6, 0, -9, 0, tan).walk(1.0f, 0).part("pawL", "legL", 0, -16.5f, -1, 7, 3, 8, 0, 0, 0, dark)
                .part("legR", null, 4, 18, 1, 6, 18, 6, 0, -9, 0, tan).walk(1.0f, P).part("pawR", "legR", 0, -16.5f, -1, 7, 3, 8, 0, 0, 0, dark)
                .part("body", null, 0, 18, 0, 15, 16, 10, 0, 8, 0, tan).sway(0.03f, 0.1f, 0, 0)
                .part("patch", "body", 4, 11, -5.2f, 5, 5, 1, 0, 0, 0, dark)
                .part("rip", "body", -3, 6, -5.2f, 4, 7, 1, 0, 0, 0, BLOOD)
                .part("collar", "body", 0, 16, 0, 16, 3, 11, 0, 0, 0, BLACK)
                .part("chain0", "collar", 0, -2, -6, 2, 3, 1, 0, 0, 0, IRON).part("chain1", "chain0", 0, -3, 0, 1, 3, 2, 0, 0, 0, IRON).sway(0.3f, 0.15f, 0, 2)
                .part("head", "body", 0, 17, 0, 13, 11, 11, 0, 5.5f, -1, tan).sway(0.06f, 2.3f, 0, 2)
                .part("snout", "head", 0, 3, -6.5f, 7, 5, 6, 0, 0, -3, snout)
                .part("nose", "snout", 0, 1.5f, -6.3f, 3, 2, 1, 0, 0, 0, BLACK)
                .part("jaw", "head", 0, 0.5f, -5, 7, 2.5f, 6, 0, -1, -3, mouth)
                .part("earL", "head", -6.5f, 10, 0, 3, 9, 5, 0, -4.5f, 0, dark).sway(0.15f, 0.2f, 0, 2)
                .part("earR", "head", 6.5f, 10, 0, 3, 5, 5, 0, -2.5f, 0, dark).part("earRrip", "earR", 0, -5, 0, 3.2f, 1.2f, 4, 0, 0, 0, BLOOD)
                .part("eyeL", "head", -3.5f, 7, -6.8f, 3, 3, 1, 0, 0, 0, GLOW_RED).glow()
                .part("button", "head", 3.5f, 7, -6.8f, 3.5f, 3.5f, 1, 0, 0, 0, BLACK)
                .part("browL", "head", -3.5f, 9.3f, -6.8f, 4, 1, 1, 0, 0, 0, BLACK).part("browR", "head", 3.5f, 9.3f, -6.8f, 4, 1, 1, 0, 0, 0, BLACK)
                .part("armL", "body", -9.5f, 14, 0, 5, 20, 5, 0, -10, 0, tan).sway(0.04f, 0.1f, 0, 2)
                .part("armR", "body", 9.5f, 14, 0, 5, 20, 5, 0, -10, 0, tan).sway(0.04f, 0.1f, P, 2)
                .part("tail", "body", 0, 4, 5, 3, 3, 9, 0, 2, 4.5f, dark).sway(0.5f, 0.6f, 0, 1);
        row(d, "collar", "spike", 4, -6, 0, -5.8f, 4, 1.5f, 2, 1.5f, BONE);
        row(d, "snout", "fang", 4, -2.25f, -2.8f, -5.2f, 1.5f, 0.8f, 2, 0.8f, BONE);
        row(d, "button", "thread", 2, -0.6f, 0, -0.6f, 1.2f, 0.5f, 2.4f, 0.3f, THREAD);
        row(d, "rip", "st", 3, -1.2f, 0, -0.6f, 1.2f, 0.4f, 6, 0.3f, THREAD);
        row(d, "armL", "clawL", 3, -1.5f, -21, -2, 1.5f, 1, 2.5f, 1, BONE);
        row(d, "armR", "clawR", 3, -1.5f, -21, -2, 1.5f, 1, 2.5f, 1, BONE);
        return d
                .atk(Atk.BITE, 8, 3.2f, 0, 32).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.3f, 0, 0, -0.7f, 0, 0, w, s, r); p.pos("body", 0, -2, 4, 0, -2, -8, w, s, r);
                    p.rot("head", 0.6f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("head", 0, 0, 2, 0, 0, -5, w, s, r); p.rot("jaw", -1.1f, 0, 0, 0.05f, 0, 0, w, s, r);
                    p.rot("armL", -0.5f, 0, -0.6f, 1.0f, 0, -0.2f, w, s, r); p.rot("armR", -0.5f, 0, 0.6f, 1.0f, 0, 0.2f, w, s, r);
                    p.rot("legL", -0.5f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("tail", 0, 0, 0, 0.6f, 0, 0, w, s, r);
                })
                .atk(Atk.GRAB, 5, 3f, 0, 150, 30, "").anim((p, e, w, s, r, age) -> {
                    // seize with jaws, then thrash head side to side
                    p.rot("body", 0.2f, 0, 0, -0.6f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, -6, w, s, r);
                    p.rot("jaw", -1.0f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("armL", 0.2f, 0, -0.8f, 1.4f, 0, 0.3f, w, s, r); p.rot("armR", 0.2f, 0, 0.8f, 1.4f, 0, -0.3f, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(age * 1.6f); p.add("head", 0, q * 0.6f, q * 0.3f); p.add("body", 0, q * 0.25f, 0); p.add("earL", 0, 0, -q * 0.6f); }
                })
                .atk(Atk.CHARGE, 9, 13f, 4f, 170).anim((p, e, w, s, r, age) -> {
                    // drops to all fours and gallops
                    p.rot("body", -0.3f, 0, 0, -1.2f, 0, 0, w, s, r); p.pos("body", 0, -2, 2, 0, -6, -4, w, s, r); p.rot("head", 0.4f, 0, 0, 1.0f, 0, 0, w, s, r);
                    p.rot("armL", 0.2f, 0, 0, 1.2f, 0, 0, w, s, r); p.rot("armR", 0.2f, 0, 0, 1.2f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.6f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.4f); p.add("armL", q * 0.8f, 0, 0); p.add("armR", -q * 0.8f, 0, 0); p.g("legL")[0] = -q; p.g("legR")[0] = q; p.add("body", q * 0.1f, 0, 0); }
                })
                .atk(Atk.SCREAM, 3, 10f, 3f, 260).anim((p, e, w, s, r, age) -> {
                    // howl: crouch, then throw head back to the sky
                    p.rot("body", 0.3f, 0, 0, 0.35f, 0, 0, w, s, r); p.pos("body", 0, -3, 0, 0, 1, 0, w, s, r);
                    p.rot("head", 0.5f, 0, 0, -1.1f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                    p.rot("armL", 0.3f, 0, -0.4f, -0.3f, 0, -0.9f, w, s, r); p.rot("armR", 0.3f, 0, 0.4f, -0.3f, 0, 0.9f, w, s, r); glitch(p, age, s, r);
                })
                .custom(6, 3.2f, 0, 60, 8, 8, 8, (m, sp, t) -> { strike(m, sp, t, 9, 0.2, 0.7f, 0.2f, null); strike(m, sp, t, 13, 0.2, 0.8f, 1.0f, e -> e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1))); },
                        (p, e, w, s, r, age) -> { // left-right paw maul
                            float f = env(w, r), a = Pose.ease(Mth.clamp(s * 2, 0, 1)), b = Pose.ease(Mth.clamp(s * 2 - 1, 0, 1));
                            p.add("armL", f * (2.6f - a * 1.8f), 0, -f * 0.4f); p.add("armR", f * (0.4f + b * 2.4f - b * b * 1.6f), 0, f * 0.4f);
                            p.add("body", -0.4f * f, f * (0.4f * a - 0.8f * b), 0); p.g("body")[5] -= 4 * f; p.add("jaw", -0.5f * f, 0, 0);
                        });
    }

    // =====================================================================
    /** Tusk Lump – hulking stitched elephant plush with cracked bloody tusks and a jointed trunk. */
    static CreatureDef tuskLump() {
        int grey = 0x55606e, dark = 0x3a414c, light = 0x7a8594;
        CreatureDef d = CreatureDef.of("tusk_lump", "Tusk Lump", "タスクランプ").size(2.0f, 3.6f).hp(280).dmg(11).speed(0.27).kbRes(0.9).armor(4).egg(grey, BLOOD).spawn(1)
                .part("legL", null, -7, 20, 0, 10, 20, 10, 0, -10, 0, dark).walk(0.6f, 0).part("nailsL", "legL", 0, -19, -5.2f, 8, 2, 1, 0, 0, 0, BONE)
                .part("legR", null, 7, 20, 0, 10, 20, 10, 0, -10, 0, dark).walk(0.6f, P).part("nailsR", "legR", 0, -19, -5.2f, 8, 2, 1, 0, 0, 0, BONE)
                .part("body", null, 0, 20, 0, 28, 24, 18, 0, 12, 0, grey).sway(0.02f, 0.07f, 0, 2)
                .part("belly", "body", 0, 10, -9.2f, 18, 14, 1, 0, 0, 0, light)
                .part("seam", "belly", 0, 0, -0.6f, 1, 14, 0.5f, 0, 0, 0, BLOOD)
                .part("bolt", "body", 11, 18, -9.2f, 3, 3, 1, 0, 0, 0, IRON)
                .part("head", "body", 0, 24, -2, 22, 18, 16, 0, 7, -4, grey).sway(0.04f, 1.6f, 0, 2)
                .part("earL", "head", -11, 10, -2, 2, 18, 14, -1, 0, 4, grey).sway(0.25f, 0.1f, 0, 1).part("earLi", "earL", -0.2f, 0, 4, 0.6f, 14, 10, 1.2f, 0, 0, BLOOD)
                .part("earR", "head", 11, 10, -2, 2, 18, 14, 1, 0, 4, grey).sway(0.25f, 0.1f, P, 1).part("earRi", "earR", 0.2f, 0, 4, 0.6f, 14, 10, -1.2f, 0, 0, BLOOD)
                .part("browL", "head", -6, 14, -12.3f, 6, 2, 1, 0, 0, 0, BLACK).part("browR", "head", 6, 14, -12.3f, 6, 2, 1, 0, 0, 0, BLACK)
                .part("jaw", "head", 0, 1, -9, 12, 3, 5, 0, -1.5f, -1, 0x1a0508)
                .part("trunk1", "head", 0, 5, -12, 7, 8, 7, 0, -4, 0, grey).sway(0.12f, 0.12f, 0, 0)
                .part("trunk2", "trunk1", 0, -8, 0, 6, 8, 6, 0, -4, 0, grey).sway(0.18f, 0.12f, 0.6f, 0)
                .part("trunk3", "trunk2", 0, -8, 0, 5, 7, 5, 0, -3.5f, 0, dark).sway(0.25f, 0.12f, 1.2f, 0)
                .part("nostril", "trunk3", 0, -7.2f, 0, 3, 0.6f, 3, 0, 0, 0, BLACK)
                .part("tuskL", "head", -6, 2, -11, 3, 3, 9, 0, 0, -4.5f, BONE).part("tipL", "tuskL", 0, 0, -9.5f, 2.6f, 2.6f, 2, 0, 0, 0, BLOOD)
                .part("tuskR", "head", 6, 2, -11, 3, 3, 5, 0, 0, -2.5f, BONE).part("crackR", "tuskR", 0, 0, -5.3f, 3.2f, 3.2f, 0.8f, 0, 0, 0, BLACK)
                .part("armL", "body", -16, 20, 0, 8, 22, 8, 0, -11, 0, grey).sway(0.03f, 0.08f, 0, 2).part("handL", "armL", 0, -22, 0, 9, 4, 9, 0, -1, 0, dark)
                .part("armR", "body", 16, 20, 0, 8, 22, 8, 0, -11, 0, grey).sway(0.03f, 0.08f, P, 2).part("handR", "armR", 0, -22, 0, 9, 4, 9, 0, -1, 0, dark)
                .part("tail", "body", 0, 8, 9, 2, 10, 2, 0, -5, 0, dark).sway(0.3f, 0.2f, 0, 2).part("tuft", "tail", 0, -10, 0, 3, 3, 3, 0, 0, 0, BLACK);
        eyes(d, "head", 6, 11, -12.2f, 3, 0xffb020, BLOOD);
        row(d, "jaw", "tooth", 4, -3, 0, -3.6f, 2, 1.2f, 1.5f, 1, BONE);
        col(d, "seam", "st", 4, -5, -0.4f, 3.3f, 3, 0.6f, THREAD);
        return d
                .atk(Atk.WHIP, 9, 7f, 0, 60).anim((p, e, w, s, r, age) -> {
                    // trunk coils up high, then cracks forward segment by segment
                    p.rot("trunk1", -1.4f, 0.3f, 0, 0.9f, -0.2f, 0, w, s, r); p.rot("trunk2", -1.2f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("trunk3", -1.0f, 0, 0, 0.4f, 0, 0, w, s, r);
                    p.scl("trunk2", 1, 1, 1, 1, 2.2f, 1, w, s, r); p.scl("trunk3", 1, 1, 1, 1, 2.2f, 1, w, s, r);
                    p.rot("head", 0.5f, 0.3f, 0, -0.35f, -0.2f, 0, w, s, r); p.rot("body", 0.15f, 0.25f, 0, -0.2f, -0.2f, 0, w, s, r);
                })
                .atk(Atk.SHOOT, 7, 14f, 4f, 90, 0x2a1a2e, "slow").anim((p, e, w, s, r, age) -> {
                    // sucks in (swells), trunk aimed, then sprays
                    p.scl("body", 1.12f, 1.05f, 1.12f, 0.95f, 1, 0.95f, w, s, r); p.rot("trunk1", -1.2f, 0, 0, -1.4f, 0, 0, w, s, r);
                    p.rot("trunk2", 0.3f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("trunk3", 0.3f, 0, 0, 0f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.15f, 0, 0, w, s, r);
                    p.pos("body", 0, 0, 0, 0, 0, 3, w, s, r);
                })
                .atk(Atk.STOMP, 11, 5f, 0, 110).anim((p, e, w, s, r, age) -> {
                    // rears up on hind legs, front limbs high, crashes down
                    p.rot("body", 0.9f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 4, 4, 0, -4, -4, w, s, r);
                    p.rot("armL", 2.6f, 0, -0.3f, 0.9f, 0, -0.1f, w, s, r); p.rot("armR", 2.6f, 0, 0.3f, 0.9f, 0, 0.1f, w, s, r);
                    p.rot("trunk1", -1.5f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("trunk2", -0.8f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", -0.6f, 0, 0, -0.3f, 0, 0, w, s, r);
                    p.rot("legL", -0.4f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("legR", -0.4f, 0, 0, 0.2f, 0, 0, w, s, r);
                })
                .atk(Atk.CHARGE, 12, 14f, 5f, 220).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.3f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("body", 0.1f, 0, 0, -0.45f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -2, -3, w, s, r);
                    p.rot("trunk1", 0, 0, 0, 0.8f, 0, 0, w, s, r); p.rot("armL", -0.4f, 0, 0, -1.0f, 0, 0.2f, w, s, r); p.rot("armR", -0.4f, 0, 0, -1.0f, 0, -0.2f, w, s, r);
                    p.scl("tuskL", 1, 1, 1, 1, 1, 1.3f, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.1f); p.g("legL")[0] = q; p.g("legR")[0] = -q; p.add("body", 0, 0, q * 0.06f); }
                })
                // Tusk toss: hooks tusks under the victim and flings it skyward.
                .custom(9, 3.5f, 0, 70, 12, 4, 14, (m, sp, t) -> strike(m, sp, t, 13, 0.3, 1f, 0.5f, e -> launch(e, 1.0)), (p, e, w, s, r, age) -> {
                    p.rot("head", 0.7f, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("body", 0.35f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, -3, 2, 0, 2, -5, w, s, r);
                    p.rot("trunk1", 0.6f, 0, 0, -1.2f, 0, 0, w, s, r); p.rot("legL", 0.3f, 0, 0, -0.4f, 0, 0, w, s, r);
                    p.rot("armL", 0.2f, 0, -0.6f, 0.1f, 0, -1.0f, w, s, r); p.rot("armR", 0.2f, 0, 0.6f, 0.1f, 0, 1.0f, w, s, r); glitch(p, age, s, r);
                });
    }

    // =====================================================================
    /** Snarl Hare – twitchy rabbit plush with spring-coil legs, torn ear and buck fangs. */
    static CreatureDef snarlHare() {
        int fur = 0x6e8a86, dark = 0x45605c, cream = 0xc9bfa8;
        CreatureDef d = CreatureDef.of("snarl_hare", "Snarl Hare", "スナールヘア").size(0.8f, 2.9f).hp(90).dmg(7).speed(0.42).kbRes(0.2).egg(fur, BLOOD).spawn(2)
                .part("legL", null, -4, 16, 2, 6, 16, 7, 0, -8, 0, fur).walk(1.1f, 0).part("footL", "legL", 0, -14.5f, -3, 5, 3, 12, 0, 0, -1, dark)
                .part("legR", null, 4, 16, 2, 6, 16, 7, 0, -8, 0, fur).walk(1.1f, P).part("footR", "legR", 0, -14.5f, -3, 5, 3, 12, 0, 0, -1, dark)
                .part("body", null, 0, 16, 0, 12, 16, 9, 0, 8, 0, fur).sway(0.04f, 0.15f, 0, 2)
                .part("belly", "body", 0, 7, -4.7f, 7, 10, 1, 0, 0, 0, cream)
                .part("gash", "belly", 1, 1, -0.6f, 2, 6, 0.5f, 0, 0, 0, BLOOD)
                .part("head", "body", 0, 16, 0, 12, 10, 10, 0, 5, 0, fur).sway(0.08f, 2.7f, 0, 2)
                .part("earL", "head", -3, 10, 0, 3, 14, 2, 0, 7, 0, fur).sway(0.08f, 0.2f, 0, 2).part("earLi", "earL", 0, 7, -1.1f, 1.6f, 11, 0.5f, 0, 0, 0, BLOOD)
                .part("earR", "head", 3, 10, 0, 3, 7, 2, 0, 3.5f, 0, fur).part("earR2", "earR", 0, 7, 0, 3, 7, 2, 0, 3.5f, 0, dark).sway(0.4f, 0.25f, 0, 0)
                .part("nose", "head", 0, 4.5f, -5.3f, 2, 1.5f, 1, 0, 0, 0, 0xc06070)
                .part("buckL", "head", -1.1f, 1.5f, -5.3f, 2, 3.5f, 1, 0, 0, 0, BONE).part("buckR", "head", 1.1f, 1.5f, -5.3f, 2, 3.5f, 1, 0, 0, 0, BONE)
                .part("jaw", "head", 0, 1, -3, 8, 2, 5, 0, -1, -2, 0x1a0508)
                .part("cheekL", "head", -5, 3, -4.8f, 2, 2, 1, 0, 0, 0, dark).part("cheekR", "head", 5, 3, -4.8f, 2, 2, 1, 0, 0, 0, dark)
                .part("armL", "body", -7.5f, 14, 0, 3, 12, 3, 0, -6, 0, fur).sway(0.05f, 0.2f, 0, 0)
                .part("armR", "body", 7.5f, 14, 0, 3, 12, 3, 0, -6, 0, fur).sway(0.05f, 0.2f, P, 0)
                .part("tail", "body", 0, 3, 5, 4, 4, 4, 0, 0, 1, cream);
        eyes(d, "head", 3, 6.5f, -5.2f, 3, 0xff3080, BLACK);
        row(d, "legL", "coilL", 3, 0, -4, 0, 0, 7, 1, 8, IRON);
        row(d, "legR", "coilR", 3, 0, -4, 0, 0, 7, 1, 8, IRON);
        row(d, "armL", "clawL", 3, -1, -12.5f, -1, 1, 0.6f, 2, 0.6f, BONE);
        row(d, "armR", "clawR", 3, -1, -12.5f, -1, 1, 0.6f, 2, 0.6f, BONE);
        col(d, "gash", "st", 3, -2, -0.4f, 2, 3, 0.5f, THREAD);
        return d
                // Double kick: plants hands, both spring legs snap forward.
                .custom(7, 3.2f, 0, 40, 10, 4, 10, (m, sp, t) -> strike(m, sp, t, 11, 0.2, 1f, 1.8f, null), (p, e, w, s, r, age) -> {
                    p.rot("body", -0.6f, 0, 0, 0.9f, 0, 0, w, s, r); p.pos("body", 0, -6, -2, 0, -2, 2, w, s, r);
                    p.rot("legL", 0.9f, 0, 0, -1.8f, 0, 0, w, s, r); p.rot("legR", 0.9f, 0, 0, -1.8f, 0, 0, w, s, r);
                    p.pos("legL", 0, -2, 0, 0, 4, -2, w, s, r); p.pos("legR", 0, -2, 0, 0, 4, -2, w, s, r);
                    p.rot("armL", 1.2f, 0, 0, 1.6f, 0, -0.3f, w, s, r); p.rot("armR", 1.2f, 0, 0, 1.6f, 0, 0.3f, w, s, r);
                    p.rot("earL", 0.6f, 0, 0, 1.2f, 0, 0, w, s, r); p.rot("head", -0.4f, 0, 0, -0.6f, 0, 0, w, s, r);
                })
                .atk(Atk.POUNCE, 8, 11f, 3f, 80).anim((p, e, w, s, r, age) -> {
                    // coils compress (deep crouch), launch, ears streaming back
                    p.pos("body", 0, -8, 0, 0, 4, -6, w, s, r); p.rot("body", -0.4f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("legL", 1.4f, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("legR", 1.4f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.scl("legL", 1, 0.7f, 1, 1, 1.25f, 1, w, s, r); p.scl("legR", 1, 0.7f, 1, 1, 1.25f, 1, w, s, r);
                    p.rot("earL", 0.3f, 0, 0, 1.4f, 0, 0, w, s, r); p.rot("earR", 0.3f, 0, 0, 1.4f, 0, 0, w, s, r);
                    p.rot("armL", 0, 0, 0, 2.5f, 0, 0, w, s, r); p.rot("armR", 0, 0, 0, 2.5f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.8f, 0, 0, w, s, r);
                })
                .atk(Atk.SWIPE, 6, 3f, 0, 25).anim((p, e, w, s, r, age) -> {
                    // spinning ear-slash: whole torso twists and the long ear cuts across
                    p.rot("body", 0, -1.0f, 0, 0, 1.4f, 0, w, s, r); p.rot("head", 0.2f, -0.5f, 0, 0.6f, 0.6f, 0, w, s, r);
                    p.rot("earL", 0.2f, 0, -0.6f, 1.6f, 0, 0.8f, w, s, r); p.rot("armR", 1.5f, -1.2f, 0, 1.4f, 1.2f, 0, w, s, r);
                    p.rot("legL", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r); glitch(p, age, s, r);
                })
                // Burrow ambush: sinks into the ground, erupts under the target.
                .custom(8, 18f, 4f, 200, 16, 6, 12, (m, sp, t) -> {
                    LivingEntity tg = m.getTarget();
                    if (t == 2 || t == 10) puff(m, m.position(), 10);
                    if (t == 16 && tg != null) { m.teleportTo(tg.getX(), tg.getY(), tg.getZ()); m.playSound(SoundEvents.ROOTED_DIRT_BREAK, 2f, 0.6f); puff(m, m.position(), 25); }
                    if (t == 17) { m.playSound(SoundEvents.GENERIC_EXPLODE, 1f, 1.5f); for (LivingEntity e : Fx.foes(m, m.position(), 2.5)) { Fx.hit(m, e, sp.dmg(), 0.6f, e.position().subtract(m.position())); launch(e, 0.9); } }
                }, (p, e, w, s, r, age) -> {
                    p.pos("body", 0, -40, 0, 0, 6, 0, w, s, r); p.pos("legL", 0, -40, 0, 0, 6, 0, w, s, r); p.pos("legR", 0, -40, 0, 0, 6, 0, w, s, r);
                    p.rot("armL", 3.0f, 0, -0.3f, 2.8f, 0, -0.8f, w, s, r); p.rot("armR", 3.0f, 0, 0.3f, 2.8f, 0, 0.8f, w, s, r); p.rot("jaw", 0, 0, 0, -1.0f, 0, 0, w, s, r);
                    if (w < 1) p.add("body", 0, Mth.sin(age * 2.5f) * 0.5f * w, 0);
                });
    }

    // =====================================================================
    /** Spurclaw – a rooster plush with rusted blade-spurs, serrated beak and a ragged black tail. */
    static CreatureDef spurclaw() {
        int red = 0xa8321c, wing = 0x7a2010, chest = 0xd4602a, leg = 0xb8a040, beak = 0xe0b020, comb = 0xd01020;
        CreatureDef d = CreatureDef.of("spurclaw", "Spurclaw", "スパークロウ").size(0.9f, 2.6f).hp(100).dmg(8).speed(0.4).kbRes(0.2).egg(red, BLACK).spawn(2)
                .part("legL", null, -3, 14, 1, 2, 14, 2, 0, -7, 0, leg).walk(1.0f, 0)
                .part("toeL", "legL", 0, -13.5f, -2, 4, 1, 5, 0, 0, 0, leg).part("spurL", "legL", 0, -10, 1.5f, 1, 1, 4, 0, 0, 1.5f, IRON).part("spurLt", "spurL", 0, 0, 4, 1.2f, 1.2f, 1.2f, 0, 0, 0, BLOOD)
                .part("legR", null, 3, 14, 1, 2, 14, 2, 0, -7, 0, leg).walk(1.0f, P)
                .part("toeR", "legR", 0, -13.5f, -2, 4, 1, 5, 0, 0, 0, leg).part("spurR", "legR", 0, -10, 1.5f, 1, 1, 4, 0, 0, 1.5f, IRON).part("spurRt", "spurR", 0, 0, 4, 1.2f, 1.2f, 1.2f, 0, 0, 0, BLOOD)
                .part("body", null, 0, 14, 0, 14, 14, 16, 0, 7, 1, red).sway(0.04f, 0.2f, 0, 0)
                .part("chest", "body", 0, 6, -7.2f, 10, 10, 1, 0, 0, 0, chest)
                .part("armL", "body", -7.5f, 11, 0, 2, 10, 12, 0, -4, 2, wing).sway(0.08f, 0.3f, 0, 2).part("featL", "armL", 0, -9.5f, 4, 2.2f, 2, 8, 0, 0, 0, BLACK)
                .part("armR", "body", 7.5f, 11, 0, 2, 10, 12, 0, -4, 2, wing).sway(0.08f, 0.3f, P, 2).part("featR", "armR", 0, -9.5f, 4, 2.2f, 2, 8, 0, 0, 0, BLACK)
                .part("tail", "body", 0, 10, 8, 2, 14, 3, 0, 5, 2, BLACK).sway(0.15f, 0.25f, 0, 0)
                .part("tailB", "tail", -2.5f, -1, 1, 2, 12, 3, 0, 4, 3, 0x2a2a35).part("tailC", "tail", 2.5f, -2, 1, 2, 10, 3, 0, 3, 4, BLOOD)
                .part("head", "body", 0, 13, -6, 8, 10, 8, 0, 5, 0, red).sway(0.1f, 3.1f, 0, 1)
                .part("comb0", "head", 0, 11, -2, 2, 3, 2, 0, 0, 0, comb).part("comb1", "head", 0, 11.5f, 0, 2, 4, 2, 0, 0, 0, comb).part("comb2", "head", 0, 11, 2, 2, 3, 2, 0, 0, 0, comb)
                .part("beak", "head", 0, 5, -4, 3, 2, 4, 0, 0, -2, beak)
                .part("jaw", "head", 0, 3.8f, -4, 3, 1, 3, 0, -0.5f, -1.5f, 0xb08010)
                .part("wattle", "head", 0, 1.5f, -4.3f, 3, 4, 1, 0, 0, 0, comb);
        eyes(d, "head", 3, 7, -4.2f, 2.2f, 0xffe030, BLACK);
        row(d, "beak", "serr", 3, -1, -1.2f, -2.5f, 1, 0.6f, 0.8f, 0.6f, BONE);
        row(d, "chest", "st", 3, -3, -3, -0.6f, 3, 0.6f, 2, 0.4f, THREAD);
        return d
                // Spur flurry: three alternating blade kicks with hops.
                .custom(4, 3f, 0, 45, 8, 12, 8, (m, sp, t) -> { for (int k = 0; k < 3; k++) strike(m, sp, t, 9 + k * 4, 0.1, 1f, k == 2 ? 1.2f : 0.2f, null); }, (p, e, w, s, r, age) -> {
                    float f = env(w, r), ph = s * 3, k = ph - (int) Math.min(ph, 2.999f), kick = Mth.sin(k * P);
                    boolean right = ((int) Math.min(ph, 2.999f)) % 2 == 0;
                    p.add("body", 0.3f * f, 0, 0); p.g("body")[4] += f * (2 + kick * 3 * (s > 0 && r <= 0 ? 1 : 0));
                    if (s > 0 && r <= 0) { p.add(right ? "legR" : "legL", -kick * 1.6f, 0, 0); p.add(right ? "legL" : "legR", kick * 0.4f, 0, 0); }
                    else { p.add("legR", 0.6f * f, 0, 0); }
                    p.add("armL", 0, 0, -1.2f * f); p.add("armR", 0, 0, 1.2f * f); p.add("head", -0.3f * f, 0, 0);
                })
                // Peck frenzy: rapid jackhammer pecks.
                .custom(2.5f, 2.6f, 0, 35, 6, 14, 8, (m, sp, t) -> { if (t > 6 && t <= 20 && t % 3 == 0) strike(m, sp, t, t, 0.4, 1f, 0.05f, null); }, (p, e, w, s, r, age) -> {
                    p.rot("body", 0.2f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r); p.pos("head", 0, 0, 2, 0, 0, -2, w, s, r);
                    if (s > 0 && r <= 0) { float q = Math.abs(Mth.sin(age * 2.2f)); p.add("head", q * 0.9f, Mth.sin(age * 4.4f) * 0.15f, 0); p.g("head")[5] -= q * 4; p.add("jaw", q * 0.5f, 0, 0); }
                })
                .atk(Atk.CLAP, 5, 3.5f, 0, 70).anim((p, e, w, s, r, age) -> {
                    // wing buffet: wings flare wide and slam together, body puffs
                    p.rot("armL", 0.2f, 0, -1.8f, 0.4f, 0, 0.3f, w, s, r); p.rot("armR", 0.2f, 0, 1.8f, 0.4f, 0, -0.3f, w, s, r);
                    p.scl("body", 1.15f, 1.1f, 1.1f, 1, 1, 1, w, s, r); p.rot("head", -0.5f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("tail", -0.5f, 0, 0, 0.2f, 0, 0, w, s, r);
                    p.pos("body", 0, 2, 2, 0, 0, -3, w, s, r);
                })
                .atk(Atk.SCREAM, 3, 9f, 2f, 220).anim((p, e, w, s, r, age) -> {
                    // crow: chest out, neck stretches, beak gapes to the sky
                    p.rot("body", 0.4f, 0, 0, 0.35f, 0, 0, w, s, r); p.pos("head", 0, -2, 1, 0, 4, -1, w, s, r); p.rot("head", 0.4f, 0, 0, -0.8f, 0, 0, w, s, r);
                    p.rot("jaw", 0, 0, 0, 0.9f, 0, 0, w, s, r); p.rot("armL", 0, 0, -0.5f, 0, 0, -1.5f, w, s, r); p.rot("armR", 0, 0, 0.5f, 0, 0, 1.5f, w, s, r); glitch(p, age, s, r);
                })
                .atk(Atk.SHOOT, 5, 12f, 3f, 60, 0x101014, "").anim((p, e, w, s, r, age) -> {
                    // flings a razor tail feather: twists away, whips round
                    p.rot("body", 0, 1.6f, 0, 0, -0.3f, 0, w, s, r); p.rot("tail", -0.6f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("armR", 0.5f, 0, 1.2f, 1.2f, 0, -0.2f, w, s, r);
                    p.rot("head", 0, -1.2f, 0, 0, 0.3f, 0, w, s, r);
                });
    }

    // =====================================================================
    /** Glimmerhorn – faded unicorn plush with hollow sockets, red-streaked black mane and a cracked glowing horn. */
    static CreatureDef glimmerhorn() {
        int lav = 0x7d6b8f, dark = 0x4e3f5e, snout = 0xa391b4, hoof = 0x2a2430, mane = 0x1a0a14, horn = 0xe9d7ff;
        CreatureDef d = CreatureDef.of("glimmerhorn", "Glimmerhorn", "グリマーホーン").size(0.9f, 3.0f).hp(120).dmg(8).speed(0.36).kbRes(0.3).egg(lav, 0xc040ff).spawn(1)
                .part("legL", null, -4, 18, 0, 6, 18, 6, 0, -9, 0, lav).walk(0.9f, 0).part("hoofL", "legL", 0, -16, 0, 7, 4, 7, 0, 0, 0, hoof)
                .part("legR", null, 4, 18, 0, 6, 18, 6, 0, -9, 0, lav).walk(0.9f, P).part("hoofR", "legR", 0, -16, 0, 7, 4, 7, 0, 0, 0, hoof)
                .part("body", null, 0, 18, 0, 14, 16, 10, 0, 8, 0, lav).sway(0.03f, 0.1f, 0, 2)
                .part("crystal", "body", 0, 9, -5.2f, 4, 4, 1, 0, 0, 0, 0xc040ff).glow()
                .part("crack", "body", -3, 5, -5.2f, 1, 8, 1, 0, 0, 0, BLACK)
                .part("tail", "body", 0, 6, 5, 4, 16, 4, 0, -6, 2, mane).sway(0.2f, 0.12f, 0, 2).part("tailStreak", "tail", 0, -6, 2.2f, 2, 14, 0.5f, 0, 0, 0, BLOOD)
                .part("head", "body", 0, 16, 0, 12, 12, 12, 0, 6, -1, lav).sway(0.06f, 2.0f, 0, 2)
                .part("snout", "head", 0, 3, -7, 8, 6, 5, 0, 0, -2, snout)
                .part("nostrilL", "snout", -2, 1, -4.6f, 1.5f, 1.5f, 0.5f, 0, 0, 0, BLACK).part("nostrilR", "snout", 2, 1, -4.6f, 1.5f, 1.5f, 0.5f, 0, 0, 0, BLACK)
                .part("jaw", "head", 0, 0.5f, -6, 7, 2, 5, 0, -1, -2, 0x1a0508)
                .part("horn", "head", 0, 12, -3, 3, 8, 3, 0, 4, 0, horn).glow().part("hornTip", "horn", 0, 8, 0, 2, 4, 2, 0, 2, 0, horn).glow()
                .part("hornCrack", "horn", 0, 4, -1.6f, 0.6f, 6, 0.6f, 0, 0, 0, GLOW_RED).glow()
                .part("earL", "head", -4.5f, 12, 1, 3, 5, 2, 0, 2.5f, 0, lav).part("earR", "head", 4.5f, 12, 1, 3, 5, 2, 0, 2.5f, 0, lav)
                .part("maneA", "head", 0, 10, 5, 3, 12, 6, 0, -2, 0, mane).sway(0.08f, 0.2f, 0, 2).part("maneS", "maneA", 0, -2, 0, 3.2f, 10, 1.5f, 0, 0, 0, BLOOD)
                .part("maneB", "body", 0, 12, 4.5f, 3, 8, 3, 0, 0, 0, mane)
                .part("sockL", "head", -3.5f, 7, -7.2f, 3.5f, 4, 1, 0, 0, 0, BLACK).part("sockR", "head", 3.5f, 7, -7.2f, 3.5f, 4, 1, 0, 0, 0, BLACK)
                .part("eyeL", "sockL", 0, 0, -0.5f, 1.2f, 1.2f, 0.5f, 0, 0, 0, GLOW_RED).glow().part("eyeR", "sockR", 0, 0, -0.5f, 1.2f, 1.2f, 0.5f, 0, 0, 0, GLOW_RED).glow()
                .part("tearL", "sockL", 0, -3.5f, 0, 1, 3, 0.5f, 0, 0, 0, BLACK)
                .part("armL", "body", -9.5f, 14, 0, 5, 18, 5, 0, -9, 0, lav).sway(0.04f, 0.1f, 0, 2).part("handL", "armL", 0, -18, 0, 6, 3, 6, 0, 0, 0, hoof)
                .part("armR", "body", 9.5f, 14, 0, 5, 18, 5, 0, -9, 0, lav).sway(0.04f, 0.1f, P, 2).part("handR", "armR", 0, -18, 0, 6, 3, 6, 0, 0, 0, hoof);
        row(d, "jaw", "tooth", 3, -2, 0.2f, -4.6f, 2, 1, 1.4f, 0.6f, BONE);
        xStitch(d, "body", "xs", 4, 4, -5.3f);
        return d
                .atk(Atk.CHARGE, 10, 13f, 4f, 160).anim((p, e, w, s, r, age) -> {
                    // paws the ground, lowers horn and gallops
                    p.rot("head", -0.2f, 0, 0, 1.1f, 0, 0, w, s, r); p.rot("body", 0.1f, 0, 0, -0.8f, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, -3, -4, w, s, r);
                    p.rot("armL", 0.2f, 0, 0, 1.3f, 0, 0, w, s, r); p.rot("armR", 0.2f, 0, 0, 1.3f, 0, 0, w, s, r);
                    if (w < 1) p.add("legR", Mth.sin(age * 1.2f) * 0.7f * w, 0, 0);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.3f); p.add("armL", q * 0.7f, 0, 0); p.add("armR", q * 0.7f, 0, 0); p.g("legL")[0] = -q; p.g("legR")[0] = -q * 0.8f; p.add("maneA", 0.4f, 0, 0); }
                })
                .atk(Atk.SHOOT, 6, 14f, 3f, 70, 0xc040ff, "blind").anim((p, e, w, s, r, age) -> {
                    // horn charges (swells), head snaps forward to fire
                    p.scl("horn", 1.3f, 1.2f, 1.3f, 1, 1, 1, w, s, r); p.rot("head", -0.6f, 0, 0, 0.7f, 0, 0, w, s, r); p.rot("body", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r);
                    p.rot("armL", 0.3f, 0, -0.5f, 0, 0, -0.2f, w, s, r); p.rot("armR", 0.3f, 0, 0.5f, 0, 0, 0.2f, w, s, r); glitch(p, age, s, r);
                })
                .atk(Atk.STOMP, 9, 4.5f, 0, 90).anim((p, e, w, s, r, age) -> {
                    // rear up whinnying, hooves crash down
                    p.rot("body", 0.8f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 3, 3, 0, -3, -3, w, s, r);
                    p.rot("armL", 2.5f, 0, -0.2f, 0.9f, 0, 0, w, s, r); p.rot("armR", 2.2f, 0, 0.2f, 0.9f, 0, 0, w, s, r);
                    p.rot("head", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("jaw", -0.7f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("tail", 0.6f, 0, 0, -0.2f, 0, 0, w, s, r);
                })
                .atk(Atk.SWIPE, 7, 3f, 0, 28).anim((p, e, w, s, r, age) -> {
                    // horn slash: head hooks sideways
                    p.rot("head", 0.3f, -0.9f, -0.4f, -0.3f, 0.9f, 0.4f, w, s, r); p.rot("body", 0, -0.5f, 0, 0, 0.6f, 0, w, s, r);
                    p.rot("armR", 1.2f, 0, 0, 0.4f, 0, 0.6f, w, s, r); p.rot("legL", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r);
                })
                // Prism burst: horn flares and sprays a ring of shards.
                .custom(4, 6f, 0, 200, 16, 4, 12, (m, sp, t) -> {
                    if (t == 17 && m.level() instanceof ServerLevel lv) { m.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 2f, 0.6f);
                        for (int i = 0; i < 10; i++) { double a = i / 10.0 * Mth.TWO_PI; PoppyBolt b = new PoppyBolt(lv, m, sp.dmg(), i % 2 == 0 ? 0xc040ff : GLOW_RED, "blind");
                            b.setPos(m.getX(), m.getEyeY() + 0.4, m.getZ()); b.shoot(Math.cos(a), 0.05, Math.sin(a), 0.9f, 0); lv.addFreshEntity(b); } }
                    if (t < 16 && t % 3 == 0 && m.level() instanceof ServerLevel lv) lv.sendParticles(ParticleTypes.WITCH, m.getX(), m.getEyeY() + 0.7, m.getZ(), 4, .2, .2, .2, 0);
                }, (p, e, w, s, r, age) -> {
                    p.rot("head", 0.5f, 0, 0, -0.7f, 0, 0, w, s, r); p.scl("horn", 1, 1, 1, 1.5f, 1.4f, 1.5f, w, s, r); p.rot("body", 0.3f, 0, 0, 0.2f, 0, 0, w, s, r);
                    p.rot("armL", 0.2f, 0, -0.3f, 0.6f, 0, -1.4f, w, s, r); p.rot("armR", 0.2f, 0, 0.3f, 0.6f, 0, 1.4f, w, s, r);
                    if (w < 1) p.add("head", 0, Mth.sin(age * 4) * 0.1f * w, 0); glitch(p, age, s, r);
                });
    }

    // =====================================================================
    /** Trough Hog – bloated dirty pig plush with a stitched belly gash, tusks and a wide maw. */
    static CreatureDef troughHog() {
        int pink = 0xb47a80, dark = 0x7a4a50, snout = 0xd49aa0, mud = 0x4a3020;
        CreatureDef d = CreatureDef.of("trough_hog", "Trough Hog", "トラフホッグ").size(1.6f, 2.8f).hp(190).dmg(10).speed(0.3).kbRes(0.7).egg(pink, mud).spawn(1)
                .part("legL", null, -5, 14, 0, 8, 14, 8, 0, -7, 0, pink).walk(0.8f, 0).part("trotL", "legL", 0, -12.5f, 0, 8.4f, 3, 9, 0, 0, -0.3f, dark)
                .part("legR", null, 5, 14, 0, 8, 14, 8, 0, -7, 0, pink).walk(0.8f, P).part("trotR", "legR", 0, -12.5f, 0, 8.4f, 3, 9, 0, 0, -0.3f, dark)
                .part("body", null, 0, 14, 0, 22, 20, 16, 0, 10, 0, pink).sway(0.03f, 0.08f, 0, 2)
                .part("belly", "body", 0, 8, -8.2f, 16, 12, 1, 0, 0, 0, snout)
                .part("gash", "belly", 0, 0, -0.6f, 12, 2, 0.5f, 0, 0, 0, BLOOD)
                .part("mud0", "body", 8, 15, -8.1f, 4, 3, 1, 0, 0, 0, mud).part("mud1", "body", -9, 3, -8.1f, 3, 4, 1, 0, 0, 0, mud).part("mud2", "body", 5, 3, 8.1f, 6, 4, 1, 0, 0, 0, mud)
                .part("head", "body", 0, 20, 0, 16, 13, 13, 0, 6, -2, pink).sway(0.06f, 1.4f, 0, 2)
                .part("snout", "head", 0, 5, -8.5f, 8, 6, 3, 0, 0, -1.5f, snout)
                .part("nostL", "snout", -2, 0, -3.2f, 2, 2, 0.6f, 0, 0, 0, BLACK).part("nostR", "snout", 2, 0, -3.2f, 2, 2, 0.6f, 0, 0, 0, BLACK)
                .part("jaw", "head", 0, 1.5f, -6, 12, 3, 6, 0, -1.5f, -2.5f, 0x2a0508)
                .part("tuskL", "jaw", -5, 0, -5, 1.5f, 3, 1.5f, 0, 1.5f, 0, BONE).part("tuskR", "jaw", 5, 0, -5, 1.5f, 3, 1.5f, 0, 1.5f, 0, BONE)
                .part("earL", "head", -6, 13, -1, 5, 5, 2, -1, 1.5f, 0, dark).sway(0.2f, 0.25f, 0, 0).part("earR", "head", 6, 13, -1, 5, 5, 2, 1, 1.5f, 0, dark).sway(0.2f, 0.25f, P, 0)
                .part("armL", "body", -12.5f, 17, 0, 6, 16, 6, 0, -8, 0, pink).sway(0.03f, 0.08f, 0, 2).part("handL", "armL", 0, -16, 0, 6.4f, 3, 6.4f, 0, 0, 0, dark)
                .part("armR", "body", 12.5f, 17, 0, 6, 16, 6, 0, -8, 0, pink).sway(0.03f, 0.08f, P, 2).part("handR", "armR", 0, -16, 0, 6.4f, 3, 6.4f, 0, 0, 0, dark)
                .part("tail", "body", 0, 12, 8, 2, 2, 4, 0, 0, 2, dark).sway(0.6f, 0.5f, 0, 2);
        eyes(d, "head", 4.5f, 9, -8.7f, 2.5f, GLOW_RED, BLACK);
        row(d, "head", "tooth", 5, -4, 2.5f, -8.8f, 2, 1.2f, 1.6f, 0.8f, BONE);
        row(d, "gash", "st", 4, -4.5f, 0, -0.4f, 3, 0.6f, 3.5f, 0.4f, THREAD);
        return d
                // Gorge: lunging bite that heals the hog.
                .custom(9, 3f, 0, 50, 12, 4, 12, (m, sp, t) -> strike(m, sp, t, 13, 0.3, 1f, 0.1f, e -> { m.heal(sp.dmg() * 0.6f); e.setDeltaMovement(Fx.flat(m).scale(-0.4)); e.hurtMarked = true;
                    if (m.level() instanceof ServerLevel lv) lv.sendParticles(ParticleTypes.HEART, m.getX(), m.getEyeY() + 0.6, m.getZ(), 2, .3, .2, .3, 0); }),
                        (p, e, w, s, r, age) -> {
                            p.rot("jaw", -1.2f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("head", 0.7f, 0, 0, -0.4f, 0, 0, w, s, r); p.pos("head", 0, 1, 2, 0, -2, -6, w, s, r);
                            p.rot("body", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, -5, w, s, r);
                            p.rot("armL", 0.3f, 0, -0.7f, 1.3f, 0, 0.2f, w, s, r); p.rot("armR", 0.3f, 0, 0.7f, 1.3f, 0, -0.2f, w, s, r);
                            if (r > 0 && r < 0.6f) p.add("jaw", Mth.sin(age * 2.5f) * 0.25f, 0, 0); // chewing
                        })
                .atk(Atk.CHARGE, 10, 10f, 3f, 150).anim((p, e, w, s, r, age) -> {
                    // belly-first bulldozer
                    p.rot("body", 0.2f, 0, 0, 0.35f, 0, 0, w, s, r); p.scl("body", 1, 1, 1, 1.12f, 1.0f, 1.18f, w, s, r); p.rot("head", 0.4f, 0, 0, 0.1f, 0, 0, w, s, r);
                    p.rot("armL", 0.4f, 0, -1.0f, -0.6f, 0, -0.5f, w, s, r); p.rot("armR", 0.4f, 0, 1.0f, -0.6f, 0, 0.5f, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.5f); p.g("legL")[0] = q; p.g("legR")[0] = -q; p.g("body")[4] += Math.abs(q) * 1.5f; }
                })
                .atk(Atk.SLAM, 12, 4.5f, 0, 120).anim((p, e, w, s, r, age) -> {
                    // hops up and belly-flops
                    p.pos("body", 0, -4, 0, 0, 0, -8, w, s, r); p.pos("legL", 0, -2, 0, 0, 0, -6, w, s, r); p.pos("legR", 0, -2, 0, 0, 0, -6, w, s, r);
                    if (w >= 1 && s <= 0) { p.g("body")[4] += 10; } // airborne apex at end of windup
                    p.rot("body", 0.4f, 0, 0, -1.1f, 0, 0, w, s, r); p.rot("armL", 3.0f, 0, -0.4f, 0.5f, 0, -1.2f, w, s, r); p.rot("armR", 3.0f, 0, 0.4f, 0.5f, 0, 1.2f, w, s, r);
                    p.rot("legL", -0.5f, 0, 0, -1.0f, 0, 0, w, s, r); p.rot("legR", -0.5f, 0, 0, -1.0f, 0, 0, w, s, r); p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r);
                    p.scl("body", 0.95f, 1.08f, 0.95f, 1.15f, 0.85f, 1.15f, w, s, r);
                })
                .atk(Atk.GAS, 3, 6f, 0, 220, 1, "").anim((p, e, w, s, r, age) -> {
                    // retches slop: belly heaves, then maw gapes forward
                    p.scl("body", 1.05f, 0.95f, 1.1f, 1.0f, 1.05f, 0.95f, w, s, r); p.rot("body", -0.2f, 0, 0, -0.45f, 0, 0, w, s, r);
                    p.rot("head", -0.3f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -1.2f, 0, 0, w, s, r);
                    p.rot("armL", 0.6f, 0, -0.2f, 0.2f, 0, -0.6f, w, s, r); p.rot("armR", 0.6f, 0, 0.2f, 0.2f, 0, 0.6f, w, s, r);
                    if (w < 1) p.g("body")[7] *= 1 + Mth.sin(age * 1.8f) * 0.05f; glitch(p, age, s, r);
                });
    }

    // =====================================================================
    /** Gloom Cub – towering ash-grey bear plush, X-stitched seams, one button eye, crushing embrace. */
    static CreatureDef gloomCub() {
        int fur = 0x4b4f5e, dark = 0x2e313c, muz = 0x8a8070;
        CreatureDef d = CreatureDef.of("gloom_cub", "Gloom Cub", "グルームカブ").size(1.5f, 3.4f).hp(210).dmg(10).speed(0.31).kbRes(0.7).egg(fur, BLOOD).spawn(1)
                .part("legL", null, -5, 20, 0, 8, 20, 8, 0, -10, 0, fur).walk(0.8f, 0).part("soleL", "legL", 0, -18, -4.2f, 6, 4, 0.6f, 0, 0, 0, BLOOD)
                .part("legR", null, 5, 20, 0, 8, 20, 8, 0, -10, 0, fur).walk(0.8f, P).part("soleR", "legR", 0, -18, -4.2f, 6, 4, 0.6f, 0, 0, 0, BLOOD)
                .part("body", null, 0, 20, 0, 20, 22, 14, 0, 11, 0, fur).sway(0.03f, 0.08f, 0, 2)
                .part("belly", "body", 0, 9, -7.2f, 12, 14, 1, 0, 0, 0, muz)
                .part("patch", "body", 6, 16, -7.2f, 5, 5, 1, 0, 0, 0, 0x6a1018)
                .part("head", "body", 0, 22, 0, 18, 15, 14, 0, 7.5f, -1, fur).sway(0.06f, 1.7f, 0, 2)
                .part("earL", "head", -8, 15, 0, 5, 5, 3, 0, 2, 0, fur).part("earLi", "earL", 0, 2, -1.6f, 3, 3, 0.5f, 0, 0, 0, BLOOD)
                .part("earR", "head", 8, 15, 0, 5, 5, 3, 0, 2, 0, fur).part("stuffing", "earR", 1.5f, 4.5f, 0, 2.5f, 2, 2.5f, 0, 0, 0, 0xe8e4dc)
                .part("muzzle", "head", 0, 4, -9, 9, 6, 4, 0, 0, 0, muz)
                .part("nose", "muzzle", 0, 2, -2.3f, 4, 2, 1, 0, 0, 0, BLACK)
                .part("jaw", "head", 0, 1, -7, 9, 2.5f, 6, 0, -1, -3, 0x2a0508)
                .part("eyeL", "head", -4.5f, 10, -8.2f, 3.5f, 3.5f, 1, 0, 0, 0, GLOW_RED).glow()
                .part("button", "head", 4.5f, 10, -8.2f, 4, 4, 1, 0, 0, 0, BLACK)
                .part("armL", "body", -12.5f, 20, 0, 7, 22, 7, 0, -11, 0, fur).sway(0.03f, 0.08f, 0, 2)
                .part("armR", "body", 12.5f, 20, 0, 7, 22, 7, 0, -11, 0, fur).sway(0.03f, 0.08f, P, 2);
        row(d, "muzzle", "fang", 4, -3, -2.8f, -2.3f, 2, 1, 2, 0.8f, BONE);
        row(d, "button", "thread", 2, -0.7f, 0, -0.6f, 1.4f, 0.5f, 2.6f, 0.3f, THREAD);
        row(d, "armL", "clawL", 3, -2, -22.5f, -2.5f, 2, 1, 3, 1, BONE);
        row(d, "armR", "clawR", 3, -2, -22.5f, -2.5f, 2, 1, 3, 1, BONE);
        xStitch(d, "belly", "xs0", -2.5f, 3, -0.6f); xStitch(d, "belly", "xs1", 2.5f, -3, -0.6f);
        xStitch(d, "head", "xs2", -5, 14, -7.2f);
        return d
                .atk(Atk.GRAB, 6, 3.2f, 0, 150, 60, "").anim((p, e, w, s, r, age) -> {
                    // arms open wide, then wrap shut; squeezes and rocks back lifting the victim
                    p.rot("armL", 1.3f, 0, -1.4f, 1.5f, 0.6f, 0.5f, w, s, r); p.rot("armR", 1.3f, 0, 1.4f, 1.5f, -0.6f, -0.5f, w, s, r);
                    p.rot("body", 0.25f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, -5, w, s, r); p.rot("head", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r);
                    p.rot("jaw", -0.6f, 0, 0, -0.2f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(age * 0.7f); p.add("armL", 0, q * 0.15f, q * 0.15f); p.add("armR", 0, -q * 0.15f, -q * 0.15f); p.add("body", 0.25f + q * 0.1f, 0, 0); p.add("head", 0, Mth.sin(age * 6) * 0.06f, 0); }
                })
                .atk(Atk.SLAM, 11, 4.5f, 0, 100).anim((p, e, w, s, r, age) -> {
                    // double-fist hammer: rises on toes, both paws overhead
                    p.rot("armL", 3.0f, 0.2f, -0.2f, 0.4f, 0, -0.05f, w, s, r); p.rot("armR", 3.0f, -0.2f, 0.2f, 0.4f, 0, 0.05f, w, s, r);
                    p.rot("body", 0.4f, 0, 0, -0.8f, 0, 0, w, s, r); p.pos("body", 0, 3, 3, 0, -5, -6, w, s, r); p.rot("head", 0.5f, 0, 0, -0.5f, 0, 0, w, s, r);
                    p.rot("jaw", -0.8f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("legL", 0.2f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("legR", -0.2f, 0, 0, 0.4f, 0, 0, w, s, r);
                })
                .atk(Atk.SWIPE, 9, 3.8f, 0, 30).anim((p, e, w, s, r, age) -> {
                    // backhand rake with the left, weight thrown through
                    p.rot("armL", 1.4f, 1.4f, -0.2f, 1.4f, -1.4f, 0, w, s, r); p.rot("armR", 0.4f, 0, 0.5f, 0.2f, 0, 0.3f, w, s, r);
                    p.rot("body", 0.05f, 0.8f, 0, -0.15f, -0.8f, 0, w, s, r); p.rot("head", 0, -0.4f, 0, 0, 0.5f, 0, w, s, r);
                    p.pos("body", 0, 0, 3, 0, 0, -4, w, s, r); p.rot("legR", -0.4f, 0, 0, 0.4f, 0, 0, w, s, r);
                })
                .atk(Atk.SCREAM, 3, 9f, 2f, 240).anim((p, e, w, s, r, age) -> {
                    // stands tall and roars, arms flung wide
                    p.rot("body", 0.3f, 0, 0, 0.3f, 0, 0, w, s, r); p.scl("body", 1, 1, 1, 1.1f, 1.06f, 1.1f, w, s, r);
                    p.rot("head", 0.6f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -1.2f, 0, 0, w, s, r);
                    p.rot("armL", 0.6f, 0, -0.3f, 0.6f, 0, -1.5f, w, s, r); p.rot("armR", 0.6f, 0, 0.3f, 0.6f, 0, 1.5f, w, s, r); glitch(p, age, s, r);
                })
                .atk(Atk.CHARGE, 10, 11f, 4f, 200).anim((p, e, w, s, r, age) -> {
                    // lumbering arms-out tackle-hug
                    p.rot("body", -0.1f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("armL", 0.4f, 0, -1.2f, 1.4f, 0, -0.6f, w, s, r); p.rot("armR", 0.4f, 0, 1.2f, 1.4f, 0, 0.6f, w, s, r);
                    p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.0f); p.g("legL")[0] = q; p.g("legR")[0] = -q; p.add("body", 0, 0, q * 0.08f); }
                });
    }

    private BatchD() {}
}
