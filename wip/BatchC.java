package com.poppy.mod.creature;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** Batch C – original "bedtime critters" toy-mascot roster (see DEV_GUIDE.md). */
public final class BatchC {
    static final float P = (float) Math.PI;

    public static List<CreatureDef> all() {
        List<CreatureDef> l = new ArrayList<>();
        l.add(lullabyLynx());
        l.add(patchworkPup());
        l.add(brassMammoth());
        l.add(springJackalope());
        l.add(rumbleRooster());
        l.add(crayonKirin());
        l.add(truffleBoar());
        l.add(snuggleGrizzly());
        return l;
    }

    // ───────────────────────────── server-side helpers ─────────────────────────────
    static void snd(PoppyMob m, SoundEvent e, float vol, float pitch) { m.playSound(e, vol, pitch); }

    /** Radial shockwave at c: damage falls off with distance, launches upward. */
    static void ring(PoppyMob m, Vec3 c, double r, float dmg, double up, ParticleOptions pt) {
        if (!(m.level() instanceof ServerLevel lv)) return;
        for (LivingEntity e : Fx.foes(m, c, r)) {
            Fx.hit(m, e, dmg * (float) (1.0 - 0.5 * e.position().distanceTo(c) / r), 1.0f, e.position().subtract(c));
            e.setDeltaMovement(e.getDeltaMovement().add(0, up, 0)); e.hurtMarked = true;
        }
        for (int i = 0; i < 32; i++) { double a = i / 32.0 * Mth.TWO_PI; lv.sendParticles(pt, c.x + Math.cos(a) * r * 0.7, m.getY() + 0.15, c.z + Math.sin(a) * r * 0.7, 0, Math.cos(a), 0.05, Math.sin(a), 0.25); }
        lv.sendParticles(ParticleTypes.EXPLOSION, c.x, m.getY() + 0.3, c.z, 2, r * 0.25, 0, r * 0.25, 0);
    }

    /** Leap at the target when the windup ends, shockwave when landing. */
    static AttackSpec.Custom leapSlam(double up, double radius, ParticleOptions pt) {
        return (m, s, t) -> {
            if (t == s.wind() + 1) {
                snd(m, SoundEvents.SLIME_JUMP, 1.5f, 0.6f);
                Vec3 d = m.getTarget() != null ? m.getTarget().position().subtract(m.position()) : Fx.flat(m).scale(4);
                Vec3 h = new Vec3(d.x, 0, d.z); h = h.lengthSqr() < 1e-4 ? Vec3.ZERO : h.normalize().scale(Math.min(1.6, 0.3 + h.length() * 0.14));
                m.setDeltaMovement(h.x, up, h.z); m.hasImpulse = true;
            } else if (t > s.wind() + 4 && m.onGround() && m.hitOnce.add(-1)) {
                snd(m, SoundEvents.GENERIC_EXPLODE, 1.2f, 0.8f);
                ring(m, m.position(), radius, s.dmg(), 0.6, pt);
            }
        };
    }

    /** Hits in the front arc at the given strike-relative ticks (combo). */
    static AttackSpec.Custom combo(float kb, double up, SoundEvent e, int... at) {
        return (m, s, t) -> {
            for (int k = 0; k < at.length; k++) if (t == s.wind() + 1 + at[k]) {
                snd(m, e, 1.2f, 0.8f + k * 0.15f);
                Vec3 look = Fx.flat(m);
                for (LivingEntity f : Fx.arc(m, s.reach(), 0.2)) { Fx.hit(m, f, s.dmg() / at.length * 1.3f, kb, look); if (up > 0) { f.setDeltaMovement(f.getDeltaMovement().add(0, up, 0)); f.hurtMarked = true; } }
            }
        };
    }

    /** Straight lunge hitting everything along a line (horn / tusk thrust). */
    static AttackSpec.Custom thrust(float kb, double up, double lunge) {
        return (m, s, t) -> {
            if (t != s.wind() + 1 || !(m.level() instanceof ServerLevel lv)) return;
            Vec3 look = Fx.flat(m);
            m.setDeltaMovement(look.x * lunge, m.getDeltaMovement().y, look.z * lunge); m.hasImpulse = true;
            Vec3 a = m.position().add(0, m.getBbHeight() * 0.55, 0), b = a.add(look.scale(s.reach() + m.getBbWidth() * 0.5));
            for (int i = 0; i <= 10; i++) { Vec3 p = a.lerp(b, i / 10.0); lv.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0); }
            snd(m, SoundEvents.TRIDENT_THROW, 1.4f, 0.7f);
            for (LivingEntity e : Fx.line(m, a, b, 0.9)) { Fx.hit(m, e, s.dmg(), kb, look); e.setDeltaMovement(e.getDeltaMovement().add(0, up, 0)); e.hurtMarked = true; }
        };
    }

    /** Volley of bolts at the target, one per listed tick. */
    static AttackSpec.Custom volley(int color, String style, float speed, double lob, int... at) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv) || m.getTarget() == null) return;
            for (int k = 0; k < at.length; k++) if (t == s.wind() + 1 + at[k]) {
                snd(m, SoundEvents.EGG_THROW, 1.3f, 0.6f + k * 0.2f);
                Vec3 o = m.getEyePosition().subtract(0, 0.3, 0), d = m.getTarget().getEyePosition().subtract(o).normalize();
                float spread = (k - (at.length - 1) / 2f) * 0.12f;
                d = d.yRot(spread).add(0, lob, 0);
                PoppyBolt b = new PoppyBolt(lv, m, s.dmg(), color, style); b.setPos(o.x, o.y, o.z); b.shoot(d.x, d.y, d.z, speed, 1.5f); lv.addFreshEntity(b);
            }
        };
    }

    // ───────────────────────────── 1. Lullaby Lynx (boss) ─────────────────────────────
    /** Lanky nightcap-wearing lynx boss: sleep-mist breath, dream blink, double rake, pounce, star-lantern flare. */
    static CreatureDef lullabyLynx() {
        int indigo = 0x3b3a7a, navy = 0x1d1b45, lav = 0xb9c3e6, gold = 0xf5d36b, cyan = 0x9ff3ff, slit = 0x0b0b18, ivory = 0xf4f1e6,
                pink = 0xd27aa8, capB = 0x5a7bd6, capW = 0xeef0ff, mouth = 0x1a0d2a;
        Vector3f mistC = new Vector3f(0.55f, 0.75f, 1f);
        return CreatureDef.of("lullaby_lynx", "Lullaby Lynx", "ララバイ・リンクス").size(1.4f, 4.8f).hp(620).dmg(12).speed(0.33).kbRes(0.9).armor(6).egg(indigo, gold).boss()
                .part("legL", null, -5, 30, 0, 7, 30, 7, 0, -15, 0, navy).walk(0.8f, 0)
                .part("footL", "legL", 0, -30, 0, 8, 3, 11, 0, 1.5f, -2, indigo)
                .part("legR", null, 5, 30, 0, 7, 30, 7, 0, -15, 0, navy).walk(0.8f, P)
                .part("footR", "legR", 0, -30, 0, 8, 3, 11, 0, 1.5f, -2, indigo)
                .part("body", null, 0, 30, 0, 16, 26, 10, 0, 13, 0, indigo).sway(0.03f, 0.08f, 0, 2)
                .part("belly", "body", 0, 12, -5.2f, 10, 16, 1, 0, 0, 0, lav)
                .part("lantern", "body", 0, 19, -5.8f, 5, 5, 2, 0, 0, 0, gold).glow()
                .part("rayH", "body", 0, 19, -6.2f, 10, 1, 1, 0, 0, 0, gold).glow()
                .part("rayV", "body", 0, 19, -6.2f, 1, 10, 1, 0, 0, 0, gold).glow()
                .part("tail", "body", 0, 3, 5, 3, 3, 24, 0, 0, 12, indigo).sway(0.25f, 0.07f, 0, 1)
                .part("tailTip", "tail", 0, 0, 24, 5, 5, 5, 0, 0, 0, lav)
                .part("head", "body", 0, 26, 0, 18, 14, 14, 0, 7, -1, indigo)
                .part("muzzle", "head", 0, 4.5f, -8.5f, 9, 5, 3, 0, 0, 0, lav)
                .part("nose", "head", 0, 7, -10.2f, 3, 2, 1, 0, 0, 0, pink)
                .part("jaw", "head", 0, 2, -6, 12, 3, 8, 0, -1, -3, mouth)
                .part("fangL", "head", -2.5f, 2.5f, -10.2f, 1.5f, 3, 1, 0, -1.2f, 0, ivory)
                .part("fangR", "head", 2.5f, 2.5f, -10.2f, 1.5f, 3, 1, 0, -1.2f, 0, ivory)
                .part("eyeL", "head", -4.5f, 9.5f, -8.2f, 5, 3, 1, 0, 0, 0, cyan).glow()
                .part("eyeR", "head", 4.5f, 9.5f, -8.2f, 5, 3, 1, 0, 0, 0, cyan).glow()
                .part("pupilL", "head", -4.5f, 9.5f, -8.8f, 1, 3, 0.5f, 0, 0, 0, slit)
                .part("pupilR", "head", 4.5f, 9.5f, -8.8f, 1, 3, 0.5f, 0, 0, 0, slit)
                .part("lidL", "head", -4.5f, 11.4f, -8.3f, 5.6f, 1, 1.2f, 0, 0, 0, navy)
                .part("lidR", "head", 4.5f, 11.4f, -8.3f, 5.6f, 1, 1.2f, 0, 0, 0, navy)
                .part("whiskL", "head", -7, 4.5f, -9.5f, 7, 0.5f, 0.5f, -1, 0, 0, ivory)
                .part("whiskR", "head", 7, 4.5f, -9.5f, 7, 0.5f, 0.5f, 1, 0, 0, ivory)
                .part("earL", "head", -6, 13, 0, 4, 7, 3, 0, 3.5f, 0, indigo).sway(0.06f, 0.1f, 0, 2)
                .part("tuftL", "earL", 0, 7, 0, 1.5f, 4, 1.5f, 0, 2, 0, navy)
                .part("earR", "head", 6, 13, 0, 4, 7, 3, 0, 3.5f, 0, indigo).sway(0.06f, 0.1f, P, 2)
                .part("tuftR", "earR", 0, 7, 0, 1.5f, 4, 1.5f, 0, 2, 0, navy)
                .part("capBase", "head", 0, 14, 0, 19, 3, 15, 0, 1.5f, -1, capW)
                .part("cap1", "head", 0, 17, 1, 15, 4, 12, 0, 2, 0, capB).sway(0.06f, 0.06f, 0, 0)
                .part("cap2", "cap1", 1, 4, 2, 11, 4, 9, 0, 2, 0, capW)
                .part("cap3", "cap2", 1, 4, 3, 7, 4, 6, 0, 2, 1, capB).sway(0.12f, 0.06f, 1, 0)
                .part("pompom", "cap3", 0, 3, 4, 5, 5, 5, 0, 0, 0, capW)
                .part("armL", "body", -10, 24, 0, 5, 30, 5, 0, -15, 0, indigo).sway(0.05f, 0.08f, 0, 2)
                .part("pawL", "armL", 0, -30, 0, 7, 6, 7, 0, -3, 0, lav)
                .part("clawL1", "pawL", -2, -6, -3, 1, 3, 1, 0, -1.5f, 0, ivory)
                .part("clawL2", "pawL", 0, -6, -3, 1, 3, 1, 0, -1.5f, 0, ivory)
                .part("clawL3", "pawL", 2, -6, -3, 1, 3, 1, 0, -1.5f, 0, ivory)
                .part("armR", "body", 10, 24, 0, 5, 30, 5, 0, -15, 0, indigo).sway(0.05f, 0.08f, P, 2)
                .part("pawR", "armR", 0, -30, 0, 7, 6, 7, 0, -3, 0, lav)
                .part("clawR1", "pawR", -2, -6, -3, 1, 3, 1, 0, -1.5f, 0, ivory)
                .part("clawR2", "pawR", 0, -6, -3, 1, 3, 1, 0, -1.5f, 0, ivory)
                .part("clawR3", "pawR", 2, -6, -3, 1, 3, 1, 0, -1.5f, 0, ivory)
                // double rake: right claw sweeps in, body twists, left claw back-hands
                .custom(12, 4.2f, 0, 34, 10, 10, 10, combo(0.9f, 0, SoundEvents.PLAYER_ATTACK_SWEEP, 0, 5), (p, e, w, s, r, age) -> {
                    float s1 = Mth.clamp(s * 2, 0, 1), s2 = Mth.clamp(s * 2 - 1, 0, 1);
                    p.rot("armR", 1.9f, -1.2f, 0.6f, 1.2f, 1.1f, 0.1f, w, s1, r);
                    p.rot("armL", 0.5f, 0, -0.4f, 1.6f, -1.2f, -0.2f, w, s2, r);
                    p.rot("body", -0.2f, -0.6f, 0, -0.35f, 0.5f * (1 - s2) - 0.4f * s2, 0, w, s, r);
                    p.rot("head", 0.15f, 0.4f, 0, 0.1f, -0.3f, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.5f, 0, 0, w, s, r);
                    p.pos("body", 0, -2, 3, 0, -3, -5, w, s, r); p.rot("legL", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("legR", -0.4f, 0, 0, 0.5f, 0, 0, w, s, r);
                })
                // pounce: sink low, tail flicks up, launch with both paws forward, land crouched
                .atk(Atk.POUNCE, 13, 11f, 3.5f, 140).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -10, 4, 0, 2, -12, w, s, r); p.rot("body", -0.7f, 0, 0, -1.25f, 0, 0, w, s, r);
                    p.rot("armL", 0.6f, 0, -0.3f, 2.9f, 0, -0.25f, w, s, r); p.rot("armR", 0.6f, 0, 0.3f, 2.9f, 0, 0.25f, w, s, r);
                    p.rot("legL", 1.3f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("legR", 1.3f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("head", 0.7f, 0, 0, 1.0f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.9f, 0, 0, w, s, r); p.rot("tail", -0.8f, 0, 0, 0.6f, 0, 0, w, s, r);
                    p.add("tail", 0, Mth.sin(age * 0.9f) * 0.3f * w * (1 - r), 0);
                })
                // lullaby mist: deep inhale (chest lantern swells), long sleepy exhale cone, leaves a drowsy blue cloud
                .custom(4, 9f, 2.5f, 260, 20, 26, 14, (m, s, t) -> {
                    if (!(m.level() instanceof ServerLevel lv)) return;
                    Vec3 look = Fx.flat(m);
                    if (t == 6) snd(m, SoundEvents.CAT_PURR, 3f, 0.4f);
                    if (t == s.wind() + 1) {
                        snd(m, SoundEvents.CAT_HISS, 3f, 0.5f);
                        AreaEffectCloud c = new AreaEffectCloud(lv, m.getX() + look.x * 3.5, m.getY(), m.getZ() + look.z * 3.5);
                        c.setOwner(m); c.setRadius(4f); c.setDuration(220); c.setRadiusPerTick(-0.004f); c.setParticle(new DustParticleOptions(mistC, 2.4f));
                        c.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2)); c.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 160, 1));
                        c.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0)); c.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0));
                        lv.addFreshEntity(c);
                    }
                    if (t > s.wind() && t <= s.wind() + s.strike()) {
                        Vec3 o = m.getEyePosition().subtract(0, 0.5, 0);
                        for (int i = 1; i <= 4; i++) { Vec3 q = o.add(look.scale(i * 1.6)); lv.sendParticles(new DustParticleOptions(mistC, 2.2f), q.x, q.y - i * 0.25, q.z, 3, 0.25 * i, 0.2, 0.25 * i, 0.02); }
                        if (t % 6 == 0) for (LivingEntity f : Fx.arc(m, s.reach(), 0.55)) { f.hurt(m.damageSources().mobAttack(m), s.dmg()); f.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1)); }
                    }
                }, (p, e, w, s, r, age) -> {
                    p.rot("head", -0.5f, 0, 0, 0.35f, 0, 0, w, s, r); p.rot("jaw", 0.2f, 0, 0, 0.9f, 0, 0, w, s, r);
                    p.rot("body", 0.25f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, 1, 2, 0, -2, -2, w, s, r);
                    p.scl("body", 1.12f, 1.05f, 1.25f, 0.95f, 1, 0.9f, w, s, r);
                    p.scl("lantern", 1.8f, 1.8f, 1.8f, 1.2f, 1.2f, 1.2f, w, s, r);
                    p.rot("armL", 0.3f, 0, -0.7f, 0.9f, 0, -0.3f, w, s, r); p.rot("armR", 0.3f, 0, 0.7f, 0.9f, 0, 0.3f, w, s, r);
                    p.add("head", 0, Mth.sin(age * 0.5f) * 0.25f * s * (1 - r), 0); // sweeps the cone side to side
                })
                // dream blink: curls up and fades, reappears behind the target and rakes
                .custom(14, 16f, 5f, 220, 14, 6, 14, (m, s, t) -> {
                    if (!(m.level() instanceof ServerLevel lv)) return;
                    LivingEntity tg = m.getTarget();
                    if (t == s.wind() && tg != null) {
                        lv.sendParticles(ParticleTypes.REVERSE_PORTAL, m.getX(), m.getY() + 2, m.getZ(), 60, 0.6, 1.2, 0.6, 0.05);
                        Vec3 d = tg.position().subtract(m.position()); d = new Vec3(d.x, 0, d.z); d = d.lengthSqr() < 1e-4 ? Fx.flat(m) : d.normalize();
                        Vec3 to = tg.position().add(d.scale(1.6 + tg.getBbWidth() * 0.5));
                        m.teleportTo(to.x, tg.getY(), to.z);
                        float yaw = (float) (Mth.atan2(-(-d.x), -d.z) * Mth.RAD_TO_DEG);
                        m.setYRot(yaw); m.setYHeadRot(yaw); m.yBodyRot = yaw;
                        snd(m, SoundEvents.ENDERMAN_TELEPORT, 2f, 0.6f);
                        lv.sendParticles(ParticleTypes.REVERSE_PORTAL, to.x, tg.getY() + 2, to.z, 60, 0.6, 1.2, 0.6, 0.05);
                    }
                    if (t == s.wind() + 2) { snd(m, SoundEvents.PLAYER_ATTACK_STRONG, 1.5f, 0.6f);
                        for (LivingEntity f : Fx.arc(m, 3.8, 0.0)) { Fx.hit(m, f, s.dmg(), 1.1f, Fx.flat(m)); f.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0)); } }
                }, (p, e, w, s, r, age) -> {
                    float fade = w < 1 ? 1 - 0.75f * Pose_ease(w) : (s < 1 ? 0.25f + 0.75f * s * 3 : 1);
                    fade = Mth.clamp(fade, 0.25f, 1);
                    p.g("body")[6] = fade; p.g("body")[7] = fade; p.g("body")[8] = fade;
                    p.g("legL")[7] = fade; p.g("legR")[7] = fade;
                    p.rot("body", -0.9f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("armR", 0.4f, 0, 0.3f, 2.2f, 0.9f, 0, w, s, r); p.rot("armL", 0.4f, 0, -0.3f, 2.0f, -0.9f, 0, w, s, r);
                    p.rot("tail", -1.0f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.8f, 0, 0, w, s, r);
                })
                // star-lantern flare: cups the chest lantern and hurls a blinding star
                .atk(Atk.SHOOT, 9, 20f, 5f, 90, 0xf5d36b, "blind").anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 1.3f, 0.6f, 0, 1.7f, -0.2f, 0, w, s, r); p.rot("armR", 1.3f, -0.6f, 0, 2.0f, 0.5f, 0.3f, w, s, r);
                    p.rot("body", 0.3f, 0.3f, 0, -0.3f, -0.4f, 0, w, s, r); p.scl("lantern", 2.2f, 2.2f, 2.2f, 1f, 1f, 1f, w, s, r);
                    p.rot("head", 0.2f, 0, 0, -0.1f, 0, 0, w, s, r);
                })
                // call the pups: yowl with head thrown back
                .atk(Atk.SUMMON, 0, 18f, 0, 600, 2, "patchwork_pup").anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.9f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("jaw", 0.6f, 0, 0, 1.0f, 0, 0, w, s, r);
                    p.rot("armL", 0.3f, 0, -1.3f, 0.2f, 0, -0.9f, w, s, r); p.rot("armR", 0.3f, 0, 1.3f, 0.2f, 0, 0.9f, w, s, r);
                    p.rot("body", 0.25f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("tail", -1.0f, 0, 0, -0.6f, 0, 0, w, s, r);
                });
    }

    static float Pose_ease(float x) { return com.poppy.mod.client.Pose.ease(x); }

    // ───────────────────────────── 2. Patchwork Pup ─────────────────────────────
    /** Scrappy stitched-together terrier: claw flurry, lunging bite, leap, shoulder tackle. */
    static CreatureDef patchworkPup() {
        int tan = 0xc58a4b, brown = 0x7a4a22, cream = 0xf1dcb5, blue = 0x4f7fb8, stitch = 0x2a1a10, black = 0x111111, white = 0xffffff, red = 0xb8322b, gold = 0xe8c14a;
        return CreatureDef.of("patchwork_pup", "Patchwork Pup", "パッチワーク・パップ").size(0.9f, 2.3f).hp(70).dmg(7).speed(0.36).kbRes(0.2).egg(tan, blue).spawn(2)
                .part("legL", null, -3, 12, 0, 5, 12, 5, 0, -6, 0, brown).walk(1.0f, 0)
                .part("footL", "legL", 0, -12, 0, 5.5f, 2, 7, 0, 1, -1, cream)
                .part("legR", null, 3, 12, 0, 5, 12, 5, 0, -6, 0, tan).walk(1.0f, P)
                .part("footR", "legR", 0, -12, 0, 5.5f, 2, 7, 0, 1, -1, cream)
                .part("body", null, 0, 12, 0, 12, 13, 8, 0, 6.5f, 0, tan)
                .part("chest", "body", 0, 6, -4.1f, 7, 8, 1, 0, 0, 0, cream)
                .part("patch", "body", -3, 4, -4.3f, 4, 4, 0.6f, 0, 0, 0, blue)
                .part("st1", "body", -3, 6.2f, -4.7f, 3, 0.5f, 0.5f, 0, 0, 0, stitch)
                .part("st2", "body", -3, 1.8f, -4.7f, 3, 0.5f, 0.5f, 0, 0, 0, stitch)
                .part("st3", "body", -5.2f, 4, -4.7f, 0.5f, 3, 0.5f, 0, 0, 0, stitch)
                .part("st4", "body", -0.8f, 4, -4.7f, 0.5f, 3, 0.5f, 0, 0, 0, stitch)
                .part("backPatch", "body", 2, 8, 4.1f, 5, 6, 0.6f, 0, 0, 0, brown)
                .part("collar", "body", 0, 12.5f, 0, 10, 2, 9, 0, 0, 0, red)
                .part("tag", "body", 0, 11, -4.8f, 2, 2.5f, 0.6f, 0, 0, 0, gold)
                .part("tail", "body", 0, 3, 4, 2, 2, 6, 0, 1, 3, brown).sway(0.6f, 0.5f, 0, 1)
                .part("head", "body", 0, 13, 0, 12, 10, 10, 0, 5, 0, tan)
                .part("snout", "head", 0, 3, -6.5f, 6, 4, 4, 0, 0, 0, cream)
                .part("nose", "head", 0, 4.8f, -8.7f, 3, 2, 1, 0, 0, 0, black)
                .part("jaw", "head", 0, 1.5f, -5, 5, 2, 4, 0, -1, -2, 0xd9bf94)
                .part("toothL", "jaw", -1.5f, 0, -3.9f, 1, 1.2f, 0.5f, 0, 0, 0, white)
                .part("toothR", "jaw", 1.5f, 0, -3.9f, 1, 1.2f, 0.5f, 0, 0, 0, white)
                .part("tongue", "jaw", 0, -0.8f, -4.2f, 2, 0.5f, 2, 0, 0, 0, 0xe0637a)
                .part("eyePatch", "head", 3, 7, -5.1f, 5, 5, 0.6f, 0, 0, 0, brown)
                .part("eyeL", "head", -3, 7, -5.3f, 3, 3, 1, 0, 0, 0, black)
                .part("eyeR", "head", 3, 7, -5.6f, 3, 3, 1, 0, 0, 0, black)
                .part("glintL", "head", -3.6f, 7.6f, -5.9f, 0.8f, 0.8f, 0.3f, 0, 0, 0, white).glow()
                .part("glintR", "head", 2.4f, 7.6f, -6.2f, 0.8f, 0.8f, 0.3f, 0, 0, 0, white).glow()
                .part("headSt", "head", 0, 10.1f, -2, 0.5f, 0.5f, 6, 0, 0, 0, stitch)
                .part("earL", "head", -6, 9, 0, 2, 7, 4, -0.5f, -3, 0, brown).sway(0.12f, 0.15f, 0, 2)
                .part("earR", "head", 6, 9, 0, 2, 7, 4, 0.5f, -3, 0, brown).sway(0.12f, 0.15f, P, 2)
                .part("armL", "body", -7.5f, 12, 0, 4, 11, 4, 0, -5.5f, 0, tan).walk(0.8f, P)
                .part("pawL", "armL", 0, -11, 0, 4.5f, 3, 4.5f, 0, -1.5f, 0, cream)
                .part("clawL1", "pawL", -1, -3, -2, 1, 1.5f, 1, 0, -0.5f, 0, white)
                .part("clawL2", "pawL", 1, -3, -2, 1, 1.5f, 1, 0, -0.5f, 0, white)
                .part("armR", "body", 7.5f, 12, 0, 4, 11, 4, 0, -5.5f, 0, tan).walk(0.8f, 0)
                .part("pawR", "armR", 0, -11, 0, 4.5f, 3, 4.5f, 0, -1.5f, 0, cream)
                .part("clawR1", "pawR", -1, -3, -2, 1, 1.5f, 1, 0, -0.5f, 0, white)
                .part("clawR2", "pawR", 1, -3, -2, 1, 1.5f, 1, 0, -0.5f, 0, white)
                // claw flurry: alternating left-right scratches
                .custom(8, 3.2f, 0, 30, 8, 12, 8, combo(0.4f, 0, SoundEvents.PLAYER_ATTACK_SWEEP, 0, 4, 8), (p, e, w, s, r, age) -> {
                    float k = Mth.sin(s * P * 3);
                    p.rot("armR", 2.0f, 0, 0.3f, 1.4f + 0.7f * k, 0.6f * k, 0, w, s, r);
                    p.rot("armL", 2.0f, 0, -0.3f, 1.4f - 0.7f * k, 0.6f * k, 0, w, s, r);
                    p.rot("body", -0.1f, 0, 0, -0.45f, 0.2f * k, 0, w, s, r); p.pos("body", 0, -1, 2, 0, -1, -3, w, s, r);
                    p.rot("head", 0.3f, 0, 0, 0.2f, -0.2f * k, 0, w, s, r); p.rot("jaw", 0.4f, 0, 0, 0.6f, 0, 0, w, s, r);
                    p.rot("tail", 0.6f, 0, 0, 0.8f, 0, 0, w, s, r);
                })
                // snapping bite: head cocks back, whole body lunges, jaws clamp, shake
                .atk(Atk.BITE, 8, 2.8f, 0, 40).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.4f, 0.3f, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.9f, 0, 0, -0.05f, 0, 0, w, s, r);
                    p.rot("body", 0.15f, 0, 0, -0.7f, 0, 0, w, s, r); p.pos("body", 0, -1, 3, 0, -2, -7, w, s, r);
                    p.rot("armL", -0.4f, 0, -0.4f, 1.2f, 0, -0.2f, w, s, r); p.rot("armR", -0.4f, 0, 0.4f, 1.2f, 0, 0.2f, w, s, r);
                    p.rot("legL", 0.4f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("legR", 0.4f, 0, 0, 0.5f, 0, 0, w, s, r);
                    if (r > 0) p.add("head", 0, 0, Mth.sin(age * 2.2f) * 0.35f * (1 - r));
                })
                // leap
                .atk(Atk.POUNCE, 8, 9f, 3f, 110).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -5, 3, 0, 1, -6, w, s, r); p.rot("body", -0.4f, 0, 0, -1.0f, 0, 0, w, s, r);
                    p.rot("armL", -0.6f, 0, -0.2f, 2.6f, 0, -0.3f, w, s, r); p.rot("armR", -0.6f, 0, 0.2f, 2.6f, 0, 0.3f, w, s, r);
                    p.rot("legL", 1.1f, 0, 0, -0.7f, 0, 0, w, s, r); p.rot("legR", 1.1f, 0, 0, -0.7f, 0, 0, w, s, r);
                    p.rot("head", 0.4f, 0, 0, 0.9f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.8f, 0, 0, w, s, r);
                    p.rot("earL", 0, 0, 0, -1.2f, 0, -0.4f, w, s, r); p.rot("earR", 0, 0, 0, -1.2f, 0, 0.4f, w, s, r);
                })
                // shoulder tackle: lowered head, ears flat
                .atk(Atk.CHARGE, 7, 10f, 4f, 160).anim((p, e, w, s, r, age) -> {
                    p.rot("body", -0.2f, 0.4f, 0, -0.7f, 0.3f, 0, w, s, r); p.pos("body", 0, -2, 3, 0, -2, -2, w, s, r);
                    p.rot("head", 0.5f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("earL", -1.0f, 0, 0, -1.4f, 0, 0, w, s, r); p.rot("earR", -1.0f, 0, 0, -1.4f, 0, 0, w, s, r);
                    p.rot("armL", -0.8f, 0, -0.2f, -1.0f, 0, -0.2f, w, s, r); p.rot("armR", 0.6f, 0, 0.6f, 0.9f, 0, 0.9f, w, s, r);
                    if (s > 0 && r <= 0) { float c = Mth.cos(age * 1.8f); p.add("legL", c * 1.1f, 0, 0); p.add("legR", -c * 1.1f, 0, 0); }
                });
    }

    // ───────────────────────────── 3. Brass Mammoth ─────────────────────────────
    /** Heavy quadruped with a brass bell on its trunk: trunk slam, rearing stomp, tusk charge, tusk toss, bell blast. */
    static CreatureDef brassMammoth() {
        int slate = 0x7d8ea3, dark = 0x55657a, brass = 0xd4a640, ivory = 0xf1ead2, ear = 0xd99aa5, red = 0xa8323a, black = 0x101010, white = 0xffffff, mouth = 0x6e2a35;
        return CreatureDef.of("brass_mammoth", "Brass Mammoth", "ブラス・マンモス").size(2.2f, 2.9f).hp(180).dmg(10).speed(0.24).kbRes(1.0).armor(6).egg(slate, brass).spawn(1)
                .part("body", null, 0, 22, 12, 24, 20, 34, 0, 7, -12, slate)
                .part("blanket", "body", 0, 17.5f, -12, 26, 2, 16, 0, 0, 0, red)
                .part("trimF", "body", 0, 17.5f, -20.2f, 26.4f, 2.4f, 1, 0, 0, 0, brass)
                .part("trimB", "body", 0, 17.5f, -3.8f, 26.4f, 2.4f, 1, 0, 0, 0, brass)
                .part("blanketL", "body", -13.1f, 13, -12, 1, 9, 16, 0, 0, 0, red)
                .part("blanketR", "body", 13.1f, 13, -12, 1, 9, 16, 0, 0, 0, red)
                .part("medalL", "body", -13.8f, 13, -12, 0.6f, 4, 4, 0, 0, 0, brass)
                .part("medalR", "body", 13.8f, 13, -12, 0.6f, 4, 4, 0, 0, 0, brass)
                .part("legLf", "body", -8, -4, -23, 8, 18, 8, 0, -9, 0, slate).walk(0.6f, 0)
                .part("toeLf", "legLf", 0, -18, -4, 6, 2, 1, 0, 1, 0, ivory)
                .part("legRf", "body", 8, -4, -23, 8, 18, 8, 0, -9, 0, slate).walk(0.6f, P)
                .part("toeRf", "legRf", 0, -18, -4, 6, 2, 1, 0, 1, 0, ivory)
                .part("legL", "body", -8, -4, -1, 8, 18, 8, 0, -9, 0, slate).walk(0.6f, P)
                .part("legR", "body", 8, -4, -1, 8, 18, 8, 0, -9, 0, slate).walk(0.6f, 0)
                .part("tail", "body", 0, 10, 5, 2, 10, 2, 0, -5, 0, dark).sway(0.2f, 0.1f, 0, 2)
                .part("tailTuft", "tail", 0, -10, 0, 3, 3, 3, 0, -1, 0, dark)
                .part("head", "body", 0, 12, -29, 18, 16, 12, 0, 0, -4, slate)
                .part("brow", "head", 0, 7, -10.2f, 18.5f, 3, 1, 0, 0, 0, dark)
                .part("eyeL", "head", -6, 3, -10.2f, 3, 3, 1, 0, 0, 0, white)
                .part("eyeR", "head", 6, 3, -10.2f, 3, 3, 1, 0, 0, 0, white)
                .part("pupilL", "head", -5.6f, 2.6f, -10.8f, 1.5f, 1.5f, 0.5f, 0, 0, 0, black)
                .part("pupilR", "head", 5.6f, 2.6f, -10.8f, 1.5f, 1.5f, 0.5f, 0, 0, 0, black)
                .part("jaw", "head", 0, -7, -6, 8, 3, 5, 0, -1, -1, mouth)
                .part("earL", "head", -9, 2, -2, 2, 16, 14, -1, 0, 2, slate).sway(0.15f, 0.1f, 0, 1)
                .part("earInL", "earL", -0.6f, 0, 0, 1, 12, 10, -1, 0, 2, ear)
                .part("earR", "head", 9, 2, -2, 2, 16, 14, 1, 0, 2, slate).sway(0.15f, 0.1f, P, 1)
                .part("earInR", "earR", 0.6f, 0, 0, 1, 12, 10, 1, 0, 2, ear)
                .part("trunk1", "head", 0, -3, -11, 6, 8, 6, 0, -4, 0, slate).sway(0.1f, 0.12f, 0, 0)
                .part("ring1", "trunk1", 0, -6, 0, 6.6f, 1, 6.6f, 0, 0, 0, dark)
                .part("trunk2", "trunk1", 0, -8, 0, 5, 8, 5, 0, -4, 0, slate).sway(0.12f, 0.12f, 0.8f, 0)
                .part("ring2", "trunk2", 0, -5, 0, 5.6f, 1, 5.6f, 0, 0, 0, dark)
                .part("trunk3", "trunk2", 0, -8, 0, 4, 6, 4, 0, -3, 0, slate)
                .part("bell", "trunk3", 0, -6, 0, 8, 3, 8, 0, -1, 0, brass)
                .part("bellIn", "trunk3", 0, -7.6f, 0, 5, 0.5f, 5, 0, 0, 0, 0x2a1a08)
                .part("tuskL", "head", -6, -5, -9, 3, 3, 12, 0, 0, -6, ivory)
                .part("tipL", "tuskL", 0, 0, -12, 2.5f, 5, 2.5f, 0, 2, 0, brass)
                .part("tuskR", "head", 6, -5, -9, 3, 3, 12, 0, 0, -6, ivory)
                .part("tipR", "tuskR", 0, 0, -12, 2.5f, 5, 2.5f, 0, 2, 0, brass)
                // trunk slam: trunk curls high overhead, head rears, then hammers the ground ahead
                .atk(Atk.SLAM, 11, 3.6f, 0, 70).anim((p, e, w, s, r, age) -> {
                    p.rot("trunk1", 2.4f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("trunk2", 0.9f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("trunk3", 0.6f, 0, 0, 0, 0, 0, w, s, r);
                    p.rot("head", -0.5f, 0, 0, 0.45f, 0, 0, w, s, r); p.rot("body", 0.12f, 0, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("earL", 0, 0.9f, 0, 0, -0.2f, 0, w, s, r); p.rot("earR", 0, -0.9f, 0, 0, 0.2f, 0, w, s, r);
                    p.rot("legLf", 0.4f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("legRf", 0.4f, 0, 0, -0.2f, 0, 0, w, s, r);
                })
                // rearing stomp: rises on hind legs, front legs paw the air, crashes down
                .atk(Atk.STOMP, 12, 4.5f, 0, 120).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.75f, 0, 0, -0.08f, 0, 0, w, s, r); p.pos("body", 0, 2, 0, 0, -1.5f, 0, w, s, r);
                    p.rot("legLf", -1.0f + Mth.sin(age * 0.6f) * 0.3f * (1 - s), 0, 0, -0.1f, 0, 0, w, s, r); p.rot("legRf", -1.0f - Mth.sin(age * 0.6f) * 0.3f * (1 - s), 0, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("legL", -0.75f, 0, 0, 0.08f, 0, 0, w, s, r); p.rot("legR", -0.75f, 0, 0, 0.08f, 0, 0, w, s, r);
                    p.rot("trunk1", 2.0f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("trunk2", 0.8f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r);
                    p.rot("earL", 0, 1.0f, 0, 0, 0.3f, 0, w, s, r); p.rot("earR", 0, -1.0f, 0, 0, -0.3f, 0, w, s, r);
                })
                // tusk charge: head lowered, ears pinned, legs pound
                .atk(Atk.CHARGE, 12, 14f, 5f, 200).anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.2f, 0, 0, 0.55f, 0, 0, w, s, r); p.rot("trunk1", -0.4f, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("trunk2", 0.2f, 0, 0, -0.4f, 0, 0, w, s, r);
                    p.rot("body", 0.05f, 0, 0, -0.08f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, 0, w, s, r);
                    p.rot("earL", 0, -0.6f, 0, 0, -0.9f, 0, w, s, r); p.rot("earR", 0, 0.6f, 0, 0, 0.9f, 0, w, s, r);
                    if (r <= 0) { float c = Mth.cos(age * (s > 0 ? 1.5f : 0.6f)) * (s > 0 ? 0.8f : 0.3f); p.add("legLf", c, 0, 0); p.add("legR", c, 0, 0); p.add("legRf", -c, 0, 0); p.add("legL", -c, 0, 0); }
                })
                // tusk toss: dips head low, scoops and flings foes skyward
                .custom(9, 3.6f, 0, 80, 12, 4, 14, (m, s, t) -> {
                    if (t != s.wind() + 1) return;
                    snd(m, SoundEvents.RAVAGER_ATTACK, 1.5f, 0.7f); Vec3 look = Fx.flat(m);
                    for (LivingEntity f : Fx.arc(m, s.reach(), 0.2)) { Fx.hit(m, f, s.dmg(), 0.4f, look); f.setDeltaMovement(f.getDeltaMovement().add(0, 1.15, 0)); f.hurtMarked = true; }
                }, (p, e, w, s, r, age) -> {
                    p.rot("head", 0.7f, 0.25f, 0, -0.7f, -0.2f, 0, w, s, r); p.rot("body", 0.0f, 0, 0, 0.15f, 0, 0, w, s, r);
                    p.rot("trunk1", -0.2f, 0, 0, 1.2f, 0, 0, w, s, r); p.pos("body", 0, -2, 2, 0, 1, -3, w, s, r);
                    p.rot("legLf", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r);
                })
                // bell blast: trunk raised like a horn, deafening blare in a cone
                .atk(Atk.SCREAM, 4, 9f, 2.5f, 260).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.3f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("trunk1", 1.0f, 0, 0, 2.6f, 0, 0, w, s, r); p.rot("trunk2", 0.4f, 0, 0, 0.5f, 0, 0, w, s, r);
                    p.rot("trunk3", 0, 0, 0, -0.4f, 0, 0, w, s, r); p.scl("bell", 1.2f, 1.2f, 1.2f, 1.6f + Mth.sin(age * 2f) * 0.1f, 1.4f, 1.6f, w, s, r);
                    p.rot("earL", 0, 0.3f, 0, 0, 1.2f, 0, w, s, r); p.rot("earR", 0, -0.3f, 0, 0, -1.2f, 0, w, s, r); p.rot("body", 0.2f, 0, 0, 0.3f, 0, 0, w, s, r);
                });
    }

    // ───────────────────────────── 4. Spring Jackalope ─────────────────────────────
    /** Antlered bunny on coil-spring legs: sky-high leap slam, bounce stomp, double kick, antler butt. */
    static CreatureDef springJackalope() {
        int mint = 0x8fd6b0, mintD = 0x5fae8a, white = 0xf7f7f2, coral = 0xf08a8a, wood = 0x8a5a2b, steel = 0xb8bec6, steelD = 0x6d737b, amber = 0xffb12a, black = 0x121212;
        return CreatureDef.of("spring_jackalope", "Spring Jackalope", "スプリング・ジャッカロープ").size(0.9f, 2.6f).hp(60).dmg(6).speed(0.38).kbRes(0.1).egg(mint, steel).spawn(2)
                .part("legL", null, -4, 14, 0, 6, 5, 6, 0, -2.5f, 0, mint).walk(0.9f, 0)
                .part("coilL1", "legL", 0, -5, 0, 4.5f, 1.5f, 4.5f, 0, -1, 0, steel)
                .part("coilL2", "legL", 0, -7.5f, 0, 4, 1.5f, 4, 0, -1, 0, steelD)
                .part("coilL3", "legL", 0, -10, 0, 4.5f, 1.5f, 4.5f, 0, -1, 0, steel)
                .part("footL", "legL", 0, -14, 0, 6, 3, 12, 0, 1.5f, -3, mint)
                .part("legR", null, 4, 14, 0, 6, 5, 6, 0, -2.5f, 0, mint).walk(0.9f, P)
                .part("coilR1", "legR", 0, -5, 0, 4.5f, 1.5f, 4.5f, 0, -1, 0, steel)
                .part("coilR2", "legR", 0, -7.5f, 0, 4, 1.5f, 4, 0, -1, 0, steelD)
                .part("coilR3", "legR", 0, -10, 0, 4.5f, 1.5f, 4.5f, 0, -1, 0, steel)
                .part("footR", "legR", 0, -14, 0, 6, 3, 12, 0, 1.5f, -3, mint)
                .part("body", null, 0, 14, 0, 12, 14, 9, 0, 7, 0, mint)
                .part("belly", "body", 0, 6, -4.6f, 8, 10, 1, 0, 0, 0, white)
                .part("tail", "body", 0, 3, 5, 5, 5, 5, 0, 0, 1.5f, white).sway(0.2f, 0.3f, 0, 1)
                .part("head", "body", 0, 14, 0, 12, 11, 10, 0, 5.5f, 0, mint)
                .part("muzzle", "head", 0, 3, -5.4f, 6, 4, 2, 0, 0, 0, white)
                .part("noseJ", "head", 0, 5, -6.6f, 2, 1.5f, 1, 0, 0, 0, coral)
                .part("jaw", "head", 0, 1, -5, 4, 1.5f, 2, 0, -0.5f, -1, mintD)
                .part("teethL", "head", -0.8f, 1.2f, -6.6f, 1.4f, 2.5f, 0.6f, 0, -0.8f, 0, white)
                .part("teethR", "head", 0.8f, 1.2f, -6.6f, 1.4f, 2.5f, 0.6f, 0, -0.8f, 0, white)
                .part("eyeL", "head", -3.5f, 7, -5.2f, 3, 3.5f, 1, 0, 0, 0, amber).glow()
                .part("eyeR", "head", 3.5f, 7, -5.2f, 3, 3.5f, 1, 0, 0, 0, amber).glow()
                .part("pupilL", "head", -3.2f, 6.8f, -5.8f, 1.2f, 2, 0.4f, 0, 0, 0, black)
                .part("pupilR", "head", 3.2f, 6.8f, -5.8f, 1.2f, 2, 0.4f, 0, 0, 0, black)
                .part("blushL", "head", -5, 3.5f, -5.1f, 2, 1, 0.4f, 0, 0, 0, coral)
                .part("blushR", "head", 5, 3.5f, -5.1f, 2, 1, 0.4f, 0, 0, 0, coral)
                .part("earL", "head", -3.5f, 11, 1, 3, 16, 2, 0, 8, 0, mint).sway(0.1f, 0.12f, 0, 2)
                .part("earInL", "earL", 0, 8, -1.1f, 1.6f, 13, 0.4f, 0, 0, 0, coral)
                .part("earR", "head", 3.5f, 11, 1, 3, 16, 2, 0, 8, 0, mint).sway(0.1f, 0.12f, P, 2)
                .part("earInR", "earR", 0, 8, -1.1f, 1.6f, 13, 0.4f, 0, 0, 0, coral)
                .part("antlerL", "head", -5.5f, 10, -1, 1.5f, 5, 1.5f, 0, 2.5f, 0, wood)
                .part("prongL", "antlerL", 0, 4, 0, 4, 1.2f, 1.2f, -2, 0, 0, wood)
                .part("prongL2", "antlerL", -3.5f, 4, 0, 1.2f, 3, 1.2f, 0, 1.5f, 0, wood)
                .part("antlerR", "head", 5.5f, 10, -1, 1.5f, 5, 1.5f, 0, 2.5f, 0, wood)
                .part("prongR", "antlerR", 0, 4, 0, 4, 1.2f, 1.2f, 2, 0, 0, wood)
                .part("prongR2", "antlerR", 3.5f, 4, 0, 1.2f, 3, 1.2f, 0, 1.5f, 0, wood)
                .part("armL", "body", -7, 12, 0, 3, 9, 3, 0, -4.5f, 0, mint).walk(0.5f, P)
                .part("mittL", "armL", 0, -9, 0, 3.6f, 3, 3.6f, 0, -1, 0, white)
                .part("armR", "body", 7, 12, 0, 3, 9, 3, 0, -4.5f, 0, mint).walk(0.5f, 0)
                .part("mittR", "armR", 0, -9, 0, 3.6f, 3, 3.6f, 0, -1, 0, white)
                // giant leap: coils compress, sails high, crashes down with a shockwave
                .custom(9, 14f, 4f, 160, 14, 24, 10, leapSlam(1.05, 3.5, ParticleTypes.CLOUD), (p, e, w, s, r, age) -> {
                    p.scl("legL", 1, 0.55f, 1, 1, 1.35f, 1, w, s, r); p.scl("legR", 1, 0.55f, 1, 1, 1.35f, 1, w, s, r);
                    p.pos("body", 0, -6, 0, 0, 2, 0, w, s, r); p.rot("body", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r);
                    p.rot("armL", -0.5f, 0, -0.4f, 2.8f, 0, -0.5f, w, s, r); p.rot("armR", -0.5f, 0, 0.4f, 2.8f, 0, 0.5f, w, s, r);
                    p.rot("earL", -0.6f, 0, -0.2f, 0.9f, 0, -0.3f, w, s, r); p.rot("earR", -0.6f, 0, 0.2f, 0.9f, 0, 0.3f, w, s, r);
                    p.rot("legL", 0.4f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("legR", 0.4f, 0, 0, -0.4f, 0, 0, w, s, r);
                })
                // bounce stomp: boing-boing-BOING on the springs
                .atk(Atk.STOMP, 7, 3.2f, 0, 70).anim((p, e, w, s, r, age) -> {
                    float b = Mth.abs(Mth.sin(age * 0.9f)) * (1 - s) * w;
                    p.pos("body", 0, b * 5, 0, 0, -3, 0, w, s, r); p.g("legL")[4] += b * 5; p.g("legR")[4] += b * 5;
                    p.scl("legL", 1, 0.8f, 1, 1, 0.5f, 1, w, s, r); p.scl("legR", 1, 0.8f, 1, 1, 0.5f, 1, w, s, r);
                    p.rot("armL", 0.2f, 0, -1.2f, 0.1f, 0, -0.5f, w, s, r); p.rot("armR", 0.2f, 0, 1.2f, 0.1f, 0, 0.5f, w, s, r);
                    p.rot("earL", 0.5f, 0, -0.5f, -0.4f, 0, -0.8f, w, s, r); p.rot("earR", 0.5f, 0, 0.5f, -0.4f, 0, 0.8f, w, s, r);
                })
                // double kick: leans back on its tail, both spring feet piston forward
                .custom(8, 3.0f, 0, 60, 10, 4, 12, combo(2.0f, 0.35, SoundEvents.PLAYER_ATTACK_KNOCKBACK, 0), (p, e, w, s, r, age) -> {
                    p.rot("body", 0.6f, 0, 0, 0.35f, 0, 0, w, s, r); p.pos("body", 0, 2, 4, 0, 3, 1, w, s, r);
                    p.rot("legL", -0.6f, 0, 0, 1.6f, 0, 0, w, s, r); p.rot("legR", -0.6f, 0, 0, 1.6f, 0, 0, w, s, r);
                    p.scl("legL", 1, 0.7f, 1, 1, 1.3f, 1, w, s, r); p.scl("legR", 1, 0.7f, 1, 1, 1.3f, 1, w, s, r);
                    p.pos("legL", 0, 0, 4, 0, 3, -2, w, s, r); p.pos("legR", 0, 0, 4, 0, 3, -2, w, s, r);
                    p.rot("armL", 0.3f, 0, -1.0f, -0.8f, 0, -0.6f, w, s, r); p.rot("armR", 0.3f, 0, 1.0f, -0.8f, 0, 0.6f, w, s, r);
                    p.rot("head", -0.3f, 0, 0, 0.2f, 0, 0, w, s, r);
                })
                // antler butt
                .custom(6, 2.6f, 0, 40, 9, 4, 9, thrust(1.3f, 0.2, 0.5), (p, e, w, s, r, age) -> {
                    p.rot("head", -0.5f, 0, 0, 0.9f, 0, 0, w, s, r); p.rot("body", 0.2f, 0, 0, -0.6f, 0, 0, w, s, r);
                    p.pos("body", 0, 0, 3, 0, -2, -5, w, s, r); p.rot("earL", 0, 0, 0, -1.3f, 0, 0, w, s, r); p.rot("earR", 0, 0, 0, -1.3f, 0, 0, w, s, r);
                    p.rot("legL", 0.4f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("legR", -0.2f, 0, 0, 0.6f, 0, 0, w, s, r);
                });
    }

    // ───────────────────────────── 5. Rumble Rooster ─────────────────────────────
    /** Masked wrestling rooster: flying talon kick, peck combo, egg barrage, wing buffet. */
    static CreatureDef rumbleRooster() {
        int plume = 0xe0662a, plumeD = 0xb2441b, comb = 0xc4152b, beak = 0xf2b51c, mask = 0x2456c8, trim = 0xf5f5f5, leg = 0xf2c230, talon = 0x3a2a1a, teal = 0x1e7a6a, black = 0x0e0e0e;
        return CreatureDef.of("rumble_rooster", "Rumble Rooster", "ランブル・ルースター").size(0.9f, 2.6f).hp(65).dmg(7).speed(0.35).kbRes(0.2).egg(plume, mask).spawn(2)
                .part("legL", null, -3, 13, 0, 2.5f, 13, 2.5f, 0, -6.5f, 0, leg).walk(1.0f, 0)
                .part("footL", "legL", 0, -13, 0, 5, 1.5f, 7, 0, 0.75f, -2, leg)
                .part("talonL", "footL", 0, 0, -5.5f, 4, 1, 1.2f, 0, 0.5f, 0, talon)
                .part("legR", null, 3, 13, 0, 2.5f, 13, 2.5f, 0, -6.5f, 0, leg).walk(1.0f, P)
                .part("footR", "legR", 0, -13, 0, 5, 1.5f, 7, 0, 0.75f, -2, leg)
                .part("talonR", "footR", 0, 0, -5.5f, 4, 1, 1.2f, 0, 0.5f, 0, talon)
                .part("body", null, 0, 13, 0, 13, 14, 11, 0, 7, 0, plume)
                .part("chest", "body", 0, 8, -5.6f, 9, 9, 1, 0, 0, 0, 0xf2a65a)
                .part("belt", "body", 0, 2, 0, 14, 3, 12, 0, 0, 0, black)
                .part("buckle", "body", 0, 2, -6.2f, 5, 3.5f, 0.6f, 0, 0, 0, beak)
                .part("tail", "body", 0, 9, 5, 2, 12, 3, 0, 5, 2, teal).sway(0.1f, 0.15f, 0, 0)
                .part("tailL", "body", -3, 8, 5, 2, 10, 3, -1, 4, 2, plumeD)
                .part("tailR", "body", 3, 8, 5, 2, 10, 3, 1, 4, 2, teal)
                .part("head", "body", 0, 14, 0, 9, 9, 9, 0, 4.5f, -1, plume)
                .part("maskF", "head", 0, 5.5f, -5.6f, 9.4f, 4.5f, 0.6f, 0, 0, 0, mask)
                .part("maskTrimL", "head", -2.5f, 5.5f, -6, 3.6f, 3, 0.4f, 0, 0, 0, trim)
                .part("maskTrimR", "head", 2.5f, 5.5f, -6, 3.6f, 3, 0.4f, 0, 0, 0, trim)
                .part("eyeL", "head", -2.5f, 5.5f, -6.3f, 2, 2, 0.4f, 0, 0, 0, black)
                .part("eyeR", "head", 2.5f, 5.5f, -6.3f, 2, 2, 0.4f, 0, 0, 0, black)
                .part("maskSide", "head", 0, 5.5f, 0, 9.6f, 4.5f, 8, 0, 0, -1, mask)
                .part("beak", "head", 0, 3.5f, -7, 3, 2, 4, 0, 0, -1, beak)
                .part("jaw", "head", 0, 2.3f, -6, 2.6f, 1, 3, 0, -0.5f, -1, 0xd99a10)
                .part("wattle", "head", 0, 1, -5.6f, 2, 3, 1, 0, -1.5f, 0, comb)
                .part("comb1", "head", -0, 9, -2, 1.6f, 4, 2, 0, 2, 0, comb)
                .part("comb2", "head", 0, 9, 0.5f, 1.6f, 5, 2, 0, 2.5f, 0, comb)
                .part("comb3", "head", 0, 9, 3, 1.6f, 3.5f, 2, 0, 1.75f, 0, comb)
                .part("armL", "body", -7.5f, 13, 0, 3, 11, 8, 0, -5, 1, plumeD).sway(0.06f, 0.2f, 0, 2)
                .part("tapeL", "armL", 0, -10, 0, 3.4f, 2, 6, 0, 0, -1, trim)
                .part("armR", "body", 7.5f, 13, 0, 3, 11, 8, 0, -5, 1, plumeD).sway(0.06f, 0.2f, P, 2)
                .part("tapeR", "armR", 0, -10, 0, 3.4f, 2, 6, 0, 0, -1, trim)
                // flying talon kick: hops, tucks, both talons strike forward
                .atk(Atk.POUNCE, 8, 9f, 3f, 110).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -4, 2, 0, 4, -4, w, s, r); p.rot("body", -0.3f, 0, 0, 0.7f, 0, 0, w, s, r);
                    p.rot("legL", 0.8f, 0, 0, 1.6f, 0, 0, w, s, r); p.rot("legR", 0.8f, 0, 0, 1.6f, 0, 0, w, s, r);
                    p.pos("legL", 0, 0, 0, 0, 4, -2, w, s, r); p.pos("legR", 0, 0, 0, 0, 4, -2, w, s, r);
                    p.rot("armL", 0, 0, -0.5f, 0, 0, -2.0f, w, s, r); p.rot("armR", 0, 0, 0.5f, 0, 0, 2.0f, w, s, r);
                    p.rot("head", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r);
                })
                // peck combo: three jackhammer pecks
                .custom(7, 2.6f, 0, 34, 6, 12, 8, combo(0.3f, 0, SoundEvents.CHICKEN_HURT, 0, 4, 8), (p, e, w, s, r, age) -> {
                    float k = Mth.abs(Mth.sin(s * P * 3));
                    p.rot("head", -0.4f, 0, 0, 0.1f + 1.0f * k, 0, 0, w, s, r); p.pos("head", 0, 0, 2, 0, -1, -2 - 4 * k, w, s, r);
                    p.rot("body", 0.1f, 0, 0, -0.3f - 0.3f * k, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.4f * (1 - k), 0, 0, w, s, r);
                    p.rot("armL", 0, 0, -0.6f, 0, 0, -0.4f, w, s, r); p.rot("armR", 0, 0, 0.6f, 0, 0, 0.4f, w, s, r);
                })
                // egg barrage: squats, wings flare, lobs three yolk-splatter eggs
                .custom(5, 16f, 4f, 120, 14, 10, 10, volley(0xfff4dc, "blind", 1.1f, 0.08, 0, 3, 6), (p, e, w, s, r, age) -> {
                    float k = Mth.sin(s * P * 3);
                    p.pos("body", 0, -3, 0, 0, -1, 0, w, s, r); p.rot("body", -0.2f, 0, 0, 0.2f * k, 0, 0, w, s, r);
                    p.rot("armL", 0, 0, -1.6f, 0, 0, -1.2f - 0.4f * k, w, s, r); p.rot("armR", 0, 0, 1.6f, 0, 0, 1.2f + 0.4f * k, w, s, r);
                    p.rot("tail", -0.3f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("head", -0.2f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.5f, 0, 0, 0.6f, 0, 0, w, s, r);
                })
                // wing buffet: both wings sweep forward, gust knockback
                .atk(Atk.SWIPE, 6, 3.2f, 0, 45).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 0.3f, 0.8f, -1.8f, 1.2f, -0.6f, -0.3f, w, s, r); p.rot("armR", 0.3f, -0.8f, 1.8f, 1.2f, 0.6f, 0.3f, w, s, r);
                    p.rot("body", 0.35f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, 1, 2, 0, -1, -3, w, s, r);
                    p.rot("head", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("legL", -0.2f, 0, 0, 0.4f, 0, 0, w, s, r);
                });
    }

    // ───────────────────────────── 6. Crayon Kirin ─────────────────────────────
    /** Paper-white kirin with a wax-crayon horn and crayon-tip mane: horn thrust, paint spit, rearing hoof stomp, spin buck kick. */
    static CreatureDef crayonKirin() {
        int paper = 0xf2efe6, scrib = 0x4aa3d9, hoof = 0x3a3340, wax = 0xd93a3a, wrap = 0xe8d36a, blue = 0x2f6fd6, black = 0x101018, pink = 0xf4a3b8;
        int[] mane = {0xe63946, 0xf4a261, 0xf1d04b, 0x5cc26a, 0x3a86ff, 0x8f5bd8};
        CreatureDef d = CreatureDef.of("crayon_kirin", "Crayon Kirin", "クレヨン・キリン").size(1.3f, 2.5f).hp(85).dmg(7).speed(0.36).kbRes(0.4).egg(paper, wax).spawn(1)
                .part("body", null, 0, 18, 9, 12, 11, 22, 0, 4, -9, paper)
                .part("scribble1", "body", -6.2f, 5, -12, 0.4f, 1, 10, 0, 0, 0, scrib)
                .part("scribble2", "body", 6.2f, 3, -6, 0.4f, 1, 8, 0, 0, 0, 0xe86a92)
                .part("scribble3", "body", 0, 9.7f, -4, 6, 0.4f, 1, 0, 0, 0, 0x5cc26a)
                .part("legLf", "body", -4, -2, -16, 3.5f, 16, 3.5f, 0, -8, 0, paper).walk(0.8f, 0)
                .part("hoofLf", "legLf", 0, -16, 0, 4, 2.5f, 4, 0, 1.25f, 0, hoof)
                .part("legRf", "body", 4, -2, -16, 3.5f, 16, 3.5f, 0, -8, 0, paper).walk(0.8f, P)
                .part("hoofRf", "legRf", 0, -16, 0, 4, 2.5f, 4, 0, 1.25f, 0, hoof)
                .part("legL", "body", -4, -2, -1, 3.5f, 16, 3.5f, 0, -8, 0, paper).walk(0.8f, P)
                .part("hoofL", "legL", 0, -16, 0, 4, 2.5f, 4, 0, 1.25f, 0, hoof)
                .part("legR", "body", 4, -2, -1, 3.5f, 16, 3.5f, 0, -8, 0, paper).walk(0.8f, 0)
                .part("hoofR", "legR", 0, -16, 0, 4, 2.5f, 4, 0, 1.25f, 0, hoof)
                .part("tail", "body", 0, 7, 2, 3, 3, 10, 0, -2, 4, mane[4]).sway(0.3f, 0.12f, 0, 1)
                .part("tailTip", "tail", 0, -3, 8, 3.5f, 3.5f, 4, 0, 0, 1, mane[0])
                .part("neck", "body", 0, 8, -18, 6, 12, 6, 0, 5, 0, paper)
                .part("head", "neck", 0, 11, 0, 7, 7, 10, 0, 1, -3, paper)
                .part("snout", "head", 0, -0.5f, -9, 5, 4, 4, 0, 0, 0, paper)
                .part("nostril", "head", 0, 0, -11.1f, 3, 1, 0.4f, 0, 0, 0, pink)
                .part("jaw", "head", 0, -2.5f, -7, 4.6f, 1.5f, 5, 0, -0.5f, -1.5f, 0xd8d4c8)
                .part("eyeL", "head", -3.6f, 2, -4, 0.5f, 2.5f, 2.5f, 0, 0, 0, blue).glow()
                .part("eyeR", "head", 3.6f, 2, -4, 0.5f, 2.5f, 2.5f, 0, 0, 0, blue).glow()
                .part("lashL", "head", -3.7f, 3.6f, -4, 0.5f, 0.6f, 3, 0, 0, 0, black)
                .part("lashR", "head", 3.7f, 3.6f, -4, 0.5f, 0.6f, 3, 0, 0, 0, black)
                .part("earL", "head", -2.5f, 4.5f, -1, 1.5f, 3.5f, 1.5f, 0, 1.75f, 0, paper)
                .part("earR", "head", 2.5f, 4.5f, -1, 1.5f, 3.5f, 1.5f, 0, 1.75f, 0, paper)
                .part("horn", "head", 0, 4.5f, -5, 2.4f, 5, 2.4f, 0, 2.5f, 0, wrap)
                .part("hornWax", "horn", 0, 5, 0, 2, 4, 2, 0, 2, 0, wax)
                .part("hornTip", "hornWax", 0, 4, 0, 1, 2, 1, 0, 1, 0, wax);
        for (int i = 0; i < 6; i++) d.part("mane" + i, "neck", 0, 11 - i * 2.4f, 3.2f, 2, 2.6f, 2, 0, 0, 0.5f, mane[i]).sway(0.12f, 0.2f, i * 0.7f, 0);
        return d
                // horn thrust: neck coils back, lunges straight through
                .custom(9, 3.8f, 0, 45, 11, 4, 12, thrust(1.6f, 0.25, 0.8), (p, e, w, s, r, age) -> {
                    p.rot("neck", -0.5f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("head", -0.3f, 0, 0, 0.7f, 0, 0, w, s, r);
                    p.rot("body", 0.1f, 0, 0, -0.1f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, -4, w, s, r);
                    p.rot("legLf", -0.3f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("legL", 0.5f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("legR", 0.5f, 0, 0, -0.6f, 0, 0, w, s, r);
                })
                // paint spit: cheeks puff, horn glows, spits a sticky wax glob
                .atk(Atk.SHOOT, 6, 16f, 3f, 70, 0x3a86ff, "slow").anim((p, e, w, s, r, age) -> {
                    p.rot("neck", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.7f, 0, 0, w, s, r);
                    p.scl("snout", 1.3f, 1.3f, 1, 0.9f, 0.9f, 1.1f, w, s, r); p.scl("hornWax", 1.3f, 1.2f, 1.3f, 1, 1, 1, w, s, r);
                })
                // rearing hoof stomp
                .atk(Atk.STOMP, 8, 3.4f, 0, 90).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.85f, 0, 0, -0.05f, 0, 0, w, s, r); p.pos("body", 0, 2, 0, 0, -1, 0, w, s, r);
                    p.rot("legLf", -1.2f + Mth.sin(age * 0.8f) * 0.4f * (1 - s), 0, 0, -0.2f, 0, 0, w, s, r);
                    p.rot("legRf", -1.2f - Mth.sin(age * 0.8f) * 0.4f * (1 - s), 0, 0, -0.2f, 0, 0, w, s, r);
                    p.rot("legL", -0.85f, 0, 0, 0.05f, 0, 0, w, s, r); p.rot("legR", -0.85f, 0, 0, 0.05f, 0, 0, w, s, r);
                    p.rot("neck", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("tail", -0.8f, 0, 0, 0.3f, 0, 0, w, s, r);
                })
                // spin buck kick: whips around, hind legs lash out, spins back
                .custom(9, 3.2f, 0, 70, 12, 6, 12, combo(2.2f, 0.4, SoundEvents.HORSE_ANGRY, 0), (p, e, w, s, r, age) -> {
                    float turn = (r > 0 ? 1 - Pose_ease(r) : Pose_ease(w)) * P;
                    p.g("body")[1] += turn;
                    p.rot("body", 0.0f, 0, 0, -0.55f, 0, 0, w, s, r); p.pos("body", 0, 0, 0, 0, 2, 0, w, s, r);
                    p.rot("legL", 0, 0, 0, -1.4f, 0, 0, w, s, r); p.rot("legR", 0, 0, 0, -1.4f, 0, 0, w, s, r);
                    p.rot("neck", 0, 0, 0, 0.8f, 0, 0, w, s, r); p.rot("tail", 0, 0, 0, -1.0f, 0, 0, w, s, r);
                });
    }

    // ───────────────────────────── 7. Truffle Boar ─────────────────────────────
    /** Harnessed truffle-hunting boar: tusk charge, uproot burst under the target, gnashing bite, tusk gore. */
    static CreatureDef truffleBoar() {
        int hide = 0xb07a6a, bristle = 0x5a3a30, snout = 0xe8a0a0, ivory = 0xf1ead2, eye = 0xffd23a, leather = 0x6b4423, brass = 0xd4a640, nostril = 0x7a3b3b;
        return CreatureDef.of("truffle_boar", "Truffle Boar", "トリュフ・ボア").size(1.3f, 1.6f).hp(75).dmg(7).speed(0.33).kbRes(0.6).armor(3).egg(hide, bristle).spawn(2)
                .part("body", null, 0, 14, 9, 16, 13, 24, 0, 3, -9, hide)
                .part("mane", "body", 0, 10, -14, 4, 3, 14, 0, 0, 0, bristle)
                .part("mane2", "body", 0, 10.5f, -18, 3, 3, 4, 0, 1, 0, bristle)
                .part("strap", "body", 0, 3, -17, 16.6f, 13.6f, 3, 0, 0, 0, leather)
                .part("buckleL", "body", -8.5f, 3, -17, 0.6f, 3, 3.4f, 0, 0, 0, brass)
                .part("buckleR", "body", 8.5f, 3, -17, 0.6f, 3, 3.4f, 0, 0, 0, brass)
                .part("pouch", "body", -8.6f, 2, -6, 2, 5, 6, 0, 0, 0, leather)
                .part("legLf", "body", -5, -3, -17, 5, 11, 5, 0, -5.5f, 0, hide).walk(0.9f, 0)
                .part("hoofLf", "legLf", 0, -11, 0, 5.4f, 2, 5.4f, 0, 1, 0, bristle)
                .part("legRf", "body", 5, -3, -17, 5, 11, 5, 0, -5.5f, 0, hide).walk(0.9f, P)
                .part("hoofRf", "legRf", 0, -11, 0, 5.4f, 2, 5.4f, 0, 1, 0, bristle)
                .part("legL", "body", -5, -3, -2, 5, 11, 5, 0, -5.5f, 0, hide).walk(0.9f, P)
                .part("hoofL", "legL", 0, -11, 0, 5.4f, 2, 5.4f, 0, 1, 0, bristle)
                .part("legR", "body", 5, -3, -2, 5, 11, 5, 0, -5.5f, 0, hide).walk(0.9f, 0)
                .part("hoofR", "legR", 0, -11, 0, 5.4f, 2, 5.4f, 0, 1, 0, bristle)
                .part("tail", "body", 0, 7, 3, 1.5f, 1.5f, 5, 0, 0, 2, bristle).sway(0.5f, 0.3f, 0, 1)
                .part("head", "body", 0, 4, -21, 12, 11, 9, 0, 0, -4, hide)
                .part("snout", "head", 0, -2, -9.5f, 7, 5, 3, 0, 0, 0, snout)
                .part("nostrilL", "head", -1.5f, -2, -11.1f, 1.2f, 1.6f, 0.4f, 0, 0, 0, nostril)
                .part("nostrilR", "head", 1.5f, -2, -11.1f, 1.2f, 1.6f, 0.4f, 0, 0, 0, nostril)
                .part("jaw", "head", 0, -5, -6, 8, 2, 6, 0, -1, -1, 0x8c5a4c)
                .part("tuskL", "head", -4, -4, -9, 1.5f, 5, 1.5f, 0, 2, 0, ivory)
                .part("tuskR", "head", 4, -4, -9, 1.5f, 5, 1.5f, 0, 2, 0, ivory)
                .part("eyeL", "head", -3.5f, 2, -8.6f, 2, 1.5f, 0.5f, 0, 0, 0, eye).glow()
                .part("eyeR", "head", 3.5f, 2, -8.6f, 2, 1.5f, 0.5f, 0, 0, 0, eye).glow()
                .part("browL", "head", -3.5f, 3.4f, -8.7f, 3, 0.8f, 0.5f, 0, 0, 0, bristle)
                .part("browR", "head", 3.5f, 3.4f, -8.7f, 3, 0.8f, 0.5f, 0, 0, 0, bristle)
                .part("earL", "head", -5, 5, -3, 3, 4, 1.5f, -1, 2, 0, hide).sway(0.15f, 0.15f, 0, 2)
                .part("earR", "head", 5, 5, -3, 3, 4, 1.5f, 1, 2, 0, hide).sway(0.15f, 0.15f, P, 2)
                // charge: paws the ground (windup), head low, gallop
                .atk(Atk.CHARGE, 9, 12f, 4f, 150).anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.2f, 0, 0, 0.45f, 0, 0, w, s, r); p.rot("body", -0.05f, 0, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("earL", -0.6f, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("earR", -0.6f, 0, 0, -0.9f, 0, 0, w, s, r);
                    if (s <= 0) p.add("legRf", Mth.sin(age * 1.4f) * 0.6f * w, 0, 0);
                    else if (r <= 0) { float c = Mth.cos(age * 1.7f) * 0.9f; p.add("legLf", c, 0, 0); p.add("legRf", c, 0, 0); p.add("legL", -c, 0, 0); p.add("legR", -c, 0, 0); }
                })
                // uproot: roots its snout into the ground, the earth bursts beneath the target
                .custom(8, 7f, 0, 120, 16, 4, 12, (m, s, t) -> {
                    if (!(m.level() instanceof ServerLevel lv)) return;
                    if (t % 4 == 0 && t <= s.wind()) { Vec3 o = m.position().add(Fx.flat(m).scale(1.2)); lv.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, o.x, o.y + 0.1, o.z, 2, 0.3, 0, 0.3, 0.01); snd(m, SoundEvents.ROOTED_DIRT_BREAK, 1f, 0.8f); }
                    if (t != s.wind() + 1 || m.getTarget() == null) return;
                    Vec3 c = m.getTarget().position();
                    snd(m, SoundEvents.GENERIC_EXPLODE, 1f, 1.2f);
                    lv.sendParticles(ParticleTypes.EXPLOSION, c.x, c.y + 0.3, c.z, 2, 0.4, 0, 0.4, 0);
                    lv.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, c.x, c.y + 0.2, c.z, 20, 0.6, 0.2, 0.6, 0.05);
                    for (LivingEntity f : Fx.foes(m, c, 1.8)) { Fx.hit(m, f, s.dmg(), 0, Vec3.ZERO); f.setDeltaMovement(f.getDeltaMovement().add(0, 0.95, 0)); f.hurtMarked = true; f.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1)); }
                }, (p, e, w, s, r, age) -> {
                    p.rot("head", 0.9f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("body", -0.15f, 0, 0, 0.1f, 0, 0, w, s, r);
                    if (s <= 0) p.add("head", 0, Mth.sin(age * 1.6f) * 0.25f * w, 0);
                    p.rot("legLf", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("legRf", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r);
                })
                // gnashing bite
                .atk(Atk.BITE, 7, 2.6f, 0, 35).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.3f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("jaw", 0.7f, 0, 0, 0, 0, 0, w, s, r);
                    p.pos("body", 0, 0, 2, 0, 0, -4, w, s, r); p.rot("legLf", -0.3f, 0, 0, 0.4f, 0, 0, w, s, r);
                    if (r > 0) p.add("jaw", Mth.abs(Mth.sin(age * 1.5f)) * 0.4f * (1 - r), 0, 0);
                })
                // tusk gore: hooks head sideways, then rips upward across
                .custom(8, 3.0f, 0, 50, 10, 4, 10, combo(1.0f, 0.5, SoundEvents.RAVAGER_ATTACK, 0), (p, e, w, s, r, age) -> {
                    p.rot("head", 0.5f, -0.6f, 0.4f, -0.5f, 0.6f, -0.3f, w, s, r); p.rot("body", 0, -0.2f, 0, 0, 0.25f, 0, w, s, r);
                    p.pos("body", 0, -1, 2, 0, 1, -3, w, s, r);
                });
    }

    // ───────────────────────────── 8. Snuggle Grizzly ─────────────────────────────
    /** Plush grizzly with a honeycomb patch: crushing bear hug, claw swipe, rearing roar, belly flop. */
    static CreatureDef snuggleGrizzly() {
        int fur = 0xa86b3c, furD = 0x7a4a26, cream = 0xf1d8b0, nose = 0x2a1a12, black = 0x0f0f0f, white = 0xffffff, honey = 0xf2b632, honeyD = 0xc7861a, pad = 0xe79aa3, stitch = 0x3a2416, claw = 0xf4ecd8;
        CreatureDef d = CreatureDef.of("snuggle_grizzly", "Snuggle Grizzly", "スナッグル・グリズリー").size(1.4f, 3.1f).hp(130).dmg(8).speed(0.29).kbRes(0.7).armor(4).egg(fur, honey).spawn(1)
                .part("legL", null, -5, 16, 0, 8, 16, 8, 0, -8, 0, furD).walk(0.7f, 0)
                .part("footL", "legL", 0, -16, 0, 9, 3, 11, 0, 1.5f, -1.5f, fur)
                .part("legR", null, 5, 16, 0, 8, 16, 8, 0, -8, 0, furD).walk(0.7f, P)
                .part("footR", "legR", 0, -16, 0, 9, 3, 11, 0, 1.5f, -1.5f, fur)
                .part("body", null, 0, 16, 0, 20, 20, 14, 0, 10, 0, fur).sway(0.02f, 0.08f, 0, 2)
                .part("belly", "body", 0, 9, -7.1f, 14, 14, 1, 0, 0, 0, cream)
                .part("hex", "belly", 0, 1, -0.6f, 6, 4, 0.5f, 0, 0, 0, honey)
                .part("hex2", "belly", 0, 1, -0.6f, 4, 7, 0.5f, 0, 0, 0, honey)
                .part("hexIn", "belly", 0, 1, -0.9f, 2.4f, 3, 0.4f, 0, 0, 0, honeyD)
                .part("tail", "body", 0, 3, 7, 4, 4, 3, 0, 0, 1.5f, furD)
                .part("head", "body", 0, 20, 0, 16, 13, 13, 0, 6.5f, -1, fur)
                .part("muzzle", "head", 0, 3.5f, -8.5f, 8, 6, 4, 0, 0, 0, cream)
                .part("nose", "head", 0, 5.5f, -10.7f, 4, 2.5f, 1, 0, 0, 0, nose)
                .part("jaw", "head", 0, 1, -7, 7, 2.5f, 5, 0, -1, -1.5f, 0x5a2a20)
                .part("toothL", "jaw", -2, -0.2f, -3.9f, 1.2f, 1.5f, 0.6f, 0, 0.5f, 0, white)
                .part("toothR", "jaw", 2, -0.2f, -3.9f, 1.2f, 1.5f, 0.6f, 0, 0.5f, 0, white)
                .part("eyeL", "head", -4, 8.5f, -7.7f, 2.5f, 2.5f, 1, 0, 0, 0, black)
                .part("eyeR", "head", 4, 8.5f, -7.7f, 2.5f, 2.5f, 1, 0, 0, 0, black)
                .part("glintL", "head", -4.5f, 9.1f, -8.3f, 0.8f, 0.8f, 0.3f, 0, 0, 0, white).glow()
                .part("glintR", "head", 3.5f, 9.1f, -8.3f, 0.8f, 0.8f, 0.3f, 0, 0, 0, white).glow()
                .part("headSeam", "head", 0, 13.1f, -1, 0.6f, 0.5f, 12, 0, 0, 0, stitch)
                .part("earL", "head", -6.5f, 12, 0, 5, 5, 3, 0, 2, 0, fur)
                .part("earInL", "earL", 0, 2, -1.6f, 3, 3, 0.4f, 0, 0, 0, pad)
                .part("earR", "head", 6.5f, 12, 0, 5, 5, 3, 0, 2, 0, fur)
                .part("earInR", "earR", 0, 2, -1.6f, 3, 3, 0.4f, 0, 0, 0, pad)
                .part("armL", "body", -13, 17, 0, 7, 20, 7, 0, -9, 0, fur).sway(0.04f, 0.08f, 0, 2)
                .part("pawL", "armL", 0, -20, 0, 8, 5, 8, 0, -1.5f, 0, furD)
                .part("padL", "pawL", 0, -2, -4.1f, 4, 3, 0.5f, 0, 0, 0, pad)
                .part("armR", "body", 13, 17, 0, 7, 20, 7, 0, -9, 0, fur).sway(0.04f, 0.08f, P, 2)
                .part("pawR", "armR", 0, -20, 0, 8, 5, 8, 0, -1.5f, 0, furD)
                .part("padR", "pawR", 0, -2, -4.1f, 4, 3, 0.5f, 0, 0, 0, pad);
        for (int i = 0; i < 3; i++) {
            d.part("clawL" + i, "pawL", -2.5f + i * 2.5f, -4, -3.5f, 1, 2.5f, 1, 0, -1, 0, claw);
            d.part("clawR" + i, "pawR", -2.5f + i * 2.5f, -4, -3.5f, 1, 2.5f, 1, 0, -1, 0, claw);
            d.part("seam" + i, "body", 0, 3 + i * 5, -7.7f, 0.5f, 2, 0.4f, -7.5f, 0, 0, stitch);
        }
        return d
                // bear hug: arms spread wide, lurches in, crushes; rocking squeeze while holding
                .atk(Atk.GRAB, 8, 3.0f, 0, 150, 60, "").anim((p, e, w, s, r, age) -> {
                    boolean hold = s >= 1 && r <= 0;
                    p.rot("armL", 1.3f, 0, -1.4f, 1.5f, -0.9f, 0.1f, w, s, r); p.rot("armR", 1.3f, 0, 1.4f, 1.5f, 0.9f, -0.1f, w, s, r);
                    p.rot("body", 0.25f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, 1, 3, 0, -1, -5, w, s, r);
                    p.rot("head", -0.3f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("jaw", 0.4f, 0, 0, 0.1f, 0, 0, w, s, r);
                    if (hold) { float q = Mth.sin(age * 0.7f); p.add("body", 0, 0, q * 0.12f); p.add("armL", 0, -0.1f * Mth.abs(q), 0); p.add("armR", 0, 0.1f * Mth.abs(q), 0); p.add("head", 0, 0, -q * 0.15f); }
                })
                // claw swipe: big wind-up over the shoulder, diagonal rake
                .atk(Atk.SWIPE, 9, 3.6f, 0, 35).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.6f, -0.4f, 0.6f, 0.6f, 0.7f, -0.3f, w, s, r); p.rot("armL", 0.3f, 0, -0.5f, 0.6f, 0, -0.2f, w, s, r);
                    p.rot("body", 0.15f, -0.6f, 0.1f, -0.35f, 0.5f, -0.05f, w, s, r); p.pos("body", 0, 0, 2, 0, -1, -4, w, s, r);
                    p.rot("head", 0.2f, 0.4f, 0, 0.1f, -0.3f, 0, w, s, r); p.rot("jaw", 0.3f, 0, 0, 0.6f, 0, 0, w, s, r);
                    p.rot("legL", -0.3f, 0, 0, 0.4f, 0, 0, w, s, r);
                })
                // rearing roar
                .atk(Atk.SCREAM, 3, 8f, 2.5f, 240).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.35f, 0, 0, -0.2f, 0, 0, w, s, r); p.pos("body", 0, 2, 0, 0, 0, -2, w, s, r);
                    p.rot("armL", 2.4f, 0, -0.6f, 1.6f, 0, -1.0f, w, s, r); p.rot("armR", 2.4f, 0, 0.6f, 1.6f, 0, 1.0f, w, s, r);
                    p.rot("head", -0.6f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.4f, 0, 0, 1.1f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) p.add("head", 0, Mth.sin(age * 1.2f) * 0.15f, 0);
                })
                // belly flop: rises on tiptoes, flops forward full-length
                .atk(Atk.SLAM, 10, 3.4f, 0, 110).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.3f, 0, 0, -1.35f, 0, 0, w, s, r); p.pos("body", 0, 3, 2, 0, -9, -8, w, s, r);
                    p.rot("armL", 3.0f, 0, -0.3f, 3.0f, 0, -0.6f, w, s, r); p.rot("armR", 3.0f, 0, 0.3f, 3.0f, 0, 0.6f, w, s, r);
                    p.rot("legL", 0.1f, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("legR", 0.1f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("head", -0.3f, 0, 0, -0.7f, 0, 0, w, s, r);
                });
    }

    private BatchC() {}
}
