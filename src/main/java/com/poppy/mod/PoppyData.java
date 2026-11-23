package com.poppy.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.poppy.mod.creature.CreatureDef;
import com.poppy.mod.creature.Creatures;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Datagen (gradlew runData): spawn-egg item models + biome spawn modifiers from CreatureDef. */
public final class PoppyData {
    public static void gather(GatherDataEvent e) {
        PackOutput out = e.getGenerator().getPackOutput();
        e.getGenerator().addProvider(true, (DataProvider) new DataProvider() {
            @Override public CompletableFuture<?> run(CachedOutput c) {
                List<CompletableFuture<?>> f = new ArrayList<>();
                Path root = out.getOutputFolder();
                for (CreatureDef d : Creatures.ALL) {
                    JsonObject m = new JsonObject(); m.addProperty("parent", "minecraft:item/template_spawn_egg");
                    f.add(DataProvider.saveStable(c, m, root.resolve("assets/" + Poppy.ID + "/models/item/" + d.id + "_spawn_egg.json")));
                    if (d.spawnWeight > 0) {
                        JsonObject b = new JsonObject(); b.addProperty("type", "forge:add_spawns"); b.addProperty("biomes", "#minecraft:is_overworld");
                        JsonObject s = new JsonObject(); s.addProperty("type", Poppy.ID + ":" + d.id); s.addProperty("weight", d.spawnWeight); s.addProperty("minCount", 1); s.addProperty("maxCount", 1);
                        b.add("spawners", s);
                        f.add(DataProvider.saveStable(c, b, root.resolve("data/" + Poppy.ID + "/forge/biome_modifier/spawn_" + d.id + ".json")));
                    }
                }
                return CompletableFuture.allOf(f.toArray(new CompletableFuture[0]));
            }
            @Override public String getName() { return "Poppy resources"; }
        });
    }
}
