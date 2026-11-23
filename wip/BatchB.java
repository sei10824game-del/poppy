package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;
import com.poppy.mod.entity.Fx;
import com.poppy.mod.entity.PoppyBolt;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch B – "Chapter 2 slot" creatures, all ORIGINAL designs (wind-up tin toys, pillow bugs, brick dinos...) that fill the
 * gameplay roles of the assigned roster: stretchy-limbed spider boss + hatchling + giant, segmented crawler boss,
 * jack-in-the-box, brick dinosaur, larva doll, crayon skeleton, candy cat, winged critter. See DEV_GUIDE.md.
 */
public final class BatchB {
    static final float P = (float) Math.PI;

    public static List<CreatureDef> all() {
        List<CreatureDef> l = new ArrayList<>();
        l.add(coilWidow());
        l.add(coilHatchling());
        l.add(coilPatriarch());
        l.add(bolsterpede());
        l.add(crankCrate());
        l.add(brickrex());
        l.add(grublet());
        l.add(scribbleBones());
        l.add(gumdropTabby());
        l.add(mothmouse());
        return l;
    }

    // ---------------------------------------------------------------- model helper: '#' in a name -> L (x mirrored) / R
    private static final class M {
        final CreatureDef d;
        M(CreatureDef d) { this.d = d; }
        M p(String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) { d.part(n, par, px, py, pz, w, h, dd, cx, cy, cz, c); return this; }
        M glow() { d.glow(); return this; }
        M walk(float a, float ph) { d.walk(a, ph); return this; }
        M sway(float a, float sp, float ph, int ax) { d.sway(a, sp, ph, ax); return this; }
        /** mode 0 plain, 1 glow, 2 walk(a, opposite phase), 3 sway(a, speed b, axis z, opposite phase) */
        private M pair(int mode, float a, float b, String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) {
            for (int side = 0; side < 2; side++) {
                String s = side == 0 ? "L" : "R"; float sg = side == 0 ? -1 : 1;
                d.part(n.replace("#", s), par == null ? null : par.replace("#", s), px * sg, py, pz, w, h, dd, cx * sg, cy, cz, c);
                if (mode == 1) d.glow();
                if (mode == 2) d.walk(a, side * P);
                if (mode == 3) d.sway(a * sg, b, 0, 2);
            }
            return this;
        }
        M s(String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) { return pair(0, 0, 0, n, par, px, py, pz, w, h, dd, cx, cy, cz, c); }
        M sg(String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) { return pair(1, 0, 0, n, par, px, py, pz, w, h, dd, cx, cy, cz, c); }
        M sw(float amp, String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) { return pair(2, amp, 0, n, par, px, py, pz, w, h, dd, cx, cy, cz, c); }
        M ss(float amp, float sp, String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) { return pair(3, amp, sp, n, par, px, py, pz, w, h, dd, cx, cy, cz, c); }
    }

    // ---------------------------------------------------------------- shared server effects for custom attacks
    static int hit(AttackSpec s) { return s.wind() + 1; }

    /** Launch a line (spring arm / tongue / thread) at the aim; everything hit is reeled in to the creature's front during strike. */
    static AttackSpec.Custom reel(ParticleOptions trail) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv)) return;
            Vec3 a = m.getEyePosition().subtract(0, m.getBbHeight() * 0.2, 0), b = a.add(Fx.aim(m).scale(s.reach()));
            if (t == hit(s)) {
                m.playSound(SoundEvents.FISHING_BOBBER_THROW, 2f, 0.5f);
                for (int i = 0; i <= 20; i++) { Vec3 q = a.lerp(b, i / 20.0); lv.sendParticles(trail, q.x, q.y, q.z, 1, 0, 0, 0, 0); }
                for (LivingEntity e : Fx.line(m, a, b, 1.2)) { Fx.hit(m, e, s.dmg(), 0, Vec3.ZERO); m.hitOnce.add(e.getId()); }
            }
            if (t > hit(s) && t <= hit(s) + s.strike()) {
                Vec3 front = m.position().add(Fx.flat(m).scale(m.getBbWidth() * 0.5 + 1.0)).add(0, 0.3, 0);
                for (int id : m.hitOnce) if (lv.getEntity(id) instanceof LivingEntity e && e.isAlive()) {
                    Vec3 v = front.subtract(e.position()); if (v.lengthSqr() < 1) continue;
                    e.setDeltaMovement(v.normalize().scale(Math.min(1.1, v.length() * 0.3)).add(0, 0.06, 0)); e.hurtMarked = true; e.fallDistance = 0;
                    Vec3 q = a.lerp(e.position().add(0, 1, 0), 0.5); lv.sendParticles(trail, q.x, q.y, q.z, 2, 0.2, 0.2, 0.2, 0);
                }
            }
        };
    }

    /** Volley of n bolts in a fan at the target. */
    static AttackSpec.Custom volley(int color, String style, int n, float spread) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv) || t != hit(s)) return;
            m.playSound(SoundEvents.LLAMA_SPIT, 2f, 0.6f);
            Vec3 o = m.getEyePosition(), d = Fx.aim(m);
            for (int i = 0; i < n; i++) {
                float off = n == 1 ? 0 : (i / (float) (n - 1) - 0.5f) * spread; Vec3 v = d.yRot(off);
                PoppyBolt b = new PoppyBolt(lv, m, s.dmg(), color, style); b.setPos(o.x, o.y - 0.2, o.z); b.shoot(v.x, v.y + 0.05, v.z, 1.2f, 2f); lv.addFreshEntity(b);
            }
        };
    }

    /** Ring shockwave around the creature (or ahead by `ahead` blocks): damage falls off, launches upward, optional effect. */
    static AttackSpec.Custom quake(float ahead, float lift, MobEffect eff, ParticleOptions puff) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv) || t != hit(s)) return;
            m.playSound(SoundEvents.GENERIC_EXPLODE, 1.5f, 0.6f);
            Vec3 c = m.position().add(Fx.flat(m).scale(ahead)); double r = s.reach();
            for (LivingEntity e : Fx.foes(m, c, r)) {
                Fx.hit(m, e, s.dmg() * (float) (1 - 0.4 * e.position().distanceTo(c) / r), 0.8f, e.position().subtract(c));
                e.setDeltaMovement(e.getDeltaMovement().add(0, lift, 0)); e.hurtMarked = true;
                if (eff != null) e.addEffect(new MobEffectInstance(eff, 60, 1));
            }
            for (int i = 0; i < 48; i++) { double a = i / 48.0 * Mth.TWO_PI; lv.sendParticles(puff, c.x + Math.cos(a) * r * 0.7, m.getY() + 0.15, c.z + Math.sin(a) * r * 0.7, 0, Math.cos(a), 0.1, Math.sin(a), 0.25); }
        };
    }

    /** Cone of dust/breath in front: damage + effect, particles throughout the strike. */
    static AttackSpec.Custom cone(ParticleOptions puff, MobEffect eff, int dur) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv)) return;
            Vec3 look = Fx.flat(m);
            if (t == hit(s)) { m.playSound(SoundEvents.PHANTOM_FLAP, 2f, 0.7f); for (LivingEntity e : Fx.arc(m, s.reach(), 0.35)) { Fx.hit(m, e, s.dmg(), 0.4f, look); e.addEffect(new MobEffectInstance(eff, dur, 0)); } }
            if (t >= hit(s) && t < hit(s) + s.strike()) { Vec3 o = m.getEyePosition().add(look.scale(1.0 + (t - hit(s)) * 0.4)); lv.sendParticles(puff, o.x, o.y - 0.3, o.z, 8, 0.6, 0.4, 0.6, 0.02); }
        };
    }

    /** Strike the ground under the target from range (stilt/spike from below). */
    static AttackSpec.Custom skewer(ParticleOptions puff) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv) || m.getTarget() == null) return;
            Vec3 c = m.getTarget().position();
            if (t < hit(s) && t % 3 == 0) lv.sendParticles(ParticleTypes.CRIT, c.x, c.y + 0.1, c.z, 6, 0.6, 0, 0.6, 0.02);
            if (t != hit(s)) return;
            m.playSound(SoundEvents.TRIDENT_HIT_GROUND, 2f, 0.5f);
            for (LivingEntity e : Fx.foes(m, c, 1.8)) { Fx.hit(m, e, s.dmg(), 0, Vec3.ZERO); e.setDeltaMovement(e.getDeltaMovement().add(0, 0.9, 0)); e.hurtMarked = true; }
            for (int i = 0; i < 10; i++) lv.sendParticles(puff, c.x, c.y + i * 0.3, c.z, 3, 0.15, 0.1, 0.15, 0.01);
        };
    }

    /** Leap at target and land with a splash (belly flop / pop-out). */
    static AttackSpec.Custom flop(float upV, ParticleOptions puff) {
        return (m, s, t) -> {
            if (!(m.level() instanceof ServerLevel lv)) return;
            if (t == hit(s)) { Vec3 d = m.getTarget() != null ? m.getTarget().position().subtract(m.position()) : Fx.flat(m); Vec3 h = new Vec3(d.x, 0, d.z).normalize().scale(Math.min(1.2, 0.3 + new Vec3(d.x, 0, d.z).length() * 0.12));
                m.setDeltaMovement(h.x, upV, h.z); m.hasImpulse = true; m.playSound(SoundEvents.SLIME_JUMP, 1.5f, 0.6f); }
            if (t > hit(s) + 3 && t <= hit(s) + s.strike() && (m.onGround() || t == hit(s) + s.strike()) && m.hitOnce.add(-1)) {
                m.playSound(SoundEvents.SLIME_SQUISH, 2f, 0.5f);
                for (LivingEntity e : Fx.foes(m, m.position(), s.reach())) { Fx.hit(m, e, s.dmg(), 0.9f, e.position().subtract(m.position())); e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 2)); }
                lv.sendParticles(puff, m.getX(), m.getY() + 0.2, m.getZ(), 30, s.reach() * 0.4, 0.1, s.reach() * 0.4, 0.05);
            }
        };
    }

    // ================================================================= 1. COIL WIDOW (boss) – wind-up tin spider-matron with telescoping spring arms
    static CreatureDef coilWidow() {
        int brass = 0xc9a24a, copper = 0xb5652e, teal = 0x2f8f8a, tealD = 0x1d5e5b, amber = 0xffb02e, dark = 0x1a1414, ivory = 0xf1ead2, plum = 0x6b2d5c, pink = 0xe58fb6;
        CreatureDef d = CreatureDef.of("coil_widow", "Coil Widow", "コイル・ウィドウ").size(1.8f, 4.6f).hp(520).dmg(12).speed(0.33).kbRes(0.9).armor(6).egg(teal, brass).boss();
        new M(d)
                // spider legs: thigh rises outward to a knee, long shin down to the floor
                .sw(0.5f, "leg#", null, 7, 36, -4, 3, 9, 3, 3, 4, 0, brass)
                .s("knee#", "leg#", 6, 8, 0, 4, 4, 4, 0, 0, 0, copper)
                .s("shin#", "knee#", 0, 0, 0, 2.5f, 44, 2.5f, 1, -22, 0, dark)
                .sw(0.5f, "leg#2", null, 7, 36, 4, 3, 9, 3, 3, 4, 0, brass)
                .s("knee#2", "leg#2", 6, 8, 0, 4, 4, 4, 0, 0, 0, copper)
                .s("shin#2", "knee#2", 0, 0, 0, 2.5f, 44, 2.5f, 1, -22, 0, dark)
                .p("body", null, 0, 36, 0, 16, 6, 14, 0, 0, 0, plum).sway(0.03f, 0.08f, 0, 2) // lampshade skirt
                .p("fringe", "body", 0, -3.5f, 0, 17, 2, 15, 0, 0, 0, pink)
                .p("torso", "body", 0, 3, 0, 10, 18, 8, 0, 9, 0, brass)
                .p("waist", "body", 0, 4, 0, 11, 2, 9, 0, 0, 0, copper)
                .p("chest", "torso", 0, 13, -4.2f, 6, 6, 1, 0, 0, 0, tealD)
                .p("rivet1", "torso", -3, 6, -4.3f, 1, 1, 1, 0, 0, 0, ivory).p("rivet2", "torso", 3, 6, -4.3f, 1, 1, 1, 0, 0, 0, ivory)
                .p("key", "body", 0, 14, 4, 2, 2, 5, 0, 0, 2.5f, brass)
                .p("keyBow", "key", 0, 0, 5, 9, 5, 1, 0, 0, 0, copper).sway(0.6f, 0.15f, 0, 2) // wind-up key ticks around
                .p("head", "body", 0, 24, 0, 12, 11, 11, 0, 5.5f, 0, teal)
                .p("lensRim", "head", 0, 6.5f, -5.6f, 8, 8, 1, 0, 0, 0, brass)
                .p("lens", "head", 0, 6.5f, -6.2f, 5, 5, 1, 0, 0, 0, amber).glow()
                .p("pupil", "head", 0, 6.5f, -6.8f, 2, 3, 1, 0, 0, 0, dark)
                .p("jaw", "head", 0, 2, -4, 10, 3, 4, 0, -1, -1.5f, dark)
                .p("tooth1", "jaw", -3, 0, -3.6f, 1.5f, 2, 1, 0, 0, 0, ivory).p("tooth2", "jaw", -1, 0, -3.6f, 1.5f, 2, 1, 0, 0, 0, ivory)
                .p("tooth3", "jaw", 1, 0, -3.6f, 1.5f, 2, 1, 0, 0, 0, ivory).p("tooth4", "jaw", 3, 0, -3.6f, 1.5f, 2, 1, 0, 0, 0, ivory)
                .p("hat", "head", 0, 11, 0, 14, 3, 14, 0, 1.5f, 0, plum)
                .p("finial", "hat", 0, 3, 0, 2, 3, 2, 0, 1.5f, 0, brass)
                // main telescoping arms: upper coil + rings + forearm + two-prong claw
                .ss(0.05f, 0.1f, "arm#", "body", 7, 21, 0, 3, 14, 3, 0, -7, 0, brass)
                .s("coil#1", "arm#", 0, -4, 0, 4.5f, 1.5f, 4.5f, 0, 0, 0, copper)
                .s("coil#2", "arm#", 0, -10, 0, 4.5f, 1.5f, 4.5f, 0, 0, 0, copper)
                .s("fore#", "arm#", 0, -14, 0, 2.5f, 16, 2.5f, 0, -8, 0, dark)
                .s("claw#a", "fore#", 0, -16, 0, 1.2f, 6, 1.2f, -1.2f, -3, 0, ivory)
                .s("claw#b", "fore#", 0, -16, 0, 1.2f, 6, 1.2f, 1.2f, -3, -0.5f, ivory)
                // second, thinner pair lower on the torso
                .ss(0.06f, 0.13f, "arm#2", "body", 6, 12, 1, 2, 22, 2, 0, -11, 0, copper)
                .s("pin#2", "arm#2", 0, -22, 0, 1, 4, 1, 0, -2, 0, ivory);
        return d
                .atk(Atk.SWIPE, 12, 4.5f, 0, 30).anim((p, e, w, s, r, a) -> { // two-armed claw rake: coil back right, scythe across
                    p.rot("body", 0.1f, -0.6f, 0, -0.25f, 0.75f, 0, w, s, r); p.pos("body", 0, -2, 2, 0, -3, -4, w, s, r);
                    p.rot("armR", 2.7f, -0.4f, 0.6f, 1.3f, 1.4f, 0.1f, w, s, r); p.rot("armR2", 2.3f, -0.2f, 0.7f, 1.2f, 1.2f, 0, w, s, r);
                    p.rot("armL", 0.6f, 0, -0.5f, 1.1f, 0.5f, -0.2f, w, s, r); p.rot("armL2", 0.3f, 0, -0.5f, 0.9f, 0.4f, 0, w, s, r);
                    p.rot("head", 0.2f, 0.5f, 0, -0.2f, -0.5f, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -0.6f, 0, 0, w, s, r);
                })
                .atk(Atk.WHIP, 11, 9f, 2f, 70).anim((p, e, w, s, r, a) -> { // spring lash: compress the coil, then fire it out like a slinky
                    p.rot("armR", 2.9f, 0.2f, 0.4f, 1.6f, 0, 0, w, s, r); p.scl("armR", 1, 0.6f, 1, 1, 3.2f, 1, w, s, r);
                    p.rot("body", 0.25f, -0.5f, 0, -0.45f, 0.35f, 0, w, s, r); p.pos("body", 0, -1, 3, 0, 0, -5, w, s, r);
                    p.rot("armL", 0.3f, 0, -0.9f, 0.5f, 0, -0.5f, w, s, r); p.rot("head", 0.3f, -0.3f, 0, -0.1f, 0.2f, 0, w, s, r);
                    if (s > 0 && r <= 0) p.add("armR", 0, Mth.sin(a * 2.5f) * 0.12f, 0); // spring wobble
                })
                .custom(8, 15f, 4f, 160, 16, 18, 12, reel(ParticleTypes.CRIT), (p, e, w, s, r, a) -> { // Spring Snare: all four arms shoot out, then reel the prey in
                    for (String n : new String[]{"armL", "armR", "armL2", "armR2"}) {
                        float sg = n.startsWith("armL") ? -1 : 1;
                        p.rot(n, 2.6f, 0, 0.9f * sg, 1.55f, -0.1f * sg, 0.05f * sg, w, s, r);
                        float[] g = p.g(n); if (s > 0 && r <= 0) g[7] = 4.2f - 3.2f * Pose_e(s); else if (w < 1) g[7] = 1 - 0.3f * w;
                    }
                    p.rot("body", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, -3, 4, 0, -1, -2, w, s, r);
                    p.rot("head", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.8f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) p.add("body", 0, 0, Mth.sin(a * 1.7f) * 0.06f); // straining while reeling
                })
                .atk(Atk.GRAB, 9, 3.5f, 0, 180, 60, "").anim((p, e, w, s, r, a) -> { // wrap all arms round the victim, hoist up to the lens
                    for (String n : new String[]{"armL", "armR", "armL2", "armR2"}) { float sg = n.startsWith("armL") ? -1 : 1; p.rot(n, 1.3f, 0, 1.3f * sg, 1.6f, 0.35f * sg, -0.3f * sg, w, s, r); }
                    p.rot("body", 0.3f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, -4, 3, 0, 4, -3, w, s, r);
                    p.rot("head", 0.3f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) { p.add("head", 0, Mth.sin(a * 0.6f) * 0.25f, Mth.sin(a * 0.9f) * 0.15f); p.add("armR", Mth.sin(a * 1.2f) * 0.1f, 0, 0); p.add("armL", -Mth.sin(a * 1.2f) * 0.1f, 0, 0); }
                })
                .atk(Atk.POUNCE, 13, 11f, 4f, 140).anim((p, e, w, s, r, a) -> { // spider drop: squat on all legs, spring up, arms splayed down
                    p.pos("body", 0, -10, 2, 0, 6, -8, w, s, r); p.rot("body", 0.35f, 0, 0, -0.8f, 0, 0, w, s, r);
                    for (String n : new String[]{"armL", "armR", "armL2", "armR2"}) { float sg = n.startsWith("armL") ? -1 : 1; p.rot(n, -0.6f, 0, 1.5f * sg, 2.2f, 0, 0.7f * sg, w, s, r); }
                    for (String n : new String[]{"legL", "legR", "legL2", "legR2"}) { float sg = n.startsWith("legL") ? -1 : 1; p.rot(n, 0, 0, -0.5f * sg, 0, 0, 0.4f * sg, w, s, r); }
                    p.rot("head", 0.5f, 0, 0, 0.7f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.9f, 0, 0, w, s, r);
                });
    }

    /** smoothstep (Pose.ease) without importing the client package at class-init time of server code paths. */
    static float Pose_e(float x) { x = Mth.clamp(x, 0, 1); return x * x * (3 - 2 * x); }

    // ================================================================= 2. COIL HATCHLING – fist-sized tin spiderling
    static CreatureDef coilHatchling() {
        int brass = 0xc9a24a, copper = 0xb5652e, teal = 0x2f8f8a, amber = 0xffb02e, dark = 0x1a1414, ivory = 0xf1ead2;
        CreatureDef d = CreatureDef.of("coil_hatchling", "Coil Hatchling", "コイル・ハッチリング").size(0.8f, 0.7f).hp(26).dmg(4).speed(0.38).egg(brass, teal).spawn(2);
        new M(d)
                .p("body", null, 0, 6, 0, 8, 6, 8, 0, 0, 0, teal).sway(0.05f, 0.3f, 0, 2)
                .p("band", "body", 0, 0, 0, 9, 1.5f, 9, 0, 0, 0, brass)
                .p("dome", "body", 0, 3, 0, 6, 2, 6, 0, 1, 0, brass)
                .p("key", "body", 0, 1, 4, 1, 1, 3, 0, 0, 1.5f, brass).p("keyBow", "key", 0, 0, 3, 5, 3, 1, 0, 0, 0, copper).sway(0.8f, 0.3f, 0, 2)
                .p("head", "body", 0, 0, -4, 6, 5, 4, 0, 0, -2, teal)
                .p("lensRim", "head", 0, 0.5f, -4.1f, 4, 4, 1, 0, 0, 0, brass)
                .p("lens", "head", 0, 0.5f, -4.6f, 2.5f, 2.5f, 1, 0, 0, 0, amber).glow()
                .p("jaw", "head", 0, -2, -3, 5, 1.5f, 2, 0, -0.5f, -0.5f, dark)
                .s("fang#", "jaw", 1.5f, -1, -1.5f, 0.8f, 2, 0.8f, 0, -0.5f, 0, ivory)
                .sw(0.8f, "leg#", null, 4, 6, -3, 6, 1.5f, 1.5f, 3, 0, 0, copper).s("foot#", "leg#", 6, 0, 0, 1.2f, 6, 1.2f, 0, -3, 0, dark)
                .sw(0.8f, "leg#2", null, 4, 6, 0, 6, 1.5f, 1.5f, 3, 0, 0, brass).s("foot#2", "leg#2", 6, 0, 0, 1.2f, 6, 1.2f, 0, -3, 0, dark)
                .sw(0.8f, "leg#3", null, 4, 6, 3, 6, 1.5f, 1.5f, 3, 0, 0, copper).s("foot#3", "leg#3", 6, 0, 0, 1.2f, 6, 1.2f, 0, -3, 0, dark)
                .ss(0.15f, 0.25f, "arm#", "head", 2, -1, -3, 1, 1, 5, 0, 0, -2.5f, brass); // feeler-pincers
        return d
                .atk(Atk.BITE, 4, 1.8f, 0, 20).anim((p, e, w, s, r, a) -> {
                    p.rot("body", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, -1, 2, 0, 0, -4, w, s, r);
                    p.rot("jaw", -0.9f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("armL", 0, -0.6f, 0, 0, 0.3f, 0, w, s, r); p.rot("armR", 0, 0.6f, 0, 0, -0.3f, 0, w, s, r);
                })
                .atk(Atk.POUNCE, 5, 6f, 2f, 70).anim((p, e, w, s, r, a) -> {
                    p.pos("body", 0, -3, 1, 0, 2, -3, w, s, r); p.rot("body", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.9f, 0, 0, w, s, r);
                    for (String n : new String[]{"legL", "legR", "legL2", "legR2", "legL3", "legR3"}) { float sg = n.startsWith("legL") ? -1 : 1; p.rot(n, 0, 0, 0.5f * sg, 0, 0, -0.6f * sg, w, s, r); }
                })
                .custom(3, 7f, 2.5f, 90, 10, 10, 8, reel(ParticleTypes.CRIT), (p, e, w, s, r, a) -> { // tiny spring-feeler snag
                    p.rot("armR", 0, 0.3f, 0, 0, 0, 0, w, s, r); p.scl("armR", 1, 1, 0.6f, 1, 1, 4f, w, s, r); p.rot("body", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r);
                    p.rot("keyBow", 0, 0, 0, 0, 0, 6f, w, s, r);
                });
    }

    // ================================================================= 3. COIL PATRIARCH (giant boss) – stilt-legged tin gentleman
    static CreatureDef coilPatriarch() {
        int brass = 0xc9a24a, copper = 0xb5652e, navy = 0x23305e, navyD = 0x161d3d, amber = 0xffb02e, dark = 0x141010, ivory = 0xf1ead2, red = 0xa3222a, silver = 0xb8bcc4;
        CreatureDef d = CreatureDef.of("coil_patriarch", "Coil Patriarch", "コイル・パトリアーク").size(2.4f, 7.2f).hp(950).dmg(15).speed(0.3).kbRes(1.0).armor(10).egg(navy, brass).boss();
        new M(d)
                .sw(0.45f, "leg#", null, 6, 64, 0, 4, 64, 4, 0, -32, 0, dark)
                .s("hip#", "leg#", 0, 0, 0, 6, 6, 6, 0, 0, 0, copper)
                .s("kneeRing#", "leg#", 0, -30, 0, 6, 2, 6, 0, 0, 0, brass)
                .s("spike#", "leg#", 0, -64, 0, 2, 4, 2, 0, -1, 0, silver)
                .p("body", null, 0, 64, 0, 20, 22, 14, 0, 11, 0, navy).sway(0.02f, 0.06f, 0, 2)
                .p("coatTail", "body", 0, 2, 5, 18, 14, 2, 0, -5, 0, navyD).sway(0.08f, 0.1f, 0, 0)
                .p("vest", "body", 0, 12, -7.2f, 10, 18, 1, 0, 0, 0, red)
                .p("button1", "vest", 0, 5, -0.6f, 2, 2, 1, 0, 0, 0, brass).p("button2", "vest", 0, 0, -0.6f, 2, 2, 1, 0, 0, 0, brass).p("button3", "vest", 0, -5, -0.6f, 2, 2, 1, 0, 0, 0, brass)
                .p("key", "body", 0, 13, 7, 3, 3, 6, 0, 0, 3, brass).p("keyBow", "key", 0, 0, 6, 14, 8, 2, 0, 0, 0, copper).sway(0.4f, 0.08f, 0, 2)
                .p("head", "body", 0, 22, 0, 16, 14, 14, 0, 7, 0, silver)
                .p("lensRim", "head", 4, 8, -7.3f, 8, 8, 1, 0, 0, 0, brass)
                .p("lens", "head", 4, 8, -7.9f, 5, 5, 1, 0, 0, 0, amber).glow()
                .p("chain", "lensRim", 4, -4, 0, 1, 8, 1, 0, -4, 0, brass)
                .p("eyeSlit", "head", -4, 8, -7.3f, 5, 1.5f, 1, 0, 0, 0, dark)
                .p("moustache", "head", 0, 3.5f, -7.5f, 12, 2.5f, 1.5f, 0, 0, 0, copper)
                .s("tache#", "moustache", 6, 0, 0, 3, 2, 1.5f, 1, 1, 0, copper)
                .p("jaw", "head", 0, 1, -6, 12, 3, 4, 0, -1.5f, -1, dark)
                .p("tooth1", "jaw", -3, 0, -3.1f, 2, 2, 1, 0, -0.5f, 0, ivory).p("tooth2", "jaw", 0, 0, -3.1f, 2, 2, 1, 0, -0.5f, 0, ivory).p("tooth3", "jaw", 3, 0, -3.1f, 2, 2, 1, 0, -0.5f, 0, ivory)
                .p("brim", "head", 0, 14, 0, 20, 2, 18, 0, 1, 0, dark)
                .p("hat", "brim", 0, 2, 0, 13, 14, 12, 0, 7, 0, dark)
                .p("hatBand", "hat", 0, 2, 0, 13.6f, 3, 12.6f, 0, 0, 0, red)
                .ss(0.05f, 0.08f, "arm#", "body", 12, 20, 0, 5, 30, 5, 0, -15, 0, navy)
                .s("cuff#", "arm#", 0, -30, 0, 6, 3, 6, 0, 0, 0, ivory)
                .s("hand#", "arm#", 0, -32, 0, 6, 6, 6, 0, -3, 0, silver)
                .p("cane", "handR", 0, -3, 0, 2, 44, 2, 0, 4, -6, dark)
                .p("caneHead", "cane", 0, 26, -6, 4, 4, 4, 0, 0, 0, brass);
        return d
                .atk(Atk.STOMP, 15, 5f, 0, 50).anim((p, e, w, s, r, a) -> { // lift a whole stilt leg high and drive it down
                    p.rot("legR", 1.6f, 0, -0.2f, -0.05f, 0, 0, w, s, r); p.rot("legL", -0.2f, 0, 0.05f, 0.15f, 0, 0, w, s, r);
                    p.rot("body", 0.2f, 0.2f, 0.12f, -0.3f, 0, -0.05f, w, s, r); p.pos("body", 0, 2, 2, 0, -5, -3, w, s, r);
                    p.rot("armL", 0.4f, 0, -0.9f, 0.2f, 0, -0.3f, w, s, r); p.rot("armR", 0.6f, 0, 0.8f, 0.1f, 0, 0.3f, w, s, r);
                    p.rot("head", 0.3f, 0, 0, -0.35f, 0, 0, w, s, r);
                })
                .atk(Atk.SLAM, 16, 5.5f, 0, 90).anim((p, e, w, s, r, a) -> { // two-handed cane smash
                    p.rot("armR", 3.0f, 0, -0.3f, 0.7f, 0, 0.2f, w, s, r); p.rot("armL", 3.0f, 0, 0.3f, 0.7f, 0, -0.2f, w, s, r);
                    p.rot("body", 0.4f, 0, 0, -0.7f, 0, 0, w, s, r); p.pos("body", 0, 3, 4, 0, -8, -8, w, s, r);
                    p.rot("head", 0.4f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                    p.rot("legL", -0.2f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("legR", 0.1f, 0, 0, -0.4f, 0, 0, w, s, r);
                })
                .custom(14, 16f, 5f, 110, 22, 4, 16, skewer(ParticleTypes.CRIT), (p, e, w, s, r, a) -> { // Stilt Skewer: rear back, spike erupts under the target
                    p.rot("legR", 0.3f, 0, 0, -0.6f, 0, 0, w, s, r); p.pos("legR", 0, 6, 0, 0, -10, 0, w, s, r);
                    p.rot("body", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("armR", 2.2f, 0, 0.4f, 1.0f, 0, 0, w, s, r); p.rot("head", 0.4f, 0, 0, -0.5f, 0, 0, w, s, r);
                    if (w < 1) p.add("body", 0, 0, Mth.sin(a * 2f) * 0.03f * w);
                })
                .atk(Atk.SUMMON, 0, 18f, 4f, 360, 3, "coil_hatchling").anim((p, e, w, s, r, a) -> { // wind his own key, spring the brood loose
                    p.rot("keyBow", 0, 0, 0, 0, 0, 9f, w, s, r); p.rot("armR", 2.2f, -0.8f, -0.6f, 0.4f, 0, 1.2f, w, s, r); p.rot("armL", 0.2f, 0, -1.1f, 0.4f, 0, -1.2f, w, s, r);
                    p.rot("head", -0.3f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.9f, 0, 0, w, s, r);
                })
                .atk(Atk.CHARGE, 13, 16f, 6f, 220).anim((p, e, w, s, r, a) -> { // hat-first ram on clattering stilts
                    p.rot("body", -0.2f, 0, 0, -0.7f, 0, 0, w, s, r); p.rot("head", 0.2f, 0, 0, 0.4f, 0, 0, w, s, r);
                    p.rot("armR", -0.6f, 0, 0.3f, -1.2f, 0, 0.2f, w, s, r); p.rot("armL", -0.6f, 0, -0.3f, -1.2f, 0, -0.2f, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(a * 1.1f) * 0.7f; p.g("legR")[0] = q; p.g("legL")[0] = -q; }
                });
    }

    // ================================================================= 4. BOLSTERPEDE (boss) – crawler stitched from striped bolster pillows
    static CreatureDef bolsterpede() {
        int lilac = 0x9a7fd1, cream = 0xf4ecd6, mint = 0x8fd6b5, dark = 0x1e1626, lamp = 0xfff2a8, red = 0xc23a4a, ivory = 0xf3efe6, seam = 0x5b3f8f;
        CreatureDef d = CreatureDef.of("bolsterpede", "Bolsterpede", "ボルスターピード").size(2.2f, 2.4f).hp(560).dmg(12).speed(0.36).kbRes(0.85).armor(4).egg(lilac, mint).boss();
        M m = new M(d)
                .p("body", null, 0, 14, -10, 18, 18, 14, 0, 0, 0, lilac).sway(0.05f, 0.12f, 0, 1)
                .p("stripeF", "body", 0, 0, 0, 18.5f, 4, 14.5f, 0, 0, 0, cream)
                .p("head", "body", 0, 4, -7, 16, 14, 12, 0, 2, -6, mint)
                .p("nightcap", "head", 0, 9, -5, 12, 5, 10, 0, 2.5f, 0, red)
                .p("capTip", "nightcap", 0, 5, 4, 5, 4, 5, 0, 0, 2, red).sway(0.2f, 0.15f, 0, 0)
                .p("pompom", "capTip", 0, 0, 4, 3, 3, 3, 0, 0, 1, ivory)
                .sg("goggle#", "head", 4, 5, -12.2f, 5, 5, 1, 0, 0, 0, lamp)
                .s("pupil#", "head", 4, 5, -12.8f, 2, 2, 1, 0.5f, -0.5f, 0, dark)
                .s("lid#", "head", 4, 8, -12.4f, 6, 1.5f, 1, 0, 0, 0, dark)
                .p("jaw", "head", 0, -3, -9, 14, 5, 6, 0, -2.5f, -1, dark)
                .p("tongue", "jaw", 0, -1, -1, 6, 1, 4, 0, 0, -1, red)
                .s("mandible#", "head", 6, -2, -12, 2, 2, 6, 0, 0, -2, ivory).sway(0.25f, 0.2f, 0, 1)
                .s("tooth#a", "head", 5, -1, -12.3f, 1.5f, 2.5f, 1, 0, -1, 0, ivory)
                .s("tooth#b", "head", 2, -1, -12.3f, 1.5f, 2.5f, 1, 0, -1, 0, ivory)
                .s("arm#", "body", 9, -4, -2, 3, 9, 3, 0, -4.5f, 0, mint); // front stubby grabbing legs
        // trailing segments chained back (each sways a little later in phase -> slithering wave)
        String prev = "body"; float z = 7;
        for (int i = 1; i <= 5; i++) {
            String n = "seg" + i; float sc = 1 - i * 0.07f;
            m.p(n, prev, 0, i == 1 ? -2 : 0, z, 16 * sc, 15 * sc, 12, 0, 0, 6, i % 2 == 0 ? lilac : cream).sway(0.12f, 0.12f, -i * 0.8f, 1)
             .p(n + "seam", n, 0, 0, 6, 16 * sc + 0.4f, 1.5f, 12.4f, 0, 0, 0, seam)
             .p(n + "fL", n, -6 * sc, -7 * sc, 6, 3, 4, 3, 0, -1, 0, i % 2 == 0 ? cream : lilac).walk(0.7f, i)
             .p(n + "fR", n, 6 * sc, -7 * sc, 6, 3, 4, 3, 0, -1, 0, i % 2 == 0 ? cream : lilac).walk(0.7f, i + P);
            prev = n; z = 12;
        }
        m.p("tail", "seg5", 0, 0, 12, 6, 6, 6, 0, 0, 2, red).sway(0.3f, 0.2f, 0, 1);
        return d
                .atk(Atk.BITE, 12, 3.5f, 0, 26).anim((p, e, w, s, r, a) -> { // rear back, gape, snap down
                    p.rot("body", 0.45f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, 4, 4, 0, -2, -8, w, s, r);
                    p.rot("head", 0.4f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("jaw", 0.9f, 0, 0, -0.05f, 0, 0, w, s, r);
                    p.rot("mandibleL", 0, -0.7f, 0, 0, 0.4f, 0, w, s, r); p.rot("mandibleR", 0, 0.7f, 0, 0, -0.4f, 0, w, s, r);
                    p.rot("seg1", -0.3f, 0, 0, 0.25f, 0, 0, w, s, r);
                })
                .custom(7, 14f, 4f, 80, 14, 6, 12, volley(0xf3efe6, "slow", 5, 0.8f), (p, e, w, s, r, a) -> { // Feather Spit: inflate, cough a fan of stuffing
                    p.rot("body", 0.6f, 0, 0, -0.1f, 0, 0, w, s, r); p.pos("body", 0, 6, 3, 0, 2, -2, w, s, r);
                    p.scl("body", 1.15f, 1.15f, 1.1f, 0.9f, 0.9f, 1.05f, w, s, r); p.rot("jaw", 0.2f, 0, 0, 1.1f, 0, 0, w, s, r);
                    p.rot("seg1", -0.5f, 0, 0, -0.2f, 0, 0, w, s, r);
                })
                .atk(Atk.SLAM, 14, 5f, 0, 90).anim((p, e, w, s, r, a) -> { // rear the whole front half up like a cobra, crash down
                    p.rot("body", 1.1f, 0, 0, -0.5f, 0, 0, w, s, r); p.pos("body", 0, 16, 6, 0, -4, -10, w, s, r);
                    p.rot("seg1", -0.7f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("seg2", -0.3f, 0, 0, 0.1f, 0, 0, w, s, r);
                    p.rot("head", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("jaw", 0.5f, 0, 0, 0.9f, 0, 0, w, s, r);
                    p.rot("armL", 2.2f, 0, -0.5f, 0.5f, 0, 0, w, s, r); p.rot("armR", 2.2f, 0, 0.5f, 0.5f, 0, 0, w, s, r);
                })
                .atk(Atk.CHARGE, 12, 15f, 5f, 160).anim((p, e, w, s, r, a) -> { // head-down wriggling rush
                    p.rot("body", -0.1f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, -2, 3, 0, -3, -2, w, s, r); p.rot("head", 0.1f, 0, 0, 0.3f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) for (int i = 1; i <= 5; i++) p.add("seg" + i, 0, Mth.sin(a * 1.4f - i) * 0.35f, 0);
                })
                .atk(Atk.GAS, 4, 6f, 0, 240, 1, "").anim((p, e, w, s, r, a) -> { // shake out a cloud of stale pillow dust
                    p.scl("body", 1, 1, 1, 1.12f, 1.1f, 1.1f, w, s, r); p.rot("jaw", 0.2f, 0, 0, 0.9f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) for (int i = 1; i <= 5; i++) p.add("seg" + i, 0, 0, Mth.sin(a * 2.2f + i) * 0.2f);
                });
    }

    // ================================================================= 5. CRANK CRATE – wooden crate, brass crank, spring-necked paper-lantern head
    static CreatureDef crankCrate() {
        int wood = 0x8a5a2b, woodD = 0x5c3a1a, green = 0x3f8a4e, brass = 0xc9a24a, paper = 0xf0d9a0, red = 0xd03a2f, dark = 0x1a1010, ivory = 0xf6f1e3, metal = 0x9aa0a8;
        CreatureDef d = CreatureDef.of("crank_crate", "Crank Crate", "クランク・クレート").size(1.2f, 2.4f).hp(95).dmg(7).speed(0.3).kbRes(0.6).armor(3).egg(green, brass).spawn(1);
        new M(d)
                .p("box", null, 0, 9, 0, 18, 18, 18, 0, 0, 0, green)
                .p("trimTop", "box", 0, 8.5f, 0, 19, 2, 19, 0, 0, 0, woodD).p("trimBot", "box", 0, -8.5f, 0, 19, 2, 19, 0, 0, 0, woodD)
                .p("plank", "box", 0, 0, -9.1f, 14, 3, 1, 0, 0, 0, wood)
                .p("star", "box", 0, 4, -9.3f, 4, 4, 1, 0, 0, 0, brass).glow()
                .p("crank", "box", 9.5f, 2, 0, 2, 2, 2, 1, 0, 0, metal).sway(0.6f, 0.12f, 0, 0)
                .p("crankArm", "crank", 2, 0, 0, 1, 7, 1.5f, 0, -3, 0, brass).p("crankKnob", "crankArm", 0, -7, 0, 2.5f, 2.5f, 2.5f, 0, 0, 0, red)
                .p("lid", "box", 0, 9, 9, 18, 1.5f, 18, 0, 0, -9, wood) // hinged at the back edge
                .s("leg#", null, 6, 1, 0, 4, 2, 4, 0, 0, 0, woodD)
                .p("body", "box", 0, 9, 0, 7, 3, 7, 0, 0, 0, metal) // spring base, rises out of the box
                .p("spring1", "body", 0, 3, 0, 6, 2, 6, 0, 0, 0, brass).p("spring2", "body", 0, 6, 0, 6, 2, 6, 0, 0, 0, metal).p("spring3", "body", 0, 9, 0, 6, 2, 6, 0, 0, 0, brass)
                .p("head", "body", 0, 11, 0, 14, 12, 12, 0, 6, 0, paper).sway(0.08f, 0.1f, 0, 2)
                .p("rib1", "head", 0, 3, 0, 14.4f, 1, 12.4f, 0, 0, 0, red).p("rib2", "head", 0, 9, 0, 14.4f, 1, 12.4f, 0, 0, 0, red)
                .p("cap", "head", 0, 12, 0, 8, 2, 8, 0, 1, 0, dark).p("tassel", "cap", 0, 2, 0, 1, 4, 1, 0, 2, 0, red).sway(0.3f, 0.2f, 0, 2)
                .sg("eye#", "head", 3, 8, -6.2f, 3, 3, 1, 0, 0, 0, 0xffe066)
                .p("jaw", "head", 0, 3.5f, -6, 10, 2.5f, 1, 0, -1, -0.5f, dark)
                .p("grin", "head", 0, 5, -6.2f, 11, 1, 1, 0, 0, 0, dark)
                .s("stitch#", "head", 3, 5, -6.5f, 1, 3, 0.5f, 0, 0, 0, ivory).p("stitchM", "head", 0, 5, -6.5f, 1, 3, 0.5f, 0, 0, 0, ivory)
                .ss(0.1f, 0.12f, "arm#", "body", 6, 6, 0, 3, 12, 3, 0, -2, 0, metal) // accordion arms
                .s("fold#", "arm#", 0, -1, 0, 4, 1.5f, 4, 0, 0, 0, brass)
                .s("mitt#", "arm#", 0, -8, 0, 5, 5, 5, 0, -2.5f, 0, red);
        return d
                .custom(8, 3.5f, 0, 60, 12, 4, 14, quake(0, 0.75f, null, ParticleTypes.POOF), (p, e, w, s, r, a) -> { // Pop Goes: crank winds, head bursts up, lid flies open
                    p.rot("crank", 0, 0, 0, 7f, 0, 0, w, s, r); p.pos("body", 0, -8, 0, 0, 14, 0, w, s, r); p.scl("body", 1, 0.5f, 1, 1, 2.2f, 1, w, s, r);
                    p.rot("lid", 0, 0, 0, -2.2f, 0, 0, w, s, r); p.rot("armL", 0, 0, -0.2f, 2.8f, 0, -0.6f, w, s, r); p.rot("armR", 0, 0, 0.2f, 2.8f, 0, 0.6f, w, s, r);
                    p.rot("jaw", 0, 0, 0, 0.8f, 0, 0, w, s, r); if (w < 1) p.add("box", 0, 0, Mth.sin(a * 3f) * 0.05f * w);
                })
                .atk(Atk.WHIP, 7, 7f, 0, 50).anim((p, e, w, s, r, a) -> { // accordion punch
                    p.rot("armR", 1.0f, 0.2f, 0.3f, 1.57f, 0, 0, w, s, r); p.scl("armR", 1, 0.5f, 1, 1, 3.0f, 1, w, s, r);
                    p.rot("body", 0, -0.4f, 0, -0.2f, 0.3f, 0, w, s, r); p.pos("body", 0, 0, 0, 0, 4, 0, w, s, r);
                    p.rot("head", 0, -0.3f, 0, -0.2f, 0.2f, 0, w, s, r);
                })
                .atk(Atk.GRAB, 6, 3f, 0, 140, 40, "").anim((p, e, w, s, r, a) -> { // stuff the victim towards the box mouth
                    p.rot("armL", 1.2f, 0, -1.1f, 1.6f, 0.3f, 0.1f, w, s, r); p.rot("armR", 1.2f, 0, 1.1f, 1.6f, -0.3f, -0.1f, w, s, r);
                    p.rot("lid", 0, 0, 0, -1.6f, 0, 0, w, s, r); p.pos("body", 0, 0, 0, 0, 6, -2, w, s, r); p.rot("head", 0, 0, 0, 0.4f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) p.add("lid", Mth.sin(a * 1.5f) * 0.4f, 0, 0);
                })
                .atk(Atk.CLAP, 6, 3f, 0, 80).anim((p, e, w, s, r, a) -> { // lid snap: whole crate tips forward, lid slams like a jaw
                    p.rot("lid", -1.8f, 0, 0, 0.1f, 0, 0, w, s, r);
                    p.rot("box", 0.3f, 0, 0, -0.25f, 0, 0, w, s, r); p.pos("body", 0, 6, 0, 0, -6, 0, w, s, r);
                });
    }

    // ================================================================= 6. BRICKREX – snap-brick toy dinosaur
    static CreatureDef brickrex() {
        int yel = 0xf2b51c, yelD = 0xc98d0e, red = 0xd23a2a, blue = 0x2b6fd6, white = 0xf4f4f0, dark = 0x1a1a1a, green = 0x47b04b;
        CreatureDef d = CreatureDef.of("brickrex", "Brickrex", "ブリックレックス").size(1.6f, 2.6f).hp(170).dmg(9).speed(0.33).kbRes(0.7).armor(5).egg(yel, blue).spawn(1);
        new M(d)
                .sw(0.8f, "leg#", null, 5, 16, 2, 7, 10, 9, 0, -2, 0, yelD)
                .s("shin#", "leg#", 0, -7, 0, 5, 9, 5, 0, -4.5f, 0, yel)
                .s("toe#", "shin#", 0, -8, -3, 6, 2, 7, 0, 0, -1, dark)
                .p("body", null, 0, 18, 2, 14, 14, 20, 0, 4, 0, yel).sway(0.02f, 0.1f, 0, 0)
                .p("belly", "body", 0, 2, -8, 10, 8, 4, 0, 0, -1, white)
                .p("stud1", "body", 0, 11.5f, -5, 3, 1.5f, 3, 0, 0, 0, red).p("stud2", "body", 0, 11.5f, 1, 3, 1.5f, 3, 0, 0, 0, blue).p("stud3", "body", 0, 11.5f, 7, 3, 1.5f, 3, 0, 0, 0, green)
                .p("neck", "body", 0, 8, -10, 9, 8, 6, 0, 0, -2, yel)
                .p("head", "neck", 0, 4, -4, 12, 10, 14, 0, 2, -6, yel)
                .p("snout", "head", 0, 1, -13, 10, 4, 4, 0, 0, 0, yelD)
                .sg("eye#", "head", 4.5f, 5, -9, 2, 3, 3, 1.5f, 0, 0, white)
                .s("pupil#", "head", 6.6f, 5, -9.5f, 0.5f, 2, 1.5f, 0, -0.3f, 0, dark)
                .p("headStud", "head", 0, 7.5f, -4, 4, 1.5f, 4, 0, 0, 0, red)
                .p("jaw", "head", 0, -3, -2, 11, 3, 13, 0, -1.5f, -6, yelD)
                .s("tooth#1", "head", 4, -3, -13, 2, 2.5f, 2, 0, -1, 0, white).s("tooth#2", "head", 4, -3, -9, 2, 2.5f, 2, 0, -1, 0, white)
                .s("jtooth#", "jaw", 3, 0, -11, 2, 2, 2, 0, 1, 0, white)
                .s("arm#", "body", 7, 4, -8, 2.5f, 6, 2.5f, 0, -3, 0, yelD).sway(0.2f, 0.2f, 0, 0)
                .s("claw#", "arm#", 0, -6, 0, 3, 2, 3, 0, 0, -1, dark)
                .p("tail", "body", 0, 6, 10, 10, 9, 10, 0, 0, 5, yel).sway(0.12f, 0.1f, 0, 1)
                .p("tail2", "tail", 0, 0, 10, 7, 6, 10, 0, 0, 5, yelD).sway(0.15f, 0.1f, -1, 1)
                .p("tail3", "tail2", 0, 0, 10, 4, 4, 8, 0, 0, 4, red).sway(0.2f, 0.1f, -2, 1)
                .p("spike1", "body", 0, 12, 4, 2, 3, 3, 0, 1.5f, 0, blue).p("spike2", "tail", 0, 5, 5, 2, 3, 3, 0, 1.5f, 0, blue);
        return d
                .atk(Atk.BITE, 10, 3f, 0, 24).anim((p, e, w, s, r, a) -> { // neck coils back, lunges down, jaws snap
                    p.rot("body", 0.15f, 0, 0, -0.25f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, -5, w, s, r);
                    p.rot("neck", 0.5f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("jaw", 0.9f, 0, 0, 0f, 0, 0, w, s, r);
                    p.rot("tail", -0.2f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("legL", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r);
                })
                .atk(Atk.STOMP, 9, 4f, 0, 70).anim((p, e, w, s, r, a) -> { // rear up on one leg, brick-crash stomp
                    p.rot("body", 0.4f, 0, 0.1f, -0.2f, 0, 0, w, s, r); p.rot("legR", 1.3f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("legL", -0.3f, 0, 0, 0.2f, 0, 0, w, s, r);
                    p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.6f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("tail", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r);
                })
                .atk(Atk.CHARGE, 10, 13f, 4f, 150).anim((p, e, w, s, r, a) -> { // head down, tail stiff, stampede
                    p.rot("body", 0.1f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("neck", -0.2f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("head", 0.2f, 0, 0, 0.3f, 0, 0, w, s, r);
                    p.rot("tail", 0.1f, 0, 0, 0.25f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(a * 1.4f) * 1.0f; p.g("legR")[0] = q; p.g("legL")[0] = -q; p.add("body", 0, 0, Mth.sin(a * 1.4f) * 0.06f); }
                })
                .atk(Atk.SPIN, 7, 4.5f, 0, 110).anim((p, e, w, s, r, a) -> { // tail-sweep pirouette
                    p.rot("body", 0, 0.6f, 0, 0, 0, 0, w, s, r); p.rot("tail", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("tail2", 0, -0.4f, 0, 0, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) p.g("body")[1] += s * Mth.TWO_PI * 2;
                })
                .atk(Atk.SCREAM, 2, 8f, 0, 300).anim((p, e, w, s, r, a) -> { // toy-speaker roar
                    p.rot("neck", 0.4f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("jaw", 0.4f, 0, 0, 1.0f, 0, 0, w, s, r);
                    p.rot("armL", 0, 0, 0, 0.8f, 0, -0.6f, w, s, r); p.rot("armR", 0, 0, 0, 0.8f, 0, 0.6f, w, s, r);
                    if (s > 0 && r <= 0) p.add("head", 0, Mth.sin(a * 3f) * 0.08f, Mth.sin(a * 2.7f) * 0.08f);
                });
    }

    // ================================================================= 7. GRUBLET – chubby larva doll in a frilled bonnet, ring-mouth
    static CreatureDef grublet() {
        int cream = 0xefe0c4, peach = 0xf2b49a, bonnet = 0x8ec9e8, lace = 0xffffff, dark = 0x1f1416, pink = 0xd96b86, ivory = 0xfaf5ea, slime = 0x9bd34f;
        CreatureDef d = CreatureDef.of("grublet", "Grublet", "グラブレット").size(0.8f, 1.1f).hp(34).dmg(4).speed(0.26).egg(cream, bonnet).spawn(2);
        new M(d)
                .p("body", null, 0, 7, 2, 10, 10, 11, 0, 0, 0, cream).sway(0.05f, 0.2f, 0, 0)
                .p("ring1", "body", 0, 0, -2, 10.5f, 10.5f, 1, 0, 0, 0, peach).p("ring2", "body", 0, 0, 2, 10.5f, 10.5f, 1, 0, 0, 0, peach)
                .p("tail", "body", 0, -2, 5.5f, 7, 6, 6, 0, 0, 3, cream).sway(0.2f, 0.2f, 0, 1)
                .p("tailTip", "tail", 0, 0, 6, 4, 4, 4, 0, -0.5f, 2, peach).sway(0.25f, 0.2f, -1, 1)
                .s("nub#", "body", 5.5f, -4, -2, 2, 2, 2, 0, 0, 0, peach).walk(0.6f, 0)
                .s("nub#2", "body", 5.5f, -4, 2, 2, 2, 2, 0, 0, 0, peach).walk(0.6f, P)
                .p("head", "body", 0, 3, -5.5f, 9, 9, 7, 0, 1, -3, cream)
                .p("bonnet", "head", 0, 3, 0.5f, 11, 7, 7, 0, 2, 0, bonnet)
                .p("frill", "bonnet", 0, 0, -3.6f, 12, 8, 1, 0, 0, 0, lace)
                .p("bow", "bonnet", 0, -4, -3.8f, 3, 2, 1, 0, 0, 0, pink)
                .s("eye#", "head", 2.5f, 3, -6.6f, 2, 2, 0.5f, 0, 0, 0, dark)
                .s("x#a", "head", 2.5f, 3, -6.9f, 2.6f, 0.6f, 0.4f, 0, 0, 0, pink) // cross-stitched button eyes
                .s("blush#", "head", 3.5f, 0.5f, -6.6f, 2, 1, 0.4f, 0, 0, 0, pink)
                .p("jaw", "head", 0, -1, -6, 5, 4, 1, 0, -1, -0.3f, dark) // round mouth plate opens downwards
                .p("tooth1", "jaw", -1.5f, 0, -0.6f, 1, 1.5f, 0.5f, 0, 0, 0, ivory).p("tooth2", "jaw", 1.5f, 0, -0.6f, 1, 1.5f, 0.5f, 0, 0, 0, ivory)
                .p("tooth3", "jaw", 0, -2, -0.6f, 1, 1.5f, 0.5f, 0, 0, 0, ivory)
                .s("arm#", "body", 5, 1, -4, 2, 4, 2, 0, -2, 0, peach).sway(0.25f, 0.2f, 0, 0)
                .p("drool", "jaw", 0, -3.5f, -0.5f, 1, 2, 0.5f, 0, -1, 0, slime).glow();
        return d
                .atk(Atk.BITE, 4, 1.8f, 0, 22).anim((p, e, w, s, r, a) -> { // rear up like a grub, chomp
                    p.rot("body", 0.4f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("jaw", 0.9f, 0, 0, 0, 0, 0, w, s, r);
                    p.scl("body", 0.9f, 1.1f, 1.1f, 1.05f, 0.95f, 0.9f, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -3, w, s, r);
                })
                .custom(5, 3f, 1.5f, 90, 12, 12, 10, flop(0.55f, ParticleTypes.ITEM_SLIME), (p, e, w, s, r, a) -> { // Belly Flop: squash, spring, flatten on landing
                    p.scl("body", 1.2f, 0.7f, 1.2f, 1, 1, 1, w, s, r); p.rot("body", 0, 0, 0, -0.4f, 0, 0, w, s, r);
                    if (r > 0 && r < 0.5f) p.g("body")[7] *= 0.7f + 0.6f * r;
                    p.rot("tail", 0, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("armL", 0, 0, 0, 0, 0, -1.2f, w, s, r); p.rot("armR", 0, 0, 0, 0, 0, 1.2f, w, s, r);
                })
                .custom(3, 9f, 2.5f, 70, 12, 2, 10, volley(0x9bd34f, "slow", 1, 0), (p, e, w, s, r, a) -> { // sticky goo spit
                    p.rot("head", -0.5f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("jaw", 0.3f, 0, 0, 1.0f, 0, 0, w, s, r); p.scl("head", 1.1f, 1.1f, 1.1f, 0.95f, 0.95f, 0.95f, w, s, r);
                    p.rot("body", 0.2f, 0, 0, -0.1f, 0, 0, w, s, r);
                });
    }

    // ================================================================= 8. SCRIBBLE BONES – a crayon doodle of a skeleton, flat as paper
    static CreatureDef scribbleBones() {
        int paper = 0xf7f3e8, ink = 0x22222b, crayonR = 0xe0453a, crayonB = 0x3c6fd9, crayonY = 0xf2cf3a, glow = 0x7cf5ff;
        CreatureDef d = CreatureDef.of("scribble_bones", "Scribble Bones", "スクリブル・ボーンズ").size(0.7f, 2.0f).hp(48).dmg(5).speed(0.34).egg(paper, ink).spawn(2);
        new M(d)
                .sw(0.9f, "leg#", null, 2.5f, 13, 0, 2, 13, 1, 0, -6.5f, 0, paper)
                .s("legLine#", "leg#", 1.2f, 0, 0, 0.5f, 13, 1.2f, 0, -6.5f, 0, ink)
                .s("foot#", "leg#", 0, -13, 0, 3, 1.5f, 3, 0, 0.7f, -1, paper)
                .p("body", null, 0, 13, 0, 6, 2, 1, 0, 0, 0, paper) // pelvis
                .p("spine", "body", 0, 1, 0, 1.5f, 10, 1, 0, 5, 0, paper)
                .p("rib1", "spine", 0, 9, 0, 8, 1.2f, 1, 0, 0, 0, paper).p("rib2", "spine", 0, 7, 0, 7, 1.2f, 1, 0, 0, 0, paper)
                .p("rib3", "spine", 0, 5, 0, 6, 1.2f, 1, 0, 0, 0, paper).p("rib4", "spine", 0, 3, 0, 4.5f, 1.2f, 1, 0, 0, 0, paper)
                .p("scribble", "spine", 0, 6, 0.7f, 7, 6, 0.3f, 0, 0, 0, ink) // ink outline behind the ribs
                .p("head", "body", 0, 12, 0, 8, 7, 3, 0, 3.5f, 0, paper).sway(0.08f, 0.15f, 0, 2)
                .sg("eye#", "head", 2, 4.5f, -1.6f, 2.5f, 2.5f, 0.4f, 0, 0, 0, glow)
                .p("nose", "head", 0, 2.5f, -1.6f, 1, 1, 0.4f, 0, 0, 0, ink)
                .p("jaw", "head", 0, 0.5f, 0, 6, 2, 2.6f, 0, -1, 0, paper)
                .p("teeth", "jaw", 0, 0, -1.4f, 5, 0.5f, 0.3f, 0, 0, 0, ink)
                .p("crayonHat", "head", 1, 7, 0, 3, 5, 3, 0, 2, 0, crayonR).sway(0.12f, 0.15f, 1, 2)
                .p("crayonTip", "crayonHat", 0, 5, 0, 1.5f, 2, 1.5f, 0, 0.5f, 0, crayonR)
                .p("crayonWrap", "crayonHat", 0, 1, 0, 3.3f, 1.5f, 3.3f, 0, 0, 0, crayonY)
                .ss(0.1f, 0.15f, "arm#", "body", 5, 10, 0, 1.5f, 10, 1, 0, -5, 0, paper)
                .s("hand#", "arm#", 0, -10, 0, 3, 2.5f, 1, 0, -1, 0, paper)
                .p("crayon", "handR", 0, -2, 0, 1.5f, 1.5f, 6, 0, 0, -2.5f, crayonB);
        return d
                .atk(Atk.SWIPE, 5, 2.5f, 0, 22).anim((p, e, w, s, r, a) -> { // crayon-slash: scratch a jagged line through the air
                    p.rot("armR", 2.6f, -0.8f, 0.4f, 0.8f, 1.2f, -0.3f, w, s, r); p.rot("body", 0, -0.5f, 0, -0.1f, 0.7f, 0, w, s, r);
                    p.rot("head", 0, 0.4f, 0, 0, -0.4f, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.5f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) p.add("armR", Mth.sin(a * 6f) * 0.3f, 0, 0); // scribbling zig-zag
                })
                .custom(4, 12f, 3f, 50, 10, 2, 10, volley(0xf7f3e8, "", 3, 0.5f), (p, e, w, s, r, a) -> { // rip off ribs and throw them
                    p.rot("armR", -0.4f, 0, 0.3f, 2.6f, 0, 0, w, s, r); p.rot("armL", 1.2f, 0.4f, 0, 0.3f, 0, -0.5f, w, s, r);
                    p.rot("body", 0, 0.5f, 0, -0.2f, -0.4f, 0, w, s, r); p.pos("rib2", 0, 0, 0, 0, 0, -2, w, s, r); p.scl("rib2", 1, 1, 1, 0.01f, 1, 1, w, s, r);
                })
                .custom(6, 7f, 2f, 90, 10, 12, 10, flop(0.45f, ParticleTypes.ASH), (p, e, w, s, r, a) -> { // Fold & Pop: crumple flat like paper, unfold mid-air onto the target
                    p.scl("body", 1, 0.3f, 1, 1.05f, 1.1f, 1, w, s, r); p.scl("legL", 1, 0.3f, 1, 1, 1, 1, w, s, r); p.scl("legR", 1, 0.3f, 1, 1, 1, 1, w, s, r);
                    p.pos("body", 0, -8, 0, 0, 3, -3, w, s, r); p.rot("armL", 0, 0, 0, 3.0f, 0, -0.4f, w, s, r); p.rot("armR", 0, 0, 0, 3.0f, 0, 0.4f, w, s, r);
                    if (s > 0 && r <= 0) p.g("body")[0] -= s * Mth.TWO_PI;
                })
                .atk(Atk.SPIN, 3, 2.5f, 0, 100).anim((p, e, w, s, r, a) -> { // spinning bone pinwheel
                    p.rot("armL", 0, 0, 0, 0, 0, -1.57f, w, s, r); p.rot("armR", 0, 0, 0, 0, 0, 1.57f, w, s, r); p.rot("head", 0.2f, 0, 0, -0.1f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) p.g("body")[1] += s * Mth.TWO_PI * 4;
                });
    }

    // ================================================================= 9. GUMDROP TABBY – a cat built from gumdrops, candy-cane tail, wrapper bow
    static CreatureDef gumdropTabby() {
        int orange = 0xff8f3a, lime = 0x9be04a, cherry = 0xe2324a, grape = 0x8a4fd1, sugar = 0xfff5fa, dark = 0x1c1018, mint = 0x6ff0c8, white = 0xffffff;
        CreatureDef d = CreatureDef.of("gumdrop_tabby", "Gumdrop Tabby", "ガムドロップ・タビー").size(0.8f, 1.3f).hp(40).dmg(5).speed(0.37).egg(orange, cherry).spawn(2);
        new M(d)
                .sw(1.0f, "leg#", null, 3, 6, -4, 3, 6, 3, 0, -3, 0, orange).s("paw#", "leg#", 0, -6, -0.5f, 3.5f, 1.5f, 4, 0, 0.7f, 0, sugar)
                .sw(1.0f, "leg#2", null, 3, 6, 5, 3, 6, 3, 0, -3, 0, orange)
                .p("body", null, 0, 9, 1, 9, 7, 14, 0, 0, 0, orange).sway(0.02f, 0.15f, 0, 2)
                .p("drop1", "body", 0, 3.5f, -3, 5, 2, 4, 0, 0.5f, 0, cherry).p("drop2", "body", 0, 3.5f, 3, 5, 2, 4, 0, 0.5f, 0, lime)
                .p("stripe1", "body", 0, 0, 0, 9.3f, 7.3f, 1.5f, 0, 0, 0, grape)
                .p("collar", "body", 0, 2, -7, 8, 2, 2, 0, 0, 0, cherry).p("bowL", "collar", -2.5f, 0, -1, 3, 3, 1, 0, 0, 0, sugar).p("bowR", "collar", 2.5f, 0, -1, 3, 3, 1, 0, 0, 0, sugar)
                .p("head", "body", 0, 4, -8, 9, 8, 8, 0, 3, -3, orange)
                .s("ear#", "head", 3, 7, -2, 3, 4, 2, 0, 2, 0, cherry).s("earIn#", "ear#", 0, 1.5f, -1.1f, 1.5f, 2, 0.3f, 0, 0, 0, sugar)
                .sg("eye#", "head", 2.2f, 4, -7.1f, 2.5f, 3, 0.5f, 0, 0, 0, mint)
                .s("slit#", "head", 2.2f, 4, -7.5f, 0.6f, 2.5f, 0.3f, 0, 0, 0, dark)
                .p("nose", "head", 0, 2, -7.5f, 1.5f, 1, 0.5f, 0, 0, 0, cherry)
                .s("whisker#", "head", 4.5f, 1.8f, -6.5f, 4, 0.3f, 0.3f, 1, 0, 0, white)
                .p("jaw", "head", 0, 0.5f, -6, 5, 1.5f, 2, 0, -0.5f, -0.5f, dark)
                .s("fang#", "jaw", 1.3f, 0, -1.2f, 0.8f, 1.5f, 0.5f, 0, -0.5f, 0, sugar)
                .p("tail", "body", 0, 2, 7, 2, 2, 8, 0, 0, 4, white).sway(0.3f, 0.15f, 0, 1)
                .p("tailStripe", "tail", 0, 0, 3, 2.3f, 2.3f, 2, 0, 0, 0, cherry)
                .p("tailHook", "tail", 0, 0, 8, 2, 6, 2, 0, 3, 0, cherry).sway(0.25f, 0.2f, -1, 0)
                .s("arm#", "body", 3, -3, -5, 3, 6, 3, 0, -3, 0, orange) // front legs = armL/R so swipes drive them
                .s("claw#", "arm#", 0, -6, -1, 3.2f, 1.5f, 2, 0, 0.5f, 0, sugar);
        return d
                .atk(Atk.SWIPE, 5, 2.2f, 0, 18).anim((p, e, w, s, r, a) -> { // rear up, double paw rake
                    p.rot("body", 0.6f, 0, 0, 0.2f, 0, 0, w, s, r); p.pos("body", 0, 3, 2, 0, 1, -2, w, s, r);
                    p.rot("armR", 2.4f, -0.3f, 0.3f, 0.6f, 0.4f, 0, w, s, r); p.rot("armL", 1.8f, 0.3f, -0.3f, 1.2f, -0.2f, 0, w, s, r);
                    p.rot("head", -0.3f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("jaw", 0.5f, 0, 0, 0.7f, 0, 0, w, s, r); p.rot("tail", 0.5f, 0, 0, -0.3f, 0, 0, w, s, r);
                })
                .atk(Atk.POUNCE, 6, 7f, 2.5f, 60).anim((p, e, w, s, r, a) -> { // butt-wiggle then leap
                    p.pos("body", 0, -3, 2, 0, 3, -4, w, s, r); p.rot("body", 0.2f, 0, 0, -0.3f, 0, 0, w, s, r);
                    if (w > 0 && s <= 0) p.add("body", 0, 0, Mth.sin(a * 2.5f) * 0.12f * w);
                    p.rot("armL", -0.5f, 0, 0, 2.2f, 0, 0, w, s, r); p.rot("armR", -0.5f, 0, 0, 2.2f, 0, 0, w, s, r); p.rot("legL2", 0.5f, 0, 0, -1.0f, 0, 0, w, s, r); p.rot("legR2", 0.5f, 0, 0, -1.0f, 0, 0, w, s, r);
                    p.rot("jaw", 0, 0, 0, 0.8f, 0, 0, w, s, r);
                })
                .atk(Atk.BITE, 5, 1.8f, 0, 30).anim((p, e, w, s, r, a) -> {
                    p.rot("head", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.9f, 0, 0, 0, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, -1, -3, w, s, r);
                })
                .custom(3, 10f, 3f, 80, 12, 2, 10, volley(0xe2324a, "slow", 3, 0.6f), (p, e, w, s, r, a) -> { // hack up a hairball of sticky gumdrops
                    p.rot("body", 0.2f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.2f, 0, 0, 1.0f, 0, 0, w, s, r);
                    if (w > 0 && s <= 0) p.add("body", Mth.sin(a * 3f) * 0.08f * w, 0, 0); // retching heave
                });
    }

    // ================================================================= 10. MOTHMOUSE – fuzzy mouse with dusty moth wings and lamp antennae
    static CreatureDef mothmouse() {
        int fur = 0xb9a58a, furD = 0x8c7a63, wing = 0xd9cba5, wingE = 0x6e5a3c, eyeSpot = 0x3a6fd0, pink = 0xf0a0b0, dark = 0x140e0c, lamp = 0xfff2a0, ruff = 0xf4ecdc;
        CreatureDef d = CreatureDef.of("mothmouse", "Mothmouse", "モスマウス").size(0.7f, 1.0f).hp(28).dmg(4).speed(0.4).egg(wing, eyeSpot).spawn(2);
        new M(d)
                .sw(1.0f, "leg#", null, 2, 4, 1, 2, 4, 3, 0, -2, 0, furD)
                .p("body", null, 0, 7, 1, 7, 7, 9, 0, 0, 0, fur).sway(0.06f, 0.25f, 0, 0)
                .p("ruff", "body", 0, 2, -4, 9, 4, 3, 0, 0, 0, ruff)
                .p("head", "body", 0, 2, -5, 6, 6, 5, 0, 1, -2.5f, fur)
                .p("snout", "head", 0, 0, -5, 3, 2.5f, 2, 0, 0, -0.5f, furD).p("noseTip", "snout", 0, 0.5f, -1.6f, 1, 1, 0.5f, 0, 0, 0, pink)
                .sg("eye#", "head", 1.8f, 2.5f, -5.1f, 2, 2, 0.5f, 0, 0, 0, dark)
                .s("ear#", "head", 3, 4, -1, 4, 4, 1, 0, 1.5f, 0, pink)
                .p("jaw", "head", 0, -1.5f, -4, 2.5f, 1, 2, 0, -0.5f, -0.5f, dark)
                .s("tooth#", "jaw", 0.5f, 0, -1.6f, 0.8f, 1.5f, 0.4f, 0, -0.5f, 0, ruff)
                .s("antenna#", "head", 1.5f, 4, -2, 0.5f, 6, 0.5f, 0.5f, 3, -1, wingE).sway(0.2f, 0.2f, 0, 2)
                .sg("bulb#", "antenna#", 0.5f, 6, -1, 1.5f, 1.5f, 1.5f, 0, 0, 0, lamp)
                .ss(0.5f, 0.6f, "wing#", "body", 3, 3, 1, 11, 1, 8, 5.5f, 0, 0, wing)
                .s("spot#", "wing#", 6, 0.6f, 0, 3, 0.2f, 3, 0, 0, 0, eyeSpot)
                .s("edge#", "wing#", 11, 0, 0, 1, 1.2f, 8, 0, 0, 0, wingE)
                .s("arm#", "body", 2.5f, -1, -3, 1.5f, 4, 1.5f, 0, -2, 0, furD)
                .p("tail", "body", 0, -1, 4.5f, 1, 1, 9, 0, 0, 4.5f, pink).sway(0.3f, 0.2f, 0, 1);
        return d
                .atk(Atk.BITE, 4, 1.6f, 0, 18).anim((p, e, w, s, r, a) -> {
                    p.rot("head", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.8f, 0, 0, 0, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -3, w, s, r);
                    p.rot("wingL", 0, 0, 0.3f, 0, 0, -0.4f, w, s, r); p.rot("wingR", 0, 0, -0.3f, 0, 0, 0.4f, w, s, r);
                })
                .custom(3, 5f, 0, 120, 14, 14, 10, cone(ParticleTypes.WHITE_ASH, MobEffects.BLINDNESS, 80), (p, e, w, s, r, a) -> { // Wing Dust: wings fold high, then beat a blinding cloud
                    p.rot("wingL", 0, 0, -1.2f, 0, 0, 0, w, s, r); p.rot("wingR", 0, 0, 1.2f, 0, 0, 0, w, s, r); p.pos("body", 0, 0, 0, 0, 4, 0, w, s, r);
                    if (s > 0 && r <= 0) { float f = Mth.sin(a * 2.4f) * 1.1f; p.add("wingL", 0, 0, f); p.add("wingR", 0, 0, -f); }
                    p.rot("antennaL", 0, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("antennaR", 0, 0, 0, -0.5f, 0, 0, w, s, r);
                })
                .atk(Atk.POUNCE, 5, 7f, 2f, 60).anim((p, e, w, s, r, a) -> { // fluttering dive
                    p.pos("body", 0, -2, 1, 0, 4, -3, w, s, r); p.rot("body", 0.2f, 0, 0, -0.5f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) { float f = Mth.sin(a * 2.8f) * 0.9f; p.add("wingL", 0, 0, f); p.add("wingR", 0, 0, -f); }
                    p.rot("armL", 0, 0, 0, 1.5f, 0, 0, w, s, r); p.rot("armR", 0, 0, 0, 1.5f, 0, 0, w, s, r);
                })
                .atk(Atk.SHOOT, 3, 10f, 3f, 70, 0xfff2a0, "blind").anim((p, e, w, s, r, a) -> { // flick a spark from the antenna bulbs
                    p.rot("head", -0.4f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("antennaL", 0, 0, -0.3f, 1.0f, 0, 0, w, s, r); p.rot("antennaR", 0, 0, 0.3f, 1.0f, 0, 0, w, s, r);
                    p.scl("bulbL", 1, 1, 1, 1.8f, 1.8f, 1.8f, w, s, r); p.scl("bulbR", 1, 1, 1, 1.8f, 1.8f, 1.8f, w, s, r);
                });
    }

    private BatchB() {}
}
