package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;
import com.poppy.mod.entity.Fx;
import com.poppy.mod.entity.PoppyBolt;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** Batch G – canon Poppy Playtime roster (see DEV_GUIDE.md). */
public final class BatchG {
    static final float P = (float) Math.PI;

    public static List<CreatureDef> all() {
        List<CreatureDef> l = new ArrayList<>();
        l.add(cleaverMatron());
        l.add(rivetHound());
        l.add(gravelGrinder());
        l.add(bootstrapBert());
        l.add(grimgiggle());
        l.add(pirouette());
        l.add(taffyTyrant());
        l.add(hollowHattie());
        l.add(stingwing());
        l.add(cuddleClump());
        l.add(plushImp());
        l.add(staticSleeper());
        l.add(joystickJuggernaut());
        return l;
    }

    // ---------- shared model helpers ----------
    /** Pair of eyes (sclera + pupil) on parent at ±x. */
    static CreatureDef eyes(CreatureDef d, String par, String pre, float x, float y, float z, float s, int white, int pupil, boolean glow) {
        for (int sg = -1; sg <= 1; sg += 2) {
            String side = sg < 0 ? "L" : "R";
            d.part(pre + "Eye" + side, par, sg * x, y, z, s, s, 1, 0, 0, 0, white); if (glow) d.glow();
            d.part(pre + "Pupil" + side, par, sg * x, y, z - 0.6f, s * 0.45f, s * 0.45f, 0.5f, 0, 0, 0, pupil).glow();
        }
        return d;
    }
    /** Row of n teeth centred on x=0. */
    static CreatureDef teeth(CreatureDef d, String par, String pre, float y, float z, int n, float tw, float th, float gap, int col) {
        float x0 = -(n - 1) * gap / 2f;
        for (int i = 0; i < n; i++) d.part(pre + i, par, x0 + i * gap, y, z, tw, th, 0.8f, 0, 0, 0, col);
        return d;
    }

    // ---------- shared server helpers ----------
    static void snd(PoppyMob m, SoundEvent s, float vol, float pitch) { m.playSound(s, vol, pitch); }
    static void puff(PoppyMob m, ParticleOptions p, Vec3 c, int n, double spread) {
        if (m.level() instanceof ServerLevel lv) lv.sendParticles(p, c.x, c.y, c.z, n, spread, spread * 0.5, spread, 0.05);
    }
    static Vec3 front(PoppyMob m, double dist, double up) { return m.position().add(Fx.flat(m).scale(dist)).add(0, up, 0); }
    static void bolt(PoppyMob m, float dmg, int col, String style, float speed, float spreadYaw) {
        if (!(m.level() instanceof ServerLevel lv) || m.getTarget() == null) return;
        Vec3 o = m.getEyePosition(); Vec3 d = m.getTarget().getEyePosition().subtract(o).normalize().yRot(spreadYaw);
        PoppyBolt b = new PoppyBolt(lv, m, dmg, col, style); b.setPos(o.x, o.y - 0.2, o.z); b.shoot(d.x, d.y, d.z, speed, 1f); lv.addFreshEntity(b);
    }
    static void effect(LivingEntity e, net.minecraft.world.effect.MobEffect ef, int ticks, int amp) { e.addEffect(new MobEffectInstance(ef, ticks, amp)); }

    // =====================================================================
    // 1. Cleaver Matron – porcelain-faced kitchen-play doll, two cleaver hands, apron, bun.
    static CreatureDef cleaverMatron() {
        int skin = 0xe9d9c4, apron = 0xf3f0ea, dress = 0x6b2a3a, hair = 0x3a2a22, steel = 0xb8c0c8, wood = 0x6b4a2a, blood = 0x8a1010, mouth = 0x1a0808;
        CreatureDef d = CreatureDef.of("mother_marie", "Mother Marie", "マザー・マリー").size(1.0f, 2.6f).hp(90).dmg(7).speed(0.3).kbRes(0.3).egg(apron, dress).spawn(1)
                .part("body", null, 0, 22, 0, 14, 16, 9, 0, 8, 0, dress).sway(0.02f, 0.1f, 0, 2)
                .part("skirt", "body", 0, 0, 0, 18, 8, 12, 0, -3, 0, dress)
                .part("apron", "body", 0, 4, -4.8f, 10, 14, 1, 0, -1, 0, apron)
                .part("apronStain", "apron", 2, 0, -0.6f, 4, 3, 0.5f, 0, 0, 0, blood)
                .part("apronTie", "body", 0, 10, 4.8f, 6, 2, 1, 0, 0, 0, apron)
                .part("pocket", "apron", -2, -4, -0.6f, 4, 3, 0.5f, 0, 0, 0, 0xd8d0c4)
                .part("head", "body", 0, 16, 0, 12, 12, 11, 0, 6, 0, skin)
                .part("crack", "head", 3, 9, -5.6f, 1, 5, 0.4f, 0, 0, 0, 0x6a5a4a)
                .part("crack2", "head", 4, 7, -5.6f, 2, 1, 0.4f, 0, 0, 0, 0x6a5a4a)
                .part("bun", "head", 0, 14, 2, 7, 5, 6, 0, 0, 0, hair)
                .part("hairTop", "head", 0, 12, 0, 13, 2, 12, 0, 0, 0, hair)
                .part("hairBack", "head", 0, 7, 5, 13, 10, 2, 0, 0, 0, hair)
                .part("cheekL", "head", -4, 3.5f, -5.7f, 2, 1, 0.4f, 0, 0, 0, 0xe08a8a)
                .part("cheekR", "head", 4, 3.5f, -5.7f, 2, 1, 0.4f, 0, 0, 0, 0xe08a8a)
                .part("jaw", "head", 0, 2, -5.6f, 7, 2, 1, 0, 0, 0, mouth);
        eyes(d, "head", "", 3, 6.5f, -5.7f, 2.4f, 0x101010, 0xe02020, false);
        teeth(d, "jaw", "tooth", 0.6f, -0.6f, 4, 1.2f, 1, 1.6f, 0xf0ead8);
        d.part("armL", "body", -8.5f, 14, 0, 4, 14, 4, 0, -7, 0, skin).sway(0.04f, 0.1f, 0, 0)
                .part("cleaverL", "armL", 0, -14, -1, 1, 6, 9, 0, -2, -3, steel)
                .part("handleL", "armL", 0, -14, 0, 2, 2, 2, 0, 0, 0, wood)
                .part("armR", "body", 8.5f, 14, 0, 4, 14, 4, 0, -7, 0, skin).sway(0.04f, 0.1f, P, 0)
                .part("cleaverR", "armR", 0, -14, -1, 1, 6, 9, 0, -2, -3, steel)
                .part("cleaverEdgeR", "cleaverR", 0, -5.2f, -3, 1.2f, 0.6f, 9, 0, 0, 0, blood)
                .part("handleR", "armR", 0, -14, 0, 2, 2, 2, 0, 0, 0, wood)
                .part("legL", null, -3, 14, 0, 4, 14, 4, 0, -7, 0, 0xf4f4f4).walk(0.8f, 0)
                .part("shoeL", "legL", 0, -14, -1, 5, 2, 6, 0, 1, 0, 0x201818)
                .part("legR", null, 3, 14, 0, 4, 14, 4, 0, -7, 0, 0xf4f4f4).walk(0.8f, P)
                .part("shoeR", "legR", 0, -14, -1, 5, 2, 6, 0, 1, 0, 0x201818);
        // Signature: double-cleaver "mince" — both arms rise behind the head, then chop down alternately.
        d.custom(8, 3.4f, 0, 50, 14, 14, 12, (m, s, t) -> {
            int h = s.wind() + 1;
            if (t >= h && t < h + s.strike() && (t - h) % 5 == 0) {
                snd(m, SoundEvents.ANVIL_PLACE, 0.6f, 1.6f + (t - h) * 0.02f);
                for (LivingEntity e : Fx.arc(m, s.reach(), 0.3)) { Fx.hit(m, e, s.dmg() * 0.45f, 0.2f, Fx.flat(m)); effect(e, MobEffects.MOVEMENT_SLOWDOWN, 30, 1); }
                puff(m, ParticleTypes.CRIT, front(m, 1.5, 1.0), 8, 0.3);
            }
        }, (p, e, w, s, r, age) -> {
            float chop = s > 0 && r <= 0 ? Mth.sin(s * P * 6) : 0;
            p.rot("armL", 2.9f, 0, -0.2f, 0.6f, 0, -0.1f, w, s, r); p.rot("armR", 2.9f, 0, 0.2f, 0.6f, 0, 0.1f, w, s, r);
            p.add("armL", chop * 1.1f, 0, 0); p.add("armR", -chop * 1.1f, 0, 0);
            p.rot("body", 0.25f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0.15f, -0.2f, 0, -0.1f, w, s, r);
            p.add("body", 0, chop * 0.15f, 0); p.pos("body", 0, 1, 2, 0, -2, -3, w, s, r);
        });
        // Wide butchering sweep with one cleaver, whole body twisting.
        d.atk(Atk.SWIPE, 7, 3.6f, 0, 30).anim((p, e, w, s, r, age) -> {
            p.rot("armR", 1.2f, -1.9f, 0.5f, 1.3f, 1.5f, 0.2f, w, s, r); p.rot("armL", -0.4f, 0, -0.5f, 0.3f, 0, -0.2f, w, s, r);
            p.rot("body", 0.05f, -1.0f, 0, -0.15f, 1.0f, 0, w, s, r); p.rot("skirt", 0, 0.3f, 0, 0, -0.4f, 0, w, s, r);
            p.rot("head", 0, 0.6f, 0, 0, -0.3f, 0, w, s, r); p.rot("legR", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r);
        });
        // Twirling cleaver tornado.
        d.atk(Atk.SPIN, 5, 3.2f, 0, 140).anim((p, e, w, s, r, age) -> {
            p.rot("armL", 0.2f, 0, -0.6f, 0.1f, 0, -1.55f, w, s, r); p.rot("armR", 0.2f, 0, 0.6f, 0.1f, 0, 1.55f, w, s, r);
            if (s > 0 && r <= 0) p.g("body")[1] += s * P * 8;
            p.rot("skirt", 0, 0, 0, 0, 0, 0, w, s, r); p.scl("skirt", 1, 1, 1, 1.35f, 0.8f, 1.35f, w, s, r);
            p.rot("head", 0.3f, 0, 0, -0.1f, 0, 0, w, s, r);
        });
        // Thrown cleaver (over-arm hurl).
        d.atk(Atk.SHOOT, 6, 14, 4, 70, 0xc8d0d8, "").anim((p, e, w, s, r, age) -> {
            p.rot("armR", 3.0f, 0, 0.3f, 0.2f, 0, -0.1f, w, s, r); p.rot("body", 0.3f, 0.4f, 0, -0.35f, -0.3f, 0, w, s, r);
            p.scl("cleaverR", 1, 1, 1, 0.01f, 0.01f, 0.01f, w, s, r); p.rot("legL", -0.5f, 0, 0, 0.3f, 0, 0, w, s, r);
        });
        return d;
    }

    // =====================================================================
    // 2. Rivet Hound – tin wind-up robo-dog, wind key, antenna, bolted panels, red visor eyes.
    static CreatureDef rivetHound() {
        int tin = 0x9aa3ad, dark = 0x4d555e, rust = 0x9a5a2c, red = 0xff3020, gold = 0xd8b040, black = 0x111111;
        CreatureDef d = CreatureDef.of("nightmare_chompers", "Nightmare Chum Chompers", "ナイトメア・チャムチョンパーズ").size(0.9f, 1.2f).hp(40).dmg(5).speed(0.38).kbRes(0.2).egg(tin, red).spawn(2)
                .part("body", null, 0, 11, 0, 9, 8, 16, 0, 0, 0, tin).sway(0.02f, 0.2f, 0, 2)
                .part("panel", "body", 0, 1, -0.5f, 9.4f, 3, 10, 0, 0, 0, dark)
                .part("rivet1", "body", -4.9f, 2, -5, 0.6f, 1, 1, 0, 0, 0, gold)
                .part("rivet2", "body", 4.9f, 2, -5, 0.6f, 1, 1, 0, 0, 0, gold)
                .part("rivet3", "body", -4.9f, 2, 5, 0.6f, 1, 1, 0, 0, 0, gold)
                .part("rivet4", "body", 4.9f, 2, 5, 0.6f, 1, 1, 0, 0, 0, gold)
                .part("rustPatch", "body", 2, -4.2f, 2, 4, 0.5f, 5, 0, 0, 0, rust)
                .part("keyShaft", "body", 0, 4, 2, 1, 4, 1, 0, 2, 0, gold)
                .part("key", "keyShaft", 0, 4, 0, 6, 3, 1, 0, 1, 0, gold).sway(3f, 0.25f, 0, 2)
                .part("head", "body", 0, 3, -8, 8, 7, 7, 0, 2, -3, tin)
                .part("visor", "head", 0, 3.5f, -6.6f, 7, 2, 1, 0, 0, 0, black)
                .part("eyeL", "head", -2, 3.5f, -7.1f, 2, 1.4f, 0.5f, 0, 0, 0, red).glow()
                .part("eyeR", "head", 2, 3.5f, -7.1f, 2, 1.4f, 0.5f, 0, 0, 0, red).glow()
                .part("snout", "head", 0, 0.5f, -7, 5, 3, 5, 0, 0, -2, dark)
                .part("nose", "snout", 0, 1, -4.6f, 2, 1.4f, 1, 0, 0, 0, black)
                .part("jaw", "head", 0, -1.5f, -6, 5, 1.5f, 6, 0, -0.5f, -2, dark);
        teeth(d, "jaw", "fang", 0.6f, -4.6f, 4, 0.8f, 1.4f, 1.2f, 0xeeeeee);
        d.part("earL", "head", -3, 6, -1, 2, 4, 1, 0, 2, 0, dark).sway(0.2f, 0.3f, 0, 2)
                .part("earR", "head", 3, 6, -1, 2, 4, 1, 0, 2, 0, dark).sway(0.2f, 0.3f, P, 2)
                .part("antenna", "head", 2, 6, 1, 0.6f, 6, 0.6f, 0, 3, 0, black).sway(0.3f, 0.4f, 0, 0)
                .part("antennaTip", "antenna", 0, 6, 0, 1.4f, 1.4f, 1.4f, 0, 0, 0, red).glow()
                .part("tail", "body", 0, 2, 8, 1.4f, 1.4f, 6, 0, 0, 3, dark).sway(0.5f, 0.6f, 0, 1)
                .part("legL", "body", -3.5f, -4, -5, 2.5f, 7, 2.5f, 0, -3.5f, 0, dark).walk(1f, 0)
                .part("legR", "body", 3.5f, -4, -5, 2.5f, 7, 2.5f, 0, -3.5f, 0, dark).walk(1f, P)
                .part("legL2", "body", -3.5f, -4, 5, 2.5f, 7, 2.5f, 0, -3.5f, 0, dark).walk(1f, P)
                .part("legR2", "body", 3.5f, -4, 5, 2.5f, 7, 2.5f, 0, -3.5f, 0, dark).walk(1f, 0);
        d.atk(Atk.BITE, 5, 2.2f, 0, 25).anim((p, e, w, s, r, age) -> {
            p.rot("jaw", 0.9f, 0, 0, 0f, 0, 0, w, s, r); p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r);
            p.pos("body", 0, -1, 3, 0, 0, -5, w, s, r); p.rot("body", 0.15f, 0, 0, -0.1f, 0, 0, w, s, r);
            p.rot("legL", -0.6f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("legR", -0.6f, 0, 0, 0.5f, 0, 0, w, s, r);
        });
        d.atk(Atk.POUNCE, 6, 8, 3, 80);
        // Signature: "laser bark" — key winds up fast, head recoils, red bolt from visor.
        d.atk(Atk.SHOOT, 4, 14, 3, 60, 0xff3020, "fire").anim((p, e, w, s, r, age) -> {
            p.g("key")[2] += w * age * 0.8f * (r > 0 ? 0 : 1);
            p.rot("head", 0.4f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("jaw", 0.2f, 0, 0, 1.0f, 0, 0, w, s, r);
            p.pos("body", 0, -1, 1, 0, 0, 2.5f, w, s, r); p.scl("antennaTip", 1, 1, 1, 2, 2, 2, w, s, r);
        });
        d.atk(Atk.CHARGE, 6, 12, 4, 160);
        return d;
    }

    // =====================================================================
    // 3. Gravel Grinder – monster-truck toy turned beast; grille mouth, headlight eyes, four big wheels.
    static CreatureDef gravelGrinder() {
        int paint = 0x2a8a3a, trim = 0xe8c020, tire = 0x1a1a1a, rim = 0xb0b0b0, glass = 0x5a8ab0, light = 0xfff2a0, grille = 0x2a2a2a;
        CreatureDef d = CreatureDef.of("giant_bigger_bot", "Giant Bigger Bot", "ジャイアント・ビガーボット").size(2.0f, 2.2f).hp(160).dmg(9).speed(0.32).kbRes(0.9).armor(4).egg(paint, trim).spawn(1)
                .part("body", null, 0, 20, 0, 26, 10, 36, 0, 0, 0, paint).sway(0.03f, 0.3f, 0, 2)
                .part("stripe", "body", 0, 5.2f, 0, 8, 0.5f, 36, 0, 0, 0, trim)
                .part("cab", "body", 0, 5, 4, 20, 9, 16, 0, 4.5f, 0, paint)
                .part("windshield", "cab", 0, 5, -8.2f, 17, 6, 0.5f, 0, 0, 0, glass)
                .part("roofLights", "cab", 0, 9.5f, -6, 14, 1.5f, 2, 0, 0, 0, light).glow()
                .part("head", "body", 0, 0, -18, 24, 9, 2, 0, 0, 0, grille)
                .part("jaw", "head", 0, -4, -1, 24, 3, 4, 0, -1.5f, -1, 0x3a3a3a)
                .part("bumper", "jaw", 0, -2, -2.5f, 27, 2, 2, 0, 0, 0, rim)
                .part("lightL", "head", -9, 1.5f, -1.3f, 5, 4, 1, 0, 0, 0, light).glow()
                .part("lightR", "head", 9, 1.5f, -1.3f, 5, 4, 1, 0, 0, 0, light).glow()
                .part("pupilL", "lightL", 0, -0.5f, -0.6f, 2, 2, 0.5f, 0, 0, 0, 0x200000)
                .part("pupilR", "lightR", 0, -0.5f, -0.6f, 2, 2, 0.5f, 0, 0, 0, 0x200000);
        teeth(d, "head", "grilleTooth", -2.5f, -1.3f, 7, 2, 4, 2.6f, 0xe8e8e0);
        teeth(d, "jaw", "lowTooth", 1, -3.2f, 6, 2, 3, 3, 0xe8e8e0);
        d.part("exhaustL", "cab", -9, 6, 7, 2, 10, 2, 0, 5, 0, rim).part("exhaustR", "cab", 9, 6, 7, 2, 10, 2, 0, 5, 0, rim)
                .part("legL", null, -15, 10, -11, 6, 20, 20, 0, 0, 0, tire).walk(1.4f, 0)
                .part("hubL", "legL", -3.2f, 0, 0, 0.5f, 8, 8, 0, 0, 0, rim)
                .part("legR", null, 15, 10, -11, 6, 20, 20, 0, 0, 0, tire).walk(1.4f, 0)
                .part("hubR", "legR", 3.2f, 0, 0, 0.5f, 8, 8, 0, 0, 0, rim)
                .part("legL2", null, -15, 10, 12, 6, 20, 20, 0, 0, 0, tire).walk(1.4f, 0)
                .part("hubL2", "legL2", -3.2f, 0, 0, 0.5f, 8, 8, 0, 0, 0, rim)
                .part("legR2", null, 15, 10, 12, 6, 20, 20, 0, 0, 0, tire).walk(1.4f, 0)
                .part("hubR2", "legR2", 3.2f, 0, 0, 0.5f, 8, 8, 0, 0, 0, rim);
        d.atk(Atk.BITE, 8, 3.2f, 0, 35).anim((p, e, w, s, r, age) -> {
            p.rot("jaw", 0.7f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("body", 0.15f, 0, 0, -0.12f, 0, 0, w, s, r); p.pos("body", 0, 1, 3, 0, -1, -5, w, s, r);
        });
        // Signature: wheelie slam – rears up on back wheels, front axle crashes down.
        d.custom(11, 4.5f, 0, 120, 18, 4, 16, (m, s, t) -> {
            if (t == s.wind() + 1) {
                snd(m, SoundEvents.GENERIC_EXPLODE, 1.2f, 0.8f); Vec3 c = front(m, 1.8, 0);
                for (LivingEntity e : Fx.foes(m, c, s.reach())) { Fx.hit(m, e, s.dmg(), 1.2f, e.position().subtract(c)); e.setDeltaMovement(e.getDeltaMovement().add(0, 0.6, 0)); e.hurtMarked = true; }
                puff(m, ParticleTypes.EXPLOSION, c, 3, 1.2); puff(m, ParticleTypes.CAMPFIRE_COSY_SMOKE, c, 20, 1.5);
            }
            if (t < s.wind() && t % 4 == 0) { snd(m, SoundEvents.MINECART_RIDING, 0.6f, 0.6f); puff(m, ParticleTypes.LARGE_SMOKE, m.position().add(Fx.flat(m).scale(-1.5)).add(0, 1.8, 0), 3, 0.2); }
        }, (p, e, w, s, r, age) -> {
            p.rot("body", 0.9f, 0, 0, -0.15f, 0, 0, w, s, r); p.pos("body", 0, 6, 4, 0, -2, -3, w, s, r);
            p.pos("legL", 0, 13, 2, 0, -1, -2, w, s, r); p.pos("legR", 0, 13, 2, 0, -1, -2, w, s, r);
            p.g("legL2")[0] += w * age * 0.6f * (r > 0 ? 0 : 1); p.g("legR2")[0] += w * age * 0.6f * (r > 0 ? 0 : 1);
            p.rot("jaw", 0.5f, 0, 0, 0.9f, 0, 0, w, s, r); p.add("body", 0, 0, Mth.sin(age * 2f) * 0.03f * (1 - s));
        });
        d.atk(Atk.CHARGE, 12, 18, 5, 140).anim((p, e, w, s, r, age) -> {
            p.rot("body", 0.12f, 0, 0, -0.08f, 0, 0, w, s, r); p.pos("body", 0, 0, 3, 0, -1, -2, w, s, r);
            float spin = (w + s) * age; p.g("legL")[0] -= spin; p.g("legR")[0] -= spin; p.g("legL2")[0] -= spin; p.g("legR2")[0] -= spin;
            p.add("body", 0, 0, Mth.sin(age * 3f) * 0.05f * w);
        });
        // Exhaust blast – black smoke cloud.
        d.atk(Atk.GAS, 2, 6, 0, 200, 0, "").anim((p, e, w, s, r, age) -> {
            p.rot("body", -0.15f, 0, 0, 0.1f, 0, 0, w, s, r); p.scl("exhaustL", 1, 1, 1, 1.3f, 1.4f, 1.3f, w, s, r); p.scl("exhaustR", 1, 1, 1, 1.3f, 1.4f, 1.3f, w, s, r);
            p.rot("jaw", 0.3f, 0, 0, 0.6f, 0, 0, w, s, r);
        });
        return d;
    }

    // =====================================================================
    // 4. Bootstrap Bert – wind-up tin "kick-me" toy: huge boot on a spring leg, sign on back, grinning face.
    static CreatureDef bootstrapBert() {
        int jacket = 0x3060b0, pants = 0xd0a030, skin = 0xf0c8a0, boot = 0x5a2a10, sign = 0xf4ecd0, hat = 0xc02020;
        CreatureDef d = CreatureDef.of("gregg", "Gregg", "グレッグ").size(0.8f, 1.9f).hp(45).dmg(5).speed(0.34).kbRes(0.1).egg(jacket, boot).spawn(2)
                .part("body", null, 0, 16, 0, 10, 10, 6, 0, 5, 0, jacket).sway(0.04f, 0.2f, 0, 2)
                .part("btn1", "body", 0, 7, -3.2f, 1, 1, 0.4f, 0, 0, 0, pants).part("btn2", "body", 0, 4, -3.2f, 1, 1, 0.4f, 0, 0, 0, pants)
                .part("sign", "body", 0, 5, 3.4f, 8, 6, 0.5f, 0, 0, 0, sign)
                .part("signText", "sign", 0, 0, 0.3f, 6, 1, 0.2f, 0, 1, 0, 0x202020)
                .part("signText2", "sign", 0, 0, 0.3f, 4, 1, 0.2f, 0, -1, 0, 0x202020)
                .part("head", "body", 0, 10, 0, 9, 9, 8, 0, 4.5f, 0, skin)
                .part("hat", "head", 0, 9, 0, 10, 2, 9, 0, 0, 0, hat).part("hatTop", "hat", 0, 2, 0, 7, 3, 7, 0, 0, 0, hat)
                .part("nose", "head", 0, 4, -4.5f, 2, 2, 2, 0, 0, -0.5f, 0xe06040)
                .part("jaw", "head", 0, 1.5f, -4.2f, 6, 1.5f, 1, 0, 0, 0, 0x300808);
        eyes(d, "head", "", 2.2f, 6, -4.2f, 2, 0xffffff, 0x101010, false);
        teeth(d, "jaw", "tooth", 0.3f, -0.6f, 4, 1, 0.8f, 1.3f, 0xffffff);
        d.part("armL", "body", -6, 9, 0, 2.5f, 9, 2.5f, 0, -4.5f, 0, jacket).sway(0.1f, 0.2f, 0, 0)
                .part("handL", "armL", 0, -9, 0, 3, 3, 3, 0, -1, 0, 0xffffff)
                .part("armR", "body", 6, 9, 0, 2.5f, 9, 2.5f, 0, -4.5f, 0, jacket).sway(0.1f, 0.2f, P, 0)
                .part("handR", "armR", 0, -9, 0, 3, 3, 3, 0, -1, 0, 0xffffff)
                .part("keyShaft", "body", 0, 5, 3.5f, 1, 1, 3, 0, 0, 2, 0xb0b0b0)
                .part("key", "keyShaft", 0, 0, 4, 5, 3, 0.6f, 0, 0, 0, 0xd8b040).sway(2f, 0.3f, 0, 2)
                .part("legL", null, -2.5f, 16, 0, 3.5f, 14, 3.5f, 0, -7, 0, pants).walk(0.9f, 0)
                .part("bootL", "legL", 0, -14, -1, 5, 3, 7, 0, 1.5f, -1, boot)
                .part("legR", null, 2.5f, 16, 0, 3.5f, 14, 3.5f, 0, -7, 0, pants).walk(0.9f, P)
                .part("spring1", "legR", 0, -10, 0, 4.4f, 0.6f, 4.4f, 0, 0, 0, 0xc0c0c0).part("spring2", "legR", 0, -7, 0, 4.4f, 0.6f, 4.4f, 0, 0, 0, 0xc0c0c0)
                .part("bootR", "legR", 0, -14, -1, 7, 4, 10, 0, 2, -2, boot)
                .part("toeCap", "bootR", 0, 0, -6.5f, 6, 3, 2, 0, 2, 0, 0x3a1a08);
        // Signature: flying spring kick – crouch, coil the spring leg back, launch boot forward.
        d.custom(6, 2.8f, 0, 40, 12, 5, 12, (m, s, t) -> {
            if (t == s.wind() + 1) {
                snd(m, SoundEvents.PISTON_EXTEND, 1f, 1.6f); Vec3 f = Fx.flat(m);
                m.setDeltaMovement(f.x * 0.5, 0.25, f.z * 0.5); m.hasImpulse = true;
                for (LivingEntity e : Fx.arc(m, s.reach(), 0.3)) { Fx.hit(m, e, s.dmg(), 1.8f, f); e.setDeltaMovement(e.getDeltaMovement().add(0, 0.4, 0)); }
                puff(m, ParticleTypes.CLOUD, front(m, 1.2, 0.6), 6, 0.2);
            }
        }, (p, e, w, s, r, age) -> {
            p.rot("legR", -1.4f, 0, 0, 1.6f, 0, 0, w, s, r); p.scl("legR", 1, 0.7f, 1, 1, 1.3f, 1, w, s, r);
            p.rot("legL", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("body", 0.35f, 0, 0, -0.55f, 0, 0, w, s, r);
            p.pos("body", 0, -3, 2, 0, 2, -2, w, s, r);
            p.rot("armL", -0.8f, 0, -0.6f, 1.2f, 0, -1.2f, w, s, r); p.rot("armR", -0.8f, 0, 0.6f, 1.2f, 0, 1.2f, w, s, r);
            p.rot("head", 0.3f, 0, 0, 0.5f, 0, 0, w, s, r);
        });
        // Back-flip stomp onto the target.
        d.atk(Atk.POUNCE, 6, 7, 2.5f, 80).anim((p, e, w, s, r, age) -> {
            p.pos("body", 0, -4, 0, 0, 3, -6, w, s, r);
            if (s > 0 && r <= 0) p.g("body")[0] -= s * P * 2;
            p.rot("legL", 1.2f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("legR", 1.2f, 0, 0, -0.4f, 0, 0, w, s, r);
            p.rot("armL", -0.5f, 0, -0.4f, 2.8f, 0, -0.2f, w, s, r); p.rot("armR", -0.5f, 0, 0.4f, 2.8f, 0, 0.2f, w, s, r);
        });
        // Clumsy tin-fist punch.
        d.atk(Atk.SWIPE, 4, 2.6f, 0, 25);
        return d;
    }

    // =====================================================================
    // 5. Grimgiggle – jester that lives in a crank box; accordion spring torso, belled hat, painted grin.
    static CreatureDef grimgiggle() {
        int box = 0x8a1a8a, boxTrim = 0xf0c030, suit = 0x20a050, suit2 = 0xe02040, face = 0xf8f4ec, bell = 0xf0d040, mouth = 0x200010;
        CreatureDef d = CreatureDef.of("boxy_boo", "Boxy Boo", "ボクシーブー").size(1.0f, 2.4f).hp(80).dmg(6).speed(0.22).kbRes(0.6).egg(box, suit).spawn(1)
                .part("crate", null, 0, 6, 0, 14, 12, 14, 0, 0, 0, box)
                .part("crateTrim", "crate", 0, 6.2f, 0, 15, 1, 15, 0, 0, 0, boxTrim)
                .part("crateStar", "crate", 0, 0, -7.2f, 5, 5, 0.5f, 0, 0, 0, boxTrim)
                .part("lid", "crate", 0, 6, 7, 14, 1, 14, 0, 0, -7, box).sway(0.08f, 0.15f, 0, 0)
                .part("crank", "crate", 7.5f, 1, 0, 1, 1, 1, 0, 0, 0, 0xb0b0b0)
                .part("crankArm", "crank", 1, 0, 0, 1, 5, 1, 0, -2, 0, 0xb0b0b0).sway(P, 0.15f, 0, 0)
                .part("body", "crate", 0, 6, 0, 8, 12, 6, 0, 6, 0, suit).sway(0.12f, 0.12f, 0, 2)
                .part("coil1", "body", 0, 3, 0, 9.5f, 1, 7.5f, 0, 0, 0, 0xb0b0b0).part("coil2", "body", 0, 7, 0, 9.5f, 1, 7.5f, 0, 0, 0, 0xb0b0b0)
                .part("half", "body", 2, 6, -3.1f, 4, 12, 0.5f, 0, 0, 0, suit2)
                .part("collar", "body", 0, 12, 0, 12, 2, 10, 0, 0, 0, suit2)
                .part("head", "body", 0, 13, 0, 9, 9, 8, 0, 4.5f, 0, face)
                .part("hatL", "head", -3, 9, 0, 3, 7, 3, -2, 2, 0, suit).sway(0.25f, 0.2f, 0, 2)
                .part("bellL", "hatL", -3, 5, 0, 2, 2, 2, 0, 0, 0, bell)
                .part("hatR", "head", 3, 9, 0, 3, 7, 3, 2, 2, 0, suit2).sway(0.25f, 0.2f, P, 2)
                .part("bellR", "hatR", 3, 5, 0, 2, 2, 2, 0, 0, 0, bell)
                .part("tearL", "head", -2.5f, 3.5f, -4.2f, 1, 2, 0.3f, 0, 0, 0, 0x2040c0)
                .part("noseBall", "head", 0, 4.5f, -4.6f, 2, 2, 1.5f, 0, 0, 0, 0xe01010)
                .part("jaw", "head", 0, 1.5f, -4.2f, 7, 2, 1, 0, 0, 0, mouth)
                .part("grinL", "head", -4, 2.6f, -4.2f, 1.5f, 1, 0.4f, 0, 0, 0, mouth).part("grinR", "head", 4, 2.6f, -4.2f, 1.5f, 1, 0.4f, 0, 0, 0, mouth);
        eyes(d, "head", "", 2.3f, 6, -4.2f, 2.2f, 0x101010, 0xffe040, true);
        teeth(d, "jaw", "tooth", 0.4f, -0.6f, 5, 1, 1, 1.3f, 0xfff8e0);
        d.part("armL", "body", -5, 10, 0, 2.5f, 10, 2.5f, 0, -5, 0, suit2).sway(0.15f, 0.2f, 0, 2)
                .part("gloveL", "armL", 0, -10, 0, 3.5f, 3.5f, 3.5f, 0, -1, 0, 0xffffff)
                .part("armR", "body", 5, 10, 0, 2.5f, 10, 2.5f, 0, -5, 0, suit).sway(0.15f, 0.2f, P, 2)
                .part("gloveR", "armR", 0, -10, 0, 3.5f, 3.5f, 3.5f, 0, -1, 0, 0xffffff);
        // Signature: spring-loaded lunge – sinks into the box, then the coil fires the head forward.
        d.custom(7, 4.2f, 0, 45, 14, 4, 14, (m, s, t) -> {
            if (t == 4) snd(m, SoundEvents.NOTE_BLOCK_CHIME.value(), 1f, 0.6f);
            if (t == s.wind() + 1) {
                snd(m, SoundEvents.PISTON_EXTEND, 1.2f, 0.7f);
                Vec3 a = m.getEyePosition(), b = a.add(Fx.flat(m).scale(s.reach()));
                for (LivingEntity e : Fx.line(m, a, b, 0.9)) { Fx.hit(m, e, s.dmg(), 1.0f, Fx.flat(m)); effect(e, MobEffects.CONFUSION, 80, 0); }
                puff(m, ParticleTypes.NOTE, a.add(Fx.flat(m).scale(2)), 6, 0.6);
            }
        }, (p, e, w, s, r, age) -> {
            p.pos("body", 0, -9, 0, 0, 6, -12, w, s, r); p.scl("body", 1, 0.4f, 1, 1, 1.5f, 1, w, s, r);
            p.rot("body", 0, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("lid", -1.8f, 0, 0, -2.2f, 0, 0, w, s, r);
            p.rot("jaw", 0, 0, 0, 0.9f, 0, 0, w, s, r); p.rot("armL", 0, 0, -0.3f, 2.4f, 0, -0.3f, w, s, r); p.rot("armR", 0, 0, 0.3f, 2.4f, 0, 0.3f, w, s, r);
            p.g("crankArm")[0] += w * age * 0.9f * (r > 0 ? 0 : 1);
        });
        // Confetti blast (blinding).
        d.atk(Atk.SHOOT, 4, 14, 3, 60, 0xff60e0, "blind").anim((p, e, w, s, r, age) -> {
            p.rot("armR", 0.5f, 0, 0.4f, 2.0f, 0, -0.2f, w, s, r); p.rot("armL", 0.5f, 0, -0.4f, 2.0f, 0, 0.2f, w, s, r);
            p.rot("head", 0.3f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.6f, 0, 0, w, s, r);
        });
        // Cymbal-like glove clap (stun).
        d.atk(Atk.CLAP, 5, 3, 0, 70);
        // Laugh – shrill cackle, bells ringing.
        d.atk(Atk.SCREAM, 2, 8, 2, 220).anim((p, e, w, s, r, age) -> {
            float shake = (s > 0 && r <= 0) ? Mth.sin(age * 2.5f) * 0.3f : 0;
            p.rot("head", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("jaw", 0.2f, 0, 0, 1.1f, 0, 0, w, s, r);
            p.add("head", 0, 0, shake); p.add("hatL", shake * 2, 0, 0); p.add("hatR", -shake * 2, 0, 0);
            p.rot("body", 0.2f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("armL", 0, 0, -1, 0, 0, -2.4f, w, s, r); p.rot("armR", 0, 0, 1, 0, 0, 2.4f, w, s, r);
        });
        return d;
    }

    // =====================================================================
    // 6. Pirouette – cracked music-box ballerina; tutu, rotating pedestal leg, glass eyes.
    static CreatureDef pirouette() {
        int porcelain = 0xf4ece6, tutu = 0xf0a0c0, bodice = 0xc04070, hair = 0xe0c070, base = 0x4a2a5a, gold = 0xd8b040;
        CreatureDef d = CreatureDef.of("nightmare_ruby", "Nightmare Ruby", "ナイトメア・ルビー").size(0.8f, 2.3f).hp(60).dmg(6).speed(0.33).egg(tutu, base).spawn(1)
                .part("body", null, 0, 20, 0, 7, 10, 5, 0, 5, 0, bodice).sway(0.04f, 0.15f, 0, 1)
                .part("tutu", "body", 0, 0, 0, 18, 2, 16, 0, 0, 0, tutu).sway(0.06f, 0.3f, 0, 2)
                .part("tutu2", "tutu", 0, -1.5f, 0, 15, 1.5f, 13, 0, 0, 0, 0xf8c8dc)
                .part("bow", "body", 0, 9, -2.7f, 3, 2, 0.5f, 0, 0, 0, tutu)
                .part("head", "body", 0, 10, 0, 7, 8, 7, 0, 4, 0, porcelain)
                .part("bunHair", "head", 0, 9, 1, 4, 3, 4, 0, 0, 0, hair).part("hairCap", "head", 0, 7.5f, 0.5f, 7.6f, 2, 7.6f, 0, 0, 0, hair)
                .part("tiara", "head", 0, 8.5f, -2.5f, 5, 1.5f, 0.5f, 0, 0, 0, gold)
                .part("crack", "head", -2, 5, -3.6f, 1, 4, 0.3f, 0, 0, 0, 0x403030).part("crackB", "head", -1, 3, -3.6f, 2, 1, 0.3f, 0, 0, 0, 0x403030)
                .part("jaw", "head", 0, 1.5f, -3.6f, 3, 1, 0.5f, 0, 0, 0, 0x501020);
        eyes(d, "head", "", 1.7f, 4.5f, -3.6f, 1.8f, 0x80c0ff, 0x101030, true);
        d.part("armL", "body", -4.5f, 9, 0, 2, 10, 2, 0, -5, 0, porcelain).sway(0.3f, 0.15f, 0, 2)
                .part("armR", "body", 4.5f, 9, 0, 2, 10, 2, 0, -5, 0, porcelain).sway(0.3f, 0.15f, P, 2)
                .part("legL", null, 0, 20, 0, 2.5f, 16, 2.5f, 0, -8, 0, porcelain)
                .part("shoe", "legL", 0, -16, 0, 3, 3, 3, 0, 0.5f, 0, tutu)
                .part("legR", "body", 1.5f, 0, 0, 2.2f, 12, 2.2f, 0, -6, 0, porcelain).sway(0.15f, 0.15f, 0, 0)
                .part("pedestal", null, 0, 2, 0, 10, 2, 10, 0, 0, 0, base).part("pedestalTrim", "pedestal", 0, 1.2f, 0, 11, 0.6f, 11, 0, 0, 0, gold);
        // Signature: grand fouetté – whirls on the point, extended leg slicing.
        d.atk(Atk.SPIN, 6, 3.2f, 0, 90).anim((p, e, w, s, r, age) -> {
            p.rot("armL", 0, 0, -0.5f, 0, 0, -2.8f, w, s, r); p.rot("armR", 0, 0, 0.5f, 0, 0, 2.8f, w, s, r);
            p.rot("legR", 0.3f, 0, 0.4f, 0, 0, 1.5f, w, s, r);
            if (s > 0 && r <= 0) { p.g("body")[1] += s * P * 10; p.g("legL")[1] += s * P * 10; }
            p.pos("body", 0, -2, 0, 0, 1, 0, w, s, r);
        });
        // Arabesque kick: leans forward, back leg swings high then snaps.
        d.custom(6, 3, 0, 35, 10, 4, 10, (m, s, t) -> {
            if (t == s.wind() + 1) { snd(m, SoundEvents.GLASS_HIT, 1f, 1.4f);
                for (LivingEntity e : Fx.arc(m, s.reach(), 0.3)) Fx.hit(m, e, s.dmg(), 0.9f, Fx.flat(m)); }
        }, (p, e, w, s, r, age) -> {
            p.rot("body", 0.7f, 0, 0, -0.5f, 0.3f, 0, w, s, r); p.rot("legR", -2.2f, 0, 0, 1.6f, 0, 0.3f, w, s, r);
            p.rot("armL", 2.6f, 0, -0.2f, 1.2f, 0, -1.2f, w, s, r); p.rot("armR", -0.5f, 0, 0.8f, 0.3f, 0, 1.3f, w, s, r);
            p.rot("head", -0.5f, 0, 0, 0.2f, 0, 0, w, s, r);
        });
        // Music box shriek – cracked lullaby (glass shards burst).
        d.custom(3, 8, 2, 200, 16, 14, 10, (m, s, t) -> {
            if (t == 2) snd(m, SoundEvents.NOTE_BLOCK_BELL.value(), 1.5f, 0.5f);
            if (t == s.wind() + 1) { snd(m, SoundEvents.GLASS_BREAK, 1.5f, 0.6f);
                for (LivingEntity e : Fx.foes(m, m.position(), s.reach())) { Fx.hit(m, e, s.dmg(), 0.5f, e.position().subtract(m.position())); effect(e, MobEffects.MOVEMENT_SLOWDOWN, 80, 2); effect(e, MobEffects.CONFUSION, 100, 0); } }
            if (t > s.wind() && t <= s.wind() + s.strike() && t % 2 == 0) puff(m, ParticleTypes.NOTE, m.getEyePosition(), 3, 1.5);
        }, (p, e, w, s, r, age) -> {
            p.rot("head", 0.4f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, 0.6f, 0, 0, w, s, r);
            p.rot("armL", 0, 0, -0.3f, 3.0f, 0, -0.3f, w, s, r); p.rot("armR", 0, 0, 0.3f, 3.0f, 0, 0.3f, w, s, r);
            p.add("head", 0, 0, (s > 0 && r <= 0) ? Mth.sin(age * 3) * 0.25f : 0);
        });
        return d;
    }

    // =====================================================================
    // 7. Taffy Tyrant – melted candy-shop mascot gone feral: dripping sugar body, lollipop staff arm.
    static CreatureDef taffyTyrant() {
        int taffy = 0xff8fb8, deep = 0xd04880, drip = 0xffb8d4, swirl = 0xffffff, teal = 0x40d0c0, mouth = 0x3a0018;
        CreatureDef d = CreatureDef.of("giant_kissy", "Giant Kissy Missy", "ジャイアント・キッシー").size(1.6f, 3.2f).hp(200).dmg(9).speed(0.27).kbRes(0.7).egg(taffy, teal).spawn(1)
                .part("body", null, 0, 22, 0, 22, 22, 16, 0, 11, 0, taffy).sway(0.03f, 0.08f, 0, 2)
                .part("belly", "body", 0, 9, -8.2f, 14, 12, 1, 0, 0, 0, drip)
                .part("swirl1", "belly", -2, 2, -0.6f, 6, 1.5f, 0.4f, 0, 0, 0, swirl).part("swirl2", "belly", 2, -1, -0.6f, 6, 1.5f, 0.4f, 0, 0, 0, swirl)
                .part("drip1", "body", -7, 0, -7, 3, 6, 3, 0, -3, 0, drip).sway(0.1f, 0.1f, 0, 0)
                .part("drip2", "body", 6, 0, -6, 2, 9, 2, 0, -4.5f, 0, drip).sway(0.1f, 0.12f, 1, 0)
                .part("drip3", "body", 0, 0, 7, 3, 7, 3, 0, -3.5f, 0, drip)
                .part("head", "body", 0, 22, 0, 20, 14, 16, 0, 7, 0, taffy)
                .part("wrapperL", "head", -10, 8, 0, 6, 8, 2, -3, 0, 0, teal).sway(0.15f, 0.2f, 0, 2)
                .part("wrapperR", "head", 10, 8, 0, 6, 8, 2, 3, 0, 0, teal).sway(0.15f, 0.2f, P, 2)
                .part("jaw", "head", 0, 3, -8, 16, 4, 6, 0, -2, -1, mouth)
                .part("tongue", "jaw", 0, -1, -1, 10, 1, 4, 0, 0, 0, 0xff4070);
        eyes(d, "head", "", 5, 10, -8.2f, 4, 0xffffff, 0x101010, false);
        teeth(d, "head", "upper", 3, -8.3f, 7, 1.6f, 2.5f, 2.2f, 0xfff4c0);
        teeth(d, "jaw", "lower", 0.2f, -4.2f, 6, 1.6f, 2, 2.4f, 0xfff4c0);
        d.part("armL", "body", -13, 18, 0, 6, 22, 6, 0, -11, 0, deep).sway(0.05f, 0.1f, 0, 0)
                .part("handL", "armL", 0, -22, 0, 8, 7, 8, 0, -2, 0, taffy)
                .part("armR", "body", 13, 18, 0, 6, 22, 6, 0, -11, 0, deep).sway(0.05f, 0.1f, P, 0)
                .part("lollyStick", "armR", 0, -22, -2, 2, 2, 20, 0, 0, -7, swirl)
                .part("lollyHead", "lollyStick", 0, 0, -18, 12, 12, 3, 0, 0, 0, teal)
                .part("lollySwirl", "lollyHead", 0, 0, -1.7f, 8, 2, 0.5f, 0, 0, 0, swirl)
                .part("legL", null, -6, 12, 0, 8, 12, 8, 0, -6, 0, deep).walk(0.7f, 0)
                .part("legR", null, 6, 12, 0, 8, 12, 8, 0, -6, 0, deep).walk(0.7f, P);
        // Signature: lollipop hammer – staff swung from behind the back over the head.
        d.atk(Atk.SLAM, 12, 4.5f, 0, 90).anim((p, e, w, s, r, age) -> {
            p.rot("armR", -1.0f, 0, 0.2f, 1.2f, 0, 0, w, s, r); p.rot("lollyStick", 2.2f, 0, 0, 0.2f, 0, 0, w, s, r);
            p.rot("armL", 2.4f, 0, -0.3f, 0.9f, 0, -0.2f, w, s, r);
            p.rot("body", 0.4f, 0, 0, -0.6f, 0, 0, w, s, r); p.pos("body", 0, 2, 3, 0, -4, -4, w, s, r);
            p.rot("head", 0.4f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("jaw", 0.1f, 0, 0, 0.6f, 0, 0, w, s, r);
            p.rot("legR", -0.4f, 0, 0, 0.4f, 0, 0, w, s, r);
        });
        d.atk(Atk.GRAB, 6, 3.4f, 0, 160, 40, "");
        // Sticky syrup spew (slows + poisons).
        d.atk(Atk.GAS, 3, 6, 0, 220, 1, "").anim((p, e, w, s, r, age) -> {
            p.rot("head", 0.5f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("jaw", 0.3f, 0, 0, 1.1f, 0, 0, w, s, r);
            p.rot("body", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.scl("tongue", 1, 1, 1, 1, 1, 2.2f, w, s, r);
        });
        d.atk(Atk.SHOOT, 6, 16, 4, 70, 0xff8fb8, "slow");
        return d;
    }

    // =====================================================================
    // 8. Hollow Hattie – antique porcelain doll; bonnet, lace dress, empty eye sockets with pinprick lights.
    static CreatureDef hollowHattie() {
        int face = 0xf2e6dc, dress = 0x2a2a50, lace = 0xf0eada, hair = 0x7a2a1a, bonnet = 0x8a7aa0, socket = 0x050505, pin = 0xe0f0ff;
        CreatureDef d = CreatureDef.of("nightmare_poppy", "Nightmare Poppy", "ナイトメア・ポピー").size(0.7f, 1.6f).hp(70).dmg(6).speed(0.3).kbRes(0.2).egg(face, dress).spawn(1)
                .part("body", null, 0, 10, 0, 8, 8, 5, 0, 4, 0, dress).sway(0.03f, 0.08f, 0, 2)
                .part("skirt", "body", 0, 0, 0, 12, 7, 9, 0, -3, 0, dress)
                .part("hem", "skirt", 0, -6.5f, 0, 12.6f, 1, 9.6f, 0, 0, 0, lace)
                .part("collar", "body", 0, 8, 0, 9, 1.5f, 6, 0, 0, 0, lace)
                .part("btn", "body", 0, 5, -2.7f, 1, 1, 0.4f, 0, 0, 0, lace)
                .part("head", "body", 0, 8, 0, 9, 9, 8, 0, 4.5f, 0, face)
                .part("bonnet", "head", 0, 7, 1, 11, 6, 8, 0, 0, 0, bonnet)
                .part("bonnetRim", "bonnet", 0, -1, -4.2f, 12, 7, 1, 0, 0, 0, lace)
                .part("curlL", "head", -5, 3, 0, 2, 6, 2, 0, -2, 0, hair).sway(0.1f, 0.1f, 0, 2)
                .part("curlR", "head", 5, 3, 0, 2, 6, 2, 0, -2, 0, hair).sway(0.1f, 0.1f, P, 2)
                .part("hairBack", "head", 0, 3, 4, 9, 8, 1, 0, 0, 0, hair)
                .part("eyeL", "head", -2, 5, -4.1f, 2.5f, 2.5f, 0.4f, 0, 0, 0, socket).part("eyeR", "head", 2, 5, -4.1f, 2.5f, 2.5f, 0.4f, 0, 0, 0, socket)
                .part("pinL", "eyeL", 0, 0, -0.3f, 0.6f, 0.6f, 0.3f, 0, 0, 0, pin).glow().part("pinR", "eyeR", 0, 0, -0.3f, 0.6f, 0.6f, 0.3f, 0, 0, 0, pin).glow()
                .part("crack", "head", 2.5f, 2, -4.2f, 0.6f, 4, 0.3f, 0, 0, 0, 0x302020)
                .part("blushL", "head", -3, 3, -4.1f, 1.5f, 1, 0.3f, 0, 0, 0, 0xe09090).part("blushR", "head", 3, 3, -4.1f, 1.5f, 1, 0.3f, 0, 0, 0, 0xe09090)
                .part("jaw", "head", 0, 1.5f, -4.1f, 3, 1, 0.4f, 0, 0, 0, 0x8a1020)
                .part("armL", "body", -5, 7, 0, 2, 8, 2, 0, -4, 0, face).sway(0.05f, 0.1f, 0, 0)
                .part("armR", "body", 5, 7, 0, 2, 8, 2, 0, -4, 0, face).sway(0.05f, 0.1f, P, 0)
                .part("legL", null, -2, 4, 0, 2.5f, 4, 2.5f, 0, -2, 0, lace).walk(0.6f, 0)
                .part("legR", null, 2, 4, 0, 2.5f, 4, 2.5f, 0, -2, 0, lace).walk(0.6f, P);
        // Signature: "unblinking stare" – head creaks 180°, sockets flare, victims are blinded and frozen.
        d.custom(4, 12, 0, 220, 20, 12, 14, (m, s, t) -> {
            if (t == 3) snd(m, SoundEvents.WOODEN_DOOR_OPEN, 1f, 0.4f);
            if (t == s.wind() + 1) { snd(m, SoundEvents.WARDEN_HEARTBEAT, 2f, 1.2f);
                LivingEntity tg = m.getTarget();
                for (LivingEntity e : Fx.arc(m, s.reach(), 0.6)) { if (!m.hasLineOfSight(e)) continue; Fx.hit(m, e, s.dmg(), 0, Vec3.ZERO); effect(e, MobEffects.BLINDNESS, 60, 0); effect(e, MobEffects.MOVEMENT_SLOWDOWN, 60, 4); }
                if (tg != null) puff(m, ParticleTypes.SCULK_SOUL, tg.getEyePosition(), 10, 0.3);
            }
        }, (p, e, w, s, r, age) -> {
            float creak = w < 1 ? Mth.sin(age * 4) * 0.05f : 0;
            p.rot("head", 0, P, 0.3f, 0, 0, -0.1f, w, s, r); p.add("head", 0, creak, 0);
            p.scl("pinL", 1, 1, 1, 4, 4, 1, w, s, r); p.scl("pinR", 1, 1, 1, 4, 4, 1, w, s, r);
            p.rot("armL", 0, 0, 0, 1.4f, 0, 0, w, s, r); p.rot("armR", 0, 0, 0, 1.4f, 0, 0, w, s, r);
            p.pos("body", 0, 0, 0, 0, 2, 0, w, s, r);
        });
        // Ribbon-hair whip.
        d.atk(Atk.WHIP, 4, 8, 2, 80).anim((p, e, w, s, r, age) -> {
            p.rot("armR", 2.6f, 0, 0.6f, 1.4f, 0, -0.2f, w, s, r); p.rot("curlR", 2.0f, 0, 0, -1.6f, 0, 0, w, s, r);
            p.scl("curlR", 1, 1, 1, 1, 4, 1, w, s, r); p.rot("body", 0, 0.5f, 0, 0, -0.4f, 0, w, s, r);
        });
        // Toddling bite with tiny porcelain teeth.
        d.atk(Atk.BITE, 5, 2, 0, 25);
        // Calls more dolls.
        d.atk(Atk.SUMMON, 0, 14, 2, 600, 2, "mini_plush").anim((p, e, w, s, r, age) -> {
            p.rot("armL", 0, 0, -0.3f, 2.9f, 0, -0.4f, w, s, r); p.rot("armR", 0, 0, 0.3f, 2.9f, 0, 0.4f, w, s, r);
            p.rot("head", 0, 0, 0, -0.5f, 0, 0.4f, w, s, r); p.pos("body", 0, 0, 0, 0, 3, 0, w, s, r);
        });
        return d;
    }

    // =====================================================================
    // 9. Stingwing – striped clockwork wasp toy; hovering, propeller wings, drill stinger.
    static CreatureDef stingwing() {
        int yel = 0xf0c020, blk = 0x181818, wing = 0xd0e8ff, steel = 0xa0a8b0, red = 0xe02020;
        CreatureDef d = CreatureDef.of("stinger_flynn", "Stinger Flynn", "スティンガー・フリン").size(0.9f, 1.4f).hp(35).dmg(4).speed(0.36).egg(yel, blk).spawn(2)
                .part("body", null, 0, 14, 0, 8, 7, 9, 0, 0, 0, yel).sway(0.08f, 0.25f, 0, 0)
                .part("band1", "body", 0, 0, 1.5f, 8.4f, 7.4f, 1.5f, 0, 0, 0, blk)
                .part("tail", "body", 0, -1, 4.5f, 6, 6, 8, 0, -1, 3, yel).sway(0.15f, 0.3f, 0, 0)
                .part("band2", "tail", 0, -1, 2, 6.4f, 6.4f, 1.5f, 0, 0, 0, blk).part("band3", "tail", 0, -1, 5, 6.4f, 6.4f, 1.5f, 0, 0, 0, blk)
                .part("stinger", "tail", 0, -3, 7, 1.5f, 1.5f, 6, 0, 0, 3, steel)
                .part("stingTip", "stinger", 0, 0, 6.5f, 0.8f, 0.8f, 2, 0, 0, 0, red).glow()
                .part("head", "body", 0, 1, -4.5f, 7, 6, 6, 0, 0, -3, blk)
                .part("jaw", "head", 0, -2.5f, -6, 5, 1.5f, 2, 0, 0, 0, blk)
                .part("mandL", "jaw", -2, 0, -1.5f, 1, 1, 2, 0, 0, -0.5f, steel).part("mandR", "jaw", 2, 0, -1.5f, 1, 1, 2, 0, 0, -0.5f, steel)
                .part("eyeL", "head", -2.6f, 1, -4.5f, 2.5f, 3, 3, 0, 0, 0, red).glow()
                .part("eyeR", "head", 2.6f, 1, -4.5f, 2.5f, 3, 3, 0, 0, 0, red).glow()
                .part("antL", "head", -1.5f, 3, -4, 0.5f, 4, 0.5f, 0, 2, 0, blk).sway(0.3f, 0.4f, 0, 2)
                .part("antR", "head", 1.5f, 3, -4, 0.5f, 4, 0.5f, 0, 2, 0, blk).sway(0.3f, 0.4f, P, 2)
                .part("keyShaft", "body", 0, 3.5f, 0, 1, 2, 1, 0, 1, 0, steel)
                .part("wingL", "body", -3, 3.5f, -1, 10, 0.4f, 5, -5, 0, 0, wing).sway(0.7f, 2.5f, 0, 2)
                .part("wingR", "body", 3, 3.5f, -1, 10, 0.4f, 5, 5, 0, 0, wing).sway(0.7f, 2.5f, P, 2)
                .part("armL", "body", -3, -3.5f, -2, 1, 5, 1, 0, -2.5f, 0, blk).sway(0.2f, 0.3f, 0, 0)
                .part("armR", "body", 3, -3.5f, -2, 1, 5, 1, 0, -2.5f, 0, blk).sway(0.2f, 0.3f, P, 0)
                .part("legL", "body", -3, -3.5f, 2, 1, 5, 1, 0, -2.5f, 0, blk).walk(0.6f, 0)
                .part("legR", "body", 3, -3.5f, 2, 1, 5, 1, 0, -2.5f, 0, blk).walk(0.6f, P);
        // Signature: curl-under sting – abdomen tucks forward and drives the drill stinger in (poison).
        d.custom(4, 2.6f, 0, 35, 10, 4, 10, (m, s, t) -> {
            if (t == s.wind() + 1) { snd(m, SoundEvents.BEE_STING, 1.2f, 0.8f);
                for (LivingEntity e : Fx.arc(m, s.reach(), 0.4)) { Fx.hit(m, e, s.dmg(), 0.3f, Fx.flat(m)); effect(e, MobEffects.POISON, 80, 1); } }
        }, (p, e, w, s, r, age) -> {
            p.rot("tail", 0.4f, 0, 0, 2.0f, 0, 0, w, s, r); p.rot("body", -0.3f, 0, 0, 0.4f, 0, 0, w, s, r);
            p.pos("body", 0, 2, 2, 0, -1, -4, w, s, r); p.scl("stinger", 1, 1, 1, 1, 1, 1.6f, w, s, r);
        });
        d.atk(Atk.SHOOT, 3, 14, 3, 50, 0x80ff40, "poison").anim((p, e, w, s, r, age) -> {
            p.rot("tail", 0.8f, 0, 0, 1.6f, 0, 0, w, s, r); p.rot("body", -0.4f, 0, 0, 0.2f, 0, 0, w, s, r);
        });
        d.atk(Atk.POUNCE, 4, 8, 3, 70);
        d.atk(Atk.BITE, 3, 2, 0, 20).anim((p, e, w, s, r, age) -> {
            p.rot("mandL", 0, 0.8f, 0, 0, -0.3f, 0, w, s, r); p.rot("mandR", 0, -0.8f, 0, 0, 0.3f, 0, w, s, r);
            p.rot("head", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -4, w, s, r);
        });
        return d;
    }

    // =====================================================================
    // 10. Cuddle Clump – sewn-together playhouse mascot plush; patchwork, mismatched button eyes, stitched grin.
    static CreatureDef cuddleClump() {
        int fur = 0xa070d0, patch = 0x60c0a0, patch2 = 0xf0a040, stitch = 0x201020, btn = 0x202020, btn2 = 0xe04040, belly = 0xf0e0f0;
        CreatureDef d = CreatureDef.of("huggy_wuggy_plush", "Huggy Wuggy Plush", "ハギーワギー・プラッシュ").size(1.3f, 2.6f).hp(110).dmg(7).speed(0.29).kbRes(0.5).egg(fur, patch).spawn(1)
                .part("body", null, 0, 14, 0, 18, 18, 14, 0, 9, 0, fur).sway(0.03f, 0.08f, 0, 2)
                .part("belly", "body", 0, 8, -7.2f, 12, 12, 1, 0, 0, 0, belly)
                .part("patchA", "body", -6, 13, -7.3f, 4, 4, 0.5f, 0, 0, 0, patch).part("patchB", "body", 7.3f, 6, 2, 0.5f, 5, 5, 0, 0, 0, patch2)
                .part("seam", "belly", 0, 0, -0.6f, 0.6f, 12, 0.4f, 0, 0, 0, stitch)
                .part("seamX1", "seam", 0, 3, 0, 2, 0.5f, 0.4f, 0, 0, 0, stitch).part("seamX2", "seam", 0, -1, 0, 2, 0.5f, 0.4f, 0, 0, 0, stitch).part("seamX3", "seam", 0, -5, 0, 2, 0.5f, 0.4f, 0, 0, 0, stitch)
                .part("stuffing", "body", 5, 2, -7.4f, 3, 2, 1, 0, 0, 0, 0xf8f8f8)
                .part("head", "body", 0, 18, 0, 16, 13, 13, 0, 6.5f, 0, fur)
                .part("earL", "head", -6, 12, 0, 4, 7, 3, 0, 3, 0, patch).sway(0.15f, 0.12f, 0, 2)
                .part("earR", "head", 6, 12, 0, 4, 6, 3, 0, 2, 0, fur).sway(0.15f, 0.12f, P, 0)
                .part("btnEyeL", "head", -4, 8, -6.8f, 4, 4, 1, 0, 0, 0, btn).part("holeL", "btnEyeL", 0, 0, -0.6f, 1, 1, 0.3f, 0, 0, 0, 0x808080)
                .part("btnEyeR", "head", 4, 8, -6.8f, 3, 3, 1, 0, 0, 0, btn2).part("holeR", "btnEyeR", 0, 0, -0.6f, 0.8f, 0.8f, 0.3f, 0, 0, 0, 0x400000)
                .part("nose", "head", 0, 5, -7, 3, 2, 1.5f, 0, 0, 0, 0xff80a0)
                .part("jaw", "head", 0, 2.5f, -6.6f, 12, 3, 1, 0, -1, 0, 0x300818);
        teeth(d, "jaw", "stitchT", 1, -0.6f, 6, 0.6f, 3, 2, 0xf0f0f0);
        d.part("armL", "body", -10.5f, 15, 0, 5, 14, 5, 0, -7, 0, fur).sway(0.06f, 0.1f, 0, 0)
                .part("pawL", "armL", 0, -14, 0, 6, 4, 6, 0, -1, 0, belly)
                .part("armR", "body", 10.5f, 15, 0, 5, 14, 5, 0, -7, 0, patch2).sway(0.06f, 0.1f, P, 0)
                .part("pawR", "armR", 0, -14, 0, 6, 4, 6, 0, -1, 0, belly)
                .part("legL", null, -5, 14, 0, 6, 14, 6, 0, -7, 0, fur).walk(0.8f, 0)
                .part("legR", null, 5, 14, 0, 6, 14, 6, 0, -7, 0, fur).walk(0.8f, P);
        // Signature: "hug too tight" – arms open wide, then crush.
        d.atk(Atk.GRAB, 6, 3, 0, 140, 50, "").anim((p, e, w, s, r, age) -> {
            boolean hold = s >= 1 && r <= 0; float q = hold ? Mth.sin(age * 0.7f) * 0.1f : 0;
            p.rot("armL", 1.4f, 0, -1.4f, 1.5f, 0.6f, 0.3f, w, s, r); p.rot("armR", 1.4f, 0, 1.4f, 1.5f, -0.6f, -0.3f, w, s, r);
            p.add("armL", 0, 0, q); p.add("armR", 0, 0, -q);
            p.rot("body", -0.15f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("head", -0.3f, 0, 0, 0.2f, 0, 0, w, s, r);
            p.rot("jaw", 0, 0, 0, 0.3f, 0, 0, w, s, r); p.scl("body", 1, 1, 1, 1.05f, 0.95f, 1.05f, w, s, r);
        });
        d.atk(Atk.SWIPE, 7, 3.4f, 0, 30);
        // Stuffing burst – tears its seam, spraying fluff (blindness cloud).
        d.custom(2, 6, 0, 240, 14, 10, 12, (m, s, t) -> {
            if (t == s.wind() + 1) { snd(m, SoundEvents.WOOL_BREAK, 2f, 0.6f);
                for (LivingEntity e : Fx.foes(m, m.position(), s.reach())) { Fx.hit(m, e, s.dmg(), 0.6f, e.position().subtract(m.position())); effect(e, MobEffects.BLINDNESS, 50, 0); } }
            if (t > s.wind() && t <= s.wind() + s.strike()) puff(m, ParticleTypes.WHITE_ASH, m.position().add(0, 1.4, 0), 25, 1.2);
        }, (p, e, w, s, r, age) -> {
            p.scl("body", 1, 1, 1, 1.25f, 1.1f, 1.25f, w, s, r); p.scl("stuffing", 1, 1, 1, 4, 4, 2, w, s, r);
            p.rot("armL", 0, 0, -0.4f, 0, 0, -1.4f, w, s, r); p.rot("armR", 0, 0, 0.4f, 0, 0, 1.4f, w, s, r);
            p.rot("head", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r);
        });
        d.atk(Atk.SUMMON, 0, 14, 2, 500, 3, "mini_plush");
        return d;
    }

    // 11. Plush Imp – tiny lost-toy minion (summoned by Cuddle Clump / Hollow Hattie).
    static CreatureDef plushImp() {
        int fur = 0x60c0a0, inner = 0xf0e0f0, stitch = 0x201020;
        CreatureDef d = CreatureDef.of("mini_plush", "Mini Plush", "ミニ・プラッシュ").size(0.5f, 0.75f).hp(14).dmg(3).speed(0.38).egg(fur, inner).spawn(2)
                .part("body", null, 0, 4, 0, 6, 5, 5, 0, 2.5f, 0, fur)
                .part("seam", "body", 0, 2.5f, -2.6f, 0.4f, 4, 0.3f, 0, 0, 0, stitch)
                .part("head", "body", 0, 5, 0, 7, 6, 6, 0, 3, 0, fur)
                .part("earL", "head", -2.5f, 6, 0, 2, 3, 1, 0, 1.5f, 0, inner).sway(0.2f, 0.3f, 0, 2)
                .part("earR", "head", 2.5f, 6, 0, 2, 3, 1, 0, 1.5f, 0, inner).sway(0.2f, 0.3f, P, 2)
                .part("jaw", "head", 0, 1, -3.1f, 4, 1, 0.5f, 0, 0, 0, 0x300818);
        eyes(d, "head", "", 1.6f, 3.5f, -3.1f, 1.5f, 0x101010, 0xffffff, false);
        teeth(d, "jaw", "t", 0, -0.4f, 3, 0.6f, 0.8f, 1.2f, 0xffffff);
        d.part("armL", "body", -3.5f, 4, 0, 1.5f, 3, 1.5f, 0, -1.5f, 0, fur).sway(0.2f, 0.3f, 0, 0)
                .part("armR", "body", 3.5f, 4, 0, 1.5f, 3, 1.5f, 0, -1.5f, 0, fur).sway(0.2f, 0.3f, P, 0)
                .part("legL", null, -1.5f, 2, 0, 2, 2, 2, 0, -1, 0, fur).walk(1f, 0)
                .part("legR", null, 1.5f, 2, 0, 2, 2, 2, 0, -1, 0, fur).walk(1f, P);
        d.atk(Atk.BITE, 3, 1.6f, 0, 20).atk(Atk.POUNCE, 3, 6, 2, 60);
        return d;
    }

    // =====================================================================
    // 12. Static Sleeper – nightmare-era shadow plush: frayed black body, TV-static face, long dragging arms.
    static CreatureDef staticSleeper() {
        int shadow = 0x1a1420, fray = 0x2e2436, screen = 0xb0b8c0, glowc = 0x9060ff, mouth = 0x000000;
        CreatureDef d = CreatureDef.of("nightmare_ollie", "Nightmare Ollie", "ナイトメア・オリー").size(1.1f, 3.0f).hp(130).dmg(8).speed(0.3).kbRes(0.4).egg(shadow, glowc).spawn(1)
                .part("body", null, 0, 26, 0, 12, 18, 8, 0, 9, 0, shadow).sway(0.05f, 0.06f, 0, 2)
                .part("ribL", "body", -3, 10, -4.2f, 4, 1, 0.5f, 0, 0, 0, fray).part("ribR", "body", 3, 10, -4.2f, 4, 1, 0.5f, 0, 0, 0, fray)
                .part("ribL2", "body", -3, 7, -4.2f, 4, 1, 0.5f, 0, 0, 0, fray).part("ribR2", "body", 3, 7, -4.2f, 4, 1, 0.5f, 0, 0, 0, fray)
                .part("tatter1", "body", -4, 0, 0, 3, 5, 8, 0, -2.5f, 0, fray).sway(0.15f, 0.1f, 0, 0)
                .part("tatter2", "body", 4, 0, 0, 3, 7, 8, 0, -3.5f, 0, fray).sway(0.15f, 0.1f, 2, 0)
                .part("head", "body", 0, 18, 0, 12, 11, 10, 0, 5.5f, 0, shadow).sway(0.1f, 0.05f, 0, 2)
                .part("screen", "head", 0, 6, -5.1f, 9, 6, 0.5f, 0, 0, 0, screen).glow()
                .part("scan1", "screen", 0, 1.5f, -0.3f, 9, 0.6f, 0.2f, 0, 0, 0, 0x606870).glow()
                .part("scan2", "screen", 0, -1, -0.3f, 9, 0.6f, 0.2f, 0, 0, 0, 0xe0e8f0).glow()
                .part("eyeL", "screen", -2.2f, 0.5f, -0.4f, 1.6f, 1.6f, 0.2f, 0, 0, 0, mouth)
                .part("eyeR", "screen", 2.2f, 0.5f, -0.4f, 1.6f, 1.6f, 0.2f, 0, 0, 0, mouth)
                .part("jaw", "head", 0, 1.5f, -5, 8, 3, 1, 0, -1, 0, mouth);
        teeth(d, "jaw", "fang", 0.8f, -0.6f, 5, 0.8f, 2.4f, 1.6f, glowc);
        d.part("hornL", "head", -4, 11, 0, 1.5f, 5, 1.5f, 0, 2.5f, 0, fray).part("hornR", "head", 4, 11, 0, 1.5f, 5, 1.5f, 0, 2.5f, 0, fray)
                .part("armL", "body", -7.5f, 17, 0, 3, 28, 3, 0, -14, 0, shadow).sway(0.06f, 0.07f, 0, 0)
                .part("clawL", "armL", 0, -28, -1, 4, 4, 5, 0, -2, -1, glowc).glow()
                .part("armR", "body", 7.5f, 17, 0, 3, 28, 3, 0, -14, 0, shadow).sway(0.06f, 0.07f, P, 0)
                .part("clawR", "armR", 0, -28, -1, 4, 4, 5, 0, -2, -1, glowc).glow()
                .part("legL", null, -3, 26, 0, 4, 26, 4, 0, -13, 0, shadow).walk(0.7f, 0)
                .part("legR", null, 3, 26, 0, 4, 26, 4, 0, -13, 0, shadow).walk(0.7f, P);
        // Signature: static lurch – flickers forward (short teleport) and rakes both claws.
        d.custom(9, 6, 0, 100, 14, 4, 12, (m, s, t) -> {
            if (t == 2) snd(m, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 0.3f);
            if (t == s.wind() && m.getTarget() != null) {
                Vec3 to = m.getTarget().position().subtract(m.position()); Vec3 dst = m.position().add(to.scale(Math.max(0, 1 - 1.4 / Math.max(1.4, to.length()))));
                puff(m, ParticleTypes.SQUID_INK, m.position().add(0, 1.5, 0), 20, 0.4); m.teleportTo(dst.x, m.getY(), dst.z); puff(m, ParticleTypes.SQUID_INK, dst.add(0, 1.5, 0), 20, 0.4);
            }
            if (t == s.wind() + 1) { snd(m, SoundEvents.PHANTOM_BITE, 1.5f, 0.6f);
                for (LivingEntity e : Fx.arc(m, 3, 0.2)) { Fx.hit(m, e, s.dmg(), 0.8f, Fx.flat(m)); effect(e, MobEffects.DARKNESS, 80, 0); } }
        }, (p, e, w, s, r, age) -> {
            float flick = w > 0.6f && w < 1 ? (Mth.sin(age * 9) > 0 ? 1 : 0) : 0;
            p.add("head", 0, flick * 0.4f, flick * 0.3f);
            p.rot("armL", 2.6f, 0, -0.7f, 0.4f, 0, 0.3f, w, s, r); p.rot("armR", 2.6f, 0, 0.7f, 0.4f, 0, -0.3f, w, s, r);
            p.rot("body", 0.3f, 0, 0, -0.7f, 0, 0, w, s, r); p.rot("jaw", 0.2f, 0, 0, 0.9f, 0, 0, w, s, r);
            p.pos("body", 0, 0, 3, 0, -3, -6, w, s, r);
        });
        d.atk(Atk.WHIP, 7, 9, 2.5f, 70).anim((p, e, w, s, r, age) -> {
            p.rot("armR", -0.6f, 0, 0.8f, 1.6f, 0, -0.1f, w, s, r); p.scl("armR", 1, 1, 1, 1, 1.8f, 1, w, s, r);
            p.rot("body", 0.1f, -0.6f, 0, -0.3f, 0.4f, 0, w, s, r);
        });
        d.atk(Atk.SWIPE, 8, 3.8f, 0, 30);
        // Nightmare fog – darkness cloud.
        d.atk(Atk.GAS, 2, 6, 0, 260, 0, "").anim((p, e, w, s, r, age) -> {
            p.rot("head", 0.6f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("jaw", 0.2f, 0, 0, 1.2f, 0, 0, w, s, r);
            p.rot("armL", 0, 0, -0.4f, 0, 0, -1.3f, w, s, r); p.rot("armR", 0, 0, 0.4f, 0, 0, 1.3f, w, s, r);
        });
        return d;
    }

    // =====================================================================
    // 13. Joystick Juggernaut – arcade-cabinet brute: CRT face, joystick on its head, button knuckles.
    static CreatureDef joystickJuggernaut() {
        int cab = 0x2a2a6a, side = 0xe040a0, crt = 0x103020, pix = 0x40ff60, btnR = 0xff3030, btnB = 0x3080ff, metal = 0x909090;
        CreatureDef d = CreatureDef.of("giant_bron", "Giant Bron", "ジャイアント・ブロン").size(1.6f, 3.0f).hp(320).dmg(11).speed(0.26).kbRes(0.95).armor(6).egg(cab, pix).boss()
                .part("body", null, 0, 18, 0, 20, 20, 14, 0, 10, 0, cab).sway(0.02f, 0.08f, 0, 2)
                .part("sideL", "body", -10.2f, 10, 0, 0.5f, 20, 14, 0, 0, 0, side).part("sideR", "body", 10.2f, 10, 0, 0.5f, 20, 14, 0, 0, 0, side)
                .part("coinSlot", "body", 0, 4, -7.2f, 4, 3, 0.5f, 0, 0, 0, metal).part("coinLight", "coinSlot", 0, 0, -0.3f, 2, 0.6f, 0.3f, 0, 0, 0, 0xffa020).glow()
                .part("head", "body", 0, 20, 0, 18, 12, 14, 0, 6, 0, cab)
                .part("screen", "head", 0, 6.5f, -7.1f, 14, 9, 0.5f, 0, 0, 0, crt).glow()
                .part("eyeL", "screen", -3.5f, 1.5f, -0.3f, 3, 3, 0.2f, 0, 0, 0, pix).glow()
                .part("eyeR", "screen", 3.5f, 1.5f, -0.3f, 3, 3, 0.2f, 0, 0, 0, pix).glow()
                .part("jaw", "head", 0, 1, -7, 16, 3, 3, 0, -1, -0.5f, 0x101010);
        teeth(d, "screen", "pixTooth", -2.6f, -0.3f, 6, 1.4f, 1.4f, 2, pix);
        teeth(d, "jaw", "lowT", 0.8f, -1.8f, 6, 1.5f, 1.5f, 2.4f, 0xe0e0e0);
        d.part("marquee", "head", 0, 12, -2, 18, 3, 6, 0, 1.5f, 0, side).part("marqueeLit", "marquee", 0, 1.5f, -3.1f, 14, 1.5f, 0.3f, 0, 0, 0, 0xfff080).glow()
                .part("stick", "head", 0, 15, 0, 1.4f, 6, 1.4f, 0, 3, 0, metal).sway(0.3f, 0.2f, 0, 2)
                .part("stickBall", "stick", 0, 6, 0, 3, 3, 3, 0, 0, 0, btnR)
                .part("armL", "body", -13, 18, 0, 6, 22, 6, 0, -11, 0, cab).sway(0.04f, 0.1f, 0, 0)
                .part("fistL", "armL", 0, -22, 0, 9, 8, 9, 0, -3, 0, metal)
                .part("btnL1", "fistL", -2, -2, -4.7f, 2, 2, 0.6f, 0, 0, 0, btnR).glow().part("btnL2", "fistL", 2, -2, -4.7f, 2, 2, 0.6f, 0, 0, 0, btnB).glow()
                .part("armR", "body", 13, 18, 0, 6, 22, 6, 0, -11, 0, cab).sway(0.04f, 0.1f, P, 0)
                .part("fistR", "armR", 0, -22, 0, 9, 8, 9, 0, -3, 0, metal)
                .part("btnR1", "fistR", -2, -2, -4.7f, 2, 2, 0.6f, 0, 0, 0, btnB).glow().part("btnR2", "fistR", 2, -2, -4.7f, 2, 2, 0.6f, 0, 0, 0, btnR).glow()
                .part("legL", null, -6, 18, 0, 8, 18, 8, 0, -9, 0, cab).walk(0.7f, 0)
                .part("legR", null, 6, 18, 0, 8, 18, 8, 0, -9, 0, cab).walk(0.7f, P);
        // Signature: combo punch – three fast alternating jabs and an uppercut finisher.
        d.custom(4, 3.6f, 0, 70, 10, 20, 12, (m, s, t) -> {
            int h = s.wind() + 1, k = t - h;
            if (k >= 0 && k < s.strike() && k % 6 == 0) {
                boolean fin = k >= 18; snd(m, fin ? SoundEvents.ANVIL_LAND : SoundEvents.PLAYER_ATTACK_STRONG, 1f, fin ? 0.7f : 1.2f);
                for (LivingEntity e : Fx.arc(m, s.reach(), 0.3)) { Fx.hit(m, e, fin ? s.dmg() * 2 : s.dmg(), fin ? 0.6f : 0.2f, Fx.flat(m));
                    if (fin) { e.setDeltaMovement(e.getDeltaMovement().add(0, 0.9, 0)); e.hurtMarked = true; } }
                puff(m, ParticleTypes.CRIT, front(m, 1.8, 1.6), 6, 0.2);
            }
        }, (p, e, w, s, r, age) -> {
            p.rot("armL", 0.8f, 0, -0.2f, 0.8f, 0, -0.2f, w, s, r); p.rot("armR", 0.8f, 0, 0.2f, 0.8f, 0, 0.2f, w, s, r);
            p.rot("body", 0.15f, 0, 0, -0.1f, 0, 0, w, s, r);
            if (s > 0 && r <= 0) {
                int jab = (int) (s * 3.3f); float ph = Mth.sin((s * 3.3f - jab) * P);
                if (jab < 3) { String a = jab % 2 == 0 ? "armR" : "armL"; p.add(a, ph * 0.8f, 0, 0); p.add("body", 0, (jab % 2 == 0 ? 0.3f : -0.3f) * ph, 0); }
                else { p.add("armR", ph * 2.0f, 0, -0.3f); p.add("body", -0.3f * ph, 0, 0); }
            }
        });
        d.atk(Atk.SLAM, 12, 5, 0, 110).anim((p, e, w, s, r, age) -> {
            p.rot("armL", 3.0f, 0, 0.4f, 0.4f, 0, 0.1f, w, s, r); p.rot("armR", 3.0f, 0, -0.4f, 0.4f, 0, -0.1f, w, s, r);
            p.rot("body", 0.4f, 0, 0, -0.7f, 0, 0, w, s, r); p.pos("body", 0, 3, 2, 0, -5, -5, w, s, r);
            p.scl("screen", 1, 1, 1, 1.1f, 0.6f, 1, w, s, r); p.rot("stick", -0.8f, 0, 0, 0.9f, 0, 0, w, s, r);
        });
        // Pixel blaster from the CRT.
        d.atk(Atk.SHOOT, 7, 18, 4, 50, 0x40ff60, "").anim((p, e, w, s, r, age) -> {
            p.rot("head", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r); p.scl("screen", 1, 1, 1, 1.2f, 1.2f, 1, w, s, r); p.rot("jaw", 0, 0, 0, 0.5f, 0, 0, w, s, r);
        });
        // "Game over" triple pixel spread.
        d.custom(6, 16, 5, 160, 18, 6, 12, (m, s, t) -> {
            if (t == 5) snd(m, SoundEvents.NOTE_BLOCK_BIT.value(), 2f, 0.5f);
            if (t == s.wind() + 1) { snd(m, SoundEvents.NOTE_BLOCK_BIT.value(), 2f, 1.5f); for (int i = -1; i <= 1; i++) bolt(m, s.dmg(), 0xff40ff, "blind", 1.3f, i * 0.25f); }
        }, (p, e, w, s, r, age) -> {
            p.rot("armL", 0, 0, -0.3f, 1.5f, -0.5f, 0, w, s, r); p.rot("armR", 0, 0, 0.3f, 1.5f, 0.5f, 0, w, s, r);
            p.rot("body", -0.2f, 0, 0, 0.15f, 0, 0, w, s, r); p.g("stick")[2] += (w < 1 ? Mth.sin(age * 3) * 0.6f : 0);
        });
        d.atk(Atk.CLAP, 8, 3.5f, 0, 90);
        return d;
    }

    private BatchG() {}
}
