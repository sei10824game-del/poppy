package com.poppy.mod.entity;

import com.poppy.mod.Poppy;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

/** Generic coloured projectile (spit, ink, goo, sonic...). style: "" plain, "slow", "poison", "blind", "fire". */
public class PoppyBolt extends ThrowableProjectile {
    public static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(PoppyBolt.class, EntityDataSerializers.INT);
    float dmg = 4; String style = "";

    public PoppyBolt(EntityType<? extends PoppyBolt> t, Level l) { super(t, l); }
    public PoppyBolt(Level l, LivingEntity owner, float dmg, int color, String style) {
        super(Poppy.BOLT.get(), owner, l); this.dmg = dmg; this.style = style; entityData.set(COLOR, color);
    }
    @Override protected void defineSynchedData() { entityData.define(COLOR, 0xff3030); }
    public int color() { return entityData.get(COLOR); }
    @Override protected float getGravity() { return 0.01f; }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

    @Override public void tick() {
        super.tick();
        if (level().isClientSide) { int c = color(); level().addParticle(new DustParticleOptions(new Vector3f((c >> 16 & 255) / 255f, (c >> 8 & 255) / 255f, (c & 255) / 255f), 1.2f), getX(), getY(), getZ(), 0, 0, 0); }
        else if (tickCount > 100) discard();
    }
    @Override protected void onHit(HitResult r) {
        super.onHit(r);
        if (!level().isClientSide) discard();
    }
    @Override protected void onHitEntity(EntityHitResult r) {
        if (!(r.getEntity() instanceof LivingEntity e) || e instanceof PoppyMob) return;
        e.hurt(damageSources().indirectMagic(this, getOwner()), dmg);
        switch (style) {
            case "slow" -> e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            case "poison" -> e.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            case "blind" -> e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
            case "fire" -> e.setSecondsOnFire(4);
            default -> {}
        }
    }
    @Override public void addAdditionalSaveData(CompoundTag t) { super.addAdditionalSaveData(t); t.putInt("c", color()); }
    @Override public void readAdditionalSaveData(CompoundTag t) { super.readAdditionalSaveData(t); entityData.set(COLOR, t.getInt("c")); }
}
