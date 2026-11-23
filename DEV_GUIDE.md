# Poppy Play Time mod – developer guide (Forge 1.20.1, mod id `poppyplaytime`)

One entity class (`PoppyMob`) serves every creature; a creature is a `CreatureDef` (stats + box-model tree + attack list). No textures: models are trees of coloured boxes (`white.png` tinted per part).

## Add a creature
1. In your batch file `creature/BatchX.java` (`static List<CreatureDef> all()`), build defs with `CreatureDef.of(id, "English name", "日本語名")`. See `HuggyWuggy.java` for the reference pattern.
2. `Creatures.java` already lists every Batch class – do NOT edit shared files (Creatures, Atk, Anim, Pose, PoppyMob, ...). Put helpers inside your own files. If you truly need an engine change, describe it in your final report instead.
3. Run `./gradlew --no-daemon -q compileJava`, then `./gradlew --no-daemon -q runData` (writes `src/generated/resources`, commit it), then `./gradlew --no-daemon -q runGameTestServer` (smoke test: every creature forces each of its attacks; must end `All 1 required tests passed`).

## Model DSL (pixels, 16px = 1 block, entity origin = feet centre, +y up, front = -z, +x = entity's right)
`.part(name, parentName|null, pivotX,pivotY,pivotZ, w,h,d, cubeCentreX,cubeCentreY,cubeCentreZ, 0xRRGGBB)` – pivot is relative to the parent pivot; the box is centred at pivot+offset. Modifiers apply to the LAST part: `.glow()` (fullbright), `.walk(amp, phase)` (walk-cycle swing about x), `.sway(amp, speed, phase, axis)` (idle). Use many small boxes for detail (eyes, teeth, stitches, buttons, fur tufts, horns…) – detail and silhouette matter. Size: `.size(w,h)` = hitbox in blocks (match the model height/16).
Animator names (standard rig): `head, body, armL, armR, legL, legR, jaw, tail`; extra limbs must start with `arm`+`L`/`R` (e.g. `armL2`, `armR_upper`) or `leg`+`L`/`R` to be driven by built-in attack poses; any other node (tentacles, ears, antennae…) can move via `.sway`/`.walk` or your own AnimHook. Rotation: positive rotX swings a hanging limb forward/raises it; body rotX negative = lean forward; rotZ on armR positive = outward; ry positive turns toward -x (entity's left).

## Attacks
Built-in `Atk` kinds (`.atk(kind, dmg, reach, minRange, cooldownTicks[, n, s])`): SWIPE (arc), GRAB (n=hold ticks; lifts + squeezes + throws), POUNCE (leap), SLAM (overhead smash, radius=reach), STOMP, BITE (pull-in), SPIN (n/a), SCREAM (cone debuff), GAS (red cloud; n>0 adds poison), WHIP (long line hit that hooks target; arm stretches), SHOOT (projectile; n=0xRRGGBB colour, s=style ""|slow|poison|blind|fire), CHARGE (dash), CLAP (stun), SUMMON (n=count, s=creature id).
Each built-in has a hand-made windup→strike→recover choreography (`client/Anim.java`). **Do not rely on defaults for signature attacks**: override with `.anim((p,e,w,s,r,age)->{ ... })` right after `.atk(...)`, or create a fully custom attack with `.custom(dmg,reach,minRange,cd, wind,strike,rec, (mob,spec,t)->{server effect}, animHook)`. In hooks use `p.rot(name, A..., B..., w,s,r)` (A = windup pose, B = strike pose; blends back in recovery), `p.pos`, `p.scl`, or edit `p.g(name)` arrays directly ([0..2] rot, [3..5] offset px, [6..8] scale). Server helpers: `entity.Fx` (foes, arc, line, hit), `PoppyBolt`, `AreaEffectCloud`, particles via `ServerLevel.sendParticles`.
Every creature must have ≥1 close-range attack (reach ≤ ~4) and a distinct, recognisable attack set taken from canon (games, wiki, trailers) plus fan/original ideas (e.g. Haggy Waggy's arm sweep). Attack animations must be choreographed to the character (anticipation, follow-through, body weight shift), not just a generic swing.

## Hostility (already automatic)
All `PoppyMob`s attack players and every non-Poppy living mob; every vanilla/modded mob attacks or flees from Poppy mobs. Poppy mobs never fight each other.

## Balance guide
Small toy: hp 20-40 dmg 3-5; mid: hp 60-120 dmg 6-9; boss: hp 300-1000 dmg 10-16 `.boss()`. `.spawn(weight)` for natural overworld spawn (1-3 rare; 0 = eggs/summon only).
