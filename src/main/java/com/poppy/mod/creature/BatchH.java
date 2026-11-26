package com.poppy.mod.creature;

import com.poppy.mod.creature.Canon.B;
import java.util.ArrayList;
import java.util.List;

/** Batch H – extra canon villains not covered earlier (Pianosaurus). */
public final class BatchH {
    public static List<CreatureDef> all() {
        List<CreatureDef> l = new ArrayList<>();
        l.add(pianosaurus());
        return l;
    }

    /** Green theropod with a piano-key underside, three coloured horns (red/orange/yellow), long spiked neck and tail. */
    static CreatureDef pianosaurus() {
        int gr = 0x58b848, dk = 0x3a8a34, pk = 0xf08aa0, wh = 0xf4f1ea, bk = 0x14141a;
        CreatureDef d = CreatureDef.of("pianosaurus", "Pianosaurus", "ピアノサウルス").size(1.8f, 3.6f).hp(260).dmg(10).speed(0.3).kbRes(0.8).armor(3).egg(gr, wh).spawn(1);
        B b = new B(d, d.h * 16f / 62);
        b.pr("leg#", null, 6, 24, 2, 6, 24, 7, 0, -12, 0, gr, 0.7f);
        b.pr("foot#", "leg#", 0, -24, -2, 7, 2.4f, 10, 0, 1.2f, 0, dk);
        b.p("body", null, 0, 24, 0, 16, 13, 22, 0, 6.5f, 0, gr).sw(0.03f, 0.08f, 0, 2);
        for (int i = 0; i < 9; i++) b.p("key" + i, "body", 0, 0, -9 + i * 2.2f, 12, 0.8f, 2, 0, 0, 0, i % 2 == 0 ? wh : bk);
        for (int i = 0; i < 5; i++) b.p("bspike" + i, "body", 0, 13, -6 + i * 4, 2.4f, 3, 2.4f, 0, 1.5f, 0, dk);
        b.p("neck", "body", 0, 11, -9, 7, 26, 7, 0, 13, 0, gr).sw(0.05f, 0.08f, 0, 0);
        for (int i = 0; i < 4; i++) b.p("nspike" + i, "neck", 0, 24 - i * 5.6f, 3.4f, 2, 3, 2, 0, 0, 0, i % 2 == 0 ? 0xe8503a : 0xf2c82a);
        b.p("head", "neck", 0, 25, -2, 9, 8, 13, 0, 4, -4, gr).sw(0.05f, 0.1f, 0, 2);
        b.p("snout", "head", 0, 2, -12, 6, 4.4f, 3, 0, 0, 0, gr);
        b.p("snoutSpike", "head", 0, 5, -13.6f, 1.4f, 2, 1.4f, 0, 0, 0, 0xf2c82a);
        b.p("decalFace", "snout", 0, 0, -1.75f, 6, 4.4f, 0.05f, 0, 0, 0, 0xffffff); d.decal("piano");
        b.p("hornR", "head", 3, 8, -3, 1.8f, 5, 1.8f, 0, 2.5f, 0, 0xe8503a);
        b.p("hornM", "head", 0, 8, -3, 1.8f, 6, 1.8f, 0, 3, 0, 0xf08a30);
        b.p("hornL", "head", -3, 8, -3, 1.8f, 5, 1.8f, 0, 2.5f, 0, 0xf2c82a);
        b.pr("eye#", "head", 4.6f, 6, -6, 1.4f, 2, 2, 0, 0, 0, bk).g();
        b.pr("blush#", "head", 3.4f, 2.4f, -12.4f, 0.8f, 2.2f, 2.2f, 0, 0, 0, pk);
        b.p("jaw", "head", 0, 1, -12, 5.6f, 1.2f, 3, 0, 0, 0, 0x3a0a14);
        b.pr("arm#", "body", 8.6f, 9, -6, 2, 5, 2, 0, -2.5f, 0, gr);
        b.pr("hand#", "arm#", 0, -5, 0, 2.6f, 1.6f, 2.6f, 0, 0, 0, dk);
        b.p("tail", "body", 0, 8, 11, 8, 7, 22, 0, 0, 11, gr).sw(0.1f, 0.07f, 0, 1);
        b.p("tail2", "tail", 0, 0, 22, 5, 5, 18, 0, 0, 9, gr);
        for (int i = 0; i < 4; i++) b.p("tkey" + i, "tail", 0, -3.4f, 2 + i * 3.4f, 4, 0.7f, 2, 0, 0, 0, i % 2 == 0 ? wh : bk);
        for (int i = 0; i < 4; i++) b.p("tspike" + i, "tail", 0, 3.5f, 2 + i * 3.8f, 2, 2.4f, 2, 0, 0, 0, dk);
        return d.atk(com.poppy.mod.entity.Atk.BITE, 12, 4.4f, 0, 50);
    }
    private BatchH() {}
}
