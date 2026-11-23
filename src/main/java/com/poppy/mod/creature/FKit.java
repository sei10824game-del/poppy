package com.poppy.mod.creature;

import com.poppy.mod.entity.Fx;
import com.poppy.mod.entity.PoppyBolt;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

/** Batch F helpers: model shorthands + server effects for custom attacks. */
final class FKit {
    static final float P = (float) Math.PI;

    // ---------- model ----------
    /** n small boxes evenly spaced along x (teeth, stitches, rivets). dir -1 hangs down, +1 points up. */
    static void row(CreatureDef d, String prefix, String parent, int n, float x0, float x1, float y, float z, float w, float h, float dz, float dir, int c) {
        for (int i = 0; i < n; i++) { float x = n == 1 ? x0 : x0 + (x1 - x0) * i / (n - 1); d.part(prefix + i, parent, x, y, z, w, h, dz, 0, dir * h / 2, 0, c); }
    }

    // ---------- server ----------
    static ServerLevel lv(PoppyMob m) { return m.level() instanceof ServerLevel l ? l : null; }
    static DustParticleOptions dust(int rgb, float size) { return new DustParticleOptions(new Vector3f(((rgb >> 16) & 255) / 255f, ((rgb >> 8) & 255) / 255f, (rgb & 255) / 255f), size); }
    static void puff(PoppyMob m, Vec3 at, ParticleOptions pt, int n, double spread, double speed) { ServerLevel l = lv(m); if (l != null) l.sendParticles(pt, at.x, at.y, at.z, n, spread, spread * 0.5, spread, speed); }
    static void ring(PoppyMob m, double r, ParticleOptions pt) {
        ServerLevel l = lv(m); if (l == null) return;
        for (int i = 0; i < 32; i++) { double a = i / 32.0 * Math.PI * 2; l.sendParticles(pt, m.getX() + Math.cos(a) * r, m.getY() + 0.2, m.getZ() + Math.sin(a) * r, 0, Math.cos(a), 0.05, Math.sin(a), 0.15); }
    }
    static Vec3 front(PoppyMob m, double dist, double up) { return m.position().add(Fx.flat(m).scale(dist)).add(0, up, 0); }

    /** Lingering cloud of the given dust colour with effects. */
    static void cloud(PoppyMob m, Vec3 at, int rgb, float radius, int dur, MobEffectInstance... fx) {
        if (lv(m) == null) return;
        AreaEffectCloud c = new AreaEffectCloud(m.level(), at.x, at.y, at.z);
        c.setOwner(m); c.setRadius(radius); c.setDuration(dur); c.setRadiusPerTick(-radius / dur * 0.5f); c.setParticle(dust(rgb, 1.8f));
        for (MobEffectInstance f : fx) c.addEffect(f);
        m.level().addFreshEntity(c);
    }

    /** Fan of projectiles toward the target. */
    static void fan(PoppyMob m, float dmg, int count, float spread, int color, String style) {
        LivingEntity tg = m.getTarget(); if (tg == null || lv(m) == null) return;
        Vec3 o = m.getEyePosition(); Vec3 d = tg.getEyePosition().subtract(o).normalize();
        for (int i = 0; i < count; i++) {
            Vec3 v = d.yRot((i - (count - 1) / 2f) * spread);
            PoppyBolt b = new PoppyBolt(m.level(), m, dmg, color, style); b.setPos(o.x, o.y - 0.2, o.z); b.shoot(v.x, v.y, v.z, 1.3f, 2f); m.level().addFreshEntity(b);
        }
    }

    /** Hit every foe within r of the mob (all round) once at the hit tick. */
    static void burst(PoppyMob m, AttackSpec s, double r, float kb, Consumer<LivingEntity> extra) {
        for (LivingEntity e : Fx.foes(m, m.position(), r)) { Fx.hit(m, e, s.dmg(), kb, e.position().subtract(m.position())); if (extra != null) extra.accept(e); }
    }

    /**
     * Seize the first foe in front at the hit tick and hold it in front of the mob for the whole strike phase
     * (dmgFrac of spec dmg every `every` ticks, `each` per squeeze), then fling it forward. Target ids are kept in m.hitOnce.
     */
    static void hold(PoppyMob m, AttackSpec s, int t, double reach, int every, float frac, double fling, Consumer<LivingEntity> each) {
        int hit = s.wind() + 1, end = s.wind() + s.strike();
        Vec3 look = Fx.flat(m);
        if (t == hit) Fx.arc(m, reach, 0.3).stream().findFirst().ifPresent(e -> m.hitOnce.add(e.getId()));
        if (t < hit || t > end) return;
        for (int id : List.copyOf(m.hitOnce)) {
            if (!(m.level().getEntity(id) instanceof LivingEntity g) || !g.isAlive()) continue;
            if (t == end) { g.setDeltaMovement(look.x * fling, 0.5, look.z * fling); g.hurtMarked = true; continue; }
            Vec3 anchor = m.position().add(look.scale(m.getBbWidth() * 0.5 + 0.6)).add(0, m.getBbHeight() * 0.3, 0);
            g.setDeltaMovement(anchor.subtract(g.position()).scale(0.5)); g.hurtMarked = true; g.fallDistance = 0;
            if ((t - hit) % every == 0) { Fx.hit(m, g, s.dmg() * frac, 0, Vec3.ZERO); if (each != null) each.accept(g); }
        }
    }

    private FKit() {}
}
