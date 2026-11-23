package com.poppy.mod.test;

import com.poppy.mod.Poppy;
import com.poppy.mod.creature.Creatures;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;

/** Smoke test: every creature fights a zombie for 10s; its attacks must all run without exception. */
@net.minecraftforge.gametest.PrefixGameTestTemplate(false)
@GameTestHolder(Poppy.ID)
public class PoppyTests {
    @GameTest(template = "empty", timeoutTicks = 400, batch = "poppy")
    public static void allAttacksRun(GameTestHelper h) {
        for (int x = 0; x < 24; x++) for (int z = 0; z < 24; z++) h.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
        int[] i = {0};
        for (var d : Creatures.ALL) {
            var m = Poppy.TYPES.get(d.id).get().create(h.getLevel());
            var p = h.absolutePos(new BlockPos(6 + (i[0] % 4) * 4, 1, 4 + (i[0] / 4 % 4) * 4)); i[0]++;
            m.moveTo(p.getX() + .5, p.getY(), p.getZ() + .5); h.getLevel().addFreshEntity(m);
            var z = EntityType.ZOMBIE.create(h.getLevel()); z.moveTo(p.getX() + 2.5, p.getY(), p.getZ() + .5); z.setNoAi(true); z.setHealth(1000); h.getLevel().addFreshEntity(z); m.setTarget(z);
            // force every attack once
            for (int a = 0; a < d.attacks.size(); a++) { final int ai = a; h.runAfterDelay(20 + a * 60L, () -> { if (m.isAlive() && !m.attacking()) { m.setTarget(z); m.startAttack(ai); } }); }
        }
        h.runAfterDelay(380, h::succeed);
    }
}
