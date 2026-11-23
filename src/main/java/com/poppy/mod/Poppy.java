package com.poppy.mod;

import com.poppy.mod.client.BoltRenderer;
import com.poppy.mod.client.PoppyRenderer;
import com.poppy.mod.creature.CreatureDef;
import com.poppy.mod.creature.Creatures;
import com.poppy.mod.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod(Poppy.ID)
public class Poppy {
    public static final String ID = "poppyplaytime";
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final Map<String, RegistryObject<EntityType<PoppyMob>>> TYPES = new LinkedHashMap<>();
    public static final RegistryObject<EntityType<PoppyBolt>> BOLT = ENTITIES.register("bolt", () -> EntityType.Builder.<PoppyBolt>of(PoppyBolt::new, MobCategory.MISC).sized(0.4f, 0.4f).clientTrackingRange(8).updateInterval(2).build(ID + ":bolt"));

    static {
        for (CreatureDef d : Creatures.ALL) {
            var t = ENTITIES.register(d.id, () -> EntityType.Builder.<PoppyMob>of((ty, l) -> new PoppyMob(ty, l, d), MobCategory.MONSTER).sized(d.w, d.h).clientTrackingRange(10).fireImmune().build(ID + ":" + d.id));
            TYPES.put(d.id, t);
            ITEMS.register(d.id + "_spawn_egg", () -> new ForgeSpawnEggItem(t, d.eggA, d.eggB, new Item.Properties()) {
                @Override public Component getName(ItemStack s) { return Component.literal(d.name + " Spawn Egg"); }
            });
        }
        TABS.register("poppy", () -> CreativeModeTab.builder().title(Component.literal("Poppy Play Time")).icon(() -> new ItemStack(Items.RED_DYE))
                .displayItems((p, o) -> { for (var d : Creatures.ALL) o.accept(ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation(ID, d.id + "_spawn_egg"))); }).build());
    }

    public Poppy() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus); ITEMS.register(bus); TABS.register(bus);
        bus.addListener(Poppy::attrs); bus.addListener(Poppy::placements); bus.addListener(PoppyData::gather);
        MinecraftForge.EVENT_BUS.register(Poppy.class);
    }

    static void attrs(EntityAttributeCreationEvent e) { for (CreatureDef d : Creatures.ALL) e.put(TYPES.get(d.id).get(), PoppyMob.attrs(d).build()); }
    static void placements(SpawnPlacementRegisterEvent e) {
        for (CreatureDef d : Creatures.ALL) e.register(TYPES.get(d.id).get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (t, lv, r, pos, rnd) -> PoppyMob.checkMonsterSpawnRules(t, lv, r, pos, rnd), SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    /** Every non-Poppy mob fights (or flees from) Poppy creatures. */
    @SubscribeEvent
    public static void join(EntityJoinLevelEvent e) {
        if (e.getLevel().isClientSide || !(e.getEntity() instanceof Mob m) || m instanceof PoppyMob) return;
        if (m instanceof Enemy || (m instanceof PathfinderMob && m.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))) m.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(m, PoppyMob.class, true));
        else if (m instanceof PathfinderMob p) p.goalSelector.addGoal(1, new AvoidEntityGoal<>(p, PoppyMob.class, 14f, 1.1, 1.5));
    }

    @Mod.EventBusSubscriber(modid = ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
            for (var t : TYPES.values()) e.registerEntityRenderer(t.get(), PoppyRenderer::new);
            e.registerEntityRenderer(BOLT.get(), BoltRenderer::new);
        }
    }
}
