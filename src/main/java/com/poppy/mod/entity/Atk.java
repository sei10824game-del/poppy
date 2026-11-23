package com.poppy.mod.entity;

import com.poppy.mod.creature.AttackSpec;
import com.poppy.mod.creature.Creatures;
import net.minecraft.core.particles.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3f;

import java.util.List;

/** Attack kinds: timing (windup/strike/recover ticks) + server effects. Client poses live in client.Anim. */
public enum Atk {
    SWIPE(10, 4, 8), GRAB(14, 6, 12), POUNCE(14, 14, 12), SLAM(16, 4, 14), STOMP(12, 4, 12), BITE(10, 4, 10),
    SPIN(10, 22, 12), SCREAM(18, 14, 12), GAS(12, 20, 12), WHIP(14, 4, 12), SHOOT(12, 2, 10), CHARGE(14, 16, 14),
    CLAP(10, 3, 10), SUMMON(18, 2, 14), CUSTOM(10, 4, 10);

    public final int wind, strike, rec;
    Atk(int w, int s, int r) { wind = w; strike = s; rec = r; }
    public int total() { return wind + strike + rec; }

    /** Called every server tick while attacking; t = ticks since start (1-based). */
    public void tick(PoppyMob m, AttackSpec s, int t) {
        if (!(m.level() instanceof ServerLevel lv)) return;
        int hit = s.wind() + 1; int wind = s.wind(), strike = s.strike();
        Vec3 look = Fx.flat(m);
        switch (this) {
            case CUSTOM -> { if (s.custom() != null) s.custom().tick(m, s, t); }
            case SWIPE -> { if (t == hit) { sound(m, 1); for (LivingEntity e : Fx.arc(m, s.reach(), 0.1)) Fx.hit(m, e, s.dmg(), 0.9f, look.yRot(-1.2f).add(look)); } }
            case CLAP -> { if (t == hit) { sound(m, 2); for (LivingEntity e : Fx.arc(m, s.reach(), 0.2)) { Fx.hit(m, e, s.dmg(), 0.8f, look); e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 2)); }
                lv.sendParticles(ParticleTypes.EXPLOSION, m.getX() + look.x * 1.5, m.getY() + m.getBbHeight() * 0.6, m.getZ() + look.z * 1.5, 1, 0, 0, 0, 0); } }
            case BITE -> { if (t == hit) { sound(m, 3); for (LivingEntity e : Fx.arc(m, s.reach(), 0.3)) { Fx.hit(m, e, s.dmg() * 1.2f, 0.2f, look);
                e.setDeltaMovement(look.scale(-0.35).add(0, 0.1, 0)); e.hurtMarked = true; } } }
            case SLAM, STOMP -> { if (t == hit) { sound(m, 4); double r = s.reach(); Vec3 c = m.position().add(look.scale(this == SLAM ? r * 0.55 : 0));
                for (LivingEntity e : Fx.foes(m, c, r)) { Fx.hit(m, e, s.dmg() * (float) (1.0 - 0.5 * e.position().distanceTo(c) / r), 1.1f, e.position().subtract(c)); e.setDeltaMovement(e.getDeltaMovement().add(0, 0.55, 0)); e.hurtMarked = true;
                    if (this == STOMP) e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1)); }
                lv.sendParticles(ParticleTypes.EXPLOSION, c.x, m.getY() + 0.2, c.z, 3, r * 0.3, 0, r * 0.3, 0);
                for (int i = 0; i < 40; i++) { double a = i / 40.0 * Mth.TWO_PI; lv.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, c.x + Math.cos(a) * r * 0.8, m.getY() + 0.1, c.z + Math.sin(a) * r * 0.8, 0, Math.cos(a), 0.05, Math.sin(a), 0.2); } } }
            case SPIN -> { if (t > hit && t <= hit + strike && t % 3 == 0) { sound(m, 1); for (LivingEntity e : Fx.foes(m, m.position(), s.reach())) Fx.hit(m, e, s.dmg() * 0.6f, 0.9f, e.position().subtract(m.position()).add(0, 0.3, 0)); } }
            case SCREAM -> { if (t == hit) { m.playSound(SoundEvents.RAVAGER_ROAR, 3f, 0.6f); for (LivingEntity e : Fx.arc(m, s.reach(), 0.4)) { Fx.hit(m, e, s.dmg(), 0.5f, look);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1)); e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0)); e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0)); } }
                if (t >= hit && t < hit + strike && t % 2 == 0) { Vec3 o = m.getEyePosition().add(look.scale(1.0 + (t - hit) * 0.5)); lv.sendParticles(ParticleTypes.SONIC_BOOM, o.x, o.y, o.z, 1, 0, 0, 0, 0); } }
            case GAS -> { if (t == hit) { m.playSound(SoundEvents.FIRECHARGE_USE, 2f, 0.5f); AreaEffectCloud c = new AreaEffectCloud(lv, m.getX() + look.x * 2.5, m.getY(), m.getZ() + look.z * 2.5);
                c.setOwner(m); c.setRadius(3.5f); c.setDuration(180); c.setRadiusPerTick(-0.004f); c.setParticle(new DustParticleOptions(new Vector3f(0.85f, 0.05f, 0.1f), 2.2f));
                c.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 140, 0)); c.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1)); c.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0));
                if (s.n() > 0) c.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0)); lv.addFreshEntity(c); }
                if (t >= hit && t < hit + strike) { Vec3 o = m.getEyePosition().add(look.scale(1.2)); lv.sendParticles(new DustParticleOptions(new Vector3f(0.85f, 0.05f, 0.1f), 2f), o.x, o.y - 0.2, o.z, 6, 0.3, 0.2, 0.3, 0.05); } }
            case WHIP -> { if (t == hit) { sound(m, 1); Vec3 a = m.getEyePosition().subtract(0, 0.4, 0); Vec3 b = a.add(Fx.aim(m).scale(s.reach()));
                for (int i = 0; i <= 12; i++) { Vec3 p = a.lerp(b, i / 12.0); lv.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 1, 0, 0, 0, 0); }
                for (LivingEntity e : Fx.line(m, a, b, 1.1)) { Fx.hit(m, e, s.dmg(), 0.3f, look); e.setDeltaMovement(m.position().subtract(e.position()).normalize().scale(0.9).add(0, 0.25, 0)); e.hurtMarked = true; } } }
            case SHOOT -> { if (t == hit && m.getTarget() != null) { sound(m, 5); LivingEntity tg = m.getTarget(); Vec3 o = m.getEyePosition(); Vec3 d = tg.getEyePosition().subtract(o).normalize();
                PoppyBolt b = new PoppyBolt(lv, m, s.dmg(), s.n() == 0 ? 0xff3030 : s.n(), s.s()); b.setPos(o.x, o.y - 0.2, o.z); b.shoot(d.x, d.y, d.z, 1.4f, 1f); lv.addFreshEntity(b); } }
            case CHARGE -> { if (t > hit && t <= hit + strike) { Vec3 d = look.scale(0.95); m.setDeltaMovement(d.x, m.getDeltaMovement().y, d.z); m.hasImpulse = true;
                for (LivingEntity e : Fx.foes(m, m.position().add(0, 0.5, 0), m.getBbWidth() * 0.5 + 1.0)) if (m.hitOnce.add(e.getId())) { Fx.hit(m, e, s.dmg(), 1.4f, look); e.setDeltaMovement(look.x * 1.3, 0.5, look.z * 1.3); e.hurtMarked = true; }
                if (m.horizontalCollision && t > hit + 3) { m.atkT = hit + strike; m.playSound(SoundEvents.GENERIC_EXPLODE, 1f, 1.3f); m.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 5)); } } }
            case POUNCE -> { if (t == hit && m.getTarget() != null) { sound(m, 1); Vec3 d = m.getTarget().position().subtract(m.position()); Vec3 h = new Vec3(d.x, 0, d.z).normalize().scale(Math.min(1.5, 0.45 + d.horizontalDistance() * 0.13)); m.setDeltaMovement(h.x, 0.55, h.z); m.hasImpulse = true; }
                if (t > hit && t <= hit + strike) for (LivingEntity e : Fx.foes(m, m.position().add(0, m.getBbHeight() * 0.4, 0), m.getBbWidth() * 0.5 + 1.0)) if (m.hitOnce.add(e.getId())) Fx.hit(m, e, s.dmg(), 0.9f, look); }
            case SUMMON -> { if (t == hit) { m.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 2f, 0.7f); var d = Creatures.BY_ID.get(s.s());
                if (d != null && lv.getEntitiesOfClass(PoppyMob.class, m.getBoundingBox().inflate(24), e -> e.def == d).size() < 6)
                    for (int i = 0; i < Math.max(1, s.n()); i++) { var et = com.poppy.mod.Poppy.TYPES.get(d.id); if (et == null) break; Mob c = et.get().create(lv); if (c == null) break;
                        c.moveTo(m.getX() + lv.random.nextInt(7) - 3, m.getY(), m.getZ() + lv.random.nextInt(7) - 3, lv.random.nextFloat() * 360, 0); c.finalizeSpawn(lv, lv.getCurrentDifficultyAt(c.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                        c.setTarget(m.getTarget()); lv.addFreshEntity(c); lv.sendParticles(ParticleTypes.POOF, c.getX(), c.getY() + 1, c.getZ(), 12, .3, .5, .3, .02); } } }
            case GRAB -> { if (t == hit) { sound(m, 1); LivingEntity g = Fx.arc(m, s.reach(), 0.4).stream().findFirst().orElse(null);
                    if (g != null) { m.grabbed = g; m.holdTicks = s.n() > 0 ? s.n() : 50; } }
                if (m.grabbed != null) { LivingEntity g = m.grabbed; if (!g.isAlive() || m.holdTicks <= 0 || g.distanceTo(m) > 6) { if (g.isAlive()) { g.setDeltaMovement(look.x * 1.4, 0.6, look.z * 1.4); g.hurtMarked = true; } m.grabbed = null; }
                    else { m.holdTicks--; m.atkT = hit + 1; Vec3 anchor = m.position().add(look.scale(m.getBbWidth() * 0.5 + 0.4)).add(0, m.getBbHeight() * 0.45, 0);
                        g.setDeltaMovement(anchor.subtract(g.position().add(0, g.getBbHeight() * 0.5, 0)).scale(0.6)); g.hurtMarked = true; g.fallDistance = 0;
                        if (m.holdTicks % 10 == 0) { Fx.hit(m, g, s.dmg() * 0.35f, 0f, Vec3.ZERO); g.setDeltaMovement(anchor.subtract(g.position()).scale(0.3)); m.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1f, 0.6f); } } } }
        }
    }

    private static void sound(PoppyMob m, int k) {
        var snd = switch (k) { case 1 -> SoundEvents.PLAYER_ATTACK_SWEEP; case 2 -> SoundEvents.ANVIL_LAND; case 3 -> SoundEvents.WOLF_GROWL; case 4 -> SoundEvents.GENERIC_EXPLODE; default -> SoundEvents.SNOWBALL_THROW; };
        m.level().playSound(null, m.getX(), m.getY(), m.getZ(), snd, SoundSource.HOSTILE, 1.2f, 0.7f + m.getRandom().nextFloat() * 0.3f);
    }
}
