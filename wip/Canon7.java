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

    static final Map<Atk, AnimHook> TOY = Map.of(
        Atk.BITE, (p, e, w, s, r, a) -> { r(p, "head", -0.4f, 0, 0, 0.5f, 0, 0, w, s, r); r(p, "jaw", 0, 0, 0, 0.5f, 0, 0, w, s, r); r(p, "body", 0.2f, 0, 0, -0.35f, 0, 0, w, s, r); pr(p, "arm", -0.4f, 0, 0.5f, 0.6f, 0, 0.1f, w, s, r); },
        Atk.SCREAM, (p, e, w, s, r, a) -> { r(p, "head", 0.1f, 0, 0, -0.55f, 0, 0, w, s, r); r(p, "body", 0.1f, 0, 0, -0.25f, 0, 0, w, s, r); pr(p, "arm", 0.3f, 0, 0.6f, 0.5f, 0, 1.0f, w, s, r); r(p, "jaw", 0, 0, 0, 0.6f, 0, 0, w, s, r); },
        Atk.POUNCE, (p, e, w, s, r, a) -> { r(p, "body", 0.5f, 0, 0, -0.5f, 0, 0, w, s, r); pr(p, "leg", 0.6f, 0, 0, -0.5f, 0, 0, w, s, r); pr(p, "arm", -0.6f, 0, 0.4f, 1.7f, 0, 0.1f, w, s, r); r(p, "head", 0.3f, 0, 0, -0.3f, 0, 0, w, s, r); },
        Atk.SWIPE, (p, e, w, s, r, a) -> { r(p, "armR", -0.7f, -0.6f, 1.1f, 1.5f, 0.8f, -0.2f, w, s, r); r(p, "body", 0, -0.45f, 0, 0, 0.55f, 0, w, s, r); r(p, "head", 0, 0.3f, 0, 0, -0.35f, 0, w, s, r); r(p, "armL", 0.3f, 0, -0.4f, -0.2f, 0, -0.3f, w, s, r); },
        Atk.SLAM, (p, e, w, s, r, a) -> { pr(p, "arm", 2.7f, 0, 0.3f, 0.2f, 0, 0.1f, w, s, r); r(p, "body", -0.25f, 0, 0, 0.45f, 0, 0, w, s, r); r(p, "head", -0.3f, 0, 0, 0.3f, 0, 0, w, s, r); },
        Atk.STOMP, (p, e, w, s, r, a) -> { r(p, "legR", -1.0f, 0, 0, 0.4f, 0, 0, w, s, r); r(p, "body", -0.2f, 0.2f, 0, 0.2f, 0, 0, w, s, r); pr(p, "arm", 0.6f, 0, 0.8f, -0.1f, 0, 0.3f, w, s, r); },
        Atk.CHARGE, (p, e, w, s, r, a) -> { pr(p, "arm", -1.0f, 0, 0.3f, -1.4f, 0, 0.2f, w, s, r); r(p, "body", 0, 0, 0, -0.5f, 0, 0, w, s, r); r(p, "head", 0, 0, 0, 0.4f, 0, 0, w, s, r); },
        Atk.GRAB, (p, e, w, s, r, a) -> { pr(p, "arm", -0.3f, 0, 1.3f, 1.5f, 0, -0.3f, w, s, r); r(p, "body", 0.2f, 0, 0, -0.35f, 0, 0, w, s, r); r(p, "head", 0.2f, 0, 0, -0.2f, 0, 0, w, s, r); });

    static final java.util.Set<String> TOYS = java.util.Set.of(
        "bubba_bubbaphant", "craftycorn", "kickin_chicken", "pickypiggy", "hoppy_hopscotch", "dogday", "bobby_bearhug", "mini_bubba",
        "nightmare_bubba", "nightmare_craftycorn", "nightmare_kickin", "nightmare_pickypiggy", "nightmare_hoppy", "nightmare_dogday", "nightmare_bobby",
        "maggie_mako", "allister_gator", "poe", "rabie_baby", "simon_smoke", "icky_licky", "baba_chops", "touille",
        "blanky_blankz", "doodle_bones", "goober_gutz", "loopsy_noodle", "boogie_bot", "giant_boogie_bot", "bunzo_bunny", "nightmare_bunzo", "miss_delight", "nightmare_miss_delight", "lily_lovebraids", "poppy");
    static final java.util.Set<String> HUGGYS = java.util.Set.of("kissy_missy", "nightmare_kissy_missy", "giant_kissy", "mini_kissy", "mini_huggy", "mini_huggies", "mini_plush", "nightmare_huggy");
    static final java.util.Set<String> LONGS = java.util.Set.of("mama_long_legs", "nightmare_mommy", "mini_mommy", "daddy_long_legs");

    static Map<Atk, AnimHook> mapFor(String id) {
        if (A.containsKey(id)) return A.get(id);
        if (TOYS.contains(id)) return TOY;
        if (HUGGYS.contains(id)) return A.get("huggy_wuggy");
        if (LONGS.contains(id)) return A.get("mommy_long_legs");
        return switch (id) {
            case "mini_prototype", "nightmare_prototype" -> A.get("the_prototype");
            case "nightmare_catnap" -> A.get("catnap");
            case "giant_bron" -> A.get("bron");
            case "mini_boxy_boo", "nightmare_boxy_boo" -> A.get("boxy_boo");
            case "mini_yarnaby" -> A.get("yarnaby");
            default -> null;
        };
    }

    static void apply(CreatureDef d) {
        Map<Atk, AnimHook> m = mapFor(d.id);
        if (m == null) return;
        for (int i = 0; i < d.attacks.size(); i++) {
            AttackSpec a = d.attacks.get(i);
            AnimHook h = m.get(a.kind());
            if (h != null) d.attacks.set(i, new AttackSpec(a.kind(), a.dmg(), a.reach(), a.minRange(), a.cd(), a.n(), a.s(), a.custom(), a.t(), h));
        }
    }
    private Canon7() {}
}
