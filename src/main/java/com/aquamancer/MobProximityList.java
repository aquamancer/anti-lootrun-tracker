package com.aquamancer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MobProximityList {
    private final Map<Integer, List<MobEntity>> mobsByDistance = new HashMap<>();

    public MobProximityList(Iterable<MobEntity> mobs, Vec3d distanceFrom) {
        for (MobEntity mob : mobs) {
            mobsByDistance.compute((int) Math.sqrt(mob.squaredDistanceTo(distanceFrom)),
                    (k, v) -> {
                        if (v == null) {
                            return new ArrayList<>(List.of(mob));
                        } else {
                            v.add(mob);
                            return v;
                        }
                    }
            );
        }
        for (Map.Entry<Integer, List<MobEntity>> distanceGroup : this.mobsByDistance.entrySet()) {
            distanceGroup.getValue().sort(Comparator.comparing(mob -> mob.getName().toString()));
        }
    }

    @Nullable
    public Text toText() {
        if (this.mobsByDistance.isEmpty()) {
            return null;
        }

        MutableText result = Text.empty();
        for (Map.Entry<Integer, List<MobEntity>> distanceGroup : mobsByDistance.entrySet().stream().sorted(Comparator.comparingInt(pair -> pair.getKey())).toList()) {
            Integer distance = distanceGroup.getKey();
            List<MobEntity> mobs = distanceGroup.getValue();
            result.append(Text.literal("  " + distance + "m (" + mobs.size() + "): ").formatted(Formatting.RED, Formatting.BOLD));
            for (int i = 0; i < mobs.size(); i++) {
                if (i != 0) {
                    result.append(", ");
                }
                String mobName = EntityType.getId(mobs.get(i).getType()).getPath();
                result.append(mobName);
            }
        }

        return result;
    }

}
