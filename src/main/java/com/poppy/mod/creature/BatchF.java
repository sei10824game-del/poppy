package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;
import com.poppy.mod.entity.Fx;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.poppy.mod.creature.FKit.*;

/**
 * Batch F – "Safe Haven" toy-town line-up. Original creatures (own designs) filling the chapter-4 / misc slots:
 * dough golem, wind-up tin croc, knitted moth, bath toad, heart beetle, pool-noodle wyrm, sewing-spider matriarch (boss),
 * candy slug, jelly gumdrop, spinning-top jester, plush night owl. See DEV_GUIDE.md.
 */
public final class BatchF {
    public static List<CreatureDef> all() {
        List<CreatureDef> l = new ArrayList<>();
        l.add(kneadKnight()); l.add(doughBlob()); l.add(clockjaw()); l.add(skeinMoth()); l.add(puddleToad()); l.add(heartshell());
        l.add(noodleWyrm()); l.add(bobbinMatriarch()); l.add(slurpSlug()); l.add(gloopGumdrop()); l.add(topsyJester()); l.add(gloamOwl());
        return l;
    }

    // =====================================================================================================
    static CreatureDef kneadKnight() {
        int dough = 0xe9cf98, light = 0xf3e0b5, crust = 0xb8803f, dark = 0x3b2414, flour = 0xfbf6ea, glint = 0xffa23a, wood = 0x9a6a36, woodD = 0x6e4a24;
        CreatureDef d = CreatureDef.of("doey_the_doughman", "Doey the Doughman", "ドーイ・ザ・ドウマン").size(1.4f, 2.7f).hp(120).dmg(8).speed(0.27).kbRes(0.7).egg(dough, crust).spawn(1)
                .part("legL", null, -5, 12, 0, 8, 12, 9, 0, -6, 0, dough).walk(0.7f, 0)
                .part("footL", "legL", 0, -12, -1, 9, 3, 11, 0, 1.5f, -0.5f, crust)
                .part("legR", null, 5, 12, 0, 8, 12, 9, 0, -6, 0, dough).walk(0.7f, P)
                .part("footR", "legR", 0, -12, -1, 9, 3, 11, 0, 1.5f, -0.5f, crust)
                .part("body", null, 0, 12, 0, 22, 20, 15, 0, 10, 0, dough).sway(0.02f, 0.1f, 0, 2)
                .part("belly", "body", 0, 8, -7.6f, 14, 10, 1, 0, 0, 0, light)
                .part("crust1", "body", 7, 15, -7.4f, 6, 4, 1, 0, 0, 0, crust)
                .part("crust2", "body", -8, 4, -7.4f, 5, 3, 1, 0, 0, 0, crust)
                .part("print", "body", -3, 14, -7.7f, 4, 4, 0.5f, 0, 0, 0, crust)
                .part("lumpL", "body", -11, 14, -2, 5, 6, 7, 0, 0, 0, dough)
                .part("lumpR", "body", 10, 6, 3, 5, 5, 6, 0, 0, 0, dough)
                .part("lumpBack", "body", 0, 12, 7.5f, 12, 8, 2, 0, 0, 0, light)
                .part("drip1", "body", -5, 0, -7, 2, 4, 2, 0, -2, 0, light).sway(0.12f, 0.15f, 0, 0)
                .part("drip2", "body", 6, 1, -7, 2, 3, 2, 0, -1.5f, 0, light).sway(0.12f, 0.15f, 1.5f, 0)
                .part("head", "body", 0, 20, 0, 16, 13, 14, 0, 6.5f, 0, light)
                .part("flour", "head", 0, 13, 0, 15, 2, 13, 0, 1, 0, flour)
                .part("flourDrip", "head", -5, 12, -7.2f, 3, 3, 1, 0, 0, 0, flour)
                .part("browL", "head", -4, 9.5f, -7.3f, 6, 2, 1, 0, 0, 0, crust)
                .part("browR", "head", 4, 9.5f, -7.3f, 6, 2, 1, 0, 0, 0, crust)
                .part("eyeL", "head", -4, 7, -7.2f, 4, 4, 1, 0, 0, 0, dark)
                .part("glintL", "eyeL", 0, 0, -0.6f, 1.5f, 1.5f, 0.5f, 0, 0, 0, glint).glow()
                .part("eyeR", "head", 4, 7, -7.2f, 4, 4, 1, 0, 0, 0, dark)
                .part("glintR", "eyeR", 0, 0, -0.6f, 1.5f, 1.5f, 0.5f, 0, 0, 0, glint).glow()
                .part("nose", "head", 0, 5, -7.6f, 3, 3, 2, 0, 0, 0, dough)
                .part("jaw", "head", 0, 3, -5, 12, 4, 4, 0, -1.5f, -2.5f, dark);
        row(d, "tooth", "head", 4, -4.5f, 4.5f, 3, -7.4f, 1.8f, 2, 1, -1, flour);
        return d
                .part("armL", "body", -13, 18, 0, 7, 20, 7, 0, -9, 0, dough).sway(0.04f, 0.1f, 0, 2)
                .part("fistL", "armL", 0, -19, 0, 10, 9, 10, 0, -3.5f, 0, light)
                .part("knuckleL", "fistL", 0, -4, -5, 8, 2, 1, 0, 0, 0, crust)
                .part("armR", "body", 13, 18, 0, 7, 20, 7, 0, -9, 0, dough).sway(0.04f, 0.1f, P, 2)
                .part("fistR", "armR", 0, -19, 0, 10, 9, 10, 0, -3.5f, 0, light)
                .part("knuckleR", "fistR", 0, -4, -5, 8, 2, 1, 0, 0, 0, crust)
                .part("pin", "fistR", 0, -4, -5, 3, 3, 18, 0, 0, -5, wood)
                .part("pinA", "pin", 0, 0, -14, 2, 2, 4, 0, 0, -2, woodD)
                .part("pinB", "pin", 0, 0, 4, 2, 2, 4, 0, 0, 2, woodD)
                // rolling-pin backhand: coil the pin behind the shoulder, whip it across, body follows through
                .atk(Atk.SWIPE, 8, 3.8f, 0, 30).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.7f, -0.9f, 0.5f, 1.1f, 1.3f, -0.2f, w, s, r); p.rot("armL", 0.3f, 0, -0.5f, -0.4f, 0, -0.2f, w, s, r);
                    p.rot("body", 0.15f, -0.6f, 0, -0.25f, 0.75f, 0, w, s, r); p.rot("head", 0.1f, 0.4f, 0, -0.1f, -0.4f, 0, w, s, r);
                    p.rot("legR", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("legL", 0.3f, 0, 0, -0.25f, 0, 0, w, s, r);
                    p.scl("body", 1, 1.05f, 1, 1.07f, 0.94f, 1.07f, w, s, r);
                })
                // double-fist knead slam: dough stretches tall, then squashes flat on impact
                .atk(Atk.SLAM, 11, 4.5f, 0, 90).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 3.0f, 0, 0.25f, 0.6f, 0, 0.05f, w, s, r); p.rot("armR", 3.0f, 0, -0.25f, 0.6f, 0, -0.05f, w, s, r);
                    p.rot("body", 0.3f, 0, 0, -0.8f, 0, 0, w, s, r); p.pos("body", 0, 2, 3, 0, -5, -6, w, s, r);
                    p.scl("body", 0.92f, 1.15f, 0.92f, 1.2f, 0.8f, 1.2f, w, s, r); p.rot("jaw", -0.6f, 0, 0, 0.05f, 0, 0, w, s, r);
                    p.rot("head", 0.35f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("legL", 0.2f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("legR", 0.2f, 0, 0, -0.35f, 0, 0, w, s, r);
                })
                // engulf: body spreads wide, arms fold inward to knead the victim into the belly
                .atk(Atk.GRAB, 5, 3f, 0, 160, 50, "").anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 0.6f, 0, -1.3f, 1.5f, 0, 0.55f, w, s, r); p.rot("armR", 0.6f, 0, 1.3f, 1.5f, 0, -0.55f, w, s, r);
                    p.scl("body", 1, 1, 1, 1.25f, 0.95f, 1.3f, w, s, r); p.rot("jaw", -0.9f, 0, 0, -0.3f, 0, 0, w, s, r);
                    p.rot("body", 0.15f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, -1, -4, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(age * 0.6f) * 0.07f; float[] b = p.g("body"); b[6] += q; b[8] += q; b[7] -= q; p.add("armL", q * 2, 0, 0); p.add("armR", -q * 2, 0, 0); }
                })
                // tear a glob from the fist and lob it overhand
                .atk(Atk.SHOOT, 5, 14f, 4f, 60, 0xe9cf98, "slow").anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 3.0f, 0, 0.3f, 0.8f, 0, 0.1f, w, s, r); p.scl("fistR", 1.4f, 1.4f, 1.4f, 0.7f, 0.7f, 0.7f, w, s, r);
                    p.rot("body", 0.25f, 0.3f, 0, -0.3f, -0.2f, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("armL", -0.5f, 0, -0.4f, 0.6f, 0, -0.2f, w, s, r);
                })
                // Leaven Rise: proofs up to a huge size, then bursts outward in a sticky shockwave
                .custom(9, 4.5f, 0, 140, 22, 6, 14, (m, sp, t) -> {
                    if (t % 4 == 0 && t <= sp.wind()) puff(m, m.position().add(0, 1.5, 0), dust(0xe9cf98, 1.4f), 4, 0.8, 0.01);
                    if (t == sp.wind() + 1) {
                        m.playSound(SoundEvents.SLIME_SQUISH, 2f, 0.4f); ring(m, 3.5, dust(0xf3e0b5, 2f)); ring(m, 2, ParticleTypes.CLOUD);
                        burst(m, sp, 4.5, 1.3f, e -> e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2)));
                    }
                }, (p, e, w, s, r, age) -> {
                    float sw = 1 + 0.35f * Pose_e(w);
                    p.scl("body", sw, sw * 0.95f, sw, 0.9f, 1.1f, 0.9f, w, s, r); p.rot("armL", 0.2f, 0, -0.7f, 0.3f, 0, -1.6f, w, s, r); p.rot("armR", 0.2f, 0, 0.7f, 0.3f, 0, 1.6f, w, s, r);
                    p.rot("head", -0.2f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -1.0f, 0, 0, w, s, r);
                    if (w > 0 && s <= 0) p.add("body", 0, 0, Mth.sin(age * 2.2f) * 0.04f * w);
                })
                // tear dough chunks from the belly and fling them to the floor
                .atk(Atk.SUMMON, 0, 10f, 0, 400, 2, "mini_doey").anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 0.9f, 0, 0.6f, 1.4f, 0, -1.4f, w, s, r); p.rot("armR", 0.9f, 0, -0.6f, 1.4f, 0, 1.4f, w, s, r);
                    p.rot("body", -0.35f, 0, 0, 0.15f, 0, 0, w, s, r); p.scl("belly", 1, 1, 1, 0.6f, 0.6f, 1, w, s, r); p.rot("jaw", -0.3f, 0, 0, -0.8f, 0, 0, w, s, r);
                });
    }

    static float Pose_e(float x) { return com.poppy.mod.client.Pose.ease(x); }

    static CreatureDef doughBlob() {
        int dough = 0xe9cf98, light = 0xf3e0b5, crust = 0xb8803f, dark = 0x3b2414, glint = 0xffa23a;
        CreatureDef d = CreatureDef.of("mini_doey", "Mini Doey", "ミニ・ドーイ").size(0.6f, 0.65f).hp(18).dmg(3).speed(0.31).egg(light, dark)
                .part("legL", null, -2.5f, 2, 0, 3, 2, 3, 0, -1, 0, crust).walk(1.1f, 0)
                .part("legR", null, 2.5f, 2, 0, 3, 2, 3, 0, -1, 0, crust).walk(1.1f, P)
                .part("body", null, 0, 1, 0, 9, 7, 9, 0, 3.5f, 0, dough).sway(0.05f, 0.25f, 0, 2)
                .part("head", "body", 0, 7, 0, 7, 2, 7, 0, 1, 0, light)
                .part("peak", "head", 0, 2, 0, 3, 1.5f, 3, 0, 0.75f, 0, light).sway(0.2f, 0.2f, 0, 0)
                .part("eyeL", "body", -2, 5, -4.6f, 2, 2, 1, 0, 0, 0, dark)
                .part("glintL", "eyeL", 0, 0, -0.6f, 0.8f, 0.8f, 0.4f, 0, 0, 0, glint).glow()
                .part("eyeR", "body", 2, 5, -4.6f, 2, 2, 1, 0, 0, 0, dark)
                .part("glintR", "eyeR", 0, 0, -0.6f, 0.8f, 0.8f, 0.4f, 0, 0, 0, glint).glow()
                .part("jaw", "body", 0, 2.6f, -4, 6, 2, 2, 0, -0.8f, -0.6f, dark)
                .part("lump1", "body", -4.5f, 2, 1, 2, 3, 3, 0, 0, 0, dough)
                .part("lump2", "body", 4, 4, 2, 2, 2, 3, 0, 0, 0, light)
                .part("armL", "body", -5, 4.5f, 0, 2, 4, 2, 0, -2, 0, dough).sway(0.15f, 0.3f, 0, 2)
                .part("armR", "body", 5, 4.5f, 0, 2, 4, 2, 0, -2, 0, dough).sway(0.15f, 0.3f, P, 2);
        row(d, "tooth", "body", 3, -1.6f, 1.6f, 2.9f, -4.7f, 1, 1, 0.5f, -1, 0xfbf6ea);
        return d
                .atk(Atk.POUNCE, 4, 6, 2, 40).anim((p, e, w, s, r, age) -> {
                    p.scl("body", 1.25f, 0.7f, 1.25f, 0.8f, 1.35f, 0.8f, w, s, r); p.pos("body", 0, -1, 0, 0, 2, -3, w, s, r); p.rot("body", 0.2f, 0, 0, -0.5f, 0, 0, w, s, r);
                    p.rot("jaw", 0, 0, 0, -0.8f, 0, 0, w, s, r);
                })
                .atk(Atk.BITE, 3, 1.8f, 0, 20).anim((p, e, w, s, r, age) -> {
                    p.rot("jaw", -0.9f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("body", 0.3f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, 0, 1, 0, 0, -3, w, s, r);
                    p.scl("body", 0.9f, 1.1f, 0.9f, 1.1f, 0.9f, 1.1f, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef clockjaw() {
        int tin = 0x3c8c4c, tinD = 0x2a5e35, rivet = 0xc9c9bb, brass = 0xd6a62c, belly = 0xdcca8c, eye = 0xff3a2a, tooth = 0xf4f1e6, mouth = 0x5a1212;
        CreatureDef d = CreatureDef.of("allister_gator", "Allister Gator", "アリスター・ゲイター").size(1.5f, 1.1f).hp(90).dmg(7).speed(0.3).kbRes(0.5).armor(4).egg(tin, brass).spawn(1)
                .part("body", null, 0, 11, 0, 14, 9, 24, 0, 0, 2, tin).sway(0.015f, 0.12f, 0, 2)
                .part("belly", "body", 0, -4.6f, 2, 12, 1, 20, 0, 0, 0, belly);
        row(d, "scute", "body", 1, 0, 0, 4.5f, -6, 4, 2, 4, 1, tinD);
        d.part("scute1", "body", 0, 4.5f, 0, 4, 2, 4, 0, 1, 0, tinD).part("scute2", "body", 0, 4.5f, 6, 4, 2, 4, 0, 1, 0, tinD).part("scute3", "body", 0, 4.5f, 11, 4, 2, 3, 0, 1, 0, tinD)
                .part("rivetL0", "body", -7.2f, 1, -6, 1, 1, 1, 0, 0, 0, rivet).part("rivetL1", "body", -7.2f, 1, 8, 1, 1, 1, 0, 0, 0, rivet)
                .part("rivetR0", "body", 7.2f, 1, -6, 1, 1, 1, 0, 0, 0, rivet).part("rivetR1", "body", 7.2f, 1, 8, 1, 1, 1, 0, 0, 0, rivet)
                .part("gear", "body", -7.4f, -0.5f, 3, 1, 5, 5, 0, 0, 0, brass).sway(0.6f, 0.35f, 0, 0)
                .part("keyStem", "body", 0, 4.5f, 3, 2, 5, 2, 0, 2.5f, 0, brass).sway(0.7f, 0.3f, 0, 1)
                .part("keyBow", "keyStem", 0, 5, 0, 8, 5, 1.5f, 0, 2.5f, 0, brass)
                .part("head", "body", 0, 1, -10, 12, 7, 9, 0, 0.5f, -4.5f, tin)
                .part("eyeBumpL", "head", -4, 4, -3, 4, 3, 4, 0, 1, 0, tinD)
                .part("eyeL", "eyeBumpL", 0, 1, -2.1f, 2.5f, 2, 0.6f, 0, 0, 0, eye).glow()
                .part("eyeBumpR", "head", 4, 4, -3, 4, 3, 4, 0, 1, 0, tinD)
                .part("eyeR", "eyeBumpR", 0, 1, -2.1f, 2.5f, 2, 0.6f, 0, 0, 0, eye).glow()
                .part("snout", "head", 0, 0, -9, 10, 4, 12, 0, 1, -6, tin)
                .part("nostrilL", "snout", -2, 3.1f, -10, 1.5f, 0.5f, 1.5f, 0, 0, 0, 0x111111)
                .part("nostrilR", "snout", 2, 3.1f, -10, 1.5f, 0.5f, 1.5f, 0, 0, 0, 0x111111);
        for (int i = 0; i < 3; i++) { d.part("tuL" + i, "snout", -4.3f, -1, -2.5f - i * 3.5f, 1, 2, 1, 0, -1, 0, tooth); d.part("tuR" + i, "snout", 4.3f, -1, -2.5f - i * 3.5f, 1, 2, 1, 0, -1, 0, tooth); }
        d.part("jaw", "head", 0, -2.5f, -6, 10, 3, 16, 0, -1.5f, -7, tinD)
                .part("jawInner", "jaw", 0, 0.1f, -7, 8, 0.4f, 13, 0, 0, 0, mouth);
        for (int i = 0; i < 2; i++) { d.part("tdL" + i, "jaw", -4, 0, -6 - i * 5, 1, 2, 1, 0, 1, 0, tooth); d.part("tdR" + i, "jaw", 4, 0, -6 - i * 5, 1, 2, 1, 0, 1, 0, tooth); }
        return d
                .part("legL", "body", -7, -3, -7, 4, 8, 4, -0.5f, -3.5f, 0, tinD).walk(0.8f, 0)
                .part("legR", "body", 7, -3, -7, 4, 8, 4, 0.5f, -3.5f, 0, tinD).walk(0.8f, P)
                .part("legL2", "body", -7, -3, 9, 4, 8, 4, -0.5f, -3.5f, 0, tinD).walk(0.8f, P)
                .part("legR2", "body", 7, -3, 9, 4, 8, 4, 0.5f, -3.5f, 0, tinD).walk(0.8f, 0)
                .part("tail", "body", 0, 1, 14, 8, 6, 12, 0, -0.5f, 6, tin).sway(0.25f, 0.15f, 0, 1)
                .part("tail2", "tail", 0, 0, 12, 5, 4, 11, 0, -0.5f, 5.5f, tinD).sway(0.3f, 0.15f, 0.8f, 1)
                .part("tailFin", "tail2", 0, 1.5f, 6, 1, 3, 8, 0, 1, 0, brass)
                // snap: snout rears up gaping, then the tin jaw slams shut with a lunge
                .atk(Atk.BITE, 8, 2.8f, 0, 26).anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.55f, 0, 0, -0.15f, 0, 0, w, s, r); p.rot("jaw", -0.95f, 0, 0, 0.05f, 0, 0, w, s, r);
                    p.pos("body", 0, 0, 2, 0, -1, -5, w, s, r); p.rot("body", 0.08f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("tail", 0, 0.4f, 0, 0, -0.5f, 0, w, s, r);
                })
                // death roll: clamp on, then roll the whole tin body over twice while holding the prey
                .custom(4, 2.8f, 0, 180, 10, 30, 12, (m, sp, t) -> {
                    hold(m, sp, t, 2.8, 6, 1f, 0.9, g -> puff(m, g.position().add(0, 0.5, 0), ParticleTypes.CRIT, 8, 0.4, 0.2));
                    if (t == sp.wind() + 1) m.playSound(SoundEvents.IRON_GOLEM_HURT, 1.5f, 0.6f);
                    if (t > sp.wind() && t % 8 == 0) m.playSound(SoundEvents.CHAIN_PLACE, 1.5f, 0.5f);
                }, (p, e, w, s, r, age) -> {
                    p.rot("jaw", -0.9f, 0, 0, -0.05f, 0, 0, w, s, r); p.rot("head", 0.4f, 0, 0, 0, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, -2, -3, w, s, r);
                    if (s > 0 && r <= 0) { p.add("body", 0, 0, Pose_e(s) * P * 4); p.add("tail", 0, Mth.sin(age * 0.8f) * 0.5f, 0); p.g("keyStem")[1] += age * 0.9f; }
                })
                // wind-up rush: key cranks frantically, then it scuttles in a straight line
                .atk(Atk.CHARGE, 9, 12f, 4f, 160).anim((p, e, w, s, r, age) -> {
                    if (s <= 0 && r <= 0) { p.g("keyStem")[1] += age * 1.6f * w; p.add("body", 0, 0, Mth.sin(age * 1.8f) * 0.06f * w); }
                    p.pos("body", 0, -2, 2, 0, -1, -2, w, s, r); p.rot("head", 0.15f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -0.6f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 2.2f) * 1.0f; p.g("legL")[0] = q; p.g("legR2")[0] = q; p.g("legR")[0] = -q; p.g("legL2")[0] = -q; p.add("tail", 0, Mth.sin(age * 1.1f) * 0.6f, 0); }
                })
                // spring bolt: jaw cranks wide, spring pops, a brass bolt is spat out
                .atk(Atk.SHOOT, 5, 14f, 4f, 70, 0xffd040, "").anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.5f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("jaw", -1.0f, 0, 0, -0.4f, 0, 0, w, s, r);
                    p.scl("keyBow", 1, 1, 1, 1.4f, 1.4f, 1.4f, w, s, r); p.pos("body", 0, 0, 1.5f, 0, 0, -1, w, s, r);
                })
                // tail sweep: pivot on the front legs and scythe the tail all the way round
                .custom(6, 3.5f, 0, 70, 12, 4, 10, (m, sp, t) -> {
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, 0.6f); burst(m, sp, 3.5, 1.2f, null); ring(m, 2.5, ParticleTypes.SWEEP_ATTACK); }
                }, (p, e, w, s, r, age) -> {
                    p.rot("body", 0, 0.6f, 0, 0, -2.6f, 0, w, s, r); p.rot("tail", 0, -0.5f, 0, 0, 0.9f, 0, w, s, r); p.rot("tail2", 0, -0.4f, 0, 0, 0.7f, 0, w, s, r);
                    p.rot("head", 0, -0.3f, 0, 0, 0.3f, 0, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef skeinMoth() {
        int yarn = 0x7d4fb5, yarnD = 0x5a3488, cream = 0xf1e6cc, button = 0x1a1a22, stitch = 0xe8d24a, wing = 0xb07ad8, spot = 0xf2a83a, pink = 0xe87aa8, steel = 0xc8c8d0;
        CreatureDef d = CreatureDef.of("mini_yarnaby", "Mini Yarnaby", "ミニ・ヤーナビー").size(1.0f, 1.9f).hp(60).dmg(6).speed(0.32).egg(yarn, spot).spawn(1)
                .part("legL", null, -2.5f, 12, 0, 3, 12, 3, 0, -6, 0, yarnD).walk(0.8f, 0)
                .part("legR", null, 2.5f, 12, 0, 3, 12, 3, 0, -6, 0, yarnD).walk(0.8f, P)
                .part("body", null, 0, 12, 0, 10, 12, 7, 0, 6, 0, yarn).sway(0.03f, 0.12f, 0, 2);
        for (int i = 0; i < 3; i++) d.part("stripe" + i, "body", 0, 2 + i * 4, 0, 10.4f, 1, 7.4f, 0, 0, 0, cream);
        d.part("collar", "body", 0, 12, 0, 12, 3, 9, 0, 0.5f, 0, cream)
                .part("head", "body", 0, 13, 0, 9, 8, 8, 0, 4, 0, yarn)
                .part("eyeL", "head", -2.5f, 4.5f, -4.2f, 3.5f, 3.5f, 1, 0, 0, 0, button)
                .part("glintL", "eyeL", -0.6f, 0.6f, -0.55f, 0.8f, 0.8f, 0.3f, 0, 0, 0, 0xffffff).glow()
                .part("eyeR", "head", 2.5f, 4.5f, -4.2f, 3.5f, 3.5f, 1, 0, 0, 0, button)
                .part("glintR", "eyeR", -0.6f, 0.6f, -0.55f, 0.8f, 0.8f, 0.3f, 0, 0, 0, 0xffffff).glow()
                .part("jaw", "head", 0, 1.5f, -4.05f, 5, 0.6f, 0.3f, 0, 0, 0, 0x2a1a33);
        row(d, "stitch", "head", 3, -2, 2, 1.5f, -4.15f, 0.6f, 2, 0.4f, 0, stitch);
        return d
                .part("antL", "head", -2, 8, -1, 1, 7, 1, 0, 3.5f, 0, stitch).sway(0.25f, 0.12f, 0, 2)
                .part("antTipL", "antL", 0, 7, 0, 2, 2, 2, 0, 0, 0, pink)
                .part("antR", "head", 2, 8, -1, 1, 7, 1, 0, 3.5f, 0, stitch).sway(0.25f, 0.12f, P, 2)
                .part("antTipR", "antR", 0, 7, 0, 2, 2, 2, 0, 0, 0, pink)
                .part("wingL", "body", -3, 9, 4, 14, 12, 1, -7, 0, 0, wing).sway(0.25f, 0.3f, 0, 1)
                .part("wingSpotL", "wingL", -8, 1, -0.6f, 4, 4, 0.4f, 0, 0, 0, spot)
                .part("wingTrimL", "wingL", -14, 0, 0, 1, 12, 1.2f, 0, 0, 0, yarnD)
                .part("wingL2", "body", -3, 4, 4, 10, 8, 1, -5, -2, 0, wing).sway(0.25f, 0.3f, 0.3f, 1)
                .part("wingR", "body", 3, 9, 4, 14, 12, 1, 7, 0, 0, wing).sway(0.25f, 0.3f, P, 1)
                .part("wingSpotR", "wingR", 8, 1, -0.6f, 4, 4, 0.4f, 0, 0, 0, spot)
                .part("wingTrimR", "wingR", 14, 0, 0, 1, 12, 1.2f, 0, 0, 0, yarnD)
                .part("wingR2", "body", 3, 4, 4, 10, 8, 1, 5, -2, 0, wing).sway(0.25f, 0.3f, P + 0.3f, 1)
                .part("armL", "body", -5, 10, -1, 2, 8, 2, 0, -4, 0, yarnD).sway(0.1f, 0.15f, 0, 0)
                .part("armR", "body", 5, 10, -1, 2, 8, 2, 0, -4, 0, yarnD).sway(0.1f, 0.15f, P, 0)
                .part("needle", "armR", 0, -8, 0, 1, 1, 8, 0, 0, -3, steel)
                .part("armL2", "body", -5, 6, -1, 2, 7, 2, 0, -3.5f, 0, yarnD).sway(0.1f, 0.15f, 1, 0)
                .part("armR2", "body", 5, 6, -1, 2, 7, 2, 0, -3.5f, 0, yarnD).sway(0.1f, 0.15f, 1 + P, 0)
                // thread lash: needle arm cocks high, wings flare, a stretched thread cracks forward
                .atk(Atk.WHIP, 6, 9f, 2f, 60).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.8f, 0.3f, 0.4f, 1.6f, -0.2f, 0, w, s, r); p.scl("armR", 1, 1, 1, 1, 2.6f, 1, w, s, r);
                    p.rot("body", 0.25f, -0.4f, 0, -0.4f, 0.3f, 0, w, s, r); p.rot("wingL", 0, 0.7f, 0, 0, -0.3f, 0, w, s, r); p.rot("wingR", 0, -0.7f, 0, 0, 0.3f, 0, w, s, r);
                    p.rot("antL", -0.4f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("antR", -0.4f, 0, 0, 0.6f, 0, 0, w, s, r);
                })
                // yarn ball: four hands roll a ball in front of the chest, then bowl it
                .atk(Atk.SHOOT, 4, 14f, 3f, 50, 0xb07ad8, "slow").anim((p, e, w, s, r, age) -> {
                    float q = Mth.sin(age * 0.9f) * 0.25f * (s <= 0 ? w : 0);
                    p.rot("armL", 1.2f + q, 0, 0.5f, 1.4f, 0, -0.2f, w, s, r); p.rot("armR", 1.2f - q, 0, -0.5f, 1.8f, 0, 0.1f, w, s, r);
                    p.rot("armL2", 1.0f - q, 0, 0.6f, 0.4f, 0, -0.3f, w, s, r); p.rot("armR2", 1.0f + q, 0, -0.6f, 1.6f, 0, 0, w, s, r);
                    p.rot("body", 0.2f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.1f, 0, 0, w, s, r);
                })
                // cocoon spin: whirls on the spot spooling thread round everything nearby
                .custom(6, 3.5f, 0, 160, 14, 24, 12, (m, sp, t) -> {
                    int hit = sp.wind() + 1;
                    if (t == hit) { m.playSound(SoundEvents.WOOL_PLACE, 2f, 0.6f); for (LivingEntity e : Fx.foes(m, m.position(), 3.5)) m.hitOnce.add(e.getId()); }
                    if (t >= hit && t < hit + sp.strike()) {
                        double a = t * 0.7; puff(m, m.position().add(Math.cos(a) * 1.5, 1 + Math.sin(t * 0.3) * 0.5, Math.sin(a) * 1.5), dust(0xf1e6cc, 1.2f), 3, 0.1, 0);
                        if ((t - hit) % 4 == 0) for (int id : List.copyOf(m.hitOnce)) if (m.level().getEntity(id) instanceof LivingEntity g && g.isAlive()) {
                            Fx.hit(m, g, sp.dmg() * 0.25f, 0, Vec3.ZERO); g.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3)); g.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                            g.setDeltaMovement(m.position().subtract(g.position()).scale(0.12)); g.hurtMarked = true; }
                    }
                }, (p, e, w, s, r, age) -> {
                    p.rot("armL", 0.2f, 0, -1.2f, 0.1f, 0, -1.5f, w, s, r); p.rot("armR", 0.2f, 0, 1.2f, 0.1f, 0, 1.5f, w, s, r);
                    p.rot("wingL", 0, -0.9f, 0, 0, -1.3f, 0, w, s, r); p.rot("wingR", 0, 0.9f, 0, 0, 1.3f, 0, w, s, r); p.rot("body", 0.15f, 0, 0, 0, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) p.add("body", 0, Pose_e(s) * P * 6, 0);
                })
                // dust flutter: rises on beating wings and showers a blinding lint cloud
                .custom(3, 5f, 0, 180, 12, 10, 10, (m, sp, t) -> {
                    int hit = sp.wind() + 1;
                    if (t == hit) { m.playSound(SoundEvents.PHANTOM_FLAP, 2f, 0.8f); cloud(m, front(m, 1.5, 0), 0xd9c4f0, 3f, 100,
                            new MobEffectInstance(MobEffects.BLINDNESS, 60, 0), new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)); }
                    if (t >= hit && t < hit + sp.strike()) for (LivingEntity e : Fx.foes(m, m.position(), 4)) { Vec3 d2 = e.position().subtract(m.position()).normalize().scale(0.15); e.push(d2.x, 0.02, d2.z); e.hurtMarked = true; if (t == hit) Fx.hit(m, e, sp.dmg(), 0, Vec3.ZERO); }
                }, (p, e, w, s, r, age) -> {
                    float f = Mth.sin(age * 2.6f) * 0.9f * (r > 0 ? 1 - r : Math.max(w, s));
                    p.add("wingL", 0, f, 0); p.add("wingR", 0, -f, 0); p.add("wingL2", 0, f * 0.8f, 0); p.add("wingR2", 0, -f * 0.8f, 0);
                    p.pos("body", 0, 2, 0, 0, 5, 0, w, s, r); p.pos("legL", 0, 2, 0, 0, 5, 0, w, s, r); p.pos("legR", 0, 2, 0, 0, 5, 0, w, s, r);
                    p.rot("legL", 0.3f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("legR", 0.3f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("head", 0.2f, 0, 0, -0.3f, 0, 0, w, s, r);
                })
                // needle prick: draw back, stab the knitting needle straight in
                .atk(Atk.SWIPE, 6, 2.5f, 0, 24).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 0.6f, 0, 0.3f, 1.65f, 0, 0, w, s, r); p.pos("armR", 0, 0, 3, 0, 0, -3, w, s, r);
                    p.rot("body", 0.2f, 0.3f, 0, -0.3f, -0.2f, 0, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -3, w, s, r);
                    p.rot("legR", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("legL", 0.3f, 0, 0, -0.4f, 0, 0, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef puddleToad() {
        int rub = 0x36b39a, rubD = 0x23806c, spot = 0xf2d23c, belly = 0xf6eecb, white = 0xfafafa, pupil = 0x111111, mouth = 0x7a1c2c, tongue = 0xff5d7a, foot = 0xf2a93c;
        return CreatureDef.of("icky_licky", "Icky Licky", "イッキー・リッキー").size(1.2f, 1.1f).hp(70).dmg(6).speed(0.28).egg(rub, spot).spawn(2)
                .part("body", null, 0, 8, 0, 16, 9, 16, 0, 3, 1, rub).sway(0.02f, 0.1f, 0, 0)
                .part("belly", "body", 0, 3, -7.2f, 12, 7, 1, 0, 0, 0, belly)
                .part("spot0", "body", -5, 7.6f, 2, 3, 1, 3, 0, 0, 0, spot)
                .part("spot1", "body", 4, 7.6f, 5, 3, 1, 3, 0, 0, 0, spot)
                .part("spot2", "body", 1, 7.6f, -3, 2, 1, 2, 0, 0, 0, spot)
                .part("spot3", "body", -8.1f, 4, 4, 1, 3, 3, 0, 0, 0, spot)
                .part("throat", "body", 0, 0, -7.5f, 10, 4, 3, 0, 0, -1, belly)
                .part("head", "body", 0, 6, -5, 14, 6, 10, 0, 2, -4, rub)
                .part("eyeL", "head", -4.5f, 5, -4, 5, 5, 5, 0, 1.5f, 0, white)
                .part("pupilL", "eyeL", 0, 1.5f, -2.6f, 2.5f, 3, 0.5f, 0, 0, 0, pupil)
                .part("lidL", "eyeL", 0, 4, 0, 5.4f, 1.5f, 5.4f, 0, 0, 0, rubD)
                .part("eyeR", "head", 4.5f, 5, -4, 5, 5, 5, 0, 1.5f, 0, white)
                .part("pupilR", "eyeR", 0, 1.5f, -2.6f, 2.5f, 3, 0.5f, 0, 0, 0, pupil)
                .part("lidR", "eyeR", 0, 4, 0, 5.4f, 1.5f, 5.4f, 0, 0, 0, rubD)
                .part("jaw", "head", 0, 0, -1, 13, 2, 9, 0, -1, -4.5f, rubD)
                .part("mouthIn", "jaw", 0, 0.05f, -4.5f, 11, 0.4f, 7, 0, 0, 0, mouth)
                .part("tongue", "jaw", 0, 0.4f, -2, 3, 1, 6, 0, 0, -3, tongue)
                .part("tongueTip", "tongue", 0, 0, -6, 4, 1.5f, 2, 0, 0, -1, tongue)
                .part("legL", "body", -7, 0, 5, 4, 6, 8, 0, -1, 0, rubD).walk(0.6f, 0)
                .part("footL", "legL", 0, -4, -2, 5, 1, 6, 0, 0, -2, foot)
                .part("legR", "body", 7, 0, 5, 4, 6, 8, 0, -1, 0, rubD).walk(0.6f, P)
                .part("footR", "legR", 0, -4, -2, 5, 1, 6, 0, 0, -2, foot)
                .part("armL", "body", -6, 0, -5, 3, 7, 3, 0, -3, 0, rub).walk(0.6f, P)
                .part("handL", "armL", 0, -7, 0, 4, 1, 4, 0, 0, -0.5f, foot)
                .part("armR", "body", 6, 0, -5, 3, 7, 3, 0, -3, 0, rub).walk(0.6f, 0)
                .part("handR", "armR", 0, -7, 0, 4, 1, 4, 0, 0, -0.5f, foot)
                .part("plug", "body", 6, 8, 6, 3, 2, 3, 0, 1, 0, 0x444444)
                .part("chain", "plug", 0, 2, 0, 1, 4, 1, 0, 2, 0, 0xbbbbbb).sway(0.3f, 0.2f, 0, 2)
                // tongue snare: head tilts up, mouth flies open, tongue shoots out 9x and drags the prey in
                .atk(Atk.WHIP, 6, 8f, 2f, 70).anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.35f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("jaw", -0.4f, 0, 0, -0.55f, 0, 0, w, s, r);
                    p.scl("tongue", 1, 1, 1, 1, 1, 9f, w, s, r); p.pos("body", 0, -1, 1, 0, 0, -2, w, s, r);
                    p.scl("throat", 1.2f, 1.3f, 1.2f, 1, 1, 1, w, s, r);
                })
                // belly flop: crouch deep, spring up stretched, land splayed
                .atk(Atk.POUNCE, 8, 7f, 2f, 80).anim((p, e, w, s, r, age) -> {
                    p.scl("body", 1.15f, 0.8f, 1.1f, 0.9f, 1.15f, 1.1f, w, s, r); p.pos("body", 0, -2.5f, 0, 0, 3, -4, w, s, r);
                    p.rot("body", 0.15f, 0, 0, 0.45f, 0, 0, w, s, r); p.rot("legL", -0.7f, 0, 0, 1.3f, 0, 0, w, s, r); p.rot("legR", -0.7f, 0, 0, 1.3f, 0, 0, w, s, r);
                    p.rot("armL", 0.6f, 0, -0.3f, -0.8f, 0, -0.9f, w, s, r); p.rot("armR", 0.6f, 0, 0.3f, -0.8f, 0, 0.9f, w, s, r);
                })
                // croak quake: throat sac balloons, then a booming croak rings out
                .custom(5, 4.5f, 0, 140, 16, 6, 12, (m, sp, t) -> {
                    if (t == sp.wind() + 1) {
                        m.playSound(SoundEvents.FROG_AMBIENT, 3f, 0.4f); ring(m, 3, ParticleTypes.SPLASH); ring(m, 1.5, ParticleTypes.BUBBLE_POP);
                        burst(m, sp, 4.5, 1.4f, e -> { e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0)); e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)); });
                    }
                }, (p, e, w, s, r, age) -> {
                    p.scl("throat", 2.2f, 2.4f, 2.0f, 1, 1, 1, w, s, r); p.pos("throat", 0, -1, -1.5f, 0, 0, 0, w, s, r);
                    p.rot("head", 0.4f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                    p.scl("body", 1.08f, 1.08f, 1.08f, 1.15f, 0.9f, 1.15f, w, s, r); p.pos("body", 0, 1, 0, 0, -1.5f, 0, w, s, r);
                    if (s > 0 && r <= 0) p.add("body", 0, 0, Mth.sin(age * 3f) * 0.06f);
                })
                .atk(Atk.BITE, 7, 2.2f, 0, 24).anim((p, e, w, s, r, age) -> {
                    p.rot("jaw", -0.8f, 0, 0, 0.05f, 0, 0, w, s, r); p.rot("head", 0.45f, 0, 0, -0.2f, 0, 0, w, s, r);
                    p.pos("body", 0, 0, 2, 0, 0, -5, w, s, r); p.scl("tongue", 1, 1, 1, 1, 1, 2f, w, s, r);
                })
                // bubble spit: cheeks puff, a soapy bubble pops out
                .atk(Atk.SHOOT, 4, 12f, 3f, 55, 0x9fe6ff, "slow").anim((p, e, w, s, r, age) -> {
                    p.scl("head", 1.15f, 1.1f, 1, 0.95f, 0.95f, 1.05f, w, s, r); p.rot("head", 0.2f, 0, 0, 0.35f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.35f, 0, 0, w, s, r);
                    p.scl("throat", 1.5f, 1.6f, 1.5f, 0.9f, 0.9f, 0.9f, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef heartshell() {
        int shell = 0xd8336b, shellD = 0x9c1f4a, heart = 0xffc0d6, under = 0x2b1a24, eye = 0xffe14a, leg = 0x3a2230, horn = 0xf5d0dc, mem = 0xf7e8ff;
        CreatureDef d = CreatureDef.of("mini_kissy", "Mini Kissy", "ミニ・キッシー").size(1.0f, 0.9f).hp(45).dmg(5).speed(0.33).armor(5).egg(shell, heart).spawn(2)
                .part("body", null, 0, 6, 0, 12, 6, 16, 0, 2, 1, under)
                .part("wingL", "body", -1, 4.8f, -5, 7, 0.3f, 14, -3.5f, 0, 7, mem)
                .part("wingR", "body", 1, 4.8f, -5, 7, 0.3f, 14, 3.5f, 0, 7, mem)
                .part("elytraL", "body", -0.1f, 5, -6, 6.5f, 3, 15, -3.25f, 0, 7.5f, shell)
                .part("heartL", "elytraL", -3.25f, 1.6f, 6, 3, 0.4f, 3, 0, 0, 0, heart)
                .part("heartL2", "elytraL", -3.25f, 1.6f, 10.5f, 2, 0.4f, 2, 0, 0, 0, heart)
                .part("ridgeL", "elytraL", -0.4f, 1.6f, 7.5f, 0.6f, 0.4f, 14, 0, 0, 0, shellD)
                .part("elytraR", "body", 0.1f, 5, -6, 6.5f, 3, 15, 3.25f, 0, 7.5f, shell)
                .part("heartR", "elytraR", 3.25f, 1.6f, 6, 3, 0.4f, 3, 0, 0, 0, heart)
                .part("heartR2", "elytraR", 3.25f, 1.6f, 10.5f, 2, 0.4f, 2, 0, 0, 0, heart)
                .part("ridgeR", "elytraR", 0.4f, 1.6f, 7.5f, 0.6f, 0.4f, 14, 0, 0, 0, shellD)
                .part("head", "body", 0, 2, -7, 9, 5, 6, 0, 0.5f, -3, shellD)
                .part("eyeL", "head", -3.6f, 1.5f, -4, 2, 2, 2, 0, 0, 0, eye).glow()
                .part("eyeR", "head", 3.6f, 1.5f, -4, 2, 2, 2, 0, 0, 0, eye).glow()
                .part("horn", "head", 0, 3, -5, 2, 2, 6, 0, 1, -2, horn)
                .part("hornHeart", "horn", 0, 2.5f, -5, 4, 3, 1, 0, 0, 0, heart)
                .part("jaw", "head", 0, -1, -6, 4, 1.2f, 2, 0, 0, -1, under)
                .part("pinchL", "head", -2.5f, -1, -6, 1.5f, 1.5f, 4, 0, 0, -2, horn)
                .part("pinchR", "head", 2.5f, -1, -6, 1.5f, 1.5f, 4, 0, 0, -2, horn)
                .part("antL", "head", -2, 2.5f, -5, 0.6f, 0.6f, 6, 0, 0, -3, 0x111111).sway(0.2f, 0.2f, 0, 1)
                .part("antR", "head", 2, 2.5f, -5, 0.6f, 0.6f, 6, 0, 0, -3, 0x111111).sway(0.2f, 0.2f, P, 1);
        float[] zs = {-4, 1, 6};
        for (int i = 0; i < 3; i++) {
            String sfx = i == 0 ? "" : String.valueOf(i + 1);
            d.part("legL" + sfx, "body", -6, 0, zs[i], 1.5f, 6, 1.5f, -1, -3, 0, leg).walk(0.9f, i % 2 == 0 ? 0 : P);
            d.part("legR" + sfx, "body", 6, 0, zs[i], 1.5f, 6, 1.5f, 1, -3, 0, leg).walk(0.9f, i % 2 == 0 ? P : 0);
        }
        return d
                // horn toss: dip the horn under the foe and flick it skyward
                .custom(6, 2.5f, 0, 60, 10, 4, 12, (m, sp, t) -> {
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.SHIELD_BLOCK, 1.5f, 1.2f);
                        for (LivingEntity e : Fx.arc(m, 2.5, 0.3)) { Fx.hit(m, e, sp.dmg(), 0.5f, Fx.flat(m)); e.setDeltaMovement(e.getDeltaMovement().add(0, 0.85, 0)); e.hurtMarked = true; puff(m, e.position(), ParticleTypes.HEART, 3, 0.3, 0); } }
                }, (p, e, w, s, r, age) -> {
                    p.rot("head", -0.5f, 0, 0, 0.8f, 0, 0, w, s, r); p.rot("body", -0.15f, 0, 0, 0.35f, 0, 0, w, s, r); p.pos("body", 0, -1.5f, 1, 0, 1, -3, w, s, r);
                    p.rot("legL", -0.4f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("legR", -0.4f, 0, 0, 0.5f, 0, 0, w, s, r);
                })
                // swoon dive: shell halves pop open, wings blur, it dives in
                .atk(Atk.POUNCE, 6, 7f, 2f, 70).anim((p, e, w, s, r, age) -> {
                    p.rot("elytraL", 0.4f, 0, -0.9f, 0.5f, 0, -1.1f, w, s, r); p.rot("elytraR", 0.4f, 0, 0.9f, 0.5f, 0, 1.1f, w, s, r);
                    float f = (r > 0 ? 1 - r : Math.max(w, s)) * Mth.sin(age * 3f) * 0.6f;
                    p.rot("wingL", 0.2f, 0.6f, -0.3f, 0.2f, 0.9f, -0.2f, w, s, r); p.rot("wingR", 0.2f, -0.6f, 0.3f, 0.2f, -0.9f, 0.2f, w, s, r);
                    p.add("wingL", 0, 0, f); p.add("wingR", 0, 0, -f); p.rot("body", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 1, 0, 0, 4, -2, w, s, r);
                })
                // heart pulse: shell spreads, body throbs and a lovesick aura dizzies everything around
                .custom(3, 4f, 0, 150, 16, 4, 10, (m, sp, t) -> {
                    if (t < sp.wind() && t % 4 == 0) puff(m, m.position().add(0, 1, 0), ParticleTypes.HEART, 1, 0.4, 0);
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 3f, 0.6f); ring(m, 3, dust(0xff7aa8, 1.6f));
                        burst(m, sp, 4, 0.3f, e -> { e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 140, 0)); e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1)); }); }
                }, (p, e, w, s, r, age) -> {
                    p.rot("elytraL", 0, 0, -0.6f, 0.2f, 0, -0.9f, w, s, r); p.rot("elytraR", 0, 0, 0.6f, 0.2f, 0, 0.9f, w, s, r);
                    float pulse = 1 + Mth.sin(age * 1.4f) * 0.08f * w;
                    p.scl("body", pulse, pulse, pulse, 1.2f, 1.2f, 1.2f, w, s, r); p.scl("hornHeart", 1.3f, 1.3f, 1.3f, 2f, 2f, 2f, w, s, r); p.rot("head", 0.3f, 0, 0, 0.5f, 0, 0, w, s, r);
                })
                // shell ram: elytra clamp shut, head tucks low, six legs blur
                .atk(Atk.CHARGE, 7, 10f, 3f, 120).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.3f, 0, 0, -0.4f, 0, 0, w, s, r); p.pos("body", 0, -1, 1.5f, 0, -1, -1, w, s, r);
                    if (w > 0 && s <= 0) p.add("body", 0, 0, Mth.sin(age * 2f) * 0.05f);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 2.6f) * 1.1f; p.g("legL")[0] = q; p.g("legR2")[0] = q; p.g("legL3")[0] = q; p.g("legR")[0] = -q; p.g("legL2")[0] = -q; p.g("legR3")[0] = -q; }
                })
                // pincer nip: pincers spread, then scissor shut
                .atk(Atk.BITE, 5, 1.8f, 0, 20).anim((p, e, w, s, r, age) -> {
                    p.rot("pinchL", 0, 0.7f, 0, 0, -0.35f, 0, w, s, r); p.rot("pinchR", 0, -0.7f, 0, 0, 0.35f, 0, w, s, r);
                    p.rot("head", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r); p.pos("body", 0, 0, 1, 0, 0, -3, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef noodleWyrm() {
        int lime = 0x8fe03a, mag = 0xe03aa8, cyan = 0x3ac8e0, orange = 0xff9a2e, hole = 0x3a0a2a, white = 0xf4f4f4, yel = 0xffe14a;
        return CreatureDef.of("nightmare_pj", "Nightmare PJ", "ナイトメアPJ").size(1.0f, 1.6f).hp(75).dmg(7).speed(0.3).egg(lime, mag).spawn(1)
                .part("seg0", null, 0, 3, 5, 8, 6, 10, 0, 0, 0, lime).sway(0.2f, 0.15f, 0, 1)
                .part("seg1", "seg0", 0, 0, 6, 7, 6, 8, 0, 0, 4, mag).sway(0.25f, 0.15f, 0.7f, 1)
                .part("seg2", "seg1", 0, 0, 8, 6, 5, 8, 0, 0, 4, cyan).sway(0.3f, 0.15f, 1.4f, 1)
                .part("segTail", "seg2", 0, 0, 8, 5, 4, 6, 0, 0, 3, orange).sway(0.35f, 0.15f, 2.1f, 1)
                .part("rattle", "segTail", 0, 0, 6, 3, 3, 3, 0, 0, 1.5f, yel).sway(0.4f, 0.6f, 0, 2)
                .part("rattle2", "rattle", 0, 0, 3, 2, 2, 2, 0, 0, 1, yel)
                .part("body", null, 0, 3, 0, 9, 14, 9, 0, 7, 0, orange).sway(0.05f, 0.1f, 0, 2)
                .part("ring0", "body", 0, 4, 0, 9.4f, 1.5f, 9.4f, 0, 0, 0, mag)
                .part("ring1", "body", 0, 10, 0, 9.4f, 1.5f, 9.4f, 0, 0, 0, cyan)
                .part("spine1", "body", 0, 12, 4.6f, 1, 3, 3, 0, 0, 0, lime)
                .part("spine2", "body", 0, 6, 4.6f, 1, 3, 3, 0, 0, 0, lime)
                .part("head", "body", 0, 14, -1, 10, 9, 10, 0, 4, -2, mag)
                .part("spine0", "head", 0, 9, 1, 1, 3, 3, 0, 0, 0, lime)
                .part("mouth", "head", 0, 3, -7.1f, 6, 5, 0.5f, 0, 0, 0, hole)
                .part("fangL", "head", -2, 5.5f, -7.3f, 1, 3, 1, 0, -1.5f, 0, white)
                .part("fangR", "head", 2, 5.5f, -7.3f, 1, 3, 1, 0, -1.5f, 0, white)
                .part("eyeL", "head", -3.5f, 7, -7.1f, 2.5f, 2.5f, 0.6f, 0, 0, 0, white)
                .part("pupilL", "eyeL", 0, 0, -0.4f, 1, 2, 0.3f, 0, 0, 0, 0xff2a2a).glow()
                .part("eyeR", "head", 3.5f, 7, -7.1f, 2.5f, 2.5f, 0.6f, 0, 0, 0, white)
                .part("pupilR", "eyeR", 0, 0, -0.4f, 1, 2, 0.3f, 0, 0, 0, 0xff2a2a).glow()
                .part("hoodL", "head", -5, 4, 0, 2, 10, 6, -1, 0, 0, cyan).sway(0.1f, 0.1f, 0, 2)
                .part("hoodR", "head", 5, 4, 0, 2, 10, 6, 1, 0, 0, cyan).sway(0.1f, 0.1f, P, 2)
                .part("jaw", "head", 0, 0, -3, 9, 3, 9, 0, -1.5f, -2, lime)
                .part("tongue", "jaw", 0, 0, -7, 1, 0.4f, 4, 0, 0, -2, 0xff3355).sway(0.3f, 0.5f, 0, 1)
                // noodle lash: rears back coiled, then the foam neck stretches out like a whip
                .atk(Atk.WHIP, 7, 8f, 2f, 60).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.6f, 0, 0, -1.0f, 0, 0, w, s, r); p.scl("body", 1, 0.85f, 1, 0.9f, 1.7f, 0.9f, w, s, r);
                    p.rot("head", 0.4f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -0.6f, 0, 0, w, s, r);
                    p.rot("seg0", 0, 0.4f, 0, 0, -0.3f, 0, w, s, r); p.rot("seg1", 0, -0.5f, 0, 0, 0.4f, 0, w, s, r);
                })
                // coil crush: lunges, wraps its foam body round the prey and squeezes
                .custom(4, 3f, 0, 170, 12, 40, 10, (m, sp, t) -> {
                    hold(m, sp, t, 3, 8, 1f, 0.6, g -> { g.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 3)); m.playSound(SoundEvents.SLIME_SQUISH_SMALL, 1.5f, 0.5f); });
                }, (p, e, w, s, r, age) -> {
                    p.rot("body", 0.3f, 0, 0, -0.7f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("jaw", -0.6f, 0, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("seg0", 0, 0, 0, 0, 1.2f, 0, w, s, r); p.rot("seg1", 0, 0, 0, 0, 1.2f, 0, w, s, r); p.rot("seg2", 0, 0, 0, 0, 1.2f, 0, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 0.7f); p.add("body", 0, q * 0.25f, 0); float[] b = p.g("body"); b[6] += q * 0.06f; b[8] += q * 0.06f; }
                })
                // fang strike: cobra-style rear, hood flares, explosive lunge
                .atk(Atk.BITE, 8, 2.6f, 0, 24).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.5f, 0, 0, -0.8f, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, -1, -6, w, s, r);
                    p.rot("head", 0.5f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("jaw", -1.0f, 0, 0, 0.05f, 0, 0, w, s, r);
                    p.rot("hoodL", 0, 0, -0.6f, 0, 0, -0.3f, w, s, r); p.rot("hoodR", 0, 0, 0.6f, 0, 0, 0.3f, w, s, r);
                })
                // rattle sweep: rattle buzzes, then the whole noodle body spins round, tail first
                .custom(6, 3.5f, 0, 90, 12, 8, 10, (m, sp, t) -> {
                    if (t < sp.wind() && t % 3 == 0) m.playSound(SoundEvents.ARMOR_EQUIP_CHAIN, 1f, 1.8f);
                    if (t == sp.wind() + 2) { m.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, 0.7f); burst(m, sp, 3.5, 1.1f, null); ring(m, 2.5, ParticleTypes.SWEEP_ATTACK); }
                }, (p, e, w, s, r, age) -> {
                    if (w > 0 && s <= 0) p.add("rattle", 0, 0, Mth.sin(age * 4f) * 0.6f);
                    p.rot("seg0", 0, 0, 0, 0, 0.6f, 0, w, s, r); p.rot("seg1", 0, 0, 0, 0, 0.5f, 0, w, s, r);
                    if (s > 0 && r <= 0) { p.add("body", 0, Pose_e(s) * P * 2, 0); p.add("seg0", 0, Pose_e(s) * P * 2, 0); }
                    p.rot("body", 0.2f, 0, 0, 0.1f, 0, 0, w, s, r);
                })
                // foam spit: hood flares, head pulls back, venomous foam fired
                .atk(Atk.SHOOT, 4, 12f, 3f, 60, 0xe03aa8, "poison").anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.5f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("jaw", -0.5f, 0, 0, -0.8f, 0, 0, w, s, r); p.rot("body", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r);
                    p.rot("hoodL", 0, 0, -0.7f, 0, 0, -0.5f, w, s, r); p.rot("hoodR", 0, 0, 0.7f, 0, 0, 0.5f, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef bobbinMatriarch() {
        int wood = 0xc9975a, woodD = 0x8a5f30, thread = 0xb3122e, threadL = 0xd8344f, cushion = 0xc8223a, leaf = 0x2f8a3a, steel = 0xc9ccd6, steelD = 0x7b8090, thimble = 0xb8bcc8, eye = 0x7cf2ff;
        CreatureDef d = CreatureDef.of("yarnaby", "Yarnaby", "ヤーナビー").size(2.6f, 2.4f).hp(550).dmg(12).speed(0.3).kbRes(0.95).armor(6).egg(cushion, steel).boss()
                .part("body", null, 0, 22, 0, 16, 12, 16, 0, 0, -2, cushion).sway(0.02f, 0.08f, 0, 2)
                .part("leafTop", "body", 0, 6, -2, 12, 1.5f, 12, 0, 0.75f, 0, leaf);
        float[][] pins = {{-4, -6}, {4, -4}, {-2, 2}, {5, 3}};
        for (int i = 0; i < 4; i++) {
            d.part("pin" + i, "body", pins[i][0], 6, pins[i][1], 0.6f, 8, 0.6f, 0, 4, 0, steel).sway(0.05f, 0.2f, i, 2);
            d.part("pinHead" + i, "pin" + i, 0, 8, 0, 2, 2, 2, 0, 0, 0, i % 2 == 0 ? 0xf2e04a : 0x3a7af2);
        }
        d.part("spool", "body", 0, 2, 6, 14, 14, 16, 0, 1, 8, thread).sway(0.03f, 0.08f, 0, 0)
                .part("rimF", "spool", 0, 1, 0, 18, 18, 2, 0, 0, 0, wood)
                .part("rimB", "spool", 0, 1, 16, 18, 18, 2, 0, 0, 0, wood)
                .part("thread", "spool", 3, -6, 10, 1, 10, 1, 0, -5, 0, threadL).sway(0.2f, 0.1f, 0, 0)
                .part("head", "body", 0, 0, -10, 12, 10, 6, 0, 0, -3, thimble)
                .part("eyeL", "head", -3, 2, -6.2f, 3, 3, 1, 0, 0, 0, eye).glow()
                .part("eyeR", "head", 3, 2, -6.2f, 3, 3, 1, 0, 0, 0, eye).glow()
                .part("eyeL2", "head", -5, 4.5f, -6.2f, 1.5f, 1.5f, 0.6f, 0, 0, 0, eye).glow()
                .part("eyeR2", "head", 5, 4.5f, -6.2f, 1.5f, 1.5f, 0.6f, 0, 0, 0, eye).glow()
                .part("eyeL3", "head", -1.5f, 4.8f, -6.2f, 1.2f, 1.2f, 0.6f, 0, 0, 0, eye).glow()
                .part("eyeR3", "head", 1.5f, 4.8f, -6.2f, 1.2f, 1.2f, 0.6f, 0, 0, 0, eye).glow()
                .part("jaw", "head", 0, -4, -4, 6, 2, 3, 0, -1, -1.5f, 0x111111)
                .part("bladeL", "head", -3, -4, -5, 1.5f, 2, 9, 0, -1, -4.5f, steel)
                .part("bladeR", "head", 3, -4, -5, 1.5f, 2, 9, 0, -1, -4.5f, steel);
        float[] zs = {-6, -2, 2, 6};
        for (int i = 0; i < 4; i++) {
            String sfx = i == 0 ? "" : String.valueOf(i + 1);
            float ph = i % 2 == 0 ? 0 : P;
            d.part("legL" + sfx, "body", -8, 2, zs[i], 10, 2, 2, -5, 0, 0, steelD).walk(0.5f, ph);
            d.part("ndlL" + i, "legL" + sfx, -10, 0, 0, 1.5f, 24, 1.5f, 0, -12, 0, steel);
            d.part("legR" + sfx, "body", 8, 2, zs[i], 10, 2, 2, 5, 0, 0, steelD).walk(0.5f, ph + P);
            d.part("ndlR" + i, "legR" + sfx, 10, 0, 0, 1.5f, 24, 1.5f, 0, -12, 0, steel);
        }
        return d
                // pin impale: rears up on the back legs, front needles raised high, then drives them down
                .custom(14, 4.5f, 0, 70, 18, 4, 16, (m, sp, t) -> {
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.TRIDENT_HIT, 2f, 0.6f);
                        for (LivingEntity e : Fx.arc(m, 4.5, 0.3)) { Fx.hit(m, e, sp.dmg(), 0.4f, Fx.flat(m)); e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2)); puff(m, e.position().add(0, 1, 0), ParticleTypes.CRIT, 12, 0.4, 0.3); } }
                }, (p, e, w, s, r, age) -> {
                    p.rot("body", 0.45f, 0, 0, -0.25f, 0, 0, w, s, r); p.pos("body", 0, 3, 2, 0, -2, -5, w, s, r);
                    p.rot("legL", 0, 0.5f, -1.3f, 0, 0.3f, -0.1f, w, s, r); p.rot("legR", 0, -0.5f, 1.3f, 0, -0.3f, 0.1f, w, s, r);
                    p.rot("legL2", 0, 0.2f, -0.8f, 0, 0, 0, w, s, r); p.rot("legR2", 0, -0.2f, 0.8f, 0, 0, 0, w, s, r);
                    p.rot("spool", -0.3f, 0, 0, 0.2f, 0, 0, w, s, r);
                })
                // snip: the scissor-blade mandibles gape, then shear shut
                .atk(Atk.BITE, 10, 3f, 0, 30).anim((p, e, w, s, r, age) -> {
                    p.rot("bladeL", 0, 0.7f, 0, 0, -0.25f, 0, w, s, r); p.rot("bladeR", 0, -0.7f, 0, 0, 0.25f, 0, w, s, r);
                    p.rot("head", 0.3f, 0, 0, -0.25f, 0, 0, w, s, r); p.pos("body", 0, 1, 2, 0, -1, -5, w, s, r); p.rot("body", 0.15f, 0, 0, -0.15f, 0, 0, w, s, r);
                })
                // needle rain: tilts the spool skyward, pins quiver, then fires a spread of steel needles
                .custom(6, 16f, 4f, 110, 16, 2, 12, (m, sp, t) -> {
                    if (t < sp.wind() && t % 4 == 0) m.playSound(SoundEvents.CHAIN_STEP, 1.5f, 1.5f);
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.ARROW_SHOOT, 2f, 0.6f); fan(m, sp.dmg(), 5, 0.18f, 0xc9ccd6, ""); }
                }, (p, e, w, s, r, age) -> {
                    p.rot("body", 0.3f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("spool", -0.5f, 0, 0, 0.3f, 0, 0, w, s, r);
                    for (int i = 0; i < 4; i++) { if (w > 0 && s <= 0) p.add("pin" + i, Mth.sin(age * 3f + i) * 0.25f * w, 0, Mth.cos(age * 3f + i) * 0.25f * w); p.rot("pin" + i, 0, 0, 0, -0.6f, 0, 0, w, s, r); }
                })
                // thread snare: the spool whirs and a loose thread lashes out to reel prey in
                .atk(Atk.WHIP, 8, 10f, 3f, 80).anim((p, e, w, s, r, age) -> {
                    if (w > 0 && r <= 0) p.add("spool", 0, 0, age * 0.8f);
                    p.rot("thread", 0.5f, 0, 0, 2.6f, 0, 0, w, s, r); p.scl("thread", 1, 1, 1, 1.5f, 5f, 1.5f, w, s, r);
                    p.rot("body", -0.15f, 0, 0, 0.15f, 0, 0, w, s, r);
                })
                // brood call: whole body shudders, spool spins, knitted moths hatch around her
                .atk(Atk.SUMMON, 0, 14f, 0, 500, 2, "mini_yarnaby").anim((p, e, w, s, r, age) -> {
                    float sh = (r > 0 ? 1 - r : w) * 0.06f; p.add("body", Mth.sin(age * 3f) * sh, 0, Mth.cos(age * 2.7f) * sh);
                    p.rot("spool", 0, 0, 0, 0, 0, 1.5f, w, s, r); p.rot("head", 0.4f, 0, 0, -0.2f, 0, 0, w, s, r);
                    p.rot("bladeL", 0, 0.6f, 0, 0, 0.6f, 0, w, s, r); p.rot("bladeR", 0, -0.6f, 0, 0, -0.6f, 0, w, s, r);
                })
                // spool quake: all eight needles lift, then she drops her full weight
                .atk(Atk.STOMP, 10, 5f, 0, 120).anim((p, e, w, s, r, age) -> {
                    for (String sfx : new String[]{"", "2", "3", "4"}) { p.rot("legL" + sfx, 0, 0, -0.5f, 0, 0, 0.15f, w, s, r); p.rot("legR" + sfx, 0, 0, 0.5f, 0, 0, -0.15f, w, s, r); }
                    p.pos("body", 0, 4, 0, 0, -4, 0, w, s, r); p.scl("body", 1, 1.05f, 1, 1.08f, 0.92f, 1.08f, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef slurpSlug() {
        int red = 0xe8304a, white = 0xfaf4f4, pink = 0xf2b8d0, blue = 0x6ad1ff, mouth = 0x4a0a1a, tongue = 0x9b4cff;
        int[] spr = {0xffe14a, 0x3ac8e0, 0x8fe03a, 0xff9a2e, 0xffffff};
        CreatureDef d = CreatureDef.of("bubby_chumz", "Bubby Chumz", "バビー・チャムズ").size(1.0f, 1.2f).hp(55).dmg(5).speed(0.22).egg(red, white).spawn(2)
                .part("foot", null, 0, 0, 2, 12, 3, 22, 0, 1.5f, 0, pink)
                .part("tailPart", "foot", 0, 3, 8, 8, 5, 8, 0, 2.5f, 0, red).sway(0.08f, 0.12f, 0, 1)
                .part("tailStripe", "tailPart", 0, 2.5f, 0, 8.4f, 5.4f, 2, 0, 0, 0, white)
                .part("body", null, 0, 3, -2, 10, 8, 14, 0, 4, 1, red).sway(0.03f, 0.12f, 0, 2)
                .part("stripeA", "body", 0, 4, -3.5f, 10.4f, 8.4f, 2, 0, 0, 0, white)
                .part("stripeB", "body", 0, 4, 2.5f, 10.4f, 8.4f, 2, 0, 0, 0, white);
        for (int i = 0; i < 5; i++) d.part("sprinkle" + i, "body", -3.5f + i * 1.8f, 8.2f, -4 + (i * 7 % 5) * 2, 1, 0.5f, 2, 0, 0, 0, spr[i]);
        return d
                .part("head", "body", 0, 6, -6, 9, 8, 7, 0, -1, -3.5f, red)
                .part("stalkL", "head", -2.5f, 3, -3, 1.5f, 7, 1.5f, 0, 3.5f, 0, white).sway(0.15f, 0.15f, 0, 2)
                .part("lolliL", "stalkL", 0, 7, 0, 4, 4, 1.5f, 0, 2, 0, blue)
                .part("pupilL", "lolliL", 0, 2, -0.9f, 1.5f, 1.5f, 0.4f, 0, 0, 0, 0x111111)
                .part("stalkR", "head", 2.5f, 3, -3, 1.5f, 7, 1.5f, 0, 3.5f, 0, white).sway(0.15f, 0.15f, P, 2)
                .part("lolliR", "stalkR", 0, 7, 0, 4, 4, 1.5f, 0, 2, 0, blue)
                .part("pupilR", "lolliR", 0, 2, -0.9f, 1.5f, 1.5f, 0.4f, 0, 0, 0, 0x111111)
                .part("tentL", "head", -3.5f, -3, -7, 1, 1, 3, 0, 0, -1.5f, pink).sway(0.2f, 0.2f, 0, 1)
                .part("tentR", "head", 3.5f, -3, -7, 1, 1, 3, 0, 0, -1.5f, pink).sway(0.2f, 0.2f, P, 1)
                .part("jaw", "head", 0, -3.5f, -6.5f, 7, 2.5f, 2, 0, -1, -0.5f, mouth)
                .part("toothL", "head", -2, -3.6f, -7.2f, 1.2f, 1.5f, 0.6f, 0, -0.75f, 0, white)
                .part("toothR", "head", 2, -3.6f, -7.2f, 1.2f, 1.5f, 0.6f, 0, -0.75f, 0, white)
                .part("tongue", "jaw", 0, -0.5f, -1, 4, 1, 4, 0, 0, -2, tongue)
                .part("tongueTip", "tongue", 0, 0, -4, 5, 1, 2, 0, 0, -1, tongue)
                .part("drool", "jaw", 0, -2, -1.5f, 1, 3, 1, 0, -1.5f, 0, 0xbfe8ff).sway(0.2f, 0.2f, 0, 0)
                // long lick: head drops, mouth gapes, tongue unrolls ten-fold
                .atk(Atk.WHIP, 5, 7f, 1.5f, 50).anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.3f, 0, 0, -0.15f, 0, 0, w, s, r); p.rot("jaw", -0.5f, 0, 0, -0.2f, 0, 0, w, s, r);
                    p.scl("tongue", 1, 1, 1, 1, 1, 10f, w, s, r); p.pos("body", 0, 0, 1.5f, 0, 0, -2, w, s, r);
                    p.rot("stalkL", -0.3f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("stalkR", -0.3f, 0, 0, 0.5f, 0, 0, w, s, r);
                })
                // syrup spill: body heaves and gushes a sticky pool that glues feet in place
                .custom(2, 5f, 0, 160, 16, 10, 12, (m, sp, t) -> {
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 2f, 0.5f);
                        cloud(m, front(m, 1.8, 0), 0xd98a1a, 3.2f, 160, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3), new MobEffectInstance(MobEffects.JUMP, 60, 128));
                        for (LivingEntity e : Fx.arc(m, 3, 0.2)) Fx.hit(m, e, sp.dmg(), 0, Vec3.ZERO); }
                    if (t > sp.wind() && t <= sp.wind() + sp.strike()) puff(m, front(m, 1.2, 0.6), dust(0xd98a1a, 1.5f), 6, 0.3, 0.05);
                }, (p, e, w, s, r, age) -> {
                    float hv = (s <= 0 && r <= 0) ? Mth.sin(age * 1.5f) * 0.08f * w : 0;
                    p.scl("body", 1 + hv, 1.1f - hv, 1, 0.9f, 0.9f, 1.15f, w, s, r); p.rot("head", -0.3f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -1.0f, 0, 0, w, s, r);
                    p.rot("stalkL", 0.2f, 0, -0.3f, -0.4f, 0, -0.5f, w, s, r); p.rot("stalkR", 0.2f, 0, 0.3f, -0.4f, 0, 0.5f, w, s, r);
                })
                .atk(Atk.BITE, 6, 2f, 0, 24).anim((p, e, w, s, r, age) -> {
                    p.rot("jaw", -0.9f, 0, 0, 0.05f, 0, 0, w, s, r); p.rot("head", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -4, w, s, r);
                    p.scl("body", 0.95f, 1.05f, 0.9f, 1, 0.95f, 1.15f, w, s, r);
                })
                // gumball spit
                .atk(Atk.SHOOT, 4, 12f, 3f, 60, 0x6ad1ff, "slow").anim((p, e, w, s, r, age) -> {
                    p.scl("head", 1.15f, 1.15f, 1, 0.95f, 0.95f, 1.1f, w, s, r); p.rot("head", 0.3f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                })
                // sugar slam: rears its front half up high, then crashes down
                .atk(Atk.SLAM, 7, 3f, 0, 80).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.9f, 0, 0, -0.35f, 0, 0, w, s, r); p.pos("body", 0, 2, 2, 0, -1, -3, w, s, r);
                    p.scl("body", 0.9f, 1.15f, 0.9f, 1.2f, 0.85f, 1.1f, w, s, r); p.rot("head", -0.3f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("jaw", -0.4f, 0, 0, 0, 0, 0, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef gloopGumdrop() {
        int jelly = 0x6fd14a, jellyD = 0x3f9a2a, sugar = 0xf2f7e8, gut = 0xb0235a, gutL = 0xff6fa0, eye = 0xfff6c8, mouth = 0x2a0a18, tooth = 0xf2efe0;
        CreatureDef d = CreatureDef.of("mini_doughman", "Mini Doughman", "ミニ・ドウマン").size(1.1f, 1.2f).hp(60).dmg(6).speed(0.3).egg(jelly, gut).spawn(2)
                .part("legL", null, -4, 2, 0, 5, 2, 5, 0, -1, 0, jellyD).walk(0.9f, 0)
                .part("legR", null, 4, 2, 0, 5, 2, 5, 0, -1, 0, jellyD).walk(0.9f, P)
                .part("body", null, 0, 1, 0, 16, 13, 16, 0, 6.5f, 0, jelly).sway(0.04f, 0.2f, 0, 2)
                .part("top", "body", 0, 13, 0, 12, 3, 12, 0, 1.5f, 0, jelly)
                .part("topTip", "body", 0, 16, 0, 6, 2, 6, 0, 1, 0, jelly).sway(0.15f, 0.2f, 0.5f, 0);
        float[][] sg = {{-6, 13.2f, -3}, {4, 13.2f, 4}, {7.9f, 9, -3}, {-7.9f, 4, 5}, {0, 18.2f, 1}, {3, 13.2f, -5}};
        for (int i = 0; i < sg.length; i++) d.part("sugar" + i, "body", sg[i][0], sg[i][1], sg[i][2], 1.2f, 1.2f, 1.2f, 0, 0, 0, sugar);
        d.part("gut0", "body", -4, 3, -8.1f, 5, 3, 0.4f, 0, 0, 0, gut)
                .part("gut1", "body", 5, 4, -8.1f, 3, 2, 0.4f, 0, 0, 0, gutL)
                .part("gut2", "body", 8.1f, 6, 1, 0.4f, 4, 5, 0, 0, 0, gut)
                .part("gut3", "body", -8.1f, 9, -2, 0.4f, 3, 4, 0, 0, 0, gutL)
                .part("head", "body", 0, 9, -8, 12, 7, 1, 0, 0, -0.2f, jelly)
                .part("eye", "head", -1.5f, 1.5f, -0.8f, 6, 5, 0.6f, 0, 0, 0, eye)
                .part("pupil", "eye", 0.5f, 0, -0.4f, 2.5f, 3.5f, 0.4f, 0, 0, 0, 0x111111)
                .part("eye2", "head", 4, 2.5f, -0.8f, 2, 2, 0.6f, 0, 0, 0, eye)
                .part("pupil2", "eye2", 0, 0, -0.4f, 1, 1, 0.4f, 0, 0, 0, 0x111111)
                .part("jaw", "head", 0, -2, -0.5f, 10, 3, 1, 0, -1.5f, -0.3f, mouth);
        row(d, "tooth", "jaw", 4, -3.5f, 3.5f, 0, -0.9f, 1.5f, 1.5f, 0.4f, -1, tooth);
        return d
                .part("drip0", "body", -8.1f, 5, -3, 1, 5, 2, 0, -2.5f, 0, jellyD).sway(0.1f, 0.2f, 0, 0)
                .part("drip1", "body", 8.1f, 3, 4, 1, 4, 2, 0, -2, 0, jellyD).sway(0.1f, 0.2f, 1, 0)
                .part("drip2", "body", 3, 1, -8.1f, 2, 3, 1, 0, -1.5f, 0, jellyD).sway(0.1f, 0.2f, 2, 0)
                .part("armL", "body", -8, 8, 0, 3, 7, 3, -1, -3.5f, 0, jelly).sway(0.1f, 0.2f, 0, 2)
                .part("armR", "body", 8, 8, 0, 3, 7, 3, 1, -3.5f, 0, jelly).sway(0.1f, 0.2f, P, 2)
                // splat pounce: squash low, stretch tall mid-air, splat open-mouthed
                .atk(Atk.POUNCE, 7, 7f, 2f, 60).anim((p, e, w, s, r, age) -> {
                    p.scl("body", 1.25f, 0.7f, 1.25f, 0.85f, 1.3f, 0.85f, w, s, r); p.pos("body", 0, -1, 0, 0, 2, -3, w, s, r);
                    p.rot("body", 0.2f, 0, 0, -0.45f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -1.0f, 0, 0, w, s, r);
                    p.rot("armL", 0, 0, -0.4f, 2.6f, 0, -0.3f, w, s, r); p.rot("armR", 0, 0, 0.4f, 2.6f, 0, 0.3f, w, s, r);
                })
                // gut spew: bloats, heaves and retches a toxic green-pink stream
                .custom(3, 5f, 0, 160, 14, 12, 10, (m, sp, t) -> {
                    int hit = sp.wind() + 1;
                    if (t == hit) { m.playSound(SoundEvents.PLAYER_BURP, 2f, 0.4f);
                        cloud(m, front(m, 2.2, 0), 0x6fd14a, 3f, 140, new MobEffectInstance(MobEffects.POISON, 80, 0), new MobEffectInstance(MobEffects.CONFUSION, 120, 0), new MobEffectInstance(MobEffects.HUNGER, 120, 1)); }
                    if (t >= hit && t < hit + sp.strike()) {
                        Vec3 o = m.getEyePosition().add(Fx.flat(m).scale(0.8));
                        puff(m, o.add(Fx.flat(m).scale((t - hit) * 0.2)), dust(t % 2 == 0 ? 0x6fd14a : 0xff6fa0, 1.6f), 6, 0.25, 0.05);
                        if ((t - hit) % 4 == 0) for (LivingEntity e : Fx.arc(m, 3.5, 0.5)) Fx.hit(m, e, sp.dmg(), 0.2f, Fx.flat(m));
                    }
                }, (p, e, w, s, r, age) -> {
                    p.scl("body", 1.18f, 1.12f, 1.18f, 0.92f, 1.0f, 0.95f, w, s, r); p.rot("body", 0.25f, 0, 0, -0.35f, 0, 0, w, s, r);
                    p.rot("jaw", -0.3f, 0, 0, -1.2f, 0, 0, w, s, r); p.pos("jaw", 0, 0, 0, 0, -1.5f, -0.5f, w, s, r);
                    if (s > 0 && r <= 0) { p.add("body", Mth.sin(age * 2.2f) * 0.08f, 0, 0); p.g("body")[7] += Mth.sin(age * 2.2f) * 0.05f; }
                })
                // glob shot: contracts, then squirts a poisonous glob
                .atk(Atk.SHOOT, 4, 12f, 3f, 50, 0x6fd14a, "poison").anim((p, e, w, s, r, age) -> {
                    p.scl("body", 0.9f, 1.1f, 0.9f, 1.1f, 0.9f, 1.1f, w, s, r); p.rot("jaw", 0, 0, 0, -0.8f, 0, 0, w, s, r); p.rot("body", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r);
                })
                // engulf bite: whole face surges forward and gulps
                .atk(Atk.BITE, 7, 2.2f, 0, 22).anim((p, e, w, s, r, age) -> {
                    p.rot("jaw", -0.9f, 0, 0, 0.05f, 0, 0, w, s, r); p.scl("head", 1.1f, 1.3f, 1, 1.25f, 1.1f, 1, w, s, r);
                    p.pos("body", 0, 0, 2, 0, 0, -5, w, s, r); p.rot("body", 0.25f, 0, 0, -0.35f, 0, 0, w, s, r); p.scl("body", 1, 1, 1, 1.05f, 0.92f, 1.2f, w, s, r);
                })
                // jiggle burst: wobbles violently, then pops outward in a slimy shock ring
                .custom(5, 3.5f, 0, 110, 16, 4, 12, (m, sp, t) -> {
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.SLIME_SQUISH, 2f, 0.5f); ring(m, 2.5, ParticleTypes.ITEM_SLIME); burst(m, sp, 3.5, 1.5f, null); }
                }, (p, e, w, s, r, age) -> {
                    float j = (s <= 0 && r <= 0) ? Mth.sin(age * 3.3f) * 0.15f * w : 0;
                    p.scl("body", 1 + j, 1 - j, 1 + j, 1.35f, 0.75f, 1.35f, w, s, r); p.rot("jaw", 0, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("armL", 0, 0, -0.3f, 0, 0, -1.4f, w, s, r); p.rot("armR", 0, 0, 0.3f, 0, 0, 1.4f, w, s, r);
                });
    }

    // =====================================================================================================
    static CreatureDef topsyJester() {
        int red = 0xe63a3a, yel = 0xf6d23a, blue = 0x3a6ae6, white = 0xf8f4ec, black = 0x1a1a1a, gold = 0xe8b830, pink = 0xff8fb0, purple = 0x8a3ae6;
        CreatureDef d = CreatureDef.of("nightmare_bunzo", "Nightmare Bunzo", "ナイトメア・バンゾー").size(0.9f, 1.9f).hp(65).dmg(6).speed(0.34).egg(purple, yel).spawn(1)
                .part("spinner", null, 0, 0, 0, 1, 1, 1, 0, 0.5f, 0, gold).sway(0.6f, 0.5f, 0, 1)
                .part("tip", "spinner", 0, 0, 0, 2, 3, 2, 0, 1.5f, 0, gold)
                .part("tier1", "spinner", 0, 3, 0, 6, 3, 6, 0, 1.5f, 0, blue)
                .part("tier2", "spinner", 0, 6, 0, 10, 3, 10, 0, 1.5f, 0, red)
                .part("tier3", "spinner", 0, 9, 0, 12, 2, 12, 0, 1, 0, yel)
                .part("body", null, 0, 11, 0, 8, 9, 6, 0, 4.5f, 0, purple).sway(0.06f, 0.15f, 0, 2)
                .part("patchL", "body", -2, 5, -3.1f, 3, 3, 0.4f, 0, 0, 0, yel)
                .part("patchR", "body", 2, 2, -3.1f, 3, 3, 0.4f, 0, 0, 0, red)
                .part("button", "body", 0, 7.5f, -3.2f, 1, 1, 0.4f, 0, 0, 0, gold)
                .part("ruff", "body", 0, 9, 0, 11, 2, 9, 0, 0.5f, 0, white)
                .part("head", "body", 0, 10, 0, 8, 7, 7, 0, 3.5f, 0, white)
                .part("cheekL", "head", -3, 2.5f, -3.6f, 1.5f, 1.5f, 0.4f, 0, 0, 0, pink)
                .part("cheekR", "head", 3, 2.5f, -3.6f, 1.5f, 1.5f, 0.4f, 0, 0, 0, pink)
                .part("markL", "head", -2, 4.5f, -3.55f, 2.4f, 3, 0.2f, 0, 0, 0, blue)
                .part("markR", "head", 2, 4.5f, -3.55f, 2.4f, 3, 0.2f, 0, 0, 0, blue)
                .part("eyeL", "markL", 0, 0.3f, -0.2f, 1.2f, 1.2f, 0.3f, 0, 0, 0, 0xfff36a).glow()
                .part("eyeR", "markR", 0, 0.3f, -0.2f, 1.2f, 1.2f, 0.3f, 0, 0, 0, 0xfff36a).glow()
                .part("nose", "head", 0, 3, -3.8f, 2, 2, 1.5f, 0, 0, 0, red)
                .part("jaw", "head", 0, 1.5f, -3, 6, 2, 1, 0, -0.5f, -0.7f, black);
        row(d, "tooth", "head", 3, -1.5f, 1.5f, 1.4f, -3.8f, 1.2f, 1, 0.4f, -1, white);
        return d
                .part("hatBase", "head", 0, 7, 0, 9, 2, 8, 0, 1, 0, red)
                .part("hatL", "hatBase", -2, 2, 0, 3, 7, 3, -2, 2, 0, red).sway(0.2f, 0.15f, 0, 2)
                .part("bellL", "hatL", -3, 5, 0, 2, 2, 2, -1, 0, 0, gold)
                .part("hatR", "hatBase", 2, 2, 0, 3, 7, 3, 2, 2, 0, blue).sway(0.2f, 0.15f, P, 2)
                .part("bellR", "hatR", 3, 5, 0, 2, 2, 2, 1, 0, 0, gold)
                .part("armL", "body", -5, 8, 0, 2.5f, 14, 2.5f, 0, -7, 0, purple).sway(0.08f, 0.15f, 0, 2)
                .part("cuffL", "armL", 0, -13, 0, 3.5f, 2, 3.5f, 0, 0, 0, yel)
                .part("handL", "armL", 0, -14, 0, 3, 3, 3, 0, -1.5f, 0, white)
                .part("armR", "body", 5, 8, 0, 2.5f, 14, 2.5f, 0, -7, 0, purple).sway(0.08f, 0.15f, P, 2)
                .part("cuffR", "armR", 0, -13, 0, 3.5f, 2, 3.5f, 0, 0, 0, yel)
                .part("handR", "armR", 0, -14, 0, 3, 3, 3, 0, -1.5f, 0, white)
                // whirligig: arms fling out flat, the whole top spins up, hat-bells flying
                .atk(Atk.SPIN, 4, 3f, 0, 100).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 0.1f, 0, -0.6f, 0, 0, -1.55f, w, s, r); p.rot("armR", 0.1f, 0, 0.6f, 0, 0, 1.55f, w, s, r);
                    p.rot("hatL", 0, 0, -0.4f, 0, 0, -1.1f, w, s, r); p.rot("hatR", 0, 0, 0.4f, 0, 0, 1.1f, w, s, r); p.rot("body", 0.15f, 0, 0, 0, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) { float a = Pose_e(s) * P * 8; p.add("body", 0, a, 0); p.add("spinner", 0, a * 1.5f, 0); }
                    if (r > 0) p.add("head", 0, 0, Mth.sin(age * 0.9f) * 0.3f * (1 - r));
                })
                // cymbal clap: arms swing wide behind, then slam together at face height
                .atk(Atk.CLAP, 6, 3f, 0, 60).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 1.0f, 0.6f, -1.5f, 1.6f, 0, 0.15f, w, s, r); p.rot("armR", 1.0f, -0.6f, 1.5f, 1.6f, 0, -0.15f, w, s, r);
                    p.rot("body", 0.2f, 0, 0, -0.25f, 0, 0, w, s, r); p.rot("head", 0.25f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.6f, 0, 0, w, s, r);
                })
                // confetti burst: flourish overhead, then flings a blinding confetti bomb
                .atk(Atk.SHOOT, 4, 12f, 3f, 50, 0xf6d23a, "blind").anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.9f, 0, 0.4f, 1.3f, 0, -0.1f, w, s, r); p.rot("armL", 0.4f, 0, -0.9f, 0.2f, 0, -0.4f, w, s, r);
                    p.rot("body", 0.15f, 0.4f, 0, -0.2f, -0.3f, 0, w, s, r); p.rot("head", 0.2f, 0, 0.2f, -0.1f, 0, -0.1f, w, s, r);
                })
                // dizzy wobble: tilts and reels like a top losing speed; everyone nearby gets dizzy
                .custom(2, 5f, 0, 150, 14, 20, 10, (m, sp, t) -> {
                    int hit = sp.wind() + 1;
                    if (t == hit) { m.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 2f, 0.5f);
                        burst(m, sp, 5, 0.2f, e -> { e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0)); e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1)); }); }
                    if (t >= hit && t < hit + sp.strike() && t % 2 == 0) { double a = t * 0.6; puff(m, m.position().add(Math.cos(a) * 2, 2, Math.sin(a) * 2), ParticleTypes.NOTE, 1, 0, 0); }
                }, (p, e, w, s, r, age) -> {
                    float k = r > 0 ? 1 - r : Math.max(w, s);
                    p.add("body", Mth.cos(age * 0.7f) * 0.25f * k, 0, Mth.sin(age * 0.7f) * 0.35f * k); p.add("spinner", Mth.cos(age * 0.7f) * 0.2f * k, age * 0.8f * k, Mth.sin(age * 0.7f) * 0.2f * k);
                    p.add("head", 0, 0, Mth.sin(age * 0.5f) * 0.4f * k); p.rot("armL", 0.2f, 0, -1.0f, 0.2f, 0, -1.2f, w, s, r); p.rot("armR", 0.2f, 0, 1.0f, 0.2f, 0, 1.2f, w, s, r);
                })
                // jack-in-the-box: torso springs up on a coil, then headbutts down onto the target
                .custom(8, 3f, 0, 80, 12, 4, 12, (m, sp, t) -> {
                    if (t == sp.wind() - 2) m.playSound(SoundEvents.CROSSBOW_LOADING_END, 2f, 1.4f);
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.ANVIL_LAND, 1f, 1.6f);
                        for (LivingEntity e : Fx.arc(m, 3, 0.3)) { Fx.hit(m, e, sp.dmg(), 0.6f, Fx.flat(m)); e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2)); puff(m, e.getEyePosition(), ParticleTypes.CRIT, 8, 0.3, 0.2); } }
                }, (p, e, w, s, r, age) -> {
                    p.pos("body", 0, 9, 2, 0, 2, -5, w, s, r); p.rot("body", 0.35f, 0, 0, -1.1f, 0, 0, w, s, r);
                    p.rot("armL", -0.3f, 0, -0.6f, 2.8f, 0, -0.2f, w, s, r); p.rot("armR", -0.3f, 0, 0.6f, 2.8f, 0, 0.2f, w, s, r);
                    p.rot("jaw", -0.2f, 0, 0, -0.8f, 0, 0, w, s, r); if (w > 0 && s <= 0) p.add("body", 0, 0, Mth.sin(age * 4f) * 0.05f * w);
                });
    }

    // =====================================================================================================
    static CreatureDef gloamOwl() {
        int navy = 0x23305e, navyD = 0x161e40, cream = 0xe9dfc4, amber = 0xffb02e, ring = 0x6a7bc4, beak = 0xe08a2a, stitch = 0xc9c2a8;
        CreatureDef d = CreatureDef.of("poe", "Poe", "ポー").size(0.9f, 1.6f).hp(50).dmg(6).speed(0.3).egg(navy, amber).spawn(2)
                .part("legL", null, -2.5f, 4, 0, 2.5f, 4, 2.5f, 0, -2, 0, beak).walk(0.8f, 0)
                .part("talonL", "legL", 0, -4, -1, 3, 1, 4, 0, 0.5f, -1, 0x2a2a2a)
                .part("legR", null, 2.5f, 4, 0, 2.5f, 4, 2.5f, 0, -2, 0, beak).walk(0.8f, P)
                .part("talonR", "legR", 0, -4, -1, 3, 1, 4, 0, 0.5f, -1, 0x2a2a2a)
                .part("body", null, 0, 4, 0, 12, 12, 10, 0, 6, 0, navy).sway(0.03f, 0.1f, 0, 2)
                .part("belly", "body", 0, 5, -5.2f, 8, 8, 0.6f, 0, 0, 0, cream);
        for (int i = 0; i < 3; i++) d.part("chevron" + i, "body", 0, 2.5f + i * 2.5f, -5.6f, 4, 0.6f, 0.4f, 0, 0, 0, stitch);
        return d
                .part("seam", "body", 0, 6, 5.2f, 0.6f, 12, 0.4f, 0, 0, 0, stitch)
                .part("patch", "body", 3.5f, 9, -5.25f, 3, 3, 0.4f, 0, 0, 0, 0x7a4fb5)
                .part("tail", "body", 0, 1, 5, 6, 2, 4, 0, 0, 2, navyD)
                .part("head", "body", 0, 12, 0, 12, 9, 10, 0, 4.5f, 0, navy)
                .part("ringL", "head", -3, 5, -5.2f, 5, 5, 0.6f, 0, 0, 0, ring)
                .part("eyeL", "ringL", 0, 0, -0.5f, 3.5f, 3.5f, 0.4f, 0, 0, 0, amber).glow()
                .part("pupilL", "eyeL", 0, 0, -0.3f, 1.5f, 2.5f, 0.3f, 0, 0, 0, 0x0d0d14)
                .part("ringR", "head", 3, 5, -5.2f, 5, 5, 0.6f, 0, 0, 0, ring)
                .part("eyeR", "ringR", 0, 0, -0.5f, 3.5f, 3.5f, 0.4f, 0, 0, 0, amber).glow()
                .part("pupilR", "eyeR", 0, 0, -0.3f, 1.5f, 2.5f, 0.3f, 0, 0, 0, 0x0d0d14)
                .part("brow", "head", 0, 7.6f, -5.3f, 10, 1, 0.6f, 0, 0, 0, navyD)
                .part("beak", "head", 0, 3, -5.4f, 2, 3, 1.5f, 0, -0.5f, -0.5f, beak)
                .part("jaw", "head", 0, 1.8f, -5.4f, 1.6f, 1.2f, 1.2f, 0, -0.6f, -0.4f, 0xb0661a)
                .part("tuftL", "head", -4.5f, 9, 0, 2, 5, 2, 0, 2.5f, 0, navyD).sway(0.1f, 0.12f, 0, 2)
                .part("tuftR", "head", 4.5f, 9, 0, 2, 5, 2, 0, 2.5f, 0, navyD).sway(0.1f, 0.12f, P, 2)
                .part("armL", "body", -6.5f, 11, 0, 2, 10, 8, -0.5f, -5, 0, navyD).sway(0.05f, 0.1f, 0, 2)
                .part("featherL", "armL", 0, -10, 0, 2, 3, 6, 0, -1, 0, cream)
                .part("wstitchL", "armL", -1.1f, -4, 0, 0.3f, 0.5f, 6, 0, 0, 0, stitch)
                .part("armR", "body", 6.5f, 11, 0, 2, 10, 8, 0.5f, -5, 0, navyD).sway(0.05f, 0.1f, P, 2)
                .part("featherR", "armR", 0, -10, 0, 2, 3, 6, 0, -1, 0, cream)
                .part("wstitchR", "armR", 1.1f, -4, 0, 0.3f, 0.5f, 6, 0, 0, 0, stitch)
                // silent swoop: wings rise, it drops into a dive with talons thrown forward
                .atk(Atk.POUNCE, 7, 8f, 2f, 60).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 0.2f, 0, -2.4f, -0.3f, 0, -1.2f, w, s, r); p.rot("armR", 0.2f, 0, 2.4f, -0.3f, 0, 1.2f, w, s, r);
                    p.rot("body", 0.1f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("head", 0, 0, 0, 0.5f, 0, 0, w, s, r);
                    p.rot("legL", -0.3f, 0, 0, 1.3f, 0, 0, w, s, r); p.rot("legR", -0.3f, 0, 0, 1.3f, 0, 0, w, s, r); p.pos("body", 0, -1.5f, 0, 0, 2, -2, w, s, r);
                })
                // night screech: head cocks back, wings fan, the glowing eyes widen
                .atk(Atk.SCREAM, 3, 8f, 2f, 200).anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.6f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", -0.6f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("armL", 0.3f, 0, -1.3f, 0.4f, 0, -1.6f, w, s, r); p.rot("armR", 0.3f, 0, 1.3f, 0.4f, 0, 1.6f, w, s, r);
                    p.scl("ringL", 1, 1, 1, 1.35f, 1.35f, 1, w, s, r); p.scl("ringR", 1, 1, 1, 1.35f, 1.35f, 1, w, s, r);
                    p.rot("tuftL", 0, 0, -0.3f, 0, 0, -0.6f, w, s, r); p.rot("tuftR", 0, 0, 0.3f, 0, 0, 0.6f, w, s, r);
                    if (s > 0 && r <= 0) p.add("head", 0, 0, Mth.sin(age * 3f) * 0.1f);
                })
                // wing buffet: body winds round, then the right wing backhands across
                .atk(Atk.SWIPE, 6, 3f, 0, 26).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 0.4f, -0.6f, 1.6f, 1.2f, 1.0f, 0.4f, w, s, r); p.rot("armL", 0, 0, -0.6f, 0.2f, 0, -0.3f, w, s, r);
                    p.rot("body", 0, -0.6f, 0, -0.15f, 0.6f, 0, w, s, r); p.rot("head", 0, 0.5f, 0, 0, -0.4f, 0, w, s, r);
                })
                // talon rake: hops up and rakes with alternating feet, two hits
                .custom(4, 2.5f, 0, 50, 10, 10, 10, (m, sp, t) -> {
                    int hit = sp.wind() + 1;
                    if (t == hit) { m.setDeltaMovement(m.getDeltaMovement().add(Fx.flat(m).scale(0.25)).add(0, 0.35, 0)); m.hasImpulse = true; }
                    if (t == hit + 2 || t == hit + 7) { m.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1f, 1.4f); for (LivingEntity e : Fx.arc(m, 2.5, 0.3)) { e.invulnerableTime = 0; Fx.hit(m, e, sp.dmg(), 0.3f, Fx.flat(m)); } }
                }, (p, e, w, s, r, age) -> {
                    p.rot("armL", 0, 0, -0.5f, 0, 0, -1.8f, w, s, r); p.rot("armR", 0, 0, 0.5f, 0, 0, 1.8f, w, s, r); p.rot("body", 0.1f, 0, 0, 0.4f, 0, 0, w, s, r);
                    p.pos("body", 0, -1.5f, 0, 0, 3, 0, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(s * P * 2); p.g("legL")[0] += 0.6f + q; p.g("legR")[0] += 0.6f - q; }
                })
                // blackout: wings wrap over the face, eyes go dark, then flare as darkness spreads
                .custom(2, 6f, 0, 220, 18, 4, 14, (m, sp, t) -> {
                    if (t == sp.wind() + 1) { m.playSound(SoundEvents.WARDEN_NEARBY_CLOSE, 2f, 1.2f); puff(m, m.position().add(0, 1, 0), ParticleTypes.SQUID_INK, 40, 2, 0.05);
                        burst(m, sp, 6, 0, e -> { e.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0)); e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0)); }); }
                }, (p, e, w, s, r, age) -> {
                    p.rot("armL", 1.6f, 0, 0.8f, 0.3f, 0, -1.7f, w, s, r); p.rot("armR", 1.6f, 0, -0.8f, 0.3f, 0, 1.7f, w, s, r);
                    p.scl("eyeL", 0.2f, 0.2f, 1, 1.6f, 1.6f, 1, w, s, r); p.scl("eyeR", 0.2f, 0.2f, 1, 1.6f, 1.6f, 1, w, s, r);
                    p.rot("head", -0.3f, 0, 0, 0.2f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) p.add("head", 0, Mth.sin(s * P) * 2.4f, 0); // creepy near-full head swivel
                });
    }

    private BatchF() {}
}
