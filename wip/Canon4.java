package com.poppy.mod.creature;

import com.poppy.mod.creature.Canon.B;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Canon rebuilds, part 3: rigs matched to official renders (toy-style Smiling Critters, CatNap, Boxy Boo, Prototype spider, Yarnaby, Bron, Doey, PJ, Bunzo...). */
final class Canon4 {
    static final int BLK = 0x0b0b0e, WHT = 0xf4f1ea;
    static final Map<String, Consumer<CreatureDef>> MAP = new HashMap<>();
    static float PI = (float) Math.PI;

    static B plush(CreatureDef d, float px) { return new B(d, d.h * 16f / px); }

    /** Vertical spring of thin rings hanging from `parent` pivot; core limb part carries the rotation. */
    static void spring(B b, String name, String parent, float px, float py, float pz, float len, int c1, int c2, float walk) {
        b.pr(name, parent, px, py, pz, 1.2f, len, 1.2f, 0, -len / 2, 0, c2, walk);
        int n = (int) (len / 1.6f);
        for (int i = 0; i < n; i++) b.pr(name + "r" + i + "#", name, 0, -0.8f - i * 1.6f, 0, 3.2f, 1f, 3.2f, 0, 0, 0, i % 2 == 0 ? c1 : c2);
    }

    /** Toy-style Smiling Critter: huge round head, giant black open mouth, shiny eyes, stubby plush body. */
    /** Ear pair as a broad face-on slab; rest tilt splays it outward (rz = outward tilt, radians). */
    static void ear(B b, String name, float x, float y, float w, float h, float d, float cx, float cy, int col, int inner, float rz) {
        for (int s = 0; s < 2; s++) {
            float sg = s == 0 ? -1 : 1; String S = s == 0 ? "L" : "R";
            b.p(name + S, "head", sg * x, y, 0, w, h, d, sg * cx, cy, 0, col);
            b.d.parts.get(b.d.parts.size() - 1).rz = sg * rz * (cy < 0 ? 1 : -1);
            if (inner != 0) b.p(name + "In" + S, name + S, 0, 0, 0, w * 0.64f, h * 0.7f, d + 0.35f, sg * cx, cy, 0, inner);
        }
    }

    static B toy(CreatureDef d, int fur, int belly, int claw, int pend, int inner) {
        B b = plush(d, 32);
        b.pr("leg#", null, 3.4f, 7, 0, 4.6f, 7, 5, 0, -3.5f, 0, fur, 0.8f);
        b.pr("foot#", "leg#", 0, -7, -1.4f, 5.6f, 2.4f, 8, 0, 1.2f, 0, claw);
        b.p("body", null, 0, 7, 0, 12, 10, 9, 0, 5, 0, fur).sw(0.03f, 0.1f, 0, 2);
        b.p("belly", "body", 0, 1, -4.6f, 8, 8, 0.6f, 0, 4, 0, belly);
        b.p("zip", "body", 0, 0, -5f, 0.8f, 10, 0.4f, 0, 5, 0, BLK);
        b.p("head", "body", 0, 10, -0.5f, 19, 15, 14, 0, 7.5f, 0, fur).sw(0.05f, 0.1f, 1.2f, 2);
        b.p("rim", "head", 0, 5.9f, -7.1f, 16.4f, 10.6f, 0.5f, 0, 0, 0, belly);
        b.p("jaw", "head", 0, 5.6f, -7.3f, 15, 9.4f, 0.6f, 0, 0, 0, 0x08030a);
        b.p("grille", "jaw", 0, 0, -0.4f, 5, 4, 0.3f, 0, 3, 0, 0x202028);
        b.pr("eyeB#", "head", 5.4f, 12.6f, -7f, 5.4f, 5.4f, 0.8f, 0, 0, 0, BLK);
        b.pr("glint#", "head", 4.4f, 14f, -7.5f, 1.4f, 1.4f, 0.4f, 0, 0, 0, WHT).g();
        b.pr("arm#", "body", 7.4f, 9, 0, 3.6f, 8, 4, 0, -3.4f, 0, fur);
        b.pr("hand#", "arm#", 0, -8, 0, 4.4f, 3.4f, 4.6f, 0, -1, 0, claw);
        b.p("tail", "body", 0, 2, 4.5f, 2.4f, 2.4f, 5, 0, 0, 2.5f, fur).sw(0.2f, 0.12f, 0, 1);
        b.p("decalFace", "head", 0, 7.5f, -7.65f, 19, 15, 0.05f, 0, 0, 0, WHT); d.decal("toy");
        b.p("decalBody", "body", 0, 5, -5.3f, 12, 10, 0.05f, 0, 0, 0, WHT); d.decal("toybody");
        b.pendant(12, 10, pend);
        return b;
    }

    static final Map<String, String> NM = Map.of("bubba_bubbaphant", "nightmare_bubba", "craftycorn", "nightmare_craftycorn", "kickin_chicken", "nightmare_kickin", "pickypiggy", "nightmare_pickypiggy",
        "hoppy_hopscotch", "nightmare_hoppy", "dogday", "nightmare_dogday", "bobby_bearhug", "nightmare_bobby");

    static void toySpecies(String id, int fur, int belly, int claw, int pend, Consumer<B> extra) {
        MAP.put(id, d -> { B b = toy(d, fur, belly, claw, pend, belly); extra.accept(b); });
        MAP.put(NM.get(id), d -> {
            B b = toy(d, fur, belly, claw, pend, belly); extra.accept(b);
            b.pr("brow#", "head", 5.4f, 14, -7.2f, 6.4f, 1.8f, 0.8f, 0, 0, 0, BLK);
            for (int i = 0; i < 7; i++) { b.p("nfU" + i, "head", -4.8f + i * 1.6f, 9.4f, -7.7f, 1, 3, 0.5f, 0, -1.5f, 0, WHT); b.p("nfD" + i, "head", -4.8f + i * 1.6f, 1.8f, -7.7f, 1, 2.4f, 0.5f, 0, 1.2f, 0, WHT); }
            for (int i = 0; i < 4; i++) b.p("nsp" + i, "body", 0, 9 - i * 2.4f, 4.8f, 1.6f, 2.2f, 1.8f, 0, 0, 1, 0x1a1016);
            for (var q : d.parts) if (q.name.equals("decalFace")) q.decal = "toy_nm";
            b.p("decalEyes", "head", 0, 7.5f, -7.72f, 19, 15, 0.05f, 0, 0, 0, WHT); d.decal("toy_nm_eyes"); d.glow();
            b.p("decalGrime", "head", 0, 7.5f, -7.78f, 19, 15, 0.05f, 0, 0, 0, WHT); d.decal("grime_toy");
            b.p("decalGrimeBody", "body", 0, 5, -5.36f, 12, 10, 0.05f, 0, 0, 0, WHT); d.decal("grime_toybody");
            for (var q : d.parts) {
                if (q.name.startsWith("glint")) { q.glow = true; continue; }
                int c = q.color; if (q.decal != null || c == WHT || c == BLK || c == 0x08030a || q.name.startsWith("nf") || q.name.startsWith("nsp")) continue;
                q.color = Canon2.tint(c, 0.72f);
            }
            for (var q : d.parts) if (q.name.startsWith("glint")) q.color = 0xff2020;
        });
    }

    static void bron(CreatureDef d) {
        int or = 0xee6a3a, pale = 0xf4b858, teal = 0x4ab8a8, dots = 0x3ac0a8;
        B b = plush(d, 34);
        b.pr("leg#", null, 6, 6, -7, 5.4f, 6, 5.4f, 0, -3, 0, or, 0.7f);
        b.pr("legB#", null, 6, 6, 8, 5.4f, 6, 5.4f, 0, -3, 0, or, 0.7f);
        for (int i = 0; i < 3; i++) { b.pr("toe" + i + "#", "leg#", -1.6f + i * 1.6f, -6, -3, 1.6f, 1.6f, 2, 0, 0, 0, teal); b.pr("toeB" + i + "#", "legB#", -1.6f + i * 1.6f, -6, -3, 1.6f, 1.6f, 2, 0, 0, 0, teal); }
        b.p("body", null, 0, 6, 0, 17, 11, 26, 0, 5.5f, 0, or).sw(0.02f, 0.08f, 0, 2);
        b.p("belly", "body", 0, 0, 0, 13, 3, 22, 0, 1, 0, pale);
        for (int i = 0; i < 8; i++) b.p("dot" + i, "body", ((i * 7) % 5 - 2) * 2.6f, 11.2f, -10 + i * 3, 2.6f, 0.4f, 2.6f, 0, 0, 0, dots);
        b.p("neck", "body", 0, 9, -11, 6.4f, 14, 6.4f, 0, 7, 0, or).sw(0.04f, 0.08f, 0, 0);
        b.p("neck2", "neck", 0, 13, -1.4f, 5.6f, 12, 5.6f, 0, 6, 0, or);
        b.p("neckF", "neck", 0, 0, -3.3f, 4, 12, 0.6f, 0, 6, 0, pale);
        b.p("collar", "neck", 0, 8, 0, 7.4f, 1.6f, 7.4f, 0, 0, 0, 0x3a2a22);
        b.p("head", "neck2", 0, 12, -1, 10, 9, 11, 0, 4, -2, or).sw(0.04f, 0.1f, 0, 2);
        b.p("snout", "head", 0, 1.4f, -7.4f, 7, 4.6f, 3.6f, 0, 0, 0, or);
        b.p("snoutP", "head", 0, 0.4f, -7.4f, 6, 1.6f, 3.4f, 0, 0, 0, pale);
        b.pr("eyeW#", "head", 3.6f, 5, -7.4f, 3, 2.4f, 0.5f, 0, 0, 0, 0xf4ecd0);
        b.pr("eye#", "head", 3.6f, 4.6f, -7.7f, 1.6f, 1.6f, 0.4f, 0, 0, 0, BLK);
        b.pr("lid#", "head", 3.6f, 6.4f, -7.7f, 3.4f, 1.4f, 0.5f, 0, 0, 0, 0xb83a1a);
        b.pr("brow#", "head", 3.6f, 8, -7.4f, 3.6f, 0.6f, 0.4f, 0, 0, 0, 0x3a1a10);
        b.p("jaw", "head", 0, 0.4f, -7.4f, 5, 0.8f, 3, 0, 0, 0, 0x40080c);
        b.p("decalFace", "head", 0, 4, -7.95f, 10, 9, 0.05f, 0, 0, 0, WHT); d.decal("bron");
        b.p("tail", "body", 0, 6, 13, 6, 4, 18, 0, 0, 9, or).sw(0.12f, 0.08f, 0, 1);
        b.p("tailTip", "tail", 0, 0, 18, 3, 2.4f, 10, 0, 0, 5, or);
        b.pr("arm#", "body", 8, 8, -9, 2, 2, 2, 0, 0, 0, or);
    }

    static void boxy(CreatureDef d, boolean nm) {
        float f = nm ? 0.75f : 1f; int rd = Canon2.tint(0xd02828, f), bx = Canon2.tint(0x3a8ae0, f), ye = Canon2.tint(0xf2c82a, f), sp = Canon2.tint(0x1a0a0a, f), dk = Canon2.tint(0x7a1010, f);
        B b = plush(d, 44);
        spring(b, "leg#", null, 4, 14, 0, 14, rd, sp, 0.7f);
        b.pr("foot#", "leg#", 0, -14, -2, 7, 2.4f, 10, 0, 1.2f, 0, rd);
        for (int i = 0; i < 3; i++) b.pr("fclaw" + i + "#", "foot#", -2 + i * 2, 1, -5.6f, 1, 1.2f, 2.4f, 0, 0, 0, 0xf4e4b0);
        b.p("body", null, 0, 14, 0, 14, 14, 14, 0, 7, 0, bx).sw(0.03f, 0.1f, 0, 2);
        b.p("star", "body", 0, 7, -7.2f, 7, 7, 0.6f, 0, 0, 0, ye); b.p("starV", "body", 0, 7, -7.4f, 2.6f, 10, 0.5f, 0, 0, 0, ye); b.p("starH", "body", 0, 7, -7.4f, 10, 2.6f, 0.5f, 0, 0, 0, ye);
        b.p("topFlap", "body", 0, 14, 0, 14.4f, 1.6f, 14.4f, 0, 0.5f, 0, ye);
        for (int i = 0; i < 4; i++) b.p("spike" + i, "body", i % 2 == 0 ? -7.2f : 7.2f, 2 + (i / 2) * 7, -2, 1, 1, 1, 0, 0, 0, 0xc8c8d0);
        b.p("crank", "body", -7, 11, 3, 3, 0.8f, 0.8f, -1.5f, 0, 0, 0xb04020).sw(0.4f, 0.25f, 0, 0);
        b.p("head", "body", 0, 15, 0, 13, 14, 12, 0, 7, 0, rd).sw(0.05f, 0.12f, 0, 2);
        b.p("jaw", "head", 0, 7, -6.1f, 12f, 10.5f, 0.8f, 0, -3.4f, 0, nm ? 0x14020a : 0x2a0a10);
        b.p("tongue", "jaw", 0, -6, -0.3f, 6, 1.4f, 0.5f, 0, 0, 0, 0xe0506a);
        b.p("decalFace", "head", 0, 7, -6.45f, 13, 14, 0.05f, 0, 0, 0, WHT); d.decal("boxy");
        if (nm) { b.p("decalGrime", "head", 0, 7, -6.5f, 13, 14, 0.05f, 0, 0, 0, WHT); d.decal("grime_boxy"); }
        for (int i = 0; i < 6; i++) { b.p("tU" + i, "head", -4.4f + i * 1.76f, 6.8f, -6.4f, 1.2f, 3.2f, 0.5f, 0, -1.6f, 0, 0xf4e4b0); b.p("tD" + i, "jaw", -4.2f + i * 1.7f, -5.4f, -0.3f, 1.2f, 3f, 0.5f, 0, 1.5f, 0, 0xf4e4b0); }
        b.pr("eye#", "head", 3.4f, 11, -6.2f, 2.4f, 2.4f, 0.5f, 0, 0, 0, nm ? 0xff2020 : 0xfff0a0).g();
        b.p("armL", "body", -8, 11, 0, 1.2f, 13, 1.2f, 0, -6.5f, 0, sp);
        b.p("armR", "body", 8, 11, 0, 1.2f, 13, 1.2f, 0, -6.5f, 0, sp);
        for (int i = 0; i < 8; i++) { b.p("cL" + i, "armL", 0, -0.8f - i * 1.6f, 0, 3f, 1f, 3f, 0, 0, 0, i % 2 == 0 ? rd : sp); b.p("cR" + i, "armR", 0, -0.8f - i * 1.6f, 0, 3f, 1f, 3f, 0, 0, 0, i % 2 == 0 ? rd : sp); }
        b.pr("hand#", "arm#", 0, -13, 0, 6, 5, 5, 0, -2, 0, rd);
        for (int i = 0; i < 3; i++) b.pr("claw" + i + "#", "hand#", -2 + i * 2, -5, -1, 1, 3, 1, 0, -1.5f, 0, 0xf4e4b0);
    }

    static void catnap(CreatureDef d, boolean nm) {
        float f = nm ? 0.8f : 1f; int pu = Canon2.tint(0x7a3aa8, f), dk = Canon2.tint(0x4a2070, f), lt = Canon2.tint(0xb888d8, f);
        B b = plush(d, 60);
        b.pr("leg#", null, 4, 26, 0, 3, 26, 3, 0, -13, 0, pu, 0.8f);
        b.pr("paw#", "leg#", 0, -26, -1.6f, 4.4f, 2, 6.4f, 0, 1, 0, dk);
        b.p("body", null, 0, 26, 0, 9, 14, 6, 0, 7, 0, pu).sw(0.04f, 0.1f, 0, 0);
        b.p("ribs", "body", 0, 3, -3.1f, 6, 7, 0.4f, 0, 0, 0, dk);
        b.p("head", "body", 0, 14, -1, 14, 11, 10, 0, 5.5f, 0, pu).sw(0.06f, 0.1f, 1, 2);
        b.pr("ear#", "head", 4.8f, 11, 0, 4, 9, 2, 0, 4.5f, 0, pu);
        b.pr("earIn#", "ear#", 0, 0, -1.1f, 2.2f, 6.4f, 0.4f, 0, 3.4f, 0, dk);
        b.pr("eyeW#", "head", 3.6f, 6.8f, -5.2f, 4.4f, 4.4f, 0.5f, 0, 0, 0, WHT);
        b.pr("eye#", "head", 3.6f, 6.8f, -5.5f, 2, 2.6f, 0.4f, 0, 0, 0, nm ? 0xff2020 : BLK).g();
        b.p("muzzle", "head", 0, 2.6f, -5.2f, 7, 4, 0.8f, 0, 0, 0, lt);
        b.p("nose", "head", 0, 5, -5.6f, 2, 1.4f, 0.6f, 0, 0, 0, 0xd06a90);
        b.p("jaw", "head", 0, 2.4f, -5.3f, 9, 2.4f, 0.6f, 0, -1, 0, 0x0a0208);
        b.p("grin", "head", 0, 3.6f, -5.35f, 11, 0.6f, 0.4f, 0, 0, 0, BLK);
        b.pr("arm#", "body", 6, 12, 0, 2.6f, 28, 2.6f, 0, -14, 0, pu);
        b.pr("hand#", "arm#", 0, -28, 0, 3.6f, 3, 3.6f, 0, -1, 0, dk);
        for (int i = 0; i < 4; i++) b.pr("c" + i + "#", "hand#", -1.5f + i, -3, -0.4f, 0.6f, 2.6f, 0.6f, 0, -1.3f, 0, BLK);
        b.p("tail", "body", 0, 2, 3, 1.6f, 14, 1.6f, 0, 6, 3, pu).sw(0.15f, 0.1f, 0, 1);
        b.p("tailC", "tail", 0, 14, 0, 6, 1.6f, 1.6f, 3, 0, 3, pu);
        b.p("collar", "body", 0, 13.5f, -0.3f, 7, 1.2f, 6.6f, 0, 0, 0, BLK);
        b.p("zipM", "body", 0, 0, -3.2f, 0.7f, 14, 0.3f, 0, 7, 0, BLK);
    }

    static void yarnaby(CreatureDef d) {
        int am = 0xe8a848, tan = 0xd8b078, cl = 0xf4ecd8;
        int[] yarn = {0x3a8a4a, 0x2a6a3a, 0xb83a2a, 0xd8602a, 0x7a3a9a, 0x4a2a7a};
        B b = plush(d, 28);
        b.pr("leg#", null, 5, 7, -7, 4, 7, 4, 0, -3.5f, 0, am, 0.7f);
        b.pr("legB#", null, 5, 7, 8, 4, 7, 4, 0, -3.5f, 0, am, 0.7f);
        b.pr("paw#", "leg#", 0, -7, -1, 5, 2, 7, 0, 1, 0, tan); b.pr("pawB#", "legB#", 0, -7, -1, 5, 2, 7, 0, 1, 0, tan);
        for (int i = 0; i < 3; i++) { b.pr("cl" + i + "#", "paw#", -1.6f + i * 1.6f, 0, -3.6f, 1, 1, 2.4f, 0, 0, 0, cl); }
        b.p("body", null, 0, 7, 0, 14, 11, 22, 0, 5.5f, 0, am).sw(0.02f, 0.1f, 0, 2);
        for (int z = 0; z < 6; z++) for (int x = -1; x <= 1; x++) b.p("yb" + z + x, "body", x * 4.6f, 11 + (x == 0 ? 2 : 0), -8 + z * 3.6f, 5.4f, 5 + ((z + x) & 1) * 2, 4.4f, 0, 2, 0, yarn[(z + x + 3) % 6]);
        for (int i = 0; i < 6; i++) b.pr("side" + i + "#", "body", 7.2f, 5, -8 + i * 3.6f, 3, 6, 4, 0, 0, 0, yarn[i % 6]);
        b.p("head", "body", 0, 9, -11, 11, 11, 10, 0, 4, -2, am).sw(0.05f, 0.1f, 0, 2);
        b.p("seam", "head", 0, 0, -7.1f, 0.6f, 9, 0.4f, 0, 4.5f, 0, BLK);
        b.pr("eyeB#", "head", 3.2f, 6.4f, -7f, 4.2f, 4.2f, 0.8f, 0, 0, 0, BLK);
        b.pr("glint#", "head", 2.6f, 7.4f, -7.5f, 1.2f, 1.2f, 0.4f, 0, 0, 0, WHT).g();
        b.p("jaw", "head", 0, 2, -7.1f, 7.6f, 2, 0.6f, 0, 0, 0, 0x2a0a10);
        b.p("decalFace", "head", 0, 4, -7.55f, 11, 11, 0.05f, 0, 0, 0, WHT); d.decal("yarnaby");
        b.p("tooth0", "head", -2.4f, 3, -7.4f, 1, 2, 0.4f, 0, -1, 0, WHT); b.p("tooth1", "head", 0.6f, 3, -7.4f, 1.2f, 2.6f, 0.4f, 0, -1.3f, 0, WHT); b.p("tooth2", "head", 2.8f, 3, -7.4f, 1, 1.8f, 0.4f, 0, -0.9f, 0, WHT);
        for (int i = 0; i < 10; i++) { double a = i * Math.PI / 5; b.p("mane" + i, "head", (float) Math.cos(a) * 7f, 4 + (float) Math.sin(a) * 6.6f, 3, 4.6f, 4.6f, 5, 0, 0, 0, yarn[(i * 5) % 6]).sw(0.05f, 0.2f, i, 2); }
        b.pr("arm#", "body", 8, 9, -8, 2, 2, 2, 0, 0, 0, am);
    }

    static void doey(CreatureDef d) {
        int lb = 0x6ad0e8, or = 0xf08a30, ye = 0xf2d23a, rd = 0xd02828, hat = 0x2a4ac0, bl = 0x2a9ad8, mg = 0xe05aa0;
        B b = plush(d, 44);
        b.pr("leg#", null, 5, 6, 0, 6, 6, 7, 0, -3, 0, rd, 0.7f);
        b.p("body", null, 0, 6, 0, 18, 19, 13, 0, 9.5f, 0, rd).sw(0.03f, 0.1f, 0, 2);
        b.p("mid", "body", 0, 7, 0, 18.4f, 12, 13.4f, 0, 6, 0, lb);
        b.p("blotchY", "body", -4, 9, -6.8f, 7, 6, 0.4f, 0, 0, 0, ye); b.p("blotchO", "body", 4, 12, -6.8f, 6, 5, 0.4f, 0, 0, 0, or);
        b.p("blotchM", "body", 3, 5, -6.8f, 5, 4, 0.4f, 0, 0, 0, mg); b.p("blotchB", "body", -5, 14, -6.8f, 4, 4, 0.4f, 0, 0, 0, bl);
        b.p("neck", "body", 0, 19, 0, 6, 8, 6, 0, 3, 0, lb);
        b.p("head", "neck", 0, 8, 0, 13, 13, 11, 0, 6.5f, 0, lb).sw(0.05f, 0.1f, 0, 2);
        b.p("mouth", "head", 0, 1.4f, -5.6f, 9.6f, 6.4f, 0.6f, 0, 0, 0, 0x061018);
        b.p("decalFace", "head", 0, 6.5f, -5.95f, 13, 13, 0.05f, 0, 0, 0, WHT); d.decal("doey");
        b.p("jaw", "head", 0, 3.6f, -5.7f, 8, 3.4f, 0.5f, 0, 0, 0, 0x061018);
        b.pr("eye#", "head", 3.2f, 10, -5.6f, 2, 2.4f, 0.5f, 0, 0, 0, 0x061018);
        b.p("bowlerBrim", "head", 0, 13, 0, 8, 0.8f, 8, 0, 0, 0, hat); b.p("bowler", "head", 0, 13.2f, 0, 5, 3, 5, 0, 1.5f, 0, hat);
        b.p("armL", "body", -10, 16, 0, 4.4f, 14, 4.4f, 0, -6, 0, or).sw(0.05f, 0.1f, 0, 2);
        b.p("forearmL", "armL", 0, -13, 0, 6.2f, 8, 6.2f, 0, -3, 0, ye);
        b.p("armR", "body", 10, 16, 0, 4.4f, 14, 4.4f, 0, -6, 0, ye).sw(0.05f, 0.1f, PI, 2);
        b.p("forearmR", "armR", 0, -13, 0, 6.2f, 8, 6.2f, 0, -3, 0, lb);
        for (int i = 0; i < 4; i++) { b.p("fingL" + i, "forearmL", -2.4f + i * 1.6f, -7, 0, 1.4f, 3.4f, 1.6f, 0, -1.7f, 0, ye); b.p("fingR" + i, "forearmR", -2.4f + i * 1.6f, -7, 0, 1.4f, 3.4f, 1.6f, 0, -1.7f, 0, lb); }
    }

    static void pj(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int[] sg = {Canon2.tint(0x8a5ad0, f), Canon2.tint(0x58b8e8, f), Canon2.tint(0xe86ab8, f), Canon2.tint(0xa8a0f0, f)};
        B b = plush(d, 30);
        int N = 7;
        for (int i = 0; i < N; i++) {
            String s = "seg" + i, par = i == 0 ? null : "seg" + (i - 1);
            float sz = 12.4f - i * 0.5f;
            b.p(s, par, 0, i == 0 ? 7 : 0, i == 0 ? 0 : 7.4f, sz, sz * 0.9f, 8, 0, 5, 0, sg[i % 4]).sw(0.07f, 0.14f, i * 0.8f, 1);
            b.p("puffA" + i, s, 0, 9.4f, 0, sz * 0.8f, 2.6f, 6.6f, 0, 0, 0, sg[(i + 1) % 4]);
            b.pr("puffS" + i + "#", s, sz / 2 + 0.6f, 5, 0, 2.2f, sz * 0.5f, 6, 0, 0, 0, sg[(i + 2) % 4]);
            b.pr("limb" + i + "#", s, sz / 2 - 1, 1.6f, 0, 1.6f, 6, 1.6f, 0, -3, 0, sg[(i + 1) % 4]);
            b.pr("shoe" + i + "#", "limb" + i + "#", 0, -6, -1, 1.8f, 1.4f, 4.4f, 0, 0, -0.7f, 0x8a3ad0);
        }
        b.p("body", "seg0", 0, 0, 0, 1, 1, 1, 0, 0, 0, sg[0]);
        b.p("head", "seg0", 0, 0, -9, 14, 13, 12, 0, 7, 0, sg[2]).sw(0.06f, 0.1f, 0, 2);
        b.p("muzzle", "head", 0, 3, -6.6f, 8, 5.4f, 2.6f, 0, 0, 0, 0xf4a0c8);
        b.p("nose", "head", 0, 6.4f, -8, 3.4f, 2.4f, 1, 0, 0, 0, 0x6a58c0);
        b.pr("cheek#", "head", 5.6f, 3, -4, 2.4f, 4, 4, 0, 0, 0, 0xd8409a);
        b.pr("eyeB#", "head", 3.8f, 9.6f, -6f, 4.4f, 4.6f, 0.8f, 0, 0, 0, BLK);
        b.pr("glint#", "head", 3.2f, 10.8f, -6.5f, 1.4f, 1.4f, 0.4f, 0, 0, 0, nm ? 0xff2020 : WHT).g();
        b.pr("tear#", "head", 3.8f, 6.4f, -6.2f, 0.9f, 1.8f, 0.3f, 0, 0, 0, 0x58b8e8);
        b.pr("ear#", "head", 7.2f, 12, 0, 4.4f, 8, 4, 0, -2, 0, sg[3]).sw(0.1f, 0.2f, 0, 2);
        b.p("hairTop", "head", 0, 13, -1, 8, 3, 6, 0, 1, 0, sg[1]);
        b.p("jaw", "head", 0, 2, -6.6f, 7, 1.6f, 3, 0, 0, 0, 0x2a0a10);
        b.p("tongue", "jaw", 1.4f, -1, -2.6f, 3, 0.8f, 3.6f, 0, 0, 0, 0xf06a90);
        b.p("decalFace", "head", 0, 7, -6.55f, 14, 13, 0.05f, 0, 0, 0, WHT); d.decal("pj");
        if (nm) { b.p("decalGrime", "head", 0, 7, -6.6f, 14, 13, 0.05f, 0, 0, 0, WHT); d.decal("grime_pj"); }
        for (int i = 0; i < 5; i++) b.p("tooth" + i, "head", -3 + i * 1.5f, 2.2f, -8.2f, 1, nm ? 2.8f : 1.6f, 0.5f, 0, 0, 0, WHT);
    }

    static void bunzo(CreatureDef d, boolean nm) {
        float f = nm ? 0.78f : 1f; int ye = Canon2.tint(0xf0b838, f), or = Canon2.tint(0xe86a28, f), gr = Canon2.tint(0x4aa840, f), gold = Canon2.tint(0xb08a28, f);
        B b = plush(d, 40);
        b.pr("leg#", null, 3.4f, 15, 0, 3, 15, 3, 0, -7.5f, 0, ye, 0.8f);
        b.pr("foot#", "leg#", 0, -15, -2, 5.4f, 2.4f, 8.4f, 0, 1.2f, 0, ye);
        b.p("body", null, 0, 15, 0, 10, 11, 7, 0, 5.5f, 0, ye).sw(0.03f, 0.1f, 0, 2);
        b.p("over", "body", 0, 0, -0.2f, 10.6f, 6, 7.4f, 0, 3, 0, gr);
        b.pr("strap#", "body", 3, 11, -3.7f, 1.6f, 5, 0.5f, 0, -2.5f, 0, gr);
        b.pr("btn#", "body", 3, 6.4f, -4, 1.4f, 1.4f, 0.4f, 0, 0, 0, gold);
        b.p("head", "body", 0, 11, 0, 13, 11, 10, 0, 5.5f, 0, ye).sw(0.05f, 0.1f, 0, 2);
        b.pr("ear#", "head", 4, 11, 0, 3.4f, 12, 1.6f, 0, 6, 0, ye).sw(0.08f, 0.2f, 0, 2);
        b.pr("earIn#", "ear#", 0, 0, -0.9f, 2, 10, 0.4f, 0, 5, 0, or);
        b.pr("cheek#", "head", 5.6f, 2.4f, -4.6f, 3.4f, 3.4f, 1, 0, 0, 0, or);
        b.pr("eyeB#", "head", 3.4f, 7, -5.2f, 3.2f, 3.4f, 0.6f, 0, 0, 0, BLK); 
        b.pr("glint#", "head", 3, 8, -5.6f, 1, 1, 0.3f, 0, 0, 0, nm ? 0xff2020 : WHT).g();
        b.p("jaw", "head", 0, 1.6f, -5.2f, 7, 3.4f, 0.6f, 0, 0, 0, 0x2a0a10);
        b.pr("buck#", "head", 0.9f, 3.2f, -5.5f, 1.6f, 3, 0.5f, 0, 0, 0, WHT);
        b.p("decalFace", "head", 0, 5.5f, -5.6f, 13, 11, 0.05f, 0, 0, 0, WHT); d.decal(nm ? "bunzo_nm" : "bunzo");
        if (nm) { b.p("decalGrime", "head", 0, 5.5f, -5.66f, 13, 11, 0.05f, 0, 0, 0, WHT); d.decal("grime_bunzo"); }
        b.p("hat", "head", 0, 11, 0, 6.6f, 4, 6.6f, 0, 2, 0, gr); b.p("hatTop", "hat", 0, 4, 0, 3.4f, 4, 3.4f, 0, 2, 0, gr); b.p("pom", "hatTop", 0, 4, 0, 1.8f, 1.8f, 1.8f, 0, 0.6f, 0, gold);
        b.pr("arm#", "body", 6, 9, 0, 2.2f, 12, 2.2f, 0, -5.5f, 0, ye);
        b.pr("hand#", "arm#", 0, -12, 0, 3.4f, 3.4f, 3.4f, 0, -1, 0, ye);
        b.pr("cymbal#", "hand#", 0, -1, -4, 1, 19, 19, 0, 0, 0, gold);
        b.pr("cymbalC#", "hand#", 0, -1, -4.6f, 1, 5, 5, 0, 0, 0, 0x6a5018);
        if (nm) Canon2.nm(b, 13, 11, 10);
    }

    static void prototype(CreatureDef d, boolean nm) {
        float f = nm ? 0.8f : 1f; int rs = Canon2.tint(0x6a3a2a, f), dk = Canon2.tint(0x2a2a30, f), mt = Canon2.tint(0x8a6a50, f), por = Canon2.tint(0xf0ece0, f), rd = Canon2.tint(0xc82020, f), gold = Canon2.tint(0xd8b040, f), co = Canon2.tint(0x3a5aa0, f);
        B b = plush(d, 60);
        float[] zs = {-12, -4, 4, 12};
        for (int i = 0; i < 4; i++) for (int sd = 0; sd < 2; sd++) {
            float sg = sd == 0 ? -1 : 1; String S = sd == 0 ? "L" : "R";
            String sx = (i == 0 ? "leg" : "leg" + (char) ('B' + i - 1)) + S, lo = (i == 0 ? "shin" : "shin" + i) + S;
            b.p(sx, null, sg * 6, 16, zs[i] * 1.25f, 22, 3.2f, 3.2f, sg * 11, 0, 0, rs); d.walk(0.6f, (i + sd) % 2 == 0 ? 0 : PI);
            b.p(lo, sx, sg * 22, 0, 0, 3f, 16, 3f, 0, -8, 0, i % 2 == 0 ? mt : rs);
            b.p("knee" + i + S, sx, sg * 22, 0, 0, 4.4f, 4.4f, 4.4f, 0, 0, 0, dk);
            b.p("tip" + i + S, lo, 0, -16, 0, 1.4f, 3, 1.4f, 0, -1.5f, 0, 0xc8c8d0);
        }
        b.p("body", null, 0, 16, 0, 16, 10, 34, 0, 5, 0, dk).sw(0.02f, 0.1f, 0, 2);
        b.p("ribs", "body", 0, 10, -2, 10, 1, 14, 0, 0, 0, 0xe8e0c8);
        for (int i = 0; i < 5; i++) b.p("cable" + i, "body", -6 + i * 3, 10.4f, 8 + i * 2, 1.4f, 1, 10, 0, 0, 0, i % 2 == 0 ? 0xe05a8a : rd);
        b.p("core", "body", 0, 6, -17.2f, 4, 4, 0.6f, 0, 0, 0, 0xffd820).g();
        b.p("torso", "body", 0, 10, -10, 8, 22, 6, 0, 11, 0, co);
        b.p("head", "torso", 0, 22, 0, 10, 12, 9, 0, 6, 0, por).sw(0.07f, 0.1f, 0, 2);
        b.p("eyeHollow", "head", -2.4f, 8, -4.6f, 3, 3, 0.5f, 0, 0, 0, BLK);
        b.p("eyeY", "head", 2.4f, 8, -4.7f, 2, 2, 0.5f, 0, 0, 0, nm ? 0xff2020 : 0xffd820).g();
        b.p("jaw", "head", 0, 2.4f, -4.6f, 7.6f, 3, 0.6f, 0, 0, 0, 0x14080a);
        b.p("decalFace", "head", 0, 6, -4.95f, 10, 12, 0.05f, 0, 0, 0, WHT); d.decal("proto");
        if (nm) { b.p("decalGrime", "head", 0, 6, -5.0f, 10, 12, 0.05f, 0, 0, 0, WHT); d.decal("grime_proto"); b.p("decalEyes", "head", 0, 6, -5.06f, 10, 12, 0.05f, 0, 0, 0, WHT); d.decal("proto_nm_eyes"); d.glow(); }
        for (int i = 0; i < 6; i++) b.p("sqt" + i, "head", -3.2f + i * 1.3f, 3.6f, -5f, 1.1f, 1.8f, 0.4f, 0, 0, 0, WHT);
        b.p("hatBase", "head", 0, 12, 0, 10, 2, 9, 0, 1, 0, rd);
        for (int i = -1; i <= 1; i++) { b.p("prong" + i, "hatBase", i * 3.4f, 1, 0, 2.6f, 8 - Math.abs(i), 2.6f, 0, 4, 0, i == 0 ? gold : co); b.p("bell" + i, "prong" + i, 0, 8 - Math.abs(i), 0, 2.2f, 2.2f, 2.2f, 0, 0.6f, 0, gold); }
        b.p("bow", "torso", 0, 19, -3.4f, 5, 3, 1.2f, 0, 0, 0, rd);
        b.pr("arm#", "torso", 6, 18, 0, 2, 24, 2, 0, -11, 0, mt).sw(0.07f, 0.1f, 0, 2);
        b.pr("fore#", "arm#", 0, -24, 0, 1.6f, 14, 1.6f, 0, -7, 0, rs);
        for (int i = 0; i < 4; i++) b.pr("claw" + i + "#", "fore#", -1.4f + i * 0.9f, -14, 0, 0.6f, 6, 0.6f, 0, -3, 0, 0xc8c8d0);
        b.p("tail", "body", 0, 6, 17, 2, 2, 18, 0, 0, 9, 0xe05a8a).sw(0.2f, 0.1f, 0, 1);
    }

    static {
        toySpecies("bubba_bubbaphant", 0x4aa8c0, 0x6cc0d4, 0x2a7898, 0xf2d24a, b -> {
            ear(b, "ear", 8.4f, 12.5f, 10, 12, 1.6f, 4.5f, -4.5f, 0x4aa8c0, 0x2a7898, -0.3f);
            b.p("trunk", "head", 0, 5, -7.3f, 5, 6, 3, 0, 0, -1.5f, 0x4aa8c0); b.p("trunk2", "trunk", 0, -5, -2, 4, 4, 3.4f, 0, -1, 0, 0x4aa8c0);
            b.p("tailTuft", "tail", 0, 0, 5, 3, 3, 3, 0, 0, 0, 0x2a7898);
        });
        toySpecies("craftycorn", 0xf4f4f8, 0xe4eef0, 0x6ad0c8, 0xf06ab0, b -> {
            ear(b, "ear", 6.6f, 14.5f, 5, 6.4f, 2, 0, 3.2f, 0xf4f4f8, 0x6ad0c8, 0.4f);
            b.p("horn", "head", 0, 15, -1, 3, 4, 3, 0, 2, 0, 0xc8ccd8); b.p("horn2", "horn", 0, 4, 0, 2, 3, 2, 0, 1.5f, 0, 0xc8ccd8);
            b.p("mane", "head", 0, 15, 3, 12, 3, 5, 0, 0, 0, 0x6ad0c8);
        });
        toySpecies("kickin_chicken", 0xf4d63a, 0xfbe890, 0xf08a20, 0xf08a20, b -> {
            b.p("beak", "head", 0, 8.4f, -7.3f, 6, 2.6f, 2, 0, 0, 0, 0xf08a20);
            for (int i = 0; i < 3; i++) b.p("crest" + i, "head", 3 - i * 2.6f, 15, 0, 2, 4 - i * 0.6f, 2.4f, 0, 2, 0, 0xf4d63a);
        });
        toySpecies("pickypiggy", 0xf6a8c0, 0xfbd0dc, 0xd87898, 0xe02828, b -> {
            b.p("snout", "head", 0, 10.4f, -7.4f, 6, 4, 1.6f, 0, 0, 0, 0xd87898);
            ear(b, "ear", 7.2f, 14.6f, 5.6f, 5.6f, 1.8f, 1.6f, 2.8f, 0xf6a8c0, 0xd87898, 0.6f);
            b.p("curl", "tail", 0, 0, 5, 2, 2, 2, 0, 1, 1, 0xf6a8c0);
        });
        toySpecies("hoppy_hopscotch", 0x8ad84a, 0xc8f090, 0x5ab030, 0xf2c020, b -> {
            ear(b, "ear", 5, 14.8f, 4.2f, 14, 1.8f, 0, 7, 0x8ad84a, 0x5ab030, 0.22f);
            b.p("nose", "head", 0, 10.4f, -7.4f, 2.2f, 1.6f, 0.6f, 0, 0, 0, BLK);
            b.p("cotton", "tail", 0, 0, 5, 4.4f, 4.4f, 4.4f, 0, 0, 0, 0xc8f090);
        });
        toySpecies("dogday", 0xe8982a, 0xfbe2a8, 0x9a5a18, 0xffe040, b -> {
            ear(b, "ear", 9.2f, 12.6f, 5.4f, 10, 1.8f, 2.4f, -5, 0xb06a1c, 0, 0.18f);
            b.p("nose", "head", 0, 11.2f, -7.4f, 3.4f, 2.2f, 0.8f, 0, 0, 0, BLK);
        });
        toySpecies("bobby_bearhug", 0xc83a3a, 0xf0a0b8, 0xf0a0b8, 0xd01040, b -> {
            ear(b, "ear", 7, 14.6f, 5.2f, 5.2f, 2.4f, 1.4f, 2.6f, 0xc83a3a, 0xf0a0b8, 0.5f);
            b.p("nose", "head", 0, 11.2f, -7.4f, 3, 2, 0.8f, 0, 0, 0, BLK);
        });
        MAP.put("bron", Canon4::bron); MAP.put("giant_bron", Canon4::bron);
        MAP.put("boxy_boo", d -> boxy(d, false)); MAP.put("mini_boxy_boo", d -> boxy(d, false)); MAP.put("nightmare_boxy_boo", d -> boxy(d, true));
        MAP.put("catnap", d -> catnap(d, false)); MAP.put("nightmare_catnap", d -> catnap(d, true));
        MAP.put("yarnaby", Canon4::yarnaby); MAP.put("mini_yarnaby", Canon4::yarnaby);
        for (String s : new String[]{"doey_the_doughman", "mini_doey"}) MAP.put(s, Canon4::doey);
        MAP.put("mini_doughman", d -> { doey(d); for (var q : d.parts) { if (q.color == 0x6ad0e8) q.color = 0x8ae06a; else if (q.color == 0x2a4ac0) q.color = 0xc02a4a; else if (q.color == 0xf08a30) q.color = 0xb86ae0; } });
        MAP.put("pj_pug_a_pillar", d -> pj(d, false)); MAP.put("mini_pj", d -> pj(d, false)); MAP.put("nightmare_pj", d -> pj(d, true));
        MAP.put("bunzo_bunny", d -> bunzo(d, false)); MAP.put("nightmare_bunzo", d -> bunzo(d, true));
        MAP.put("the_prototype", d -> prototype(d, false)); MAP.put("mini_prototype", d -> prototype(d, false)); MAP.put("nightmare_prototype", d -> prototype(d, true));
    }
}
