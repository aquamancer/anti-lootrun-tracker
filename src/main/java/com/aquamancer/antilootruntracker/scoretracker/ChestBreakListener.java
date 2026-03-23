package com.aquamancer.antilootruntracker.scoretracker;

import net.minecraft.block.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChestBreakListener {
    record DroppedItem(ItemStack stack, long timeDropped) {}

    static class BrokenChest {
        private static final long TIMEOUT_MILLIS = 10000L;
        private static final long DROP_WINDOW_MILLIS = 125L;

        final String shard;
        final BlockPos pos;
        final BlockState state;
        final boolean isSingleChest;
        private final Box blockBox;
        private final Deque<DroppedItem> itemsDroppedSinceBreak = new ArrayDeque<>();
        final Deque<ItemStack> actualContents = new ArrayDeque<>();

        private final long timeBroken;

        private BrokenChest(String shard, BlockPos pos, BlockState state, boolean isSingleChest) {
            this.shard = shard;
            this.pos = pos;
            this.state = state;
            this.isSingleChest = isSingleChest;
            this.blockBox = new Box(pos);
            this.timeBroken = System.currentTimeMillis();
        }

        private boolean isTimedOut() {
            return System.currentTimeMillis() > timeBroken + TIMEOUT_MILLIS;
        }

        @Override
        public boolean equals(Object o2) {
            return (o2 instanceof BrokenChest c) && this.pos.equals(c.pos);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }
    }

    private static final Set<BrokenChest> brokenChests = new HashSet<>();

    public static void onBlockMined(World world, BlockPos pos, BlockState state, String shard) {
        Block blockMined = world.getBlockState(pos).getBlock();
        if (!(blockMined instanceof ChestBlock)) return;
        BlockPos otherHalf = getOtherHalf(pos, state);
        if (otherHalf != null) {
            LootingTracker.registerDoubleChest(pos, otherHalf);
        }

        BrokenChest chestMined = new BrokenChest(shard, pos, state, otherHalf == null);
        brokenChests.add(chestMined);
    }

    public static void onEntitySpawn(ItemEntity entity, String shard) {
        long currentTime = System.currentTimeMillis();

        ItemStack stack = entity.getStack();
        if (stack.isEmpty()) return;
        Iterator<BrokenChest> iterator = brokenChests.iterator();
        while (iterator.hasNext()) {
            BrokenChest chest = iterator.next();
            if (chest.isTimedOut()) {
                iterator.remove();
                continue;
            }
            if (!chest.shard.equals(shard)) continue;
            if (!chest.blockBox.contains(entity.getPos())) continue;

            if (stack.getItem() == Items.CHEST) {
                // the mined chest itself is always dropped after all of its contents
                DroppedItem tail;
                while ((tail = chest.itemsDroppedSinceBreak.pollLast()) != null) {
                    // only consider items to be from the chest if they are dropped within t millis
                    // before the mined chest itself dropping
                    if (currentTime > tail.timeDropped + BrokenChest.DROP_WINDOW_MILLIS) break;
                    chest.actualContents.offerFirst(tail.stack);
                }
                LootingTracker.onChestBroken(chest);
                iterator.remove();
            } else {
                chest.itemsDroppedSinceBreak.offer(new DroppedItem(stack, currentTime));
            }
            break;
        }
    }

}
