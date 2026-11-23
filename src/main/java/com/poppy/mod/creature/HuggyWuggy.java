package com.poppy.mod.creature;

import com.poppy.mod.entity.Atk;

final class HuggyWuggy {
    static CreatureDef def() {
        int blue = 0x2f6fe0, dark = 0x1c3f94, red = 0xd8262b, mouth = 0x16050a, teeth = 0xf2efe4, yellow = 0xf4d12a;
        float P = (float) Math.PI;
        return CreatureDef.of("huggy_wuggy", "Huggy Wuggy", "ハギーワギー").size(1.3f, 3.7f).hp(140).dmg(8).speed(0.34).kbRes(0.6).egg(blue, red).spawn(1)
                .part("body", null, 0, 28, 0, 20, 24, 12, 0, 12, 0, blue).sway(0.02f, 0.12f, 0, 2)
                .part("belly", "body", 0, 10, -6.2f, 12, 10, 1, 0, 0, 0, dark)
                .part("head", "body", 0, 24, 0, 24, 20, 18, 0, 10, 0, blue)
                .part("jaw", "head", 0, 3, -9, 22, 6, 4, 0, -3, -2, mouth)
                .part("upperLip", "head", 0, 5, -9.2f, 22, 4, 1, 0, 0, 0, red)
                .part("toothL", "head", -4, 3.5f, -9.6f, 3, 3.5f, 1, 0, -1.7f, 0, teeth)
                .part("toothR", "head", 4, 3.5f, -9.6f, 3, 3.5f, 1, 0, -1.7f, 0, teeth)
                .part("noseT", "head", 0, 10, -9.5f, 6, 4, 2, 0, 0, 0, dark)
                .part("eyeL", "head", -6, 14, -9.2f, 5, 5, 1, 0, 0, 0, yellow).glow()
                .part("eyeR", "head", 6, 14, -9.2f, 5, 5, 1, 0, 0, 0, yellow).glow()
                .part("earL", "head", -10, 19, 0, 4, 8, 5, 0, 4, 0, dark)
                .part("earR", "head", 10, 19, 0, 4, 8, 5, 0, 4, 0, dark)
                .part("armL", "body", -12, 22, 0, 6, 34, 6, 0, -17, 0, blue).sway(0.04f, 0.1f, 0, 2)
                .part("handL", "armL", 0, -34, 0, 8, 8, 8, 0, -3, 0, yellow)
                .part("armR", "body", 12, 22, 0, 6, 34, 6, 0, -17, 0, blue).sway(0.04f, 0.1f, P, 2)
                .part("handR", "armR", 0, -34, 0, 8, 8, 8, 0, -3, 0, yellow)
                .part("legL", null, -5, 28, 0, 8, 28, 8, 0, -14, 0, dark).walk(0.9f, 0)
                .part("legR", null, 5, 28, 0, 8, 28, 8, 0, -14, 0, dark).walk(0.9f, P)
                .atk(Atk.SWIPE, 8, 4.2f, 0, 30)
                .atk(Atk.GRAB, 7, 3.2f, 0, 160, 50, "")
                .atk(Atk.CHARGE, 9, 14f, 5f, 200)
                .atk(Atk.SCREAM, 3, 9f, 3f, 260);
    }
}
