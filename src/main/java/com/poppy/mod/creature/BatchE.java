package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch E – "lab experiment" slot: original creature designs (boss + experiment-style horrors). See DEV_GUIDE.md.
 * Server-side effects for custom attacks live in {@link EFx}.
 */
public final class BatchE {
    static final float P = (float) Math.PI;

    public static List<CreatureDef> all() {
        return new ArrayList<>(List.of(scrapSovereign(), ragStalker(), cogHound(), wireWidow(), furnaceBrute(), jarWraith(),
                stringPuppeteer(), thornBloom(), sporecap(), patchworkHydra(), tinCrawler()));
    }

    /** Final boss: hunched scrap-metal colossus with six mechanical limbs, cable tail and a cluster of red optics. */
    static CreatureDef scrapSovereign() {
        int steel = 0x5b6168, dark = 0x2b2e33, rust = 0x8a4b2a, brass = 0xb08a3e, red = 0xff2a1a, cable = 0x1a1a1d, bone = 0xd9d2bf, glow = 0x6fd8ff;
        return CreatureDef.of("the_prototype", "The Prototype", "ザ・プロトタイプ").size(2.6f, 5.6f).hp(900).dmg(14).speed(0.3).kbRes(1.0).armor(10).egg(steel, red).boss()
                // legs (hydraulic, reverse-knee feel via foot offset)
                .part("legL", null, -8, 30, 2, 10, 30, 10, 0, -15, 0, dark).walk(0.6f, 0)
                .part("pistonL", "legL", 0, -8, -5.5f, 4, 14, 2, 0, 0, 0, brass)
                .part("footL", "legL", 0, -30, -2, 12, 4, 16, 0, 2, -2, steel)
                .part("legR", null, 8, 30, 2, 10, 30, 10, 0, -15, 0, dark).walk(0.6f, P)
                .part("pistonR", "legR", 0, -8, -5.5f, 4, 14, 2, 0, 0, 0, brass)
                .part("footR", "legR", 0, -30, -2, 12, 4, 16, 0, 2, -2, steel)
                // torso
                .part("body", null, 0, 30, 0, 30, 34, 20, 0, 17, 2, steel).sway(0.03f, 0.08f, 0, 0)
                .part("chestPlate", "body", 0, 20, -8.5f, 22, 16, 2, 0, 0, 0, rust)
                .part("core", "body", 0, 20, -9.8f, 8, 8, 1, 0, 0, 0, glow).glow()
                .part("rib1", "body", 0, 9, -8.6f, 24, 2, 2, 0, 0, 0, dark)
                .part("rib2", "body", 0, 5, -8.6f, 22, 2, 2, 0, 0, 0, dark)
                .part("rib3", "body", 0, 1, -8.6f, 20, 2, 2, 0, 0, 0, dark)
                .part("rivetL", "body", -9, 26, -9.8f, 2, 2, 1, 0, 0, 0, brass)
                .part("rivetR", "body", 9, 26, -9.8f, 2, 2, 1, 0, 0, 0, brass)
                .part("hump", "body", 0, 30, 8, 26, 10, 14, 0, 0, 0, dark)
                .part("pipeL", "body", -8, 34, 12, 4, 14, 4, 0, 7, 0, rust)
                .part("pipeR", "body", 8, 34, 12, 4, 18, 4, 0, 9, 0, rust)
                .part("ventL", "pipeL", 0, 14, 0, 3, 2, 3, 0, 1, 0, 0xff8a2a).glow()
                .part("ventR", "pipeR", 0, 18, 0, 3, 2, 3, 0, 1, 0, 0xff8a2a).glow()
                // head: armoured skull with optic cluster and steel teeth
                .part("head", "body", 0, 34, -8, 16, 14, 16, 0, 4, -6, dark)
                .part("faceplate", "head", 0, 6, -14.2f, 14, 8, 1, 0, 0, 0, bone)
                .part("optic1", "head", -4, 8, -14.9f, 3, 3, 1, 0, 0, 0, red).glow()
                .part("optic2", "head", 3, 9, -14.9f, 4, 4, 1, 0, 0, 0, red).glow()
                .part("optic3", "head", 0, 5, -14.9f, 2, 2, 1, 0, 0, 0, red).glow()
                .part("optic4", "head", 6, 5, -14.9f, 2, 2, 1, 0, 0, 0, red).glow()
                .part("optic5", "head", -6, 4, -14.9f, 2, 2, 1, 0, 0, 0, red).glow()
                .part("antenna", "head", 5, 11, -4, 1, 10, 1, 0, 5, 0, brass).sway(0.15f, 0.2f, 0, 2)
                .part("jaw", "head", 0, 0, -6, 14, 4, 14, 0, -2, -4, steel)
                .part("toothRow", "jaw", 0, 0, -11, 12, 3, 1, 0, 1.5f, 0, bone)
                .part("tusksL", "jaw", -6, 0, -10, 2, 5, 2, 0, 2, 0, bone)
                .part("tusksR", "jaw", 6, 0, -10, 2, 5, 2, 0, 2, 0, bone)
                // main claws
                .part("armL", "body", -18, 30, -2, 8, 40, 8, 0, -20, 0, steel).sway(0.05f, 0.09f, 0, 2)
                .part("elbowL", "armL", 0, -20, 0, 10, 4, 10, 0, 0, 0, brass)
                .part("clawL1", "armL", -3, -40, -2, 2, 12, 2, 0, -6, 0, bone)
                .part("clawL2", "armL", 0, -40, -3, 2, 14, 2, 0, -7, 0, bone)
                .part("clawL3", "armL", 3, -40, -2, 2, 12, 2, 0, -6, 0, bone)
                .part("armR", "body", 18, 30, -2, 8, 40, 8, 0, -20, 0, steel).sway(0.05f, 0.09f, P, 2)
                .part("elbowR", "armR", 0, -20, 0, 10, 4, 10, 0, 0, 0, brass)
                .part("clawR1", "armR", -3, -40, -2, 2, 12, 2, 0, -6, 0, bone)
                .part("clawR2", "armR", 0, -40, -3, 2, 14, 2, 0, -7, 0, bone)
                .part("clawR3", "armR", 3, -40, -2, 2, 12, 2, 0, -6, 0, bone)
                // long grabber arms from the back
                .part("armL2", "body", -12, 32, 8, 3, 46, 3, 0, -23, 0, cable).sway(0.12f, 0.13f, 1, 0)
                .part("pincerL", "armL2", 0, -46, 0, 6, 5, 4, 0, -2, 0, red).glow()
                .part("armR2", "body", 12, 32, 8, 3, 46, 3, 0, -23, 0, cable).sway(0.12f, 0.13f, 2, 0)
                .part("pincerR", "armR2", 0, -46, 0, 6, 5, 4, 0, -2, 0, red).glow()
                // small lower manipulators
                .part("armL3", "body", -16, 12, -6, 4, 22, 4, 0, -11, 0, rust).sway(0.2f, 0.25f, 0, 0)
                .part("armR3", "body", 16, 12, -6, 4, 22, 4, 0, -11, 0, rust).sway(0.2f, 0.25f, P, 0)
                // cable tail
                .part("tail", "body", 0, 6, 12, 4, 4, 30, 0, 0, 15, cable).sway(0.25f, 0.1f, 0, 1)
                .part("tailTip", "tail", 0, 0, 30, 6, 6, 4, 0, 0, 2, glow).glow()
                // claw sweep: shoulder winds back, whole torso twists through
                .atk(Atk.SWIPE, 13, 5f, 0, 30).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.4f, -1.2f, 0.6f, 0.8f, 1.6f, -0.2f, w, s, r); p.rot("armL", 0.3f, 0, -0.8f, 1.4f, 0.6f, -0.2f, w, s, r);
                    p.rot("body", 0.1f, -0.7f, 0.1f, -0.35f, 0.9f, -0.05f, w, s, r); p.rot("head", -0.2f, 0.5f, 0, 0.2f, -0.6f, 0, w, s, r);
                    p.rot("jaw", -0.3f, 0, 0, -0.6f, 0, 0, w, s, r); p.pos("body", 0, -1, 3, 0, -3, -6, w, s, r);
                    p.rot("legR", -0.4f, 0, 0, 0.5f, 0, 0, w, s, r); p.rot("armR3", 1.2f, 0, 0, 0.2f, 0, 0, w, s, r);
                })
                // signature: hand-grab pull - grabber arm coils back, then shoots out 3x length and drags the victim in
                .atk(Atk.WHIP, 10, 14f, 4f, 110).anim((p, e, w, s, r, age) -> {
                    p.rot("armR2", 3.0f, 0.6f, 0.4f, 1.6f, 0, 0.05f, w, s, r); p.scl("armR2", 1, 0.6f, 1, 1, 3.4f, 1, w, s, r);
                    p.scl("pincerR", 1, 1, 1, 1.8f, 1.8f, 1.8f, w, s, r);
                    p.rot("body", 0.25f, -0.5f, 0, -0.3f, 0.2f, 0, w, s, r); p.rot("head", 0.3f, 0.3f, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("armR", 0.2f, 0, 0.7f, -0.4f, 0, 0.3f, w, s, r); p.rot("armL", 0.6f, 0, -0.4f, 0.2f, 0, -0.2f, w, s, r);
                    if (r > 0) p.add("armR2", Mth.sin(age * 1.5f) * 0.2f * (1 - r), 0, 0);
                })
                // all six limbs close on a victim and crush
                .atk(Atk.GRAB, 9, 4f, 0, 170, 60, "").anim((p, e, w, s, r, age) -> {
                    for (String a : new String[]{"armL", "armR", "armL2", "armR2", "armL3", "armR3"}) {
                        float sg = a.startsWith("armL") ? -1 : 1;
                        p.rot(a, 1.8f, 0, 1.0f * sg, 1.45f, 0.3f * -sg, -0.25f * sg, w, s, r);
                    }
                    p.rot("body", 0.35f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("head", 0.4f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -0.9f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(age * 1.1f) * 0.1f; p.add("armL", q, 0, -q); p.add("armR", q, 0, q); p.add("body", q * 0.4f, 0, 0); }
                })
                // electric arcs from the core: crouches & charges, then discharges a lightning line
                .custom(11, 16f, 3f, 130, 18, 8, 12, EFx::arc, (p, e, w, s, r, age) -> {
                    p.rot("body", -0.25f, 0, 0, 0.35f, 0, 0, w, s, r); p.pos("body", 0, -4, 3, 0, 0, -3, w, s, r);
                    p.rot("armL", 0.6f, 0, -1.4f, 1.6f, 0.3f, -0.2f, w, s, r); p.rot("armR", 0.6f, 0, 1.4f, 1.6f, -0.3f, 0.2f, w, s, r);
                    p.rot("armL2", 2.6f, 0, -0.8f, 1.5f, 0, 0, w, s, r); p.rot("armR2", 2.6f, 0, 0.8f, 1.5f, 0, 0, w, s, r);
                    p.rot("head", -0.5f, 0, 0, 0.2f, 0, 0, w, s, r); p.rot("jaw", -0.6f, 0, 0, -0.8f, 0, 0, w, s, r);
                    p.scl("core", 1, 1, 1, 2.2f, 2.2f, 1.4f, w, s, r);
                    if (r <= 0) { float j = Mth.sin(age * 4f) * 0.06f * (w + s); p.add("body", j, j, -j); p.add("head", -j, j, j); }
                })
                // charge: drops low on all limbs and rams
                .atk(Atk.CHARGE, 15, 18f, 6f, 220).anim((p, e, w, s, r, age) -> {
                    p.rot("body", -0.2f, 0, 0, -0.9f, 0, 0, w, s, r); p.pos("body", 0, -2, 2, 0, -8, -4, w, s, r);
                    p.rot("armL", -0.8f, 0, -0.3f, 0.7f, 0, -0.1f, w, s, r); p.rot("armR", -0.8f, 0, 0.3f, 0.7f, 0, 0.1f, w, s, r);
                    p.rot("head", 0.3f, 0, 0, 0.7f, 0, 0, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.2f) * 0.9f; p.g("legL")[0] = q; p.g("legR")[0] = -q; p.add("armL", -q * 0.6f, 0, 0); p.add("armR", q * 0.6f, 0, 0); }
                })
                // overhead double-claw slam
                .atk(Atk.SLAM, 14, 6f, 0, 160).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 3.1f, 0, -0.4f, 0.3f, 0, 0.1f, w, s, r); p.rot("armR", 3.1f, 0, 0.4f, 0.3f, 0, -0.1f, w, s, r);
                    p.rot("armL2", 2.2f, 0, -0.6f, 0.6f, 0, 0, w, s, r); p.rot("armR2", 2.2f, 0, 0.6f, 0.6f, 0, 0, w, s, r);
                    p.rot("body", 0.5f, 0, 0, -0.8f, 0, 0, w, s, r); p.pos("body", 0, 4, 4, 0, -7, -8, w, s, r);
                    p.rot("head", 0.5f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.7f, 0, 0, w, s, r);
                })
                // summons scrap crawlers out of its hump
                .atk(Atk.SUMMON, 0, 20f, 0, 420, 3, "mini_prototype").anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.6f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("head", -0.6f, 0, 0, 0.3f, 0, 0, w, s, r);
                    p.rot("armL", 0.2f, 0, -1.2f, 0.5f, 0, -0.6f, w, s, r); p.rot("armR", 0.2f, 0, 1.2f, 0.5f, 0, 0.6f, w, s, r);
                    p.scl("hump", 1, 1, 1, 1.25f, 1.4f, 1.2f, w, s, r); p.rot("tail", 0.6f, 0, 0, -0.3f, 0, 0, w, s, r);
                    if (r <= 0) p.add("hump", 0, Mth.sin(age * 2.5f) * 0.1f, 0);
                })
                // mimicry: tilts its head and imitates a victim's voice, blinding nearby foes, then snaps forward
                .custom(7, 12f, 0, 300, 24, 6, 14, EFx::mimic, (p, e, w, s, r, age) -> {
                    p.rot("head", 0, 0, 0.6f, -0.4f, 0, 0, w, s, r); p.rot("jaw", -0.15f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("body", 0.15f, 0, 0, -0.45f, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, -2, -5, w, s, r);
                    p.rot("armL", 0.1f, 0, -0.2f, 1.2f, 0, -0.1f, w, s, r); p.rot("armR", 0.1f, 0, 0.2f, 1.2f, 0, 0.1f, w, s, r);
                    if (s <= 0) p.add("head", 0, 0, Mth.sin(age * 0.5f) * 0.15f * w);
                });
    }

    /** Lanky bandage-wrapped stalker with a stitched-shut mouth and long finger blades. */
    static CreatureDef ragStalker() {
        int cloth = 0xbfb39a, band = 0x8f8470, dark = 0x2a221c, stitch = 0xe8e0c8, eye = 0xf4f0ff, claw = 0x3b3b3b;
        return CreatureDef.of("nightmare_miss_delight", "Nightmare Miss Delight", "ナイトメア・ミスディライト").size(0.9f, 3.4f).hp(90).dmg(7).speed(0.34).kbRes(0.3).egg(cloth, dark).spawn(2)
                .part("legL", null, -3, 24, 0, 4, 24, 4, 0, -12, 0, cloth).walk(0.9f, 0)
                .part("wrapL", "legL", 0, -10, 0, 4.4f, 2, 4.4f, 0, 0, 0, band)
                .part("legR", null, 3, 24, 0, 4, 24, 4, 0, -12, 0, cloth).walk(0.9f, P)
                .part("wrapR", "legR", 0, -14, 0, 4.4f, 2, 4.4f, 0, 0, 0, band)
                .part("body", null, 0, 24, 0, 10, 18, 6, 0, 9, 0, cloth).sway(0.04f, 0.07f, 0, 2)
                .part("bandA", "body", 0, 5, 0, 10.4f, 2, 6.4f, 0, 0, 0, band)
                .part("bandB", "body", 0, 11, 0, 10.4f, 2, 6.4f, 0, 0, 0, band)
                .part("ribTear", "body", -2, 8, -3.2f, 3, 4, 1, 0, 0, 0, dark)
                .part("rag1", "body", 4, 0, 0, 2, 6, 1, 0, -3, -2, band).sway(0.2f, 0.2f, 0, 0)
                .part("rag2", "body", -3, 0, 2, 2, 8, 1, 0, -4, 0, band).sway(0.2f, 0.2f, 1, 0)
                .part("head", "body", 0, 18, 0, 9, 11, 9, 0, 5.5f, 0, cloth)
                .part("hood", "head", 0, 10, 1, 10, 3, 10, 0, 0, 0, band)
                .part("eyeL", "head", -2, 7, -4.6f, 2, 2, 1, 0, 0, 0, eye).glow()
                .part("eyeHole", "head", 2, 7, -4.6f, 2, 3, 1, 0, 0, 0, dark)
                .part("jaw", "head", 0, 2, -4.6f, 7, 1, 1, 0, 0, 0, dark)
                .part("st1", "jaw", -2.5f, 0, -0.3f, 0.6f, 3, 1, 0, 0, 0, stitch)
                .part("st2", "jaw", -0.8f, 0, -0.3f, 0.6f, 3, 1, 0, 0, 0, stitch)
                .part("st3", "jaw", 0.8f, 0, -0.3f, 0.6f, 3, 1, 0, 0, 0, stitch)
                .part("st4", "jaw", 2.5f, 0, -0.3f, 0.6f, 3, 1, 0, 0, 0, stitch)
                .part("armL", "body", -6.5f, 17, 0, 3, 30, 3, 0, -15, 0, cloth).sway(0.06f, 0.09f, 0, 2)
                .part("fL1", "armL", -1, -30, 0, 1, 7, 1, 0, -3.5f, 0, claw)
                .part("fL2", "armL", 0, -30, -1, 1, 8, 1, 0, -4, 0, claw)
                .part("fL3", "armL", 1, -30, 0, 1, 7, 1, 0, -3.5f, 0, claw)
                .part("armR", "body", 6.5f, 17, 0, 3, 30, 3, 0, -15, 0, cloth).sway(0.06f, 0.09f, P, 2)
                .part("fR1", "armR", -1, -30, 0, 1, 7, 1, 0, -3.5f, 0, claw)
                .part("fR2", "armR", 0, -30, -1, 1, 8, 1, 0, -4, 0, claw)
                .part("fR3", "armR", 1, -30, 0, 1, 7, 1, 0, -3.5f, 0, claw)
                // X-slash: both blade hands cross over the head then rake down
                .atk(Atk.SWIPE, 7, 3.6f, 0, 26).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 2.9f, 0.4f, -0.5f, 0.4f, -0.6f, 0.3f, w, s, r); p.rot("armR", 2.9f, -0.4f, 0.5f, 0.4f, 0.6f, -0.3f, w, s, r);
                    p.rot("body", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0.2f, -0.2f, 0, -0.1f, w, s, r);
                    p.pos("body", 0, 1, 2, 0, -2, -5, w, s, r); p.rot("legL", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r);
                })
                .atk(Atk.POUNCE, 8, 9f, 4f, 120).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -8, 0, 0, 2, -8, w, s, r); p.rot("body", 0.2f, 0, 0, -1.3f, 0, 0, w, s, r);
                    p.rot("armL", -1.0f, 0, -0.3f, 3.0f, 0, -0.2f, w, s, r); p.rot("armR", -1.0f, 0, 0.3f, 3.0f, 0, 0.2f, w, s, r);
                    p.rot("legL", 1.2f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("legR", 1.2f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("head", -0.3f, 0, 0, 0.6f, 0, 0, w, s, r);
                })
                // strangle: long arms wrap the throat
                .atk(Atk.GRAB, 5, 3f, 0, 180, 40, "").anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 1.4f, 0, -0.9f, 1.7f, 0.5f, 0.4f, w, s, r); p.rot("armR", 1.4f, 0, 0.9f, 1.7f, -0.5f, -0.4f, w, s, r);
                    p.rot("body", 0.2f, 0, 0, -0.35f, 0, 0, w, s, r); p.rot("head", 0, 0, 0.5f, 0.3f, 0, 0.4f, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(age * 1.6f) * 0.06f; p.add("armL", 0, 0, q); p.add("armR", 0, 0, -q); }
                })
                // muffled shriek through the stitches: head shakes, stitches strain
                .atk(Atk.SCREAM, 2, 8f, 2f, 260).anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.5f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("body", 0.4f, 0, 0, -0.1f, 0, 0, w, s, r);
                    p.rot("armL", 0.3f, 0, -0.3f, 0.6f, 0, -1.2f, w, s, r); p.rot("armR", 0.3f, 0, 0.3f, 0.6f, 0, 1.2f, w, s, r);
                    p.scl("jaw", 1, 1, 1, 1.1f, 3f, 1, w, s, r);
                    if (s > 0 && r <= 0) p.add("head", 0, Mth.sin(age * 3.5f) * 0.25f, 0);
                });
    }

    /** Four-legged clockwork hound: brass gears on its flanks, piston jaw, steam tail. */
    static CreatureDef cogHound() {
        int brass = 0xb98b3a, iron = 0x3d3f44, cog = 0xd8b45a, eye = 0x4dffb2, steam = 0xdddddd, tooth = 0xeae4d0;
        return CreatureDef.of("pianosaurus", "Pianosaurus", "ピアノサウルス").size(1.0f, 1.3f).hp(50).dmg(6).speed(0.38).kbRes(0.2).egg(brass, eye).spawn(2)
                .part("body", null, 0, 13, 0, 10, 9, 22, 0, 2, 0, brass)
                .part("spine", "body", 0, 7, 0, 3, 2, 20, 0, 0, 0, iron)
                .part("cogL", "body", -5.5f, 2, 2, 1, 7, 7, 0, 0, 0, cog).sway(3f, 0.15f, 0, 0)
                .part("cogR", "body", 5.5f, 2, -3, 1, 6, 6, 0, 0, 0, cog).sway(3f, 0.15f, 1, 0)
                .part("bellyGrate", "body", 0, -2.6f, 0, 8, 1, 14, 0, 0, 0, iron)
                .part("legL", "body", -4, -2, -8, 3, 11, 3, 0, -5.5f, 0, iron).walk(1.0f, 0)
                .part("legR", "body", 4, -2, -8, 3, 11, 3, 0, -5.5f, 0, iron).walk(1.0f, P)
                .part("legL2", "body", -4, -2, 8, 3, 11, 3, 0, -5.5f, 0, iron).walk(1.0f, P)
                .part("legR2", "body", 4, -2, 8, 3, 11, 3, 0, -5.5f, 0, iron).walk(1.0f, 0)
                .part("pawL", "legL", 0, -11, -1, 4, 2, 5, 0, 1, 0, brass)
                .part("pawR", "legR", 0, -11, -1, 4, 2, 5, 0, 1, 0, brass)
                .part("pawL2", "legL2", 0, -11, -1, 4, 2, 5, 0, 1, 0, brass)
                .part("pawR2", "legR2", 0, -11, -1, 4, 2, 5, 0, 1, 0, brass)
                .part("head", "body", 0, 5, -11, 9, 8, 9, 0, 0, -4, brass)
                .part("snout", "head", 0, -1, -8, 6, 4, 5, 0, 0, -2, iron)
                .part("eyeL", "head", -3, 2, -8.6f, 2, 2, 1, 0, 0, 0, eye).glow()
                .part("eyeR", "head", 3, 2, -8.6f, 2, 2, 1, 0, 0, 0, eye).glow()
                .part("earL", "head", -3, 4, -2, 2, 5, 2, 0, 2.5f, 0, iron).sway(0.15f, 0.2f, 0, 2)
                .part("earR", "head", 3, 4, -2, 2, 5, 2, 0, 2.5f, 0, iron).sway(0.15f, 0.2f, P, 2)
                .part("jaw", "head", 0, -3, -6, 6, 2, 7, 0, -1, -3, iron)
                .part("fangL", "jaw", -2, 0, -6, 1, 2, 1, 0, 1, 0, tooth)
                .part("fangR", "jaw", 2, 0, -6, 1, 2, 1, 0, 1, 0, tooth)
                .part("tail", "body", 0, 5, 11, 2, 2, 8, 0, 0, 4, iron).sway(0.4f, 0.3f, 0, 1)
                .part("puff", "tail", 0, 0, 8, 3, 3, 3, 0, 0, 1, steam).sway(0.2f, 0.5f, 0, 2)
                // snap: head cocks back, lunges, piston-jaw clamps
                .atk(Atk.BITE, 6, 2.6f, 0, 22).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r); p.rot("jaw", -0.9f, 0, 0, 0.05f, 0, 0, w, s, r);
                    p.pos("head", 0, 1, 2, 0, -1, -5, w, s, r); p.pos("body", 0, -1, 3, 0, 0, -5, w, s, r);
                    p.rot("legL", -0.6f, 0, 0, 0.7f, 0, 0, w, s, r); p.rot("legR", -0.6f, 0, 0, 0.7f, 0, 0, w, s, r);
                })
                .atk(Atk.POUNCE, 7, 8f, 3f, 90).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -4, 2, 0, 3, -6, w, s, r); p.rot("body", 0.3f, 0, 0, -0.4f, 0, 0, w, s, r);
                    p.rot("legL", 0.4f, 0, 0, -1.3f, 0, 0, w, s, r); p.rot("legR", 0.4f, 0, 0, -1.3f, 0, 0, w, s, r);
                    p.rot("legL2", -0.6f, 0, 0, 1.2f, 0, 0, w, s, r); p.rot("legR2", -0.6f, 0, 0, 1.2f, 0, 0, w, s, r);
                    p.rot("jaw", 0, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("tail", 0.5f, 0, 0, -0.6f, 0, 0, w, s, r);
                })
                // full-steam dash
                .atk(Atk.CHARGE, 7, 12f, 5f, 160).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -3, 2, 0, -1, -2, w, s, r); p.rot("head", 0.3f, 0, 0, 0.1f, 0, 0, w, s, r);
                    p.scl("puff", 1, 1, 1, 2.5f, 2.5f, 2.5f, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.8f) * 1.2f; p.g("legL")[0] = q; p.g("legR")[0] = q; p.g("legL2")[0] = -q; p.g("legR2")[0] = -q; }
                });
    }

    /** Spider made of tangled copper wire: bulb abdomen with glowing filaments, six eyes, pincer fangs, eight legs. */
    static CreatureDef wireWidow() {
        int wire = 0xb5652a, dark = 0x2a1c14, bulb = 0xf2ecc8, fil = 0xffc24a, eye = 0xff3b3b, fang = 0xd8d8d8;
        CreatureDef d = CreatureDef.of("nightmare_mommy", "Nightmare Mommy Long Legs", "ナイトメア・マミー").size(1.6f, 1.0f).hp(60).dmg(6).speed(0.36).kbRes(0.3).egg(wire, fil).spawn(1)
                .part("body", null, 0, 9, 0, 8, 6, 8, 0, 0, -2, dark)
                .part("abdomen", "body", 0, 2, 6, 11, 9, 12, 0, 0, 3, bulb).sway(0.05f, 0.1f, 0, 0)
                .part("filament1", "abdomen", -2, 0, 3, 1, 6, 1, 0, 0, 0, fil).glow()
                .part("filament2", "abdomen", 2, 0, 3, 1, 6, 1, 0, 0, 0, fil).glow()
                .part("cap", "abdomen", 0, 0, 9.5f, 6, 6, 2, 0, 0, 0, wire)
                .part("head", "body", 0, 1, -6, 6, 5, 5, 0, 0, -2, wire)
                .part("eye1", "head", -1.5f, 1.5f, -4.6f, 1, 1, 1, 0, 0, 0, eye).glow()
                .part("eye2", "head", 1.5f, 1.5f, -4.6f, 1, 1, 1, 0, 0, 0, eye).glow()
                .part("eye3", "head", -2.5f, 0, -4.6f, 1, 1, 1, 0, 0, 0, eye).glow()
                .part("eye4", "head", 2.5f, 0, -4.6f, 1, 1, 1, 0, 0, 0, eye).glow()
                .part("eye5", "head", 0, 2.5f, -4.6f, 1, 1, 1, 0, 0, 0, eye).glow()
                .part("eye6", "head", 0, 0.5f, -4.6f, 1.4f, 1.4f, 1, 0, 0, 0, eye).glow()
                .part("jaw", "head", 0, -2, -4, 5, 1, 2, 0, 0, 0, dark)
                .part("fangL", "jaw", -1.5f, 0, -1, 1, 3, 1, 0, -1.5f, 0, fang)
                .part("fangR", "jaw", 1.5f, 0, -1, 1, 3, 1, 0, -1.5f, 0, fang)
                .part("armL", "head", -2, -1, -4, 1.5f, 6, 1.5f, 0, -2, -1, wire)
                .part("armR", "head", 2, -1, -4, 1.5f, 6, 1.5f, 0, -2, -1, wire);
        for (int i = 0; i < 4; i++) {
            float z = -5 + i * 3.2f, ph = (i % 2) * P;
            for (int sg = -1; sg <= 1; sg += 2) {
                String n = (sg < 0 ? "legL" : "legR") + (i + 1);
                d.part(n, "body", sg * 4, 0, z, 10, 1.5f, 1.5f, sg * 5, 2, 0, wire).walk(0.6f, ph + (sg > 0 ? P : 0))
                 .part(n + "f", n, sg * 10, 3, 0, 1.5f, 12, 1.5f, 0, -6, 0, dark);
            }
        }
        return d
                .atk(Atk.BITE, 6, 2.4f, 0, 20).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 2, 2, 0, -1, -4, w, s, r);
                    p.rot("fangL", 0, 0, 0.7f, 0.3f, 0, -0.3f, w, s, r); p.rot("fangR", 0, 0, -0.7f, 0.3f, 0, 0.3f, w, s, r);
                    p.rot("armL", 1.5f, 0, -0.5f, 0.2f, 0, 0.3f, w, s, r); p.rot("armR", 1.5f, 0, 0.5f, 0.2f, 0, -0.3f, w, s, r);
                    p.rot("legL1", 0, 0, -0.8f, 0, 0, 0.2f, w, s, r); p.rot("legR1", 0, 0, 0.8f, 0, 0, -0.2f, w, s, r);
                })
                // spits a sticky wire-web glob from the abdomen: abdomen tips up over the body like a scorpion
                .atk(Atk.SHOOT, 3, 14f, 4f, 80, 0xe8e8e8, "slow").anim((p, e, w, s, r, age) -> {
                    p.rot("abdomen", -1.4f, 0, 0, -0.9f, 0, 0, w, s, r); p.pos("abdomen", 0, 4, -2, 0, 5, -4, w, s, r);
                    p.rot("body", -0.25f, 0, 0, 0.1f, 0, 0, w, s, r); p.scl("abdomen", 1, 1, 1, 0.85f, 1.1f, 0.85f, w, s, r);
                })
                .atk(Atk.POUNCE, 7, 8f, 3f, 100).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -3, 0, 0, 3, -5, w, s, r);
                    p.rot("legL1", 0, 0, 0.5f, -1.0f, 0, -0.6f, w, s, r); p.rot("legR1", 0, 0, -0.5f, -1.0f, 0, 0.6f, w, s, r);
                    p.rot("legL2", 0, 0, 0.5f, -0.6f, 0, -0.5f, w, s, r); p.rot("legR2", 0, 0, -0.5f, -0.6f, 0, 0.5f, w, s, r);
                    p.rot("fangL", 0, 0, 0.6f, 0, 0, 0.6f, w, s, r); p.rot("fangR", 0, 0, -0.6f, 0, 0, -0.6f, w, s, r);
                })
                // live-wire snare: shoots a wire line that hooks and reels the target in
                .atk(Atk.WHIP, 4, 10f, 3f, 140).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.5f, 0, 0.4f, 1.57f, 0, 0, w, s, r); p.scl("armR", 1, 1, 1, 1, 4f, 1, w, s, r);
                    p.rot("body", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r); p.rot("legL1", 0, 0, 0.4f, 0, 0, 0, w, s, r);
                });
    }

    /** Hulking furnace-bellied brute: glowing grate belly, chimney on its back, riveted fists. */
    static CreatureDef furnaceBrute() {
        int iron = 0x45403c, soot = 0x1e1b19, fire = 0xff7a1a, ember = 0xffd34a, rivet = 0x9a8f80, eye = 0xffe08a;
        return CreatureDef.of("nightmare_bron", "Nightmare Bron", "ナイトメア・ブロン").size(1.8f, 3.0f).hp(140).dmg(9).speed(0.25).kbRes(0.8).armor(6).egg(iron, fire).spawn(1).fireImmune()
                .part("legL", null, -6, 16, 0, 8, 16, 8, 0, -8, 0, soot).walk(0.7f, 0)
                .part("legR", null, 6, 16, 0, 8, 16, 8, 0, -8, 0, soot).walk(0.7f, P)
                .part("bootL", "legL", 0, -16, -1, 9, 3, 11, 0, 1.5f, 0, iron)
                .part("bootR", "legR", 0, -16, -1, 9, 3, 11, 0, 1.5f, 0, iron)
                .part("body", null, 0, 16, 0, 24, 24, 16, 0, 12, 0, iron)
                .part("grate", "body", 0, 9, -8.2f, 14, 10, 1, 0, 0, 0, fire).glow()
                .part("bar1", "body", -4, 9, -8.8f, 1.5f, 10, 1, 0, 0, 0, soot)
                .part("bar2", "body", 0, 9, -8.8f, 1.5f, 10, 1, 0, 0, 0, soot)
                .part("bar3", "body", 4, 9, -8.8f, 1.5f, 10, 1, 0, 0, 0, soot)
                .part("rv1", "body", -10, 20, -8.4f, 2, 2, 1, 0, 0, 0, rivet)
                .part("rv2", "body", 10, 20, -8.4f, 2, 2, 1, 0, 0, 0, rivet)
                .part("rv3", "body", -10, 3, -8.4f, 2, 2, 1, 0, 0, 0, rivet)
                .part("rv4", "body", 10, 3, -8.4f, 2, 2, 1, 0, 0, 0, rivet)
                .part("chimney", "body", 6, 24, 5, 5, 12, 5, 0, 6, 0, soot)
                .part("smoke", "chimney", 0, 12, 0, 4, 3, 4, 0, 2, 0, ember).glow().sway(0.3f, 0.4f, 0, 1)
                .part("head", "body", 0, 24, -3, 10, 8, 9, 0, 3, -2, iron)
                .part("brow", "head", 0, 6, -6.6f, 11, 2, 2, 0, 0, 0, soot)
                .part("eyeL", "head", -2.5f, 4, -6.6f, 2, 1.5f, 1, 0, 0, 0, eye).glow()
                .part("eyeR", "head", 2.5f, 4, -6.6f, 2, 1.5f, 1, 0, 0, 0, eye).glow()
                .part("jaw", "head", 0, 0, -3, 9, 3, 8, 0, -1, -2, soot)
                .part("mouthGlow", "jaw", 0, 0.5f, -6.1f, 7, 1, 1, 0, 0, 0, fire).glow()
                .part("armL", "body", -14, 22, 0, 7, 22, 7, 0, -11, 0, iron).sway(0.04f, 0.08f, 0, 2)
                .part("fistL", "armL", 0, -22, 0, 10, 9, 10, 0, -4, 0, soot)
                .part("knuckL", "fistL", 0, -4, -5.2f, 8, 2, 1, 0, 0, 0, rivet)
                .part("armR", "body", 14, 22, 0, 7, 22, 7, 0, -11, 0, iron).sway(0.04f, 0.08f, P, 2)
                .part("fistR", "armR", 0, -22, 0, 10, 9, 10, 0, -4, 0, soot)
                .part("knuckR", "fistR", 0, -4, -5.2f, 8, 2, 1, 0, 0, 0, rivet)
                // jab-hook combo with the right fist
                .atk(Atk.SWIPE, 9, 3.5f, 0, 28).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 0.9f, -0.6f, 0.8f, 1.6f, 0.8f, -0.1f, w, s, r); p.rot("armL", 1.2f, 0.3f, -0.3f, 0.3f, 0, -0.4f, w, s, r);
                    p.rot("body", 0, -0.5f, 0, -0.15f, 0.6f, 0, w, s, r); p.pos("body", 0, 0, 2, 0, -1, -4, w, s, r); p.rot("legR", -0.3f, 0, 0, 0.4f, 0, 0, w, s, r);
                })
                // double-fist hammer: grate flares on impact
                .atk(Atk.SLAM, 11, 4.5f, 0, 120).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 3.0f, 0, -0.5f, 0.6f, 0, 0.3f, w, s, r); p.rot("armR", 3.0f, 0, 0.5f, 0.6f, 0, -0.3f, w, s, r);
                    p.rot("body", 0.4f, 0, 0, -0.6f, 0, 0, w, s, r); p.pos("body", 0, 2, 3, 0, -4, -4, w, s, r);
                    p.scl("grate", 1, 1, 1, 1.15f, 1.15f, 1.5f, w, s, r); p.rot("head", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r);
                })
                .atk(Atk.STOMP, 7, 4f, 0, 140)
                // belches a fireball from its belly grate
                .atk(Atk.SHOOT, 7, 14f, 4f, 70, 0xff6a10, "fire").anim((p, e, w, s, r, age) -> {
                    p.rot("body", -0.35f, 0, 0, 0.25f, 0, 0, w, s, r); p.scl("grate", 1, 1, 1, 1.4f, 1.4f, 2.5f, w, s, r);
                    p.rot("armL", 0.2f, 0, -1.0f, 0.5f, 0, -0.5f, w, s, r); p.rot("armR", 0.2f, 0, 1.0f, 0.5f, 0, 0.5f, w, s, r);
                    p.rot("jaw", -0.2f, 0, 0, -0.7f, 0, 0, w, s, r);
                })
                // vents choking soot from the chimney
                .atk(Atk.GAS, 2, 6f, 0, 260).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.4f, 0, 0, -0.1f, 0, 0, w, s, r); p.scl("smoke", 1, 1, 1, 3f, 4f, 3f, w, s, r); p.scl("body", 1, 1, 1, 1.08f, 0.96f, 1.08f, w, s, r);
                    if (s > 0 && r <= 0) p.add("body", 0, 0, Mth.sin(age * 2f) * 0.05f);
                });
    }

    /** Walking specimen jar: glass tank on spindly brass legs, a one-eyed goo blob inside with tentacles spilling out the lid. */
    static CreatureDef jarWraith() {
        int glass = 0xa8e6e0, goo = 0x58c23a, gooD = 0x2f7a1e, lid = 0x7a7a80, brass = 0xa98238, eye = 0xfff36a, pupil = 0x101010;
        return CreatureDef.of("harley_sawyer", "Dr. Harley Sawyer", "ハーレー・ソーヤー博士").size(1.2f, 2.4f).hp(70).dmg(6).speed(0.27).kbRes(0.4).egg(glass, goo).spawn(1)
                .part("legL", null, -5, 10, 0, 2, 10, 2, 0, -5, 0, brass).walk(1.0f, 0)
                .part("legR", null, 5, 10, 0, 2, 10, 2, 0, -5, 0, brass).walk(1.0f, P)
                .part("legL2", null, -5, 10, 5, 2, 10, 2, 0, -5, 0, brass).walk(1.0f, P)
                .part("legR2", null, 5, 10, 5, 2, 10, 2, 0, -5, 0, brass).walk(1.0f, 0)
                .part("body", null, 0, 10, 2, 16, 3, 16, 0, 1.5f, 0, lid)
                .part("glassF", "body", 0, 3, -7.5f, 14, 18, 1, 0, 9, 0, glass)
                .part("glassB", "body", 0, 3, 7.5f, 14, 18, 1, 0, 9, 0, glass)
                .part("glassL", "body", -7.5f, 3, 0, 1, 18, 14, 0, 9, 0, glass)
                .part("glassR", "body", 7.5f, 3, 0, 1, 18, 14, 0, 9, 0, glass)
                .part("blob", "body", 0, 3, 0, 11, 11, 11, 0, 5.5f, 0, goo).sway(0.08f, 0.15f, 0, 1)
                .part("blobTop", "blob", 0, 11, 0, 8, 4, 8, 0, 2, 0, gooD)
                .part("head", "blob", 0, 6, -5.6f, 6, 6, 1, 0, 0, 0, eye).glow()
                .part("pupil", "head", 0, 0, -0.6f, 2, 4, 1, 0, 0, 0, pupil)
                .part("jaw", "blob", 0, 1, -5.6f, 6, 2, 1, 0, 0, 0, gooD)
                .part("lid", "body", 0, 21, 0, 17, 3, 17, 0, 1.5f, 0, lid)
                .part("knob", "lid", 0, 3, 0, 4, 2, 4, 0, 1, 0, brass)
                .part("label", "body", 0, 9, -8.2f, 8, 4, 1, 0, 0, 0, 0xe9dfc0)
                .part("tag", "label", 0, 0, -0.6f, 6, 1, 1, 0, 0, 0, 0x5a2a2a)
                .part("armL", "lid", -6, 2, -6, 3, 16, 3, 0, 8, 0, goo).sway(0.4f, 0.13f, 0, 2)
                .part("armLtip", "armL", 0, 16, 0, 4, 4, 4, 0, 2, 0, gooD)
                .part("armR", "lid", 6, 2, -6, 3, 16, 3, 0, 8, 0, goo).sway(0.4f, 0.13f, P, 2)
                .part("armRtip", "armR", 0, 16, 0, 4, 4, 4, 0, 2, 0, gooD)
                .part("tail", "lid", 0, 2, 6, 2, 12, 2, 0, 6, 0, goo).sway(0.5f, 0.2f, 1, 0)
                .part("bubble1", "blob", -3, 2, -5.4f, 1.5f, 1.5f, 1, 0, 0, 0, 0xd9ffc8).glow()
                .part("bubble2", "blob", 3, 8, -5.4f, 1, 1, 1, 0, 0, 0, 0xd9ffc8).glow()
                // tentacles curl back over the lid then whip down
                .atk(Atk.SWIPE, 6, 3.4f, 0, 26).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", -0.9f, 0, -0.4f, -2.6f, 0, 0.2f, w, s, r); p.rot("armR", -0.9f, 0, 0.4f, -2.6f, 0, -0.2f, w, s, r);
                    p.rot("body", 0.15f, 0, 0, -0.2f, 0, 0, w, s, r); p.scl("blob", 1, 1, 1, 1.1f, 0.9f, 1.1f, w, s, r);
                })
                // goo spit: blob squishes and fires through the open lid
                .atk(Atk.SHOOT, 5, 13f, 3f, 60, 0x6cff3a, "poison").anim((p, e, w, s, r, age) -> {
                    p.scl("blob", 1, 1, 1, 1.25f, 0.75f, 1.25f, w, s, r); p.rot("lid", -0.6f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.rot("body", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r); p.scl("head", 1, 1, 1, 0.6f, 1.3f, 1, w, s, r);
                })
                // lid pops, toxic fumes spill out
                .atk(Atk.GAS, 2, 5f, 0, 220, 1, "").anim((p, e, w, s, r, age) -> {
                    p.pos("lid", 0, 2, 0, 0, 6, 0, w, s, r); p.rot("lid", -0.3f, 0, 0.3f, -0.5f, 0, 0.4f, w, s, r);
                    p.scl("blob", 1, 1, 1, 1.2f, 1.2f, 1.2f, w, s, r);
                    if (s > 0 && r <= 0) p.add("lid", 0, Mth.sin(age * 2f) * 0.3f, 0);
                })
                // headbutts with the whole jar
                .atk(Atk.CHARGE, 7, 10f, 4f, 180).anim((p, e, w, s, r, age) -> {
                    p.rot("body", -0.2f, 0, 0, -0.45f, 0, 0, w, s, r); p.rot("armL", 0, 0, 0, 1.2f, 0, -0.3f, w, s, r); p.rot("armR", 0, 0, 0, 1.2f, 0, 0.3f, w, s, r);
                    if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.6f) * 1.0f; p.g("legL")[0] = q; p.g("legR")[0] = -q; p.g("legL2")[0] = -q; p.g("legR2")[0] = q; }
                });
    }

    /** Wooden marionette that pulls its own strings: control cross floating above its head, ball joints, painted grin. */
    static CreatureDef stringPuppeteer() {
        int wood = 0xc49a6c, joint = 0x6b4a2e, paint = 0xb3202a, white = 0xf2ead8, str = 0xe6e6e6, coat = 0x3b2a5c, eye = 0x1a1a1a;
        return CreatureDef.of("nightmare_prototype", "Nightmare Prototype", "ナイトメア・プロトタイプ").size(0.9f, 2.6f).hp(100).dmg(7).speed(0.3).kbRes(0.3).egg(wood, coat).spawn(1)
                .part("legL", null, -3, 18, 0, 3, 18, 3, 0, -9, 0, wood).walk(1.1f, 0)
                .part("kneeL", "legL", 0, -9, 0, 4, 3, 4, 0, 0, 0, joint)
                .part("legR", null, 3, 18, 0, 3, 18, 3, 0, -9, 0, wood).walk(1.1f, P)
                .part("kneeR", "legR", 0, -9, 0, 4, 3, 4, 0, 0, 0, joint)
                .part("body", null, 0, 18, 0, 10, 14, 6, 0, 7, 0, coat).sway(0.08f, 0.1f, 0, 2)
                .part("btn1", "body", 0, 10, -3.2f, 1.5f, 1.5f, 1, 0, 0, 0, white)
                .part("btn2", "body", 0, 6, -3.2f, 1.5f, 1.5f, 1, 0, 0, 0, white)
                .part("collar", "body", 0, 14, 0, 12, 2, 8, 0, 0, 0, white)
                .part("head", "body", 0, 15, 0, 8, 9, 8, 0, 4.5f, 0, wood)
                .part("cheekL", "head", -3, 3, -4.2f, 2, 1.5f, 1, 0, 0, 0, paint)
                .part("cheekR", "head", 3, 3, -4.2f, 2, 1.5f, 1, 0, 0, 0, paint)
                .part("eyeL", "head", -2, 6, -4.2f, 2, 2, 1, 0, 0, 0, eye)
                .part("eyeR", "head", 2, 6, -4.2f, 2, 2, 1, 0, 0, 0, eye)
                .part("glintL", "eyeL", 0.4f, 0.4f, -0.3f, 0.6f, 0.6f, 1, 0, 0, 0, 0xff3030).glow()
                .part("glintR", "eyeR", 0.4f, 0.4f, -0.3f, 0.6f, 0.6f, 1, 0, 0, 0, 0xff3030).glow()
                .part("jaw", "head", 0, 1, -4.2f, 6, 2, 1, 0, -0.5f, 0, paint)
                .part("hinge", "head", -3.6f, 1, -3.6f, 1, 3, 1, 0, -1, 0, joint)
                .part("hinge2", "head", 3.6f, 1, -3.6f, 1, 3, 1, 0, -1, 0, joint)
                .part("cross", "head", 0, 22, 0, 16, 2, 2, 0, 0, 0, joint).sway(0.15f, 0.07f, 0, 2)
                .part("cross2", "cross", 0, 0, 0, 2, 2, 12, 0, 0, 0, joint)
                .part("strHead", "cross", 0, 0, 0, 0.5f, 13, 0.5f, 0, -6.5f, 0, str)
                .part("strL", "cross", -7, 0, 0, 0.5f, 20, 0.5f, 0, -10, 0, str)
                .part("strR", "cross", 7, 0, 0, 0.5f, 20, 0.5f, 0, -10, 0, str)
                .part("armL", "body", -6.5f, 13, 0, 3, 16, 3, 0, -8, 0, wood).sway(0.15f, 0.1f, 0, 0)
                .part("elbowL", "armL", 0, -8, 0, 4, 3, 4, 0, 0, 0, joint)
                .part("handL", "armL", 0, -16, 0, 4, 4, 2, 0, -2, 0, white)
                .part("armR", "body", 6.5f, 13, 0, 3, 16, 3, 0, -8, 0, wood).sway(0.15f, 0.1f, P, 0)
                .part("elbowR", "armR", 0, -8, 0, 4, 3, 4, 0, 0, 0, joint)
                .part("handR", "armR", 0, -16, 0, 4, 4, 2, 0, -2, 0, white)
                // jerky puppet chop: limp anticipation, then snapped up by the strings
                .atk(Atk.SWIPE, 7, 3.2f, 0, 24).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", -0.3f, 0, 0.1f, 2.4f, 0.5f, -0.3f, w, s, r); p.rot("armL", -0.3f, 0, -0.1f, 0.4f, 0, -0.2f, w, s, r);
                    p.rot("head", 0.5f, 0, 0.6f, -0.2f, 0, -0.3f, w, s, r); p.rot("body", 0.3f, 0, 0.2f, -0.2f, 0.4f, 0, w, s, r);
                    p.pos("body", 0, -2, 0, 0, 1, -3, w, s, r);
                })
                // flings a string from its fingertips that yanks prey in
                .atk(Atk.WHIP, 6, 11f, 3f, 100).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.8f, 0, 0.6f, 1.57f, 0, 0, w, s, r); p.scl("armR", 1, 1, 1, 0.6f, 3.2f, 0.6f, w, s, r);
                    p.rot("cross", 0, 0, -0.4f, 0, 0, 0.3f, w, s, r); p.rot("body", 0.2f, -0.4f, 0, -0.3f, 0.3f, 0, w, s, r);
                })
                // ties the victim up in strings and dances it around
                .atk(Atk.GRAB, 6, 3f, 0, 200, 50, "").anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 2.2f, 0, -0.8f, 1.6f, 0, 0.2f, w, s, r); p.rot("armR", 2.2f, 0, 0.8f, 1.6f, 0, -0.2f, w, s, r);
                    p.rot("head", 0, 0, 0.4f, 0.2f, 0, -0.3f, w, s, r);
                    if (s >= 1 && r <= 0) { float q = Mth.sin(age * 0.7f); p.add("armL", q * 0.3f, 0, 0); p.add("armR", -q * 0.3f, 0, 0); p.add("cross", 0, 0, q * 0.3f); p.add("head", 0, 0, q * 0.2f); }
                })
                // jerks the cross and summons tin crawlers like puppets
                .atk(Atk.SUMMON, 0, 16f, 0, 400, 2, "mini_prototype").anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 3.0f, 0, -0.4f, 2.0f, 0, -0.9f, w, s, r); p.rot("armR", 3.0f, 0, 0.4f, 2.0f, 0, 0.9f, w, s, r);
                    p.pos("cross", 0, 4, 0, 0, -2, 0, w, s, r); p.rot("head", -0.4f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.8f, 0, 0, w, s, r);
                });
    }

    /** Mutated carnivorous plant: thick stalk, fanged bulb head ringed by petals, thorny vine arms, root feet. */
    static CreatureDef thornBloom() {
        int stalk = 0x3f7a2c, dark = 0x23461a, petal = 0xc8326e, inner = 0x5a0f1e, tooth = 0xf3edd5, thorn = 0xd6c27a, pollen = 0xffe14a;
        return CreatureDef.of("kissy_missy", "Kissy Missy", "キッシーミッシー").size(1.4f, 3.2f).hp(110).dmg(8).speed(0.22).kbRes(0.6).egg(stalk, petal).spawn(1)
                .part("legL", null, -4, 8, 0, 5, 8, 5, 0, -4, 0, dark).walk(0.6f, 0)
                .part("rootL", "legL", -2, -8, -2, 8, 2, 3, 0, 1, 0, dark)
                .part("legR", null, 4, 8, 0, 5, 8, 5, 0, -4, 0, dark).walk(0.6f, P)
                .part("rootR", "legR", 2, -8, -2, 8, 2, 3, 0, 1, 0, dark)
                .part("body", null, 0, 8, 0, 8, 26, 8, 0, 13, 0, stalk).sway(0.06f, 0.06f, 0, 2)
                .part("leafL", "body", -4, 10, 0, 8, 1, 5, -4, 0, 0, stalk).sway(0.2f, 0.12f, 0, 2)
                .part("leafR", "body", 4, 16, 0, 8, 1, 5, 4, 0, 0, stalk).sway(0.2f, 0.12f, P, 2)
                .part("thorn1", "body", 4.5f, 6, -2, 2, 1, 1, 0, 0, 0, thorn)
                .part("thorn2", "body", -4.5f, 18, 1, 2, 1, 1, 0, 0, 0, thorn)
                .part("thorn3", "body", 0, 22, -4.5f, 1, 1, 2, 0, 0, 0, thorn)
                .part("head", "body", 0, 26, 0, 14, 12, 14, 0, 6, -2, petal)
                .part("pet1", "head", 0, 14, -2, 6, 5, 6, 0, 2, 0, petal)
                .part("pet2", "head", -8, 8, -2, 3, 8, 8, 0, 0, 0, petal).sway(0.15f, 0.2f, 0, 2)
                .part("pet3", "head", 8, 8, -2, 3, 8, 8, 0, 0, 0, petal).sway(0.15f, 0.2f, P, 2)
                .part("pet4", "head", 0, 8, 6.5f, 10, 8, 3, 0, 0, 0, petal)
                .part("maw", "head", 0, 7, -9.1f, 10, 6, 1, 0, 0, 0, inner)
                .part("tTop1", "head", -3, 9, -9.5f, 1.5f, 3, 1, 0, -1, 0, tooth)
                .part("tTop2", "head", 0, 9, -9.5f, 1.5f, 3, 1, 0, -1, 0, tooth)
                .part("tTop3", "head", 3, 9, -9.5f, 1.5f, 3, 1, 0, -1, 0, tooth)
                .part("jaw", "head", 0, 3, -2, 14, 4, 14, 0, -1, -2, petal)
                .part("tBot1", "jaw", -2, 0, -9.5f, 1.5f, 3, 1, 0, 1, 0, tooth)
                .part("tBot2", "jaw", 2, 0, -9.5f, 1.5f, 3, 1, 0, 1, 0, tooth)
                .part("stamen", "head", 0, 10, -6, 2, 2, 2, 0, 0, 0, pollen).glow()
                .part("armL", "body", -4.5f, 22, 0, 3, 24, 3, 0, -12, 0, stalk).sway(0.15f, 0.1f, 0, 2)
                .part("barbL", "armL", -2, -14, 0, 2, 1, 1, 0, 0, 0, thorn)
                .part("tipL", "armL", 0, -24, 0, 4, 4, 4, 0, -2, 0, petal)
                .part("armR", "body", 4.5f, 22, 0, 3, 24, 3, 0, -12, 0, stalk).sway(0.15f, 0.1f, P, 2)
                .part("barbR", "armR", 2, -14, 0, 2, 1, 1, 0, 0, 0, thorn)
                .part("tipR", "armR", 0, -24, 0, 4, 4, 4, 0, -2, 0, petal)
                // bulb rears back, petals flare, snaps down onto the target
                .atk(Atk.BITE, 9, 3.2f, 0, 26).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.7f, 0, 0, 0.8f, 0, 0, w, s, r); p.rot("jaw", -1.0f, 0, 0, 0f, 0, 0, w, s, r);
                    p.rot("body", 0.3f, 0, 0, -0.6f, 0, 0, w, s, r); p.pos("head", 0, 2, 3, 0, -3, -6, w, s, r);
                    p.rot("pet2", 0, 0, 0.6f, 0, 0, -0.3f, w, s, r); p.rot("pet3", 0, 0, -0.6f, 0, 0, 0.3f, w, s, r);
                })
                .atk(Atk.WHIP, 7, 9f, 2.5f, 80).anim((p, e, w, s, r, age) -> {
                    p.rot("armR", 2.8f, -0.4f, 0.6f, 1.5f, 0.1f, 0, w, s, r); p.scl("armR", 1, 1, 1, 0.8f, 2.6f, 0.8f, w, s, r);
                    p.rot("body", 0.2f, -0.3f, 0, -0.2f, 0.3f, 0, w, s, r); p.rot("head", 0, 0.3f, 0, 0, -0.2f, 0, w, s, r);
                })
                // spits barbed poison seeds
                .atk(Atk.SHOOT, 5, 14f, 4f, 60, 0x8ad13a, "poison").anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.5f, 0, 0, -0.4f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -0.9f, 0, 0, w, s, r);
                    p.scl("head", 1, 1, 1, 0.9f, 1.15f, 0.9f, w, s, r); p.rot("body", 0.15f, 0, 0, -0.2f, 0, 0, w, s, r);
                })
                // vines coil the victim and lift it toward the maw
                .atk(Atk.GRAB, 6, 3.5f, 0, 180, 50, "").anim((p, e, w, s, r, age) -> {
                    p.rot("armL", 1.8f, 0, -0.9f, 1.3f, 0.3f, 0.3f, w, s, r); p.rot("armR", 1.8f, 0, 0.9f, 1.3f, -0.3f, -0.3f, w, s, r);
                    p.rot("head", 0.5f, 0, 0, 0.6f, 0, 0, w, s, r); p.rot("jaw", 0, 0, 0, -0.9f, 0, 0, w, s, r);
                    if (s >= 1 && r <= 0) p.add("jaw", Mth.sin(age * 1.2f) * 0.35f, 0, 0);
                })
                // pollen burst
                .atk(Atk.GAS, 2, 5f, 0, 240, 1, "").anim((p, e, w, s, r, age) -> {
                    p.rot("head", 0.6f, 0, 0, -0.6f, 0, 0, w, s, r); p.scl("stamen", 1, 1, 1, 3f, 3f, 3f, w, s, r);
                    p.rot("pet2", 0, 0, 0.3f, 0, 0, 0.9f, w, s, r); p.rot("pet3", 0, 0, -0.3f, 0, 0, -0.9f, w, s, r);
                });
    }

    /** Hostile toadstool: spotted cap with gills, glowing eyes in the shade beneath, stubby limbs. */
    static CreatureDef sporecap() {
        int cap = 0x9a2fb8, spot = 0xf0e6ff, stem = 0xe8dcc0, gill = 0x7a6a5a, eye = 0x9cff4a, dark = 0x2a1f18;
        return CreatureDef.of("mini_bubba", "Mini Bubba", "ミニ・ババ").size(1.0f, 1.6f).hp(45).dmg(5).speed(0.26).kbRes(0.2).egg(cap, spot).spawn(2)
                .part("legL", null, -2.5f, 5, 0, 3.5f, 5, 3.5f, 0, -2.5f, 0, stem).walk(1.2f, 0)
                .part("legR", null, 2.5f, 5, 0, 3.5f, 5, 3.5f, 0, -2.5f, 0, stem).walk(1.2f, P)
                .part("body", null, 0, 5, 0, 9, 10, 9, 0, 5, 0, stem)
                .part("ring", "body", 0, 8, 0, 10, 1.5f, 10, 0, 0, 0, spot)
                .part("eyeL", "body", -2, 6, -4.6f, 2, 2, 1, 0, 0, 0, eye).glow()
                .part("eyeR", "body", 2, 6, -4.6f, 2, 2, 1, 0, 0, 0, eye).glow()
                .part("jaw", "body", 0, 3, -4.6f, 5, 1.5f, 1, 0, 0, 0, dark)
                .part("tooth1", "jaw", -1.5f, 0, -0.3f, 1, 1, 1, 0, 0.6f, 0, spot)
                .part("tooth2", "jaw", 1.5f, 0, -0.3f, 1, 1, 1, 0, 0.6f, 0, spot)
                .part("head", "body", 0, 10, 0, 18, 7, 18, 0, 3.5f, 0, cap)
                .part("gills", "head", 0, 0, 0, 16, 1, 16, 0, -0.3f, 0, gill)
                .part("capTop", "head", 0, 7, 0, 12, 3, 12, 0, 1.5f, 0, cap)
                .part("sp1", "head", -5, 7.2f, -4, 3, 1, 3, 0, 0, 0, spot)
                .part("sp2", "head", 4, 7.2f, 3, 2, 1, 2, 0, 0, 0, spot)
                .part("sp3", "capTop", 1, 3.2f, -2, 3, 1, 3, 0, 0, 0, spot)
                .part("sp4", "head", 9.2f, 3, 0, 1, 2, 3, 0, 0, 0, spot)
                .part("sp5", "head", -9.2f, 4, 3, 1, 2, 2, 0, 0, 0, spot)
                .part("sp6", "head", 2, 4, -9.2f, 3, 2, 1, 0, 0, 0, spot)
                .part("armL", "body", -5.5f, 8, 0, 2.5f, 8, 2.5f, 0, -4, 0, stem).sway(0.1f, 0.12f, 0, 2)
                .part("armR", "body", 5.5f, 8, 0, 2.5f, 8, 2.5f, 0, -4, 0, stem).sway(0.1f, 0.12f, P, 2)
                // headbutt with the cap
                .atk(Atk.BITE, 5, 2.4f, 0, 22).anim((p, e, w, s, r, age) -> {
                    p.rot("body", 0.4f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("head", 0.4f, 0, 0, -0.3f, 0, 0, w, s, r);
                    p.pos("body", 0, 0, 2, 0, -1, -4, w, s, r); p.scl("jaw", 1, 1, 1, 1, 2.5f, 1, w, s, r);
                })
                // cap puffs up and releases a spore cloud
                .atk(Atk.GAS, 2, 5f, 0, 200, 1, "").anim((p, e, w, s, r, age) -> {
                    p.scl("head", 0.85f, 0.8f, 0.85f, 1.25f, 1.3f, 1.25f, w, s, r);
                    p.pos("body", 0, -2, 0, 0, 1, 0, w, s, r); p.rot("armL", 0, 0, -0.3f, 0, 0, -1.2f, w, s, r); p.rot("armR", 0, 0, 0.3f, 0, 0, 1.2f, w, s, r);
                    if (s > 0 && r <= 0) p.add("head", 0, age * 0.3f % (2 * P), 0);
                })
                // hops and lands cap-first: shockwave
                .atk(Atk.STOMP, 5, 3.5f, 0, 120).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -3, 0, 0, 0, 0, w, s, r); p.scl("body", 1, 1, 1, 1.2f, 0.8f, 1.2f, w, s, r);
                    p.scl("head", 1, 1, 1, 1.2f, 0.8f, 1.2f, w, s, r); p.rot("armL", 2.5f, 0, -0.3f, 0, 0, -0.8f, w, s, r); p.rot("armR", 2.5f, 0, 0.3f, 0, 0, 0.8f, w, s, r);
                });
    }

    /** Two-headed beast stitched from mismatched hides: one head snarls, the other screeches. */
    static CreatureDef patchworkHydra() {
        int hideA = 0x6b8a3c, hideB = 0xa0603a, hideC = 0x5a5a7a, stitch = 0xefe6c8, mouth = 0x2a0a0a, tooth = 0xf1ecd8, eyeA = 0xffd23a, eyeB = 0x3af0ff;
        return CreatureDef.of("nightmare_huggy", "Nightmare Huggy Wuggy", "ナイトメア・ハギーワギー").size(1.8f, 2.6f).hp(160).dmg(9).speed(0.3).kbRes(0.6).egg(hideA, hideB).spawn(1)
                .part("body", null, 0, 18, 0, 18, 14, 28, 0, 2, 2, hideA)
                .part("patch1", "body", -9.2f, 3, 6, 1, 8, 10, 0, 0, 0, hideB)
                .part("patch2", "body", 9.2f, 2, -4, 1, 9, 9, 0, 0, 0, hideC)
                .part("patch3", "body", 0, 9.2f, 8, 10, 1, 10, 0, 0, 0, hideC)
                .part("seam1", "body", -9.6f, 3, 0.5f, 1, 9, 0.6f, 0, 0, 0, stitch)
                .part("seam2", "body", 9.6f, 2, 0.8f, 1, 9, 0.6f, 0, 0, 0, stitch)
                .part("seam3", "body", 0, 9.6f, 2.8f, 10, 1, 0.6f, 0, 0, 0, stitch)
                .part("legL", "body", -6, -5, -9, 5, 13, 5, 0, -6.5f, 0, hideB).walk(0.9f, 0)
                .part("legR", "body", 6, -5, -9, 5, 13, 5, 0, -6.5f, 0, hideC).walk(0.9f, P)
                .part("legL2", "body", -6, -5, 12, 5, 13, 5, 0, -6.5f, 0, hideC).walk(0.9f, P)
                .part("legR2", "body", 6, -5, 12, 5, 13, 5, 0, -6.5f, 0, hideA).walk(0.9f, 0)
                .part("tail", "body", 0, 5, 16, 3, 3, 14, 0, 0, 7, hideB).sway(0.4f, 0.15f, 0, 1)
                // left head (snarler)
                .part("neckA", "body", -5, 6, -12, 6, 6, 10, 0, 2, -4, hideA).sway(0.1f, 0.11f, 0, 1)
                .part("head", "neckA", 0, 3, -9, 9, 8, 10, 0, 0, -4, hideA)
                .part("eyeA1", "head", -2.5f, 2, -9.2f, 2, 2, 1, 0, 0, 0, eyeA).glow()
                .part("eyeA2", "head", 2.5f, 2, -9.2f, 2, 2, 1, 0, 0, 0, eyeA).glow()
                .part("snoutA", "head", 0, -1, -9.2f, 7, 3, 1, 0, 0, 0, mouth)
                .part("fangA1", "head", -2, -2, -9.6f, 1, 2.5f, 1, 0, 0, 0, tooth)
                .part("fangA2", "head", 2, -2, -9.6f, 1, 2.5f, 1, 0, 0, 0, tooth)
                .part("jaw", "head", 0, -3, -3, 8, 2, 9, 0, -1, -4, hideA)
                // right head (screecher), sewn on
                .part("neckB", "body", 5, 6, -12, 6, 6, 10, 0, 2, -4, hideC).sway(0.12f, 0.13f, P, 1)
                .part("headB", "neckB", 0, 3, -9, 8, 9, 9, 0, 0, -4, hideC)
                .part("eyeB1", "headB", -2, 3, -8.7f, 2.5f, 2.5f, 1, 0, 0, 0, eyeB).glow()
                .part("eyeB2", "headB", 2.5f, 2, -8.7f, 1.5f, 1.5f, 1, 0, 0, 0, eyeB).glow()
                .part("seamB", "headB", 0, 4.6f, -4, 0.6f, 1, 9, 0, 0, 0, stitch)
                .part("jawB", "headB", 0, -2, -3, 7, 3, 8, 0, -1.5f, -4, mouth)
                .part("toothB", "jawB", 0, 0, -8, 5, 1.5f, 1, 0, 0.5f, 0, tooth)
                // both heads bite in turn (left first, right follows)
                .atk(Atk.BITE, 9, 3f, 0, 24).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.6f, 0.3f, 0, 0.5f, -0.1f, 0, w, s, r); p.rot("jaw", -1.0f, 0, 0, 0, 0, 0, w, s, r);
                    p.rot("headB", -0.2f, -0.3f, 0, -0.5f, 0.2f, 0, w, s, r); p.rot("jawB", -0.3f, 0, 0, -1.0f, 0, 0, w, s, r);
                    p.pos("neckA", 0, 1, 2, 0, -1, -6, w, s, r); p.pos("neckB", 0, 0, 0, 0, 0, -2, w, s, r);
                    p.pos("body", 0, 0, 2, 0, -1, -3, w, s, r);
                })
                .atk(Atk.SWIPE, 8, 3.4f, 0, 30).anim((p, e, w, s, r, age) -> {
                    p.rot("legL", 1.6f, 0, -0.4f, 0.8f, 0, 0.3f, w, s, r); p.rot("body", -0.25f, 0, -0.15f, 0.1f, 0.3f, 0.05f, w, s, r);
                    p.pos("body", 0, 3, 0, 0, 0, -3, w, s, r); p.rot("head", 0.3f, 0, 0, 0, 0, 0, w, s, r);
                })
                // right head shrieks while the left recoils
                .atk(Atk.SCREAM, 3, 9f, 2f, 240).anim((p, e, w, s, r, age) -> {
                    p.rot("headB", 0.5f, 0, 0, -0.6f, 0, 0, w, s, r); p.rot("jawB", -0.5f, 0, 0, -1.3f, 0, 0, w, s, r);
                    p.rot("neckB", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.rot("head", 0, 0, 0, 0.3f, 0.5f, 0.3f, w, s, r);
                    if (s > 0 && r <= 0) p.add("headB", 0, Mth.sin(age * 3f) * 0.2f, 0);
                })
                .atk(Atk.POUNCE, 9, 9f, 4f, 120).anim((p, e, w, s, r, age) -> {
                    p.pos("body", 0, -4, 2, 0, 3, -6, w, s, r); p.rot("body", 0.25f, 0, 0, -0.35f, 0, 0, w, s, r);
                    p.rot("legL", 0.4f, 0, 0, -1.4f, 0, 0, w, s, r); p.rot("legR", 0.4f, 0, 0, -1.4f, 0, 0, w, s, r);
                    p.rot("legL2", -0.4f, 0, 0, 1.2f, 0, 0, w, s, r); p.rot("legR2", -0.4f, 0, 0, 1.2f, 0, 0, w, s, r);
                    p.rot("jaw", 0, 0, 0, -0.9f, 0, 0, w, s, r); p.rot("jawB", 0, 0, 0, -0.9f, 0, 0, w, s, r);
                });
    }

    /** Small legless tin torso that drags itself on two arms; one lamp eye. Summoned by the Sovereign and Puppeteer. */
    static CreatureDef tinCrawler() {
        int tin = 0x9aa3ab, dark = 0x3a3f45, eye = 0xff4a2a, rust = 0x8a4b2a, tooth = 0xe8e2d0;
        return CreatureDef.of("mini_prototype", "Mini Prototype", "ミニ・プロトタイプ").size(0.7f, 0.6f).hp(18).dmg(3).speed(0.36).kbRes(0).egg(tin, eye)
                .part("body", null, 0, 4, 2, 7, 5, 9, 0, 0, 0, tin)
                .part("rust", "body", 2, 2.6f, 2, 3, 1, 4, 0, 0, 0, rust)
                .part("spring", "body", 0, 0, 5, 2, 2, 4, 0, 0, 2, dark).sway(0.3f, 0.4f, 0, 1)
                .part("head", "body", 0, 1, -5, 6, 5, 5, 0, 0, -2, tin)
                .part("eyeL", "head", 0, 1, -4.6f, 3, 3, 1, 0, 0, 0, eye).glow()
                .part("jaw", "head", 0, -1.5f, -2, 5, 1.5f, 4, 0, -0.5f, -2, dark)
                .part("teeth", "jaw", 0, 0.4f, -4, 4, 1, 1, 0, 0, 0, tooth)
                .part("armL", "body", -4.5f, 1, -3, 2, 7, 2, 0, -3.5f, 0, dark).walk(1.2f, 0)
                .part("armR", "body", 4.5f, 1, -3, 2, 7, 2, 0, -3.5f, 0, dark).walk(1.2f, P)
                .atk(Atk.BITE, 3, 1.8f, 0, 20).anim((p, e, w, s, r, age) -> {
                    p.rot("head", -0.5f, 0, 0, 0.3f, 0, 0, w, s, r); p.rot("jaw", 0.9f, 0, 0, 0, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -3, w, s, r);
                })
                .atk(Atk.POUNCE, 3, 6f, 2f, 80).anim((p, e, w, s, r, age) -> {
                    p.rot("armL", -0.6f, 0, 0, 1.8f, 0, 0, w, s, r); p.rot("armR", -0.6f, 0, 0, 1.8f, 0, 0, w, s, r);
                    p.rot("body", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, -1, 1, 0, 2, -3, w, s, r);
                });
    }

    private BatchE() {}
}
