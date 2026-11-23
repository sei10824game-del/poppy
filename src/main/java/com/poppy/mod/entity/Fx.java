package com.poppy.mod.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class Fx {
    public static boolean isFoe(PoppyMob m, LivingEntity e) {
        return e != m && e.isAlive() && !(e instanceof PoppyMob) && !(e instanceof ArmorStand) && !(e instanceof Player p && (p.isCreative() || p.isSpectator()));
    }
    public static Vec3 flat(PoppyMob m) { float y = m.getYRot() * ((float) Math.PI / 180f); return new Vec3(-Math.sin(y), 0, Math.cos(y)); }
    public static Vec3 aim(PoppyMob m) { return m.getTarget() != null ? m.getTarget().getEyePosition().subtract(m.getEyePosition()).normalize() : m.getLookAngle(); }

    public static List<LivingEntity> foes(PoppyMob m, Vec3 c, double r) {
        return m.level().getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(r, r * 0.6 + 1, r), e -> isFoe(m, e) && e.position().distanceTo(c) <= r + e.getBbWidth() * 0.5);
    }
    /** Foes in front of m within reach; minDot = cos-ish cone width (-1..1). */
    public static List<LivingEntity> arc(PoppyMob m, double reach, double minDot) {
        Vec3 f = flat(m);
        return foes(m, m.position(), reach + m.getBbWidth() * 0.5).stream().filter(e -> { Vec3 d = e.position().subtract(m.position()); d = new Vec3(d.x, 0, d.z); return d.lengthSqr() < 0.01 || d.normalize().dot(f) > minDot; }).toList();
    }
    public static List<LivingEntity> line(PoppyMob m, Vec3 a, Vec3 b, double r) {
        return m.level().getEntitiesOfClass(LivingEntity.class, new AABB(a, b).inflate(r), e -> isFoe(m, e) && e.getBoundingBox().inflate(r).clip(a, b).isPresent());
    }
    public static void hit(PoppyMob m, LivingEntity e, float dmg, float kb, Vec3 dir) {
        if (!e.hurt(m.damageSources().mobAttack(m), dmg) || kb <= 0) return;
        Vec3 d = new Vec3(dir.x, 0, dir.z); if (d.lengthSqr() < 1e-4) return;
        d = d.normalize().scale(kb);
        e.setDeltaMovement(e.getDeltaMovement().add(d.x, 0.25 * kb, d.z)); e.hurtMarked = true;
    }
    private Fx() {}
}
