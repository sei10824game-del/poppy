package com.poppy.mod.client;

import com.poppy.mod.creature.CreatureDef;
import com.poppy.mod.entity.Atk;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.util.Mth;

/** Pose generation: locomotion/idle from the def, then the active attack's choreography on top. */
public final class Anim {
    static final float P = (float) Math.PI;

    public static Pose compute(PoppyMob e, float pt, float ls, float lsa, float age, float headYaw, float headPitch) {
        Pose p = new Pose();
        CreatureDef d = e.def;
        for (CreatureDef.Part n : d.parts) {
            float[] a = p.g(n.name);
            if (n.walkAmp != 0) a[0] += Mth.cos(ls * 0.6662f + n.walkPhase) * n.walkAmp * lsa;
            if (n.swayAmp != 0) a[n.swayAxis] += Mth.sin(age * n.swaySpeed + n.swayPhase) * n.swayAmp;
        }
        // locomotion polish: arms counter-swing the legs, body bobs per step and leans into the run, subtle breathing, flinch when hurt
        for (CreatureDef.Part n : d.parts) {
            if (n.walkAmp == 0 && n.swayAmp == 0 && (n.name.equals("armL") || n.name.equals("armR"))) p.g(n.name)[0] += Mth.cos(ls * 0.6662f + (n.name.equals("armL") ? P : 0)) * 0.55f * lsa;
        }
        if (((e.tickCount + e.getId() * 13) % 90 < 4 || System.getProperty("poppy.blink") != null) && e.getHealth() > 0)
            for (CreatureDef.Part n : d.parts) if (n.name.matches("^(eye[WRBLR]?|iris|pupil|glint)[LR]?$")) p.g(n.name)[7] *= 0.12f;
        if (p.has("body")) {
            float[] b = p.g("body");
            b[4] += Math.abs(Mth.sin(ls * 0.6662f)) * lsa * 1.4f; b[0] -= Math.min(lsa, 1f) * 0.07f; b[7] += Mth.sin(age * 0.09f) * 0.012f;
            if (e.hurtTime > 0) { b[0] += e.hurtTime * 0.025f; if (p.has("head")) p.g("head")[0] += e.hurtTime * 0.03f; }
        }
        if (p.has("head")) { float[] h = p.g("head"); h[1] += -headYaw * Mth.DEG_TO_RAD; h[0] += -headPitch * Mth.DEG_TO_RAD; }
        if (p.has("body")) p.g("body")[4] += Mth.sin(age * 0.1f) * 0.35f;
        int a = e.atk();
        if (a >= 0) {
            var sp = d.attacks.get(a);
            float t = e.atkTick() + pt;
            float w = Mth.clamp(t / sp.wind(), 0, 1), st = Mth.clamp((t - sp.wind()) / sp.strike(), 0, 1), r = Mth.clamp((t - sp.wind() - sp.strike()) / sp.rec(), 0, 1);
            if (sp.anim() != null) sp.anim().apply(p, e, w, st, r, age);
            else pose(p, sp.kind(), w, st, r, age, e);
        }
        return p;
    }

    /** Apply to every node whose name starts with prefix. */
    private static void each(Pose p, String prefix, java.util.function.Consumer<String> f) { for (String n : java.util.List.copyOf(p.m.keySet())) if (n.startsWith(prefix)) f.accept(n); }
    /** Arms: side sign +1 for names containing R, -1 for L; rz and ry mirrored accordingly. */
    private static void arms(Pose p, float ax, float ay, float az, float bx, float by, float bz, float w, float s, float r) {
        each(p, "arm", n -> { float sg = n.startsWith("armL") ? -1 : 1; p.rot(n, ax, ay * sg, az * sg, bx, by * sg, bz * sg, w, s, r); });
    }
    private static void legs(Pose p, float ax, float bx, float w, float s, float r) {
        each(p, "leg", n -> { float sg = n.startsWith("legL") ? -1 : 1; p.rot(n, ax * (sg > 0 ? 1 : 0.6f), 0, 0, bx * (sg > 0 ? 1 : 0.6f), 0, 0, w, s, r); });
    }

    private static void pose(Pose p, Atk k, float w, float s, float r, float age, PoppyMob e) {
        boolean hold = s >= 1 && r <= 0;
        switch (k) {
            case SWIPE -> {
                p.rot("armR", 1.5f, -1.6f, 0.15f, 1.5f, 1.4f, 0f, w, s, r); p.rot("armL", 0.4f, 0, -0.6f, 0.2f, 0, -0.3f, w, s, r);
                p.rot("body", 0.05f, -0.8f, 0, -0.1f, 0.8f, 0, w, s, r); p.rot("head", 0, 0.5f, 0, 0, -0.5f, 0, w, s, r);
                legs(p, -0.5f, 0.4f, w, s, r); p.pos("body", 0, 0, 3, 0, 0, -4, w, s, r);
            }
            case GRAB -> {
                arms(p, 1.5f, 0, 1.0f, 1.55f, 0.25f, -0.15f, w, s, r); p.rot("body", 0.3f, 0, 0, -0.45f, 0, 0, w, s, r); p.rot("head", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r);
                p.pos("body", 0, 0, 4, 0, -1, -6, w, s, r);
                if (hold) { float q = Mth.sin(age * 0.9f) * 0.08f; arms(p, q, 0, q, q, 0, q, 1, 1, 0); p.pos("body", 0, 0, 0, 0, -1 + Mth.sin(age * 0.9f), -6, 1, 1, 0); }
            }
            case POUNCE -> {
                p.pos("body", 0, -7, 0, 0, 4, -10, w, s, r); p.rot("body", -0.5f, 0, 0, -1.1f, 0, 0, w, s, r);
                arms(p, -0.9f, 0, 0.3f, 2.7f, 0, 0.2f, w, s, r); legs(p, 1.0f, 0.9f, w, s, r); p.rot("head", 0.4f, 0, 0, 0.8f, 0, 0, w, s, r); p.rot("jaw", -0.2f, 0, 0, -0.8f, 0, 0, w, s, r);
                p.rot("tail", 0.2f, 0, 0, -0.5f, 0, 0, w, s, r);
            }
            case SLAM -> {
                arms(p, 3.05f, 0, 0.25f, 0.5f, 0, 0.1f, w, s, r); p.rot("body", 0.45f, 0, 0, -0.75f, 0, 0, w, s, r); p.pos("body", 0, 2, 3, 0, -5, -6, w, s, r);
                p.rot("head", 0.4f, 0, 0, -0.5f, 0, 0, w, s, r); legs(p, 0.2f, -0.3f, w, s, r);
            }
            case STOMP -> {
                p.rot("legR", 1.4f, 0, 0, 0f, 0, 0, w, s, r); p.rot("legL", -0.2f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("body", 0.25f, 0, 0, -0.25f, 0, 0, w, s, r);
                arms(p, 0.5f, 0, 1.1f, 0.2f, 0, 0.3f, w, s, r); p.pos("body", 0, 1, 0, 0, -4, -2, w, s, r);
            }
            case BITE -> {
                p.rot("jaw", -1.0f, 0, 0, 0.05f, 0, 0, w, s, r); p.rot("head", 0.7f, 0, 0, -0.5f, 0, 0, w, s, r); p.rot("body", 0.35f, 0, 0, -0.5f, 0, 0, w, s, r);
                p.pos("head", 0, 0, 2, 0, -2, -6, w, s, r); p.pos("body", 0, 0, 3, 0, 0, -7, w, s, r); arms(p, -0.4f, 0, 0.7f, 0.6f, 0, 0.4f, w, s, r);
            }
            case SPIN -> {
                arms(p, 0.1f, 0, 0.5f, 0f, 0, 1.57f, w, s, r); p.rot("body", 0.2f, 0, 0, 0.05f, 0, 0, w, s, r);
                if (s > 0 && r <= 0) p.g("body")[1] += s * Mth.TWO_PI * 3; else if (r > 0) p.g("body")[1] += Pose.ease(1 - r) * 0.0f;
                p.rot("head", 0.3f, 0, 0, 0f, 0, 0, w, s, r); legs(p, 0.2f, 0.7f, w, s, r);
                if (r > 0) p.add("head", 0, Mth.sin(age * 0.8f) * 0.3f * (1 - r), 0);
            }
            case SCREAM -> {
                p.rot("head", 0.7f, 0, 0, 0.45f, 0, 0, w, s, r); p.rot("jaw", -1.1f, 0, 0, -1.2f, 0, 0, w, s, r); p.rot("body", 0.35f, 0, 0, 0.2f, 0, 0, w, s, r);
                arms(p, 0.4f, 0, 0.6f, 0.9f, 0, 1.0f, w, s, r); p.scl("body", 1, 1, 1, 1.08f, 1.04f, 1.08f, w, s, r);
                if (s > 0 && r <= 0) { p.add("head", 0, 0, Mth.sin(age * 3f) * 0.12f); p.add("body", 0, 0, Mth.sin(age * 3.2f) * 0.05f); }
            }
            case GAS -> {
                p.scl("body", 1, 1, 1, 1.12f, 1.05f, 1.12f, w, s, r); p.rot("head", -0.35f, 0, 0, 0.1f, 0, 0, w, s, r); p.rot("jaw", -0.3f, 0, 0, -1.0f, 0, 0, w, s, r);
                p.rot("body", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r); arms(p, 0.2f, 0, 0.9f, 0.6f, 0, 0.35f, w, s, r);
                if (s > 0 && r <= 0) p.add("head", Mth.sin(age * 1.4f) * 0.1f, 0, 0);
            }
            case WHIP -> {
                p.rot("armR", 2.6f, 0.3f, 0.5f, 1.57f, 0f, 0, w, s, r); p.scl("armR", 1, 1, 1, 1, 2.3f, 1, w, s, r); p.rot("armL", 0.5f, 0, -0.6f, 0.3f, 0, -0.4f, w, s, r);
                p.rot("body", 0.35f, -0.4f, 0, -0.5f, 0.3f, 0, w, s, r); p.pos("body", 0, 0, 3, 0, 0, -5, w, s, r);
            }
            case SHOOT -> {
                p.rot("armR", 2.2f, 0, 0.2f, 1.5f, 0, 0, w, s, r); p.rot("head", 0.5f, 0, 0, -0.1f, 0, 0, w, s, r); p.rot("jaw", -0.7f, 0, 0, 0f, 0, 0, w, s, r);
                p.rot("body", -0.1f, 0, 0, 0.3f, 0, 0, w, s, r); p.scl("body", 1, 1, 1, 0.94f, 0.96f, 0.94f, w, s, r);
            }
            case CHARGE -> {
                p.rot("body", -0.3f, 0, 0, -0.65f, 0, 0, w, s, r); arms(p, -1.0f, 0, 0.2f, -1.5f, 0, 0.1f, w, s, r); p.rot("head", 0.2f, 0, 0, 0.3f, 0, 0, w, s, r);
                legs(p, 0.5f, 0, w, s, r); p.pos("body", 0, -3, 0, 0, -1, -4, w, s, r);
                if (s > 0 && r <= 0) { float q = Mth.sin(age * 1.3f) * 1.2f; p.g("legR")[0] = q; p.g("legL")[0] = -q; }
            }
            case CLAP -> {
                arms(p, 1.2f, 0, 1.35f, 1.57f, 0, 0.0f, w, s, r); p.rot("body", 0.25f, 0, 0, -0.3f, 0, 0, w, s, r); p.pos("body", 0, 0, 2, 0, 0, -4, w, s, r);
            }
            case CUSTOM -> {}
            case SUMMON -> {
                arms(p, 2.8f, 0, 0.6f, 1.0f, 0, 0.5f, w, s, r); p.rot("head", 0.55f, 0, 0, -0.2f, 0, 0, w, s, r); p.pos("body", 0, 0, 0, 0, -2, 0, w, s, r);
                if (s > 0 && r <= 0) p.add("head", 0, 0, Mth.sin(age * 2f) * 0.1f);
            }
        }
    }
    private Anim() {}
}
