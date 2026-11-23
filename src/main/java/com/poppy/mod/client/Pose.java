package com.poppy.mod.client;

import net.minecraft.util.Mth;
import java.util.HashMap;
import java.util.Map;

/** Per-node transform: [0..2] rot xyz, [3..5] offset px, [6..8] scale. Animators blend towards keyframes. */
public final class Pose {
    public static final float[] Z = {0, 0, 0}, O = {0, 0, 0};
    public final Map<String, float[]> m = new HashMap<>();

    public float[] g(String n) { return m.computeIfAbsent(n, k -> new float[]{0, 0, 0, 0, 0, 0, 1, 1, 1}); }
    public boolean has(String n) { return m.containsKey(n); }
    public void add(String n, float x, float y, float z) { float[] a = g(n); a[0] += x; a[1] += y; a[2] += z; }

    /** Windup/strike/recover keying of rotation: A = windup pose, B = strike pose. w,s,r in 0..1 phase progress. */
    public void rot(String n, float[] A, float[] B, float w, float s, float r) { key(n, 0, A, B, w, s, r); }
    public void pos(String n, float[] A, float[] B, float w, float s, float r) { key(n, 3, A, B, w, s, r); }
    public void scl(String n, float[] A, float[] B, float w, float s, float r) { key(n, 6, A, B, w, s, r); }
    public void rot(String n, float ax, float ay, float az, float bx, float by, float bz, float w, float s, float r) { rot(n, new float[]{ax, ay, az}, new float[]{bx, by, bz}, w, s, r); }
    public void pos(String n, float ax, float ay, float az, float bx, float by, float bz, float w, float s, float r) { pos(n, new float[]{ax, ay, az}, new float[]{bx, by, bz}, w, s, r); }
    public void scl(String n, float ax, float ay, float az, float bx, float by, float bz, float w, float s, float r) { scl(n, new float[]{ax, ay, az}, new float[]{bx, by, bz}, w, s, r); }

    private void key(String n, int o, float[] A, float[] B, float w, float s, float r) {
        float[] a = g(n);
        float f = r > 0 ? 1 - ease(r) : ease(w), sk = ease(s);
        for (int i = 0; i < 3; i++) { float tgt = Mth.lerp(sk, A[i], B[i]); a[o + i] = Mth.lerp(f, a[o + i], tgt); }
    }
    public static float ease(float x) { x = Mth.clamp(x, 0, 1); return x * x * (3 - 2 * x); }
}
