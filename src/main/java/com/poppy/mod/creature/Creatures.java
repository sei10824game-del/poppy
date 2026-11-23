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
    }

    private static void add(CreatureDef d) { ALL.add(d); BY_ID.put(d.id, d); }
    private Creatures() {}
}
