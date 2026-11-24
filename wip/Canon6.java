package com.poppy.mod.creature;

import com.poppy.mod.creature.Canon.B;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Canon rebuilds, part 5: non-upright rigs. Long Legs (crouched, spider-like: round abdomen, bent limbs touching the floor) and CatNap (hunched on all fours). */
final class Canon6 {
    static final int BLK = 0x0b0b0e, WHT = 0xf4f1ea;
    static final Map<String, Consumer<CreatureDef>> MAP = new HashMap<>();
    static float PI = (float) Math.PI;

    /** Crouched Long Legs. Limbs are two stepped segments (out, then down) so they read as bent without box rotation. */
    static void longLegs(CreatureDef d, boolean daddy, boolean nm) {
        float f = nm ? 0.75f : 1f;
        int skin = Canon2.tint(daddy ? 0x5aa8e0 : 0xf0508c, f), face = Canon2.tint(daddy ? 0x8ac8f0 : 0xf47ab4, f), dk = Canon2.tint(daddy ? 0x2a58a0 : 0xb82060, f);
        int cuff = Canon2.tint(0x4a9a9a, f), shoe = Canon2.tint(daddy ? 0xf2c82a : 0xd82030, f), hand = Canon2.tint(daddy ? 0xf2c82a : 0xf0508c, f), bod = Canon2.tint(daddy ? 0x3a78c8 : 0xe0407a, f);
        int hair = Canon2.tint(daddy ? 0x2a3a8a : 0xf0508c, f);
        B b = new B(d, d.h * 16f / 44);
        b.p("body", null, 0, 22, 2, 7, 6, 6, 0, 0, 0, bod).sw(0.03f, 0.1f, 0, 2);
        Canon5.ball(b, "abd", "body", 0, 0, 7.4f, 7.2f, dk);
        Canon5.ball(b, "chest", "body", 0, 1, -3.4f, 3.8f, daddy ? bod : Canon2.tint(0xf04c80, f));
        if (daddy) b.p("tieB", "body", 0, 4, -6.6f, 5, 2, 1, 0, 0, 0, 0xf2c82a);
        b.p("neck", "body", 0, 3, -5, 1.8f, 7, 1.8f, 0, 3.5f, 0, face);
        b.p("collar", "neck", 0, 0, 0, 3.4f, 1.2f, 3.4f, 0, 0, 0, cuff);
        b.p("head", "neck", 0, 7, -1.4f, 1, 1, 1, 0, 0, 0, face).sw(0.07f, 0.1f, 1, 2);
        Canon5.ball(b, "skull", "head", 0, 4.6f, 0, 5.4f, face);
        b.pr("eyeW#", "head", 2.6f, 5.8f, -9f, 4.2f, 4.2f, 0.6f, 0, 0, 0, BLK);
        b.pr("eyeR#", "head", 2.6f, 5.8f, -9.4f, 3.4f, 3.4f, 0.5f, 0, 0, 0, WHT);
        b.pr("iris#", "head", 2.6f, 5.8f, -9.8f, 2.4f, 2.4f, 0.4f, 0, 0, 0, nm ? 0xff2020 : (daddy ? 0xe020a0 : 0x38d038)).g();
        b.pr("lash#", "head", 3.6f, 8.6f, -9.4f, 3, 0.5f, 0.4f, 0, 0, 0, BLK);
        b.p("jaw", "head", 0, 1.8f, -9.2f, 5.6f, 1.6f, 0.6f, 0, 0, 0, nm ? 0x200408 : 0x1a0810);
        b.p("lips", "head", 0, 3, -9.3f, 5.8f, 0.6f, 0.4f, 0, 0, 0, daddy ? face : 0xe02060);
        if (nm) for (int i = 0; i < 5; i++) b.p("nfang" + i, "head", -2 + i, 2.6f, -9.5f, 0.6f, 1.8f, 0.4f, 0, -0.9f, 0, WHT);
        if (daddy) {
            b.p("mustache", "head", 0, 3.8f, -9.4f, 7, 1.4f, 0.6f, 0, 0, 0, hair);
            b.p("brim", "head", 0, 9.6f, -1, 11, 0.8f, 10, 0, 0, 0, 0x808088); b.p("fedora", "head", 0, 10, -1, 7.4f, 4.4f, 7, 0, 2.2f, 0, 0x6a6a72);
        } else {
            Canon5.ball(b, "bun", "head", -3.6f, 9, -0.6f, 2.8f, hair);
            b.p("swirl", "head", -3.6f, 9, -3.6f, 2.6f, 2.6f, 0.8f, 0, 0, 0, Canon2.tint(0xf47ab4, f));
            b.p("tie", "head", 2.4f, 8.8f, -2, 2, 2.6f, 2.6f, 0, 0, 0, Canon2.tint(0x88c0f0, f));
            for (int i = 0; i < 6; i++) b.p("pony" + i, "head", i % 2 == 0 ? -2 : -4.4f, 10 + i * 1.6f, 1 + i * 0.4f, 2.4f, 1.6f, 1.6f, 0, 0, 0, hair).sw(0.08f, 0.2f, i, 2);
        }
        // arms: out, then down to splayed floor hands
        for (int s = 0; s < 2; s++) {
            String sd = s == 0 ? "L" : "R"; float sg = s == 0 ? -1 : 1;
            b.p("arm" + sd, "body", sg * 3.4f, 2, -3.6f, 12, 2, 2, sg * 6, 0, 0, skin).sw(0.05f, 0.1f, s * PI, 2);
            b.p("fore" + sd, "arm" + sd, sg * 12, 0, 0, 2, 21, 2, 0, -10.5f, 0, skin);
            b.p("elbow" + sd, "arm" + sd, sg * 12, 0, 0, 3, 3, 3, 0, 0, 0, dk);
            b.p("cuffA" + sd, "fore" + sd, 0, -18.4f, 0, 3.4f, 1.6f, 3.4f, 0, 0, 0, cuff);
            b.p("hand" + sd, "fore" + sd, 0, -21, 0, 4, 1.4f, 4, 0, 0, -1, hand);
            for (int i = 0; i < 4; i++) b.p("fing" + sd + i, "hand" + sd, -1.5f + i, 0, -2.4f, 0.9f, 0.9f, 6, 0, 0, -3, hand);
            b.p("thumb" + sd, "hand" + sd, sg * 2, 0, -0.6f, 4, 0.9f, 0.9f, sg * 2, 0, 0, hand);
        }
        // legs: out, then down, red shoes with teal cuff
        for (int s = 0; s < 2; s++) {
            String sd = s == 0 ? "L" : "R"; float sg = s == 0 ? -1 : 1;
            b.p("leg" + sd, "body", sg * 3, -1, 5.4f, 13, 2.4f, 2.4f, sg * 6.5f, 0, 0, skin);
            d.walk(0.5f, s == 0 ? 0 : PI);
            b.p("shin" + sd, "leg" + sd, sg * 13, 0, 0, 2.4f, 21, 2.4f, 0, -10.5f, 0, skin);
            b.p("knee" + sd, "leg" + sd, sg * 13, 0, 0, 3.2f, 3.2f, 3.2f, 0, 0, 0, dk);
            b.p("cuffL" + sd, "shin" + sd, 0, -17.6f, 0, 3.6f, 2, 3.6f, 0, 0, 0, cuff);
            b.p("shoe" + sd, "shin" + sd, 0, -21, -1.6f, 4.6f, 3.4f, 8, 0, 1.7f, 0, shoe);
            b.p("toeCap" + sd, "shoe" + sd, 0, 2, -4, 4.8f, 2, 1, 0, 0, 0, Canon2.tint(0xa01824, f));
        }
    }

    /** CatNap on all fours: hunched, long arms planted ahead, hind legs folded. */
    static void catnap(CreatureDef d, boolean nm) {
        float f = nm ? 0.8f : 1f; int pu = Canon2.tint(0x7a3aa8, f), dk = Canon2.tint(0x4a2070, f), lt = Canon2.tint(0xb888d8, f);
        B b = new B(d, d.h * 16f / 34);
        b.p("body", null, 0, 17, 0, 8, 8, 15, 0, 0, 0, pu).sw(0.03f, 0.1f, 0, 2);
        b.p("ribs", "body", 0, -1, -4, 6.4f, 5, 0.5f, 0, 0, 0, dk);
        for (int i = 0; i < 4; i++) b.p("spine" + i, "body", 0, 4.4f, -5 + i * 3.4f, 1.4f, 1.4f, 1.4f, 0, 0, 0, dk);
        b.p("zipM", "body", 0, -4.2f, 0, 0.8f, 0.5f, 14, 0, 0, 0, BLK);
        b.p("collar", "body", 0, 1, -7.6f, 6, 6, 1, 0, 0, 0, BLK);
        b.p("head", "body", 0, 2, -9.4f, 11, 9, 9, 0, 0, -2, pu).sw(0.07f, 0.1f, 1, 2);
        b.pr("ear#", "head", 4.6f, 4.4f, 1, 4.4f, 4.6f, 1.8f, 0, 2.3f, 0, pu); b.pr("earTip#", "ear#", 0, 4.6f, 0, 2.6f, 2.4f, 1.6f, 0, 1.2f, 0, pu);
        b.pr("earIn#", "ear#", 0, 0, -1, 2.6f, 3.6f, 0.4f, 0, 2.2f, 0, dk);
        b.pr("eyeW#", "head", 3.2f, 1.6f, -6.6f, 3, 3, 0.5f, 0, 0, 0, WHT);
        b.pr("eye#", "head", 3.2f, 1.6f, -6.9f, 1.6f, 2, 0.4f, 0, 0, 0, nm ? 0xff2020 : BLK).g();
        b.p("muzzle", "head", 0, -2.4f, -6.6f, 6.4f, 3.6f, 0.8f, 0, 0, 0, lt);
        b.p("nose", "head", 0, -0.6f, -7f, 1.8f, 1.2f, 0.6f, 0, 0, 0, 0xd06a90);
        b.p("jaw", "head", 0, -3.6f, -6.7f, 8.6f, 2.2f, 0.6f, 0, 0, 0, 0x0a0208);
        b.p("grin", "head", 0, -2.2f, -6.75f, 10.4f, 0.6f, 0.4f, 0, 0, 0, BLK);
        for (int s = 0; s < 2; s++) {
            String sd = s == 0 ? "L" : "R"; float sg = s == 0 ? -1 : 1;
            b.p("arm" + sd, "body", sg * 4.4f, 1, -5.6f, 2.6f, 18, 2.6f, 0, -9, 0, pu); d.walk(0.55f, s == 0 ? 0 : PI);
            b.p("hand" + sd, "arm" + sd, 0, -18, 0, 3.6f, 1.6f, 6, 0, 0.8f, -2, dk);
            for (int i = 0; i < 4; i++) b.p("claw" + sd + i, "hand" + sd, -1.4f + i * 0.95f, 0, -5, 0.6f, 0.8f, 3, 0, 0, -1.5f, BLK);
            b.p("leg" + sd, "body", sg * 3.6f, -1, 6.4f, 3, 13, 3, 0, -6, 0, pu); d.walk(0.55f, s == 0 ? PI : 0);
            b.p("foot" + sd, "leg" + sd, 0, -12, 0, 4, 1.8f, 7, 0, 0.9f, -1.4f, dk);
        }
        b.p("tail", "body", 0, 1, 7.4f, 1.6f, 1.6f, 9, 0, 0, 4.5f, pu).sw(0.15f, 0.1f, 0, 1);
        b.p("tail2", "tail", 0, 0, 9, 1.6f, 10, 1.6f, 0, 5, 0, pu);
        b.p("tail3", "tail2", 0, 10, 0, 5, 1.6f, 1.6f, 2, 0, 0, pu);
    }

    static {
        MAP.put("mommy_long_legs", d -> longLegs(d, false, false));
        MAP.put("mama_long_legs", d -> longLegs(d, false, false));
        MAP.put("mini_mommy", d -> longLegs(d, false, false));
        MAP.put("nightmare_mommy", d -> longLegs(d, false, true));
        MAP.put("daddy_long_legs", d -> longLegs(d, true, false));
        MAP.put("catnap", d -> catnap(d, false));
        MAP.put("nightmare_catnap", d -> catnap(d, true));
    }
    private Canon6() {}
}
