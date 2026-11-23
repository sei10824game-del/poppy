package com.poppy.mod.creature;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registry of every creature. Add a new class in this package with `static CreatureDef def()` and list it in ALL. */
public final class Creatures {
    public static final List<CreatureDef> ALL = new ArrayList<>();
    public static final Map<String, CreatureDef> BY_ID = new LinkedHashMap<>();

    static {
        add(HuggyWuggy.def());
        for (var l : List.of(BatchA.all(), BatchB.all(), BatchC.all(), BatchD.all(), BatchE.all(), BatchF.all(), BatchG.all(), BatchH.all())) l.forEach(Creatures::add);
    }

    private static void add(CreatureDef d) { Canon.apply(d); ALL.add(d); BY_ID.put(d.id, d); }
    private Creatures() {}
}
