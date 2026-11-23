package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;
import com.poppy.mod.entity.PoppyMob;

/**
 * One attack. kind=built-in behaviour (or Atk.CUSTOM with `custom` + `t`={wind,strike,rec}), dmg, reach=max start distance,
 * minRange, cd=cooldown ticks, n/s = kind-specific params (see Atk). `anim` (optional) overrides the client choreography for this attack.
 */
public record AttackSpec(Atk kind, float dmg, float reach, float minRange, int cd, int n, String s, Custom custom, int[] t, AnimHook anim) {
    /** Server effect for CUSTOM attacks; called every tick, ticks = 1.. total (windup ends at wind). */
    public interface Custom { void tick(PoppyMob m, AttackSpec spec, int ticks); }
    public int wind() { return custom != null ? t[0] : kind.wind; }
    public int strike() { return custom != null ? t[1] : kind.strike; }
    public int rec() { return custom != null ? t[2] : kind.rec; }
    public int total() { return wind() + strike() + rec(); }
}
