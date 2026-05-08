package com.aquamancer.antilootruntracker.scoretracker;

import com.aquamancer.antilootruntracker.ShardTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LootingTracker {
    enum LootMethod {
        OPENED,
        MINED,
        EXPLODED
    }

    record ChestLoc(String shard, BlockPos pos) {}
    record DoubleChest(String shard, BlockPos left, BlockPos right) {}

    static class LootedChest {
        private final ChestLoc location;
        private final List<ItemStack> contents;

        // used to compare contents count-wise
        private final Map<String, Integer> itemCount;

        LootedChest(String shard, BlockPos pos, List<ItemStack> contents) {
            this.location = new ChestLoc(shard, pos);
            this.contents = contents;
            this.itemCount = toCounts(this.contents);
        }

        LootedChest(ChestLoc location, List<ItemStack> contents) {
            this.location = location;
            this.contents = contents;
            this.itemCount = toCounts(this.contents);
        }

        private static Map<String, Integer> toCounts(List<ItemStack> contents) {
            Map<String, Integer> result = new HashMap<>();

            for (ItemStack stack : contents) {
                result.compute(stack.getName().getString(), (k, v) -> (v == null) ? stack.getCount() : v + stack.getCount());
            }
        }

        private boolean contentsEqual(LootedChest c2) {
            if (c2 == null) return false;
            return contentsEqual(c2.itemCount);
        }

        private boolean contentsEqual(SimpleInventory c2) {
            if (c2 == null) return false;
            return contentsEqual(toCounts(c2.clearToList()));
        }

        private boolean contentsEqual(Map<String, Integer> c2) {
            if (c2 == null) return false;
            if (this.itemCount.size() != c2.size()) return false;

            for (Map.Entry<String, Integer> counts : this.itemCount.entrySet()) {
                String itemName = counts.getKey();
                Integer count = counts.getValue();

                Integer comparison = c2.get(itemName);
                if (comparison == null || !comparison.equals(count)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static final int MAX_BANKED_CHESTS = 4;
    private static final int RING_MOB_COST = 16;
    private static final int RING_SPAWNER_COST = 8;
    private static final int ISLES_MOB_COST = 5;
    private static final int ISLES_SPAWNER_COST = 2;
    private static final int VALLEY_MOB_COST = 4;
    private static final int VALLEY_SPAWNER_COST = 2;
    private static final Set<String> CURRENCY = new HashSet<>(List.of(
            "Archos Ring",
            "Compressed Crystalline Shard",
            "Crystalline Shard",
            "Concentrated Experience",
            "Experience Bottle"
    ));

    //
    private static final Set<ChestLoc> otherHalfLooted = new HashSet<>();
    private static final Map<ChestLoc, SimpleInventory> openedSingleChests = new HashMap<>();
    private static LootedChest openedChest;
    private static ChestType openedChestType;

    private static void onChestLooted(LootedChest chest, LootMethod method) {
//        if (!ShardInfo.inLootrunProtectedShard()) return;
        if (alreadyLooted(chest, method)) return;
        if (!resemblesGeneratedChest(chest, method)) return;
        // adjust points
    }

    private static boolean alreadyLooted(LootedChest chest, LootMethod method) {
        SimpleInventory previous = openedChests.get(chest.location);
        return switch (method) {
            case OPENED, MINED -> chest.contentsEqual(previous);
            case EXPLODED ->
                // todo deal with carrier of explosions
                    true;
        };
    }

    static void onChestBroken(ChestBreakListener.BrokenChest chest) {
<<<<<<< HEAD
        BlockPos otherHalf = getOtherHalf(chest.pos, chest.state);
        if (otherHalf != null) {
            // is double chest
            otherHalfLooted.add(new ChestLoc(chest.shard, otherHalf));
        }
        onChestLooted(new LootedChest(chest.shard, chest.pos, new ArrayList<>(chest.actualContents)), LootMethod.MINED);
=======
        DoubleChest doubleChest = isDoubleChest(chest.pos, )
>>>>>>> 2ec2d8b (wip scoretracking)
    }

    static void onChestOpened(ChestOpenListener.Match match, String shard) {
        LootedChest chestOpened = new LootedChest(shard, match.blockEvent.getPos(), match.container);
        openedChest = chestOpened;
        onChestLooted(chestOpened, LootMethod.OPENED);
    }

    public static void onContainerScreenClosed(Inventory contents) {
        if (openedChest == null || !(contents instanceof SimpleInventory)) {
            return;
        }

        openedChests.put(openedChest.location, (SimpleInventory) contents);
        openedChest = null;
    }

    static ChestType getChestType(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.get(ChestBlock.CHEST_TYPE);
    }

    @Nullable
    static BlockPos getOtherHalf(BlockPos pos, BlockState state) {
        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        Direction facing = state.get(ChestBlock.FACING);

        if (chestType == ChestType.LEFT) {
            return pos.offset(facing.rotateYClockwise());
        } else if (chestType == ChestType.RIGHT) {
            return pos.offset(facing.rotateYCounterclockwise());
        } else {
            return null;
        }
    }

    @Nullable
    static DoubleChest isDoubleChest(BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);
        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        Direction facing = state.get(ChestBlock.FACING);

        if (chestType == ChestType.LEFT) {
            return new DoubleChest(pos, pos.offset(facing.rotateYClockwise()));
        } else if (chestType == ChestType.RIGHT) {
            return new DoubleChest(pos.offset(facing.rotateYCounterclockwise()), pos);
        } else {
            return null;
        }
    }

    private static boolean chestWasModified(String shard) {
        if (openedChest == null) return true;

        BlockPos pos = openedChest.blockEvent.getPos();
        List<ItemStack> currentContents = openedChest.container;

        Map<BlockPos, SimpleInventory> chestHistoryOfShard = chestHistory.get(shard);
        if (chestHistoryOfShard == null) return true;

        SimpleInventory prevContents = chestHistoryOfShard.get(pos);
        if (prevContents == null) return true;

        if (currentContents.size() != prevContents.size()) return true;

        for (int i = 0; i < currentContents.size(); i++) {
            if (!ItemStack.areEqual(currentContents.get(i), prevContents.getStack(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean resemblesGeneratedChest(List<ItemStack> container) {
        if (container.size() > 54) return false;
        return container.stream().anyMatch(stack -> {
            if (stack.getName() == null) return false;
            if (CURRENCY.contains(stack.getName().getString())) {
                return true;
            }
            return false;
        });
    }
}
