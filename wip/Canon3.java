package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;
import java.util.HashMap;
import java.util.Map;

/** Canon attack sets (from games/wiki behaviour) using the built-in, hand-choreographed Atk kinds. Replaces each listed creature's attacks. */
final class Canon3 {
    static final Map<String, String> SETS = new HashMap<>();
    static void s(String ids, String set) { for (String i : ids.split(",")) SETS.put(i, set); }

    static {
        s("the_prototype", "SLAM,WHIP,SHOOT:0xffd820:,CHARGE,SWIPE,SUMMON:mini_prototype:3");
        s("nightmare_prototype", "SLAM,WHIP,SHOOT:0xff2020:fire,CHARGE,SWIPE,SCREAM");
        s("mini_prototype", "SWIPE,POUNCE,BITE");
        s("mommy_long_legs", "WHIP,GRAB,SWIPE,SLAM,STOMP");
        s("mama_long_legs", "SLAM,WHIP,STOMP,SWIPE,SUMMON:mini_mommy:2");
        s("nightmare_mommy", "WHIP,GRAB,SWIPE,SLAM,SCREAM");
        s("daddy_long_legs", "GRAB,WHIP,SWIPE,STOMP");
        s("mini_mommy", "SWIPE,WHIP,POUNCE");
        s("nightmare_huggy", "GRAB,SWIPE,CHARGE,SCREAM,BITE");
        s("kissy_missy,nightmare_kissy_missy", "GRAB,SWIPE,CHARGE,SCREAM");
        s("giant_kissy", "SLAM,GRAB,STOMP,SWIPE");
        s("mini_huggy,mini_huggies,mini_kissy,mini_plush", "SWIPE,POUNCE,BITE");
        s("catnap,nightmare_catnap", "GAS,SWIPE,POUNCE,GRAB");
        s("dogday,nightmare_dogday", "SWIPE,POUNCE,BITE,CHARGE");
        s("bubba,nightmare_bubba,bubba_bubbaphant", "SLAM,CHARGE,STOMP,SWIPE");
        s("hoppy_hopscotch,nightmare_hoppy", "POUNCE,STOMP,SWIPE,CHARGE");
        s("kickin_chicken,nightmare_kickin", "SWIPE,CHARGE,POUNCE,SCREAM");
        s("craftycorn,nightmare_craftycorn", "CHARGE,SWIPE,SHOOT:0xf06ab0:,STOMP");
        s("pickypiggy,nightmare_pickypiggy", "BITE,CHARGE,SWIPE,STOMP");
        s("bobby_bearhug,nightmare_bobby", "GRAB,SWIPE,SLAM,SCREAM");
        s("mini_bubba", "SWIPE,STOMP");
        s("bron", "STOMP,SLAM,CHARGE,SWIPE");
        s("giant_bron", "STOMP,SLAM,CHARGE,SWIPE,SCREAM");
        s("boxy_boo,nightmare_boxy_boo", "SWIPE,POUNCE,CLAP,SHOOT:0xf2c030:");
        s("mini_boxy_boo", "SWIPE,POUNCE");
        s("doey_the_doughman", "SWIPE,WHIP,GRAB,SLAM,SUMMON:mini_doey:2");
        s("mini_doey,mini_doughman", "SWIPE,POUNCE");
        s("yarnaby", "WHIP,SWIPE,POUNCE,SHOOT:0xe03030:slow,GRAB");
        s("mini_yarnaby", "SWIPE,POUNCE,WHIP");
        s("bunzo_bunny", "CLAP,SWIPE,CHARGE");
        s("nightmare_bunzo", "CLAP,SWIPE,CHARGE,SCREAM,BITE");
        s("miss_delight", "SWIPE,SLAM,GRAB");
        s("nightmare_miss_delight", "SWIPE,SLAM,GRAB,SCREAM,BITE");
        s("pj_pug_a_pillar", "BITE,CHARGE,SWIPE,POUNCE");
        s("nightmare_pj", "BITE,CHARGE,SWIPE,SCREAM");
        s("mini_pj", "BITE,SWIPE");
        s("maggie_mako,touille,rabie_baby", "BITE,SWIPE,POUNCE");
        s("allister_gator", "BITE,SWIPE,STOMP,SLAM");
        s("poe", "SWIPE,SHOOT:0x7a6ad0:,POUNCE");
        s("simon_smoke", "SHOOT:0xff6a30:fire,SWIPE,BITE,POUNCE");
        s("icky_licky", "SHOOT:0x60e030:poison,SWIPE,POUNCE");
        s("baba_chops", "SWIPE,CHARGE,STOMP");
        s("candy_cat", "SHOOT:0xf06ab0:,SWIPE,POUNCE");
        s("cat_bee", "SHOOT:0xf6cf3a:poison,SWIPE,POUNCE");
        s("doodle_bones", "SWIPE,SHOOT:0x22222b:,POUNCE");
        s("loopsy_noodle,blanky_blankz", "SWIPE,POUNCE");
        s("bubby_chumz", "BITE,SLAM,CHARGE");
        s("goober_gutz", "SWIPE,BITE,POUNCE");
        s("boogie_bot", "SWIPE,SHOOT:0x7cf5ff:slow,CHARGE");
        s("giant_boogie_bot", "SLAM,STOMP,SHOOT:0x7cf5ff:slow,SWIPE");
        s("glowby", "SHOOT:0xf2c418:,CLAP,SWIPE");
        s("lily_lovebraids", "SWIPE,WHIP,GRAB");
        s("harley_sawyer", "SHOOT:0xffd820:slow,SWIPE,SLAM,GAS");
        s("poppy", "SWIPE,POUNCE,WHIP");
    }

    static void apply(CreatureDef d) {
        String set = SETS.get(d.id);
        if (set == null) return;
        d.attacks.clear();
        float dm = (float) d.dmg, r = Math.max(2.8f, d.w * 1.6f + 1.4f);
        for (String tok : set.split(",")) {
            String[] a = tok.split(":", -1);
            switch (Atk.valueOf(a[0])) {
                case SWIPE -> d.atk(Atk.SWIPE, dm, r, 0, 30);
                case BITE -> d.atk(Atk.BITE, dm * 1.2f, r, 0, 50);
                case POUNCE -> d.atk(Atk.POUNCE, dm * 1.1f, 10, 3, 90);
                case SLAM -> d.atk(Atk.SLAM, dm * 1.5f, r + 1, 0, 120);
                case STOMP -> d.atk(Atk.STOMP, dm * 1.3f, r + 1, 0, 100);
                case GRAB -> d.atk(Atk.GRAB, dm, r, 0, 160, 50, "");
                case WHIP -> d.atk(Atk.WHIP, dm * 1.1f, 12, 3, 120);
                case CHARGE -> d.atk(Atk.CHARGE, dm * 1.3f, 14, 5, 200);
                case SCREAM -> d.atk(Atk.SCREAM, dm * 0.4f, 9, 3, 260);
                case GAS -> d.atk(Atk.GAS, dm * 0.3f, 8, 0, 240, 1, "");
                case CLAP -> d.atk(Atk.CLAP, dm * 0.8f, r, 0, 150);
                case SHOOT -> d.atk(Atk.SHOOT, dm * 0.9f, 16, 5, 100, Integer.decode(a[1]), a[2]);
                case SUMMON -> d.atk(Atk.SUMMON, 0, 10, 0, 420, Integer.parseInt(a[2]), a[1]);
                default -> { }
            }
        }
    }
    private Canon3() {}
}
