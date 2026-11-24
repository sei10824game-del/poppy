package com.poppy.mod.creature;

import com.poppy.mod.creature.Canon.B;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Canon rebuilds, part 2: Smiling/Nightmare Critters, Huggy/Kissy line, Long Legs, Bron, Boxy Boo, Doey, Yarnaby, Prototype, Bunzo, Miss Delight, PJ. */
final class Canon2 {
    static final int BLK = 0x0b0b0e, WHT = 0xf4f1ea, RED = 0xff2020;
    static final Map<String, Consumer<CreatureDef>> MAP = new HashMap<>();

    static int tint(int c, float f) { return ((int) (((c >> 16) & 255) * f) << 16) | ((int) (((c >> 8) & 255) * f) << 8) | (int) ((c & 255) * f); }

    /** Nightmare treatment: jagged fang row on the head, red eyes, back spikes. */
    static void nm(B b, float hw, float hh, float hd) {
        for (int i = 0; i < 6; i++) b.p("nfang" + i, "head", -hw * 0.3f + i * hw * 0.12f, hh * 0.3f, -hd / 2 - 0.5f, 1.2f, 2.8f, 0.5f, 0, -1, 0, WHT);
        for (int i = 0; i < 3; i++) b.p("nspike" + i, "body", 0, 11 - i * 3.4f, 4.8f, 1.6f, 2.6f, 2.4f, 0, 0, 1, 0x1a1016);
    }

    /** Critter with species extras via callback; nightmare tints the palette and swaps eyes to red. */
    static void crit(String id, int fur, int belly, int claw, int pend, Consumer<B> extra) {
        for (boolean n : new boolean[]{false, true}) {
            String key = n ? "nightmare_" + id : id;
            MAP.put(key, d -> {
                float f = n ? 0.78f : 1f;
                B b = Canon.critter(d, tint(fur, f), tint(belly, f), n ? 0x300408 : 0x2a0a10, tint(claw, f), n ? RED : 0xffffff, n);
                extra.accept(b);
                b.pendant(12, 12, pend);
                if (n) nm(b, 14, 12, 11);
            });
        }
    }

    static void huggy(CreatureDef d, int blue, int dark, int red, int yel, boolean nm, boolean kissy) {
        CreatureDef h = HuggyWuggy.def(); float k = d.h / 3.7f;
        for (var pt : h.parts) {
            int c = pt.color == 0x2f6fe0 ? blue : pt.color == 0x1c3f94 ? dark : pt.color == 0xd8262b ? red : pt.color == 0xf4d12a ? yel : pt.color;
            if (nm && pt.glow) c = RED;
            d.part(pt.name, pt.parent, pt.px * k, pt.py * k, pt.pz * k, pt.w * k, pt.h * k, pt.d * k, pt.cx * k, pt.cy * k, pt.cz * k, c);
            var n = d.parts.get(d.parts.size() - 1);
            n.glow = pt.glow; n.walkAmp = pt.walkAmp; n.walkPhase = pt.walkPhase; n.swayAmp = pt.swayAmp; n.swaySpeed = pt.swaySpeed; n.swayPhase = pt.swayPhase; n.swayAxis = pt.swayAxis;
        }
        B b = new B(d, k);
        b.p("tongue", "jaw", 0, -2, -1, 6, 1, 8, 0, 0, -3, 0xe0506a);
        if (nm) for (int i = 0; i < 7; i++) b.p("nfang" + i, "head", -9 + i * 3, 3.5f, -9.9f, 1.4f, 4.5f, 0.8f, 0, -2, 0, WHT);
        if (kissy) {
            b.pr("lash#", "head", 6, 18.4f, -9.5f, 6, 1, 1, 0, 0, 0, BLK);
            b.pr("lash2#", "head", 9, 15, -9.5f, 1, 4, 1, 0, 0, 0, BLK);
            b.p("bow", "head", 0, 20, 2, 10, 4, 4, 0, 2, 0, 0x2a6ae0);
            b.p("bowK", "bow", 0, 2, -2.2f, 3, 3, 2, 0, 0, 0, 0x1a4ab0);
        }
    }

    static void kissy(String id, boolean nm) { MAP.put(id, d -> huggy(d, nm ? 0x8a2a58 : 0xf06aa8, nm ? 0x5a1a38 : 0xb83a80, 0xd8262b, 0xf4d12a, nm, true)); }

    static B plush(CreatureDef d, float totalPx) { return new B(d, d.h * 16f / totalPx); }

    /** Tall spindly Long Legs build. */
    static void longLegs(CreatureDef d, boolean daddy, boolean nm, int extraArms) {
        float f = nm ? 0.78f : 1f;
        int skin = tint(daddy ? 0x90c8f0 : 0xf4b6c8, f), hair = tint(daddy ? 0x1a2a6a : 0xf070a0, f), suit = tint(daddy ? 0xf2d23a : 0xf06a98, f), acc = tint(0x90c8f0, f);
        B b = plush(d, 76);
        b.pr("leg#", null, 4, 40, 0, 3.4f, 40, 3.4f, 0, -20, 0, skin, 0.7f);
        b.pr("boot#", "leg#", 0, -40, -1, 4.4f, 3, 6, 0, 1, 0, daddy ? 0xf2d23a : suit);
        b.p("body", null, 0, 40, 0, daddy ? 14 : 12, 15, 8, 0, 7.5f, 0, suit).sw(0.02f, 0.1f, 0, 2);
        b.p("belly", "body", 0, -3, 1, 8, 6, 6, 0, 0, 3, skin);
        b.p("neck", "body", 0, 15, 0, 3.2f, 5, 3.2f, 0, 2.5f, 0, skin);
        b.p("head", "neck", 0, 5, 0, 15, 18, 12, 0, 9, 0, skin);
        b.p("choker", "neck", 0, 1, 0, 3.6f, 1.4f, 3.6f, 0, 0, 0, acc);
        b.pr("eyeW#", "head", 3.6f, 11, -6.1f, 5, 5, 0.5f, 0, 0, 0, WHT);
        b.pr("eye#", "head", 3.6f, 11, -6.4f, 2.8f, 2.8f, 0.4f, 0, 0, 0, nm ? RED : (daddy ? 0xe020a0 : 0x30c050)).g();
        b.pr("lash#", "head", 3.6f, 14, -6.4f, 5.6f, 0.7f, 0.3f, 0, 0, 0, BLK);
        b.p("jaw", "head", 0, 3.4f, -6.1f, 10.4f, 3.2f, 0.6f, 0, 0, 0, nm ? 0x200408 : 0x0e0e12);
        b.p("lips", "head", 0, 5.2f, -6.3f, 10.6f, 1f, 0.4f, 0, 0, 0, daddy ? skin : 0xd02060);
        if (nm) for (int i = 0; i < 6; i++) b.p("nfang" + i, "head", -3 + i * 1.2f, 5, -6.5f, 0.8f, 2, 0.4f, 0, -1, 0, WHT);
        if (daddy) {
            b.p("mustache", "head", 0, 7.4f, -6.4f, 11, 2f, 0.6f, 0, 0, 0, hair);
            b.p("fedoraBrim", "head", 0, 18, 0, 19, 1, 16, 0, 0.5f, 0, 0x808088);
            b.p("fedora", "head", 0, 18.5f, 0, 12, 6, 11, 0, 3, 0, 0x6a6a72);
            b.p("bowtie", "body", 0, 13, -3.2f, 5, 2, 1, 0, 0, 0, 0xf2d23a);
        } else {
            b.p("fringe", "head", 0, 18, -1, 16, 4, 12.6f, 0, -0.5f, 0, hair);
            b.p("tie", "head", 6.8f, 15, 0, 2.4f, 3.4f, 3.4f, 0, 0, 0, acc);
            b.p("ponytail", "head", 7.4f, 17, 2, 2.4f, 30, 2.4f, 0, 14, 0, hair).sw(0.15f, 0.1f, 0, 2);
            b.p("heart", "body", 0, 8, -3.2f, 6, 5, 0.6f, 0, 0, 0, suit);
        }
        float aw = 3f;
        b.pr("arm#", "body", 8, 13, 0, aw, 36, aw, 0, -18, 0, skin);
        b.pr("hand#", "arm#", 0, -36, 0, 3.4f, 3.6f, 3.4f, 0, -1, 0, daddy ? 0xf2d23a : skin);
        for (int i = 0; i < extraArms; i++) {
            b.pr("arm" + (i + 2) + "#", "body", 4.6f, 7 - i * 3, 0, aw, 30, aw, 0, -15, 0, skin);
            b.pr("hand" + (i + 2) + "#", "arm" + (i + 2) + "#", 0, -30, 0, 3.4f, 3.6f, 3.4f, 0, -1, 0, skin);
        }
    }

    static void bron(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int rd = tint(0xe02020, f), pale = tint(0xf4e090, f), bl = tint(0x3a6ae0, f);
        B b = plush(d, 40);
        b.pr("leg#", null, 5, 7, -7, 5, 7, 5, 0, -3.5f, 0, rd, 0.7f);
        b.pr("legB#", null, 5, 7, 7, 5, 7, 5, 0, -3.5f, 0, rd, 0.7f);
        for (String n : new String[]{"leg#", "legB#"}) for (int i = 0; i < 3; i++) b.pr("toe" + n + i + "#", n, 0, -7, -2 + i * 2, 1.4f, 1.2f, 1.4f, 0, 0, -1, bl);
        b.p("body", null, 0, 7, 0, 16, 12, 24, 0, 6, 0, rd).sw(0.02f, 0.1f, 0, 2);
        b.p("belly", "body", 0, 0, 0, 12, 4, 20, 0, 1, 0, pale);
        for (int i = 0; i < 7; i++) b.p("dot" + i, "body", ((i * 5) % 3 - 1) * 3.4f, 12.2f, -9 + i * 3, 3, 0.4f, 3, 0, 0, 0, bl);
        b.p("neck", "body", 0, 9, -10, 6, 18, 6, 0, 8, 0, rd);
        b.p("neckF", "neck", 0, 0, -2.6f, 4, 14, 0.6f, 0, 7, 0, pale);
        b.p("head", "neck", 0, 17, -1, 9, 8, 10, 0, 3, -2, rd);
        b.p("snout", "head", 0, 1, -7, 6, 4, 3, 0, 0, 0, pale);
        b.pr("cheek#", "head", 4.6f, 2, -3, 0.6f, 4, 4, 0, 0, 0, pale);
        b.pr("eyeLid#", "head", 3.4f, 6.4f, -7.1f, 2.4f, 1, 0.4f, 0, 0, 0, 0xc01010);
        b.pr("eye#", "head", 3.4f, 5, -7.1f, 1.6f, 1.6f, 0.4f, 0, 0, 0, nm ? RED : BLK).g();
        b.pr("brow#", "head", 3.4f, 7.8f, -7.1f, 2.6f, 0.4f, 0.3f, 0, 0, 0, BLK);
        b.p("jaw", "head", 0, 0.5f, -6.8f, 5, 1, 2.4f, 0, 0, 0, 0x40080c);
        b.p("tail", "body", 0, 7, 12, 5, 4, 14, 0, 0, 7, rd).sw(0.15f, 0.08f, 0, 1);
        b.pr("arm#", "body", 6, 8, -8, 2, 2, 2, 0, 0, 0, rd);
    }

    static void boxy(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int bx = tint(0x8ad0f0, f), gold = tint(0xf2c030, f), rd = tint(0xd02828, f), sk = tint(0xf4e4cc, f);
        B b = plush(d, 40);
        b.pr("leg#", null, 3.4f, 8, 0, 3, 8, 3, 0, -4, 0, gold, 0.8f);
        b.pr("shoe#", "leg#", 0, -8, -1, 3.6f, 2, 5, 0, 1, 0, rd);
        b.p("body", null, 0, 8, 0, 14, 14, 14, 0, 7, 0, bx).sw(0.02f, 0.1f, 0, 2);
        b.p("starV", "body", 0, 3, -7.1f, 2.4f, 8, 0.5f, 0, 0, 0, gold).p("starH", "body", 0, 6, -7.2f, 8, 2.4f, 0.5f, 0, 0, 0, gold);
        b.p("starD", "body", 0, 5, -7.3f, 5, 5, 0.4f, 0, 0, 0, gold);
        b.pr("lidS#", "body", 7.2f, 7, 0, 0.8f, 6, 6, 0, 0, 0, bx);
        b.pr("circ#", "body", 7.6f, 7, 0, 0.5f, 3, 3, 0, 0, 0, gold);
        b.p("lid", "body", 0, 14, 6.6f, 14, 1.2f, 6, 0, 0, -3, rd);
        b.p("lidIn", "body", 0, 13.6f, 6.6f, 12, 0.6f, 5, 0, 0, -3, gold);
        b.p("crank", "body", 5, 13, 0, 0.8f, 3, 0.8f, 0, 1.5f, 0, 0x808088).sw(0.5f, 0.3f, 0, 2);
        b.p("head", "body", 0, 14, 0, 9, 10, 8, 0, 5, 0, sk);
        b.p("hat", "head", 0, 10, 0, 6, 5, 6, 0, 2.5f, 0, rd);
        b.p("pom", "hat", 0, 5, 0, 2, 2, 2, 0, 0.5f, 0, gold);
        b.pr("eye#", "head", 2.4f, 6, -4.1f, 2.6f, 2.8f, 0.5f, 0, 0, 0, nm ? RED : BLK).g();
        b.p("nose", "head", 0, 4, -4.4f, 2.4f, 2.4f, 1, 0, 0, 0, rd);
        b.p("jaw", "head", 0, 1.8f, -4.1f, 6, 2.2f, 0.6f, 0, 0, 0, 0x2a0a10);
        b.p("teeth", "head", 0, 3.2f, -4.4f, 5.4f, 0.8f, 0.3f, 0, 0, 0, WHT);
        if (nm) for (int i = 0; i < 6; i++) b.p("nfang" + i, "head", -2.5f + i, 3, -4.6f, 0.7f, 2.4f, 0.3f, 0, -1, 0, WHT);
        b.pr("arm#", "body", 8.4f, 10, 0, 2.4f, 10, 2.4f, 0, -4, 0, gold);
        b.pr("hand#", "arm#", 0, -9, 0, 3.4f, 3, 3.4f, 0, -1, 0, WHT);
    }

    static void doey(CreatureDef d) {
        int lb = 0x7ac4ec, hat = 0x1c3aa0, or = 0xf08a30, ye = 0xf2d23a, rd = 0xd02828;
        B b = plush(d, 40);
        b.pr("leg#", null, 4, 5, 0, 5, 5, 6, 0, -2.5f, 0, rd, 0.8f);
        b.p("body", null, 0, 5, 0, 15, 17, 11, 0, 8.5f, 0, lb).sw(0.03f, 0.1f, 0, 2);
        b.p("belly", "body", 0, 2, -4.8f, 9, 6, 1.6f, 0, 3, 0, 0x9ad8f4);
        b.p("neck", "body", 0, 17, 0, 4, 3, 4, 0, 1, 0, lb);
        b.p("head", "neck", 0, 3, 0, 11, 10, 9, 0, 5, 0, lb);
        b.p("brim", "head", 0, 9, 0, 14, 1.4f, 12, 0, 0.5f, 0, hat);
        b.p("bowler", "head", 0, 10, 0, 9, 6, 8, 0, 3, 0, hat);
        b.pr("eye#", "head", 2.6f, 5.4f, -4.6f, 2, 2.4f, 0.5f, 0, 0, 0, BLK);
        b.p("jaw", "head", 0, 2.4f, -4.6f, 4.4f, 1, 0.4f, 0, 0, 0, 0x1a3a6a);
        b.p("armL", "body", -9, 14, 0, 4, 20, 4, 0, -9, 0, or).sw(0.04f, 0.1f, 0, 2);
        b.p("forearmL", "armL", 0, -18, 0, 6, 8, 6, 0, -3, 0, or);
        b.p("armR", "body", 9, 14, 0, 4, 20, 4, 0, -9, 0, ye).sw(0.04f, 0.1f, 3.14f, 2);
        b.p("forearmR", "armR", 0, -18, 0, 6, 8, 6, 0, -3, 0, ye);
        for (int i = 0; i < 4; i++) { b.p("fingL" + i, "forearmL", -2.2f + i * 1.5f, -7, 0, 1.3f, 3, 1.5f, 0, -1.5f, 0, or); b.p("fingR" + i, "forearmR", -2.2f + i * 1.5f, -7, 0, 1.3f, 3, 1.5f, 0, -1.5f, 0, ye); }
    }

    static void yarnaby(CreatureDef d) {
        int am = 0xe8a030, tan = 0xd8b078;
        int[] yarn = {0xe03030, 0xf08a30, 0xf2d23a, 0x4ac060, 0x3a8ae0, 0x9a4ad0};
        B b = plush(d, 40);
        b.pr("leg#", null, 4, 10, 0, 5.4f, 10, 6, 0, -5, 0, am, 0.8f);
        b.pr("paw#", "leg#", 0, -10, -1, 6, 2.4f, 7.4f, 0, 1.2f, 0, tan);
        b.p("body", null, 0, 10, 0, 14, 14, 10, 0, 7, 0, am).sw(0.02f, 0.1f, 0, 2);
        for (int i = 0; i < 6; i++) b.p("yarnB" + i, "body", -5 + (i % 3) * 5, 2 + (i / 3) * 6, -5.2f, 4, 4, 0.8f, 0, 0, 0, yarn[i]);
        b.p("head", "body", 0, 14, 0, 13, 12, 11, 0, 6, 0, am);
        for (int i = 0; i < 12; i++) { double a = i * Math.PI / 6; b.p("mane" + i, "head", (float) Math.cos(a) * 8.6f, 6 + (float) Math.sin(a) * 8f, 3.4f, 5, 5, 6, 0, 0, 0, yarn[i % 6]); }
        b.p("seam", "head", 0, 0, -5.6f, 0.6f, 12, 0.4f, 0, 6, 0, BLK);
        b.p("muzzle", "head", 0, 3, -5.6f, 7, 4, 1.2f, 0, 0, 0, tan);
        b.p("nose", "head", 0, 5, -6.3f, 2.4f, 1.6f, 0.6f, 0, 0, 0, BLK);
        b.pr("eye#", "head", 3, 8, -5.7f, 3.8f, 4, 0.5f, 0, 0, 0, WHT);
        b.pr("pupil#", "head", 3, 8, -6, 2, 2.4f, 0.4f, 0, 0, 0, BLK);
        b.p("jaw", "head", 0, 2, -5.7f, 8, 2, 0.6f, 0, 0, 0, 0x2a0a10);
        b.p("fang0", "head", -2.4f, 3.4f, -6, 1.2f, 2, 0.4f, 0, -1, 0, WHT); b.p("fang1", "head", 0.4f, 3.4f, -6, 1.2f, 2.6f, 0.4f, 0, -1, 0, WHT); b.p("fang2", "head", 2.8f, 3.4f, -6, 1.2f, 1.8f, 0.4f, 0, -1, 0, WHT);
        b.pr("arm#", "body", 8.4f, 12, 0, 3.6f, 12, 3.6f, 0, -5, 0, am);
        b.pr("hand#", "arm#", 0, -11, 0, 4.4f, 3.4f, 4.4f, 0, -1, 0, tan);
        b.p("spool", "body", 0, 9, 6, 6, 6, 6, 0, 0, 0, yarn[0]);
        b.p("thread", "spool", 0, 0, 3, 0.8f, 0.8f, 12, 0, 0, 6, yarn[3]);
    }

    static void prototype(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int gr = tint(0x5a5a62, f), dk = tint(0x2c2c34, f), por = tint(0xf0ece0, f), co = tint(0x3a60b0, f), gold = tint(0xd8b040, f), rd = tint(0xc82020, f);
        B b = plush(d, 90);
        b.pr("leg#", null, 7, 38, -12, 6f, 38, 6f, 0, -19, 0, gr, 0.7f);
        b.pr("legB#", null, 7, 38, 12, 6f, 38, 6f, 0, -19, 0, gr, 0.7f);
        b.pr("hoof#", "leg#", 0, -38, -1, 6, 2.4f, 7, 0, 1.2f, 0, dk); b.pr("hoofB#", "legB#", 0, -38, -1, 6, 2.4f, 7, 0, 1.2f, 0, dk);
        b.p("body", null, 0, 38, 0, 16, 14, 34, 0, 7, 0, dk).sw(0.02f, 0.1f, 0, 2);
        for (int i = 0; i < 4; i++) b.p("cable" + i, "body", -5 + i * 3.4f, 14.4f, 6 + i * 2, 1, 1, 14, 0, 0, 0, 0xc82020);
        b.p("torso", "body", 0, 12, -14, 20, 28, 12, 0, 14, 0, co);
        b.p("trim", "torso", 0, 6, -6.1f, 17, 1.4f, 0.4f, 0, 0, 0, gold);
        b.p("bow", "torso", 0, 27, -6.1f, 8, 4, 1.6f, 0, 0, 0, rd);
        b.p("head", "torso", 0, 28, 0, 16, 17, 13, 0, 8.5f, 0, por);
        b.p("eyeHollow", "head", -3.6f, 10.5f, -6.6f, 4.2f, 4.2f, 0.5f, 0, 0, 0, BLK);
        b.p("eyeY", "head", 3.6f, 10.5f, -6.7f, 2.4f, 2.4f, 0.5f, 0, 0, 0, nm ? RED : 0xffd820).g();
        b.p("jaw", "head", 0, 3.4f, -6.6f, 11, 4, 0.6f, 0, 0, 0, 0x14080a);
        for (int i = 0; i < 6; i++) b.p("sqt" + i, "head", -4.5f + i * 1.8f, 4.4f, -7f, 1.6f, 2.2f, 0.4f, 0, 0, 0, WHT);
        b.p("hatBase", "head", 0, 17, 0, 16, 2.4f, 13, 0, 1, 0, rd);
        for (int i = -1; i <= 1; i++) { b.p("prong" + i, "hatBase", i * 6f, 1, 0, 3.2f, 9 - Math.abs(i), 3.2f, 0, 4.5f, 0, i == 0 ? gold : co); b.p("bell" + i, "prong" + i, 0, 9 - Math.abs(i), 0, 2.6f, 2.6f, 2.6f, 0, 0.6f, 0, gold); }
        b.pr("arm#", "torso", 12, 24, 0, 4.4f, 26, 4.4f, 0, -12, 0, co);
        b.pr("hand#", "arm#", 0, -26, 0, 4.4f, 4.4f, 4.4f, 0, -2, 0, por);
        for (int i = 0; i < 3; i++) b.pr("claw" + i + "#", "hand#", -1.2f + i * 1.2f, -4, -0.6f, 0.8f, 4, 0.8f, 0, -2, 0, gr);
        b.p("tail", "body", 0, 8, 17, 2, 2, 22, 0, 0, 11, gr).sw(0.2f, 0.1f, 0, 1);
    }

    static void bunzo(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int ye = tint(0xf2d23a, f), or = tint(0xf08a30, f), gr = tint(0x3aa850, f), gold = tint(0xf2c030, f);
        B b = Canon.critter(d, ye, tint(0xf8e890, f), nm ? 0x300408 : 0x2a0a10, ye, nm ? RED : 0xffffff, false);
        b.p("overall", "body", 0, 0, -0.2f, 12.6f, 6, 9.6f, 0, 3, 0, gr);
        b.pr("strap#", "body", 3.4f, 6, -4.6f, 1.6f, 6.4f, 0.5f, 0, 3, 0, gr);
        b.pr("btn#", "body", 3.4f, 6, -5f, 1.2f, 1.2f, 0.4f, 0, 0, 0, gold);
        b.pr("ear#", "head", 3.6f, 12, 0, 3.4f, 13, 1.6f, 0, 6.5f, 0, ye);
        b.pr("earIn#", "ear#", 0, 0, -0.9f, 2, 11, 0.4f, 0, 5.5f, 0, or);
        b.pr("bcheek#", "head", 5, 4, -5.6f, 2.6f, 2.6f, 0.4f, 0, 0, 0, or);
        b.pr("buck#", "head", 0.9f, 3.6f, -5.9f, 1.6f, 3, 0.5f, 0, 0, 0, WHT);
        b.p("hat", "head", 0, 12, 0, 7, 5, 7, 0, 2.5f, 0, gr); b.p("hatTop", "hat", 0, 5, 0, 3.4f, 4, 3.4f, 0, 2, 0, gr); b.p("hatPom", "hatTop", 0, 4, 0, 1.8f, 1.8f, 1.8f, 0, 0.6f, 0, gold);
        b.p("tuft", "head", 0, 12, -3, 3, 2, 2, 0, 0, 0, ye);
        b.pr("cymbal#", "hand#", 0, -1, -2.4f, 0.8f, 7, 7, 0, 0, 0, gold);
        if (nm) nm(b, 14, 12, 11);
    }

    static void delight(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int sk = tint(0xf0d0b0, f), hair = tint(0xe8c040, f), sh = tint(0xf2d84a, f), ov = tint(0x8ac0e8, f), sk2 = tint(0xb81c2c, f);
        B b = plush(d, 41);
        b.biped(14, 3.4f, 9, 12, 6, 11, 11, 9, 2.6f, 12, sh, sk, sk);
        b.p("skirt", "body", 0, 0, 0, 13, 7, 9, 0, -1, 0, sk2);
        for (int i = 0; i < 4; i++) b.p("dot" + i, "skirt", -4.5f + i * 3, -3, -4.6f, 1.4f, 1.4f, 0.4f, 0, 0, 0, WHT);
        b.p("overall", "body", 0, 0, -0.2f, 8, 8, 6.4f, 0, 4, 0, ov);
        b.pr("strap#", "body", 2.6f, 12, -3.2f, 1.4f, 5, 0.5f, 0, -2, 0, ov);
        b.p("apple", "body", 0, 4, -3.4f, 2.6f, 2.6f, 0.4f, 0, 0, 0, 0xd02020);
        b.p("collar", "body", 0, 12, -0.2f, 8, 1.4f, 6.4f, 0, 0, 0, WHT);
        b.p("tie", "body", 0, 11, -3.3f, 1.6f, 6, 0.5f, 0, -3, 0, sk2);
        b.pr("puff#", "arm#", 0, 0, 0, 3.6f, 3, 3.6f, 0, 0, 0, WHT);
        b.p("hairTop", "head", 0, 11, 0, 11.6f, 3, 9.6f, 0, 0.4f, 0, hair);
        b.p("hairBack", "head", 0, 0, 4.4f, 11.6f, 11, 2, 0, 5.5f, 0, hair);
        b.pr("hairSide#", "head", 5.6f, 6, 0, 1.6f, 9, 8, 0, 0, 0, hair);
        b.p("bow", "head", 0, 12, -1, 7, 3, 2, 0, 1.4f, 0, sk2);
        b.pr("blush#", "head", 4, 4, -4.7f, 2.2f, 1.6f, 0.3f, 0, 0, 0, 0xe88a80);
        b.pr("eye#", "head", 2.4f, 6.4f, -4.7f, 2.6f, 3, 0.5f, 0, 0, 0, nm ? RED : 0x3a2a20).g();
        b.p("jaw", "head", 0, 2, -4.7f, 6, 2.4f, 0.6f, 0, 0, 0, 0x1a0408);
        b.p("teeth", "head", 0, 3.4f, -4.9f, 5.4f, 0.8f, 0.3f, 0, 0, 0, WHT);
        if (nm) for (int i = 0; i < 6; i++) b.p("nfang" + i, "head", -2.5f + i, 3, -5.1f, 0.7f, 2.6f, 0.3f, 0, -1, 0, WHT);
    }

    static void pj(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int[] seg = {tint(0xf08ab8, f), tint(0xd02a88, f), tint(0xa8a0e0, f)};
        B b = plush(d, 28);
        for (int i = 0; i < 5; i++) {
            b.p("seg" + i, i == 0 ? null : "seg" + (i - 1), 0, i == 0 ? 7 : 0, i == 0 ? 0 : 8, 12 - i, 11 - i * 0.6f, 9, 0, 5, 0, seg[i % 3]).sw(0.08f, 0.15f, i, 1);
            b.pr("limb" + i + "#", "seg" + i, 6 - i * 0.4f, 2, 0, 1.8f, 7, 1.8f, 0, -3, 0, seg[(i + 1) % 3]);
            b.pr("shoe" + i + "#", "limb" + i + "#", 0, -7, -1, 1.8f, 1.4f, 4, 0, 0, -0.6f, 0x8a3ad0);
        }
        b.p("body", "seg0", 0, 0, 0, 1, 1, 1, 0, 0, 0, seg[0]);
        b.p("head", "seg0", 0, 0, -9, 13, 12, 11, 0, 6.4f, 0, seg[0]);
        b.p("muzzle", "head", 0, 2, -6, 8, 5, 3, 0, 0, 0, seg[0]);
        b.p("nose", "head", 0, 5, -7.6f, 3, 2.4f, 1, 0, 0, 0, seg[2]);
        b.pr("cheek#", "head", 5, 3, -5, 2.4f, 3, 1, 0, 0, 0, seg[1]);
        b.pr("eye#", "head", 3.4f, 8.2f, -5.6f, 3.4f, 3.6f, 0.5f, 0, 0, 0, nm ? RED : BLK).g();
        b.pr("tear#", "head", 3.4f, 5.6f, -5.7f, 0.8f, 1.6f, 0.3f, 0, 0, 0, seg[1]);
        b.pr("ear#", "head", 6.6f, 11, 0, 3.6f, 6, 3, 0, 0, 0, seg[2]);
        b.p("jaw", "head", 0, 1, -6, 6, 1.4f, 3, 0, 0, 0, 0x2a0a10);
        b.p("tongue", "jaw", 1, -1, -2, 2, 3, 0.8f, 0, 0, 0, 0xf06a90);
        for (int i = 0; i < 5; i++) b.p("tooth" + i, "head", -2.4f + i * 1.2f, 2, -7.4f, 0.8f, nm ? 2.6f : 1.4f, 0.4f, 0, 0, 0, WHT);
    }

    /** The Doctor (Harley Sawyer): organs kept alive in a vital system centre; yellow eye with fingers on a static screen; mechanical vessel limbs. */
    static void doctor(CreatureDef d) {
        int gl = 0x9ad8e8, br = 0xe89aa8, lu = 0xd86a7a, lv = 0x8a2a2a, mt = 0x5a5a62, ye = 0xffd820;
        B b = plush(d, 40);
        b.pr("leg#", null, 6, 6, 0, 4, 6, 4, 0, -3, 0, mt, 0.7f);
        b.p("body", null, 0, 6, 0, 18, 6, 14, 0, 3, 0, mt).sw(0.02f, 0.1f, 0, 2);
        b.p("tank", "body", 0, 6, 0, 16, 22, 12, 0, 11, 0, gl);
        b.p("fluid", "tank", 0, 0, 0, 14, 18, 10, 0, 10, 0, 0xb8f0d0);
        b.p("head", "tank", 0, 20, 0, 9, 7, 8, 0, 3, 0, br);
        b.p("lungL", "tank", -4, 10, 0, 5, 8, 5, 0, 0, 0, lu); b.p("lungR", "tank", 4, 10, 0, 5, 8, 5, 0, 0, 0, lu);
        b.p("liver", "tank", 0, 3, -1, 8, 4, 5, 0, 0, 0, lv);
        for (int i = 0; i < 3; i++) b.p("tube" + i, "tank", -4 + i * 4, 22, 0, 1, 8, 1, 0, 4, 0, mt);
        b.p("screen", "body", 0, 12, -7.4f, 12, 9, 0.8f, 0, 0, 0, 0x2a2a30);
        b.p("static", "screen", 0, 0, -0.5f, 11, 8, 0.3f, 0, 0, 0, 0xb8b8c0);
        b.p("eye", "screen", 0, 0, -0.9f, 4, 4, 0.4f, 0, 0, 0, ye).g();
        b.p("pupil", "eye", 0, 0, -0.3f, 1.4f, 1.4f, 0.3f, 0, 0, 0, BLK);
        for (int i = 0; i < 4; i++) b.p("finger" + i, "screen", -3 + i * 2, 4.4f, -1, 0.8f, 3, 0.8f, 0, 1.5f, 0, mt);
        b.p("jaw", "head", 0, 0, 0, 1, 1, 1, 0, 0, 0, br);
        b.pr("arm#", "body", 10, 14, 0, 2, 16, 2, 0, -8, 0, mt);
        b.pr("hand#", "arm#", 0, -16, 0, 4, 3, 4, 0, -1, 0, ye);
    }

    static {
        MAP.put("harley_sawyer", Canon2::doctor);
        MAP.put("giant_boogie_bot", d -> Canon.MAP2.get("boogie_bot").accept(d));
        crit("catnap", 0x8a50b8, 0xc9a0e6, 0x5a3080, 0xf2d24a, b -> {
            b.pr("ear#", "head", 4.6f, 12, 0, 3.6f, 5.4f, 1.8f, 0, 2.4f, 0, b.d.parts.get(0).color);
            b.p("snout", "head", 0, 4, -5.8f, 6, 4, 2, 0, 0, 0, 0xc9a0e6); b.p("nose", "head", 0, 5.4f, -6.8f, 2, 1.4f, 0.8f, 0, 0, 0, 0xd06a90);
            b.pr("whisk#", "head", 5, 4, -5.2f, 4, 0.3f, 0.3f, 0, 0, 0, 0x3a2050);
        });
        crit("dogday", 0xf0a030, 0xfbe6b0, 0x9a5a18, 0xffe040, b -> {
            b.pr("ear#", "head", 7.4f, 10, 0, 2, 8, 4, 0, -3.6f, 0, 0xb06a1c);
            b.p("snout", "head", 0, 4, -6, 7, 5, 3, 0, 0, 0, 0xfbe6b0); b.p("nose", "head", 0, 6, -7.6f, 3, 2, 1, 0, 0, 0, BLK);
        });
        crit("bubba", 0x4aa8d8, 0x8ad0f0, 0x2a78b0, 0xffe860, b -> {
            b.pr("ear#", "head", 8.4f, 9, 0, 1.6f, 11, 9, 0, -1, 0, 0x4aa8d8);
            b.pr("earIn#", "ear#", 0, 0, -0.9f, 0.6f, 8, 6, 0, -1, 0, 0x2a78b0);
            b.p("trunk1", "head", 0, 5, -5.6f, 4, 4, 4, 0, 0, -1, 0x4aa8d8); b.p("trunk2", "trunk1", 0, -3, -3, 3.4f, 6, 3.4f, 0, -2, 0, 0x4aa8d8); b.p("trunk3", "trunk2", 0, -5, -1, 3.2f, 3, 3.6f, 0, 0, -1, 0x4aa8d8);
            b.p("bulbTail", "tail", 0, 0, 9, 3.4f, 3.4f, 3.4f, 0, 0, 0, 0x2a78b0);
        });
        crit("hoppy", 0x8ad84a, 0xc8f090, 0x5ab030, 0xf2c020, b -> {
            b.pr("ear#", "head", 3.6f, 12, 0, 3, 14, 1.6f, 0, 7, 0, 0x8ad84a);
            b.pr("earT#", "ear#", 0, 6, -0.9f, 1.4f, 7, 0.4f, 0, 0, 0, 0x5ab030);
            b.p("nose", "head", 0, 5.4f, -5.9f, 2, 1.4f, 0.6f, 0, 0, 0, BLK);
            b.p("cotton", "tail", 0, 0, 3, 4.4f, 4.4f, 4.4f, 0, 0, 0, 0xc8f090);
            b.p("tube", "head", 5, 3, -5.8f, 0.8f, 0.8f, 3, 0, 0, 0, 0x9a9aa0);
        });
        crit("kickin", 0xf4d63a, 0xfbec90, 0xf08a20, 0xf08a20, b -> {
            b.p("beakU", "head", 0, 5.4f, -6.2f, 6, 2.6f, 3, 0, 0, 0, 0xf08a20);
            for (int i = 0; i < 3; i++) b.p("crown" + i, "head", 3 - i * 2.2f, 12, -1, 1.6f, 4 - i * 0.6f, 1.6f, 0, 2, 0, 0xf4d63a);
            b.pr("wing#", "arm#", 0, -2, 1.2f, 3, 8, 1, 0, -2, 0, 0xf4d63a);
        });
        crit("craftycorn", 0xf4f4f8, 0xe0f0f4, 0x40d0c8, 0xf06ab0, b -> {
            b.pr("ear#", "head", 4.4f, 12, 0, 3, 5, 1.6f, 0, 2.4f, 0, 0xf4f4f8);
            b.pr("earIn#", "ear#", 0, 0, -0.9f, 1.6f, 3.4f, 0.4f, 0, 2, 0, 0x40d0c8);
            for (int i = 0; i < 3; i++) b.p("horn" + i, "head", 0, 12 + i * 2.6f, -1, 3 - i, 2.8f, 3 - i, 0, 1.4f, 0, 0xc8ccd8);
            b.p("mane", "head", 0, 12, 4, 4, 12, 3, 0, -4, 0, 0x40d0c8);
            b.p("maneTop", "head", 0, 12, -2, 5, 2.6f, 4, 0, 1, 0, 0x40d0c8);
            b.p("snout", "head", 0, 3.4f, -5.8f, 7, 4, 2, 0, 0, 0, 0xf4f4f8);
            b.p("tailC", "tail", 0, 0, 3, 3.4f, 3.4f, 9, 0, 0, 4, 0x40d0c8);
        });
        crit("pickypiggy", 0xf6a8c0, 0xfbd0dc, 0xd87898, 0xe02828, b -> {
            b.p("snout", "head", 0, 3.6f, -6.2f, 7, 5, 3, 0, 0, 0, 0xd87898);
            b.pr("nostril#", "snout", 1.4f, 0, -1.6f, 1.2f, 2, 0.4f, 0, 0, 0, 0x8a3050);
            b.pr("ear#", "head", 5.6f, 11, 0, 4, 4, 1.8f, 0, 1, -0.6f, 0xf6a8c0);
            b.pr("earIn#", "ear#", 0, 0, -1, 2.4f, 2.6f, 0.4f, 0, 1, 0, 0xd87898);
            b.p("curl", "tail", 0, 0, 3, 2, 2, 2, 0, 1, 1, 0xf6a8c0); b.p("curl2", "curl", 0, 2, 1, 2, 2, 2, 0, 0, 1, 0xf6a8c0);
        });
        crit("bobby", 0xc83a3a, 0xf0a0b8, 0xf0a0b8, 0xd01040, b -> {
            b.pr("ear#", "head", 5.6f, 11, 0, 4, 4, 2.4f, 0, 1.4f, 0, 0xc83a3a);
            b.pr("earIn#", "ear#", 0, 0, -1.3f, 2.4f, 2.4f, 0.4f, 0, 1.4f, 0, 0xf0a0b8);
            b.p("snout", "head", 0, 3.6f, -6f, 6.4f, 4.4f, 2.4f, 0, 0, 0, 0xf0a0b8); b.p("nose", "head", 0, 5.4f, -7.3f, 2.6f, 1.8f, 0.6f, 0, 0, 0, BLK);
        });
        MAP.put("bubba_bubbaphant", MAP.get("bubba")); MAP.put("hoppy_hopscotch", MAP.get("hoppy")); MAP.put("kickin_chicken", MAP.get("kickin")); MAP.put("bobby_bearhug", MAP.get("bobby"));
        MAP.put("mini_bubba", MAP.get("bubba"));
        for (String s : new String[]{"mini_huggy", "mini_huggies", "mini_plush"}) MAP.put(s, d -> huggy(d, 0x2f6fe0, 0x1c3f94, 0xd8262b, 0xf4d12a, false, false));
        MAP.put("nightmare_huggy", d -> huggy(d, 0x1a3070, 0x0e1a44, 0x7a0e12, 0xd01818, true, false));
        for (String s : new String[]{"kissy_missy", "mini_kissy", "giant_kissy"}) kissy(s, false);
        kissy("nightmare_kissy_missy", true);
        MAP.put("mommy_long_legs", d -> longLegs(d, false, false, 0));
        MAP.put("mini_mommy", d -> longLegs(d, false, false, 0));
        MAP.put("nightmare_mommy", d -> longLegs(d, false, true, 0));
        MAP.put("mama_long_legs", d -> longLegs(d, false, false, 2));
        MAP.put("daddy_long_legs", d -> longLegs(d, true, false, 0));
        MAP.put("bron", d -> bron(d, false)); MAP.put("giant_bron", d -> bron(d, false));
        MAP.put("boxy_boo", d -> boxy(d, false)); MAP.put("mini_boxy_boo", d -> boxy(d, false)); MAP.put("nightmare_boxy_boo", d -> boxy(d, true));
        for (String s : new String[]{"doey_the_doughman", "mini_doey", "mini_doughman"}) MAP.put(s, Canon2::doey);
        MAP.put("yarnaby", Canon2::yarnaby); MAP.put("mini_yarnaby", Canon2::yarnaby);
        MAP.put("the_prototype", d -> prototype(d, false)); MAP.put("mini_prototype", d -> prototype(d, false)); MAP.put("nightmare_prototype", d -> prototype(d, true));
        MAP.put("bunzo_bunny", d -> bunzo(d, false)); MAP.put("nightmare_bunzo", d -> bunzo(d, true));
        MAP.put("miss_delight", d -> delight(d, false)); MAP.put("nightmare_miss_delight", d -> delight(d, true));
        MAP.put("pj_pug_a_pillar", d -> pj(d, false)); MAP.put("mini_pj", d -> pj(d, false)); MAP.put("nightmare_pj", d -> pj(d, true));
    }
}
