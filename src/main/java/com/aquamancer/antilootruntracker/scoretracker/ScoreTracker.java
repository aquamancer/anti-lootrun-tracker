package com.aquamancer.antilootruntracker.scoretracker;

import com.aquamancer.antilootruntracker.ShardInfo;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreTracker {
    private static final int MAX_BANKED_CHESTS = 4;
    private static final int RING_MOB_COST = 16;
    private static final int RING_SPAWNER_COST = 8;
    private static final int ISLES_MOB_COST = 5;
    private static final int ISLES_SPAWNER_COST = 2;
    private static final int VALLEY_MOB_COST = 4;
    private static final int VALLEY_SPAWNER_COST = 2;

    private static int mobScore;
    private static int spawnerScore;

    private static ChestOpenListener.Match openedChest;
    private static final Map<String, Map<BlockPos, SimpleInventory>> chestHistory = new HashMap<>();

    public static void onTick(MinecraftClient client) {
    }

    public static void onEntityDeath(LivingEntity entity, DamageSource lastDamageSource) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (entity == null || lastDamageSource == null || client.player == null) {
            return;
        }
        if (lastDamageSource.getAttacker() != client.player || !ShardInfo.inLootrunProtectedShard()) {
            return;
        }

        mobScore++;
    }

    static void onChestOpened(ChestOpenListener.Match match) {
        openedChest = match;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
//        if (!ShardInfo.inLootrunProtectedShard()) return;

        if (!chestWasModified(ShardInfo.getCurrentShard())) {
            client.player.sendMessage(Text.literal("Chest is the same!"));
            return;
        }
        client.player.sendMessage(Text.literal("New chest opened: " + match.blockEvent.getPos()));
    }

    public static void onContainerScreenClosed(Inventory contents) {
        if (openedChest == null || !(contents instanceof SimpleInventory)) {
            return;
        }
        chestHistory.computeIfAbsent(ShardInfo.getCurrentShard(), s -> new HashMap<>())
                    .put(openedChest.blockEvent.getPos(), (SimpleInventory) contents);
        openedChest = null;
    }

    private static boolean chestWasModified(String shard) {
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
}
