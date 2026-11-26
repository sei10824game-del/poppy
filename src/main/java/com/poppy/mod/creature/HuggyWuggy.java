package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;

final class HuggyWuggy {
    static CreatureDef def() {
        int blue = 0x2f6fe0, dark = 0x1c3f94, red = 0xd8262b, mouth = 0x16050a, teeth = 0xf2efe4, yellow = 0xf4d12a;
        float P = (float) Math.PI;
        return CreatureDef.of("huggy_wuggy", "Huggy Wuggy", "ハギーワギー").size(1.1f, 4.9f).hp(140).dmg(8).speed(0.34).kbRes(0.6).egg(blue, red).spawn(1)
                // legs: long thin fuzzy stilts with yellow feet
                .part("legL", null, -4, 38, 0, 3.6f, 38, 3.6f, 0, -19, 0, dark).walk(0.9f, 0)
                .part("kneeFuzzL", "legL", 0, -19, 0, 4.8f, 2.4f, 4.8f, 0, 0, 0, blue)
                .part("footL", "legL", 0, -38, 0, 6, 3, 9, 0, 1.5f, -1.5f, yellow)
                .part("legR", null, 4, 38, 0, 3.6f, 38, 3.6f, 0, -19, 0, dark).walk(0.9f, P)
                .part("kneeFuzzR", "legR", 0, -19, 0, 4.8f, 2.4f, 4.8f, 0, 0, 0, blue)
                .part("footR", "legR", 0, -38, 0, 6, 3, 9, 0, 1.5f, -1.5f, yellow)
                // torso: narrow, slightly hunched, shaggy
                .part("body", null, 0, 38, 0, 10, 16, 7, 0, 8, 0, blue).sway(0.05f, 0.12f, 0, 0)
                .part("belly", "body", 0, 2, -4.2f, 8, 9, 1, 0, 0, 0, dark)
                .part("chestFuzz", "body", 0, 14, -4.4f, 11, 3, 1.4f, 0, 0, 0, blue)
                .part("hipFuzz", "body", 0, 0, 0, 13.4f, 2.4f, 9.4f, 0, 0, 0, blue)
                .part("bowL", "body", -2, 16, -4.4f, 3.6f, 2.6f, 1.2f, 0, 0, 0, dark)
                .part("bowR", "body", 2, 16, -4.4f, 3.6f, 2.6f, 1.2f, 0, 0, 0, dark)
                .part("bowK", "body", 0, 16, -4.6f, 1.6f, 1.6f, 1.2f, 0, 0, 0, dark)
                .part("bowTailL", "body", -1, 15, -4.6f, 1.2f, 4, 0.8f, 0, -2, 0, dark)
                .part("bowTailR", "body", 1, 15, -4.6f, 1.2f, 4, 0.8f, 0, -2, 0, dark)
                .part("head", "body", 0, 16, -0.5f, 14, 13, 11, 0, 6.5f, 0, blue).sway(0.06f, 0.1f, 1.3f, 2)
                // swept-back wild hair
                .part("hair0", "head", -5, 12, 1, 3, 7, 4, 0, 3, 1, blue).sway(0.06f, 0.2f, 0, 2)
                .part("hair1", "head", -2.5f, 12, 2, 3, 9, 4, 0, 4, 1, dark).sway(0.06f, 0.2f, 1, 2)
                .part("hair2", "head", 0.5f, 12, 2, 3.4f, 10, 4, 0, 5, 1.5f, blue).sway(0.06f, 0.2f, 2, 2)
                .part("hair3", "head", 3, 12, 2, 3, 8, 4, 0, 4, 1, dark).sway(0.06f, 0.2f, 3, 2)
                .part("hair4", "head", 5.4f, 12, 1, 3, 6, 4, 0, 3, 1, blue).sway(0.06f, 0.2f, 4, 2)
                .part("hairBack", "head", 0, 10, 4.6f, 12, 8, 3, 0, 0, 0, dark)
                .part("cheekFuzzL", "head", -7.4f, 3, -1, 2.4f, 6, 8, 0, 0, 0, blue)
                .part("cheekFuzzR", "head", 7.4f, 3, -1, 2.4f, 6, 8, 0, 0, 0, blue)
                // face: small black eyes with glints, little nose, huge toothy red-lipped grin
                .part("eyeL", "head", -3.6f, 8.6f, -5.6f, 3, 3.4f, 0.8f, 0, 0, 0, 0x0a0a0e)
                .part("eyeR", "head", 3.6f, 8.6f, -5.6f, 3, 3.4f, 0.8f, 0, 0, 0, 0x0a0a0e)
                .part("glintL", "head", -3.2f, 9.6f, -6.1f, 1, 1, 0.4f, 0, 0, 0, 0xffffff).glow()
                .part("glintR", "head", 4, 9.6f, -6.1f, 1, 1, 0.4f, 0, 0, 0, 0xffffff).glow()
                .part("noseT", "head", 0, 6.4f, -5.8f, 2.4f, 1.8f, 1, 0, 0, 0, dark)
                .part("jaw", "head", 0, 3.4f, -5.6f, 8, 2f, 1f, 0, -1f, 0, mouth)
                .part("decalFace", "head", 0, 0, -6.4f, 14, 13, 0.05f, 0, 6.5f, 0, 0xffffff).decal("huggy")
                // very long arms, yellow clawed hands hanging near the knees
                .part("armL", "body", -9.4f, 14, 0, 3.2f, 22, 3.2f, 0, -11, 0, blue).sway(0.07f, 0.1f, 0, 2)
                .part("shoulderFuzzL", "armL", 0, 0, 0, 4.4f, 3.4f, 4.4f, 0, 0, 0, blue)
                .part("foreL", "armL", 0, -22, 0, 2.8f, 18, 2.8f, 0, -9, 0, dark).sway(0.08f, 0.1f, 0.6f, 0)
                .part("handL", "foreL", 0, -18, 0, 5, 6, 4, 0, -2, 0, yellow)
                .part("clawL0", "handL", -1.6f, -5, 0, 1.2f, 3, 1.2f, 0, -1.5f, 0, yellow)
                .part("clawL1", "handL", 0, -5, 0, 1.2f, 3.4f, 1.2f, 0, -1.7f, 0, yellow)
                .part("clawL2", "handL", 1.6f, -5, 0, 1.2f, 3, 1.2f, 0, -1.5f, 0, yellow)
                .part("armR", "body", 9.4f, 14, 0, 3.2f, 22, 3.2f, 0, -11, 0, blue).sway(0.07f, 0.1f, P, 2)
                .part("shoulderFuzzR", "armR", 0, 0, 0, 4.4f, 3.4f, 4.4f, 0, 0, 0, blue)
                .part("foreR", "armR", 0, -22, 0, 2.8f, 18, 2.8f, 0, -9, 0, dark).sway(0.08f, 0.1f, P + 0.6f, 0)
                .part("handR", "foreR", 0, -18, 0, 5, 6, 4, 0, -2, 0, yellow)
                .part("clawR0", "handR", -1.6f, -5, 0, 1.2f, 3, 1.2f, 0, -1.5f, 0, yellow)
                .part("clawR1", "handR", 0, -5, 0, 1.2f, 3.4f, 1.2f, 0, -1.7f, 0, yellow)
                .part("clawR2", "handR", 1.6f, -5, 0, 1.2f, 3, 1.2f, 0, -1.5f, 0, yellow)
                .atk(Atk.SWIPE, 8, 4.2f, 0, 30)
                .atk(Atk.GRAB, 7, 3.2f, 0, 160, 50, "")
                .atk(Atk.CHARGE, 9, 14f, 5f, 200)
                .atk(Atk.SCREAM, 3, 9f, 3f, 260);
    }
}
