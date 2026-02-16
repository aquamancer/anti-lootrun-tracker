package com.aquamancer.antilootruntracker;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class MobProximityList {
    private final Map<Integer, List<MobEntity>> mobsByDistance = new HashMap<>();

    public MobProximityList(Stream<MobEntity> mobs, Vec3d distanceFrom) {
        mobs.forEach(mob -> {
            mobsByDistance.compute((int) Math.sqrt(mob.squaredDistanceTo(distanceFrom)),
                    (k, v) -> {
                        if (v == null) {
                            return new ArrayList<>(List.of(mob));
                        } else {
                            v.add(mob);
                            return v;
                        }
                    });
        });
        for (Map.Entry<Integer, List<MobEntity>> distanceGroup : this.mobsByDistance.entrySet()) {
            distanceGroup.getValue().sort(Comparator.comparing(mob -> mob.getName().toString()));
        }
    }

    @Nullable
    public Text toText() {
        if (this.mobsByDistance.isEmpty()) {
            if (AntiLootrunTracker.config.displayFreeChestMessage() && !AntiLootrunTracker.config.getFreeChestMessage().isBlank()) {
                return Text.literal(AntiLootrunTracker.config.getFreeChestMessage()).formatted(Formatting.GREEN);
            } else {
                return null;
            }
        }

        MutableText result = Text.empty();
        int maxChars = AntiLootrunTracker.config.getMobListMaxChars();
        int resultLength = 0;
        boolean withinCharLimit = true;
        for (Map.Entry<Integer, List<MobEntity>> distanceGroup : mobsByDistance.entrySet().stream().sorted(Comparator.comparingInt(pair -> pair.getKey())).toList()) {
            Integer distance = distanceGroup.getKey();
            List<MobEntity> mobs = distanceGroup.getValue();
            result.append(Text.literal(String.valueOf(distance)).formatted(Formatting.RED, Formatting.BOLD));
            result.append(Text.literal("m").formatted(Formatting.RED));
            result.append(Text.literal(": ").formatted(Formatting.GOLD));
            // temp string for mob names to check if the group will go over the character limit
            MutableText temp = Text.empty();
            int tempLength = 0;
            for (int i = 0; i < mobs.size(); i++) {
                if (!withinCharLimit) {
                    break;
                }

                if (i != 0) {
                    temp.append(", ");
                }
                Text mobName;
                if (mobs.get(i).getCustomName() != null) {
                    mobName = mobs.get(i).getCustomName();
                } else {
                    mobName = mobs.get(i).getDisplayName();
                }
                temp.append(mobName);
                tempLength += mobName.getString().length();
                if (resultLength + tempLength > maxChars) {
                    withinCharLimit = false;
                    break;
                }
            }
            if (withinCharLimit) {
                result.append(temp);
                resultLength += tempLength;
            } else {
                result.append(Text.literal(String.valueOf(mobs.size())));
            }
            result.append("  ");
        }

        return result;
    }

}
