package com.poppy.mod.creature;

import com.poppy.mod.client.Pose;
import com.poppy.mod.entity.PoppyMob;

/** Client choreography. w/s/r = windup/strike/recover progress 0..1 (clamped); age = ticks+partial. Use Pose.rot/pos/scl keyframes or edit p.g(name) directly. */
public interface AnimHook { void apply(Pose p, PoppyMob e, float w, float s, float r, float age); }
