package com.poppy.mod.creature;

import com.poppy.mod.client.Pose;
import com.poppy.mod.entity.Atk;
import java.util.Map;

/** Signature attack choreography for the main characters (windup -> strike -> recover), keyed by creature id and attack kind. */
final class Canon7 {
    /** Mirror-keyed pose on an L/R pair (ry, rz mirrored for the left side). */
    static void pr(Pose p, String n, float ax, float ay, float az, float bx, float by, float bz, float w, float s, float r) {
        p.rot(n + "R", ax, ay, az, bx, by, bz, w, s, r); p.rot(n + "L", ax, -ay, -az, bx, -by, -bz, w, s, r);
    }
    static void r(Pose p, String n, float ax, float ay, float az, float bx, float by, float bz, float w, float s, float r) { p.rot(n, ax, ay, az, bx, by, bz, w, s, r); }

    static final Map<String, Map<Atk, AnimHook>> A = Map.of(
        "huggy_wuggy", Map.of(
            Atk.GRAB, (p, e, w, s, r, a) -> { pr(p, "arm", -0.4f, 0, 1.3f, 1.6f, 0, -0.25f, w, s, r); pr(p, "fore", 0, 0, 0, 0.7f, 0, 0, w, s, r); r(p, "body", 0.2f, 0, 0, -0.4f, 0, 0, w, s, r); r(p, "head", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); r(p, "jaw", 0, 0, 0, 0.5f, 0, 0, w, s, r); },
            Atk.SWIPE, (p, e, w, s, r, a) -> { r(p, "armR", -0.5f, -0.6f, 1.4f, 1.5f, 0.9f, -0.2f, w, s, r); r(p, "foreR", 0, 0, 0, 0.5f, 0, 0, w, s, r); r(p, "body", 0, -0.5f, 0, -0.1f, 0.6f, 0, w, s, r); r(p, "armL", 0.3f, 0, -0.3f, -0.3f, 0, -0.5f, w, s, r); },
            Atk.CHARGE, (p, e, w, s, r, a) -> { pr(p, "arm", -1.1f, 0, 0.3f, -1.5f, 0, 0.2f, w, s, r); r(p, "body", 0, 0, 0, -0.5f, 0, 0, w, s, r); r(p, "head", 0, 0, 0, 0.4f, 0, 0, w, s, r); }),
        "mommy_long_legs", Map.of(
            Atk.WHIP, (p, e, w, s, r, a) -> { r(p, "armR", 0, -1.1f, 0.3f, 0, 1.2f, -0.1f, w, s, r); r(p, "foreR", 0, 0, 0, 0.9f, 0, 0, w, s, r); r(p, "body", 0, -0.5f, 0, 0, 0.6f, 0, w, s, r); r(p, "head", 0, 0, 0.2f, 0, 0, -0.3f, w, s, r); r(p, "armL", 0, 0.3f, 0, 0, 0, 0.2f, w, s, r); },
            Atk.GRAB, (p, e, w, s, r, a) -> { pr(p, "arm", 0, -0.2f, 0.9f, 0, 0.9f, -0.2f, w, s, r); pr(p, "fore", 0, 0, 0, 1.0f, 0, 0, w, s, r); r(p, "body", 0.2f, 0, 0, -0.35f, 0, 0, w, s, r); },
            Atk.SLAM, (p, e, w, s, r, a) -> { pr(p, "arm", 0, 0, 1.3f, 0, 0, -0.5f, w, s, r); r(p, "body", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r); pr(p, "fore", 0, 0, 0, 0.6f, 0, 0, w, s, r); }),
        "the_prototype", Map.of(
            Atk.SLAM, (p, e, w, s, r, a) -> { pr(p, "arm", 2.8f, 0, 0.3f, 0.2f, 0, 0.1f, w, s, r); r(p, "torso", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r); r(p, "head", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); r(p, "body", 0.1f, 0, 0, -0.2f, 0, 0, w, s, r); },
            Atk.WHIP, (p, e, w, s, r, a) -> { r(p, "armR", 0.4f, -0.8f, 0.6f, 1.6f, 0.7f, 0, w, s, r); r(p, "foreR", 0, 0, 0, 0.6f, 0, 0, w, s, r); r(p, "torso", 0, -0.5f, 0, 0, 0.5f, 0, w, s, r); },
            Atk.SHOOT, (p, e, w, s, r, a) -> { r(p, "head", 0.5f, 0, 0, -0.1f, 0, 0, w, s, r); pr(p, "arm", 1.0f, 0, 0.4f, 1.6f, 0, 0.1f, w, s, r); r(p, "torso", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r); }),
        "catnap", Map.of(
            Atk.POUNCE, (p, e, w, s, r, a) -> { r(p, "body", 0.35f, 0, 0, -0.3f, 0, 0, w, s, r); pr(p, "arm", -0.3f, 0, 0, 1.3f, 0, 0, w, s, r); pr(p, "leg", 0.5f, 0, 0, -0.5f, 0, 0, w, s, r); r(p, "head", 0.3f, 0, 0, -0.2f, 0, 0, w, s, r); },
            Atk.SWIPE, (p, e, w, s, r, a) -> { r(p, "armR", -0.6f, -0.7f, 0.5f, 1.2f, 0.8f, 0, w, s, r); r(p, "body", 0, -0.3f, 0, 0, 0.4f, 0, w, s, r); r(p, "head", 0, 0.3f, 0, 0, -0.3f, 0, w, s, r); },
            Atk.GAS, (p, e, w, s, r, a) -> { r(p, "head", -0.5f, 0, 0, 0.4f, 0, 0, w, s, r); r(p, "body", -0.2f, 0, 0, 0.1f, 0, 0, w, s, r); r(p, "jaw", 0, 0, 0, 0.8f, 0, 0, w, s, r); }),
        "yarnaby", Map.of(
            Atk.WHIP, (p, e, w, s, r, a) -> { r(p, "body", 0.3f, 0, 0, -0.35f, 0, 0, w, s, r); r(p, "head", -0.4f, 0.3f, 0, 0.5f, -0.4f, 0, w, s, r); },
            Atk.POUNCE, (p, e, w, s, r, a) -> { r(p, "body", 0.4f, 0, 0, -0.5f, 0, 0, w, s, r); pr(p, "leg", 0.6f, 0, 0, -0.7f, 0, 0, w, s, r); r(p, "head", 0.2f, 0, 0, -0.3f, 0, 0, w, s, r); }),
        "boxy_boo", Map.of(
            Atk.POUNCE, (p, e, w, s, r, a) -> { p.scl("legL", 1, 0.45f, 1, 1, 1.5f, 1, w, s, r); p.scl("legR", 1, 0.45f, 1, 1, 1.5f, 1, w, s, r); r(p, "body", 0.3f, 0, 0, -0.5f, 0, 0, w, s, r); pr(p, "arm", -0.5f, 0, 0.6f, 1.6f, 0, 0.1f, w, s, r); },
            Atk.SWIPE, (p, e, w, s, r, a) -> { r(p, "armR", -0.8f, -0.4f, 1.0f, 1.6f, 0.6f, -0.1f, w, s, r); r(p, "head", 0.2f, 0, 0, -0.3f, 0, 0, w, s, r); r(p, "body", 0, -0.4f, 0, 0, 0.5f, 0, w, s, r); }),
        "bunzo_bunny", Map.of(
            Atk.CLAP, (p, e, w, s, r, a) -> { pr(p, "arm", 0.3f, 0, 1.4f, 1.0f, 0, -0.1f, w, s, r); r(p, "head", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r); }),
        "pianosaurus", Map.of(
            Atk.BITE, (p, e, w, s, r, a) -> { r(p, "neck", -0.6f, 0, 0, 0.9f, 0, 0, w, s, r); r(p, "head", -0.4f, 0, 0, 0.5f, 0, 0, w, s, r); r(p, "jaw", 0, 0, 0, 0.6f, 0, 0, w, s, r); r(p, "body", 0.1f, 0, 0, -0.15f, 0, 0, w, s, r); },
            Atk.SLAM, (p, e, w, s, r, a) -> { r(p, "neck", -1.0f, 0, 0, 1.2f, 0, 0, w, s, r); r(p, "head", -0.3f, 0, 0, 0.8f, 0, 0, w, s, r); r(p, "body", 0.15f, 0, 0, -0.25f, 0, 0, w, s, r); },
            Atk.STOMP, (p, e, w, s, r, a) -> { r(p, "legR", -0.8f, 0, 0, 0.5f, 0, 0, w, s, r); r(p, "body", -0.15f, 0, 0, 0.15f, 0, 0, w, s, r); r(p, "neck", -0.2f, 0, 0, 0.3f, 0, 0, w, s, r); }),
        "bron", Map.of(
            Atk.SLAM, (p, e, w, s, r, a) -> { r(p, "neck", -0.7f, 0, 0, 1.0f, 0, 0, w, s, r); r(p, "head", -0.3f, 0, 0, 0.6f, 0, 0, w, s, r); },
            Atk.STOMP, (p, e, w, s, r, a) -> { r(p, "legR", -0.9f, 0, 0, 0.5f, 0, 0, w, s, r); r(p, "legBR", 0, 0, 0, 0, 0, 0, w, s, r); r(p, "body", -0.1f, 0, 0, 0.1f, 0, 0, w, s, r); }));

    static void apply(CreatureDef d) {
        Map<Atk, AnimHook> m = A.get(d.id);
        if (m == null) return;
        for (int i = 0; i < d.attacks.size(); i++) {
            AttackSpec a = d.attacks.get(i);
            AnimHook h = m.get(a.kind());
            if (h != null) d.attacks.set(i, new AttackSpec(a.kind(), a.dmg(), a.reach(), a.minRange(), a.cd(), a.n(), a.s(), a.custom(), a.t(), h));
        }
    }
    private Canon7() {}
}
