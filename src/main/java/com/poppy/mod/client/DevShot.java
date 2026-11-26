package com.poppy.mod.client;

import com.poppy.mod.Poppy;
import com.poppy.mod.entity.PoppyMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Dev-only visual check (-Dpoppy.shot=id,id,... [-Dpoppy.yaw=180]): lines the creatures up in front of the player, takes one screenshot, quits. */
public class DevShot {
    private boolean spawned;
    private final java.util.List<PoppyMob> mobs = new java.util.ArrayList<>();
    private int ticks;

    @SubscribeEvent public void server(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.END && e.player instanceof ServerPlayer q && spawned && System.getProperty("poppy.atk") != null) {
            String[] a = System.getProperty("poppy.atk").split(":");
            if (q.tickCount == 130 - Integer.parseInt(a[1])) for (PoppyMob m : mobs) if (m.def.attacks.size() > Integer.parseInt(a[0])) m.startAttack(Integer.parseInt(a[0]));
        }
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p) || spawned || p.tickCount < 40) return;
        spawned = true;
        var lv = p.serverLevel();
        lv.setDayTime(6000); lv.setWeatherParameters(6000, 0, false, false);
        p.getAbilities().invulnerable = true; p.getAbilities().mayfly = true; p.onUpdateAbilities();
        String[] ids = System.getProperty("poppy.shot").split(",");
        float yaw = Float.parseFloat(System.getProperty("poppy.yaw", "180"));
        int bx = (int) p.getX(), bz = (int) p.getZ(), by = 220;
        for (int dx = -40; dx <= 40; dx++) for (int dz = -8; dz <= 30; dz++) lv.setBlock(new net.minecraft.core.BlockPos(bx + dx, by, bz + dz), net.minecraft.world.level.block.Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState(), 2);
        double gap = Double.parseDouble(System.getProperty("poppy.gap", "4")), x0 = bx + 0.5 - (ids.length - 1) * gap / 2, z = bz + 0.5 + Double.parseDouble(System.getProperty("poppy.dist", "9"));
        for (int i = 0; i < ids.length; i++) {
            var t = Poppy.TYPES.get(ids[i]);
            if (t == null) continue;
            PoppyMob m = t.get().create(lv);
            double x = x0 + i * gap;
            m.moveTo(x, by + 1, z, yaw, 0);
            m.setNoAi(true); m.setYHeadRot(yaw); m.setYBodyRot(yaw);
            lv.addFreshEntity(m); mobs.add(m);
        }
        p.teleportTo(bx + 0.5, by + 1, bz + 0.5);
        p.setYRot(0); p.setXRot(Float.parseFloat(System.getProperty("poppy.pitch", "3")));
    }

    @SubscribeEvent public void client(TickEvent.ClientTickEvent e) {
        var mc = Minecraft.getInstance();
        if (e.phase != TickEvent.Phase.END || mc.player == null) return;
        ticks++;
        if (ticks == 60) mc.options.hideGui = true;
        if (ticks == 130) Screenshot.grab(mc.gameDirectory, System.getProperty("poppy.out", "poppy_shot.png"), mc.getMainRenderTarget(), m -> { });
        if (ticks == 150) mc.stop();
    }
}
