package com.aquamancer.antilootruntracker.scoretracker;

import com.aquamancer.antilootruntracker.ShardTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LootingTracker {
    record DoubleChest(BlockPos left, BlockPos right) {}

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

    private static int mobScore;
    private static int spawnerScore;

    private static ChestOpenListener.Match openedChest;
    private static final Map<String, Map<BlockPos, SimpleInventory>> chestHistory = new HashMap<>();
    private static final Map<String, Set<DoubleChest>> doubleChests = new HashMap<>();

    public static void onEntityDeath(LivingEntity entity, DamageSource lastDamageSource) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (entity == null || lastDamageSource == null || client.player == null) {
            return;
        }
        if (lastDamageSource.getAttacker() != client.player || !ShardTracker.inLootrunProtectedShard()) {
            return;
        }

        mobScore++;
    }

    static void onChestBroken(ChestBreakListener.BrokenChest chest) {
        String shard = chest.shard;
        if (chest.isSingleChest && doubleChests.containsKey(shard) && doubleChests.get(shard).contains(chest.pos)) {
            // the other half was already mined
            return;
        }

    }

    static void onChestOpened(ChestOpenListener.Match match, String shard) {
        openedChest = match;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
//        if (!ShardInfo.inLootrunProtectedShard()) return;

        if (doubleChests.containsKey(shard) && doubleChests.get(shard).contains(match.blockEvent.getPos())) {
            client.player.sendMessage(Text.literal("Double chest opened!"));
        }
        if (!chestWasModified(ShardTracker.getCurrentShard())) {
            client.player.sendMessage(Text.literal("Chest is the same!"));
//            return;
        }
        if (!resemblesGeneratedChest(match.container)) {
            client.player.sendMessage(Text.literal("!resemblesGeneratedChest"));
        }
        client.player.sendMessage(Text.literal("New chest opened: " + match.blockEvent.getPos()));
    }

    public static void onContainerScreenClosed(Inventory contents) {
        if (openedChest == null || !(contents instanceof SimpleInventory)) {
            return;
        }
        chestHistory.computeIfAbsent(ShardTracker.getCurrentShard(), s -> new HashMap<>())
                    .put(openedChest.blockEvent.getPos(), (SimpleInventory) contents);
        openedChest = null;
    }

    static void registerDoubleChest(BlockPos left, BlockPos right, String shard) {
        doubleChests.computeIfAbsent(shard, s -> new HashSet<>())
                .add(new DoubleChest(left, right));
    }

    static void registerDoubleChest(DoubleChest doubleChest, String shard) {
        doubleChests.computeIfAbsent(shard, s -> new HashSet<>())
                .add(doubleChest);

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
