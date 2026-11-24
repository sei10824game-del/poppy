package com.poppy.mod.creature;

import java.util.Map;
import java.util.function.Consumer;

/** Canon-accurate model rebuilds (appearance per Poppy Playtime wiki). Replaces a creature's part tree; attacks stay. */
final class Canon {
    static final float PI = (float) Math.PI;

    /** Scaled builder: all px values multiplied by k. '#' in a name = L/R pair (L at -x). */
    static final class B {
        final CreatureDef d; final float k;
        B(CreatureDef d, float k) { this.d = d; this.k = k; }
        B p(String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) {
            d.part(n, par, px * k, py * k, pz * k, w * k, h * k, dd * k, cx * k, cy * k, cz * k, c); return this;
        }
        B pr(String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c, float walk) {
            String pl = par == null ? null : par.replace('#', 'L'), prr = par == null ? null : par.replace('#', 'R');
            p(n.replace('#', 'L'), pl, -px, py, pz, w, h, dd, cx, cy, cz, c); if (walk != 0) d.walk(walk, 0);
            p(n.replace('#', 'R'), prr, px, py, pz, w, h, dd, cx, cy, cz, c); if (walk != 0) d.walk(walk, PI);
            return this;
        }
        B pr(String n, String par, float px, float py, float pz, float w, float h, float dd, float cx, float cy, float cz, int c) { return pr(n, par, px, py, pz, w, h, dd, cx, cy, cz, c, 0); }
        B g() {
            d.glow(); int n = d.parts.size();
            if (n > 1 && d.parts.get(n - 1).name.endsWith("R") && d.parts.get(n - 2).name.endsWith("L")) d.parts.get(n - 2).glow = true;
            return this;
        }
        B sw(float a, float s, float ph, int ax) { d.sway(a, s, ph, ax); return this; }

        /** legs + body + head + arms + jaw. Pivots: legs at y=legH, body on top, head on body, arms at shoulder. */
        B biped(float legH, float legW, float bw, float bh, float bd, float hw, float hh, float hd, float armW, float armLen, int bodyC, int headC, int limbC) {
            pr("leg#", null, legW * 0.6f, legH, 0, legW, legH, legW, 0, -legH / 2, 0, limbC, 0.8f);
            p("body", null, 0, legH, 0, bw, bh, bd, 0, bh / 2, 0, bodyC).sw(0.02f, 0.1f, 0, 2);
            p("head", "body", 0, bh, 0, hw, hh, hd, 0, hh / 2, 0, headC);
            pr("arm#", "body", bw / 2 + armW / 2, bh - 2, 0, armW, armLen, armW, 0, -armLen / 2 + 1, 0, limbC);
            return this;
        }
        /** Critter face: furrowed black eyes, wide grin with dark mouth (jaw part). */
        B face(float hw, float hh, float hd, int eyeC, int mouthC, int toothC, boolean teeth) {
            float z = -hd / 2 - 0.15f;
            pr("eye#", "head", hw * 0.23f, hh * 0.62f, z, 3.4f, 3.6f, 0.6f, 0, 0, 0, 0x0b0b0e);
            pr("pupil#", "head", hw * 0.23f, hh * 0.62f, z - 0.2f, 1.2f, 1.2f, 0.4f, 0, 0.3f, 0, eyeC).g();
            pr("brow#", "head", hw * 0.23f, hh * 0.62f + 2.4f, z, 4f, 0.9f, 0.6f, 0, 0, 0, 0x0b0b0e);
            p("jaw", "head", 0, hh * 0.18f, z, hw * 0.62f, hh * 0.2f, 0.7f, 0, 0, 0, mouthC);
            if (teeth) { pr("tooth#", "head", hw * 0.18f, hh * 0.3f, z - 0.3f, 1.6f, 1.8f, 0.4f, 0, 0, 0, toothC); }
            return this;
        }
        /** Black necklace + pendant on the chest. */
        B pendant(float bw, float bh, int pend) {
            p("necklace", "body", 0, bh - 1.5f, -0.2f, bw + 0.6f, 1.4f, 1, 0, 0, 0, 0x101014);
            p("pendant", "body", 0, bh - 6f, 0, 3.2f, 3.2f, 0.8f, 0, 0, -0.1f, pend);
            return this;
        }
        B zipper(float bh, float bd, int col) { return p("zip", "body", 0, 0, -bd / 2 - 0.1f, 0.8f, bh, 0.5f, 0, bh / 2, 0, col); }
    }

    static void apply(CreatureDef d) {
        Consumer<CreatureDef> f = Canon4.MAP.getOrDefault(d.id, MAP.getOrDefault(d.id, MAP2.getOrDefault(d.id, Canon2.MAP.get(d.id))));
        if (f == null) { Canon3.apply(d); dump(d); return; }
        d.parts.clear();
        f.accept(d);
        alive(d);
        retarget(d);
        Canon3.apply(d);
        dump(d);
        if (System.getenv("POPPY_AUDIT") != null) for (var a : d.attacks) if (a.anim() != null) for (float[] ph : new float[][]{{1, 0, 0}, {1, 1, 0}, {1, 1, 0.5f}})
            try { a.anim().apply(new com.poppy.mod.client.Pose(), null, ph[0], ph[1], ph[2], 5f); } catch (Throwable t) { }
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (var pt : d.parts) {
            if (pt.parent != null && !seen.contains(pt.parent)) throw new IllegalStateException(d.id + ": part " + pt.name + " has undefined parent " + pt.parent);
            if (!seen.add(pt.name)) throw new IllegalStateException(d.id + ": duplicate part " + pt.name);
        }
    }

    /** Wrap every attack choreography so it drives the NEW rig: missing nodes (armL2, legR_upper, wing...) fold into the nearest existing limb. */
    static void retarget(CreatureDef d) {
        java.util.Set<String> names = new java.util.HashSet<>();
        for (var pt : d.parts) names.add(pt.name);
        for (int i = 0; i < d.attacks.size(); i++) {
            AttackSpec a = d.attacks.get(i);
            if (a.anim() == null) continue;
            AnimHook h = a.anim();
            AnimHook w = (p, e, wi, st, re, age) -> {
                com.poppy.mod.client.Pose t = new com.poppy.mod.client.Pose();
                java.util.Map<String, float[]> seed = new java.util.HashMap<>();
                for (String n : names) if (p.has(n)) { float[] c = p.m.get(n).clone(); t.m.put(n, c); seed.put(n, c.clone()); }
                h.apply(t, e, wi, st, re, age);
                java.util.Set<String> touched = new java.util.HashSet<>();
                for (var en : t.m.entrySet()) if (!seed.containsKey(en.getKey()) || !java.util.Arrays.equals(seed.get(en.getKey()), en.getValue())) touched.add(en.getKey());
                for (var en : t.m.entrySet()) {
                    String n = en.getKey();
                    if (names.contains(n)) { p.m.put(n, en.getValue()); continue; }
                    String tg = target(n, names);
                    if (tg == null && touched.contains(n) && System.getenv("POPPY_AUDIT") != null) System.out.println("AUDIT " + d.id + " " + n);
                    if (tg != null && !touched.contains(tg)) {
                        float[] cur = p.g(tg), v = en.getValue();
                        if (touched.contains(n)) for (int k = 0; k < 9; k++) cur[k] = v[k];
                    }
                }
            };
            d.attacks.set(i, new AttackSpec(a.kind(), a.dmg(), a.reach(), a.minRange(), a.cd(), a.n(), a.s(), a.custom(), a.t(), w));
        }
    }

    /** Heuristic: armL2/armR_upper/handR... -> armL/armR, leg*, tail*, else none. */
    /** Idle life for every canon rig: breathing, weight shift, loose arms/ears/hair/tails/wings. Never overrides hand-set sway. */
    static void alive(CreatureDef d) {
        int i = 0;
        for (var q : d.parts) {
            i++;
            if (q.swayAmp != 0) continue;
            String n = q.name; float ph = (i * 1.7f) % 6.28f;
            boolean L = n.endsWith("L"), R = n.endsWith("R");
            if (n.equals("body")) q.swayAmp = 0.03f;
            else if (n.equals("head")) { q.swayAmp = 0.05f; q.swayAxis = 2; ph = 1.1f; }
            else if (n.startsWith("arm") && !n.contains("2")) { q.swayAmp = 0.06f; q.swayAxis = 2; ph = R ? PI : 0; }
            else if (n.startsWith("tail") || n.startsWith("ponytail") || n.startsWith("braid")) { q.swayAmp = 0.16f; q.swayAxis = 1; q.swaySpeed = 0.12f; q.swayPhase = ph; continue; }
            else if (n.startsWith("ear") || n.startsWith("wing") || n.startsWith("anten") || n.startsWith("hair") || n.startsWith("mane") || n.startsWith("crown") || n.startsWith("tongue") || n.startsWith("fringe")) { q.swayAmp = 0.1f; q.swayAxis = 2; q.swaySpeed = 0.2f; q.swayPhase = ph; continue; }
            else continue;
            q.swaySpeed = 0.1f; q.swayPhase = ph;
        }
    }

    static void dump(CreatureDef d) {
        String dir = System.getenv("POPPY_DUMP");
        if (dir == null) return;
        StringBuilder sb = new StringBuilder("[");
        for (var q : d.parts) sb.append(String.format(java.util.Locale.ROOT, "%s[\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d],", sb.length() > 1 ? "" : "", q.name, q.parent == null ? "null" : "\"" + q.parent + "\"", q.px, q.py, q.pz, q.w, q.h, q.d, q.cx, q.cy, q.cz, q.color, q.glow ? 1 : 0));
        sb.append("[]]");
        try { java.nio.file.Files.writeString(java.nio.file.Path.of(dir, d.id + ".json"), sb.toString()); } catch (Exception e) { }
    }

    static final Map<String, String> ALIAS = Map.ofEntries(
        Map.entry("fistR", "forearmR"), Map.entry("fistL", "forearmL"), Map.entry("crankArm", "crank"), Map.entry("box", "body"), Map.entry("lipU", "head"),
        Map.entry("armL", "limb0L"), Map.entry("armR", "limb0R"), Map.entry("mandibleL", "limb1L"), Map.entry("mandibleR", "limb1R"),
        Map.entry("neck", "head"), Map.entry("throat", "jaw"), Map.entry("hornWax", "horn0"), Map.entry("screen", "head"), Map.entry("coreDot", "dot"),
        Map.entry("tuskL", "snout"), Map.entry("puff", "wool"), Map.entry("lantern", "pendant"), Map.entry("smoke", "head"), Map.entry("hump", "torso"), Map.entry("core", "torso"));

    static String target(String n, java.util.Set<String> names) {
        for (String pre : new String[]{"arm", "leg", "hand", "foot"}) if (n.startsWith(pre)) {
            for (char c : n.substring(pre.length()).toCharArray()) if (c == 'L' || c == 'R') {
                String g = pre + c;
                if (names.contains(g)) return g;
                if (pre.equals("hand") || pre.equals("foot")) { g = (pre.equals("hand") ? "arm" : "leg") + c; if (names.contains(g)) return g; }
            }
        }
        if (n.startsWith("tail") && names.contains("tail")) return "tail";
        String al = ALIAS.get(n);
        if (al != null) { if (names.contains(al)) return al; if (al.contains("#")) { for (char c : new char[]{'L', 'R'}) { String g = al.replace('#', c); if (names.contains(g)) return g; } } }
        return null;
    }

    static final int BLK = 0x0b0b0e, WHT = 0xf4f1ea;

    /** Nightmare-Critter build: stocky bipedal plush with glaring eyes & grin. */
    static B critter(CreatureDef d, int fur, int belly, int mouth, int claw, int eyeC, boolean teeth) {
        float k = d.h * 16f / 36f;
        B b = new B(d, k);
        b.biped(10, 5, 12, 12, 9, 14, 12, 11, 3.6f, 11, fur, fur, fur);
        b.p("belly", "body", 0, 1.5f, -4.6f, 8, 8, 0.6f, 0, 4, 0, belly);
        b.pr("hand#", "arm#", 0, -10, 0, 4.4f, 3.4f, 4f, 0, -1, 0, claw);
        b.pr("foot#", "leg#", 0, -10, -1, 5.6f, 2.2f, 7f, 0, 1.1f, -0.8f, claw);
        b.face(14, 12, 11, eyeC, mouth, WHT, teeth);
        b.p("tail", "body", 0, 2, 4.5f, 3, 3, 8, 0, 0, 3, fur).sw(0.2f, 0.12f, 0, 1);
        b.p("zip", "body", 0, 0, -4.7f, 0.8f, 12, 0.4f, 0, 6, 0, 0x101014);
        for (int i = 0; i < 3; i++) { b.pr("finger" + i + "#", "hand#", -1.4f + i * 1.4f, -3, -1.6f, 1, 1.6f, 1, 0, -0.8f, 0, claw); b.pr("toe" + i + "#", "foot#", -1.6f + i * 1.6f, 0, -3.6f, 1.2f, 1.2f, 1.2f, 0, 0.6f, 0, claw); }
        b.pr("cheek#", "head", 6.4f, 4, -3, 1.4f, 3, 4, 0, 0, 0, belly);
        b.p("brow", "head", 0, 12.2f, -1, 7, 1.4f, 5, 0, 0, 0, fur);
        for (int i = 0; i < 4; i++) b.p("fluff" + i, "head", -4.5f + i * 3, 12, 0.5f, 2.4f, 2.6f + (i % 2), 2.4f, 0, 1, 0, fur);
        b.p("chinFluff", "head", 0, 0.4f, -3, 6, 1.6f, 3, 0, 0, 0, belly);
        b.pr("shoulderFluff#", "body", 6.6f, 11, 0, 4.6f, 2.6f, 5.6f, 0, 0, 0, fur);
        b.pr("kneeFluff#", "leg#", 0, -5, 0, 6.2f, 2, 6.2f, 0, 0, 0, fur);
        b.pr("hip#", "body", 4, 1, 0, 5.4f, 3, 8.6f, 0, 0, 0, fur);
        return b;
    }

    static final Map<String, Consumer<CreatureDef>> MAP = Map.ofEntries(
        Map.entry("maggie_mako", d -> { // shark
            int sk = 0x2fd0c4, tl = 0x16968c;
            B b = critter(d, sk, WHT, 0x2a0a10, tl, 0xffffff, true);
            b.p("snout", "head", 0, 4, -5.6f, 11, 5, 3, 0, 0, 0, sk);
            b.p("finTop", "head", 0, 12, 1, 1.4f, 6, 6, 0, 3, 0, tl).p("finTop2", "finTop", 0, 5, 1, 1.4f, 3, 3, 0, 1, 0, tl);
            b.p("tailFin", "tail", 0, 0, 7, 1.4f, 9, 4, 0, 3, 3, tl);
            b.pr("gill#", "body", 5.5f, 8, -1, 0.6f, 3, 3, 0, 0, 0, tl);
            for (int i = 0; i < 8; i++) b.p("spike" + i, "body", 0, 11 - i * 1.4f, 4.8f, 1, 1.6f, 1.6f, 0, 0, 0.8f, tl);
            b.p("lipU", "head", 0, 5, -5.7f, 10, 1, 1, 0, 0, 0, WHT);
            b.pendant(12, 12, 0xffd23a);
        }),
        Map.entry("allister_gator", d -> { // alligator
            int sk = 0xb4d62a, bl = 0xd8e68a, ag = 0x5a6b2a;
            B b = critter(d, sk, bl, 0x2a0a10, ag, 0xffffff, true);
            b.p("snout", "head", 0, 3, -8f, 10, 5, 8, 0, 0, 0, sk);
            b.p("lowJaw", "head", 0, 2, -8f, 9, 2, 7, 0, 0, 0, bl);
            b.pr("nostril#", "head", 2.6f, 8, -12.1f, 1.4f, 1.4f, 0.6f, 0, 0, 0, ag);
            b.pr("knee#", "leg#", 0, -4, -2.6f, 5.2f, 1, 0.5f, 0, 0, 0, ag);
            for (int i = 0; i < 4; i++) b.p("spine" + i, "tail", 0, 1.5f, 1 + i * 2, 1.4f, 2.4f, 1.4f, 0, 0.5f, 1, ag);
            b.p("tailLong", "tail", 0, 0, 8, 2.4f, 2.4f, 9, 0, 0, 4, sk);
            b.pendant(12, 12, 0x3a3a3a);
        }),
        Map.entry("poe", d -> { // raven
            int pl = 0x5b4a9e, bk = 0x3a2a6a;
            B b = critter(d, pl, bk, 0x120816, bk, 0xffffff, false);
            b.p("beak", "head", 0, 4.5f, -6.5f, 4, 3, 4, 0, 0, 0, bk).p("beakTip", "beak", 0, -1.4f, -3, 2.6f, 1.8f, 2, 0, 0, 0, bk);
            b.p("crest", "head", 0, 12, -1, 4, 3, 5, 0, 1, 0, pl);
            b.pr("wing#", "body", 7.4f, 11, 2.4f, 1.4f, 11, 5, 0, -5, 0, pl);
            b.pr("wingTip#", "wing#", 0, -10, 0.5f, 1.4f, 5, 4, 0, -2, 0, bk);
            b.pendant(12, 12, 0x7a6ad0);
        }),
        Map.entry("rabie_baby", d -> { // bat
            int fu = WHT, pk = 0xf08ab8, mg = 0xc8228a;
            B b = critter(d, fu, pk, 0x2a0a18, mg, 0xffffff, false);
            b.pr("fang#", "head", 2.2f, 4f, -5.9f, 1.4f, 3, 0.5f, 0, 0, 0, WHT);
            b.pr("ear#", "head", 4.6f, 12, 0, 3.4f, 9, 1.2f, 0, 4.5f, 0, fu);
            b.pr("earIn#", "ear#", 0, 0, -0.7f, 2, 6.5f, 0.4f, 0, 4, 0, pk);
            b.pr("wing#", "body", 6.8f, 11, 3, 0.8f, 12, 9, 0, -3, 4, pk);
            b.pr("wingBone#", "wing#", 0, 0, 0, 0.9f, 1, 10, 0, 0, 4.5f, mg);
            b.pr("eyeRing#", "head", 2.8f, 7.4f, -5.7f, 4.4f, 4.4f, 0.3f, 0, 0, 0, mg);
        }),
        Map.entry("simon_smoke", d -> { // dragon
            int sc = 0xe8502a, cr = 0xb01818, ye = 0xf2c230;
            B b = critter(d, sc, WHT, 0x2a0a10, cr, 0xffffff, true);
            b.pr("horn#", "head", 4, 12, -1, 1.8f, 6, 1.8f, 0, 3, 0, ye);
            b.pr("ear#", "head", 7.4f, 8, 0, 1.6f, 5, 3, 0, 2, 0, sc);
            b.p("earring", "head", 8, 6, 0, 0.8f, 2.2f, 1.2f, 0, 0, 0, ye);
            b.p("mane", "head", 0, 12, 3, 6, 4, 4, 0, 0, 0, cr);
            b.pr("wing#", "body", 6.4f, 11, 3, 1, 12, 10, 0, -2, 5, WHT);
            b.pr("wingBone#", "wing#", 0, 0, 0, 1.2f, 1.2f, 11, 0, 0, 5, cr);
            for (int i = 0; i < 3; i++) b.p("tspike" + i, "tail", 0, 2, 1 + i * 3, 1.4f, 2.6f, 1.4f, 0, 0, 0, ye);
            b.p("tailTip", "tail", 0, 0, 8, 2.4f, 2.4f, 6, 0, 0, 3, sc);
            b.pendant(12, 12, 0xff6a30);
        }),
        Map.entry("icky_licky", d -> { // poison dart frog
            int bk = 0x14141a, ye = 0xf0c020;
            B b = critter(d, bk, bk, 0x120410, ye, 0xffffff, false);
            b.p("snout", "head", 0, 4, -5.6f, 12, 5, 2, 0, 0, 0, bk);
            b.pr("eyeBulge#", "head", 4.8f, 11, -2, 4.6f, 3.6f, 4.6f, 0, 1, 0, bk);
            b.p("bandHead", "head", 0, 8.4f, 0, 14.4f, 1.4f, 11.4f, 0, 0, 0, ye);
            b.p("bandBody", "body", 0, 6, 0, 12.4f, 1.6f, 9.4f, 0, 0, 0, ye);
            b.pr("bandArm#", "arm#", 0, -6, 0, 3.8f, 1.2f, 3.8f, 0, 0, 0, ye);
            b.pr("bandLeg#", "leg#", 0, -6, 0, 5.4f, 1.2f, 5.4f, 0, 0, 0, ye);
            b.pr("toe#", "foot#", 0, 0, -3.6f, 4.8f, 1, 1.4f, 0, 0, 0, ye);
            b.p("zipBio", "body", 0, 5, -4.7f, 2.6f, 2.6f, 0.4f, 0, 0, 0, ye);
        }),
        Map.entry("baba_chops", d -> { // black-wool sheep
            int wl = 0x1c1c22, fc = 0xf4f0ea;
            B b = critter(d, wl, wl, 0x2a0a10, fc, 0xffffff, false);
            b.p("wool", "body", 0, 0, 0, 13.6f, 9, 10.6f, 0, 7, 0, wl);
            b.p("woolHead", "head", 0, 9, 0, 12, 5, 10, 0, 1.5f, 0, wl);
            b.p("face", "head", 0, 0, -5.6f, 9, 8.6f, 0.5f, 0, 5, 0, fc);
            b.pr("ear#", "head", 8, 8, 0, 5, 2.4f, 2, 0, 0, 0, fc);
            b.pr("buck#", "head", 1, 3.8f, -5.9f, 1.8f, 3, 0.5f, 0, 0, 0, WHT);
            b.pr("nostril#", "head", 1.4f, 6.4f, -5.95f, 0.8f, 1.4f, 0.3f, 0, 0, 0, BLK);
            b.pr("hoof#", "foot#", 0, 0, 0, 5.8f, 1.2f, 7.2f, 0, 0, 0, fc);
            b.p("cottonTail", "tail", 0, 0, 3, 4, 4, 4, 0, 0, 0, WHT);
        }),
        Map.entry("touille", d -> { // rat
            int gr = 0x7d7f86, sv = 0xc4c6cc, pk = 0xe69a8a;
            B b = critter(d, gr, sv, 0x2a0a10, 0x56585e, 0xffffff, false);
            b.p("snout", "head", 0, 4, -6.4f, 7, 5, 4, 0, 0, 0, sv).p("nose", "snout", 0, 1, -2.4f, 2.4f, 2.4f, 1.4f, 0, 0, 0, pk);
            b.pr("ear#", "head", 6.2f, 11, 0, 6, 6, 1.4f, 0, 2.6f, 0, gr);
            b.pr("earIn#", "ear#", 0, 0, -0.8f, 4, 4, 0.4f, 0, 2.6f, 0, pk);
            b.pr("incisor#", "head", 0.8f, 2.4f, -6.5f, 1.2f, 2.6f, 0.4f, 0, 0, 0, WHT);
            b.pr("whisker#", "snout", 3.4f, 0, -1.6f, 4, 0.3f, 0.3f, 0, 0, 0, 0x2a2a2e);
            b.p("tailRat", "tail", 0, 0, 3, 1.8f, 1.8f, 14, 0, 0, 7, pk);
        })
    );

    static B plush(CreatureDef d, float totalPx) { return new B(d, d.h * 16f / totalPx); }
    static void btnEyes(B b, float hw, float hh, float hd, int c) {
        b.pr("eye#", "head", hw * 0.24f, hh * 0.6f, -hd / 2 - 0.1f, 3, 3, 0.6f, 0, 0, 0, c);
    }

    static final Map<String, Consumer<CreatureDef>> MAP2 = Map.ofEntries(
        Map.entry("candy_cat", d -> { // pale sky-blue cat, 4 stubby legs, hatch on back, tongue
            int bl = 0xa8d4ec, ce = 0x7fb6dc, wh = WHT;
            B b = plush(d, 21);
            b.pr("leg#", null, 3, 5, -4, 3.4f, 5, 3.4f, 0, -2.5f, 0, bl, 0.8f);
            b.pr("legB#", null, 3, 5, 5, 3.4f, 5, 3.4f, 0, -2.5f, 0, bl, 0.8f);
            b.pr("paw#", "leg#", 0, -5, -0.4f, 3.8f, 1.4f, 4, 0, 0.7f, 0, wh);
            b.p("body", null, 0, 5, 0, 8, 7, 14, 0, 3.5f, 0, bl).sw(0.02f, 0.1f, 0, 2);
            b.p("splash", "body", 0, 7, 0, 8.2f, 1.2f, 6, 0, 0, 0, ce);
            b.p("hatch", "body", 0, 7.1f, 1, 4.4f, 0.5f, 4.4f, 0, 0, 0, 0xf0f4f8);
            b.p("head", "body", 0, 7, -7, 9, 8, 7, 0, 4, -1, bl);
            b.pr("ear#", "head", 3.4f, 8, 0, 2.6f, 3.4f, 1.2f, 0, 1.4f, 0, ce);
            b.p("snout", "head", 0, 2.4f, -4.6f, 5, 3, 2, 0, 0, 0, wh);
            b.p("nose", "head", 0, 4.2f, -5.7f, 1.8f, 1.2f, 0.6f, 0, 0, 0, ce);
            b.p("patch", "head", 0, 5.4f, -4.4f, 3, 1.6f, 0.5f, 0, 0, 0, wh);
            b.pr("eye#", "head", 2.4f, 5.4f, -4.1f, 1.8f, 1.8f, 0.5f, 0, 0, 0, 0x4a9cc8);
            b.pr("whisk#", "head", 3.8f, 3.4f, -4.4f, 1, 1, 0.5f, 0, 0, 0, ce);
            b.p("jaw", "head", 0, 1.4f, -4.4f, 4.6f, 1.4f, 3, 0, 0, 0, wh);
            b.p("tongue", "jaw", 0, 0.8f, -0.4f, 3, 0.8f, 3, 0, 0, 0, 0xf08aa0);
            b.pr("arm#", "body", 4.6f, 6, -5.5f, 2.2f, 5, 2.2f, 0, -2, 0, bl);
            b.p("tail", "body", 0, 5, 7, 2, 2, 10, 0, 1, 5, bl).sw(0.3f, 0.1f, 0, 1);
            b.p("tailTip", "tail", 0, 1, 10, 3.4f, 3.4f, 3.4f, 0, 0, 0, ce);
        }),
        Map.entry("cat_bee", d -> { // yellow cat/honeybee hybrid, brown stripes, blue sequin wings, antennae
            int ye = 0xf6cf3a, br = 0x6a4020, bw = 0x7ab8f0;
            B b = plush(d, 22);
            b.biped(6, 3.4f, 9, 8, 7, 10, 9, 8, 2.6f, 7, ye, ye, ye);
            b.pr("stripe#", "body", 0, 2, 0, 9.4f, 1.3f, 7.4f, 0, 0, 0, br);
            b.pr("stripeB#", "body", 0, 5, 0, 9.4f, 1.3f, 7.4f, 0, 0, 0, br);
            b.pr("ear#", "head", 3.6f, 9, 0, 2.8f, 4, 1.4f, 0, 1.8f, 0, ye);
            b.pr("anten#", "head", 1.8f, 9, -1, 0.6f, 5, 0.6f, 0, 2.5f, 0, br).sw(0.12f, 0.2f, 0, 0);
            b.pr("antBall#", "anten#", 0, 5, 0, 1.6f, 1.6f, 1.6f, 0, 0, 0, br);
            b.pr("eye#", "head", 2.4f, 5, -4.1f, 2.4f, 2.6f, 0.5f, 0, 0, 0, BLK);
            b.pr("glint#", "head", 2.4f, 5.4f, -4.3f, 0.8f, 0.8f, 0.3f, 0, 0, 0, WHT).g();
            b.p("muzzle", "head", 0, 2, -4.2f, 4, 2.6f, 0.6f, 0, 0, 0, 0xfff1c0);
            b.p("jaw", "head", 0, 2, -4.2f, 3, 0.8f, 0.6f, 0, 0, 0, 0x5a2a20);
            b.pr("wing#", "body", 3, 7, 3.6f, 0.6f, 9, 6, 0, 3, 1, bw).sw(0.2f, 0.9f, 0, 2);
            b.p("tail", "body", 0, 1, 3.6f, 1.6f, 1.6f, 6, 0, 0, 3, ye).sw(0.2f, 0.1f, 0, 1);
            b.p("stinger", "tail", 0, 0, 6, 1.4f, 1.4f, 2, 0, 0, 1, br);
        }),
        Map.entry("doodle_bones", d -> { // cyan plush, button eyes, pink nose/ears/hands, zipper, punk hair
            int cy = 0x3cc8d8, pk = 0xf08ab0, lt = 0x8ae6ee;
            B b = plush(d, 32);
            b.biped(8, 4, 11, 11, 8, 13, 12, 10, 3.4f, 10, cy, cy, cy);
            b.pr("hand#", "arm#", 0, -9.4f, 0, 4, 3.4f, 4, 0, -0.4f, 0, pk);
            b.pr("ear#", "head", 5, 11, 0, 3.4f, 4, 1.8f, 0, 1.6f, 0, pk);
            b.p("zipTop", "head", 0, 12.1f, 0, 3, 1.1f, 5, 0, 0, 0, lt);
            b.p("zip", "head", 0, 0, -5.1f, 0.8f, 12, 0.4f, 0, 6, 0, lt);
            b.p("nose", "head", 0, 5.2f, -5.4f, 3, 2.2f, 1.6f, 0, 0, 0, pk);
            b.pr("button#", "head", 3, 7.6f, -5.1f, 3.4f, 3.4f, 0.8f, 0, 0, 0, 0x1a1a22);
            b.pr("stitch#", "button#", 0, 0, -0.5f, 2.2f, 0.6f, 0.3f, 0, 0, 0, 0x6a6a7a);
            b.p("jaw", "head", 0, 2.4f, -5.1f, 6, 1, 0.5f, 0, 0, 0, 0x16303a);
            for (int i = 0; i < 4; i++) b.p("punk" + i, "head", (i - 1.5f) * 2f, 12, 0, 1.4f, 4 + (i % 2) * 2, 1.4f, 0, 2, 0, pk);
            b.pr("bone#", "body", 2.4f, 9, -4.2f, 0.8f, 5, 0.3f, 0, 0, 0, WHT);
        }),
        Map.entry("loopsy_noodle", d -> { // pink ball with squiggles, blue butterfly wings, X eyes, zipper + W tab
            int pk = 0xf06eb8, hot = 0xd01888, bl = 0x4a8af0, pu = 0x7a3ac0;
            B b = plush(d, 16);
            b.pr("leg#", null, 2.6f, 3, 0, 2.6f, 3, 2.6f, 0, -1.5f, 0, pk, 0.8f);
            b.p("body", null, 0, 3, 0, 11, 10, 9, 0, 5, 0, pk).sw(0.03f, 0.12f, 0, 2);
            for (int i = 0; i < 4; i++) b.p("squig" + i, "body", (i - 1.5f) * 2.6f, 2 + (i % 2) * 4, -4.6f, 1.2f, 3, 0.4f, 0, 0, 0, hot);
            b.p("zip", "body", 0, 0, -4.7f, 0.7f, 10, 0.4f, 0, 5, 0, WHT);
            b.p("pull", "body", 0, 10, -4.8f, 2.6f, 1.6f, 0.4f, 0, 0, 0, pu);
            b.p("head", "body", 0, 8, 0, 1, 1, 1, 0, 0, 0, pk);
            b.pr("eye#", "head", 2.6f, 0, -4.7f, 2.6f, 0.7f, 0.3f, 0, 0, 0, BLK);
            b.pr("eyeX#", "head", 2.6f, 0, -4.8f, 0.7f, 2.6f, 0.3f, 0, 0, 0, BLK);
            b.p("jaw", "body", 0, 3, -4.6f, 3.4f, 1, 0.4f, 0, 0, 0, 0x5a0a30);
            b.pr("arm#", "body", 5.8f, 6, 0, 2.2f, 4, 2.2f, 0, -1.4f, 0, pk);
            b.pr("wing#", "body", 5.4f, 7, 3, 0.6f, 9, 8, 0, 1, 3, bl).sw(0.2f, 0.8f, 0, 2);
            b.pr("wingRim#", "wing#", 0, 0, 0.1f, 0.7f, 2, 8.4f, 0, 5, 3, pu);
        }),
        Map.entry("bubby_chumz", d -> { // large red octopus-like ball, horizontal zipper, tiny black button eyes with silver X
            int rd = 0xd02a38, dk = 0x8a1020;
            B b = plush(d, 19);
            for (int i = 0; i < 4; i++) { b.pr("leg" + i + "#", null, 3 + (i % 2) * 2.2f, 5, (i / 2 - 0.5f) * 5, 2.6f, 5, 2.6f, 0, -2.5f, 0, rd, i % 2 == 0 ? 0.8f : 0); }
            b.p("body", null, 0, 5, 0, 15, 14, 13, 0, 7, 0, rd).sw(0.03f, 0.1f, 0, 2);
            b.p("zip", "body", 0, 7, -6.6f, 15.2f, 0.8f, 0.5f, 0, 0, 0, 0xe8e8ee);
            b.p("pull", "body", 6.5f, 7.6f, -6.8f, 2.4f, 1.8f, 0.4f, 0, 0, 0, 0x2a2a30);
            b.p("head", "body", 0, 14, 0, 1, 1, 1, 0, 0, 0, rd);
            b.pr("eye#", "head", 2.8f, -3, -6.7f, 1.8f, 1.8f, 0.6f, 0, 0, 0, BLK);
            b.pr("eyeX#", "head", 2.8f, -3, -7.0f, 1.2f, 0.3f, 0.2f, 0, 0, 0, 0xc8c8d0);
            b.p("jaw", "body", 0, 3, -6.6f, 5, 1.4f, 0.5f, 0, 0, 0, dk);
            b.pr("arm#", "body", 8.2f, 9, 0, 3, 7, 3, 0, -3, 0, rd);
            b.pr("tent#", "body", 4, 2, -4, 2.2f, 2.2f, 8, 0, 0, -3, dk);
        }),
        Map.entry("goober_gutz", d -> { // ball split by zipper: cyan squiggle (X eye, red spiky lips) | purple-pink patchwork; rabbit ears
            int cy = 0x38c0e8, pu = 0xb070e0, pk = 0xf0a0d0, sq = 0xff7ad8;
            B b = plush(d, 30);
            b.pr("leg#", null, 3.2f, 7, 0, 3.6f, 7, 3.6f, 0, -3.5f, 0, pk, 0.8f);
            b.p("body", null, 0, 7, 0, 14, 12, 10, 0, 6, 0, cy).sw(0.02f, 0.1f, 0, 2);
            b.p("bodyR", "body", 3.6f, 0, 0, 7, 12.2f, 10.2f, 0, 6, 0, pu);
            b.p("patch", "bodyR", 1.4f, 8, -5.2f, 4, 4, 0.4f, 0, 0, 0, pk);
            b.p("head", "body", 0, 12, 0, 14, 12, 10, 0, 6, 0, cy);
            b.p("headR", "head", 3.6f, 0, 0, 7, 12.2f, 10.2f, 0, 6, 0, pu);
            b.p("zip", "head", 0, -12, -5.2f, 0.8f, 24, 0.4f, 0, 12, 0, WHT);
            b.p("pull", "head", 0, 12.4f, -5.3f, 2.6f, 1.6f, 0.4f, 0, 0, 0, 0x2a2a30);
            b.p("eyeL", "head", -3, 8, -5.2f, 3, 0.7f, 0.3f, 0, 0, 0, WHT).p("eyeL2", "head", -3, 8, -5.2f, 0.7f, 3, 0.3f, 0, 0, 0, WHT);
            b.p("eyeR", "head", 3.4f, 8, -5.2f, 3.4f, 3.4f, 0.3f, 0, 0, 0, 0xf0b8d8);
            b.p("jaw", "head", 0, 3.4f, -5.2f, 8, 2.4f, 0.6f, 0, 0, 0, 0xd02040);
            for (int i = 0; i < 4; i++) b.p("fang" + i, "head", -3 + i * 2, 4.6f, -5.5f, 1, 1.4f, 0.4f, 0, 0, 0, WHT);
            b.pr("ear#", "head", 3.4f, 12, 0, 3, 11, 1.4f, 0, 5.5f, 0, cy);
            b.pr("arm#", "body", 8.2f, 10, 0, 2.8f, 8, 2.8f, 0, -3.4f, 0, pu);
            b.pr("squig#", "body", 3, 5, -5.2f, 4, 1, 0.3f, 0, 0, 0, sq);
        }),
        Map.entry("blanky_blankz", d -> { // white Mini-Critter plush, black stitched button eyes, zipper mouth with W pull
            B b = plush(d, 34);
            b.biped(9, 4.6f, 12, 11, 9, 14, 12, 11, 3.6f, 10, WHT, WHT, WHT);
            b.pr("btn#", "head", 3.4f, 7.4f, -5.7f, 3.8f, 3.8f, 0.7f, 0, 0, 0, BLK);
            b.pr("st#", "btn#", 0, 0, -0.45f, 5, 0.5f, 0.3f, 0, 0, 0, 0x9a9aa4);
            b.pr("stY#", "btn#", 0, 0, -0.45f, 0.5f, 5, 0.3f, 0, 0, 0, 0x9a9aa4);
            b.p("jaw", "head", 0, 2.6f, -5.7f, 7.4f, 1.4f, 0.6f, 0, 0, 0, 0x3a3a44);
            b.p("zip", "head", 0, 2.4f, -5.9f, 8, 0.4f, 0.3f, 0, 0, 0, 0xc4c4cc);
            b.p("pullW", "head", 4.2f, 2, -6, 2.4f, 2, 0.4f, 0, 0, 0, 0x2a2a30);
            b.pr("ear#", "head", 5.4f, 11, 0, 3.6f, 3.6f, 2, 0, 1.4f, 0, WHT);
            b.pr("hand#", "arm#", 0, -9.4f, 0, 4.2f, 3.2f, 4, 0, -0.4f, 0, WHT);
        }),
        Map.entry("boogie_bot", d -> { // lime battery robot: big rectangular head w/ antenna, yellow oval eyes, round body, 2 treads, big clawed hands
            int lm = 0x8ad82a, dg = 0x2f7a1a, ye = 0xffe840, gy = 0x9a9aa0;
            B b = plush(d, 37);
            b.pr("leg#", null, 4, 7, 0, 5, 7, 11, 0, -3.5f, 0, dg, 0.8f);
            b.pr("wheel#", "leg#", 0, -5, 0, 5.4f, 5, 5, 0, 0, 0, 0x2a2a30);
            b.p("body", null, 0, 7, 0, 11, 10, 9, 0, 5, 0, lm).sw(0.02f, 0.1f, 0, 2);
            b.p("dot", "body", 0, 4, -4.6f, 2.4f, 2.4f, 0.5f, 0, 0, 0, dg);
            b.p("head", "body", 0, 10, 0, 16, 13, 11, 0, 6.5f, 0, lm);
            b.p("faceplate", "head", 0, 1.5f, -5.6f, 13, 9, 0.5f, 0, 4.5f, 0, dg);
            b.pr("eye#", "head", 3.6f, 6.4f, -6, 2.2f, 5, 0.5f, 0, 0, 0, ye).g();
            b.p("jaw", "head", 0, 3.4f, -5.9f, 6, 0.8f, 0.4f, 0, 0, 0, ye).g();
            b.p("antenna", "head", 0, 13, 0, 0.8f, 5, 0.8f, 0, 2.5f, 0, gy);
            b.p("bulb", "antenna", 0, 5, 0, 2.6f, 2.6f, 2.6f, 0, 1, 0, 0xff4040).g().sw(0.1f, 0.5f, 0, 2);
            b.pr("ball#", "body", 6.4f, 9, 0, 2.6f, 2.6f, 2.6f, 0, 0, 0, gy);
            b.pr("arm#", "body", 8.6f, 8, 0, 1.6f, 9, 1.6f, 0, -4.5f, 0, gy);
            b.pr("hand#", "arm#", 0, -9, 0, 6, 5, 5, 0, -1.5f, 0, lm);
            b.pr("claw#", "hand#", 0, -4, -1.6f, 4, 2.4f, 1, 0, -1, 0, gy);
        }),
        Map.entry("glowby", d -> { // yellow five-pointed star, black screen face, antenna, speaker, two LED lights
            int ye = 0xf2c418, dk = 0xc89c10, sc = 0x08080c;
            B b = plush(d, 26);
            b.pr("leg#", null, 4, 8, 0, 4.4f, 8, 4.4f, 0, -4, 0, ye, 0.8f);
            b.p("body", null, 0, 8, 0, 13, 12, 6, 0, 6, 0, ye).sw(0.02f, 0.1f, 0, 2);
            b.p("head", "body", 0, 12, 0, 5, 7, 5, 0, 3.5f, 0, ye);
            b.pr("arm#", "body", 8.4f, 9, 0, 6, 3.4f, 4, 3, -0.5f, 0, ye);
            b.p("back", "body", 0, 0, 3.2f, 11, 10, 0.6f, 0, 6, 0, dk);
            b.p("screen", "body", 0, 3, -3.2f, 8.4f, 8, 0.6f, 0, 4, 0, 0xf4f4f4);
            b.p("screenIn", "body", 0, 3.4f, -3.5f, 7.6f, 7.2f, 0.4f, 0, 4, 0, sc);
            b.pr("eye#", "body", 1.8f, 7.4f, -3.9f, 1.4f, 2.6f, 0.3f, 0, 0, 0, 0x7af0ff).g();
            b.p("jaw", "body", 0, 5.2f, -3.9f, 4, 0.8f, 0.3f, 0, 0, 0, 0x7af0ff).g();
            b.p("btnA", "arm#".replace('#', 'R'), 2, 0, -2, 1.2f, 1.2f, 0.8f, 0, 1.8f, 0, 0xf06aa8);
            b.p("btnB", "armR", 4.4f, 0, -2, 1.2f, 1.2f, 0.8f, 0, 1.8f, 0, 0x4ac870);
            b.p("antenna", "armR", 3, 1, 0, 0.6f, 6, 0.6f, 0, 3, 0, ye);
            b.p("antTip", "antenna", 0, 6, 0, 1.4f, 1.4f, 1.4f, 0, 0, 0, WHT);
            b.pr("led#", "body", 1.8f, 1, -3.4f, 1.4f, 1.4f, 0.4f, 0, 0, 0, 0xff5050).g();
            b.p("speaker", "armL", 1, -1, -2, 3, 3, 0.6f, 0, 0, 0, dk);
        }),
        Map.entry("lily_lovebraids", d -> { // tanned ball-jointed doll, huge purple braids, oval eyes, long lashes, nose-less
            int sk = 0xd8a47c, hr = 0x9a30e0, hd = 0x7a20b8, dr = 0xf0e8f4;
            B b = plush(d, 41);
            b.biped(14, 3.4f, 9, 12, 6, 12, 11, 10, 2.6f, 13, dr, sk, sk);
            b.pr("joint#", "arm#", 0, -6.4f, 0, 3, 3, 3, 0, 0, 0, 0xc09070);
            b.pr("kneeJ#", "leg#", 0, -7, 0, 4, 3, 4, 0, 0, 0, 0xc09070);
            b.p("skirt", "body", 0, 0, 0, 14, 6, 10, 0, -1, 0, dr);
            b.p("sash", "body", 0, 7, -3.1f, 9.4f, 1.4f, 0.5f, 0, 0, 0, hr);
            b.p("updo", "head", 0, 11, 1, 11, 7, 9, 0, 3, 0, hr);
            b.p("hairBack", "head", 0, 0, 4, 12, 11, 2, 0, 5.5f, 1, hr);
            b.p("ring", "head", 0, 5, 5.5f, 4, 4, 1, 0, 0, 0, 0xf0c030);
            b.p("bang", "head", 1, 10.4f, -5.2f, 2, 4, 0.8f, 0, -2, 0, hr);
            b.pr("braid#", "head", 6.4f, 8, 2, 2.6f, 15, 2.6f, 0, -7.5f, 0, hr);
            b.pr("braidB#", "braid#", 0, -15, 0, 2.2f, 8, 2.2f, 0, -4, 0, hd);
            b.pr("eye#", "head", 2.6f, 6.4f, -5.2f, 3, 4, 0.5f, 0, 0, 0, WHT);
            b.pr("iris#", "head", 2.6f, 6.2f, -5.5f, 2, 3, 0.4f, 0, 0, 0, BLK);
            b.pr("lash#", "head", 3.6f, 8.8f, -5.5f, 3, 0.5f, 0.3f, 0, 0, 0, BLK);
            b.p("jaw", "head", 0, 1.8f, -5.2f, 4.6f, 1.6f, 0.5f, 0, 0, 0, 0x6a1a20);
            b.p("teeth", "head", 0, 3, -5.4f, 4, 0.8f, 0.3f, 0, 0, 0, WHT);
            b.pr("blush#", "head", 4, 3.4f, -5.2f, 2, 1.4f, 0.3f, 0, 0, 0, 0xe88a80);
        }),
        Map.entry("poppy", d -> { // porcelain doll, red dress, dark hair w/ ribbon, blue eyes, red lips
            int sk = 0xf4e6dc, hr = 0x4a2418, rd = 0xc41a28, wh = WHT;
            B b = plush(d, 26);
            b.biped(7, 2.6f, 8, 8, 6, 10, 10, 9, 2.2f, 8, rd, sk, sk);
            b.pr("sock#", "leg#", 0, -4, 0, 2.8f, 4, 2.8f, 0, -2, 0, wh);
            b.pr("shoe#", "leg#", 0, -7, -0.6f, 3, 1.4f, 4, 0, 0.7f, 0, 0x2a1210);
            b.p("skirt", "body", 0, 0, 0, 13, 5, 10, 0, -0.5f, 0, rd);
            b.p("frill", "body", 0, -3, 0, 14, 1, 11, 0, 0, 0, wh);
            b.p("collar", "body", 0, 8, -0.2f, 8, 1.4f, 6.4f, 0, 0, 0, wh);
            b.pr("puff#", "body", 5, 7, 0, 3.4f, 3, 3.4f, 0, 0, 0, rd);
            b.p("hairBack", "head", 0, 0, 4.4f, 11, 10, 2, 0, 5, 0, hr);
            b.p("hairTop", "head", 0, 10, 0, 10.6f, 2, 9.6f, 0, 0.4f, 0, hr);
            b.p("bang", "head", 0, 9, -4.6f, 10, 2.4f, 1, 0, -0.4f, 0, hr);
            b.pr("pigtail#", "head", 6.2f, 8, 1.5f, 2.8f, 8, 2.8f, 0, -3.5f, 0, hr);
            b.pr("bow#", "pigtail#", 0, 0, 0, 3, 3, 1.2f, 0, 0, 0, rd);
            b.pr("eye#", "head", 2.4f, 5.6f, -4.7f, 3, 3, 0.5f, 0, 0, 0, 0x5aa6f0).g();
            b.pr("pupil#", "head", 2.4f, 5.6f, -5f, 1.2f, 1.2f, 0.3f, 0, 0, 0, BLK);
            b.pr("blush#", "head", 3.8f, 3.2f, -4.7f, 2, 1.2f, 0.3f, 0, 0, 0, 0xf0a0a0);
            b.p("jaw", "head", 0, 1.8f, -4.7f, 3, 1.2f, 0.5f, 0, 0, 0, 0xc0102a);
            b.pr("hand#", "arm#", 0, -8, 0, 2.4f, 2, 2.4f, 0, 0, 0, sk);
        })
    );
}
