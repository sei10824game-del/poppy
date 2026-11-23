package com.poppy.mod.creature;

import com.poppy.mod.entity.Fx;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Server effects for BatchE custom attacks. */
final class EFx {
    /** Electric arc: sparks gather during windup, then a jagged lightning line hits everything along it (+ slowness). */
    static void arc(PoppyMob m, AttackSpec s, int t) {
        if (!(m.level() instanceof ServerLevel lv)) return;
        Vec3 core = m.position().add(Fx.flat(m).scale(m.getBbWidth() * 0.5)).add(0, m.getBbHeight() * 0.55, 0);
        if (t <= s.wind()) {
            lv.sendParticles(ParticleTypes.ELECTRIC_SPARK, core.x, core.y, core.z, 6, 0.8, 0.8, 0.8, 0.1);
            if (t == 1) m.playSound(SoundEvents.BEACON_POWER_SELECT, 2f, 1.6f);
            return;
        }
        if (t != s.wind() + 1) return;
        m.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 2f, 1.4f);
        Vec3 end = core.add(Fx.aim(m).scale(s.reach()));
        for (int i = 0; i <= 32; i++) {
            Vec3 p = core.lerp(end, i / 32.0).add(lv.random.nextGaussian() * 0.25, lv.random.nextGaussian() * 0.25, lv.random.nextGaussian() * 0.25);
            lv.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 3, 0.05, 0.05, 0.05, 0.02);
        }
        for (LivingEntity e : Fx.line(m, core, end, 1.2)) {
            Fx.hit(m, e, s.dmg(), 0.6f, e.position().subtract(m.position()));
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3));
            lv.sendParticles(ParticleTypes.ELECTRIC_SPARK, e.getX(), e.getY() + 1, e.getZ(), 20, 0.4, 0.6, 0.4, 0.2);
        }
    }

    /** Mimicry: imitates a human voice near the target while darkness falls, then snaps at anything in front. */
    static void mimic(PoppyMob m, AttackSpec s, int t) {
        if (!(m.level() instanceof ServerLevel lv)) return;
        LivingEntity tg = m.getTarget();
        if (t == 4 || t == 14) {
            Vec3 at = tg != null ? tg.position().add(Fx.flat(m).scale(-3)) : m.position();
            lv.playSound(null, at.x, at.y, at.z, t == 4 ? SoundEvents.VILLAGER_HURT : SoundEvents.PLAYER_HURT, net.minecraft.sounds.SoundSource.HOSTILE, 1.5f, 0.9f);
        }
        if (t == s.wind()) for (LivingEntity e : Fx.foes(m, m.position(), s.reach())) e.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
        if (t == s.wind() + 1) {
            m.playSound(SoundEvents.WARDEN_ROAR, 1.5f, 1.3f);
            for (LivingEntity e : Fx.arc(m, 5, 0.2)) Fx.hit(m, e, s.dmg(), 1.0f, Fx.flat(m));
        }
    }

    private EFx() {}
}
