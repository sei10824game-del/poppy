package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-driven creature. Model = tree of coloured boxes (pixels, 16px = 1 block, y up, front = -z, +x = entity's right).
 * Animators drive nodes by NAME: head, body, armL, armR (extra: armL2.. any name starting "arm"+L/R), legL, legR, jaw, tail.
 */
public final class CreatureDef {
    public static final class Part {
        public final String name, parent;
        public final float px, py, pz, w, h, d, cx, cy, cz;
        public int color;
        public boolean glow;
        /** Surface material: 0 plain, 1 fur, 2 cloth, 3 plastic, 4 metal (procedural grayscale textures tinted by colour). */
        public int mat;
        /** Optional hand-drawn overlay texture (decal/NAME.png) painted on this box's front face. */
        public String decal;
        /** Fine-detail extra (shading band, tuft, digit): skipped for distant mobs. */
        public boolean detail;
        /** Rest rotation (radians) applied every frame under animation, so ears/limbs can sit splayed. */
        public float rx, ry, rz;
        public float walkAmp, walkPhase, swayAmp, swaySpeed, swayPhase;
        public int swayAxis; // 0=x 1=y 2=z
        Part(String n, String p, float px, float py, float pz, float w, float h, float d, float cx, float cy, float cz, int c) {
            name = n; parent = p; this.px = px; this.py = py; this.pz = pz; this.w = w; this.h = h; this.d = d; this.cx = cx; this.cy = cy; this.cz = cz; color = c;
        }
    }

    public final String id, name, nameJa;
    public float w = 0.9f, h = 2f, scale = 1f;
    public double hp = 40, dmg = 5, speed = 0.3, kbRes = 0.3, armor = 0;
    public int eggA = 0xffffff, eggB = 0x000000, spawnWeight = 0;
    public boolean boss, fireImmune, noGravityFly;
    public final List<Part> parts = new ArrayList<>();
    public final List<AttackSpec> attacks = new ArrayList<>();

    private CreatureDef(String id, String name, String ja) { this.id = id; this.name = name; this.nameJa = ja; }
    public static CreatureDef of(String id, String name, String ja) { return new CreatureDef(id, name, ja); }

    public CreatureDef size(float w, float h) { this.w = w; this.h = h; return this; }
    public CreatureDef hp(double v) { hp = v; return this; }
    public CreatureDef dmg(double v) { dmg = v; return this; }
    public CreatureDef speed(double v) { speed = v; return this; }
    public CreatureDef kbRes(double v) { kbRes = v; return this; }
    public CreatureDef armor(double v) { armor = v; return this; }
    public CreatureDef egg(int a, int b) { eggA = a; eggB = b; return this; }
    public CreatureDef spawn(int weight) { spawnWeight = weight; return this; }
    public CreatureDef boss() { boss = true; return this; }
    public CreatureDef fireImmune() { fireImmune = true; return this; }

    /** Add a box node. pivot (px,py,pz) relative to parent pivot (or entity feet if parent null); size w,h,d; box centre offset (cx,cy,cz) from pivot. */
    public CreatureDef part(String name, String parent, float px, float py, float pz, float w, float h, float d, float cx, float cy, float cz, int color) {
        parts.add(new Part(name, parent, px, py, pz, w, h, d, cx, cy, cz, color)); return this;
    }
    private Part last() { return parts.get(parts.size() - 1); }
    public CreatureDef decal(String n) { last().decal = n; return this; }
    public CreatureDef glow() { last().glow = true; return this; }
    /** Walk-cycle swing on last part (radians amp, phase offset e.g. PI for opposite leg). */
    public CreatureDef walk(float amp, float phase) { last().walkAmp = amp; last().walkPhase = phase; return this; }
    /** Idle sway on last part. axis 0=x 1=y 2=z */
    public CreatureDef sway(float amp, float speed, float phase, int axis) { Part p = last(); p.swayAmp = amp; p.swaySpeed = speed; p.swayPhase = phase; p.swayAxis = axis; return this; }

    public CreatureDef atk(Atk k, float dmg, float reach, float minRange, int cd) { attacks.add(new AttackSpec(k, dmg, reach, minRange, cd, 0, "", null, null, null)); return this; }
    public CreatureDef atk(Atk k, float dmg, float reach, float minRange, int cd, int n, String s) { attacks.add(new AttackSpec(k, dmg, reach, minRange, cd, n, s, null, null, null)); return this; }
    /** Fully custom attack: own timing {wind,strike,rec}, server effect and client choreography (anim may be null -> idle pose). */
    public CreatureDef custom(float dmg, float reach, float minRange, int cd, int wind, int strike, int rec, AttackSpec.Custom fx, AnimHook anim) { attacks.add(new AttackSpec(Atk.CUSTOM, dmg, reach, minRange, cd, 0, "", fx, new int[]{wind, strike, rec}, anim)); return this; }
    /** Replace the choreography of the most recently added attack. */
    public CreatureDef anim(AnimHook h) { AttackSpec a = attacks.remove(attacks.size() - 1); attacks.add(new AttackSpec(a.kind(), a.dmg(), a.reach(), a.minRange(), a.cd(), a.n(), a.s(), a.custom(), a.t(), h)); return this; }
}
