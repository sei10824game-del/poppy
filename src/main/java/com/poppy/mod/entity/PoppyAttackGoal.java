package com.poppy.mod.entity;

import com.poppy.mod.creature.AttackSpec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/** Chases the target, picks a ready attack whose range fits, and keeps the mob facing/standing during it. */
public class PoppyAttackGoal extends Goal {
    private final PoppyMob m;
    public PoppyAttackGoal(PoppyMob m) { this.m = m; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

    @Override public boolean canUse() { LivingEntity t = m.getTarget(); return t != null && t.isAlive(); }
    @Override public boolean canContinueToUse() { return m.attacking() || canUse(); }
    @Override public void stop() { if (m.attacking()) m.endAttack(); m.getNavigation().stop(); }

    @Override public void tick() {
        LivingEntity t = m.getTarget();
        if (t != null) {
            m.getLookControl().setLookAt(t, 40f, 40f);
            double dx = t.getX() - m.getX(), dz = t.getZ() - m.getZ();
            float want = (float) (Mth.atan2(dz, dx) * 180 / Math.PI) - 90f;
            if (m.attacking() && m.atkT <= m.def.attacks.get(m.atk()).wind()) { m.setYRot(Mth.approachDegrees(m.getYRot(), want, 25f)); m.yBodyRot = m.getYRot(); m.yHeadRot = m.getYRot(); }
        }
        if (m.attacking()) {
            var k = m.def.attacks.get(m.atk()).kind();
            if (k != Atk.CHARGE && k != Atk.POUNCE) m.getNavigation().stop();
            return;
        }
        if (t == null) return;
        double d = m.distanceTo(t);
        if (m.gcd <= 0 && m.hasLineOfSight(t)) {
            List<Integer> ok = new ArrayList<>();
            for (int i = 0; i < m.def.attacks.size(); i++) { AttackSpec s = m.def.attacks.get(i); if (m.cd[i] <= 0 && d <= s.reach() && d >= s.minRange()) ok.add(i); }
            if (!ok.isEmpty()) { m.startAttack(ok.get(m.getRandom().nextInt(ok.size()))); return; }
        }
        float pref = Float.MAX_VALUE; for (AttackSpec s : m.def.attacks) pref = Math.min(pref, Math.max(1.5f, s.reach() * 0.7f));
        if (d > pref) m.getNavigation().moveTo(t, 1.0); else m.getNavigation().stop();
    }
}
