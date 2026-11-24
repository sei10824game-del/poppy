package com.poppy.mod.creature;

import com.poppy.mod.creature.Canon.B;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Canon rebuilds, part 4: Nightmare Critters (toy style), Wrongside Outimals (ball bodies, X-button eyes), Glowby, Boogie Bot, Lily, Cat-Bee, Candy Cat. */
final class Canon5 {
    static final int BLK = 0x0b0b0e, WHT = 0xf4f1ea;
    static final Map<String, Consumer<CreatureDef>> MAP = new HashMap<>();
    static float PI = (float) Math.PI;

    static B plush(CreatureDef d, float px) { return new B(d, d.h * 16f / px); }

    /** Rounded-cube "ball": core + three slabs. */
    static void ball(B b, String n, String par, float px, float py, float pz, float r, int c) {
        b.p(n, par, px, py, pz, 1.8f * r, 1.8f * r, 1.8f * r, 0, 0, 0, c);
        b.p(n + "X", n, 0, 0, 0, 2 * r, 1.2f * r, 1.2f * r, 0, 0, 0, c);
        b.p(n + "Y", n, 0, 0, 0, 1.2f * r, 2 * r, 1.2f * r, 0, 0, 0, c);
        b.p(n + "Z", n, 0, 0, 0, 1.2f * r, 1.2f * r, 2 * r, 0, 0, 0, c);
    }
    /** Button eye with X cross. */
    static void xeye(B b, String n, String par, float x, float y, float z, float s, int rim, int cross) {
        b.p(n, par, x, y, z, s, s, 0.8f, 0, 0, 0, rim);
        b.p(n + "in", par, x, y, z - 0.5f, s * 0.7f, s * 0.7f, 0.5f, 0, 0, 0, 0x1a1a1e);
        b.p(n + "x1", par, x, y, z - 0.9f, s * 0.6f, s * 0.14f, 0.3f, 0, 0, 0, cross);
        b.p(n + "x2", par, x, y, z - 0.9f, s * 0.14f, s * 0.6f, 0.3f, 0, 0, 0, cross);
    }

    /** Nightmare Critter on the toy rig: glare brows, toothy open maw, species extras. */
    static void crit(String id, int fur, int rim, int claw, int pend, Consumer<B> extra) {
        MAP.put(id, d -> {
            B b = Canon4.toy(d, fur, rim, claw, pend, rim);
            for (var q : d.parts) if (q.name.equals("decalFace")) q.decal = "toy_nm";
            b.pr("brow#", "head", 5.4f, 14, -7.2f, 6, 1.6f, 0.8f, 0, 0, 0, BLK);
            b.pr("fangU#", "head", 3.6f, 9.6f, -7.6f, 1.2f, 2.6f, 0.5f, 0, -1.3f, 0, WHT);
            extra.accept(b);
        });
    }

    static void outimalBody(B b, int c, int limb, int foot, float bw) {
        b.pr("leg#", null, 3, 5, 0, 4, 5, 4, 0, -2.5f, 0, limb, 0.8f);
        b.pr("foot#", "leg#", 0, -5, -1.2f, 5, 2.4f, 6.4f, 0, 1.2f, 0, foot);
        b.p("body", null, 0, 5, 0, bw, 7, 7, 0, 3.5f, 0, c).sw(0.03f, 0.1f, 0, 2);
        b.pr("arm#", "body", bw / 2 + 1, 6, 0, 2.6f, 7, 3, 0, -2.6f, 0, limb);
        b.pr("hand#", "arm#", 0, -6.4f, 0, 3.2f, 2.4f, 3.4f, 0, -0.6f, 0, foot);
    }

    static {
        crit("maggie_mako", 0x3ab8c8, 0xf4f4f4, 0x1e8aa0, 0xf4e8c0, b -> {
            b.p("finTop", "head", 0, 15, 0, 1.6f, 6, 7, 0, 3, 0, 0x1e8aa0);
            b.pr("tooth#", "head", 3.4f, 5.4f, -7.6f, 1.4f, 2.4f, 0.5f, 0, -1.2f, 0, WHT);
            b.pr("fin#", "body", 6.6f, 4, 1, 0.8f, 6, 4, 0, 0, 0, 0x1e8aa0);
            b.p("tailFin", "tail", 0, 0, 5, 1.2f, 7, 4, 0, 2, 2, 0x1e8aa0);
            for (int i = 0; i < 5; i++) b.p("jag" + i, "head", -4.8f + i * 2.4f, 5.8f, -7.6f, 1.6f, 2.2f, 0.5f, 0, -1.1f, 0, WHT);
        });
        crit("allister_gator", 0x8ab42a, 0x5a7a1a, 0x5a7a1a, 0xe8c0d0, b -> {
            b.p("snoutTop", "head", 0, 12.6f, -7.6f, 14, 3, 1.4f, 0, 0, 0, 0x8ab42a);
            b.pr("eyeBump#", "head", 5.2f, 15.4f, -4, 5, 2, 5, 0, 0, 0, 0x8ab42a);
            for (int i = 0; i < 4; i++) b.p("back" + i, "body", 0, 9 - i * 2.2f, 4.8f, 1.6f, 2.2f, 1.6f, 0, 0, 0, 0x5a7a1a);
            b.p("tailG", "tail", 0, 0, 5, 3, 3, 9, 0, 0, 4, 0x8ab42a);
            for (int i = 0; i < 4; i++) b.p("spk" + i, "head", -4.5f + i * 3, 5.8f, -7.6f, 1, 2.2f, 0.5f, 0, -1.1f, 0, WHT);
        });
        crit("poe", 0x5a5a78, 0x2a2a3c, 0x3a3a52, 0xf2d24a, b -> {
            b.p("horn", "head", 0, 15, -3, 2.4f, 5, 2.4f, 0, 2.5f, 0, 0x2a2a3c);
            b.pr("hornS#", "head", 6, 14, -1, 2, 3, 2, 0, 1.5f, 0, 0x2a2a3c);
            b.pr("wing#", "body", 6.4f, 9, 4, 1.4f, 8, 5, 0, -3, 0, 0x3a6a5a);
            b.p("tailP", "tail", 0, 0, 5, 3, 2, 8, 0, 0, 4, 0x3a6a5a);
        });
        crit("rabie_baby", 0xf0a8d8, 0xf8d8ee, 0xc868b0, 0xd02838, b -> {
            b.pr("ear#", "head", 6.8f, 15, 0, 3, 11, 1.8f, 0, 5.5f, 0, 0xf0a8d8);
            b.pr("earIn#", "ear#", 0, 0, -1, 1.8f, 8.6f, 0.4f, 0, 4.4f, 0, 0xf8d8ee);
            b.pr("eyeRing#", "head", 5.2f, 11, -7.3f, 6.2f, 6.2f, 0.4f, 0, 0, 0, 0xa0408a);
            b.pr("fang#", "head", 2.8f, 5.6f, -7.6f, 1.2f, 3.2f, 0.5f, 0, -1.6f, 0, WHT);
            b.pr("wingR#", "body", 6.8f, 9, 4, 0.8f, 8, 6, 0, -3, 2, 0xe090c8);
        });
        crit("simon_smoke", 0xe85a30, 0xf4e8d0, 0xa8281a, 0xff8a30, b -> {
            b.pr("horn#", "head", 5, 15, 0, 2, 5, 2, 0, 2.5f, 0, 0xf2c230);
            b.pr("ear#", "head", 9.6f, 11, 0, 1.8f, 5, 3, 0, 2, 0, 0xe85a30);
            b.p("mane", "head", 0, 15, 2, 12, 3, 6, 0, 0, 0, 0xa8281a);
            b.pr("wing#", "body", 6.8f, 9, 4, 1, 9, 8, 0, -2, 3, 0xf4e8d0);
            for (int i = 0; i < 3; i++) b.p("tsp" + i, "tail", 0, 2, 1 + i * 2, 1.4f, 2.4f, 1.4f, 0, 0, 0, 0xf2c230);
            b.pr("fang#", "head", 3, 5.6f, -7.6f, 1.2f, 2.6f, 0.5f, 0, -1.3f, 0, WHT);
        });
        crit("icky_licky", 0x141418, 0xf0c020, 0x141418, 0xc02020, b -> {
            b.pr("eyeBulge#", "head", 5.6f, 15, -3, 5.4f, 3, 5.4f, 0, 0, 0, 0x141418);
            b.pr("blotch#", "body", 3.4f, 7, -4.8f, 4.4f, 3, 0.5f, 0, 0, 0, 0xf0c020);
            b.pr("blotchL#", "leg#", 0, -3, 0, 5, 2.4f, 5.4f, 0, 0, 0, 0xf0c020);
            b.p("blotchH", "head", -4, 13, -7.3f, 5, 2, 0.5f, 0, 0, 0, 0xf0c020);
        });
        crit("baba_chops", 0x161616, 0xe8d8c0, 0xe8d8c0, 0xd8d0b8, b -> {
            b.p("woolHead", "head", 0, 14, 0, 18, 4, 14, 0, 0.5f, 0, 0x161616);
            b.pr("woolSide#", "head", 9, 8, 0, 2.6f, 10, 12, 0, 0, 0, 0x161616);
            b.pr("ear#", "head", 10.6f, 11, 0, 4.4f, 2.4f, 2, 0, 0, 0, 0xe8d8c0);
            b.pr("buck#", "head", 1.2f, 6, -7.6f, 2, 3.4f, 0.5f, 0, -1.7f, 0, WHT);
            b.p("woolBody", "body", 0, 0, 0, 13, 10, 10, 0, 5, 0, 0x161616);
            b.p("cotton", "tail", 0, 0, 5, 4.4f, 4.4f, 4.4f, 0, 0, 0, WHT);
        });
        crit("touille", 0x484850, 0x6a6a74, 0x3a3a42, 0x4ab0c8, b -> {
            b.pr("ear#", "head", 8.6f, 13, 0, 7, 7, 1.6f, 0, 3, 0, 0xe06a4a);
            b.pr("earRim#", "ear#", 0, 0, 0.5f, 7.8f, 7.8f, 0.6f, 0, 3, 0, 0x484850);
            b.p("nose", "head", 0, 10.6f, -7.4f, 3, 2.4f, 1, 0, 0, 0, 0xe06a4a);
            b.p("incisor", "head", 0, 8, -7.6f, 2.6f, 3.4f, 0.5f, 0, -1.7f, 0, 0xe0e0e8);
            b.p("tailR", "tail", 0, 0, 5, 1.4f, 1.4f, 14, 0, 0, 7, 0xe06a4a);
        });

        MAP.put("blanky_blankz", d -> {
            int w = 0xd8d4cc, lt = 0xece8e0;
            B b = plush(d, 28); outimalBody(b, w, w, w, 8);
            b.p("head", "body", 0, 7, 0, 1, 1, 1, 0, 0, 0, w);
            ball(b, "ball", "head", 0, 7, 0, 8.4f, lt);
            xeye(b, "xL", "ball", -4.6f, 3.4f, -7.8f, 5.2f, 0xe8e4dc, 0xc8c4bc); xeye(b, "xR", "ball", 4.6f, 3.4f, -7.8f, 5.2f, 0xe8e4dc, 0xc8c4bc);
            b.p("jaw", "ball", 0, -3.4f, -8.1f, 12, 1.8f, 0.5f, 0, 0, 0, 0x6a6a72);
            b.p("zipH", "ball", 0, -3.4f, -8.4f, 14, 1, 0.4f, 0, 0, 0, 0xc8c8d0);
            b.p("pull", "ball", 7, -3.4f, -8.5f, 2.4f, 3.4f, 0.4f, 0, -1, 0, 0x9a9aa4);
        });
        MAP.put("bubby_chumz", d -> {
            int rd = 0xd02a30, ye = 0xf0c020, tl = 0x30c0a8;
            B b = plush(d, 26);
            for (int i = 0; i < 4; i++) { float x = (i % 2 == 0 ? -1 : 1) * 4.6f, z = (i < 2 ? -1 : 1) * 3.6f;
                String n = i < 2 ? (i == 0 ? "legL" : "legR") : (i == 2 ? "legBL" : "legBR");
                b.p(n, null, x, 7, z, 4.4f, 8, 4.4f, 0, -4, 0, rd); d.walk(0.7f, i % 2 == 0 ? 0 : PI);
                b.p(n + "b", n, 0, -5, 0, 4.8f, 1, 4.8f, 0, 0, 0, i % 2 == 0 ? ye : tl); }
            b.p("body", null, 0, 7, 0, 5, 3, 5, 0, 0, 0, rd).sw(0.03f, 0.1f, 0, 2);
            ball(b, "dome", "body", 0, 7, 0, 8.6f, rd);
            b.p("zipBand", "dome", 0, -0.4f, -8.4f, 15.6f, 1, 0.5f, 0, 0, 0, 0xd8d8e0);
            b.p("pull", "dome", 7, -1.2f, -8.6f, 2.4f, 3.4f, 0.4f, 0, 0, 0, 0x9a9aa4);
            b.p("head", "dome", 0, 3, 0, 1, 1, 1, 0, 0, 0, rd);
            xeye(b, "xL", "dome", -3.4f, 3, -8.4f, 2.6f, 0xc8c8d0, 0xc8c8d0); xeye(b, "xR", "dome", 3.4f, 3, -8.4f, 2.6f, 0xc8c8d0, 0xc8c8d0);
            b.p("jaw", "dome", 0, -3.6f, -8.3f, 6, 1.4f, 0.4f, 0, 0, 0, 0x6a0a14);
            b.p("patchY", "dome", -3, 8, 0, 5, 1, 5, 0, 0, 0, ye); b.p("patchT", "dome", 4, 8, 1, 4, 1, 4, 0, 0, 0, tl);
            b.pr("arm#", "body", 8, 6, 0, 3, 8, 3, 0, -3, 0, rd);
        });
        MAP.put("doodle_bones", d -> {
            int bl = 0x7ab0d8, pk = 0xf0a0c8, tl = 0x4ab8b0;
            B b = plush(d, 30); outimalBody(b, bl, bl, pk, 8);
            b.p("head", "body", 0, 7, 0, 1, 1, 1, 0, 0, 0, bl);
            ball(b, "ball", "head", 0, 7.4f, 0, 7.6f, bl);
            b.p("zipCap", "ball", 0, 7, 0, 5, 1.6f, 4, 0, 0, 0, tl); b.p("zipTab", "ball", 0, 8, -0.4f, 1.4f, 2.6f, 0.4f, 0, 0, 0, 0xc8c8d0);
            xeye(b, "xL", "ball", -4, 2, -7.2f, 4.6f, 0xe8e8ee, 0xe8e8ee); xeye(b, "xR", "ball", 4, 2, -7.2f, 4.6f, 0xe8e8ee, 0xe8e8ee);
            b.p("trunk", "ball", 0, -1, -7.6f, 4.6f, 9, 2, 0, -3, 0, pk);
            b.p("jaw", "ball", 0, -6, -7.2f, 5, 1, 0.4f, 0, 0, 0, 0x3a1a3a);
            b.pr("ear#", "ball", 9.4f, 1, 0, 1.8f, 9, 9, 0, 0, 0, pk);
            b.pr("earIn#", "ear#", 0, 0, -1, 0.6f, 6, 6, 0, 0, 0, 0xf8c8de);
        });
        MAP.put("goober_gutz", d -> {
            int pu = 0x9a58c0, lil = 0xc890e0, pk = 0xf0a0c8;
            B b = plush(d, 30); outimalBody(b, pu, pu, 0xb070d0, 8);
            b.p("head", "body", 0, 7, 0, 1, 1, 1, 0, 0, 0, pu);
            ball(b, "ball", "head", 0, 7.4f, 0, 7.6f, pu);
            b.p("band", "ball", 0, 3.4f, -0.3f, 16.4f, 4, 15.8f, 0, 0, 0, lil); b.p("knot", "ball", 7, 3.4f, 5, 4, 4, 3, 0, 0, 0, lil);
            b.p("zipB", "ball", 0, 5, -7.9f, 1, 4, 0.5f, 0, 0, 0, 0xc8c8d0);
            b.pr("ear#", "ball", 5.2f, 8.4f, 0, 4, 5, 2, 0, 2, 0, pu);
            xeye(b, "xL", "ball", -4.2f, 1.2f, -7.4f, 5.2f, 0xe8e8ee, 0xe8e8ee); xeye(b, "xR", "ball", 4.2f, 1.2f, -7.4f, 5.2f, 0xe8e8ee, 0xe8e8ee);
            b.p("jaw", "ball", 0, -4.4f, -7.4f, 5.6f, 1.8f, 0.6f, 0, 0, 0, 0xd8709c);
        });
        MAP.put("loopsy_noodle", d -> {
            int pk = 0xf08ac0, dk = 0xd0609a;
            B b = plush(d, 30); outimalBody(b, pk, pk, pk, 8);
            b.p("scarf", "body", 0, 7, 0, 7, 1.6f, 5, 0, 0, 0, 0x4a6ad0);
            b.p("head", "body", 0, 7, 0, 1, 1, 1, 0, 0, 0, pk);
            ball(b, "ball", "head", 0, 7.6f, 0, 7.8f, pk);
            b.pr("ear#", "ball", 3.6f, 7, 0, 3.6f, 13, 2, 0, 6.5f, 0, pk);
            b.pr("earIn#", "ear#", 0, 0, -1.1f, 2, 10, 0.4f, 0, 5.5f, 0, dk);
            xeye(b, "xL", "ball", -4.6f, 1.6f, -7.6f, 5.4f, 0xe8e8ee, 0xe8e8ee); xeye(b, "xR", "ball", 4.6f, 1.6f, -7.6f, 5.4f, 0xe8e8ee, 0xe8e8ee);
            b.p("snout", "ball", 0, -2.6f, -8f, 3.4f, 2.4f, 1, 0, 0, 0, dk);
            b.p("jaw", "ball", 0, -3.6f, -8.1f, 2, 1.2f, 0.5f, 0, 0, 0, 0x3a1a2a);
            b.p("zipT", "ball", 0, 8, -0.4f, 1.6f, 2.4f, 0.5f, 0, 0, 0, 0xc8c8d0);
        });
        MAP.put("glowby", d -> {
            int ye = 0xe8bc28, dk = 0xb8901c;
            B b = plush(d, 26);
            b.pr("leg#", null, 3.4f, 5, 0, 3, 5, 3, 0, -2.5f, 0, dk, 0.8f);
            b.p("body", null, 0, 5, 0, 15, 15, 5, 0, 8.5f, 0, ye).sw(0.03f, 0.1f, 0, 2);
            b.p("topP", "body", 0, 16, 0, 6, 6, 5, 0, 0, 0, ye);
            b.pr("sideP#", "body", 8.4f, 11, 0, 6, 5, 5, 0, 0, 0, ye);
            b.pr("lowP#", "body", 5, 1, 0, 6, 6, 5, 0, 0, 0, ye);
            b.p("head", "body", 0, 16, 0, 1, 1, 1, 0, 0, 0, ye);
            b.p("ring", "body", 0, 8.5f, -2.8f, 12, 12, 1, 0, 0, 0, 0x8a8a90);
            b.p("screen", "body", 0, 8.5f, -3.4f, 10, 10, 0.5f, 0, 0, 0, 0x08080c);
            b.pr("eye#", "body", 2, 10.6f, -3.9f, 1, 3.4f, 0.3f, 0, 0, 0, 0xf4f4f4).g();
            for (int i = 0; i < 5; i++) b.p("smile" + i, "body", -3.2f + i * 1.6f, 6.4f - (i == 0 || i == 4 ? 0 : (i == 2 ? 1.6f : 0.8f)), -3.9f, 1, 0.8f, 0.3f, 0, 0, 0, 0xf4f4f4).g();
            b.p("antenna", "body", 5, 14, 0, 0.8f, 7, 0.8f, 0, 3.5f, 0, 0xd8d0b0);
            b.p("btnR", "body", 9, 11, -2.6f, 1.6f, 1.6f, 0.8f, 0, 0, 0, 0xd83a3a); b.p("btnG", "body", 11, 12.4f, -2.6f, 1.6f, 1.6f, 0.8f, 0, 0, 0, 0x3a9a5a);
            b.p("speaker", "body", -5, 2, -2.8f, 4, 4, 0.6f, 0, 0, 0, dk);
            b.pr("arm#", "body", 8, 6, 0, 2.2f, 6, 2.6f, 0, -2.5f, 0, dk);
        });
        MAP.put("boogie_bot", d -> {
            int gr = 0x2a9a3a, dg = 0x1a6a2a, tr = 0x2a2a30, ye = 0xffe840;
            B b = plush(d, 34);
            b.pr("leg#", null, 5, 7, 0, 6, 7, 16, 0, -3, 0, tr, 0.8f);
            b.p("body", null, 0, 7, 0, 10, 8, 8, 0, 4, 0, gr).sw(0.03f, 0.1f, 0, 2);
            b.p("head", "body", 0, 8, 0, 15, 13, 12, 0, 6.5f, 0, gr).sw(0.05f, 0.1f, 0, 2);
            b.p("visor", "head", 0, 7, -6.1f, 12, 8, 0.8f, 0, 0, 0, 0x08080c);
            b.pr("eye#", "head", 3, 7.4f, -6.6f, 1.6f, 5, 0.4f, 0, 0, 0, ye).g();
            b.pr("phone#", "head", 7.8f, 6.6f, 0, 2.4f, 7, 7, 0, 0, 0, dg);
            b.p("antenna", "head", 4, 13, 0, 0.7f, 5, 0.7f, 0, 2.5f, 0, 0x4acf4a);
            b.p("jaw", "head", 0, 3, -6.4f, 5, 0.8f, 0.3f, 0, 0, 0, ye);
            b.pr("arm#", "body", 8.4f, 6.4f, 0, 5, 3.4f, 4.4f, 2.4f, 0, 0, gr);
            b.pr("hand#", "arm#", 3.6f, 0, 0, 4.4f, 4.4f, 4.4f, 0, 0, 0, dg);
            b.pr("tread#", "leg#", 0, -4, 0, 6.4f, 1.2f, 16.4f, 0, 0, 0, 0x4a4a52);
        });
        MAP.put("giant_boogie_bot", MAP.get("boogie_bot"));
        MAP.put("cat_bee", d -> {
            int or = 0xe8a838, br = 0x6a4020, wing = 0x3a8a4a;
            B b = plush(d, 26);
            b.pr("leg#", null, 3, 6, 0, 3.4f, 6, 3.6f, 0, -3, 0, or, 0.8f);
            b.pr("paw#", "leg#", 0, -6, -1, 4, 1.6f, 5.4f, 0, 0.8f, 0, 0xf4ecd8);
            b.p("body", null, 0, 6, 0, 9, 9, 7, 0, 4.5f, 0, or).sw(0.03f, 0.1f, 0, 2);
            for (int i = 0; i < 3; i++) b.p("stripe" + i, "body", 0, 2 + i * 2.6f, 0, 9.4f, 1.1f, 7.4f, 0, 0, 0, br);
            b.p("head", "body", 0, 9, -0.5f, 12, 10, 9, 0, 5, 0, or).sw(0.05f, 0.1f, 1, 2);
            b.pr("ear#", "head", 4.4f, 10, 0, 3.6f, 4.6f, 1.8f, 0, 2.2f, 0, or);
            b.pr("anten#", "head", 2.6f, 10, 0, 0.7f, 6, 0.7f, 0, 3, 0, br).sw(0.12f, 0.2f, 0, 0);
            b.pr("antB#", "anten#", 0, 6, 0, 2, 2, 2, 0, 0, 0, br);
            b.pr("eyeW#", "head", 2.8f, 5.6f, -4.6f, 3.6f, 2.8f, 0.5f, 0, 0, 0, 0xe8e0a8);
            b.pr("lid#", "head", 2.8f, 6.8f, -4.9f, 4, 1.8f, 0.5f, 0, 0, 0, 0x8a5a28);
            b.pr("lash#", "head", 4.8f, 6.4f, -4.9f, 1.4f, 0.6f, 0.4f, 0, 0, 0, BLK);
            b.pr("pupil#", "head", 2.8f, 5, -5f, 1.2f, 1.2f, 0.3f, 0, 0, 0, BLK);
            b.p("muzzle", "head", 0, 2, -4.6f, 5, 3, 0.6f, 0, 0, 0, 0xf4ecd8);
            b.p("nose", "head", 0, 4, -5f, 1.6f, 1.2f, 0.5f, 0, 0, 0, 0xc86a5a);
            b.p("jaw", "head", 0, 1.6f, -4.8f, 3, 1.6f, 0.5f, 0, 0, 0, 0x5a2a20);
            b.pr("wing#", "body", 4, 8, 3.6f, 0.6f, 8, 5, 0, 2, 1, wing).sw(0.15f, 0.7f, 0, 2);
            b.pr("arm#", "body", 5, 8, -1, 2.4f, 6, 2.4f, 0, -2.4f, 0, or);
            b.p("tail", "body", 0, 2, 3.6f, 1.8f, 1.8f, 9, 0, 0, 4.5f, or).sw(0.2f, 0.1f, 0, 1);
            for (int i = 0; i < 3; i++) b.p("tailS" + i, "tail", 0, 0, 2 + i * 2.8f, 2.1f, 2.1f, 1, 0, 0, 0, br);
        });
        MAP.put("lily_lovebraids", d -> {
            int sk = 0xe0b090, hr = 0x6a20c8, hd = 0x4a10a0, dr = 0x2a2a34, tp = 0x9a6ad8, st = 0xf2d23a;
            B b = plush(d, 40);
            b.pr("leg#", null, 2.6f, 12, 0, 2.6f, 12, 2.6f, 0, -6, 0, 0x3a3a46, 0.8f);
            b.pr("sock#", "leg#", 0, -7, 0, 2.9f, 3, 2.9f, 0, 0, 0, 0x6a3ab0);
            b.pr("shoe#", "leg#", 0, -12, -1.4f, 5, 3.6f, 6.4f, 0, 1.8f, 0, 0x8a3ae0);
            b.p("body", null, 0, 12, 0, 7, 8, 5, 0, 4, 0, tp).sw(0.03f, 0.1f, 0, 2);
            b.p("skirt", "body", 0, 0, 0, 10, 5, 7.4f, 0, -1.5f, 0, dr);
            b.p("star", "body", 0, 4, -2.7f, 3, 3, 0.4f, 0, 0, 0, st);
            b.p("head", "body", 0, 8, 0, 10, 10, 8.6f, 0, 5, 0, sk).sw(0.06f, 0.1f, 1, 2);
            b.p("hairTop", "head", 0, 10, 0, 11, 3, 9.6f, 0, 0.4f, 0, hr);
            b.p("hairBack", "head", 0, 0, 4.6f, 11, 11, 2, 0, 5.5f, 0, hr);
            b.p("bang", "head", -2, 9.6f, -4.3f, 5, 2, 1, 0, 0, 0, hr);
            b.pr("braid#", "head", 5.6f, 7, 1, 2.8f, 22, 2.8f, 0, -11, 1, hr).sw(0.1f, 0.12f, 0, 2);
            b.pr("braidB#", "braid#", 0, -22, 1, 2.4f, 9, 2.4f, 0, -4.5f, 0, hd);
            b.pr("eyeW#", "head", 2.4f, 5.6f, -4.3f, 3.2f, 3.6f, 0.5f, 0, 0, 0, WHT);
            b.pr("pupil#", "head", 2.4f, 5.4f, -4.6f, 1.6f, 1.8f, 0.4f, 0, 0, 0, BLK);
            b.p("jaw", "head", 0, 2, -4.3f, 4.6f, 2, 0.5f, 0, 0, 0, 0x5a1a20);
            b.p("teeth", "head", 0, 3, -4.55f, 4.2f, 0.8f, 0.3f, 0, 0, 0, WHT);
            b.pr("arm#", "body", 4.6f, 7, 0, 2, 9, 2, 0, -4, 0, sk);
            b.pr("hand#", "arm#", 0, -9, 0, 2.6f, 2.4f, 2.6f, 0, -0.6f, 0, sk);
        });
    }
    private Canon5() {}
}
