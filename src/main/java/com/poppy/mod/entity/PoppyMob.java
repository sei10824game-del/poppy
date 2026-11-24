package com.poppy.mod.entity;

import com.poppy.mod.creature.AttackSpec;
import com.poppy.mod.creature.CreatureDef;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

/** The one entity class behind every Poppy Playtime creature; behaviour comes from CreatureDef. */
public class PoppyMob extends Monster {
    public static final EntityDataAccessor<Integer> ATK = SynchedEntityData.defineId(PoppyMob.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> ATK_T = SynchedEntityData.defineId(PoppyMob.class, EntityDataSerializers.INT);

    public final CreatureDef def;
    public final int[] cd;
    public LivingEntity grabbed;
    public int holdTicks, atkT, gcd;
    public final Set<Integer> hitOnce = new HashSet<>();
    private final ServerBossEvent bossBar;

    public PoppyMob(EntityType<? extends PoppyMob> t, Level l, CreatureDef def) {
        super(t, l);
        this.def = def; this.cd = new int[def.attacks.size()]; this.xpReward = (int) (def.hp / 4);
        bossBar = def.boss ? new ServerBossEvent(Component.literal(def.name), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10) : null;
        if (def.boss) setPersistenceRequired();
    }

    public static AttributeSupplier.Builder attrs(CreatureDef d) {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, d.hp).add(Attributes.MOVEMENT_SPEED, d.speed).add(Attributes.ATTACK_DAMAGE, d.dmg)
                .add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, d.kbRes).add(Attributes.ARMOR, d.armor);
    }

    @Override protected void defineSynchedData() { super.defineSynchedData(); entityData.define(ATK, -1); entityData.define(ATK_T, 0); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PoppyAttackGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12f));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, PoppyMob.class));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, e -> Fx.isFoe(this, e)));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, e -> Fx.isFoe(this, e)));
    }

    @Override public Component getName() { return Component.literal(def.name); }
    @Override public boolean fireImmune() { return def.fireImmune || super.fireImmune(); }
    @Override public boolean removeWhenFarAway(double d) { return !def.boss && super.removeWhenFarAway(d); }

    public int atk() { return entityData.get(ATK); }
    public int atkTick() { return entityData.get(ATK_T); }
    public boolean attacking() { return atk() >= 0; }

    public void startAttack(int i) {
        entityData.set(ATK, i); atkT = 0; entityData.set(ATK_T, 0); hitOnce.clear(); holdTicks = 0; grabbed = null;
        AttackSpec s = def.attacks.get(i); cd[i] = s.cd(); s.kind().tick(this, s, 0);
    }
    public void endAttack() { entityData.set(ATK, -1); entityData.set(ATK_T, 0); grabbed = null; holdTicks = 0; gcd = 12; }

    /** Switch hostility: if a nearer foe (or whoever is hitting us) threatens us while the current target is far, turn on them. */
    private void retarget() {
        if (attacking() || tickCount % 10 != 0) return;
        LivingEntity cur = getTarget(), best = null;
        double curD = cur == null || !cur.isAlive() ? Double.MAX_VALUE : distanceTo(cur);
        LivingEntity hurt = getLastHurtByMob();
        if (hurt != null && hurt.isAlive() && tickCount - getLastHurtByMobTimestamp() < 100 && Fx.isFoe(this, hurt) && distanceTo(hurt) < 24 && hurt != cur && (curD > 6 || distanceTo(hurt) < curD)) best = hurt;
        if (best == null && curD > 8)
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(6), e -> e != this && e.isAlive() && Fx.isFoe(this, e) && hasLineOfSight(e)))
                if (distanceTo(e) + 3 < curD && (best == null || distanceTo(e) < distanceTo(best))) best = e;
        if (best != null) setTarget(best);
    }

    @Override public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) retarget();
        if (level().isClientSide) return;
        for (int i = 0; i < cd.length; i++) if (cd[i] > 0) cd[i]--;
        if (gcd > 0) gcd--;
        int a = atk();
        if (a >= 0) {
            AttackSpec s = def.attacks.get(a);
            atkT++; s.kind().tick(this, s, atkT); entityData.set(ATK_T, grabbed != null ? s.wind() + s.strike() : atkT);
            if (atkT >= s.total() && grabbed == null) endAttack();
        }
        if (bossBar != null) bossBar.setProgress(getHealth() / getMaxHealth());
    }
    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); if (bossBar != null) bossBar.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); if (bossBar != null) bossBar.removePlayer(p); }
    @Override public void die(DamageSource s) { super.die(s); endAttack(); }
}
