package com.poppy.mod.creature;

import com.poppy.mod.client.Pose;
import com.poppy.mod.entity.Atk;
import com.poppy.mod.entity.Fx;
import com.poppy.mod.entity.PoppyBolt;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** Batch A – canon Poppy Playtime roster (see DEV_GUIDE.md). */
public final class BatchA {
    static final float P = (float) Math.PI;

    public static List<CreatureDef> all() {
        List<CreatureDef> l = new ArrayList<>();
        l.add(snuggleScamp());
        l.add(ribbonRosie());
        l.add(tamboHare());
        l.add(discoDroid());
        l.add(crateCuddlers());
        l.add(lanternMoth());
        l.add(quiltPhantom());
        l.add(bubbleBarb());
        l.add(longarmSweeper());
        return l;
    }

    // ---------------------------------------------------------------- helpers
    /** Mirror-keyed pose on an L/R pair (ry, rz mirrored for the L side, like Anim.arms). */
    static void pair(Pose p, String l, String r, float ax, float ay, float az, float bx, float by, float bz, float w, float s, float rc) {
        p.rot(r, ax, ay, az, bx, by, bz, w, s, rc); p.rot(l, ax, -ay, -az, bx, -by, -bz, w, s, rc);
    }
    static boolean striking(float s, float r) { return s > 0 && r <= 0; }

    interface AOn { void on(PoppyMob m, LivingEntity e); }
    static AOn fx(MobEffect eff, int dur, int amp) { return (m, e) -> e.addEffect(new MobEffectInstance(eff, dur, amp)); }
    static AOn both(AOn a, AOn b) { return (m, e) -> { a.on(m, e); b.on(m, e); }; }

    /** Area/cone hit. dot <= -1 = all around (radial knockback), else cone (forward knockback). every <= 0: one hit at strike start; else pulse every n ticks during strike. */
    static AttackSpec.Custom blast(double dot, float kb, AOn extra, ParticleOptions pt, SoundEvent snd, int every) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv)) return;
            int hit = s.wind() + 1;
            if (every <= 0 ? t != hit : (t < hit || t >= hit + s.strike() || (t - hit) % every != 0)) return;
            m.playSound(snd, 1.3f, 0.8f + m.getRandom().nextFloat() * 0.4f);
            Vec3 look = Fx.flat(m);
            List<LivingEntity> es = dot <= -1 ? Fx.foes(m, m.position(), s.reach()) : Fx.arc(m, s.reach(), dot);
            for (LivingEntity e : es) {
                if (every > 0) e.invulnerableTime = 0;
                Fx.hit(m, e, s.dmg(), kb, dot <= -1 ? e.position().subtract(m.position()) : look);
                if (extra != null) extra.on(m, e);
            }
            if (pt != null) for (int i = 0; i < 24; i++) {
                double a = i / 24.0 * Mth.TWO_PI, rr = s.reach() * 0.8; Vec3 d = new Vec3(Math.cos(a), 0, Math.sin(a));
                if (dot > -1 && d.dot(look) < dot) continue;
                lv.sendParticles(pt, m.getX() + d.x * rr, m.getY() + m.getBbHeight() * 0.4, m.getZ() + d.z * rr, 1, 0.1, 0.1, 0.1, 0.01);
            }
        };
    }

    /** Fan of bolts at the target; every > 0 repeats the fan during the strike window. */
    static AttackSpec.Custom volley(int count, float spread, int color, String style, int every) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv) || m.getTarget() == null) return;
            int hit = s.wind() + 1;
            if (every <= 0 ? t != hit : (t < hit || t >= hit + s.strike() || (t - hit) % every != 0)) return;
            m.playSound(SoundEvents.SNOWBALL_THROW, 1.2f, 0.6f + m.getRandom().nextFloat() * 0.3f);
            Vec3 o = m.getEyePosition(), d = Fx.aim(m);
            for (int i = 0; i < count; i++) {
                Vec3 dd = d.yRot(count == 1 ? 0 : (i - (count - 1) / 2f) * spread);
                PoppyBolt b = new PoppyBolt(lv, m, s.dmg(), color, style); b.setPos(o.x, o.y - 0.2, o.z); b.shoot(dd.x, dd.y, dd.z, 1.2f, 2f); lv.addFreshEntity(b);
            }
        };
    }
    static DustParticleOptions dust(int rgb, float size) { return new DustParticleOptions(new Vector3f((rgb >> 16 & 255) / 255f, (rgb >> 8 & 255) / 255f, (rgb & 255) / 255f), size); }

    // ---------------------------------------------------------------- 1. Snuggle Scamp – tiny swarm plush
    static CreatureDef snuggleScamp() {
        int teal = 0x2bb5a8, tealD = 0x1d8076, pink = 0xf7c6d9, pinkD = 0xd986a6, mouth = 0x2a0a14, white = 0xfbf8ee, black = 0x111111;
        return CreatureDef.of("mini_huggy", "Mini Huggy", "ミニハギー").size(0.6f, 1.0f).hp(24).dmg(3).speed(0.36).kbRes(0).egg(teal, pink).spawn(3)
                .part("legL", null, -2.5f, 4, 0, 3, 4, 3, 0, -2, 0, tealD).walk(1.1f, 0)
                .part("footL", "legL", 0, -4, -0.5f, 3.4f, 1.2f, 4, 0, 0.6f, -0.5f, pink)
                .part("legR", null, 2.5f, 4, 0, 3, 4, 3, 0, -2, 0, tealD).walk(1.1f, P)
                .part("footR", "legR", 0, -4, -0.5f, 3.4f, 1.2f, 4, 0, 0.6f, -0.5f, pink)
                .part("body", null, 0, 4, 0, 8, 7, 6, 0, 3.5f, 0, teal).sway(0.05f, 0.2f, 0, 2)
                .part("belly", "body", 0, 3.5f, -3.1f, 5, 5, 0.6f, 0, 0, 0, pink)
                .part("bellySeam", "body", 0, 3.5f, -3.45f, 0.5f, 4, 0.3f, 0, 0, 0, pinkD)
                .part("tail", "body", 0, 2, 3, 2, 2, 2, 0, 0, 1, pink).sway(0.4f, 0.3f, 0, 1)
                .part("head", "body", 0, 7, 0, 10, 8, 8, 0, 4, 0, teal)
                .part("muzzle", "head", 0, 2.5f, -4.2f, 6, 3, 1, 0, 0, 0, pink)
                .part("jaw", "head", 0, 1.5f, -4, 5, 1.5f, 2, 0, -0.75f, -0.5f, mouth)
                .part("tooth1", "jaw", -1.5f, 0, -1.4f, 0.8f, 1, 0.4f, 0, -0.3f, 0, white)
                .part("tooth2", "jaw", 0, 0, -1.4f, 0.8f, 1.2f, 0.4f, 0, -0.3f, 0, white)
                .part("tooth3", "jaw", 1.5f, 0, -1.4f, 0.8f, 1, 0.4f, 0, -0.3f, 0, white)
                .part("nose", "head", 0, 4.2f, -4.5f, 2, 1.2f, 0.8f, 0, 0, 0, 0x163b3a)
                .part("eyeL", "head", -2.5f, 5.5f, -4.2f, 2.4f, 2.4f, 0.6f, 0, 0, 0, black)
                .part("glintL", "eyeL", 0.5f, 0.5f, -0.35f, 0.8f, 0.8f, 0.2f, 0, 0, 0, white).glow()
                .part("eyeR", "head", 2.5f, 5.5f, -4.2f, 2.4f, 2.4f, 0.6f, 0, 0, 0, black)
                .part("glintR", "eyeR", 0.5f, 0.5f, -0.35f, 0.8f, 0.8f, 0.2f, 0, 0, 0, white).glow()
                .part("browL", "head", -2.5f, 7.3f, -4.1f, 2.6f, 0.6f, 0.5f, 0, 0, 0, tealD)
                .part("browR", "head", 2.5f, 7.3f, -4.1f, 2.6f, 0.6f, 0.5f, 0, 0, 0, tealD)
                .part("cheekL", "head", -3.8f, 3.5f, -4.1f, 1.6f, 1, 0.3f, 0, 0, 0, 0xf08aa8)
                .part("cheekR", "head", 3.8f, 3.5f, -4.1f, 1.6f, 1, 0.3f, 0, 0, 0, 0xf08aa8)
                .part("earL", "head", -4.5f, 8, 0, 2, 3, 2, 0, 1.5f, 0, teal).sway(0.12f, 0.25f, 0, 2)
                .part("earTipL", "earL", 0, 3, 0, 1, 1.5f, 1, 0, 0.75f, 0, pink)
                .part("earR", "head", 4.5f, 8, 0, 2, 3, 2, 0, 1.5f, 0, teal).sway(0.12f, 0.25f, P, 2)
                .part("earTipR", "earR", 0, 3, 0, 1, 1.5f, 1, 0, 0.75f, 0, pink)
                .part("tuft1", "head", -1, 8, -1, 1.2f, 2, 1.2f, 0, 1, 0, tealD)
                .part("tuft2", "head", 0.5f, 8, 0.5f, 1.2f, 2.6f, 1.2f, 0, 1.3f, 0, tealD)
                .part("tuft3", "head", 1.6f, 8, -0.5f, 1.2f, 1.6f, 1.2f, 0, 0.8f, 0, tealD)
                .part("armL", "body", -4.5f, 6, 0, 2.4f, 4.5f, 2.4f, 0, -2.25f, 0, teal).walk(0.8f, P)
                .part("pawL", "armL", 0, -4.5f, 0, 2.8f, 1.6f, 2.8f, 0, -0.4f, 0, pink)
                .part("armR", "body", 4.5f, 6, 0, 2.4f, 4.5f, 2.4f, 0, -2.25f, 0, teal).walk(0.8f, 0)
                .part("pawR", "armR", 0, -4.5f, 0, 2.8f, 1.6f, 2.8f, 0, -0.4f, 0, pink)
                // Nibble: rear back with jaw wide, then snap forward
                .atk(Atk.BITE, 3.5f, 1.6f, 0, 25).anim((p, e, w, s, r, a) -> {
                    p.rot("body", 0.35f, 0, 0, -0.6f, 0, 0, w, s, r); p.pos("body", 0, -1, 2, 0, 0, -4, w, s, r);
                    p.rot("head", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("jaw", -0.9f, 0, 0, 0.1f, 0, 0, w, s, r);
                    pair(p, "armL", "armR", -0.6f, 0, 0.5f, 1.4f, 0, -0.3f, w, s, r); p.rot("tail", 0, 0.6f, 0, 0, -0.6f, 0, w, s, r);
                })
                // Tumble pounce: crouch, then a rolling somersault at the target
                .atk(Atk.POUNCE, 4, 6, 2.5f, 80).anim((p, e, w, s, r, a) -> {
                    p.pos("body", 0, -2, 1, 0, 2, -4, w, s, r); p.rot("body", 0.4f, 0, 0, -0.9f, 0, 0, w, s, r);
                    pair(p, "legL", "legR", 0.9f, 0, 0, -0.6f, 0, 0, w, s, r); pair(p, "armL", "armR", -0.8f, 0, 0.3f, 2.6f, 0, 0.4f, w, s, r);
                    pair(p, "earL", "earR", 0.2f, 0, 0, 0.9f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.8f, 0, 0, w, s, r);
                    if (striking(s, r)) p.g("body")[0] -= Pose.ease(s) * P * 2;
                })
                // Cling & chew: wraps paws round the target and gnaws (rapid small hits + slowness)
                .custom(1.5f, 1.8f, 0, 110, 8, 24, 8, blast(0.2, 0, fx(MobEffects.MOVEMENT_SLOWDOWN, 30, 2), ParticleTypes.CRIT, SoundEvents.FOX_BITE, 4), (p, e, w, s, r, a) -> {
                    p.rot("body", 0.1f, 0, 0, -0.5f, 0, 0, w, s, r); p.pos("body", 0, 0, 1, 0, 1, -4, w, s, r);
                    pair(p, "armL", "armR", 1.0f, 0, 0.8f, 1.6f, 0, -0.6f, w, s, r); pair(p, "legL", "legR", 0, 0, 0, 0.7f, 0, 0, w, s, r);
                    p.rot("jaw", -0.6f, 0, 0, 0, 0, 0, w, s, r);
                    if (striking(s, r)) { p.g("jaw")[0] = -0.4f - 0.4f * Mth.sin(a * 2.4f); p.add("head", 0.15f * Mth.sin(a * 2.4f), 0.2f * Mth.sin(a * 1.2f), 0); }
                })
                // Squeak call: hops on tiptoes waving for friends
                .atk(Atk.SUMMON, 0, 10, 0, 420, 1, "mini_huggy").anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 2.8f, 0, 0.4f, 2.9f, 0, 0.9f, w, s, r); p.rot("head", 0.5f, 0, 0, 0.7f, 0, 0, w, s, r);
                    p.rot("jaw", -0.8f, 0, 0, -1.0f, 0, 0, w, s, r); p.pos("body", 0, 1, 0, 0, 2.5f, 0, w, s, r);
                    if (striking(s, r) || w > 0.5f) { p.add("armL", 0, 0, 0.4f * Mth.sin(a * 1.8f)); p.add("armR", 0, 0, -0.4f * Mth.sin(a * 1.8f)); }
                });
    }

    // ---------------------------------------------------------------- 2. Ribbon Rosie – tall lanky lavender plush with a bow
    static CreatureDef ribbonRosie() {
        int lav = 0xb58ae6, lavD = 0x7d55b8, cream = 0xf3e3f7, bow = 0x3cc8c0, bowD = 0x2a9790, mouth = 0x1e0616, teeth = 0xf4f0e6, hand = 0xf6c0dc;
        return CreatureDef.of("miss_delight", "Miss Delight", "ミス・ディライト").size(1.0f, 3.1f).hp(110).dmg(7).speed(0.32).kbRes(0.5).egg(lav, bow).spawn(1)
                .part("legL", null, -3.5f, 20, 0, 5, 20, 5, 0, -10, 0, lav).walk(0.8f, 0)
                .part("footL", "legL", 0, -20, -1, 6, 2, 7, 0, 1, 0, hand)
                .part("legR", null, 3.5f, 20, 0, 5, 20, 5, 0, -10, 0, lav).walk(0.8f, P)
                .part("footR", "legR", 0, -20, -1, 6, 2, 7, 0, 1, 0, hand)
                .part("body", null, 0, 20, 0, 14, 16, 8, 0, 8, 0, lav).sway(0.03f, 0.1f, 0, 2)
                .part("belly", "body", 0, 7, -4.1f, 9, 10, 0.6f, 0, 0, 0, cream)
                .part("seam", "body", 0, 7, -4.45f, 0.5f, 9, 0.2f, 0, 0, 0, lavD)
                .part("stitch1", "body", 0, 4, -4.5f, 2.5f, 0.5f, 0.3f, 0, 0, 0, lavD)
                .part("stitch2", "body", 0, 9, -4.5f, 2.5f, 0.5f, 0.3f, 0, 0, 0, lavD)
                .part("head", "body", 0, 16, 0, 16, 14, 12, 0, 7, 0, lav)
                .part("muzzle", "head", 0, 4, -6.3f, 10, 5, 1, 0, 0, 0, cream)
                .part("lips", "head", 0, 5, -6.9f, 10, 1, 0.6f, 0, 0, 0, 0xe0457a)
                .part("jaw", "head", 0, 3, -6.2f, 9, 3, 3, 0, -1.5f, -0.5f, mouth)
                .part("toothU1", "head", -3, 4.5f, -6.9f, 1.4f, 1.6f, 0.4f, 0, -0.8f, 0, teeth)
                .part("toothU2", "head", -1, 4.5f, -6.9f, 1.4f, 1.6f, 0.4f, 0, -0.8f, 0, teeth)
                .part("toothU3", "head", 1, 4.5f, -6.9f, 1.4f, 1.6f, 0.4f, 0, -0.8f, 0, teeth)
                .part("toothU4", "head", 3, 4.5f, -6.9f, 1.4f, 1.6f, 0.4f, 0, -0.8f, 0, teeth)
                .part("nose", "head", 0, 7.5f, -6.6f, 3, 2, 1, 0, 0, 0, 0x5a2f8f)
                .part("eyeL", "head", -4, 10, -6.1f, 4, 4, 0.6f, 0, 0, 0, 0xffffff)
                .part("pupilL", "eyeL", 0.4f, -0.3f, -0.35f, 2, 2.4f, 0.2f, 0, 0, 0, 0x1a1030)
                .part("lashL", "eyeL", 0, 2.4f, -0.3f, 4.6f, 0.8f, 0.3f, 0, 0, 0, 0x2b1640)
                .part("eyeR", "head", 4, 10, -6.1f, 4, 4, 0.6f, 0, 0, 0, 0xffffff)
                .part("pupilR", "eyeR", -0.4f, -0.3f, -0.35f, 2, 2.4f, 0.2f, 0, 0, 0, 0x1a1030)
                .part("lashR", "eyeR", 0, 2.4f, -0.3f, 4.6f, 0.8f, 0.3f, 0, 0, 0, 0x2b1640)
                .part("cheekL", "head", -6, 6, -6.1f, 2.4f, 1.2f, 0.4f, 0, 0, 0, 0xf07fb3)
                .part("cheekR", "head", 6, 6, -6.1f, 2.4f, 1.2f, 0.4f, 0, 0, 0, 0xf07fb3)
                .part("bowKnot", "head", 0, 14, -1, 3, 3, 3, 0, 0.5f, 0, bowD)
                .part("bowL", "head", -4, 14, -1, 5, 5, 2, 0, 1, 0, bow).sway(0.08f, 0.15f, 0, 2)
                .part("bowR", "head", 4, 14, -1, 5, 5, 2, 0, 1, 0, bow).sway(0.08f, 0.15f, P, 2)
                .part("bowTail", "head", 0, 13, 0.5f, 2, 5, 1, 0, -2.5f, 0, bowD).sway(0.1f, 0.12f, 0, 0)
                .part("armL", "body", -8.5f, 14, 0, 4, 30, 4, 0, -15, 0, lav).sway(0.05f, 0.1f, 0, 2)
                .part("handL", "armL", 0, -30, 0, 6, 5, 6, 0, -2.5f, 0, hand)
                .part("fingerL1", "handL", -1.5f, -5, -1.5f, 1.5f, 2, 1.5f, 0, -1, 0, hand)
                .part("fingerL2", "handL", 1.5f, -5, -1.5f, 1.5f, 2, 1.5f, 0, -1, 0, hand)
                .part("armR", "body", 8.5f, 14, 0, 4, 30, 4, 0, -15, 0, lav).sway(0.05f, 0.1f, P, 2)
                .part("handR", "armR", 0, -30, 0, 6, 5, 6, 0, -2.5f, 0, hand)
                .part("fingerR1", "handR", -1.5f, -5, -1.5f, 1.5f, 2, 1.5f, 0, -1, 0, hand)
                .part("fingerR2", "handR", 1.5f, -5, -1.5f, 1.5f, 2, 1.5f, 0, -1, 0, hand)
                // Smother hug: arms flung wide, then a crushing wrap with a rocking squeeze
                .atk(Atk.GRAB, 6, 3, 0, 140, 40, "").anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 1.2f, 0, 1.3f, 1.5f, 0, -0.55f, w, s, r); p.rot("body", 0.25f, 0, 0, -0.4f, 0, 0, w, s, r);
                    p.rot("head", 0.3f, 0, 0.15f, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, -5, w, s, r); p.rot("jaw", -0.4f, 0, 0, -0.8f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(a * 0.5f); p.add("armL", 0, 0, 0.12f * q); p.add("armR", 0, 0, -0.12f * q); p.add("body", 0, 0, 0.08f * q); p.add("head", 0, 0, -0.12f * q); }
                })
                // Ribbon lash: right arm coils high behind, snaps out stretched
                .atk(Atk.WHIP, 6, 7, 2, 70).anim((p, e, w, s, r, a) -> {
                    p.rot("armR", 3.0f, 0.4f, 0.5f, 1.57f, -0.2f, 0, w, s, r); p.scl("armR", 1, 1, 1, 1, 1.9f, 1, w, s, r);
                    p.rot("armL", 0.6f, 0, -0.7f, -0.3f, 0, -0.3f, w, s, r); p.rot("body", 0.25f, -0.5f, 0, -0.35f, 0.4f, 0, w, s, r);
                    p.rot("head", 0, 0.3f, 0, 0, -0.3f, 0, w, s, r); p.rot("bowTail", 0.6f, 0, 0, -0.6f, 0, 0, w, s, r);
                })
                // Shove: elbows cocked back, two-handed push that launches the target
                .custom(7, 2.8f, 0, 50, 10, 4, 10, blast(0.2, 2.2f, null, ParticleTypes.SWEEP_ATTACK, SoundEvents.PLAYER_ATTACK_KNOCKBACK, 0), (p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", -0.7f, 0, 0.3f, 1.65f, 0, -0.1f, w, s, r); p.rot("body", 0.3f, 0, 0, -0.45f, 0, 0, w, s, r);
                    p.pos("body", 0, -1, 3, 0, 0.5f, -6, w, s, r); pair(p, "legL", "legR", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r);
                    p.rot("head", 0.2f, 0, 0, -0.25f, 0, 0, w, s, r);
                })
                // Double-palm floor slap
                .atk(Atk.SLAM, 8, 3.5f, 0, 110).anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 3.1f, 0, 0.35f, 0.35f, 0, 0.15f, w, s, r); p.rot("body", 0.35f, 0, 0, -0.85f, 0, 0, w, s, r);
                    p.pos("body", 0, 2, 2, 0, -6, -5, w, s, r); p.rot("head", 0.5f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -0.9f, 0, 0, w, s, r);
                    pair(p, "legL", "legR", 0.1f, 0, 0, -0.5f, 0, 0, w, s, r);
                })
                // Lanky lope: long gangly sprint with flopping arms
                .atk(Atk.CHARGE, 7, 12, 4, 200).anim((p, e, w, s, r, a) -> {
                    p.rot("body", -0.2f, 0, 0, -0.7f, 0, 0, w, s, r); pair(p, "armL", "armR", -0.8f, 0, 0.3f, -1.3f, 0, 0.5f, w, s, r);
                    p.rot("head", 0.2f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                    if (striking(s, r)) { float q = Mth.sin(a * 1.2f); p.g("legR")[0] = q * 1.1f; p.g("legL")[0] = -q * 1.1f; p.add("armL", 0.3f * q, 0, 0); p.add("armR", -0.3f * q, 0, 0); }
                });
    }

    // ---------------------------------------------------------------- 3. Tambo Hare – drum-bellied rabbit with tambourines
    static CreatureDef tamboHare() {
        int pink = 0xf29bc0, white = 0xfaf6f0, red = 0xd83a3a, gold = 0xe8c040, wood = 0xc98a4a, silver = 0xd8d8e0;
        return CreatureDef.of("bunzo_bunny", "Bunzo Bunny", "バンゾー・バニー").size(0.8f, 2.4f).hp(60).dmg(5).speed(0.33).kbRes(0.3).egg(pink, gold).spawn(2)
                .part("legL", null, -3, 10, 0, 4, 10, 5, 0, -5, 0, pink).walk(0.9f, 0)
                .part("footL", "legL", 0, -10, -2, 5, 2.4f, 9, 0, 1.2f, 0, white)
                .part("legR", null, 3, 10, 0, 4, 10, 5, 0, -5, 0, pink).walk(0.9f, P)
                .part("footR", "legR", 0, -10, -2, 5, 2.4f, 9, 0, 1.2f, 0, white)
                .part("body", null, 0, 10, 0, 12, 13, 9, 0, 6.5f, 0, pink).sway(0.03f, 0.15f, 0, 2)
                .part("drum", "body", 0, 5.5f, -6, 10, 7, 4, 0, 0, 0, red)
                .part("drumRimT", "body", 0, 9.2f, -6, 10.6f, 1, 4.6f, 0, 0, 0, gold)
                .part("drumRimB", "body", 0, 1.8f, -6, 10.6f, 1, 4.6f, 0, 0, 0, gold)
                .part("strap", "body", 0, 11.5f, -4.6f, 12.4f, 1, 0.6f, 0, 0, 0, 0x6a3d22)
                .part("tail", "body", 0, 3, 4.5f, 4, 4, 3, 0, 0, 1, white).sway(0.3f, 0.3f, 0, 1)
                .part("head", "body", 0, 13, 0, 12, 10, 10, 0, 5, 0, pink)
                .part("muzzle", "head", 0, 3, -5.2f, 6, 4, 1.5f, 0, 0, 0, white)
                .part("nose", "head", 0, 5, -6, 2, 1.4f, 0.6f, 0, 0, 0, 0xff5c8a)
                .part("toothL", "head", -0.6f, 2, -6, 1.1f, 2.2f, 0.4f, 0, -1.1f, 0, 0xfffdf3)
                .part("toothR", "head", 0.6f, 2, -6, 1.1f, 2.2f, 0.4f, 0, -1.1f, 0, 0xfffdf3)
                .part("jaw", "head", 0, 1.2f, -5, 4, 1.2f, 1.5f, 0, -0.6f, -0.3f, 0x300a10)
                .part("eyeL", "head", -3, 6.5f, -5.1f, 2.6f, 3, 0.5f, 0, 0, 0, 0x1a1a1a)
                .part("glintL", "eyeL", 0.5f, 0.7f, -0.3f, 0.9f, 0.9f, 0.2f, 0, 0, 0, 0xffffff).glow()
                .part("eyeR", "head", 3, 6.5f, -5.1f, 2.6f, 3, 0.5f, 0, 0, 0, 0x1a1a1a)
                .part("glintR", "eyeR", 0.5f, 0.7f, -0.3f, 0.9f, 0.9f, 0.2f, 0, 0, 0, 0xffffff).glow()
                .part("cheekL", "head", -4.6f, 3.5f, -5.1f, 1.6f, 1, 0.3f, 0, 0, 0, 0xff8fb0)
                .part("cheekR", "head", 4.6f, 3.5f, -5.1f, 1.6f, 1, 0.3f, 0, 0, 0, 0xff8fb0)
                .part("earL", "head", -2.5f, 10, 0.5f, 3, 12, 2, 0, 6, 0, pink).sway(0.1f, 0.2f, 0, 0)
                .part("earInL", "earL", 0, 6, -1.05f, 1.6f, 10, 0.3f, 0, 0, 0, white)
                .part("earR", "head", 2.5f, 10, 0.5f, 3, 12, 2, 0, 6, 0, pink).sway(0.1f, 0.2f, 1.5f, 0)
                .part("earInR", "earR", 0, 6, -1.05f, 1.6f, 10, 0.3f, 0, 0, 0, white)
                .part("armL", "body", -7.5f, 12, 0, 3, 9, 3, 0, -4.5f, 0, pink).walk(0.6f, P)
                .part("handL", "armL", 0, -9, 0, 3.4f, 2.5f, 3.4f, 0, -1, 0, white)
                .part("tambL", "handL", 0, -2, -2, 7, 7, 1, 0, 0, 0, wood)
                .part("skinL", "tambL", 0, 0, -0.6f, 5.6f, 5.6f, 0.3f, 0, 0, 0, white)
                .part("jingleL1", "tambL", -3.6f, 0, 0, 1, 2, 1.4f, 0, 0, 0, silver)
                .part("jingleL2", "tambL", 3.6f, 0, 0, 1, 2, 1.4f, 0, 0, 0, silver)
                .part("armR", "body", 7.5f, 12, 0, 3, 9, 3, 0, -4.5f, 0, pink).walk(0.6f, 0)
                .part("handR", "armR", 0, -9, 0, 3.4f, 2.5f, 3.4f, 0, -1, 0, white)
                .part("tambR", "handR", 0, -2, -2, 7, 7, 1, 0, 0, 0, wood)
                .part("skinR", "tambR", 0, 0, -0.6f, 5.6f, 5.6f, 0.3f, 0, 0, 0, white)
                .part("jingleR1", "tambR", -3.6f, 0, 0, 1, 2, 1.4f, 0, 0, 0, silver)
                .part("jingleR2", "tambR", 3.6f, 0, 0, 1, 2, 1.4f, 0, 0, 0, silver)
                // Cymbal crash: arms thrown wide, ears up, then a stunning smash together
                .atk(Atk.CLAP, 4, 3, 0, 60).anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 1.3f, 0, 1.4f, 1.6f, 0, -0.2f, w, s, r); p.rot("body", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r);
                    pair(p, "earL", "earR", -0.2f, 0, 0.15f, -0.7f, 0, -0.1f, w, s, r); p.rot("jaw", -0.5f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("head", -0.25f, 0, 0, 0.2f, 0, 0, w, s, r); p.pos("body", 0, 1, 2, 0, -1, -3, w, s, r);
                    if (r > 0) p.add("head", 0, 0, 0.2f * Mth.sin(a * 2.5f) * (1 - r));
                })
                // Drum roll: pounds the belly drum, each beat a shockwave
                .custom(2, 3.5f, 0, 120, 8, 24, 8, blast(-2, 0.5f, null, ParticleTypes.NOTE, SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 4), (p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 1.1f, 0, -0.2f, 1.0f, 0, -0.35f, w, s, r); p.rot("body", -0.1f, 0, 0, 0.05f, 0, 0, w, s, r);
                    if (striking(s, r)) { float q = Mth.sin(a * 2.2f); p.add("armL", 0.5f * q, 0, 0); p.add("armR", -0.5f * q, 0, 0);
                        p.g("body")[4] += Math.abs(q) * 1.2f; p.add("earL", 0.2f * q, 0, 0); p.add("earR", -0.2f * q, 0, 0); p.add("head", 0, 0.15f * q, 0); }
                })
                // Bunny hop: deep crouch with ears pinned back, springing leap
                .atk(Atk.POUNCE, 4, 6, 2, 70).anim((p, e, w, s, r, a) -> {
                    p.pos("body", 0, -3, 1, 0, 3, -4, w, s, r); p.rot("body", 0.2f, 0, 0, -0.6f, 0, 0, w, s, r);
                    pair(p, "legL", "legR", 1.0f, 0, 0, -0.9f, 0, 0, w, s, r); pair(p, "earL", "earR", 0.9f, 0, 0.1f, 1.3f, 0, 0.2f, w, s, r);
                    pair(p, "armL", "armR", -0.5f, 0, 0.2f, 2.2f, 0, 0.5f, w, s, r); p.rot("jaw", 0, 0, 0, -0.6f, 0, 0, w, s, r);
                })
                // Jingle rattle: tambourines shaken overhead – dizzying noise cone
                .custom(2, 6, 0, 160, 12, 16, 10, blast(0.5, 0.3f, both(fx(MobEffects.CONFUSION, 100, 0), fx(MobEffects.MOVEMENT_SLOWDOWN, 60, 0)), ParticleTypes.NOTE, SoundEvents.NOTE_BLOCK_BELL.value(), 0), (p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 2.3f, 0, 0.6f, 2.8f, 0, 0.3f, w, s, r); p.rot("head", 0.4f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                    if (striking(s, r) || w > 0.6f) { float q = Mth.sin(a * 3.2f); p.add("armL", 0, 0, 0.3f * q); p.add("armR", 0, 0, 0.3f * q); p.add("body", 0, 0, 0.06f * q); pair(p, "earL", "earR", 0, 0, 0.4f * q, 0, 0, 0.4f * q, 1, 1, 0); }
                })
                // Foot thump: lifts a big foot and thumps the floor
                .atk(Atk.STOMP, 4, 3, 0, 80).anim((p, e, w, s, r, a) -> {
                    p.rot("legR", 1.3f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("body", 0.25f, 0, 0.1f, -0.2f, 0, 0, w, s, r);
                    pair(p, "earL", "earR", -0.2f, 0, 0, 0.6f, 0, 0.3f, w, s, r); pair(p, "armL", "armR", 0.4f, 0, 0.9f, 0.1f, 0, 0.3f, w, s, r);
                    p.pos("body", 0, 1.5f, 0, 0, -2.5f, -1, w, s, r);
                });
    }

    // ---------------------------------------------------------------- 4. Disco Droid – mirror-ball headed dancing robot toy
    static CreatureDef discoDroid() {
        int chrome = 0xb8c2cf, steel = 0x5a6170, mag = 0xff3fb4, cyan = 0x3fe8ff, gold = 0xf2c94c, black = 0x15151a;
        return CreatureDef.of("boogie_bot", "Boogie Bot", "ブギーボット").size(0.9f, 2.3f).hp(70).dmg(6).speed(0.3).kbRes(0.6).armor(4).egg(chrome, mag).spawn(1)
                .part("legL", null, -3.5f, 12, 0, 4, 12, 4, 0, -6, 0, steel).walk(0.8f, 0)
                .part("bootL", "legL", 0, -12, -0.5f, 5.5f, 3, 7, 0, 1.5f, -0.5f, chrome)
                .part("legR", null, 3.5f, 12, 0, 4, 12, 4, 0, -6, 0, steel).walk(0.8f, P)
                .part("bootR", "legR", 0, -12, -0.5f, 5.5f, 3, 7, 0, 1.5f, -0.5f, chrome)
                .part("body", null, 0, 12, 0, 13, 12, 8, 0, 6, 0, chrome).sway(0.05f, 0.25f, 0, 2)
                .part("belt", "body", 0, 1, 0, 14, 2, 9, 0, 0, 0, gold)
                .part("speaker", "body", 0, 6.5f, -4.1f, 7, 7, 0.6f, 0, 0, 0, black)
                .part("cone", "body", 0, 6.5f, -4.5f, 4, 4, 0.4f, 0, 0, 0, steel)
                .part("coreDot", "body", 0, 6.5f, -4.8f, 1.6f, 1.6f, 0.3f, 0, 0, 0, mag).glow()
                .part("lightL", "body", -4.8f, 10.5f, -4.1f, 1.2f, 1.2f, 0.4f, 0, 0, 0, cyan).glow()
                .part("lightR", "body", 4.8f, 10.5f, -4.1f, 1.2f, 1.2f, 0.4f, 0, 0, 0, mag).glow()
                .part("neck", "body", 0, 12, 0, 3, 2, 3, 0, 1, 0, steel)
                .part("head", "body", 0, 14, 0, 11, 11, 11, 0, 5.5f, 0, chrome)
                .part("visor", "head", 0, 6, -5.7f, 9, 3, 0.6f, 0, 0, 0, black)
                .part("eyeL", "head", -2.5f, 6, -6.1f, 2.5f, 1.5f, 0.3f, 0, 0, 0, 0xff2a2a).glow()
                .part("eyeR", "head", 2.5f, 6, -6.1f, 2.5f, 1.5f, 0.3f, 0, 0, 0, 0xff2a2a).glow()
                .part("jaw", "head", 0, 2.8f, -5.6f, 6, 2, 0.6f, 0, -1, 0, black)
                .part("grille", "jaw", 0, -1, -0.35f, 5, 0.4f, 0.2f, 0, 0, 0, gold)
                .part("tile1", "head", -3.5f, 9.5f, -5.6f, 2, 2, 0.4f, 0, 0, 0, cyan).glow()
                .part("tile2", "head", 3.5f, 9.5f, -5.6f, 2, 2, 0.4f, 0, 0, 0, mag).glow()
                .part("tile3", "head", 0, 10.6f, -2, 2, 0.4f, 2, 0, 0, 0, gold).glow()
                .part("tileL", "head", -5.7f, 6, 0, 0.4f, 2, 2, 0, 0, 0, cyan).glow()
                .part("tileR", "head", 5.7f, 6, 0, 0.4f, 2, 2, 0, 0, 0, mag).glow()
                .part("antenna", "head", 0, 11, 0, 0.8f, 5, 0.8f, 0, 2.5f, 0, steel).sway(0.15f, 0.3f, 0, 2)
                .part("antBall", "antenna", 0, 5, 0, 2, 2, 2, 0, 1, 0, mag).glow()
                .part("armL", "body", -8, 11, 0, 3, 6, 3, 0, -3, 0, steel).walk(0.6f, P)
                .part("foreL", "armL", 0, -6, 0, 3.6f, 6, 3.6f, 0, -3, 0, chrome)
                .part("clawL1", "foreL", -1, -6, 0, 1.2f, 3, 3, 0, -1.5f, 0, gold)
                .part("clawL2", "foreL", 1, -6, 0, 1.2f, 3, 3, 0, -1.5f, 0, gold)
                .part("armR", "body", 8, 11, 0, 3, 6, 3, 0, -3, 0, steel).walk(0.6f, 0)
                .part("foreR", "armR", 0, -6, 0, 3.6f, 6, 3.6f, 0, -3, 0, chrome)
                .part("clawR1", "foreR", -1, -6, 0, 1.2f, 3, 3, 0, -1.5f, 0, gold)
                .part("clawR2", "foreR", 1, -6, 0, 1.2f, 3, 3, 0, -1.5f, 0, gold)
                // Piston punch: forearm retracts, then rams out on a piston
                .custom(6, 2.6f, 0, 40, 8, 3, 10, blast(0.3, 1.6f, null, ParticleTypes.CRIT, SoundEvents.PISTON_EXTEND, 0), (p, e, w, s, r, a) -> {
                    p.rot("armR", -0.6f, 0, 0.3f, 1.6f, 0, 0, w, s, r); p.pos("foreR", 0, 2, 0, 0, -3, 0, w, s, r);
                    p.rot("body", 0, -0.5f, 0, 0, 0.35f, 0, w, s, r); p.rot("armL", 0.8f, 0, -0.3f, -0.4f, 0, -0.2f, w, s, r);
                    p.rot("head", 0, 0.3f, 0, 0, -0.2f, 0, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -3, w, s, r);
                })
                // Laser beat: classic disco point – arm up, then snapped at the target with a dazzling beam
                .atk(Atk.SHOOT, 4, 14, 3, 50, 0xff3fb4, "blind").anim((p, e, w, s, r, a) -> {
                    p.rot("armR", 3.0f, 0, 0.3f, 1.57f, 0, 0, w, s, r); p.rot("armL", 0.2f, 0, -0.9f, 0.1f, 0, -0.9f, w, s, r);
                    p.rot("body", 0, 0, 0.18f, 0, 0, -0.08f, w, s, r); p.rot("legL", -0.3f, 0, -0.2f, 0, 0, -0.1f, w, s, r);
                    p.rot("head", 0.2f, -0.3f, 0, 0, 0, 0, w, s, r); p.scl("coreDot", 1, 1, 1, 2.2f, 2.2f, 1, w, s, r);
                })
                // Mirror-ball spin: twirls with arms out, scattering blinding light
                .custom(3, 3.2f, 0, 120, 10, 20, 10, blast(-2, 0.8f, fx(MobEffects.BLINDNESS, 30, 0), dust(0xff3fb4, 1.5f), SoundEvents.AMETHYST_BLOCK_CHIME, 5), (p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 0.4f, 0, 0.6f, 0.2f, 0, 1.5f, w, s, r); p.rot("legL", 0, 0, 0, 0.9f, 0, -0.3f, w, s, r);
                    p.rot("head", -0.3f, 0, 0, -0.2f, 0, 0, w, s, r);
                    if (striking(s, r)) { p.g("body")[1] += s * P * 8; p.g("antenna")[2] += 0.5f * Mth.sin(a * 2); }
                })
                // Floor drop: high-knee dance stomp
                .atk(Atk.STOMP, 5, 3, 0, 90).anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 2.8f, 0, 0.4f, 0.4f, 0, 0.8f, w, s, r); p.rot("legR", 1.6f, 0, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("body", 0.25f, 0, -0.1f, -0.25f, 0, 0, w, s, r); p.pos("body", 0, 2, 0, 0, -2.5f, 0, w, s, r); p.rot("head", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r);
                });
    }

    // ---------------------------------------------------------------- 5. Crate o' Cuddlers – shipping crate stuffed with live plush
    static CreatureDef crateCuddlers() {
        int wood = 0xb07a45, woodD = 0x7a5230, metal = 0x8a8f98, teal = 0x2bb5a8, pink = 0xf29bc0, yellow = 0xf2d04a, eye = 0x111111;
        return CreatureDef.of("mini_boxy_boo", "Mini Boxy Boo", "ミニ・ボクシーブー").size(1.2f, 1.6f).hp(80).dmg(5).speed(0.26).kbRes(0.8).armor(3).egg(wood, teal).spawn(1)
                .part("legL", null, -4, 5, 0, 4, 5, 4, 0, -2.5f, 0, teal).walk(1.2f, 0)
                .part("legR", null, 4, 5, 0, 4, 5, 4, 0, -2.5f, 0, pink).walk(1.2f, P)
                .part("body", null, 0, 5, 0, 18, 16, 16, 0, 8, 0, wood)
                .part("slat1", "body", 0, 4, -8.2f, 18.4f, 1, 0.4f, 0, 0, 0, woodD)
                .part("slat2", "body", 0, 11, -8.2f, 18.4f, 1, 0.4f, 0, 0, 0, woodD)
                .part("cornerL", "body", -8.6f, 8, -8.2f, 1.2f, 16.4f, 1.2f, 0, 0, 0, metal)
                .part("cornerR", "body", 8.6f, 8, -8.2f, 1.2f, 16.4f, 1.2f, 0, 0, 0, metal)
                .part("label", "body", 0, 7.5f, -8.3f, 8, 3, 0.3f, 0, 0, 0, 0x2b2b2b)
                .part("heart", "body", 0, 7.5f, -8.5f, 2, 2, 0.2f, 0, 0, 0, 0xd83a3a)
                .part("jaw", "body", 0, 16, 8, 19, 2, 17, 0, 1, -8.5f, wood)
                .part("lidSlat", "jaw", 0, 2.2f, -8.5f, 19, 0.5f, 1.5f, 0, 0, 0, woodD)
                .part("pk1", "body", -4.5f, 16, -2, 6, 6, 6, 0, 1, 0, teal).sway(0.15f, 0.3f, 0, 2)
                .part("pk1e1", "pk1", -1.3f, 2, -3.1f, 1.2f, 1.5f, 0.3f, 0, 0, 0, eye)
                .part("pk1e2", "pk1", 1.3f, 2, -3.1f, 1.2f, 1.5f, 0.3f, 0, 0, 0, eye)
                .part("head", "body", 4, 16, 1, 6, 6, 6, 0, 1, 0, pink)
                .part("pk2e1", "head", -1.3f, 2, -3.1f, 1.2f, 1.5f, 0.3f, 0, 0, 0, eye)
                .part("pk2e2", "head", 1.3f, 2, -3.1f, 1.2f, 1.5f, 0.3f, 0, 0, 0, eye)
                .part("pk3", "body", 0.5f, 15, -4.5f, 5, 5, 5, 0, 1, 0, yellow).sway(0.15f, 0.35f, 2, 0)
                .part("pk3e1", "pk3", -1.1f, 1.8f, -2.6f, 1, 1.3f, 0.3f, 0, 0, 0, eye)
                .part("pk3e2", "pk3", 1.1f, 1.8f, -2.6f, 1, 1.3f, 0.3f, 0, 0, 0, eye)
                .part("armL", "body", -9.5f, 11, -2, 3, 10, 3, 0, -5, 0, teal).sway(0.08f, 0.2f, 0, 0)
                .part("pawL", "armL", 0, -10, 0, 3.6f, 2, 3.6f, 0, 0, 0, pink)
                .part("armR", "body", 9.5f, 11, -2, 3, 10, 3, 0, -5, 0, yellow).sway(0.08f, 0.2f, P, 0)
                .part("pawR", "armR", 0, -10, 0, 3.6f, 2, 3.6f, 0, 0, 0, teal)
                // Lid snap: lid creaks open, plush heads lean out, then SLAM shut on the target
                .atk(Atk.BITE, 5, 2, 0, 40).anim((p, e, w, s, r, a) -> {
                    p.rot("jaw", 1.1f, 0, 0, 0, 0, 0, w, s, r); p.rot("body", 0.15f, 0, 0, -0.3f, 0, 0, w, s, r);
                    p.pos("head", 0, 3, -1, 0, 0, 0, w, s, r); p.pos("pk1", 0, 2, -1, 0, 0, 0, w, s, r); p.pos("pk3", 0, 3, -2, 0, 0, 0, w, s, r);
                    pair(p, "armL", "armR", -0.3f, 0, 0.5f, 1.4f, 0, -0.2f, w, s, r);
                })
                // Spill: lid flung open, crate rattles, a cuddler tumbles out
                .atk(Atk.SUMMON, 0, 10, 0, 360, 2, "mini_huggy").anim((p, e, w, s, r, a) -> {
                    p.rot("jaw", 1.4f, 0, 0, 1.9f, 0, 0, w, s, r); p.pos("head", 0, 3, 0, 0, 5, 0, w, s, r); p.pos("pk1", 0, 3, 0, 0, 6, 0, w, s, r); p.pos("pk3", 0, 2, 0, 0, 5, -2, w, s, r);
                    pair(p, "armL", "armR", 2.4f, 0, 0.6f, 2.9f, 0, 0.9f, w, s, r);
                    p.add("body", 0, 0, 0.08f * Mth.sin(a * 2.5f) * (1 - r));
                })
                // Crate ram: tips forward on scurrying legs
                .atk(Atk.CHARGE, 6, 10, 3, 160).anim((p, e, w, s, r, a) -> {
                    p.rot("body", 0.15f, 0, 0, -0.35f, 0, 0, w, s, r); pair(p, "armL", "armR", -0.8f, 0, 0.4f, -1.4f, 0, 0.3f, w, s, r);
                    p.pos("body", 0, 0, 2, 0, 0, -2, w, s, r);
                    if (striking(s, r)) { float q = Mth.sin(a * 1.8f); p.g("legL")[0] = q * 1.2f; p.g("legR")[0] = -q * 1.2f; }
                })
                // Plush pitch: reaches over the top and hurls three stuffed balls
                .custom(3, 12, 3, 80, 12, 2, 10, volley(3, 0.18f, 0xf29bc0, "slow", 0), (p, e, w, s, r, a) -> {
                    p.rot("armR", 3.0f, 0, 0.2f, 0.9f, 0, -0.2f, w, s, r); p.rot("armL", 0.4f, 0, -0.6f, 1.2f, 0, -0.2f, w, s, r);
                    p.rot("jaw", 0.7f, 0, 0, 0.9f, 0, 0, w, s, r); p.rot("body", 0.15f, 0.3f, 0, -0.15f, -0.2f, 0, w, s, r);
                    p.pos("pk3", 0, 2, 0, 0, 4, -2, w, s, r);
                });
    }

    // ---------------------------------------------------------------- 6. Lantern Moth – lightbulb-bodied moth toy
    static CreatureDef lanternMoth() {
        int bulb = 0xffe27a, brass = 0xb8892e, fuzz = 0xf4efe0, wing = 0xd9c9a0, spot = 0xff9f40, dark = 0x3a2e24;
        return CreatureDef.of("glowby", "Glowby", "グロウビー").size(0.9f, 1.6f).hp(40).dmg(4).speed(0.34).kbRes(0.1).egg(bulb, brass).spawn(2)
                .part("legL", null, -2.5f, 8, 0, 2, 8, 2, 0, -4, 0, dark).walk(1.0f, 0)
                .part("footL", "legL", 0, -8, -0.5f, 3, 1.2f, 4, 0, 0.6f, 0, brass)
                .part("legR", null, 2.5f, 8, 0, 2, 8, 2, 0, -4, 0, dark).walk(1.0f, P)
                .part("footR", "legR", 0, -8, -0.5f, 3, 1.2f, 4, 0, 0.6f, 0, brass)
                .part("body", null, 0, 8, 0, 9, 10, 7, 0, 5, 0, fuzz).sway(0.04f, 0.2f, 0, 2)
                .part("collar", "body", 0, 10, 0, 11, 2.5f, 9, 0, 0, 0, 0xffffff)
                .part("tail", "body", 0, 4, 3.5f, 7, 8, 7, 0, -1, 3.5f, bulb).glow()
                .part("bulbCap", "tail", 0, 3.2f, 3.5f, 5, 1.5f, 5, 0, 0, 0, brass)
                .part("bulbBase", "tail", 0, -5.5f, 3.5f, 3, 1.5f, 3, 0, 0, 0, brass)
                .part("filament", "tail", 0, -1, -0.1f, 3, 0.6f, 0.3f, 0, 0, 0, 0xff7a20).glow()
                .part("head", "body", 0, 10, 0, 8, 7, 7, 0, 3.5f, 0, fuzz)
                .part("eyeL", "head", -3.3f, 4, -2.5f, 3, 4, 4, 0, 0, 0, dark)
                .part("glintL", "eyeL", 0, 0.8f, -2.05f, 1, 1, 0.2f, 0, 0, 0, 0xffffff).glow()
                .part("eyeR", "head", 3.3f, 4, -2.5f, 3, 4, 4, 0, 0, 0, dark)
                .part("glintR", "eyeR", 0, 0.8f, -2.05f, 1, 1, 0.2f, 0, 0, 0, 0xffffff).glow()
                .part("jaw", "head", 0, 1.2f, -3.6f, 3, 1.5f, 1, 0, -0.75f, 0, brass)
                .part("antennaL", "head", -1.5f, 7, -2, 0.6f, 6, 0.6f, 0, 3, 0, dark).sway(0.15f, 0.2f, 0, 2)
                .part("featherL", "antennaL", 0, 4.5f, 0, 2.4f, 3, 0.4f, 0, 0, 0, wing)
                .part("antennaR", "head", 1.5f, 7, -2, 0.6f, 6, 0.6f, 0, 3, 0, dark).sway(0.15f, 0.2f, P, 2)
                .part("featherR", "antennaR", 0, 4.5f, 0, 2.4f, 3, 0.4f, 0, 0, 0, wing)
                .part("wingL", "body", -3, 8, 3, 12, 10, 0.6f, -6, 1, 0, wing).sway(0.3f, 0.6f, 0, 1)
                .part("spotL", "wingL", -7, 2, -0.4f, 3, 3, 0.3f, 0, 0, 0, spot).glow()
                .part("wingL2", "body", -3, 4, 3, 8, 6, 0.6f, -4, -2, 0, wing).sway(0.25f, 0.6f, 0.4f, 1)
                .part("wingR", "body", 3, 8, 3, 12, 10, 0.6f, 6, 1, 0, wing).sway(0.3f, 0.6f, P, 1)
                .part("spotR", "wingR", 7, 2, -0.4f, 3, 3, 0.3f, 0, 0, 0, spot).glow()
                .part("wingR2", "body", 3, 4, 3, 8, 6, 0.6f, 4, -2, 0, wing).sway(0.25f, 0.6f, P + 0.4f, 1)
                .part("armL", "body", -5, 8, -1, 1.6f, 6, 1.6f, 0, -3, 0, dark).walk(0.6f, P)
                .part("clawL", "armL", 0, -6, 0, 2.2f, 1.6f, 2.2f, 0, 0, 0, brass)
                .part("armR", "body", 5, 8, -1, 1.6f, 6, 1.6f, 0, -3, 0, dark).walk(0.6f, 0)
                .part("clawR", "armR", 0, -6, 0, 2.2f, 1.6f, 2.2f, 0, 0, 0, brass)
                // Wing buffet: wings swept back, then clapped forward in a gust
                .custom(4, 3, 0, 50, 10, 4, 10, blast(0.1, 1.8f, fx(MobEffects.MOVEMENT_SLOWDOWN, 40, 1), ParticleTypes.CLOUD, SoundEvents.PHANTOM_FLAP, 0), (p, e, w, s, r, a) -> {
                    p.rot("wingL", 0, 1.0f, 0, 0, -1.2f, 0, w, s, r); p.rot("wingR", 0, -1.0f, 0, 0, 1.2f, 0, w, s, r);
                    p.rot("wingL2", 0, 0.8f, 0, 0, -1.0f, 0, w, s, r); p.rot("wingR2", 0, -0.8f, 0, 0, 1.0f, 0, w, s, r);
                    p.rot("body", 0.2f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 0, 1, 0, 0, -2, w, s, r);
                })
                // Spark mote: lantern abdomen curls under and flicks a burning spark
                .atk(Atk.SHOOT, 3, 12, 3, 50, 0xffe27a, "fire").anim((p, e, w, s, r, a) -> {
                    p.rot("tail", -0.7f, 0, 0, 0.5f, 0, 0, w, s, r); p.scl("tail", 1, 1, 1, 1.25f, 1.25f, 1.25f, w, s, r);
                    p.rot("body", 0.15f, 0, 0, -0.25f, 0, 0, w, s, r); pair(p, "antennaL", "antennaR", -0.4f, 0, 0.2f, -0.9f, 0, 0.1f, w, s, r);
                    pair(p, "wingL", "wingR", 0, -0.5f, 0, 0, 0.3f, 0, w, s, r);
                })
                // Flash burst: dims and folds wings, then flares blinding light
                .custom(3, 5, 0, 160, 16, 4, 12, blast(-2, 0.5f, both(fx(MobEffects.BLINDNESS, 60, 0), fx(MobEffects.GLOWING, 100, 0)), ParticleTypes.END_ROD, SoundEvents.FIREWORK_ROCKET_BLAST, 0), (p, e, w, s, r, a) -> {
                    p.scl("tail", 0.8f, 0.8f, 0.8f, 1.6f, 1.6f, 1.6f, w, s, r); pair(p, "wingL", "wingR", 0, -1.2f, 0, 0, 0.6f, 0, w, s, r);
                    pair(p, "wingL2", "wingR2", 0, -1.0f, 0, 0, 0.5f, 0, w, s, r); p.pos("body", 0, -1.5f, 0, 0, 1.5f, 0, w, s, r);
                    pair(p, "armL", "armR", 0.3f, 0, -0.2f, 0.4f, 0, 1.2f, w, s, r); p.rot("head", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r);
                    if (w > 0.4f && s <= 0) p.add("body", 0, 0, 0.06f * Mth.sin(a * 4));
                })
                // Flutter dive: frantic flapping, dives at the target
                .atk(Atk.POUNCE, 4, 7, 2.5f, 80).anim((p, e, w, s, r, a) -> {
                    p.rot("body", 0.2f, 0, 0, -0.8f, 0, 0, w, s, r); pair(p, "armL", "armR", -0.3f, 0, 0.2f, 1.8f, 0, 0.2f, w, s, r);
                    p.pos("body", 0, -1.5f, 0, 0, 1, -3, w, s, r);
                    float f = Mth.sin(a * 2.2f) * 0.8f * (1 - r); p.add("wingL", 0, f, 0); p.add("wingR", 0, -f, 0); p.add("wingL2", 0, f, 0); p.add("wingR2", 0, -f, 0);
                });
    }

    // ---------------------------------------------------------------- 7. Quilt Phantom – walking patchwork blanket
    static CreatureDef quiltPhantom() {
        int red = 0xd9534f, blue = 0x5b9bd5, yellow = 0xf0c75e, green = 0x6cbf6a, purple = 0xb07cc6, cream = 0xf4ede0, seam = 0x3a2a20;
        return CreatureDef.of("nightmare_kissy_missy", "Nightmare Kissy Missy", "ナイトメア・キッシーミッシー").size(1.0f, 2.5f).hp(75).dmg(6).speed(0.28).kbRes(0.4).egg(blue, yellow).spawn(1)
                .part("legL", null, -3, 6, 0, 4, 6, 4, 0, -3, 0, 0x2a2020).walk(0.7f, 0)
                .part("legR", null, 3, 6, 0, 4, 6, 4, 0, -3, 0, 0x2a2020).walk(0.7f, P)
                .part("body", null, 0, 6, 0, 14, 20, 10, 0, 10, 0, cream).sway(0.04f, 0.12f, 0, 2)
                .part("patch1", "body", -3.5f, 15, -5.1f, 6, 6, 0.4f, 0, 0, 0, red)
                .part("patch2", "body", 3.5f, 15, -5.1f, 6, 6, 0.4f, 0, 0, 0, blue)
                .part("patch3", "body", -3.5f, 8, -5.1f, 6, 6, 0.4f, 0, 0, 0, yellow)
                .part("patch4", "body", 3.5f, 8, -5.1f, 6, 6, 0.4f, 0, 0, 0, green)
                .part("patch5", "body", 0, 2.5f, -5.1f, 13, 4, 0.4f, 0, 0, 0, purple)
                .part("patch6", "body", 0, 12, 5.1f, 10, 10, 0.4f, 0, 0, 0, blue)
                .part("seamV", "body", 0, 10, -5.4f, 0.6f, 18, 0.2f, 0, 0, 0, seam)
                .part("seamH", "body", 0, 11.5f, -5.4f, 13, 0.6f, 0.2f, 0, 0, 0, seam)
                .part("hem1", "body", -5, 0, 0, 4, 4, 10, 0, -2, 0, cream).sway(0.15f, 0.2f, 0, 0)
                .part("hem2", "body", 0, 0, -2, 4, 5, 6, 0, -2.5f, 0, red).sway(0.15f, 0.2f, 1, 0)
                .part("hem3", "body", 5, 0, 0, 4, 4, 10, 0, -2, 0, purple).sway(0.15f, 0.2f, 2, 0)
                .part("head", "body", 0, 20, 0, 14, 13, 12, 0, 6, 0, blue)
                .part("hoodPatch", "head", 4, 9, -6.1f, 4, 4, 0.4f, 0, 0, 0, yellow)
                .part("hoodTip", "head", 0, 12.5f, 1, 6, 3, 6, 0, 1.5f, 1.5f, blue).sway(0.15f, 0.15f, 0, 0)
                .part("face", "head", 0, 5.5f, -6.05f, 9, 8, 0.3f, 0, 0, 0, 0x0b0710)
                .part("eyeL", "head", -2, 7, -6.3f, 2.4f, 2.4f, 0.3f, 0, 0, 0, 0xffdd55).glow()
                .part("eyeR", "head", 2, 7, -6.3f, 2.4f, 2.4f, 0.3f, 0, 0, 0, 0xffdd55).glow()
                .part("jaw", "head", 0, 3.5f, -6.2f, 6, 2, 0.4f, 0, -1, 0, 0x22050a)
                .part("tooth1", "jaw", -1.8f, -0.2f, -0.3f, 1, 1.2f, 0.2f, 0, -0.4f, 0, 0xfff7e0)
                .part("tooth2", "jaw", 0, -0.2f, -0.3f, 1, 1.4f, 0.2f, 0, -0.5f, 0, 0xfff7e0)
                .part("tooth3", "jaw", 1.8f, -0.2f, -0.3f, 1, 1.2f, 0.2f, 0, -0.4f, 0, 0xfff7e0)
                .part("armL", "body", -8.5f, 19, 0, 3, 18, 9, 0, -9, 0, red).sway(0.06f, 0.15f, 0, 2)
                .part("flapL", "armL", 0, -18, 0, 3, 4, 6, 0, -2, 0, yellow).sway(0.2f, 0.25f, 0, 0)
                .part("armR", "body", 8.5f, 19, 0, 3, 18, 9, 0, -9, 0, blue).sway(0.06f, 0.15f, P, 2)
                .part("flapR", "armR", 0, -18, 0, 3, 4, 6, 0, -2, 0, green).sway(0.2f, 0.25f, P, 0)
                // Swaddle: rears up spreading its flaps, then folds them shut around the victim
                .atk(Atk.GRAB, 5, 3, 0, 150, 40, "").anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 2.2f, 0, 1.0f, 1.4f, 0, -0.9f, w, s, r); p.pos("body", 0, 2, 2, 0, -1, -5, w, s, r);
                    p.rot("body", 0.25f, 0, 0, -0.45f, 0, 0, w, s, r); p.rot("head", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("jaw", -0.4f, 0, 0, -0.7f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(a * 0.7f); p.add("armL", 0.1f * q, 0, 0.1f * q); p.add("armR", -0.1f * q, 0, -0.1f * q); p.g("body")[7] = 1 + 0.05f * q; }
                })
                // Smother shroud: billows outward, dropping suffocating darkness around it
                .custom(3, 4, 0, 180, 16, 6, 12, blast(-2, 0.4f, both(fx(MobEffects.DARKNESS, 100, 0), both(fx(MobEffects.MOVEMENT_SLOWDOWN, 60, 2), fx(MobEffects.BLINDNESS, 40, 0))), ParticleTypes.SQUID_INK, SoundEvents.WOOL_PLACE, 0), (p, e, w, s, r, a) -> {
                    p.scl("body", 1, 1.15f, 1, 1.35f, 0.8f, 1.35f, w, s, r); pair(p, "armL", "armR", 0.3f, 0, 0.3f, 0.2f, 0, 1.55f, w, s, r);
                    p.rot("head", 0.5f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -0.9f, 0, 0, w, s, r);
                    pair(p, "hem1", "hem3", 0, 0, 0, 0, 0, 0.7f, w, s, r);
                })
                // Flap slap: a wide backhanded swat with the right flap
                .atk(Atk.SWIPE, 6, 3.2f, 0, 35).anim((p, e, w, s, r, a) -> {
                    p.rot("armR", 1.4f, -1.4f, 0.4f, 1.3f, 1.3f, 0, w, s, r); p.rot("armL", 0.3f, 0, -0.5f, 0.2f, 0, -0.3f, w, s, r);
                    p.rot("body", 0, -0.7f, 0, -0.1f, 0.7f, 0, w, s, r); p.rot("flapR", 0.6f, 0, 0, -0.8f, 0, 0, w, s, r); p.rot("head", 0, 0.4f, 0, 0, -0.4f, 0, w, s, r);
                })
                // Corner flick: snaps a stretched blanket corner like a wet towel
                .atk(Atk.WHIP, 5, 6, 2, 80).anim((p, e, w, s, r, a) -> {
                    p.rot("armL", 2.8f, -0.3f, -0.5f, 1.57f, 0, 0, w, s, r); p.scl("armL", 1, 1, 1, 1, 2.3f, 1, w, s, r); p.rot("flapL", 0.9f, 0, 0, -1.0f, 0, 0, w, s, r);
                    p.rot("body", 0.25f, 0.4f, 0, -0.4f, -0.3f, 0, w, s, r); p.rot("armR", 0.5f, 0, 0.6f, 0.3f, 0, 0.4f, w, s, r);
                })
                // Drape drop: spreads like a gliding sheet and falls on the target
                .atk(Atk.POUNCE, 5, 6, 2, 90).anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 0.6f, 0, 0.5f, 0.3f, 0, 1.45f, w, s, r); p.rot("body", 0.2f, 0, 0, -1.2f, 0, 0, w, s, r);
                    p.pos("body", 0, -3, 0, 0, 2, -6, w, s, r); p.rot("head", 0.2f, 0, 0, 0.9f, 0, 0, w, s, r);
                    if (striking(s, r)) { float q = Mth.sin(a * 2f) * 0.2f; p.add("armL", 0, 0, -q); p.add("armR", 0, 0, q); }
                });
    }

    // ---------------------------------------------------------------- 8. Bubble Barb – spiky pufferfish bath toy on stubby feet
    static CreatureDef bubbleBarb() {
        int orange = 0xf5a03a, belly = 0xfde2a8, spike = 0xfff1d0, fin = 0x3fb8e0, lips = 0xe8456a;
        CreatureDef d = CreatureDef.of("maggie_mako", "Maggie Mako", "マギー・マコ").size(1.0f, 1.3f).hp(45).dmg(4).speed(0.27).kbRes(0.3).egg(orange, fin).spawn(2)
                .part("legL", null, -3, 4, 0, 3, 4, 3, 0, -2, 0, fin).walk(1.1f, 0)
                .part("footL", "legL", 0, -4, -1, 3.6f, 1.2f, 5, 0, 0.6f, 0, fin)
                .part("legR", null, 3, 4, 0, 3, 4, 3, 0, -2, 0, fin).walk(1.1f, P)
                .part("footR", "legR", 0, -4, -1, 3.6f, 1.2f, 5, 0, 0.6f, 0, fin)
                .part("body", null, 0, 4, 0, 14, 13, 14, 0, 6.5f, 0, orange).sway(0.05f, 0.2f, 0, 2)
                .part("belly", "body", 0, 4.5f, -7.1f, 10, 6, 0.4f, 0, 0, 0, belly);
        float[][] sp = {{0, 13, 0, 1.5f, 3, 1.5f, 0, 1.5f, 0}, {-4, 12.5f, -3, 1.5f, 2.5f, 1.5f, 0, 1.2f, 0}, {4, 12.5f, -3, 1.5f, 2.5f, 1.5f, 0, 1.2f, 0}, {-4, 12.5f, 3, 1.5f, 2.5f, 1.5f, 0, 1.2f, 0},
                {4, 12.5f, 3, 1.5f, 2.5f, 1.5f, 0, 1.2f, 0}, {-7, 7, 0, 3, 1.5f, 1.5f, -1.5f, 0, 0}, {7, 7, 0, 3, 1.5f, 1.5f, 1.5f, 0, 0}, {0, 11, 7, 1.5f, 1.5f, 3, 0, 0, 1.5f}};
        for (int i = 0; i < sp.length; i++) { float[] q = sp[i]; d.part("spike" + i, "body", q[0], q[1], q[2], q[3], q[4], q[5], q[6], q[7], q[8], spike); }
        return d.part("dorsal", "body", 0, 13, 3, 1, 4, 6, 0, 2, 0, fin).sway(0.1f, 0.3f, 0, 2)
                .part("finL", "body", -7, 6, 1, 1, 5, 4, -0.5f, 0, 1, fin).sway(0.4f, 0.5f, 0, 1)
                .part("finR", "body", 7, 6, 1, 1, 5, 4, 0.5f, 0, 1, fin).sway(0.4f, 0.5f, P, 1)
                .part("tail", "body", 0, 6, 7, 1, 7, 5, 0, 0, 2.5f, fin).sway(0.35f, 0.3f, 0, 1)
                .part("head", "body", 0, 8, -6.5f, 12, 6, 1, 0, 0, 0, orange)
                .part("eyeL", "head", -3.5f, 1.5f, -0.8f, 4, 4, 1, 0, 0, 0, 0xffffff)
                .part("pupilL", "eyeL", 0.4f, -0.3f, -0.6f, 2, 2, 0.3f, 0, 0, 0, 0x101820)
                .part("eyeR", "head", 3.5f, 1.5f, -0.8f, 4, 4, 1, 0, 0, 0, 0xffffff)
                .part("pupilR", "eyeR", -0.4f, -0.3f, -0.6f, 2, 2, 0.3f, 0, 0, 0, 0x101820)
                .part("lipU", "head", 0, -2.5f, -0.8f, 6, 1.6f, 1.6f, 0, 0, 0, lips)
                .part("mouthIn", "head", 0, -3.3f, -0.9f, 4, 1, 0.4f, 0, 0, 0, 0x400818)
                .part("jaw", "head", 0, -3.2f, -0.6f, 6, 1.6f, 1.6f, 0, -0.8f, -0.4f, lips)
                // Bubble spit: gulps (swells), then puffs a slowing bubble with recoil
                .atk(Atk.SHOOT, 3, 12, 3, 40, 0x9fe8ff, "slow").anim((p, e, w, s, r, a) -> {
                    p.scl("body", 1.15f, 1.1f, 1.15f, 0.9f, 0.95f, 0.9f, w, s, r); p.scl("lipU", 0.7f, 1.2f, 1.4f, 1, 1, 1, w, s, r);
                    p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r); p.rot("body", 0.2f, 0, 0, -0.1f, 0, 0, w, s, r); p.pos("body", 0, 0, 0, 0, 0, 2, w, s, r);
                })
                // Puff up: gulps air then balloons, every spike jabbing outward (poison)
                .custom(5, 2.8f, 0, 140, 14, 16, 12, (m, s, t) -> {
                    blast(-2, 1.3f, fx(MobEffects.POISON, 60, 0), ParticleTypes.BUBBLE_POP, SoundEvents.PUFFER_FISH_BLOW_UP, 0).tick(m, s, t);
                    if (t > s.wind() + 1 && t < s.wind() + s.strike() && t % 4 == 0 && m.level() instanceof ServerLevel)
                        for (LivingEntity e : Fx.foes(m, m.position(), 2.0)) { e.invulnerableTime = 0; Fx.hit(m, e, 1, 0.4f, e.position().subtract(m.position())); }
                }, (p, e, w, s, r, a) -> {
                    p.scl("body", 0.85f, 0.9f, 0.85f, 1.7f, 1.6f, 1.7f, w, s, r); p.rot("jaw", 0, 0, 0, 0.2f, 0, 0, w, s, r);
                    for (int i = 0; i < 8; i++) p.scl("spike" + i, 0.6f, 0.6f, 0.6f, 1.8f, 1.8f, 1.8f, w, s, r);
                    p.scl("eyeL", 1, 1, 1, 1.3f, 1.3f, 1, w, s, r); p.scl("eyeR", 1, 1, 1, 1.3f, 1.3f, 1, w, s, r);
                    if (striking(s, r)) p.add("body", 0, 0.05f * Mth.sin(a * 3), 0);
                })
                // Lip smack: hops forward with a huge kissy chomp
                .atk(Atk.BITE, 4, 1.8f, 0, 30).anim((p, e, w, s, r, a) -> {
                    p.rot("jaw", -1.0f, 0, 0, 0.1f, 0, 0, w, s, r); p.scl("lipU", 1, 1, 1, 1.2f, 1.4f, 1.6f, w, s, r);
                    p.pos("body", 0, 0, 2, 0, 2, -4, w, s, r); p.rot("body", 0.3f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("tail", 0, 0.6f, 0, 0, -0.7f, 0, w, s, r);
                })
                // Bubble barrage: rhythmic pumping, streams fans of bubbles
                .custom(2, 10, 2, 120, 12, 10, 10, volley(3, 0.25f, 0x9fe8ff, "slow", 4), (p, e, w, s, r, a) -> {
                    p.rot("body", 0.15f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -0.6f, 0, 0, w, s, r);
                    if (striking(s, r) || w > 0.5f) { float q = Mth.sin(a * 1.6f); p.g("body")[6] = p.g("body")[8] = 1 + 0.12f * q; p.g("jaw")[0] -= 0.3f * Math.max(0, q); }
                });
    }

    // ---------------------------------------------------------------- 9. Longarm Sweeper – towering red/orange plush with floor-dragging arms
    static CreatureDef longarmSweeper() {
        int red = 0xd9482b, redD = 0x9e2a18, orange = 0xf08a2a, mouth = 0x18040a, teeth = 0xf2efe4, eye = 0xffe14a;
        CreatureDef d = CreatureDef.of("daddy_long_legs", "Daddy Long Legs", "ダディ・ロングレッグス").size(1.3f, 3.6f).hp(160).dmg(9).speed(0.32).kbRes(0.7).egg(red, orange).spawn(1)
                .part("legL", null, -5, 26, 0, 7, 26, 7, 0, -13, 0, redD).walk(0.8f, 0)
                .part("footL", "legL", 0, -26, -1, 8, 3, 10, 0, 1.5f, 0, orange)
                .part("legR", null, 5, 26, 0, 7, 26, 7, 0, -13, 0, redD).walk(0.8f, P)
                .part("footR", "legR", 0, -26, -1, 8, 3, 10, 0, 1.5f, 0, orange)
                .part("body", null, 0, 26, 0, 18, 22, 11, 0, 11, 0, red).sway(0.02f, 0.1f, 0, 2)
                .part("belly", "body", 0, 10, -5.6f, 11, 12, 0.6f, 0, 0, 0, 0xf2a65a)
                .part("tuftL", "body", -8, 22, 0, 4, 3, 4, 0, 1.5f, 0, redD)
                .part("tuftR", "body", 8, 22, 0, 4, 3, 4, 0, 1.5f, 0, redD)
                .part("head", "body", 0, 22, 0, 22, 18, 16, 0, 9, 0, red)
                .part("lipU", "head", 0, 6, -8.2f, 20, 2.5f, 1, 0, 0, 0, orange)
                .part("jaw", "head", 0, 4.5f, -8, 20, 6, 4, 0, -3, -1, mouth)
                .part("lipL", "jaw", 0, -6, -3.2f, 20, 1.5f, 1, 0, 0, 0, orange)
                .part("nose", "head", 0, 9.5f, -8.5f, 5, 3, 1, 0, 0, 0, redD)
                .part("eyeL", "head", -6, 13, -8.2f, 5, 5, 0.8f, 0, 0, 0, eye).glow()
                .part("pupilL", "eyeL", 0, 0, -0.5f, 1.6f, 3.4f, 0.3f, 0, 0, 0, 0x111111)
                .part("eyeR", "head", 6, 13, -8.2f, 5, 5, 0.8f, 0, 0, 0, eye).glow()
                .part("pupilR", "eyeR", 0, 0, -0.5f, 1.6f, 3.4f, 0.3f, 0, 0, 0, 0x111111)
                .part("earL", "head", -9, 17, 0, 4, 7, 5, 0, 3.5f, 0, redD)
                .part("earR", "head", 9, 17, 0, 4, 7, 5, 0, 3.5f, 0, redD);
        for (int i = 0; i < 5; i++) d.part("toothU" + i, "head", -8 + i * 4, 4.8f, -8.8f, 1.6f, 2.4f, 0.6f, 0, -1.2f, 0, teeth);
        for (int i = 0; i < 4; i++) d.part("toothL" + i, "jaw", -6 + i * 4, -5, -3.2f, 1.6f, 2, 0.6f, 0, 1, 0, teeth);
        return d.part("armL", "body", -11, 20, 0, 5, 44, 5, 0, -22, 0, red).sway(0.04f, 0.08f, 0, 2)
                .part("handL", "armL", 0, -44, 0, 9, 6, 9, 0, -1, 0, orange)
                .part("fingerL1", "handL", -2.5f, -4, -3.5f, 2, 3, 2, 0, -1.5f, 0, orange)
                .part("fingerL2", "handL", 2.5f, -4, -3.5f, 2, 3, 2, 0, -1.5f, 0, orange)
                .part("armR", "body", 11, 20, 0, 5, 44, 5, 0, -22, 0, red).sway(0.04f, 0.08f, P, 2)
                .part("handR", "armR", 0, -44, 0, 9, 6, 9, 0, -1, 0, orange)
                .part("fingerR1", "handR", -2.5f, -4, -3.5f, 2, 3, 2, 0, -1.5f, 0, orange)
                .part("fingerR2", "handR", 2.5f, -4, -3.5f, 2, 3, 2, 0, -1.5f, 0, orange)
                // Floor sweep (signature): coils the torso, drags the right arm back, then a low 240° sweep that bowls everything aside
                .custom(9, 5, 0, 70, 16, 6, 14, (m, s, t) -> {
                    blast(-0.5, 0, fx(MobEffects.MOVEMENT_SLOWDOWN, 40, 1), ParticleTypes.SWEEP_ATTACK, SoundEvents.PLAYER_ATTACK_SWEEP, 0).tick(m, s, t);
                    if (t == s.wind() + 1) { Vec3 side = Fx.flat(m).yRot(-1.57f); for (LivingEntity e : Fx.arc(m, s.reach(), -0.5)) { e.setDeltaMovement(side.x * 1.6, 0.45, side.z * 1.6); e.hurtMarked = true; } }
                }, (p, e, w, s, r, a) -> {
                    p.rot("body", 0.2f, -0.9f, 0, -0.35f, 1.2f, 0, w, s, r); p.pos("body", 0, -3, 2, 0, -5, -2, w, s, r);
                    p.rot("armR", -0.7f, 0, 0.7f, 1.2f, 0, -1.0f, w, s, r); p.rot("armL", 0.7f, 0, 0.4f, -0.4f, 0, -0.9f, w, s, r);
                    p.rot("head", 0.1f, 0.7f, 0, 0, -0.9f, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -0.6f, 0, 0, w, s, r);
                    pair(p, "legL", "legR", -0.3f, 0, 0, 0.35f, 0, 0, w, s, r);
                })
                // Long reach: arm cocked behind the head, lashes out across the room
                .atk(Atk.WHIP, 8, 9, 3, 90).anim((p, e, w, s, r, a) -> {
                    p.rot("armR", 2.9f, 0.3f, 0.4f, 1.5f, 0, 0, w, s, r); p.scl("armR", 1, 1, 1, 1, 1.5f, 1, w, s, r);
                    p.rot("body", 0.3f, -0.5f, 0, -0.4f, 0.3f, 0, w, s, r); p.rot("armL", 0.4f, 0, -0.5f, -0.4f, 0, -0.3f, w, s, r);
                    p.rot("head", 0.2f, 0.4f, 0, -0.1f, -0.2f, 0, w, s, r);
                })
                // Twin hammer: both arms raised to the ceiling, brought down in a crushing double fist
                .atk(Atk.SLAM, 10, 3.5f, 0, 110).anim((p, e, w, s, r, a) -> {
                    pair(p, "armL", "armR", 3.1f, 0, 0.2f, 0.6f, 0, 0.05f, w, s, r); p.rot("body", 0.3f, 0, 0, -0.8f, 0, 0, w, s, r);
                    p.pos("body", 0, 2, 3, 0, -6, -5, w, s, r); p.rot("head", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("jaw", -0.9f, 0, 0, 0, 0, 0, w, s, r);
                    pair(p, "legL", "legR", 0.1f, 0, 0, -0.4f, 0, 0, w, s, r);
                })
                // Snatch: one long arm darts out, then hauls the victim up to its gnashing mouth
                .atk(Atk.GRAB, 7, 4, 0, 180, 40, "").anim((p, e, w, s, r, a) -> {
                    p.rot("armR", 0.9f, 0, 0.6f, 1.6f, 0, -0.1f, w, s, r); p.rot("armL", 0.3f, 0, -0.4f, 0.4f, 0, -0.5f, w, s, r);
                    p.rot("body", 0.15f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("head", 0.2f, 0, 0, -0.1f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) { p.g("armR")[0] = 2.1f; p.g("armR")[2] = -0.3f; p.g("jaw")[0] = -0.5f - 0.5f * Mth.sin(a * 1.1f); p.g("head")[0] = 0.25f; }
                })
                // Roar: chest thrust out, arms flung wide, head craned forward
                .atk(Atk.SCREAM, 3, 9, 3, 260).anim((p, e, w, s, r, a) -> {
                    p.rot("head", 0.5f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("jaw", -0.6f, 0, 0, -1.2f, 0, 0, w, s, r); p.rot("body", 0.4f, 0, 0, -0.25f, 0, 0, w, s, r);
                    pair(p, "armL", "armR", 0.4f, 0, 0.5f, 0.6f, 0, 1.3f, w, s, r); p.scl("body", 1, 1, 1, 1.08f, 1.04f, 1.08f, w, s, r);
                    if (striking(s, r)) { p.add("head", 0, 0, Mth.sin(a * 3f) * 0.1f); p.add("armL", 0, 0, -Mth.sin(a * 3f) * 0.1f); p.add("armR", 0, 0, Mth.sin(a * 3f) * 0.1f); }
                });
    }

    private BatchA() {}
}
